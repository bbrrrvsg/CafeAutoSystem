akw<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 승인" scope="request" />
<c:set var="menu" value="approval" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">APPROVAL · 승인 대기</div>
        <h1><span class="accent" id="heroCount">-</span>건의 발주가 승인을 기다려요.</h1>
        <p class="hero-brief">
            AI 검토를 통과한 발주서 또는 수동 작성된 발주서를 검토하고 승인하세요.
        </p>
    </div>
</section>

<div class="tabs">
    <div class="tab active" data-tab="pending">승인 대기 <span class="tab-count" id="pendingCount">-</span></div>
    <div class="tab" data-tab="completed">승인 완료 <span class="tab-count" id="completedCount">-</span></div>
    <div class="tab" data-tab="rejected">반려 <span class="tab-count" id="rejectedCount">-</span></div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead>
            <tr>
                <th>발주번호</th>
                <th>발주일</th>
                <th>거래처</th>
                <th>식자재</th>
                <th class="text-right">최종 수량</th>
                <th>유통기한</th>
                <th>상태</th>
                <th class="text-right" style="width:220px;">작업</th>
            </tr>
        </thead>
        <tbody id="orderTbody">
            <tr><td colspan="8" style="text-align:center; padding:30px; color:var(--text-muted);">로딩 중...</td></tr>
        </tbody>
    </table>
</div>

<div class="modal-overlay" id="orderModal">
    <div class="modal">
        <div class="modal-header">
            <h3 id="modalTitle">발주서 상세 / 수정</h3>
            <button class="panel-close" onclick="closeOrderModal()">×</button>
        </div>
        <div class="modal-body">
            <div class="detail-list">
                <div class="detail-row"><span class="key">발주번호</span><span class="val num" id="mOrderId">-</span></div>
                <div class="detail-row"><span class="key">발주일</span><span class="val num" id="mCreateDate">-</span></div>
                <div class="detail-row"><span class="key">식자재</span><span class="val" id="mIngredient">-</span></div>
                <div class="detail-row"><span class="key">AI 제안 수량</span><span class="val num" id="mSuggested">-</span></div>
                <div class="detail-row"><span class="key">유통기한</span><span class="val num" id="mExpiration">-</span></div>
            </div>

            <h5 style="font-size:12px; font-weight:700; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.4px; margin: 20px 0 12px;">
                <span id="editSectionTitle">수정 가능 항목</span>
            </h5>
            <div class="form-row">
                <div class="fr-label">거래처 (우선순위)</div>
                <div class="fr-control">
                    <select id="mVendorSelect" style="width:100%; padding:8px; border:1px solid var(--border); border-radius:6px;"></select>
                </div>
            </div>
            <div class="form-row">
                <div class="fr-label">최종 발주 수량</div>
                <div class="fr-control">
                    <input type="number" id="mFinalQty" min="0" style="width:160px; padding:8px; border:1px solid var(--border); border-radius:6px;">
                </div>
            </div>

            <div id="approverInfo" style="display:none; margin-top:18px; padding:14px; border-radius:8px;">
                <div style="font-size:12px; color:var(--text-muted); margin-bottom:6px;">처리 정보</div>
                <div id="approverDetail" style="font-size:14px;"></div>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeOrderModal()">닫기</button>
            <button class="btn btn-primary" id="btnSave" onclick="askPassword('update')">수정 저장</button>
            <button class="btn btn-danger" id="btnReject" onclick="askPassword('reject')">반려</button>
            <button class="btn btn-success" id="btnApprove" onclick="askPassword('approve')">승인 + 메일발송</button>
        </div>
    </div>
</div>

<div class="modal-overlay" id="pwModal">
    <div class="modal" style="max-width: 400px;">
        <div class="modal-header">
            <h3>점장 비밀번호</h3>
            <button class="panel-close" onclick="closePwModal()">×</button>
        </div>
        <div class="modal-body">
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 14px;">
                발주 수정 / 승인 권한 확인을 위해 점장 비밀번호를 입력해주세요.
            </p>
            <input type="password" id="pwInput" placeholder="비밀번호"
                   style="width: 100%; padding: 10px 12px; border: 1px solid var(--border); border-radius: 8px; font-size: 14px;"
                   onkeydown="if (event.key === 'Enter') submitPassword()">
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closePwModal()">취소</button>
            <button class="btn btn-primary" onclick="submitPassword()">확인</button>
        </div>
    </div>
</div>

