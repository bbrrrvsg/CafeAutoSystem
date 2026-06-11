<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="AI 발주 관리" scope="request" />
<c:set var="menu" value="ai-order" scope="request" />
<jsp:include page="../layout/header.jsp" />

<%-- AI 에러 락 오버레이 및 경고 바 --%>
<c:if test="${aiStatus eq 'AI_ERROR'}">
    <div style="background-color: #fff5f5; border-left: 4px solid #e74a3b; padding: 16px; margin-bottom: 24px; border-radius: 8px;">
        <strong style="color: #e74a3b; font-size: 15px;">AI 시스템 안전 동결</strong>
        <p style="margin: 6px 0 0 0; font-size: 13px; color: #555;">
            최근 4주 누적 장부 분석 중 이상치가 감지되어 시스템 보호를 위해 <strong>[안전 기본 재고량]</strong>으로 제안 수량이 동결되었습니다.
        </p>
    </div>
</c:if>

<%-- 대시보드 상단 히어로 섹션 --%>
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">AI ORDER · 실시간 시계열 분석</div>
        <h1>내일자 추천 발주 금액은 <span class="accent">${totalOrderPrice}원</span>입니다.</h1>
        <p class="hero-brief">
            PyTorch 모델 분석 결과, 주말 매출 트렌드 반영 및 안전재고 기준치 미달 자재에 대한 추천 자동 연산이 완료되었습니다.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">예측 스냅샷</div>
        <div class="time" id="predictSnapshot" style="font-size:14px; font-weight:600;">-</div>
    </div>
</section>

<%-- 상단 툴바 조작 영역 --%>
<div class="toolbar">
    <div class="form-row" style="border:none; padding:0; grid-template-columns: auto auto auto auto; gap:12px; align-items:center;">
        <label style="font-size:12px; color:var(--text-muted); font-weight:600;">발주 예정일</label>
        <input type="date" id="orderTargetDate" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">

        <label style="font-size:12px; color:var(--text-muted); font-weight:600; margin-left:12px;">학습 로그 범위</label>
        <input type="text" id="learningLogRange" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px; width: 260px;" readonly>
    </div>
    <div class="toolbar-spacer"></div>
    <button id="btn-ai-reanalyze" class="btn btn-primary btn-sm">AI 재분석</button>
</div>

<c:choose>
    <%--  데이터가 없을 때 (EMPTY) --%>
    <c:when test="${aiStatus eq 'EMPTY'}">
        <div class="card" style="padding: 80px 40px; text-align: center; border: 1px dashed var(--border); border-radius: 12px; background: #fafafa; margin-top: 20px;">
            <div style="font-size: 54px; margin-bottom: 20px; filter: grayscale(0.2);">📥</div>
            <strong style="font-size: 18px; color: var(--text-primary); display: block; margin-bottom: 8px;">
                조회 가능한 AI 추천 발주 데이터가 없습니다.
            </strong>
            <p style="font-size: 13px; color: var(--text-muted); margin: 0; line-height: 1.6;">
                매일 오후 10시 마감 배치가 가동되거나, 백그라운드 정기 가중치 학습이 완수되어야 장부가 활성화됩니다.<br>
                새로운 예측 스냅샷을 생성하려면 우측 상단의 <strong>[AI 재분석]</strong> 버튼을 가동해 주세요.
            </p>
        </div>
    </c:when>

    <%-- 데이터가 정상 존재할 때 --%>
    <c:otherwise>
        <div class="card flush" style="margin-top: 20px;">
            <table class="data-table">
                <thead>
                <tr>
                    <th>품목명</th>
                    <th>예상 필요량</th>
                    <th>현재고</th>
                    <th>안전재고</th>
                    <th>발주 제안량</th>
                    <th>계약 단가</th>
                    <th style="text-align: right; padding-right: 20px;">예상 금액</th>
                    <th>상태</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${orderList}">
                    <tr class="ai-order-row" data-ingredient-id="${item.ingredientId}" data-suggested-qty="${item.orderQty}">
                        <td><strong>${item.ingredientName}</strong></td>
                        <td class="num">
                            <fmt:formatNumber value="${item.predictedRequiredQty}" type="number"/> ${item.ingredientUnit}
                        </td>
                        <td class="num">
                            <fmt:formatNumber value="${item.currentStock}" type="number"/> ${item.ingredientUnit}
                        </td>
                        <td class="num" style="color: var(--text-muted);">
                            <fmt:formatNumber value="${item.safetyStock}" type="number"/> ${item.ingredientUnit}
                        </td>
                        <td class="num font-bold" style="color: var(--primary);">
                            <fmt:formatNumber value="${item.orderQty}" type="number"/> ${item.ingredientUnit}
                        </td>
                        <td class="num">${item.unitPrice}원</td>

                        <td class="num font-bold" style="text-align: right; padding-right: 20px;">
                            <fmt:formatNumber value="${item.totalPrice}" type="number"/>원
                        </td>
                        <td>
                            <span class="badge badge-info">AI 검토 완료</span>
                            <div class="row-actions" style="margin-top:4px;">
                                <button>수정</button>
                                <button class="danger">제외</button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>

                    <%-- 합계 로우 --%>
                <tr style="background: #FAFAF9;">
                    <td colspan="6" class="text-right" style="font-weight:600; color:var(--text-secondary);">총 예상 발주 금액</td>
                    <td class="text-right num font-bold" style="text-align: right; padding-right: 20px; font-size:16px; color:var(--primary);">${totalOrderPrice}원</td>
                    <td></td>
                </tr>
                </tbody>
            </table>
        </div>

        <%-- 하단 마스터 액션 버튼 영역 --%>
        <div style="display:flex; gap:8px; margin-top:20px; justify-content:flex-end;">
            <button class="btn btn-secondary">초안 저장</button>
            <button id="btn-submit-bulk-order" class="btn btn-primary">승인 및 발주생성</button>
        </div>
    </c:otherwise>
</c:choose>

<script src="<c:url value='/js/purchase.js'/>"></script>

<jsp:include page="../layout/footer.jsp" />