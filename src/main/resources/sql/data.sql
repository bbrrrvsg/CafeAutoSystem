-- =====================================================================================
-- 0. 기존 데이터 초기화 (외래키 제약 조건을 고려한 순차 삭제)
-- =====================================================================================
-- 프로젝트에 존재하는 현재고 테이블도 함께 비워줍니다.
DELETE FROM current_stock_log;
DELETE FROM historical_stock_log;
DELETE FROM purchase_order;
DELETE FROM vendor_ingredient;
DELETE FROM ingredient;
DELETE FROM vendor;
DELETE FROM menu;

-- =====================================================================================
-- 1. MENU (메뉴 마스터)
-- =====================================================================================
INSERT INTO menu (menu_name, menu_price, created_at, updated_at) VALUES
    ('아메리카노',      3000, NOW(), NOW()),
    ('카페라떼',        3800, NOW(), NOW()),
    ('바닐라라떼',      4300, NOW(), NOW()),
    ('카페모카',        4300, NOW(), NOW()),
    ('아메모카',        4000, NOW(), NOW()),
    ('카라멜 마키아또', 4500, NOW(), NOW()),
    ('초코라떼',        4000, NOW(), NOW()),
    ('딸기 에이드',     4500, NOW(), NOW()),
    ('콜드브루',        4500, NOW(), NOW()),
    ('그린티 라떼',     4500, NOW(), NOW()),
    ('핫초코',          3800, NOW(), NOW()),
    ('오렌지 에이드',   4000, NOW(), NOW()),
    ('딸기 라떼',       5000, NOW(), NOW()),
    ('아이스티',        3500, NOW(), NOW()),
    ('유자차',          4000, NOW(), NOW());

-- =====================================================================================
-- 2. VENDOR (거래처 마스터)
-- =====================================================================================
INSERT INTO vendor (vendor_id, vendor_name, manager_email, manager_phone, created_at, updated_at) VALUES
    (1,  '서울원두유통',       'seoul_bean@naver.com',      '010-1234-5678', NOW(), NOW()),
    (2,  '매일유통 대리점',    'maeil_milk@gmail.com',      '010-9876-5432', NOW(), NOW()),
    (3,  '대박부자재마트',     'daebak_pack@daum.net',      '02-111-2222',   NOW(), NOW()),
    (4,  '한국제과재료',       'kbakery@naver.com',         '031-222-3344',  NOW(), NOW()),
    (5,  '청정과일농장',       'fresh_fruit@gmail.com',     '010-2222-1111', NOW(), NOW()),
    (6,  '프리미엄시럽',       'premium_syrup@daum.net',    '02-555-7788',   NOW(), NOW()),
    (7,  '서울낙농협동조합',   'seoul_dairy@naver.com',     '02-777-8899',   NOW(), NOW()),
    (8,  '제주녹차원',         'jeju_greentea@gmail.com',   '064-111-2233',  NOW(), NOW()),
    (9,  '한국음료재료',       'kr_beverage@daum.net',      '031-333-4455',  NOW(), NOW());

-- =====================================================================================
-- 3. INGREDIENT (식자재 마스터)
-- =====================================================================================
INSERT INTO ingredient (ingredient_id, ingredient_name, unit, safety_stock, ingredient_image) VALUES
    (1,  '하우스 블렌드 원두', 'g',    5000, '/images/ingredients/house_blend.jpg'),
    (2,  '바닐라 시럽',        'ml',   800,  '/images/ingredients/vanilla_syrup.jpg'),
    (3,  '냉동 딸기 블렌드',   'pack', 5,    '/images/ingredients/frozen_strawberry.jpg'),
    (4,  '신선한 우유',        'ml',   5000, '/images/ingredients/fresh_milk_1l.jpg'),
    (5,  '초코 파우더',        'g',    300,  '/images/ingredients/choco_powder.jpg'),
    (6,  '카라멜 시럽',        'ml',   800,  '/images/ingredients/caramel_syrup.jpg'),
    (7,  '생크림',             'ml',   500,  '/images/ingredients/heavy_cream.jpg'),
    (8,  '설탕 시럽',          'ml',   1000, '/images/ingredients/sugar_syrup.jpg'),
    (9,  '녹차 파우더',        'g',    200,  '/images/ingredients/greentea_powder.jpg'),
    (10, '오렌지 시럽',        'ml',   500,  '/images/ingredients/orange_syrup.jpg'),
    (11, '딸기 시럽',          'ml',   500,  '/images/ingredients/strawberry_syrup.jpg'),
    (12, '유자청',             'g',    500,  '/images/ingredients/yuzu.jpg');

