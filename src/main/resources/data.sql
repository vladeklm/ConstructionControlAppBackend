-- 1. Стартовые типовые проекты
INSERT INTO project_templates (id, name, total_area, floors, base_price, main_materials, description, created_at, updated_at)
VALUES
    (1, 'Шале 120', 120.0, 2, 12500000.00, 'Клеёный брус, металлочерепица', 'Компактный дом с вторым светом и террасой.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Сканди 180', 180.0, 2, 16500000.00, 'Газобетон, фальцевая кровля', 'Светлый дом в сканди-стиле с большим остеклением.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Минимод 90', 90.0, 1, 8900000.00, 'Каркас, мягкая кровля', 'Одноэтажный лаконичный дом для небольшого участка.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. Дефолтные этапы (один раз, без дублей)
INSERT INTO project_template_default_stages (project_template_id, stage)
VALUES
    (1, 'PREPARATION'),
    (1, 'FOUNDATION'),
    (1, 'WALLS'),
    (1, 'ROOFING'),
    (1, 'WINDOWS_AND_DOORS'),
    (1, 'ENGINEERING_SYSTEMS'),
    (1, 'FACADE'),
    (1, 'INTERIOR_FINISHING'),
    (1, 'LANDSCAPING'),
    (1, 'HANDOVER'),

    (2, 'PREPARATION'),
    (2, 'FOUNDATION'),
    (2, 'WALLS'),
    (2, 'ROOFING'),
    (2, 'WINDOWS_AND_DOORS'),
    (2, 'ENGINEERING_SYSTEMS'),
    (2, 'FACADE'),
    (2, 'INTERIOR_FINISHING'),
    (2, 'LANDSCAPING'),
    (2, 'HANDOVER'),

    (3, 'PREPARATION'),
    (3, 'FOUNDATION'),
    (3, 'WALLS'),
    (3, 'ROOFING'),
    (3, 'WINDOWS_AND_DOORS'),
    (3, 'ENGINEERING_SYSTEMS'),
    (3, 'FACADE'),
    (3, 'INTERIOR_FINISHING'),
    (3, 'LANDSCAPING'),
    (3, 'HANDOVER');

-- 3. Медиа для проектов
INSERT INTO project_media (id, project_template_id, type, url, sort_order, created_at, updated_at)
VALUES
    (101, 1, 'RENDER', 'https://cdn.example.com/projects/chalet120/render-1.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (102, 1, 'RENDER', 'https://cdn.example.com/projects/chalet120/render-2.jpg', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (103, 1, 'PHOTO',  'https://cdn.example.com/projects/chalet120/built-1.jpg', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (104, 1, 'PLAN',   'https://cdn.example.com/projects/chalet120/floor1.png', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (105, 1, 'PLAN',   'https://cdn.example.com/projects/chalet120/floor2.png', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (201, 2, 'RENDER', 'https://cdn.example.com/projects/scandi180/render-1.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (202, 2, 'PHOTO',  'https://cdn.example.com/projects/scandi180/built-1.jpg', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (203, 2, 'PLAN',   'https://cdn.example.com/projects/scandi180/floor1.png', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (204, 2, 'PLAN',   'https://cdn.example.com/projects/scandi180/floor2.png', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (301, 3, 'RENDER', 'https://cdn.example.com/projects/minimod90/render-1.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (302, 3, 'PLAN',   'https://cdn.example.com/projects/minimod90/floor1.png', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 4. Требования по документам
INSERT INTO stage_document_requirements (high_level_stage, document_type, required, order_index)
VALUES
    ('DOCS_APPROVAL', 'CONTRACT', TRUE, 1),
    ('DOCS_APPROVAL', 'ESTIMATE', TRUE, 2),
    ('DOCS_APPROVAL', 'PROJECT_PLAN', TRUE, 3),
    ('DOCS_APPROVAL', 'BUILDING_PERMIT', FALSE, 4),

    ('BUILDING', 'STAGE_REPORT', FALSE, 1),
    ('BUILDING', 'ADDITIONAL_AGREEMENT', FALSE, 2),

    ('COMPLETION', 'FINAL_ACCEPTANCE_ACT', TRUE, 1),
    ('COMPLETION', 'FINAL_REPORT', FALSE, 2),
    ('COMPLETION', 'WARRANTY', TRUE, 3);

-- 5. Пользователи (пароли уже BCrypt для "test-password" и "engineer-password")
-- ВАЖНО: сюда нужно подставить реальные сгенерированные хеши
INSERT INTO users (id, login, full_name, email, phone, role, password, created_at, updated_at)
VALUES
    (1, 'testuser',   'Тестовый клиент',  'test@example.com',     '+7-900-000-00-00', 'CUSTOMER', '$2a$10$7SUtLR/Cvad.G4zD5/1qO.bul/2e3xHJ7WAjzL2BYR5l2fYTNGfVa',     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'engineer1',  'Тестовый инженер', 'engineer@example.com', '+7-900-000-00-02', 'ENGINEER', '$2a$10$wDkZl8Xi52vxUDHt2pC5kecPGOoAobQFaMhoq2gH3xecyqwnQtTq.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 6. Объект / заказ / стадии для первого сценария (как сейчас)
INSERT INTO construction_objects (address, status, customer_id, contact_phone, contact_email, created_at, updated_at)
VALUES ('Москва, ул. Ленина 1', 'DOCUMENT_PREPARATION', 1, '+79000000001', 'client@test.ru', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

----INSERT INTO construction_stages (id, construction_object_id, order_index, type, status, planned_start_date, planned_end_date, progress_percentage, created_at, updated_at)
----VALUES
----    (1, 1, 1, 'PREPARATION', 'NOT_STARTED', '2025-12-15', '2025-12-20', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
----    (2, 1, 2, 'FOUNDATION',  'NOT_STARTED', '2025-12-21', '2026-01-15', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
----ON CONFLICT (id) DO NOTHING;
--
--INSERT INTO project_orders (id, construction_object_id, customer_id, project_template_id, status, address, phone, email, requested_timeline, submitted_at, created_at, updated_at)
--VALUES (1, 1, 1, 1, 'SUBMITTED', 'Москва, ул. Ленина 1', '+79000000001', 'client@test.ru', '6 месяцев', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
--ON CONFLICT (id) DO NOTHING;
--
---- 7. Документы по первой стадии объекта 1
--INSERT INTO documents (id, stage_id, title, type, status, file_url, preview_url, created_at, updated_at)
--VALUES
--    (1, 1, 'Договор подряда №001',            'CONTRACT', 'AWAITING_SIGNATURE', '/docs/contract.pdf', '/docs/contract-preview.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--    (2, 1, 'Смета на подготовительный этап',  'ESTIMATE', 'AWAITING_SIGNATURE', '/docs/estimate.pdf', '/docs/estimate-preview.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
--ON CONFLICT (id) DO NOTHING;
