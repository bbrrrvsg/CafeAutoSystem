<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 이력" scope="request" />
<c:set var="menu" value="order-history" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">HISTORY · 이번 달</div>
        <h1>이번 달 <span class="accent">18건</span>의 발주가 진행됐어요.</h1>
        <p class="hero-brief">총 발주 금액 3,240,000원 · 평균 발주 주기 1.6일</p>
    </div>
    <div class="hero-side">
        <div class="date">이번 달 누적</div>
        <div class="time">3.24<span style="font-size:14px;color:var(--text-muted);">M</span></div>
    </div>
</section>

<!-- 미니 통계 -->
<div class="mini-stats">
    <div class="mini-stat"><div class="ms-label">총 발주 건수</div><div class="ms-value">18<span class="unit">건</span></div></div>
    <div class="mini-stat accent"><div class="ms-label">완료</div><div class="ms-value">14<span class="unit">건</span></div></div>
    <div class="mini-stat"><div class="ms-label">진행 중</div><div class="ms-value">3<span class="unit">건</span></div></div>
    <div class="mini-stat alert"><div class="ms-label">반려/취소</div><div class="ms-value">1<span class="unit">건</span></div></div>
</div>

<!-- 툴바 -->
<div class="toolbar">
    <div class="chip-group">
        <button class="chip active">전체</button>
        <button class="chip">완료</button>
        <button class="chip">진행 중</button>
        <button class="chip">반려/취소</button>
    </div>
    <div class="search-input">
        <input type="text" placeholder="발주번호, 거래처 검색...">
    </div>
    <div class="toolbar-spacer"></div>
    <input type="date" value="2026-05-01" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
    <span style="color:var(--text-muted);">~</span>
    <input type="date" value="2026-05-29" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
</div>

<div class="card flush">
    <table class="data-table">
        <thead>
            <tr>
                <th>발주번호</th>
                <th>발주일</th>
                <th>거래처</th>
                <th>품목</th>
                <th class="text-right">금액</th>
                <th>유형</th>
                <th>상태</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>PO-20260516-006</strong></td>
                <td class="num">2026-05-16</td>
                <td>서울원두유통</td>
                <td class="num">4 품목</td>
                <td class="text-right num font-bold">210,000원</td>
                <td><span class="badge badge-info">AI</span></td>
                <td><span class="status ok"><span class="dot"></span>완료</span></td>
            </tr>
            <tr>
                <td><strong>PO-20260512-005</strong></td>
                <td class="num">2026-05-12</td>
                <td>일회용품 마트</td>
                <td class="num">3 품목</td>
                <td class="text-right num font-bold">198,000원</td>
                <td><span class="badge badge-warning">수동</span></td>
                <td><span class="status ok"><span class="dot"></span>완료</span></td>
            </tr>
            <tr>
                <td><strong>PO-20260508-004</strong></td>
                <td class="num">2026-05-08</td>
                <td>커피빈코리아</td>
                <td class="num">2 품목</td>
                <td class="text-right num font-bold">192,000원</td>
                <td><span class="badge badge-info">AI</span></td>
                <td><span class="status ok"><span class="dot"></span>완료</span></td>
            </tr>
            <tr>
                <td><strong>PO-20260505-003</strong></td>
                <td class="num">2026-05-05</td>
                <td>삼양식품</td>
                <td class="num">5 품목</td>
                <td class="text-right num font-bold">324,000원</td>
                <td><span class="badge badge-info">AI</span></td>
                <td><span class="status warn"><span class="dot"></span>배송 중</span></td>
            </tr>
            <tr>
                <td><strong>PO-20260502-002</strong></td>
                <td class="num">2026-05-02</td>
                <td>한빛 주방용품</td>
                <td class="num">2 품목</td>
                <td class="text-right num font-bold">86,000원</td>
                <td><span class="badge badge-warning">수동</span></td>
                <td><span class="status off"><span class="dot"></span>반려</span></td>
            </tr>
        </tbody>
    </table>
    <div class="pagination">
        <button>←</button>
        <button class="active">1</button>
        <button>2</button>
        <button>3</button>
        <button>→</button>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
