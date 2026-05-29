<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="AI 발주 관리" scope="request" />
<c:set var="menu" value="ai-order" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">AI ORDER · 자동 분석</div>
        <h1>내일은 <span class="accent">291,000원</span> 발주를 추천드려요.</h1>
        <p class="hero-brief">
            아이스 아메리카노 소요량이 약 40% 증가가 예상되어 원두 · 우유 등의 발주가 필요합니다.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">분석 기간</div>
        <div class="time" style="font-size:14px; font-weight:600;">5/10 ~ 5/16</div>
    </div>
</section>

<!-- 옵션 바 -->
<div class="toolbar">
    <div class="form-row" style="border:none; padding:0; grid-template-columns: auto auto auto auto; gap:12px; align-items:center;">
        <label style="font-size:12px; color:var(--text-muted); font-weight:600;">발주 예정일</label>
        <input type="date" value="2026-05-17" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        <label style="font-size:12px; color:var(--text-muted); font-weight:600; margin-left:12px;">분석 기간</label>
        <input type="text" value="2026-05-10 ~ 2026-05-16" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
    </div>
    <div class="toolbar-spacer"></div>
    <button class="btn btn-primary btn-sm">AI 재분석</button>
</div>

<!-- AI 분석 요약 -->
<div class="ai-box" style="margin-bottom: 24px;">
    <div class="ai-label">AI 분석 요약</div>
    <div class="ai-msg">
        내일(5/17) 아이스 아메리카노 소요량 <strong>40% 증가</strong>가 예상되어
        원두, 우유 등의 발주가 필요합니다. 권장 총 발주액은 <strong>291,000원</strong>입니다.
    </div>
</div>

<!-- 추천 발주 테이블 -->
<div class="card flush">
    <table class="data-table">
        <thead>
            <tr>
                <th>품목명</th>
                <th>예상 필요량</th>
                <th>현재고</th>
                <th>발주 제안량</th>
                <th>단가</th>
                <th class="text-right">예상 금액</th>
                <th>상태</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>원두 (블렌드)</strong></td>
                <td class="num">8 kg</td>
                <td class="num">1.2 kg</td>
                <td class="num font-bold">5 kg</td>
                <td class="num">32,000원</td>
                <td class="text-right num font-bold">160,000원</td>
                <td>
                    <span class="badge badge-info">AI 검토 중</span>
                    <div class="row-actions" style="margin-top:4px;">
                        <button>수정</button>
                        <button class="danger">제외</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>우유 (1L)</strong></td>
                <td class="num">40 팩</td>
                <td class="num">3 팩</td>
                <td class="num font-bold">20 팩</td>
                <td class="num">2,500원</td>
                <td class="text-right num font-bold">50,000원</td>
                <td>
                    <span class="badge badge-info">AI 검토 중</span>
                    <div class="row-actions" style="margin-top:4px;">
                        <button>수정</button>
                        <button class="danger">제외</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>플라스틱 컵</strong></td>
                <td class="num">500 개</td>
                <td class="num">40 개</td>
                <td class="num font-bold">300 개</td>
                <td class="num">150원</td>
                <td class="text-right num font-bold">45,000원</td>
                <td>
                    <span class="badge badge-info">AI 검토 중</span>
                    <div class="row-actions" style="margin-top:4px;">
                        <button>수정</button>
                        <button class="danger">제외</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>바닐라 시럽</strong></td>
                <td class="num">5 L</td>
                <td class="num">0.3 L</td>
                <td class="num font-bold">3 L</td>
                <td class="num">12,000원</td>
                <td class="text-right num font-bold">36,000원</td>
                <td>
                    <span class="badge badge-info">AI 검토 중</span>
                    <div class="row-actions" style="margin-top:4px;">
                        <button>수정</button>
                        <button class="danger">제외</button>
                    </div>
                </td>
            </tr>
            <tr style="background: #FAFAF9;">
                <td colspan="5" class="text-right" style="font-weight:600; color:var(--text-secondary);">총 예상 금액</td>
                <td class="text-right num" style="font-size:16px; font-weight:700; color:var(--primary);">291,000원</td>
                <td></td>
            </tr>
        </tbody>
    </table>
</div>

<div style="display:flex; gap:8px; margin-top:20px; justify-content:flex-end;">
    <button class="btn btn-secondary">초안 저장</button>
    <button class="btn btn-primary">승인 요청</button>
</div>

<jsp:include page="../layout/footer.jsp" />
