-- ===================================================
-- 0. Создаём уникальные ограничения
-- ===================================================
ALTER TABLE project_templates ADD CONSTRAINT uq_project_templates_name UNIQUE(name);
ALTER TABLE users ADD CONSTRAINT uq_users_login UNIQUE(login);

-- ===================================================
-- 1. Стартовые типовые проекты
-- ===================================================
INSERT INTO project_templates (name, total_area, floors, base_price, main_materials, description, created_at, updated_at)
VALUES
    ('Шале 120',   120.0, 2, 12500000.00, 'WOOD', 'Компактный дом с вторым светом и террасой.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Сканди 180', 180.0, 2, 16500000.00, 'GAS_CONCRETE', 'Светлый дом в сканди-стиле с большим остеклением.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Минимод 90',  90.0, 1,  8900000.00, 'FRAME', 'Одноэтажный лаконичный дом для небольшого участка.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===================================================
-- 2. Дефолтные этапы проектов
-- ===================================================
INSERT INTO project_template_default_stages (project_template_id, stage)
SELECT pt.id, s.stage
FROM project_templates pt
JOIN (VALUES
    ('Шале 120', 'PREPARATION'),
    ('Шале 120', 'FOUNDATION'),
    ('Шале 120', 'WALLS'),
    ('Шале 120', 'ROOFING'),
    ('Шале 120', 'WINDOWS_AND_DOORS'),
    ('Шале 120', 'ENGINEERING_SYSTEMS'),
    ('Шале 120', 'FACADE'),
    ('Шале 120', 'INTERIOR_FINISHING'),
    ('Шале 120', 'LANDSCAPING'),
    ('Шале 120', 'HANDOVER'),

    ('Сканди 180', 'PREPARATION'),
    ('Сканди 180', 'FOUNDATION'),
    ('Сканди 180', 'WALLS'),
    ('Сканди 180', 'ROOFING'),
    ('Сканди 180', 'WINDOWS_AND_DOORS'),
    ('Сканди 180', 'ENGINEERING_SYSTEMS'),
    ('Сканди 180', 'FACADE'),
    ('Сканди 180', 'INTERIOR_FINISHING'),
    ('Сканди 180', 'LANDSCAPING'),
    ('Сканди 180', 'HANDOVER'),

    ('Минимод 90', 'PREPARATION'),
    ('Минимод 90', 'FOUNDATION'),
    ('Минимод 90', 'WALLS'),
    ('Минимод 90', 'ROOFING'),
    ('Минимод 90', 'WINDOWS_AND_DOORS'),
    ('Минимод 90', 'ENGINEERING_SYSTEMS'),
    ('Минимод 90', 'FACADE'),
    ('Минимод 90', 'INTERIOR_FINISHING'),
    ('Минимод 90', 'LANDSCAPING'),
    ('Минимод 90', 'HANDOVER')
) AS s(template_name, stage)
ON pt.name = s.template_name;

-- ===================================================
-- 3. Медиа для проектов
-- ===================================================
INSERT INTO project_media (project_template_id, type, url, sort_order, created_at, updated_at)
SELECT pt.id, m.type, m.url, m.sort_order, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM project_templates pt
JOIN (VALUES
    ('Шале 120', 'RENDER', '/uploads/1_1.jpg', 1),
    ('Шале 120', 'PHOTO',  '/uploads/1_2.jpg', 2),
    ('Шале 120', 'PLAN',   '/uploads/1_3.jpg', 3),

    ('Сканди 180', 'RENDER', '/uploads/2_1.jpg', 1),
    ('Сканди 180', 'PHOTO',  '/uploads/2_2.jpg', 2),
    ('Сканди 180', 'PLAN',   '/uploads/2_3.jpg', 3),

    ('Минимод 90', 'RENDER', '/uploads/3_1.jpg', 1),
    ('Минимод 90', 'PHOTO',  '/uploads/3_2.jpg', 2),
    ('Минимод 90', 'PLAN',   '/uploads/3_3.jpg', 3)
) AS m(template_name, type, url, sort_order)
ON pt.name = m.template_name;

-- ===================================================
-- 4. Требования по документам
-- ===================================================
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

-- ===================================================
-- 5. Пользователи (пароль: 1234)
-- ===================================================
INSERT INTO users (login, full_name, email, phone, role, password, created_at, updated_at)
VALUES
    ('testuser',  'Тестовый клиент',  'test@example.com',     '+7-900-000-00-00', 'CUSTOMER', '$2a$12$l2pZ0ScAN98ll22mn6qa0uJ9VTHOV0bKp786otw4J6wLyWfq/RmkK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('engineer1', 'Тестовый инженер', 'engineer@example.com', '+7-900-000-00-02', 'ENGINEER', '$2a$12$l2pZ0ScAN98ll22mn6qa0uJ9VTHOV0bKp786otw4J6wLyWfq/RmkK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===================================================
-- 6. Объект строительства
-- ===================================================
INSERT INTO construction_objects (address, status, customer_id, contact_phone, contact_email, created_at, updated_at)
SELECT
    'Москва, ул. Ленина 1',
    'DOCUMENT_PREPARATION',
    u.id,
    '+79000000001',
    'client@test.ru',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.login = 'testuser';


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
