package com.example.constructioncontrol.service.impl;

import com.example.constructioncontrol.dto.CreateOrderRequest;
import com.example.constructioncontrol.dto.ProjectOrderResponse;
import com.example.constructioncontrol.model.ConstructionObject;
import com.example.constructioncontrol.model.ConstructionStatus;
import com.example.constructioncontrol.model.ProjectOrder;
import com.example.constructioncontrol.model.ProjectTemplate;
import com.example.constructioncontrol.model.OrderStatus;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.repository.ConstructionObjectRepository;
import com.example.constructioncontrol.repository.ProjectOrderRepository;
import com.example.constructioncontrol.repository.ProjectTemplateRepository;
import com.example.constructioncontrol.repository.UserAccountRepository;
import com.example.constructioncontrol.service.ProjectOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class ProjectOrderServiceImpl implements ProjectOrderService {

    private final ProjectOrderRepository projectOrderRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProjectTemplateRepository projectTemplateRepository;
    private final ConstructionObjectRepository constructionObjectRepository;

    public ProjectOrderServiceImpl(ProjectOrderRepository projectOrderRepository,
                                   UserAccountRepository userAccountRepository,
                                   ProjectTemplateRepository projectTemplateRepository,
                                   ConstructionObjectRepository constructionObjectRepository) {
        this.projectOrderRepository = projectOrderRepository;
        this.userAccountRepository = userAccountRepository;
        this.projectTemplateRepository = projectTemplateRepository;
        this.constructionObjectRepository = constructionObjectRepository;
    }

    @Override
    public ProjectOrderResponse createOrder(CreateOrderRequest request) {
        // 1. Найти связанные сущности (если переданы)
        UserAccount customer = null;
        if (request.getCustomerId() != null) {
            customer = userAccountRepository.findById(request.getCustomerId())
                    .orElseThrow();
        }

        ProjectTemplate template = null;
        if (request.getProjectTemplateId() != null) {
            template = projectTemplateRepository.findById(request.getProjectTemplateId())
                    .orElseThrow();
        }

        // 2. Создать ConstructionObject
        ConstructionObject constructionObject = new ConstructionObject();
        constructionObject.setAddress(request.getAddress());
        constructionObject.setStatus(ConstructionStatus.DOCUMENT_PREPARATION);
        constructionObject.setContactPhone(request.getPhone());
        constructionObject.setContactEmail(request.getEmail());
        constructionObject.setSelectedProject(template);
        constructionObject.setCustomer(customer);

        constructionObject = constructionObjectRepository.save(constructionObject);

        // 3. Создать ProjectOrder
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

        // 4. Сформировать ответ
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectOrderResponse> getAllOrders() {
        return projectOrderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectOrderResponse getOrder(Long id) {
        ProjectOrder order = projectOrderRepository.findById(id)
                .orElseThrow();
        return toResponse(order);
    }

    private ProjectOrderResponse toResponse(ProjectOrder order) {
        ProjectOrderResponse response = new ProjectOrderResponse();
        response.setId(order.getId());
        response.setAddress(order.getAddress());
        response.setStatus(order.getStatus().name());
        if (order.getConstructionObject() != null) {
            response.setConstructionObjectId(order.getConstructionObject().getId());
        }
        if (order.getProjectTemplate() != null) {
            response.setProjectTemplateId(order.getProjectTemplate().getId());
        }
        return response;
    }
}