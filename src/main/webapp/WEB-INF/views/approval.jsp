<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 승인" scope="request" />
<c:set var="menu" value="approval" scope="request" />
<jsp:include page="layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">APPROVAL · 승인 대기</div>
        <h1><span class="accent">2건</span>의 발주가 승인을 기다려요.</h1>
        <p class="hero-brief">
            AI 검토를 통과한 발주서 또는 수동 작성된 발주서를 검토하고 승인하세요.
        </p>
    </div>
</section>

<!-- 탭 -->
<div class="tabs">
    <div class="tab active">승인 대기 <span class="tab-count">2</span></div>
    <div class="tab">승인 완료 <span class="tab-count">8</span></div>
    <div class="tab">반려 <span class="tab-count">1</span></div>
</div>

<!-- 발주 카드 리스트 -->
<div class="card flush">
    <table class="data-table">
        <thead>
            <tr>
                <th>발주번호</th>
                <th>발주일</th>
                <th>거래처</th>
                <th>품목 수</th>
                <th class="text-right">총 금액</th>
                <th>유형</th>
                <th class="text-right" style="width:200px;">작업</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>PO-20260516-001</strong></td>
                <td class="num">2026-05-16</td>
                <td>서울원두유통</td>
                <td class="num">4</td>
                <td class="text-right num font-bold">291,000원</td>
                <td><span class="badge badge-info">AI 검토</span></td>
                <td class="text-right">
                    <button class="btn btn-secondary btn-sm" onclick="openOrderModal()">상세보기</button>
                    <button class="btn btn-success btn-sm">승인</button>
                    <button class="btn btn-danger btn-sm">반려</button>
                </td>
            </tr>
            <tr>
                <td><strong>PO-20260516-002</strong></td>
                <td class="num">2026-05-16</td>
                <td>일회용품 마트</td>
                <td class="num">3</td>
                <td class="text-right num font-bold">125,000원</td>
                <td><span class="badge badge-warning">수동 검토</span></td>
                <td class="text-right">
                    <button class="btn btn-secondary btn-sm" onclick="openOrderModal()">상세보기</button>
                    <button class="btn btn-success btn-sm">승인</button>
                    <button class="btn btn-danger btn-sm">반려</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<!-- 모달: 발주서 상세 -->
<div class="modal-overlay" id="orderModal">
    <div class="modal">
        <div class="modal-header">
            <h3>발주서 상세 정보</h3>
            <button class="panel-close" onclick="closeOrderModal()">×</button>
        </div>
        <div class="modal-body">
            <div class="detail-list">
                <div class="detail-row"><span class="key">발주번호</span><span class="val num">PO-20260516-001</span></div>
                <div class="detail-row"><span class="key">발주일</span><span class="val num">2026-05-16</span></div>
                <div class="detail-row"><span class="key">예상 배송일</span><span class="val num">2026-05-18</span></div>
                <div class="detail-row"><span class="key">공급처</span><span class="val">서울원두유통 외 2곳</span></div>
            </div>

            <h5 style="font-size:12px; font-weight:700; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.4px; margin: 20px 0 12px;">발주 품목</h5>
            <table class="data-table" style="border:1px solid var(--border-light); border-radius:8px;">
                <thead>
                    <tr>
                        <th>품목명</th>
                        <th>규격</th>
                        <th>발주량</th>
                        <th>단가</th>
                        <th class="text-right">금액</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>원두 (블렌드)</td>
                        <td>1kg</td>
                        <td class="num">5 kg</td>
                        <td class="num">32,000원</td>
                        <td class="text-right num font-bold">160,000원</td>
                    </tr>
                    <tr>
                        <td>우유 (1L)</td>
                        <td>1L</td>
                        <td class="num">20 팩</td>
                        <td class="num">2,500원</td>
                        <td class="text-right num font-bold">50,000원</td>
                    </tr>
                    <tr>
                        <td>플라스틱 컵</td>
                        <td>16oz</td>
                        <td class="num">300 개</td>
                        <td class="num">150원</td>
                        <td class="text-right num font-bold">45,000원</td>
                    </tr>
                    <tr>
                        <td>바닐라 시럽</td>
                        <td>1L</td>
                        <td class="num">3 L</td>
                        <td class="num">12,000원</td>
                        <td class="text-right num font-bold">36,000원</td>
                    </tr>
                    <tr style="background:#FAFAF9;">
                        <td colspan="4" class="text-right" style="font-weight:600;">총 금액</td>
                        <td class="text-right num" style="font-size:16px; font-weight:700; color:var(--primary);">291,000원</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeOrderModal()">닫기</button>
            <button class="btn btn-danger">반려</button>
            <button class="btn btn-success">승인</button>
        </div>
    </div>
</div>

<script>
    function openOrderModal() { document.getElementById('orderModal').classList.add('open'); }
    function closeOrderModal() { document.getElementById('orderModal').classList.remove('open'); }
    document.getElementById('orderModal').addEventListener('click', e => {
        if (e.target.id === 'orderModal') closeOrderModal();
    });

    // 탭 토글
    document.querySelectorAll('.tab').forEach(t => {
        t.addEventListener('click', () => {
            document.querySelectorAll('.tab').forEach(x => x.classList.remove('active'));
            t.classList.add('active');
        });
    });
</script>

<jsp:include page="layout/footer.jsp" />
