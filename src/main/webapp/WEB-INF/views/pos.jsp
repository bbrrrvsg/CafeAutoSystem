<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="POS 주문" scope="request" />
<c:set var="menu" value="pos" scope="request" />
<jsp:include page="layout/header.jsp" />

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
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">아메리카노</div>
                <div class="p-price">3,000원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">카페라떼</div>
                <div class="p-price">3,800원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">바닐라라떼</div>
                <div class="p-price">4,300원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">카페모카</div>
                <div class="p-price">4,300원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">아메모카</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">카라멜 마키아또</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">초코라떼</div>
                <div class="p-price">4,000원</div>
            </div>
            <div class="product-card">
                <div class="p-thumb"></div>
                <div class="p-name">딸기 에이드</div>
                <div class="p-price">4,500원</div>
            </div>
            <div class="product-card">
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
        <div class="cart-items">
            <div class="cart-item">
                <div>
                    <span class="ci-name">아이스 아메리카노</span>
                    <span class="ci-qty">× 1</span>
                </div>
                <span class="ci-price">3,000</span>
            </div>
            <div class="cart-item">
                <div>
                    <span class="ci-name">카페라떼</span>
                    <span class="ci-qty">× 1</span>
                </div>
                <span class="ci-price">3,800</span>
            </div>
            <div class="cart-item">
                <div>
                    <span class="ci-name">바닐라라떼</span>
                    <span class="ci-qty">× 1</span>
                </div>
                <span class="ci-price">4,300</span>
            </div>
        </div>
        <div class="cart-foot">
            <div class="cart-total">
                <span class="label">총 금액</span>
                <span class="amount">11,100원</span>
            </div>
            <div class="cart-actions">
                <button class="btn btn-secondary">초기화</button>
                <button class="btn btn-primary">결제하기</button>
            </div>
        </div>
    </div>

</div>

<script>
    // 카테고리 토글
    document.querySelectorAll('.pos-cat-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.pos-cat-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
        });
    });
</script>

<jsp:include page="layout/footer.jsp" />
