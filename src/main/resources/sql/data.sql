-- =========================================================
-- CafeAutoSystem 사장 서버: 전체 초기화 후 시연 데이터 재삽입
-- DB: MySQL
--
-- 중요:
-- 1) 구매 서버와 사장 서버 애플리케이션을 모두 중지한 뒤 실행한다.
-- 2) 현재 업무 데이터와 outbox만 초기화한다. processed_event는 보존한다.
-- 3) 구매 서버와 동일한 900001번대 review_id를 사용한다.
-- 4) 더미 리뷰는 전부 COMPLETED 상태다.
-- 5) 더미 outbox는 넣지 않으며, processed_event는 절대 초기화하지 않는다.
-- 6) 실제 신규 주문은 order_id=1021부터 생성된다.
-- 7) 메뉴 이미지는 전부 NULL로 넣고, 배포 화면에서 직접 등록한다.
-- =========================================================
SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;
DELETE FROM order_detail;
DELETE FROM cafe_order;
DELETE FROM review_read;
DELETE FROM outbox;
DELETE FROM purchase_order;
DELETE FROM vendor_ingredient;
DELETE FROM current_stock_log;
DELETE FROM historical_stock_log;
DELETE FROM menu_recipe;
DELETE FROM menu;
DELETE FROM ingredient;
DELETE FROM vendor;
SET FOREIGN_KEY_CHECKS = 1;
INSERT INTO vendor
(vendor_id, vendor_name, manager_email, manager_phone, created_at, updated_at)
VALUES
(1, '서울원두유통', 'seoul_bean@naver.com', '010-1234-5678', NOW(), NOW()),
(2, '매일유통 대리점', 'maeil_milk@gmail.com', '010-9876-5432', NOW(), NOW()),
(3, '대박부자재마트', 'daebak_pack@daum.net', '02-111-2222', NOW(), NOW()),
(4, '프레시과일상사', 'fresh_fruit@naver.com', '010-5555-1212', NOW(), NOW()),
(5, '베이커리팩토리', 'bakery_factory@daum.net', '02-333-4444', NOW(), NOW());
INSERT INTO ingredient
(ingredient_id, ingredient_name, unit, safety_stock, ingredient_image)
VALUES
(1, '에스프레소 원두', 'g', 3000, NULL),
(2, '정수 물', 'ml', 10000, NULL),
(3, '서울우유 1000ml', 'ml', 15000, NULL),
(4, '바닐라 시럽', 'ml', 1000, NULL),
(5, '카라멜 소스', 'ml', 1000, NULL),
(6, '초코 파우더', 'g', 800, NULL),
(7, '말차 파우더', 'g', 700, NULL),
(8, '딸기 베이스', 'g', 1200, NULL),
(9, '망고 베이스', 'g', 1200, NULL),
(10, '레몬 베이스', 'ml', 1000, NULL),
(11, '자몽 베이스', 'ml', 1000, NULL),
(12, '홍차 티백', 'ea', 50, NULL),
(13, '허니브레드 완제품', 'ea', 20, NULL),
(14, '치즈케이크 완제품', 'ea', 20, NULL),
(15, '크로플 생지', 'ea', 20, NULL),
(16, '흑임자 파우더', 'g', 500, NULL);
INSERT INTO vendor_ingredient
(vendor_ingredient_id, vendor_id, ingredient_id, unit_price, priority_rank, created_at, updated_at)
VALUES
(1, 1, 1, 1100, 1, NOW(), NOW()),
(2, 3, 2, 1200, 1, NOW(), NOW()),
(3, 2, 3, 1300, 1, NOW(), NOW()),
(4, 3, 4, 1400, 1, NOW(), NOW()),
(5, 3, 5, 1500, 1, NOW(), NOW()),
(6, 3, 6, 1600, 1, NOW(), NOW()),
(7, 3, 7, 1700, 1, NOW(), NOW()),
(8, 4, 8, 1800, 1, NOW(), NOW()),
(9, 4, 9, 1900, 1, NOW(), NOW()),
(10, 4, 10, 2000, 1, NOW(), NOW()),
(11, 4, 11, 2100, 1, NOW(), NOW()),
(12, 3, 12, 2200, 1, NOW(), NOW()),
(13, 5, 13, 2300, 1, NOW(), NOW()),
(14, 5, 14, 2400, 1, NOW(), NOW()),
(15, 5, 15, 2500, 1, NOW(), NOW()),
(16, 3, 16, 2600, 1, NOW(), NOW());
INSERT INTO menu
(menu_id, menu_name, menu_price, menu_image, created_at, updated_at)
VALUES
(1, '아메리카노', 3000, NULL, NOW(), NOW()),
(2, '카페라떼', 4200, NULL, NOW(), NOW()),
(3, '바닐라라떼', 4800, NULL, NOW(), NOW()),
(4, '돌체라떼', 5200, NULL, NOW(), NOW()),
(5, '카라멜마키아토', 5500, NULL, NOW(), NOW()),
(6, '초코라떼', 4500, NULL, NOW(), NOW()),
(7, '말차라떼', 4800, NULL, NOW(), NOW()),
(8, '딸기스무디', 5800, NULL, NOW(), NOW()),
(9, '망고스무디', 5800, NULL, NOW(), NOW()),
(10, '레몬에이드', 5200, NULL, NOW(), NOW()),
(11, '자몽에이드', 5400, NULL, NOW(), NOW()),
(12, '아이스티', 3900, NULL, NOW(), NOW()),
(13, '허니브레드', 6500, NULL, NOW(), NOW()),
(14, '치즈케이크', 6200, NULL, NOW(), NOW()),
(15, '크로플', 5900, NULL, NOW(), NOW()),
(16, '시즌 한정 흑임자라떼', 5600, NULL, NOW(), NOW());
INSERT INTO menu_recipe
(recipe_id, menu_name, price, ingredient_id, required_quantity, note)
VALUES
(1, '아메리카노', 3000, 1, 18, '원두 18g'),
(2, '아메리카노', 3000, 2, 180, '물 180ml'),
(3, '카페라떼', 4200, 1, 18, '원두 18g'),
(4, '카페라떼', 4200, 3, 200, '우유 200ml'),
(5, '바닐라라떼', 4800, 1, 18, '원두 18g'),
(6, '바닐라라떼', 4800, 3, 200, '우유 200ml'),
(7, '바닐라라떼', 4800, 4, 25, '바닐라 시럽 25ml'),
(8, '돌체라떼', 5200, 1, 18, '원두 18g'),
(9, '돌체라떼', 5200, 3, 220, '우유 220ml'),
(10, '돌체라떼', 5200, 5, 30, '카라멜 소스 30ml'),
(11, '카라멜마키아토', 5500, 1, 18, '원두 18g'),
(12, '카라멜마키아토', 5500, 3, 200, '우유 200ml'),
(13, '카라멜마키아토', 5500, 5, 35, '카라멜 소스 35ml'),
(14, '초코라떼', 4500, 3, 220, '우유 220ml'),
(15, '초코라떼', 4500, 6, 35, '초코 파우더 35g'),
(16, '말차라떼', 4800, 3, 220, '우유 220ml'),
(17, '말차라떼', 4800, 7, 25, '말차 파우더 25g'),
(18, '딸기스무디', 5800, 8, 160, '딸기 베이스 160g'),
(19, '딸기스무디', 5800, 3, 120, '우유 120ml'),
(20, '망고스무디', 5800, 9, 160, '망고 베이스 160g'),
(21, '망고스무디', 5800, 3, 120, '우유 120ml'),
(22, '레몬에이드', 5200, 10, 80, '레몬 베이스 80ml'),
(23, '레몬에이드', 5200, 2, 200, '물 200ml'),
(24, '자몽에이드', 5400, 11, 80, '자몽 베이스 80ml'),
(25, '자몽에이드', 5400, 2, 200, '물 200ml'),
(26, '아이스티', 3900, 12, 1, '홍차 티백 1개'),
(27, '아이스티', 3900, 2, 250, '물 250ml'),
(28, '허니브레드', 6500, 13, 1, '허니브레드 1개'),
(29, '치즈케이크', 6200, 14, 1, '치즈케이크 1개'),
(30, '크로플', 5900, 15, 1, '크로플 생지 1개'),
(31, '시즌 한정 흑임자라떼', 5600, 1, 18, '원두 18g'),
(32, '시즌 한정 흑임자라떼', 5600, 3, 220, '우유 220ml'),
(33, '시즌 한정 흑임자라떼', 5600, 16, 30, '흑임자 파우더 30g');
INSERT INTO current_stock_log
(log_id, ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at)
VALUES
(1, 1, NULL, 'STOCK_IN', '[입고] 에스프레소 원두 20kg 입고', 20000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(2, 2, NULL, 'STOCK_IN', '[입고] 정수 물 기준 재고 80L 반영', 80000, '시스템 초기 재고', 'SYSTEM', NOW(), NOW()),
(3, 3, NULL, 'STOCK_IN', '[입고] 서울우유 60L 입고', 60000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(4, 4, NULL, 'STOCK_IN', '[입고] 바닐라 시럽 5L 입고', 5000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(5, 5, NULL, 'STOCK_IN', '[입고] 카라멜 소스 5L 입고', 5000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(6, 6, NULL, 'STOCK_IN', '[입고] 초코 파우더 5kg 입고', 5000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(7, 7, NULL, 'STOCK_IN', '[입고] 말차 파우더 3kg 입고', 3000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(8, 8, NULL, 'STOCK_IN', '[입고] 딸기 베이스 8kg 입고', 8000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(9, 9, NULL, 'STOCK_IN', '[입고] 망고 베이스 8kg 입고', 8000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(10, 10, NULL, 'STOCK_IN', '[입고] 레몬 베이스 6L 입고', 6000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(11, 11, NULL, 'STOCK_IN', '[입고] 자몽 베이스 6L 입고', 6000, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(12, 12, NULL, 'STOCK_IN', '[입고] 홍차 티백 200개 입고', 200, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(13, 13, NULL, 'STOCK_IN', '[입고] 허니브레드 60개 입고', 60, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(14, 14, NULL, 'STOCK_IN', '[입고] 치즈케이크 50개 입고', 50, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(15, 15, NULL, 'STOCK_IN', '[입고] 크로플 생지 70개 입고', 70, '정기 발주 입고', 'SYSTEM', NOW(), NOW()),
(16, 16, NULL, 'STOCK_IN', '[입고] 흑임자 파우더 3kg 입고', 3000, '시즌 메뉴 입고', 'SYSTEM', NOW(), NOW()),
(17, 1, NULL, 'STOCK_OUT', '[판매] 커피 메뉴 원두 자동 차감', -1800, '레시피 자동 차감', 'SYSTEM', NOW(), NOW()),
(18, 3, NULL, 'STOCK_OUT', '[판매] 라떼/스무디 우유 자동 차감', -8200, '레시피 자동 차감', 'SYSTEM', NOW(), NOW()),
(19, 3, NULL, 'STOCK_WARNING', '[재고주의] 우유 소모량이 평소 대비 18% 증가', 0, '안전재고 모니터링', 'SYSTEM', NOW(), NOW()),
(20, 1, NULL, 'RPA_RETRY', '[장애복구] RPA 메일 재전송 성공', 0, '재시도 성공', 'SYSTEM', NOW(), NOW()),
(21, 3, NULL, 'AI_VALIDATION', '[AI오발주차단] 우유 100팩 이상치 발주 감지', 0, '과거 평균 대비 오차 초과', 'SYSTEM', NOW(), NOW());
INSERT INTO historical_stock_log
(ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at)
VALUES
(2, NULL, 'STOCK_OUT', '[통계] 5월 01일 재고 변동 로그', -1010, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 1 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 1 DAY)),
(3, NULL, 'STOCK_OUT', '[통계] 5월 02일 재고 변동 로그', -1020, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 2 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 2 DAY)),
(4, NULL, 'STOCK_IN', '[통계] 5월 03일 재고 변동 로그', 3300, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 3 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 3 DAY)),
(5, NULL, 'STOCK_OUT', '[통계] 5월 04일 재고 변동 로그', -1040, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 4 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 4 DAY)),
(6, NULL, 'STOCK_OUT', '[통계] 5월 05일 재고 변동 로그', -1050, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 5 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 5 DAY)),
(7, NULL, 'STOCK_IN', '[통계] 5월 06일 재고 변동 로그', 3600, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 6 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 6 DAY)),
(8, NULL, 'STOCK_OUT', '[통계] 5월 07일 재고 변동 로그', -1070, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 7 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 7 DAY)),
(9, NULL, 'STOCK_OUT', '[통계] 5월 08일 재고 변동 로그', -1080, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 8 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 8 DAY)),
(10, NULL, 'STOCK_IN', '[통계] 5월 09일 재고 변동 로그', 3900, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 9 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 9 DAY)),
(11, NULL, 'STOCK_OUT', '[통계] 5월 10일 재고 변동 로그', -1100, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 10 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 10 DAY)),
(12, NULL, 'STOCK_OUT', '[통계] 5월 11일 재고 변동 로그', -1110, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 11 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 11 DAY)),
(13, NULL, 'STOCK_IN', '[통계] 5월 12일 재고 변동 로그', 4200, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 12 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 12 DAY)),
(14, NULL, 'STOCK_OUT', '[통계] 5월 13일 재고 변동 로그', -1130, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 13 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 13 DAY)),
(15, NULL, 'STOCK_OUT', '[통계] 5월 14일 재고 변동 로그', -1140, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 14 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 14 DAY)),
(16, NULL, 'STOCK_IN', '[통계] 5월 15일 재고 변동 로그', 4500, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 15 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 15 DAY)),
(1, NULL, 'STOCK_OUT', '[통계] 5월 16일 재고 변동 로그', -1160, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 16 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 16 DAY)),
(2, NULL, 'STOCK_OUT', '[통계] 5월 17일 재고 변동 로그', -1170, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 17 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 17 DAY)),
(3, NULL, 'STOCK_IN', '[통계] 5월 18일 재고 변동 로그', 4800, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 18 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 18 DAY)),
(4, NULL, 'STOCK_OUT', '[통계] 5월 19일 재고 변동 로그', -1190, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 19 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 19 DAY)),
(5, NULL, 'STOCK_OUT', '[통계] 5월 20일 재고 변동 로그', -1200, '시연용 과거 재고 통계', 'SYSTEM', DATE_ADD('2026-05-01 09:00:00', INTERVAL 20 DAY), DATE_ADD('2026-05-01 09:00:00', INTERVAL 20 DAY));
INSERT INTO purchase_order
(order_item_id, vendor_ingredient_id, order_date_key, suggested_qty, final_qty, status, expiration_date, created_at, updated_at)
VALUES
(1, 1, 'PO-20260612', 15, 15, 'PENDING', NULL, NOW(), NOW()),
(2, 3, 'PO-20260612', 50000, 50000, 'PENDING', '2026-06-19', NOW(), NOW()),
(3, 8, 'PO-20260612', 6000, 6000, 'APPROVED', '2026-06-25', NOW(), NOW()),
(4, 14, 'PO-20260612', 20, 20, 'SENT', '2026-06-18', NOW(), NOW()),
(5, 16, 'PO-20260612', 3000, 3000, 'PENDING', NULL, NOW(), NOW());
INSERT INTO cafe_order
(order_id, qr_url, order_price, created_at, updated_at)
VALUES
(1001, '/qrcodes/order-1001.png', 7200, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1002, '/qrcodes/order-1002.png', 4200, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1003, '/qrcodes/order-1003.png', 11000, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1004, '/qrcodes/order-1004.png', 5800, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1005, '/qrcodes/order-1005.png', 12000, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1006, '/qrcodes/order-1006.png', 3000, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1007, '/qrcodes/order-1007.png', 9000, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1008, '/qrcodes/order-1008.png', 5400, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1009, '/qrcodes/order-1009.png', 10700, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1010, '/qrcodes/order-1010.png', 6500, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1011, '/qrcodes/order-1011.png', 8400, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1012, '/qrcodes/order-1012.png', 4800, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1013, '/qrcodes/order-1013.png', 11000, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1014, '/qrcodes/order-1014.png', 5200, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1015, '/qrcodes/order-1015.png', 12400, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1016, '/qrcodes/order-1016.png', 5800, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1017, '/qrcodes/order-1017.png', 4200, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1018, '/qrcodes/order-1018.png', 10400, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1019, '/qrcodes/order-1019.png', 5900, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(1020, '/qrcodes/order-1020.png', 15600, '2026-06-11 10:00:00', '2026-06-11 10:00:00');
INSERT INTO order_detail
(order_detail_id, quantity, order_id, menu_id, created_at, updated_at)
VALUES
(1, 1, 1001, 1, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(2, 1, 1001, 2, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(3, 1, 1002, 2, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(4, 1, 1003, 3, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(5, 1, 1003, 14, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(6, 1, 1004, 8, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(7, 1, 1005, 5, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(8, 1, 1005, 13, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(9, 1, 1006, 1, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(10, 3, 1007, 1, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(11, 1, 1008, 11, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(12, 1, 1009, 7, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(13, 1, 1009, 15, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(14, 1, 1010, 13, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(15, 2, 1011, 2, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(16, 1, 1012, 3, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(17, 2, 1013, 5, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(18, 1, 1014, 10, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(19, 2, 1015, 14, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(20, 1, 1016, 8, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(21, 1, 1017, 2, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(22, 1, 1018, 12, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(23, 1, 1018, 13, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(24, 1, 1019, 15, '2026-06-11 10:00:00', '2026-06-11 10:00:00'),
(25, 3, 1020, 4, '2026-06-11 10:00:00', '2026-06-11 10:00:00');
INSERT INTO review_read
(review_id, order_id, review_content, analysis_result_json, analysis_status, analyzed_at,
 customer_created_at, reply_content, reply_status, replied_at, reply_updated_at, created_at, updated_at)
VALUES
(900001, 900001, '아메리카노가 진하고 산미가 적당해서 좋았어요. 매장도 조용해서 잠깐 작업하기 좋았습니다.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"진하고 산미가 적당해서 좋았어요"},{"category":"ATMOSPHERE","sentiment":"POSITIVE","evidence":"매장도 조용해서"}]}', 'COMPLETED', '2026-06-10 09:16:00', '2026-06-10 09:16:00', '소중한 리뷰 감사합니다. 앞으로도 편안하게 머무실 수 있는 공간과 균형 잡힌 커피 맛을 유지하겠습니다.', 'ACTIVE', '2026-06-10 09:16:00', '2026-06-10 09:16:00', '2026-06-10 09:16:00', '2026-06-10 09:16:00'),
(900002, 900002, '라떼는 맛있었는데 주문이 조금 늦게 나왔어요. 점심시간이라 그런 건 이해합니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"라떼는 맛있었는데"},{"category":"WAITING","sentiment":"NEGATIVE","evidence":"주문이 조금 늦게 나왔어요"}]}', 'COMPLETED', '2026-06-10 09:38:00', '2026-06-10 09:38:00', '이용에 불편을 드려 죄송합니다. 점심 피크 시간에도 더 빠르게 제공될 수 있도록 제조 동선을 점검하겠습니다.', 'ACTIVE', '2026-06-10 09:38:00', '2026-06-10 09:38:00', '2026-06-10 09:38:00', '2026-06-10 09:38:00'),
(900003, 900003, '바닐라라떼랑 치즈케이크 조합이 좋았어요. 케이크가 부드럽고 달지 않아서 만족했습니다.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"케이크가 부드럽고 달지 않아서 만족했습니다"}]}', 'COMPLETED', '2026-06-10 10:08:00', '2026-06-10 10:08:00', NULL, 'NONE', NULL, NULL, '2026-06-10 10:08:00', '2026-06-10 10:08:00'),
(900004, 900004, '딸기스무디가 사진보다 양이 많고 시원해서 좋았습니다. 다만 좌석 간격은 조금 좁아요.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"양이 많고 시원해서 좋았습니다"},{"category":"SEAT","sentiment":"NEGATIVE","evidence":"좌석 간격은 조금 좁아요"}]}', 'COMPLETED', '2026-06-10 10:47:00', '2026-06-10 10:47:00', NULL, 'NONE', NULL, NULL, '2026-06-10 10:47:00', '2026-06-10 10:47:00'),
(900005, 900005, '카라멜마키아토는 달달해서 좋았는데 컵 주변이 조금 끈적했어요. 위생은 신경 써주시면 좋겠습니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"HIGH","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"달달해서 좋았는데"},{"category":"CLEANLINESS","sentiment":"NEGATIVE","evidence":"컵 주변이 조금 끈적했어요"}]}', 'COMPLETED', '2026-06-10 11:21:00', '2026-06-10 11:21:00', '불편을 드려 정말 죄송합니다. 컵 포장 및 음료 제공 전 확인 절차를 다시 점검하겠습니다.', 'ACTIVE', '2026-06-10 11:21:00', '2026-06-10 11:21:00', '2026-06-10 11:21:00', '2026-06-10 11:21:00'),
(900006, 900007, '아메리카노 두 잔 주문했는데 빠르게 나와서 좋았습니다. 직원분도 친절했어요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"WAITING","sentiment":"POSITIVE","evidence":"빠르게 나와서 좋았습니다"},{"category":"SERVICE","sentiment":"POSITIVE","evidence":"직원분도 친절했어요"}]}', 'COMPLETED', '2026-06-10 12:31:00', '2026-06-10 12:31:00', '좋은 말씀 감사합니다. 빠르고 친절한 응대를 유지할 수 있도록 팀원들과 함께 노력하겠습니다.', 'ACTIVE', '2026-06-10 12:31:00', '2026-06-10 12:31:00', '2026-06-10 12:31:00', '2026-06-10 12:31:00'),
(900007, 900008, '자몽에이드는 상큼했는데 얼음이 너무 많아서 음료가 금방 싱거워졌어요.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"NEGATIVE","evidence":"얼음이 너무 많아서 음료가 금방 싱거워졌어요"}]}', 'COMPLETED', '2026-06-10 13:05:00', '2026-06-10 13:05:00', NULL, 'NONE', NULL, NULL, '2026-06-10 13:05:00', '2026-06-10 13:05:00'),
(900008, 900009, '말차라떼가 진해서 좋았고 크로플도 바삭했습니다. 다음에도 방문할 것 같아요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"말차라떼가 진해서 좋았고 크로플도 바삭했습니다"},{"category":"REVISIT","sentiment":"POSITIVE","evidence":"다음에도 방문할 것 같아요"}]}', 'COMPLETED', '2026-06-10 13:39:00', '2026-06-10 13:39:00', '방문해주셔서 감사합니다. 말차라떼와 크로플 조합은 앞으로도 맛있게 준비하겠습니다.', 'ACTIVE', '2026-06-10 13:39:00', '2026-06-10 13:39:00', '2026-06-10 13:39:00', '2026-06-10 13:39:00'),
(900009, 900011, '라떼 맛은 괜찮았는데 매장이 조금 추웠어요. 오래 앉아있기는 어려웠습니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"라떼 맛은 괜찮았는데"},{"category":"FACILITY","sentiment":"NEGATIVE","evidence":"매장이 조금 추웠어요"}]}', 'COMPLETED', '2026-06-11 09:25:00', '2026-06-11 09:25:00', NULL, 'NONE', NULL, NULL, '2026-06-11 09:25:00', '2026-06-11 09:25:00'),
(900010, 900012, '바닐라라떼가 너무 달지 않아서 좋았습니다. 포장도 깔끔했어요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"너무 달지 않아서 좋았습니다"},{"category":"SERVICE","sentiment":"POSITIVE","evidence":"포장도 깔끔했어요"}]}', 'COMPLETED', '2026-06-11 09:50:00', '2026-06-11 09:50:00', '리뷰 감사합니다. 앞으로도 깔끔한 포장과 부담 없는 당도의 음료를 제공하겠습니다.', 'ACTIVE', '2026-06-11 09:50:00', '2026-06-11 09:50:00', '2026-06-11 09:50:00', '2026-06-11 09:50:00'),
(900011, 900013, '주문이 누락된 줄 알고 기다렸는데 직원분이 먼저 확인해주셔서 다행이었습니다. 시스템 알림이 더 명확하면 좋겠어요.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"WAITING","sentiment":"NEGATIVE","evidence":"주문이 누락된 줄 알고 기다렸는데"},{"category":"SERVICE","sentiment":"POSITIVE","evidence":"직원분이 먼저 확인해주셔서"}]}', 'COMPLETED', '2026-06-11 10:30:00', '2026-06-11 10:30:00', NULL, 'NONE', NULL, NULL, '2026-06-11 10:30:00', '2026-06-11 10:30:00'),
(900012, 900014, '레몬에이드가 상큼하고 탄산도 적당했습니다. 더운 날 마시기 좋네요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"상큼하고 탄산도 적당했습니다"}]}', 'COMPLETED', '2026-06-11 11:11:00', '2026-06-11 11:11:00', '시원하게 즐겨주셔서 감사합니다. 더운 날 기분 좋게 드실 수 있도록 항상 신선하게 준비하겠습니다.', 'ACTIVE', '2026-06-11 11:11:00', '2026-06-11 11:11:00', '2026-06-11 11:11:00', '2026-06-11 11:11:00'),
(900013, 900015, '치즈케이크는 맛있었는데 포크가 빠져있었어요. 포장 주문 확인 부탁드립니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"치즈케이크는 맛있었는데"},{"category":"SERVICE","sentiment":"NEGATIVE","evidence":"포크가 빠져있었어요"}]}', 'COMPLETED', '2026-06-11 11:55:00', '2026-06-11 11:55:00', '불편을 드려 죄송합니다. 포장 전 구성품 확인 체크를 강화하겠습니다.', 'ACTIVE', '2026-06-11 11:55:00', '2026-06-11 11:55:00', '2026-06-11 11:55:00', '2026-06-11 11:55:00'),
(900014, 900017, '카페라떼 우유 비율이 좋아요. 고소하고 부드러웠습니다.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"고소하고 부드러웠습니다"}]}', 'COMPLETED', '2026-06-11 13:20:00', '2026-06-11 13:20:00', NULL, 'NONE', NULL, NULL, '2026-06-11 13:20:00', '2026-06-11 13:20:00'),
(900015, 900018, '아이스티랑 허니브레드 먹었는데 무난했습니다. 특별히 나쁘진 않았어요.', '{"overallSentiment":"NEUTRAL","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"NEUTRAL","evidence":"무난했습니다"}]}', 'COMPLETED', '2026-06-11 14:06:00', '2026-06-11 14:06:00', NULL, 'NONE', NULL, NULL, '2026-06-11 14:06:00', '2026-06-11 14:06:00'),
(900016, 900020, '음료 세 잔 주문했는데 하나가 잘못 나왔습니다. 바로 바꿔주긴 했지만 조금 아쉬웠어요.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"SERVICE","sentiment":"NEGATIVE","evidence":"하나가 잘못 나왔습니다"}]}', 'COMPLETED', '2026-06-11 16:35:00', '2026-06-11 16:35:00', '주문 오류로 불편을 드려 죄송합니다. 제조 전 주문 확인 절차를 더 꼼꼼히 진행하겠습니다.', 'ACTIVE', '2026-06-11 16:35:00', '2026-06-11 16:35:00', '2026-06-11 16:35:00', '2026-06-11 16:35:00'),
(900017, 900022, '오늘 아메리카노랑 아이스티 주문했는데 빠르고 맛도 괜찮았습니다.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"고소하고 부드러웠습니다"}]}', 'COMPLETED', '2026-06-12 09:40:30', '2026-06-12 09:40:00', NULL, 'NONE', NULL, NULL, '2026-06-12 09:40:00', '2026-06-12 09:40:00'),
(900018, 900023, '망고스무디가 너무 달았어요. 단맛 조절 옵션이 있으면 좋겠습니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"MEDIUM","categories":[{"category":"TASTE","sentiment":"NEGATIVE","evidence":"너무 달았어요"}]}', 'COMPLETED', '2026-06-12 10:15:00', '2026-06-12 10:15:00', NULL, 'NONE', NULL, NULL, '2026-06-12 10:15:00', '2026-06-12 10:15:00'),
(900019, 900025, '돌체라떼랑 크로플 둘 다 맛있었습니다. 직원분 추천대로 시켰는데 만족했어요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"둘 다 맛있었습니다"},{"category":"SERVICE","sentiment":"POSITIVE","evidence":"직원분 추천대로"}]}', 'COMPLETED', '2026-06-12 11:25:00', '2026-06-12 11:25:00', '만족해주셔서 감사합니다. 앞으로도 취향에 맞는 메뉴를 추천드릴 수 있도록 노력하겠습니다.', 'ACTIVE', '2026-06-12 11:25:00', '2026-06-12 11:25:00', '2026-06-12 11:25:00', '2026-06-12 11:25:00'),
(900020, 900027, '허니브레드가 따뜻하게 나와서 좋았습니다. 음료보다 디저트가 더 인상적이었어요.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"허니브레드가 따뜻하게 나와서 좋았습니다"}]}', 'COMPLETED', '2026-06-12 12:45:00', '2026-06-12 12:45:00', NULL, 'NONE', NULL, NULL, '2026-06-12 12:45:00', '2026-06-12 12:45:00'),
(900021, 900028, '말차라떼는 괜찮았는데 매장 테이블이 조금 지저분했습니다.', '{"overallSentiment":"NEGATIVE","riskLevel":"HIGH","categories":[{"category":"TASTE","sentiment":"POSITIVE","evidence":"말차라떼는 괜찮았는데"},{"category":"CLEANLINESS","sentiment":"NEGATIVE","evidence":"테이블이 조금 지저분했습니다"}]}', 'COMPLETED', '2026-06-12 13:19:00', '2026-06-12 13:19:00', NULL, 'NONE', NULL, NULL, '2026-06-12 13:19:00', '2026-06-12 13:19:00'),
(900022, 900030, '여러 메뉴를 한 번에 주문했는데 포장이 깔끔했고 누락 없이 잘 받았습니다.', '{"overallSentiment":"POSITIVE","riskLevel":"LOW","categories":[{"category":"SERVICE","sentiment":"POSITIVE","evidence":"포장이 깔끔했고 누락 없이 잘 받았습니다"}]}', 'COMPLETED', '2026-06-12 14:25:00', '2026-06-12 14:25:00', '여러 메뉴를 주문해주셔서 감사합니다. 앞으로도 정확하고 깔끔한 포장으로 준비하겠습니다.', 'ACTIVE', '2026-06-12 14:25:00', '2026-06-12 14:25:00', '2026-06-12 14:25:00', '2026-06-12 14:25:00');
-- 시스템 이벤트 테이블은 비워 둔다.
-- 이후 실제 메뉴·주문·리뷰 작업이 발생할 때 애플리케이션이 정상 이벤트를 생성한다.
COMMIT;
ALTER TABLE cafe_order AUTO_INCREMENT = 1021;
ALTER TABLE order_detail AUTO_INCREMENT = 2000;
ALTER TABLE outbox AUTO_INCREMENT = 1;
-- 최종 확인
SELECT COUNT(*) AS menu_count FROM menu;
SELECT COUNT(*) AS ingredient_count FROM ingredient;
SELECT COUNT(*) AS recipe_count FROM menu_recipe;
SELECT COUNT(*) AS current_stock_log_count FROM current_stock_log;
SELECT COUNT(*) AS historical_stock_log_count FROM historical_stock_log;
SELECT COUNT(*) AS order_count FROM cafe_order;
SELECT COUNT(*) AS order_detail_count FROM order_detail;
SELECT COUNT(*) AS review_read_count FROM review_read;
SELECT COUNT(*) AS outbox_count_should_be_zero FROM outbox;
SELECT COUNT(*) AS processed_event_preserved_count FROM processed_event;
SELECT review_id, order_id, analysis_status, reply_status FROM review_read ORDER BY review_id;
SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cafe_order';
-- 주문 합계와 상세 합계가 다르면 결과가 출력된다. 정상은 0행이다.
SELECT o.order_id, o.order_price, SUM(d.quantity * m.menu_price) AS calculated_price
FROM cafe_order o
JOIN order_detail d ON d.order_id = o.order_id
JOIN menu m ON m.menu_id = d.menu_id
GROUP BY o.order_id, o.order_price
HAVING o.order_price <> SUM(d.quantity * m.menu_price);
-- 답글 내용/상태가 다르면 결과가 출력된다. 정상은 0행이다.
SELECT review_id, reply_status, reply_content FROM review_read
WHERE (reply_content IS NOT NULL AND reply_status <> 'ACTIVE')
   OR (reply_content IS NULL AND reply_status <> 'NONE');
-- 메뉴 이미지는 화면 등록 전까지 전부 NULL이어야 정상이다.
SELECT COUNT(*) AS menu_count, SUM(CASE WHEN menu_image IS NULL THEN 1 ELSE 0 END) AS null_image_count FROM menu;
-- Kafka 안전성 확인: 이 스크립트는 processed_event를 수정하지 않는다.