-- =====================================================================================
-- 4. VENDOR_INGREDIENT (거래처-식자재 매핑)
-- =====================================================================================
INSERT INTO vendor_ingredient (vendor_ingredient_id, vendor_id, ingredient_id, unit_price, priority_rank, created_at, updated_at) VALUES
    (1,  1, 1,  15,    1, NOW(), NOW()),  -- 서울원두유통 → 원두
    (2,  2, 4,  3,     1, NOW(), NOW()),  -- 매일유통 → 우유 (1순위)
    (3,  7, 4,  2,     2, NOW(), NOW()),  -- 서울낙농 → 우유 (2순위)
    (4,  6, 2,  8,     1, NOW(), NOW()),  -- 프리미엄시럽 → 바닐라시럽
    (5,  5, 3,  12000, 1, NOW(), NOW()),  -- 청정과일농장 → 냉동딸기
    (6,  4, 5,  18,    1, NOW(), NOW()),  -- 한국제과재료 → 초코파우더
    (7,  6, 6,  9,     1, NOW(), NOW()),  -- 프리미엄시럽 → 카라멜시럽
    (8,  7, 7,  5,     1, NOW(), NOW()),  -- 서울낙농 → 생크림
    (9,  9, 8,  4,     1, NOW(), NOW()),  -- 한국음료재료 → 설탕시럽
    (10, 8, 9,  25,    1, NOW(), NOW()),  -- 제주녹차원 → 녹차파우더
    (11, 9, 10, 7,     1, NOW(), NOW()),  -- 한국음료재료 → 오렌지시럽
    (12, 5, 11, 6,     1, NOW(), NOW()),  -- 청정과일농장 → 딸기시럽
    (13, 9, 12, 22,    1, NOW(), NOW());  -- 한국음료재료 → 유자청

