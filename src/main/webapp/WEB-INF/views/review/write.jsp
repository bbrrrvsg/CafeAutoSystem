<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>리뷰 작성 | Smart Cafe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review-write.css">
</head>
<body>
<main class="mobile-stage">
    <section class="phone-screen">
        <header class="app-topbar">
            <button type="button" class="back-btn" onclick="history.back()">‹</button>
            <div class="brand">
                <div class="brand-icon">☕</div>
                <div>
                    <strong>Smart Cafe</strong>
                    <span>CafeOS</span>
                </div>
            </div>
            <button type="button" class="bell-btn">⌕</button>
        </header>

        <section class="hero">
            <div class="hero-icon">💬</div>
            <h1>주문은 어떠셨나요?</h1>
            <p>남겨주신 리뷰는 해당 주문에 자동으로 저장됩니다.</p>
        </section>

        <section class="order-card">
            <div class="order-row">
                <div class="row-icon">#</div>
                <div class="row-copy">
                    <span>주문번호</span>
                    <strong id="orderNumber">-</strong>
                </div>
            </div>
            <div class="order-row">
                <div class="row-icon">⌚</div>
                <div class="row-copy">
                    <span>주문일시</span>
                    <strong id="orderDate">-</strong>
                </div>
            </div>
            <div class="order-row">
                <div class="row-icon">☕</div>
                <div class="row-copy">
                    <span>주문 메뉴</span>
                    <strong id="orderMenus">-</strong>
                </div>
            </div>
            <div class="order-row total">
                <div class="row-icon">₩</div>
                <div class="row-copy">
                    <span>총 결제금액</span>
                    <strong id="orderPrice">-</strong>
                </div>
            </div>
        </section>

        <!-- 작성 폼 (기본 표시) -->
        <section class="review-card" id="writeSection">
            <div class="section-title">
                <h2>리뷰 내용</h2>
                <span id="charCount">0 / 500</span>
            </div>
            <textarea id="reviewContent" maxlength="500"
                      placeholder="카페 이용 경험을 자유롭게 남겨주세요.&#10;맛, 서비스, 분위기 등 어떤 내용도 좋아요!"></textarea>
            <div class="notice">
                <span>i</span>
                주문 건당 리뷰는 1회만 작성할 수 있습니다.
            </div>
            <div id="resultMsg" class="result-msg"></div>
            <button type="button" class="submit-btn" id="submitBtn">리뷰 등록</button>
            <button type="button" class="later-btn" onclick="history.back()">나중에 작성</button>
        </section>

        <!-- 등록 완료 화면 (숨김 → 성공 후 표시) -->
        <section class="review-card done-section" id="doneSection">
            <div class="done-header">
                <div class="done-icon">✅</div>
                <h2>리뷰가 등록되었습니다!</h2>
                <p>소중한 의견 감사드립니다.</p>
            </div>
            <div class="done-content-box">
                <p class="done-content-label">내가 작성한 리뷰</p>
                <p id="doneContent" class="done-content-text"></p>
            </div>
            <button type="button" class="later-btn done-close-btn" onclick="window.close()">창 닫기</button>
        </section>

        <div class="home-indicator"></div>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/review-write.js"></script>
</body>
</html>
