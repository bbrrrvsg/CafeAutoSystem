<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="대시보드" scope="request" />
<c:set var="menu" value="dashboard" scope="request" />
<jsp:include page="../layout/header.jsp" />

<!-- ===== Hero ===== -->
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">2026.05.29 · FRIDAY · 영업 중</div>
        <h1>오늘은 어제보다 <span class="accent">15만원</span> 더 벌었어요.</h1>
        <p class="hero-brief">
            아이스 음료 수요가 평소 대비 40% 증가했습니다.
            원두와 우유 재고가 임계점 이하라 내일 발주가 필요해요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">2026 · 05 · 29</div>
        <div class="time" id="liveClock">14:32</div>
    </div>
</section>

<!-- ===== Bento Grid ===== -->
<div class="bento-grid">

    <!-- 1) 오늘 매출 (크게, 그래프 포함) -->
    <div class="bento cream col-3 row-2">
        <div class="tile-label">오늘 매출</div>
        <div class="tile-value">1,254,000<span class="unit">원</span></div>
        <div class="tile-delta">↗ 전일 대비 +12.5%</div>
        <div class="tile-chart">
            <canvas id="salesSpark"></canvas>
        </div>
    </div>

    <!-- 2) 재고 부족 (피치) -->
    <div class="bento peach col-3">
        <h4>재고 부족 · 즉시 발주 필요</h4>
        <div class="mini-list" id="dashLowList">
            <div class="mini-list-item text-muted"><span class="name">불러오는 중...</span></div>
        </div>
    </div>

    <!-- 3) 발주 대기 (라벤더) -->
    <div class="bento lavender col-3">
        <div class="tile-label">발주 대기</div>
        <div class="tile-value"><span id="dashPending">0</span><span class="unit">건</span></div>
        <div class="tile-foot">
            <span class="pill"><span class="dot"></span>승인 필요</span>
            <span>→</span>
        </div>
    </div>

    <!-- 4) AI 발주 초안 (다크 강조) -->
    <div class="bento charcoal col-2 row-2">
        <div class="tile-label" style="opacity:0.6;">AI 발주 추천</div>
        <div class="tile-value">2<span class="unit">건</span></div>
        <p style="font-size:12px; line-height:1.6; opacity:0.7; margin-top:12px;">
            과거 7일 판매 패턴과 재고 상태를 분석해 작성된 초안입니다.
        </p>
        <div class="tile-foot" style="border-top:1px solid rgba(255,255,255,0.1); padding-top:12px;">
            <span>검토하러 가기</span>
            <span>→</span>
        </div>
    </div>

    <!-- 5) 인기 메뉴 TOP 3 (화이트) -->
    <div class="bento white col-2">
        <h4>오늘 인기 메뉴</h4>
        <div class="rank-list">
            <div class="rank-item">
                <span class="rank-num">1</span>
                <span class="rank-name">아이스 아메리카노</span>
                <span class="rank-val">87잔</span>
            </div>
            <div class="rank-item">
                <span class="rank-num">2</span>
                <span class="rank-name">바닐라 라떼</span>
                <span class="rank-val">54잔</span>
            </div>
            <div class="rank-item">
                <span class="rank-num">3</span>
                <span class="rank-name">카페모카</span>
                <span class="rank-val">38잔</span>
            </div>
        </div>
    </div>

    <!-- 6) 다음 발주일 (버터) -->
    <div class="bento butter col-2">
        <div class="tile-label">다음 정기 발주일</div>
        <div class="tile-value" style="font-size:32px;">D-3</div>
        <div class="tile-foot">
            <span>6월 1일 · 서울원두유통</span>
        </div>
    </div>

    <!-- 7) 퀵 액션 (민트, 크게) -->
    <div class="bento mint col-4">
        <h4>빠른 작업</h4>
        <div class="quick-actions">
            <a href="/pos">POS 주문</a>
            <a href="/order">발주 작성</a>
            <a href="/inventory">재고 입력</a>
            <a href="/vendor">거래처 등록</a>
        </div>
    </div>

    <!-- 8) 이번 달 요약 (커피 그라데이션) -->
    <div class="bento coffee col-2">
        <div class="tile-label" style="opacity:0.7;">이번 달 누적</div>
        <div class="tile-value">28.4M<span class="unit">원</span></div>
        <div class="tile-delta" style="opacity:0.85;">목표 32M · 88%</div>
        <div class="tile-foot" style="opacity:0.7;">
            <span>지난달 26.1M</span>
            <span>+9%</span>
        </div>
    </div>

</div>

<!-- ===== JS ===== -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    // 라이브 시계
    (function tickClock() {
        const el = document.getElementById('liveClock');
        if (!el) return;
        const now = new Date();
        el.textContent = String(now.getHours()).padStart(2, '0') + ':' + String(now.getMinutes()).padStart(2, '0');
        setTimeout(tickClock, 30000);
    })();

    // 매출 스파크라인
    const ctx = document.getElementById('salesSpark');
    const grad = ctx.getContext('2d').createLinearGradient(0, 0, 0, 160);
    grad.addColorStop(0, 'rgba(107,66,38,0.35)');
    grad.addColorStop(1, 'rgba(107,66,38,0)');

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['월','화','수','목','금','토','일'],
            datasets: [{
                data: [950, 1100, 980, 1300, 1450, 1200, 1254],
                borderColor: '#6B4226',
                backgroundColor: grad,
                borderWidth: 2.5,
                tension: 0.45,
                fill: true,
                pointRadius: 0,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: '#6B4226'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#1A1A1A',
                    padding: 10,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: { label: c => (c.parsed.y * 1000).toLocaleString() + '원' }
                }
            },
            scales: {
                y: { display: false },
                x: { display: false }
            },
            interaction: { intersect: false, mode: 'index' }
        }
    });
</script>

<!-- 실데이터 연동: 재고 부족 / 발주 대기 (매출·AI·인기메뉴 타일은 POS/AI 영역이라 목업 유지) -->
<script>
(async function dashLive(){
    try{
        const inv = await (await fetch('/api/inventory')).json();
        const low = inv.filter(d => d.status === 'LOW');
        const el = document.getElementById('dashLowList');
        if (el) el.innerHTML = low.length
            ? low.slice(0,5).map(d => '<div class="mini-list-item"><span class="name">'+d.ingredientName+'</span><span class="val">'+d.currentStock.toLocaleString()+'/'+d.safetyStock.toLocaleString()+d.unit+'</span></div>').join('')
            : '<div class="mini-list-item text-muted"><span class="name">부족 품목 없음</span></div>';
    } catch(e) {}
    try {
        const pend = await (await fetch('/api/order/pending')).json();
        const p = document.getElementById('dashPending'); if (p) p.textContent = pend.length;
    } catch(e) {}
})();
</script>

<jsp:include page="../layout/footer.jsp" />