-- =====================================================================================
-- 5. PURCHASE_ORDER (과거 완료 발주 샘플)
-- =====================================================================================
INSERT INTO purchase_order (vendor_ingredient_id, order_date_key, suggested_qty, final_qty, status, expiration_date, created_at, updated_at) VALUES
    (1,  'PO-20260601', 15,  15,  'COMPLETED', '2026-08-01', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (2,  'PO-20260601', 40,  40,  'COMPLETED', '2026-06-15', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (4,  'PO-20260603', 5,   5,   'COMPLETED', '2026-09-01', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (6,  'PO-20260603', 3,   3,   'COMPLETED', '2026-09-01', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (7,  'PO-20260603', 5,   5,   'COMPLETED', '2026-09-01', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (10, 'PO-20260605', 3,   3,   'COMPLETED', '2026-12-01', DATE_SUB(NOW(), INTERVAL 6 DAY),  DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (5,  'PO-20260607', 10,  8,   'COMPLETED', '2026-07-01', DATE_SUB(NOW(), INTERVAL 4 DAY),  DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (2,  'PO-20260610', 30,  30,  'PENDING',   '2026-06-25', DATE_SUB(NOW(), INTERVAL 1 DAY),  DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =====================================================================================
-- 6. CURRENT_STOCK_LOG (현재 재고 기초 등록)
-- =====================================================================================
INSERT INTO current_stock_log (ingredient_id, amount, log_type, message, reason, user_id, created_at, updated_at) VALUES
    (1,  6000, 'STOCK_IN', '[초기화] 하우스 블렌드 원두 기초재고',  '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (2,  1200, 'STOCK_IN', '[초기화] 바닐라 시럽 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (3,  3,    'STOCK_IN', '[초기화] 냉동 딸기 블렌드 기초재고',    '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (4,  4000, 'STOCK_IN', '[초기화] 신선한 우유 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (5,  250,  'STOCK_IN', '[초기화] 초코 파우더 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (6,  900,  'STOCK_IN', '[초기화] 카라멜 시럽 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (7,  300,  'STOCK_IN', '[초기화] 생크림 기초재고',              '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (8,  1500, 'STOCK_IN', '[초기화] 설탕 시럽 기초재고',           '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (9,  150,  'STOCK_IN', '[초기화] 녹차 파우더 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (10, 600,  'STOCK_IN', '[초기화] 오렌지 시럽 기초재고',         '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (11, 400,  'STOCK_IN', '[초기화] 딸기 시럽 기초재고',           '초기 재고 실사', 'SYSTEM', NOW(), NOW()),
    (12, 450,  'STOCK_IN', '[초기화] 유자청 기초재고',              '초기 재고 실사', 'SYSTEM', NOW(), NOW());


-- =====================================================================================
-- 7. HISTORICAL_STOCK_LOG (과거 4주간 요일별 시계열 및 패턴 로그)
-- =====================================================================================

INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3500, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4400, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -5200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1400, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4800, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1050, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3000, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1150, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1250, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY));

-- [3주 전 주간: Day 21 ~ Day 15]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3500, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -5000, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1400, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -5500, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4900, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1150, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -2800, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1250, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3000, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- [2주 전 주간: Day 14 ~ Day 8]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1250, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3400, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -5000, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1350, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -5100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4700, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1150, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1250, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY));

-- [1주 전 주간 ~ 직전 대비 기간: Day 7 ~ Day 1]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1300, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3600, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1350, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1400, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -4500, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3800, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1150, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -3, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3900, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1200, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -2, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3500, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                                                         (1, 'STOCK_OUT', '원두 소모', -1250, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '시럽 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '딸기 소모', -1, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '우유 소모', -3600, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));
-- =====================================================================================
-- 8. 파이썬 LSTM 모델 연동용 직전 4주 요일별 월요일 소모 패턴 동적 적재 (우유 4번)
-- =====================================================================================

-- [1주 전 월요일]
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', -1500, 'DAILY_REGULAR', '월요일 아침 출근길 아메리카노 원두 소모', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (2, 'STOCK_OUT', -4, 'DAILY_REGULAR', '바닐라 라떼 선호 증가로 인한 시럽 소모', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (3, 'STOCK_OUT', -2, 'DAILY_REGULAR', '딸기 주스 정기 소모', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (4, 'STOCK_OUT', -18000, 'DAILY_REGULAR', '정상 영업 라떼 우유 소모', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                         (4, 'STOCK_DISCARD', -2000, 'EXPIRED', '유통기한 임박 우유 2팩 폐기', 'admin', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY));

-- [2주 전 월요일]
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', -1600, 'DAILY_REGULAR', '월요일 단체 주문 원두 소모', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (2, 'STOCK_OUT', -3, 'DAILY_REGULAR', '음료 베이스 시럽 소모', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (3, 'STOCK_OUT', -3, 'DAILY_REGULAR', '딸기 라떼 주문 폭증 소모', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (4, 'STOCK_OUT', -22000, 'DAILY_REGULAR', '정상 영업 라떼 우유 소모', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (2, 'STOCK_DISCARD', -1, 'DAMAGED', '시럽 용기 파손으로 인한 긴급 폐기', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                                                         (4, 'STOCK_DISCARD', -1000, 'DAMAGED', '우유 팩 파손으로 인한 긴급 폐기', 'admin', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY));

-- [3주 전 월요일]
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', -1400, 'DAILY_REGULAR', '정상 영업 원두 소모', 'admin', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (2, 'STOCK_OUT', -2, 'DAILY_REGULAR', '정상 영업 시럽 소모', 'admin', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (3, 'STOCK_OUT', -2, 'DAILY_REGULAR', '정상 영업 딸기 소모', 'admin', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                                                         (4, 'STOCK_OUT', -15000, 'DAILY_REGULAR', '정상 영업 라떼 우유 소모', 'admin', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY));

-- [4주 전 월요일]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                         (1, 'STOCK_OUT', '비오는 날 매장 방문객 폭증 원두 소모', -2000, 'DAILY_REGULAR', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (2, 'STOCK_OUT', '아이스 음료 시럽 소모 증가', -5, 'DAILY_REGULAR', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (3, 'STOCK_OUT', '디저트 토핑용 냉동 딸기 대량 소모', -4, 'DAILY_REGULAR', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (4, 'STOCK_OUT', '비오는 날 단체 주문 폭증 우유 소모', -25000, 'DAILY_REGULAR', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (3, 'STOCK_DISCARD', '주말 재고 보관 만료로 인한 냉동 딸기 일부 폐기', -1, 'EXPIRED', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                                                         (4, 'STOCK_DISCARD', '복귀 주말 재고 과다로 인한 우유 폐기', -3000, 'EXPIRED', 'admin', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY));

-- =====================================================================================
-- 9. 신규 식자재(5~12번) 4주간 히스토리 소모 데이터
-- =====================================================================================

-- [4주 전: Day 28 ~ 22]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
    (5,  'STOCK_OUT', '초코 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -65, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -35, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -90, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -18, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -25, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -72, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -105, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -21, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -52, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -28, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -80, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -115, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -58, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -55, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -30, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -85, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -50, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -120, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -26, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -62, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -58, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -50, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -18, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -55, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -30, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -80, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -15, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -35, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -32, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -20, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -60, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -33, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -88, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -17, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -35, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -23, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -68, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -95, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -19, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -47, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY));

-- [3주 전: Day 21 ~ 15]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
    (5,  'STOCK_OUT', '초코 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -70, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -20, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -50, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -27, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -78, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -110, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -23, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -55, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -50, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -46, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -32, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -88, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -52, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -118, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -27, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -60, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -55, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -52, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -21, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -63, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -36, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -92, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -17, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -36, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -23, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -67, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -96, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -19, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -46, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -38, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -26, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -74, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -104, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -53, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -29, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -82, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -112, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -25, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -58, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -53, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- [2주 전: Day 14 ~ 8]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
    (5,  'STOCK_OUT', '초코 파우더 소모', -25, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -73, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -42, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -102, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -21, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -51, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -46, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -28, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -79, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -47, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -108, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -57, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -52, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -47, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -31, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -86, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -51, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -116, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -26, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -61, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -56, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -51, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -20, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -61, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -34, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -89, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -16, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -41, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -37, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -33, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -65, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -37, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -94, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -18, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -36, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -70, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -100, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -20, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -39, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 9 DAY),  DATE_SUB(NOW(), INTERVAL 9 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -27, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -76, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -107, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -23, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -55, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -50, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 8 DAY),  DATE_SUB(NOW(), INTERVAL 8 DAY));

-- [1주 전: Day 7 ~ 1]
INSERT INTO historical_stock_log (ingredient_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
    (5,  'STOCK_OUT', '초코 파우더 소모', -26, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -75, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -105, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -53, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -48, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -29, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -83, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -49, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -113, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -25, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -59, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -54, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -49, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -33, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -90, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -54, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -120, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -28, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -63, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -58, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -53, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -64, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -37, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -93, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -18, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -45, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -41, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -37, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -69, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -39, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',   -98, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -20, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -49, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -44, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -40, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -26, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -74, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -103, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -22, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -52, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -47, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -43, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (5,  'STOCK_OUT', '초코 파우더 소모', -28, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (6,  'STOCK_OUT', '카라멜 시럽 소모', -79, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (7,  'STOCK_OUT', '생크림 소모',      -47, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (8,  'STOCK_OUT', '설탕 시럽 소모',  -109, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (9,  'STOCK_OUT', '녹차 파우더 소모', -24, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (10, 'STOCK_OUT', '오렌지 시럽 소모', -56, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (11, 'STOCK_OUT', '딸기 시럽 소모',   -51, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (12, 'STOCK_OUT', '유자청 소모',      -46, 'DAILY_REGULAR', 'SYSTEM', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));