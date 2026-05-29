<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="재고 관리" scope="request" />
<c:set var="menu" value="inventory" scope="request" />
<jsp:include page="layout/header.jsp" />

<!-- Hero -->
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">INVENTORY · 실시간</div>
        <h1>재고 <span class="accent">6개 품목</span>이 부족해요.</h1>
        <p class="hero-brief">
            안전재고 미달 품목은 자동으로 AI 발주 초안에 반영됩니다.
            지금 발주하면 모레까지 입고 예정이에요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">총 32 품목 관리 중</div>
        <div class="time">86<span style="font-size:14px;color:var(--text-muted);">%</span></div>
    </div>
</section>

<!-- 미니 통계 -->
<div class="mini-stats">
    <div class="mini-stat">
        <div class="ms-label">전체 품목</div>
        <div class="ms-value">32<span class="unit">개</span></div>
    </div>
    <div class="mini-stat accent">
        <div class="ms-label">정상</div>
        <div class="ms-value">26<span class="unit">개</span></div>
    </div>
    <div class="mini-stat alert">
        <div class="ms-label">부족</div>
        <div class="ms-value">6<span class="unit">개</span></div>
    </div>
    <div class="mini-stat">
        <div class="ms-label">유통기한 임박</div>
        <div class="ms-value">3<span class="unit">개</span></div>
    </div>
</div>

<!-- 툴바: 필터 + 검색 + 액션 -->
<div class="toolbar">
    <div class="chip-group">
        <button class="chip active">전체 <span class="count">32</span></button>
        <button class="chip">정상 <span class="count">26</span></button>
        <button class="chip">부족 <span class="count">6</span></button>
        <button class="chip">유통기한 임박</button>
    </div>
    <div class="search-input">
        <input type="text" placeholder="품목명으로 검색...">
    </div>
    <div class="toolbar-spacer"></div>
    <button class="btn btn-secondary btn-sm">엑셀 다운로드</button>
    <button class="btn btn-primary btn-sm">+ 품목 추가</button>
</div>

<!-- 테이블 -->
<div class="card flush">
    <table class="data-table">
        <thead>
            <tr>
                <th style="width:24%;">품목명</th>
                <th>재고 현황</th>
                <th>단위</th>
                <th>상태</th>
                <th>유통기한</th>
                <th class="text-right" style="width:100px;"></th>
            </tr>
        </thead>
        <tbody>
            <!-- 부족 -->
            <tr>
                <td><strong>원두 (블렌드)</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill low" style="width:24%;"></div></div>
                        <span class="stock-text">1.2 / 5</span>
                    </div>
                </td>
                <td>kg</td>
                <td><span class="status low"><span class="dot"></span>부족</span></td>
                <td class="text-muted">—</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>우유 (1L)</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill low" style="width:15%;"></div></div>
                        <span class="stock-text">3 / 20</span>
                    </div>
                </td>
                <td>팩</td>
                <td><span class="status low"><span class="dot"></span>부족</span></td>
                <td class="text-warning">2026-06-02</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>플라스틱 컵</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill low" style="width:40%;"></div></div>
                        <span class="stock-text">40 / 100</span>
                    </div>
                </td>
                <td>개</td>
                <td><span class="status low"><span class="dot"></span>부족</span></td>
                <td class="text-muted">—</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>바닐라 시럽</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill low" style="width:15%;"></div></div>
                        <span class="stock-text">0.3 / 2</span>
                    </div>
                </td>
                <td>L</td>
                <td><span class="status low"><span class="dot"></span>부족</span></td>
                <td>2026-06-22</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <!-- 정상 -->
            <tr>
                <td><strong>초코 파우더</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill ok" style="width:85%;"></div></div>
                        <span class="stock-text">0.85 / 1</span>
                    </div>
                </td>
                <td>kg</td>
                <td><span class="status ok"><span class="dot"></span>정상</span></td>
                <td>2027-01-15</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td><strong>샷 시럽 (헤이즐넛)</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill ok" style="width:72%;"></div></div>
                        <span class="stock-text">1.4 / 2</span>
                    </div>
                </td>
                <td>L</td>
                <td><span class="status ok"><span class="dot"></span>정상</span></td>
                <td>2026-09-30</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
            <!-- 임박 (warn) -->
            <tr>
                <td><strong>빨대</strong></td>
                <td>
                    <div class="stock-cell">
                        <div class="stock-bar"><div class="fill warn" style="width:30%;"></div></div>
                        <span class="stock-text">30 / 100</span>
                    </div>
                </td>
                <td>개</td>
                <td><span class="status warn"><span class="dot"></span>주의</span></td>
                <td class="text-muted">—</td>
                <td class="text-right">
                    <div class="row-actions">
                        <button>수정</button>
                        <button class="danger">삭제</button>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
    <div class="pagination">
        <button>←</button>
        <button class="active">1</button>
        <button>2</button>
        <button>3</button>
        <span class="gap">…</span>
        <button>8</button>
        <button>→</button>
    </div>
</div>

<jsp:include page="layout/footer.jsp" />
