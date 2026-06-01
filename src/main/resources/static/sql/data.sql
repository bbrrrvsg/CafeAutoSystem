-- ========================================================
-- 1. VENDOR (거래처 마스터) 초기 데이터
-- ========================================================
INSERT INTO VENDOR (vendor_name, manager_email, manager_phone, created_at, updated_at)
VALUES
    ('매일유통 대리점', 'maeil_milk@gmail.com', '010-9876-5432', NOW(), NOW()),
    ('대박부자재마트', 'daebak_pack@daum.net', '02-111-2222', NOW(), NOW()),
    ('서울원두유통', 'seoul_bean@naver.com', '010-1234-5678', NOW(), NOW());


-- ========================================================
-- 2. HISTORICAL_STOCK_LOG (과거 4주간 요일별 시계열 및 장애/폐기 로그)
-- * 4번(우유), 1번(원두)
-- * STOCK_IN(입고), STOCK_OUT(판매), STOCK_DISCARD(폐기), AI_VALIDATION(AI검사), RPA_RETRY(RPA장애)
-- ========================================================

-- [4주 전: 5월 1일 ~ 5월 7일] 정기 운영 및 우유 폐기 발생 사태
INSERT INTO HISTORICAL_STOCK_LOG (ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                                        (4, NULL, 'STOCK_IN', '[입고] 신선한 우유 15팩 입고 완료', 15000, '정기 발주 입고', 'admin', '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '[정산] 금요일 라떼 판매 소모', -4400, '금요 피크타임 소모', 'SYSTEM', '2026-05-01 23:00:00', '2026-05-01 23:00:00'),
                                                                                                                                        (1, NULL, 'STOCK_OUT', '[정산] 아메리카노 및 라떼 원두 소모', -1200, '금요 피크타임 소모', 'SYSTEM', '2026-05-01 23:00:00', '2026-05-01 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '[정산] 토요일 라떼 판매 소모', -5200, '주말 매출 상승', 'SYSTEM', '2026-05-02 23:00:00', '2026-05-02 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '[정산] 일요일 라떼 판매 소모', -4800, '주말 매출 상승', 'SYSTEM', '2026-05-03 23:00:00', '2026-05-03 23:00:00'),
-- 5월 4일 야간 마감 배치 가동: 유통기한 임박 우유 1000ml(1팩) 자동 폐기 처리 시나리오
                                                                                                                                        (4, NULL, 'STOCK_DISCARD', '[자동폐기] 유통기한 경과 우유 1팩 폐기 처리', -1000, '유통기한 초과 마감 배치', 'SYSTEM', '2026-05-04 23:05:00', '2026-05-04 23:05:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '월요일 라떼 판매 소모', -3000, '평일 기본 소모', 'SYSTEM', '2026-05-04 23:00:00', '2026-05-04 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '화요일 라떼 판매 소모', -3200, '평일 기본 소모', 'SYSTEM', '2026-05-05 23:00:00', '2026-05-05 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '수요일 라떼 판매 소모', -3100, '평일 기본 소모', 'SYSTEM', '2026-05-06 23:00:00', '2026-05-06 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '목요일 라떼 판매 소모', -3500, '평일 기본 소모', 'SYSTEM', '2026-05-07 23:00:00', '2026-05-07 23:00:00');

-- [3주 전: 5월 8일 ~ 5월 14일] RPA 네트워크 메일 다운 및 재시도(Retry) 장애 사태
INSERT INTO HISTORICAL_STOCK_LOG (ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                                        (4, NULL, 'STOCK_OUT', '금요일 라떼 판매 소모', -5000, '금요 피크 소모', 'SYSTEM', '2026-05-08 23:00:00', '2026-05-08 23:00:00'),
                                                                                                                                        (1, NULL, 'STOCK_OUT', '금요일 원두 소모', -1350, '금요 피크 소모', 'SYSTEM', '2026-05-08 23:00:00', '2026-05-08 23:00:00'),
-- 5월 9일 아침: 서울원두유통 메일 서버 다운으로 인한 RPA 발송 장애 및 롤백/재시도 로그 시나리오
                                                                                                                                        (1, NULL, 'RPA_RETRY', '[발주실패] 서울원두유통 메일 전송 실패 (Retry 1회차 대기)', 0, 'Mail 서버 Connection Timeout', 'SYSTEM', '2026-05-09 08:31:00', '2026-05-09 08:31:00'),
                                                                                                                                        (1, NULL, 'RPA_RETRY', '[장애복구] RPA 메일 재전송 성공 완료', 0, '재시도 1회차 성공', 'SYSTEM', '2026-05-09 08:35:00', '2026-05-09 08:35:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '토요일 라떼 판매 소모', -5500, '주말 매출 상승', 'SYSTEM', '2026-05-09 23:00:00', '2026-05-09 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '일요일 라떼 판매 소모', -4900, '주말 매출 상승', 'SYSTEM', '2026-05-10 23:00:00', '2026-05-10 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '월요일 라떼 판매 소모', -2800, '평일 기본 소모', 'SYSTEM', '2026-05-11 23:00:00', '2026-05-11 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '화요일 라떼 판매 소모', -3300, '평일 기본 소모', 'SYSTEM', '2026-05-12 23:00:00', '2026-05-12 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '수요일 라떼 판매 소모', -3000, '평일 기본 소모', 'SYSTEM', '2026-05-13 23:00:00', '2026-05-13 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '목요일 라떼 판매 소모', -3400, '평일 기본 소모', 'SYSTEM', '2026-05-14 23:00:00', '2026-05-14 23:00:00');

-- [2주 전: 5월 15일 ~ 5월 21일] AI 오발주 시스템 검증 및 동결 차단 사태
INSERT INTO HISTORICAL_STOCK_LOG (ingredient_id, order_item_id, log_type, message, amount, reason, user_id, created_at, updated_at) VALUES
                                                                                                                                        (4, NULL, 'STOCK_OUT', '금요일 라떼 판매 소모', -5000, '금요 피크 소모', 'SYSTEM', '2026-05-15 23:00:00', '2026-05-15 23:00:00'),
                                                                                                                                        (1, NULL, 'STOCK_OUT', '금요일 원두 소모', -1350, '금요 피크 소모', 'SYSTEM', '2026-05-15 23:00:00', '2026-05-15 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '토요일 라떼 판매 소모', -5100, '주말 매출 상승', 'SYSTEM', '2026-05-16 23:00:00', '2026-05-16 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '일요일 라떼 판매 소모', -4700, '주말 매출 상승', 'SYSTEM', '2026-05-17 23:00:00', '2026-05-17 23:00:00'),
-- 5월 18일 야간: AI 알고리즘이 우유 100팩 오발주 예측 감지하여 차단하고 시스템 동결시킨 시나리오
                                                                                                                                        (4, NULL, 'AI_VALIDATION', '[AI오발주차단] 우유 100팩 이상치 발주 감지 (+400% 폭증)', 0, '과거 평균 대비 오차 허용치 초과', 'SYSTEM', '2026-05-18 23:01:00', '2026-05-18 23:01:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '월요일 라떼 판매 소모', -3200, '평일 기본 소모', 'SYSTEM', '2026-05-18 23:00:00', '2026-05-18 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '화요일 라떼 판매 소모', -3100, '평일 기본 소모', 'SYSTEM', '2026-05-19 23:00:00', '2026-05-19 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '수요일 라떼 판매 소모', -3300, '평일 기본 소모', 'SYSTEM', '2026-05-20 23:00:00', '2026-05-20 23:00:00'),
                                                                                                                                        (4, NULL, 'STOCK_OUT', '목요일 라떼 판매 소모', -3600, '평일 기본 소모', 'SYSTEM', '2026-05-21 23:00:00', '2026-05-21 23:00:00');

-- ===================================================================================
-- 🥛 4번 자재(서울우유 1000ml)에 대한 최근 4주간 요일별(월요일 타겟) 소모/폐기 통계 데이터
-- ===================================================================================

-- [1주 전 월요일] 2026-05-25 : 소모 18개, 폐기 2개 (총 20개 소모)
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_CONSUME', 18, 'DAILY_REGULAR', '정상 영업 소모', 'admin', '2026-05-25 22:00:00', '2026-05-25 22:00:00');

INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_DISCARD', 2, 'EXPIRED', '유통기한 임박 폐기', 'admin', '2026-05-25 22:30:00', '2026-05-25 22:30:00');


-- [2주 전 월요일] 2026-05-18 : 소모 22개, 폐기 1개 (총 23개 소모)
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_CONSUME', 22, 'DAILY_REGULAR', '정상 영업 소모', 'admin', '2026-05-18 22:00:00', '2026-05-18 22:00:00');

INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_DISCARD', 1, 'DAMAGED', '용기 파손으로 인한 폐기', 'admin', '2026-05-18 22:30:00', '2026-05-18 22:30:00');


-- [3주 전 월요일] 2026-05-11 : 소모 15개, 폐기 0개 (총 15개 소모)
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_CONSUME', 15, 'DAILY_REGULAR', '정상 영업 소모', 'admin', '2026-05-11 22:00:00', '2026-05-11 22:00:00');


-- [4주 전 월요일] 2026-05-04 : 소모 25개, 폐기 3개 (총 28개 소모)
INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_CONSUME', 25, 'DAILY_REGULAR', '비오는 날 단체 주문 소모', 'admin', '2026-05-04 22:00:00', '2026-05-04 22:00:00');

INSERT INTO historical_stock_log (ingredient_id, log_type, amount, reason, message, user_id, created_at, updated_at)
VALUES (4, 'STOCK_DISCARD', 3, 'EXPIRED', '복귀 주말 재고 남음 폐기', 'admin', '2026-05-04 22:30:00', '2026-05-04 22:30:00');

-- ========================================================
-- 3. PURCHASE_ORDER (1차 점검용 오늘자 AI 발주 제안 초안)
-- ========================================================
INSERT INTO PURCHASE_ORDER (order_date_key, vendor_ingredient_id, suggested_qty, final_qty, status, expiration_date, created_at, updated_at)
VALUES
    ('PO-20260529', 1, 15, 15, 'PENDING', NULL, NOW(), NOW()),
    ('PO-20260529', 3, 5000, 5000, 'PENDING', NULL, NOW(), NOW());