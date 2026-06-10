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
INSERT INTO ingredient (ingredient_id, ingredient_name, unit, safety_stock, unit_per_order, ingredient_image) VALUES
                                                                                                                  (1, '하우스 블렌드 원두', 'g', 5000, 1000, '/images/ingredients/house_blend.jpg'),
                                                                                                                  (2, '파우더 및 시럽', '개', 10, 1, '/images/ingredients/powder_syrup.jpg'),
                                                                                                                  (3, '냉동 딸기 블렌드', 'pack', 5, 1, '/images/ingredients/frozen_strawberry.jpg'),
                                                                                                                  (4, '신선한 우유(1L)', 'ml', 5000, 1000, '/images/ingredients/fresh_milk_1l.jpg');

-- =====================================================================================
-- 4. VENDOR_INGREDIENT (거래처-식자재 매핑)
-- =====================================================================================
INSERT INTO vendor_ingredient (vendor_ingredient_id, vendor_id, ingredient_id, unit_price, priority_rank, created_at, updated_at) VALUES
                                                                                                                                      (1, 1, 1, 15000, 1, NOW(), NOW()),
                                                                                                                                      (2, 2, 4, 2500, 1, NOW(), NOW()),
                                                                                                                                      (3, 3, 4, 2700, 2, NOW(), NOW()),
                                                                                                                                      (4, 6, 2, 8500, 1, NOW(), NOW()),
                                                                                                                                      (5, 5, 3, 12000, 1, NOW(), NOW());

-- =====================================================================================
-- 5. PURCHASE_ORDER (샘플 발주 데이터 - 최소 유지)
-- =====================================================================================
INSERT INTO purchase_order (vendor_ingredient_id, order_date_key, suggested_qty, final_qty, status, expiration_date, created_at, updated_at) VALUES
                                                                                                                                                 (1, 'PO-20260601', 15, 15, 'COMPLETED', '2026-08-01', NOW(), NOW()),
                                                                                                                                                 (2, 'PO-20260601', 40, 40, 'COMPLETED', '2026-06-15', NOW(), NOW());