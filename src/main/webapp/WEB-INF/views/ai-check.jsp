<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="AI 오발주 검사" scope="request" />
<c:set var="menu" value="ai-check" scope="request" />
<jsp:include page="layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">AI CHECK · 발주 안정성 검증</div>
        <h1>모든 항목이 <span class="accent">정상 범위</span>예요.</h1>
        <p class="hero-brief">
            AI가 제안한 발주량의 적정성을 검사합니다. 과거 발주 패턴 대비 이상치가 없는지 확인해요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">검사 시간</div>
        <div class="time" style="font-size:14px; font-weight:600;">2026-05-16 23:01</div>
    </div>
</section>

<!-- 검사 결과 박스 -->
<div class="check-box">
    <div>
        <div class="cb-status">검사 통과 (PASS)</div>
        <div class="cb-sub">AI가 제안한 발주량이 정상 범위입니다.</div>
    </div>
    <button class="btn btn-secondary btn-sm">다시 검사</button>
</div>

<!-- 검사 결과 테이블 -->
<div class="card flush">
    <div class="card-header">
        <div>
            <h3>품목별 검사 결과</h3>
            <div class="card-sub">4개 품목 · 모두 정상</div>
        </div>
    </div>
    <table class="data-table">
        <thead>
            <tr>
                <th>품목명</th>
                <th>AI 발주량</th>
                <th>검사 결과</th>
                <th>세부 내용</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>원두 (블렌드)</strong></td>
                <td class="num">5 kg</td>
                <td><span class="badge badge-success">정상 (PASS)</span></td>
                <td class="text-muted">과거 평균 대비 12% 증가 (정상 범위)</td>
            </tr>
            <tr>
                <td><strong>우유 (1L)</strong></td>
                <td class="num">20 팩</td>
                <td><span class="badge badge-success">정상 (PASS)</span></td>
                <td class="text-muted">과거 평균 대비 8% 증가 (정상 범위)</td>
            </tr>
            <tr>
                <td><strong>플라스틱 컵</strong></td>
                <td class="num">300 개</td>
                <td><span class="badge badge-success">정상 (PASS)</span></td>
                <td class="text-muted">과거 평균 대비 15% 증가 (정상 범위)</td>
            </tr>
            <tr>
                <td><strong>바닐라 시럽</strong></td>
                <td class="num">3 L</td>
                <td><span class="badge badge-success">정상 (PASS)</span></td>
                <td class="text-muted">과거 평균 대비 10% 증가 (정상 범위)</td>
            </tr>
        </tbody>
    </table>
</div>

<div class="ai-box" style="margin-top: 24px;">
    <div class="ai-label">AI 코멘트</div>
    <div class="ai-msg">
        모든 품목이 정상 범위로 판단되어 <strong>발주 대기 상태로 이동</strong>되었습니다.
        담당자 승인 후 거래처로 자동 발송됩니다.
    </div>
</div>

<jsp:include page="layout/footer.jsp" />