<script>
    // 거래처 메일 (장민서 RPA 연동용) — 추후 거래처별 매핑으로 교체 예정
    const RPA_TARGET_EMAIL = 'wkdalstj0522@gmail.com';

    let pendingOrders = [], completedOrders = [], rejectedOrders = [];
    let currentTab = 'pending';
    let currentOrderId = null;
    let pendingAction = null;

    async function init() {
        try {
            await Promise.all([loadOrders('pending'), loadOrders('completed'), loadOrders('rejected')]);
            updateCounts();
            renderTable();
        } catch (e) { alert('데이터 로드 실패: ' + e.message); }
    }

    async function loadOrders(status) {
        const res = await fetch('/api/order/' + status);
        if (!res.ok) throw new Error(status + ' 목록 조회 실패');
        const data = await res.json();
        if (status === 'pending')   pendingOrders   = data;
        if (status === 'completed') completedOrders = data;
        if (status === 'rejected')  rejectedOrders  = data;
    }

    function fmtDate(iso) { return iso ? iso.substring(0, 10) : ''; }
    function fmtDateTime(iso) { return iso ? iso.substring(0, 10) + ' ' + iso.substring(11, 16) : ''; }

    function switchTab(tab) {
        currentTab = tab;
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelector('.tab[data-tab="' + tab + '"]').classList.add('active');
        renderTable();
    }

    function getCurrentList() {
        if (currentTab === 'pending')   return pendingOrders;
        if (currentTab === 'completed') return completedOrders;
        if (currentTab === 'rejected')  return rejectedOrders;
        return [];
    }

    function updateCounts() {
        document.getElementById('pendingCount').textContent   = pendingOrders.length;
        document.getElementById('completedCount').textContent = completedOrders.length;
        document.getElementById('rejectedCount').textContent  = rejectedOrders.length;
        document.getElementById('heroCount').textContent      = pendingOrders.length;
    }

    function statusBadge(s) {
        if (s === 'PENDING')   return '<span class="badge badge-warning">대기</span>';
        if (s === 'COMPLETED') return '<span class="badge badge-success">완료</span>';
        if (s === 'REJECTED')  return '<span class="badge badge-danger">반려</span>';
        return s;
    }

    function actionButtons(o) {
        let html = '<button class="btn btn-secondary btn-sm" onclick="openOrderModal(' + o.orderItemId + ')">상세보기</button>';
        if (o.status === 'PENDING') {
            html += ' <button class="btn btn-success btn-sm" onclick="quickAction(\'approve\', ' + o.orderItemId + ')">승인</button>';
            html += ' <button class="btn btn-danger btn-sm" onclick="quickAction(\'reject\', ' + o.orderItemId + ')">반려</button>';
        }
        return html;
    }

    function renderTable() {
        const list = getCurrentList();
        const tbody = document.getElementById('orderTbody');
        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding:30px; color:var(--text-muted);">해당 상태의 발주가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = list.map(function(o) {
            const orderNo = o.orderDateKey + '-' + String(o.orderItemId).padStart(3, '0');
            const vendorLabel = (o.vendorName || '-') + ' <span style="color:var(--text-muted); font-size:11px;">[' + o.priorityRank + '순위]</span>';
            return '<tr>'
                + '<td><strong>' + orderNo + '</strong></td>'
                + '<td class="num">' + fmtDate(o.createdAt) + '</td>'
                + '<td>' + vendorLabel + '</td>'
                + '<td>' + (o.ingredientName || '-') + '</td>'
                + '<td class="text-right num font-bold">' + o.finalQty + '</td>'
                + '<td class="num">' + (fmtDate(o.expirationDate) || '-') + '</td>'
                + '<td>' + statusBadge(o.status) + '</td>'
                + '<td class="text-right">' + actionButtons(o) + '</td>'
                + '</tr>';
        }).join('');
    }

    async function openOrderModal(id) {
        const order = getCurrentList().find(o => o.orderItemId === id);
        if (!order) return;
        currentOrderId = id;

        const orderNo = order.orderDateKey + '-' + String(order.orderItemId).padStart(3, '0');
        document.getElementById('mOrderId').textContent     = orderNo;
        document.getElementById('mCreateDate').textContent  = fmtDate(order.createdAt);
        document.getElementById('mIngredient').textContent  = order.ingredientName || '-';
        document.getElementById('mSuggested').textContent   = order.suggestedQty;
        document.getElementById('mExpiration').textContent  = fmtDate(order.expirationDate) || '없음';
        document.getElementById('mFinalQty').value          = order.finalQty;

        // 같은 식자재의 거래처 1/2/3순위를 드롭다운에 채움
        const sel = document.getElementById('mVendorSelect');
        try {
            const res = await fetch('/api/vendor-ingredient/by-ingredient/' + order.ingredientId);
            const mappings = res.ok ? await res.json() : [];
            sel.innerHTML = mappings.map(function(m) {
                const selected = m.vendorIngredientId === order.vendorIngredientId ? ' selected' : '';
                const label = '[' + m.priorityRank + '순위] ' + (m.vendorName || '-') + ' · ₩' + m.unitPrice.toLocaleString();
                return '<option value="' + m.vendorIngredientId + '"' + selected + '>' + label + '</option>';
            }).join('');
        } catch (e) {
            sel.innerHTML = '<option value="' + order.vendorIngredientId + '" selected>' + (order.vendorName || '매핑 #' + order.vendorIngredientId) + '</option>';
        }

        const isPending = order.status === 'PENDING';
        document.getElementById('mFinalQty').disabled            = !isPending;
        document.getElementById('mVendorSelect').disabled        = !isPending;
        document.getElementById('btnSave').style.display         = isPending ? '' : 'none';
        document.getElementById('btnApprove').style.display      = isPending ? '' : 'none';
        document.getElementById('btnReject').style.display       = isPending ? '' : 'none';
        document.getElementById('editSectionTitle').textContent  = isPending ? '수정 가능 항목' : '발주 내역';
        document.getElementById('modalTitle').textContent        = isPending ? '발주서 상세 / 수정' : '발주서 상세';

        const info = document.getElementById('approverInfo');
        if (isPending) {
            info.style.display = 'none';
        } else {
            info.style.display = 'block';
            info.style.background = order.status === 'COMPLETED' ? '#F0FDF4' : '#FEF2F2';
            const label = order.status === 'COMPLETED' ? '승인 완료' : '반려 처리';
            document.getElementById('approverDetail').innerHTML =
                '<strong>' + label + '</strong>'
                + '<br><span style="color:var(--text-muted); font-size:12px;">처리 시각: ' + fmtDateTime(order.updatedAt) + '</span>';
        }
        document.getElementById('orderModal').classList.add('open');
    }

    function closeOrderModal() {
        document.getElementById('orderModal').classList.remove('open');
        currentOrderId = null;
    }

    function quickAction(action, id) {
        currentOrderId = id;
        askPassword(action);
    }

    function askPassword(action) {
        if (!currentOrderId) return;
        pendingAction = { action: action, orderId: currentOrderId };
        document.getElementById('pwInput').value = '';
        document.getElementById('pwModal').classList.add('open');
        setTimeout(function() { document.getElementById('pwInput').focus(); }, 100);
    }

    function closePwModal() {
        document.getElementById('pwModal').classList.remove('open');
        pendingAction = null;
    }

    async function submitPassword() {
        const password = document.getElementById('pwInput').value;
        if (!password) { alert('비밀번호를 입력하세요.'); return; }
        if (!pendingAction) return;
        try {
            if (pendingAction.action === 'approve') {
                await approveOrder(pendingAction.orderId, password);
                // 승인 성공 → 장민서 RPA 메일 발송 연동
                if (typeof sendRpaMail === 'function') {
                    sendRpaMail(RPA_TARGET_EMAIL);
                } else {
                    alert('승인 완료!');
                }
            } else if (pendingAction.action === 'reject') {
                await rejectOrder(pendingAction.orderId, password);
                alert('반려 처리됨');
            } else if (pendingAction.action === 'update') {
                await updateOrder(pendingAction.orderId, password);
                alert('수정 저장됨!');
            }
            closePwModal();
            closeOrderModal();
            await Promise.all([loadOrders('pending'), loadOrders('completed'), loadOrders('rejected')]);
            updateCounts();
            renderTable();
        } catch (e) { alert(e.message); }
    }

    async function approveOrder(id, password) {
        const res = await fetch('/api/order/' + id + '/approve?password=' + encodeURIComponent(password), { method: 'PUT' });
        if (!res.ok) throw new Error('승인 실패: 비밀번호가 틀렸거나 이미 처리된 발주입니다.');
        return res.json();
    }

    async function rejectOrder(id, password) {
        const res = await fetch('/api/order/' + id + '/reject?password=' + encodeURIComponent(password), { method: 'PUT' });
        if (!res.ok) throw new Error('반려 실패: 비밀번호가 틀렸거나 이미 처리된 발주입니다.');
        return res.json();
    }

    async function updateOrder(id, password) {
        const order = pendingOrders.find(o => o.orderItemId === id);
        const body = Object.assign({}, order, {
            vendorIngredientId: parseInt(document.getElementById('mVendorSelect').value),
            finalQty: parseInt(document.getElementById('mFinalQty').value)
        });
        const res = await fetch('/api/order/' + id + '?password=' + encodeURIComponent(password), {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (!res.ok) throw new Error('수정 실패: 비밀번호가 틀렸거나 PENDING 상태가 아닙니다.');
        return res.json();
    }

    document.getElementById('orderModal').addEventListener('click', function(e) { if (e.target.id === 'orderModal') closeOrderModal(); });
    document.getElementById('pwModal').addEventListener('click', function(e) { if (e.target.id === 'pwModal') closePwModal(); });
    document.querySelectorAll('.tab').forEach(function(t) { t.addEventListener('click', function() { switchTab(t.dataset.tab); }); });
    window.addEventListener('DOMContentLoaded', init);
</script>
<script src="/js/rpa.js"></script>

<jsp:include page="../layout/footer.jsp" />
