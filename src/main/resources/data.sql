-- Стартовые типовые проекты для каталога
INSERT INTO project_templates (id, name, total_area, floors, base_price, main_materials, description, created_at, updated_at)
VALUES
    (1, 'Шале 120', 120.0, 2, 12500000.00, 'Клеёный брус, металлочерепица', 'Компактный дом с вторым светом и террасой.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Сканди 180', 180.0, 2, 16500000.00, 'Газобетон, фальцевая кровля', 'Светлый дом в сканди-стиле с большим остеклением.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Минимод 90', 90.0, 1, 8900000.00, 'Каркас, мягкая кровля', 'Одноэтажный лаконичный дом для небольшого участка.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Дефолтные этапы для проектов
INSERT INTO project_template_default_stages (project_template_id, stage)
VALUES
    (1, 'PREPARATION'), (1, 'FOUNDATION'), (1, 'WALLS'), (1, 'ROOFING'), (1, 'ENGINEERING_SYSTEMS'), (1, 'FACADE'), (1, 'INTERIOR_FINISHING'), (1, 'HANDOVER'),
    (2, 'PREPARATION'), (2, 'FOUNDATION'), (2, 'WALLS'), (2, 'ROOFING'), (2, 'ENGINEERING_SYSTEMS'), (2, 'FACADE'), (2, 'INTERIOR_FINISHING'), (2, 'HANDOVER'),
    (3, 'PREPARATION'), (3, 'FOUNDATION'), (3, 'WALLS'), (3, 'ROOFING'), (3, 'ENGINEERING_SYSTEMS'), (3, 'FACADE'), (3, 'INTERIOR_FINISHING'), (3, 'HANDOVER')
ON CONFLICT DO NOTHING;

-- Медиа для проектов (рендеры/фото/планы)
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

-- Требования по документам для укрупнённых этапов
INSERT INTO stage_document_requirements (high_level_stage, document_type, required, order_index)
VALUES
    -- Этап согласования документов
    ('DOCS_APPROVAL', 'CONTRACT', TRUE, 1),
    ('DOCS_APPROVAL', 'ESTIMATE', TRUE, 2),
    ('DOCS_APPROVAL', 'PROJECT_PLAN', TRUE, 3),
    ('DOCS_APPROVAL', 'BUILDING_PERMIT', FALSE, 4),

    -- Этап стройки
    ('BUILDING', 'STAGE_REPORT', FALSE, 1),
    ('BUILDING', 'ADDITIONAL_AGREEMENT', FALSE, 2),

    -- Завершение и подписание финальных документов
    ('COMPLETION', 'FINAL_ACCEPTANCE_ACT', TRUE, 1),
    ('COMPLETION', 'FINAL_REPORT', FALSE, 2),
    ('COMPLETION', 'WARRANTY', TRUE, 3);
