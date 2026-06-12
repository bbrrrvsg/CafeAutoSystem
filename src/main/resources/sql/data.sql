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
                                                                     ('아메리카노', 3000, NOW(), NOW()),
                                                                     ('카페라떼', 3800, NOW(), NOW()),
                                                                     ('바닐라라떼', 4300, NOW(), NOW()),
                                                                     ('카페모카', 4300, NOW(), NOW()),
                                                                     ('아메모카', 4000, NOW(), NOW()),
                                                                     ('카라멜 마키아또', 4000, NOW(), NOW()),
                                                                     ('초코라떼', 4000, NOW(), NOW()),
                                                                     ('딸기 에이드', 4500, NOW(), NOW()),
                                                                     ('콜드브루', 4500, NOW(), NOW());

-- =====================================================================================
-- 2. VENDOR (거래처 마스터)
-- =====================================================================================
INSERT INTO vendor (vendor_id, vendor_name, manager_email, manager_phone, created_at, updated_at) VALUES
                                                                                                      (1, '서울원두유통', 'seoul_bean@naver.com', '010-1234-5678', NOW(), NOW()),
                                                                                                      (2, '매일유통 대리점', 'maeil_milk@gmail.com', '010-9876-5432', NOW(), NOW()),
                                                                                                      (3, '대박부자재마트', 'daebak_pack@daum.net', '02-111-2222', NOW(), NOW()),
                                                                                                      (4, '한국제과재료', 'kbakery@naver.com', '031-222-3344', NOW(), NOW()),
                                                                                                      (5, '청정과일농장', 'fresh_fruit@gmail.com', '010-2222-1111', NOW(), NOW()),
                                                                                                      (6, '프리미엄시럽', 'premium_syrup@daum.net', '02-555-7788', NOW(), NOW());

-- =====================================================================================
-- 3. INGREDIENT (식자재 마스터)
-- =====================================================================================
INSERT INTO ingredient (ingredient_id, ingredient_name, unit, safety_stock, ingredient_image, shelf_life_days) VALUES
                                                                                                                   (1, '하우스 블렌드 원두', 'g', 5000, '/images/ingredients/house_blend.jpg', 90),
                                                                                                                   (2, '파우더 및 시럽', '개', 10, '/images/ingredients/powder_syrup.jpg', 180),
                                                                                                                   (3, '냉동 딸기 블렌드', 'pack', 5, '/images/ingredients/frozen_strawberry.jpg', 180),
                                                                                                                   (4, '신선한 우유(1L)', 'ml', 5000, '/images/ingredients/fresh_milk_1l.jpg', 7);

-- =====================================================================================
-- 4. VENDOR_INGREDIENT (거래처-식자재 매핑)
-- =====================================================================================
INSERT INTO vendor_ingredient (vendor_ingredient_id, vendor_id, ingredient_id, unit_price, priority_rank, created_at, updated_at) VALUES
                                                                                                                                      (1, 1, 1, 15, 1, NOW(), NOW()),
                                                                                                                                      (2, 2, 4, 3, 1, NOW(), NOW()),
                                                                                                                                      (3, 3, 4, 2700, 2, NOW(), NOW()),
                                                                                                                                      (4, 6, 2, 8500, 1, NOW(), NOW()),
                                                                                                                                      (5, 5, 3, 12000, 1, NOW(), NOW());

-- =====================================================================================
-- 5. PURCHASE_ORDER (초기 대시보드 진입용 과거 완료 내역 샘플 데이터)
-- =====================================================================================
INSERT INTO purchase_order (vendor_ingredient_id, order_date_key, suggested_qty, final_qty, status, expiration_date, created_at, updated_at) VALUES
                                                                                                                                                 (1, 'PO-20260601', 15, 15, 'COMPLETED', '2026-08-01', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                                 (2, 'PO-20260601', 40, 40, 'COMPLETED', '2026-06-15', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));


INSERT INTO current_stock_log (ingredient_id, amount, log_type, message, reason, user_id, created_at, updated_at) VALUES
                                                                                                                      (1, 3000, 'STOCK_IN', '[초기화] 마스터 데이터 기준 원두 기초재고 등록', '초기 재고 실사', 'SYSTEM', NOW(), NOW()), -- 1번 원두 (3,000g)
                                                                                                                      (2, 5,    'STOCK_IN', '[초기화] 마스터 데이터 기준 시럽 기초재고 등록', '초기 재고 실사', 'SYSTEM', NOW(), NOW()), -- 2번 시럽 (5개)
                                                                                                                      (3, 2,    'STOCK_IN', '[초기화] 마스터 데이터 기준 딸기 기초재고 등록', '초기 재고 실사', 'SYSTEM', NOW(), NOW()), -- 3번 딸기 (2pack)
                                                                                                                      (4, 3500, 'STOCK_IN', '[초기화] 마스터 데이터 기준 우유 기초재고 등록', '초기 재고 실사', 'SYSTEM', NOW(), NOW()); -- 4번 우유 (3,500ml


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