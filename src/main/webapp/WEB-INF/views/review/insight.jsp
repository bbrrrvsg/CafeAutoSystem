<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="리뷰 대시보드" scope="request" />
<c:set var="menu" value="review-insight" scope="request" />
<c:set var="breadcrumb" value="관리 / 리뷰 대시보드" scope="request" />
<jsp:include page="../layout/header.jsp" />
<link rel="stylesheet" href="/css/review-insight.css">

<!-- 페이지 헤더 -->
<div class="insight-page-head">
    <div>
        <h1>리뷰 대시보드</h1>
        <p>AI 분석을 통해 고객 반응과 운영 인사이트를 한눈에 확인하세요.</p>
    </div>
    <button type="button" class="date-btn">2026.05.23 ~ 2026.05.29 ▾</button>
</div>

<!-- 통계 카드 -->
<div class="insight-stats">
    <div class="insight-stat">
        <span>전체 리뷰 수</span>
        <strong>326건</strong>
        <p class="up">전주 대비 +12.4%</p>
    </div>
    <div class="insight-stat success">
        <span>긍정 비율</span>
        <strong>72%</strong>
        <p class="up">전주 대비 +4.3%p</p>
    </div>
    <div class="insight-stat danger">
        <span>부정 비율</span>
        <strong>14%</strong>
        <p class="down">전주 대비 -2.1%p</p>
    </div>
    <div class="insight-stat warning">
        <span>답글 대기</span>
        <strong>18건</strong>
        <p>전주 대비 +5건</p>
    </div>
    <div class="insight-stat gold">
        <span>이번 주 신규 리뷰</span>
        <strong>56건</strong>
        <p class="up">전주 대비 +18.2%</p>
    </div>
</div>

<!-- 대시보드 그리드 -->
<div class="insight-grid">
    <!-- 감정 분포 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>감정 분포</h2>
            <span>총 326건</span>
        </div>
        <div class="donut-wrap">
            <div class="donut"><span>326<br><small>리뷰</small></span></div>
            <div class="legend">
                <div><em class="green-dot"></em><span>긍정</span><strong>72% · 235건</strong></div>
                <div><em class="gray-dot"></em><span>중립</span><strong>14% · 46건</strong></div>
                <div><em class="gray-dot"></em><span>부정</span><strong>14% · 45건</strong></div>
            </div>
        </div>
    </div>

    <!-- 카테고리 TOP4 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>주요 카테고리 TOP 4</h2>
            <span>전체 기준</span>
        </div>
        <div class="bar-row"><span>맛</span><div><i style="width:84%"></i></div><strong>84%</strong></div>
        <div class="bar-row"><span>서비스</span><div><i style="width:69%"></i></div><strong>69%</strong></div>
        <div class="bar-row"><span>가격</span><div><i style="width:52%"></i></div><strong>52%</strong></div>
        <div class="bar-row"><span>청결</span><div><i style="width:46%"></i></div><strong>46%</strong></div>
    </div>

    <!-- 일별 감정 추이 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>일별 감정 추이</h2>
            <span>최근 7일</span>
        </div>
        <div class="line-chart">
            <svg viewBox="0 0 520 220" preserveAspectRatio="none">
                <line x1="0" y1="40"  x2="520" y2="40"></line>
                <line x1="0" y1="100" x2="520" y2="100"></line>
                <line x1="0" y1="160" x2="520" y2="160"></line>
                <polyline class="positive-line" points="20,78 95,68 170,58 245,74 320,66 395,62 500,50"></polyline>
                <polyline class="neutral-line"  points="20,148 95,150 170,154 245,156 320,150 395,153 500,148"></polyline>
                <polyline class="negative-line" points="20,164 95,170 170,178 245,158 320,170 395,174 500,183"></polyline>
            </svg>
            <div class="chart-days">
                <span>5/23</span><span>5/24</span><span>5/25</span>
                <span>5/26</span><span>5/27</span><span>5/28</span><span>5/29</span>
            </div>
        </div>
    </div>

    <!-- AI 운영 인사이트 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>AI 운영 인사이트</h2>
            <span>AI</span>
        </div>
        <ul class="insight-list">
            <li><strong>맛에 대한 긍정 언급이 가장 많아요.</strong><p>아메리카노와 크림라떼 만족도가 높게 나타났습니다.</p></li>
            <li><strong>서비스 응대 속도 관련 불만이 증가했어요.</strong><p>피크 시간대 대기시간 언급이 전주 대비 증가했습니다.</p></li>
            <li><strong>가격 만족도는 보통 수준입니다.</strong><p>가성비 관련 중립 의견이 많아 프로모션 검토가 필요합니다.</p></li>
            <li><strong>청결 칭찬이 꾸준합니다.</strong><p>매장 관리에 대한 긍정 반응이 지속되고 있습니다.</p></li>
        </ul>
    </div>

    <!-- 부정 리뷰 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>주의가 필요한 부정 리뷰</h2>
            <span>더보기</span>
        </div>
        <div class="negative-item"><p>음료가 너무 늦게 나왔어요. 20분 넘게 기다렸습니다.</p><em>서비스</em></div>
        <div class="negative-item"><p>라떼 맛이 예전보다 밍밍해졌어요. 원두가 바뀐 건가요?</p><em>맛</em></div>
        <div class="negative-item"><p>테이블이 끈적이고 청소가 아쉬웠어요.</p><em>청결</em></div>
        <div class="negative-item"><p>가격 대비 양이 적은 것 같아요.</p><em>가격</em></div>
    </div>

    <!-- 추천 액션 -->
    <div class="insight-card">
        <div class="insight-card-head">
            <h2>추천 액션</h2>
            <span>3개</span>
        </div>
        <div class="action-item">
            <strong>메뉴 개선</strong>
            <p>아메리카노, 라떼 레시피 및 원두 품질을 점검하세요.</p>
        </div>
        <div class="action-item">
            <strong>응대 강화</strong>
            <p>피크 시간대 인력 배치와 응대 매뉴얼을 확인하세요.</p>
        </div>
        <div class="action-item">
            <strong>재고 관리</strong>
            <p>품절 언급 메뉴의 재고를 우선적으로 보충하세요.</p>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
