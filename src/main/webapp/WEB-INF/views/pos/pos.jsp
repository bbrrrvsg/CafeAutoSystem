<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="POS 주문" scope="request" />
<c:set var="menu" value="pos" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero" style="padding-bottom: 18px;">
    <div class="hero-text">
        <div class="hero-meta">POS · 영업 중</div>
        <h1>주문을 받아볼까요?</h1>
        <p class="hero-brief">왼쪽에서 카테고리를 고르고 메뉴를 클릭하면 우측 장바구니에 담깁니다.</p>
    </div>
    <div class="hero-side">
        <div class="date">오늘 주문</div>
        <div class="time">142<span style="font-size:14px;color:var(--text-muted);">건</span></div>
    </div>
</section>

<div class="pos-layout">

    <!-- 카테고리 -->
    <div class="pos-categories">
        <button class="pos-cat-btn active">커피</button>
        <button class="pos-cat-btn">라떼</button>
        <button class="pos-cat-btn">에이드</button>
        <button class="pos-cat-btn">티</button>
        <button class="pos-cat-btn">디저트</button>
        <button class="pos-cat-btn">기타</button>
    </div>

    <!-- 제품 그리드 -->
    <div class="pos-products">
        <div class="product-grid">
            <div class="product-card" data-menu-id="1" data-price="3000">
                <div class="p-thumb"></div>
                <div class="p-name">아메리카노</div>
                <div class="p-price">3,000원</div>
            </div>
            <div class="product-card" data-menu-id="2" data-price="3800">
                <div class="p-thumb"></div>
                <div class="p-name">카페라떼</div>
                <div class="p-price">3,800원</div>
            </div>
            <div class="product-card" data-menu-id="3" data-price="4300">
                <div class="p-thumb"></div>
                <div class="p-name">바닐라라떼</div>
                <div class="p-price">4,300원</div>
            </div>
            <div class="product-card" data-menu-id="4" data-price="4300">
                <div class="p-thumb"></div>
                <div class="p-name">카페모카</div>
                <div class="p-price">4,300원</div>
            </div>
            <div class="product-card" data-menu-id="5" data-price="4000">
                <div class="p-thumb"></div>
                <div class="p-name">아메모카</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card" data-menu-id="6" data-price="4000">
                <div class="p-thumb"></div>
                <div class="p-name">카라멜 마키아또</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card" data-menu-id="7" data-price="4000">
                <div class="p-thumb"></div>
                <div class="p-name">초코라떼</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card" data-menu-id="8" data-price="4500">
                <div class="p-thumb"></div>
                <div class="p-name">딸기 에이드</div>
                <div class="p-price">4,500원</div>
            </div>
            <div class="product-card" data-menu-id="9" data-price="4500">
                <div class="p-thumb"></div>
                <div class="p-name">콜드브루</div>
                <div class="p-price">4,500원</div>
            </div>
        </div>
    </div>

    <!-- 장바구니 -->
    <div class="pos-cart">
        <div class="cart-head">
            <h3>주문 내역 <span style="color:var(--text-muted);font-size:12px;font-weight:500;">· 테이블 5번</span></h3>
        </div>
        <div class="cart-items" id="cartItems">
            <div style="text-align:center;color:#aaa;padding:32px 0;font-size:13px;">장바구니가 비어 있습니다.</div>
        </div>
        <div class="cart-foot">
            <div class="cart-total">
                <span class="label">총 금액</span>
                <span class="amount" id="cartTotal">0원</span>
            </div>
            <div class="cart-actions">
                <button class="btn btn-secondary" id="clearBtn">초기화</button>
                <button class="btn btn-primary"   id="payBtn">결제하기</button>
            </div>
        </div>
    </div>

</div>

<script>
    // 카테고리 토글
    document.querySelectorAll('.pos-cat-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.pos-cat-btn').forEach(function (b) { b.classList.remove('active'); });
            btn.classList.add('active');
        });
    });
</script>
<script src="${pageContext.request.contextPath}/js/pos.js"></script>

<jsp:include page="../layout/footer.jsp" />
