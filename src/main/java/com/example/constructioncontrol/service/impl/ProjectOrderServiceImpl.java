package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.OrderListItemResponse;
import com.example.constructioncontrol.dto.OrderPageResponse;
import com.example.constructioncontrol.dto.ProjectOrderResponse;
import com.example.constructioncontrol.model.ConstructionObject;
import com.example.constructioncontrol.model.ConstructionStage;
import com.example.constructioncontrol.model.ConstructionStageStatus;
import com.example.constructioncontrol.model.ConstructionStatus;
import com.example.constructioncontrol.model.OrderStatus;
import com.example.constructioncontrol.model.ProjectOrder;
import com.example.constructioncontrol.model.ProjectTemplate;
import com.example.constructioncontrol.model.StageType;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.repository.ConstructionObjectRepository;
import com.example.constructioncontrol.repository.ConstructionStageRepository;
import com.example.constructioncontrol.repository.ProjectOrderRepository;
import com.example.constructioncontrol.repository.ProjectTemplateRepository;
import com.example.constructioncontrol.repository.UserAccountRepository;
import com.example.constructioncontrol.service.ProjectOrderService;
import com.example.constructioncontrol.service.DocumentService;
import com.example.constructioncontrol.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProjectOrderServiceImpl implements ProjectOrderService {

    private final ProjectOrderRepository projectOrderRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProjectTemplateRepository projectTemplateRepository;
    private final ConstructionObjectRepository constructionObjectRepository;
    private final ConstructionStageRepository constructionStageRepository;
    private final UserService userService;
    private final DocumentService documentService;

    public ProjectOrderServiceImpl(ProjectOrderRepository projectOrderRepository,
                                   UserAccountRepository userAccountRepository,
                                   ProjectTemplateRepository projectTemplateRepository,
                                   ConstructionObjectRepository constructionObjectRepository,
                                   ConstructionStageRepository constructionStageRepository,
                                   UserService userService,
                                   DocumentService documentService) {
        this.projectOrderRepository = projectOrderRepository;
        this.userAccountRepository = userAccountRepository;
        this.projectTemplateRepository = projectTemplateRepository;
        this.constructionObjectRepository = constructionObjectRepository;
        this.constructionStageRepository = constructionStageRepository;
        this.userService = userService;
        this.documentService = documentService;
    }

    @Override
    public ProjectOrderResponse createOrder(CreateOrderRequest request) {
        UserAccount customer = userService.getCurrentUserAccount();

        if (request.getProjectTemplateId() == null) {
            throw new IllegalArgumentException("projectTemplateId is required");
        }

        ProjectTemplate template = projectTemplateRepository.findById(request.getProjectTemplateId())
                .orElseThrow(() -> new EntityNotFoundException("Project template not found"));

        // 1. Создаём объект
        ConstructionObject constructionObject = new ConstructionObject();
        constructionObject.setAddress(request.getAddress());
        constructionObject.setStatus(ConstructionStatus.DOCUMENT_PREPARATION);
        constructionObject.setContactPhone(request.getPhone());
        constructionObject.setContactEmail(request.getEmail());
        constructionObject.setSelectedProject(template);
        constructionObject.setCustomer(customer);
        constructionObject = constructionObjectRepository.save(constructionObject);

        // 2. Создаём все стандартные стадии для объекта
        List<ConstructionStage> stages = createAllStandardStages(constructionObject);
        constructionStageRepository.saveAll(stages);

        documentService.createRequiredDocumentsForObject(constructionObject);

        // 3. Создаём заказ
        ProjectOrder order = new ProjectOrder();
        order.setAddress(request.getAddress());
        order.setStatus(OrderStatus.SUBMITTED);
        order.setSubmittedAt(OffsetDateTime.now());
        order.setRequestedTimeline(request.getRequestedTimeline());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setCustomer(customer);
        order.setProjectTemplate(template);
        order.setConstructionObject(constructionObject);

        order = projectOrderRepository.save(order);

        return toResponse(order);
    }

    // --- метод создания всех стандартных стадий ---
    private List<ConstructionStage> createAllStandardStages(ConstructionObject constructionObject) {
        List<ConstructionStage> stages = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        stages.add(createStage(constructionObject, 1, StageType.PREPARATION,
                currentDate.plusDays(0), currentDate.plusDays(5)));
        stages.add(createStage(constructionObject, 2, StageType.FOUNDATION,
                currentDate.plusDays(6), currentDate.plusDays(30)));
        stages.add(createStage(constructionObject, 3, StageType.WALLS,
                currentDate.plusDays(31), currentDate.plusDays(60)));
        stages.add(createStage(constructionObject, 4, StageType.ROOFING,
                currentDate.plusDays(61), currentDate.plusDays(80)));
        stages.add(createStage(constructionObject, 5, StageType.WINDOWS_AND_DOORS,
                currentDate.plusDays(81), currentDate.plusDays(95)));
        stages.add(createStage(constructionObject, 6, StageType.FACADE,
                currentDate.plusDays(96), currentDate.plusDays(115)));
        stages.add(createStage(constructionObject, 7, StageType.ENGINEERING_SYSTEMS,
                currentDate.plusDays(116), currentDate.plusDays(150)));
        stages.add(createStage(constructionObject, 8, StageType.INTERIOR_FINISHING,
                currentDate.plusDays(151), currentDate.plusDays(210)));
        stages.add(createStage(constructionObject, 9, StageType.LANDSCAPING,
                currentDate.plusDays(211), currentDate.plusDays(230)));
        stages.add(createStage(constructionObject, 10, StageType.HANDOVER,
                currentDate.plusDays(231), currentDate.plusDays(240)));

        return stages;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getOrdersForCurrentUser(OrderStatus status, int page, int size) {
        UserAccount currentUser = userService.getCurrentUserAccount();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ProjectOrder> ordersPage;
        if (status != null) {
            ordersPage = projectOrderRepository
                    .findByCustomerIdAndStatus(currentUser.getId(), status, pageable);
        } else {
            ordersPage = projectOrderRepository
                    .findByCustomerId(currentUser.getId(), pageable);
        }

        List<OrderListItemResponse> content = ordersPage.getContent().stream()
                .map(this::toOrderListItemResponse)
                .toList();

        OrderPageResponse response = new OrderPageResponse();
        response.setContent(content);
        response.setTotalElements(ordersPage.getTotalElements());
        response.setTotalPages(ordersPage.getTotalPages());
        response.setCurrentPage(ordersPage.getNumber());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectOrderResponse getOrderForCurrentUser(Long orderId) {
        UserAccount customer = userService.getCurrentUserAccount();

        ProjectOrder order = projectOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Access denied to this order");
        }

        ProjectOrderResponse response = toResponse(order);

        ConstructionObject constructionObject = order.getConstructionObject();
        if (constructionObject != null) {
            ConstructionStage firstStage = constructionStageRepository
                    .findFirstByConstructionObjectIdOrderByOrderIndexAsc(constructionObject.getId())
                    .orElse(null);

            if (firstStage != null) {
                response.setFirstStageId(firstStage.getId());
                response.setFirstStageName(firstStage.getType().name());
                response.setFirstStageStatus(firstStage.getStatus().name());
            }
        }

        return response;
    }

    // --- вспомогательный метод создания стадии ---

    private ConstructionStage createStage(
            ConstructionObject object,
            int orderIndex,
            StageType type,
            LocalDate plannedStart,
            LocalDate plannedEnd
    ) {
        ConstructionStage stage = new ConstructionStage();
        stage.setConstructionObject(object);
        stage.setOrderIndex(orderIndex);
        stage.setType(type);
        stage.setStatus(type == StageType.PREPARATION ? ConstructionStageStatus.IN_PROGRESS: ConstructionStageStatus.NOT_STARTED);
        stage.setProgressPercentage(0);
        stage.setPlannedStartDate(plannedStart);
        stage.setPlannedEndDate(plannedEnd);
        return stage;
    }

    // --- маппинг в DTO ---

    private ProjectOrderResponse toResponse(ProjectOrder order) {
        ProjectOrderResponse response = new ProjectOrderResponse();
        response.setId(order.getId());
        response.setAddress(order.getAddress());
        response.setStatus(order.getStatus().name());
        response.setRequestedTimeline(order.getRequestedTimeline());
        response.setPhone(order.getPhone());
        response.setEmail(order.getEmail());
        response.setSubmittedAt(order.getSubmittedAt());

        if (order.getConstructionObject() != null) {
            response.setConstructionObjectId(order.getConstructionObject().getId());
        }
        if (order.getProjectTemplate() != null) {
            response.setProjectTemplateId(order.getProjectTemplate().getId());
            response.setProjectTemplateName(order.getProjectTemplate().getName());
        }
        return response;
    }

    private OrderListItemResponse toOrderListItemResponse(ProjectOrder order) {
        OrderListItemResponse dto = new OrderListItemResponse();
        dto.setId(order.getId());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus().name());
        dto.setRequestedTimeline(order.getRequestedTimeline());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setSubmittedAt(order.getSubmittedAt());

        if (order.getConstructionObject() != null) {
            dto.setConstructionObjectId(order.getConstructionObject().getId());
        }
        if (order.getProjectTemplate() != null) {
            dto.setProjectTemplateId(order.getProjectTemplate().getId());
            dto.setProjectTemplateName(order.getProjectTemplate().getName());
        }
        return dto;
    }
}
