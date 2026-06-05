<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="재고 관리" scope="request" />
<c:set var="menu" value="inventory" scope="request" />
<jsp:include page="../layout/header.jsp" />

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<!-- 안전재고 미달 경고 팝업 -->
<div id="alertPopup" style="display:none; position:fixed; top:20px; right:20px; z-index:9999;
     background:#fff; border:2px solid #e74c3c; border-radius:12px; padding:20px 24px;
     box-shadow:0 8px 32px rgba(0,0,0,0.18); min-width:300px; max-width:380px;">
    <div style="display:flex; align-items:center; gap:10px; margin-bottom:12px;">
        <span style="font-size:22px;">⚠️</span>
        <strong style="color:#e74c3c; font-size:15px;">안전재고 미달 경고</strong>
        <button onclick="closeAlert()" style="margin-left:auto; background:none; border:none;
                font-size:18px; cursor:pointer; color:#aaa;">✕</button>
    </div>
    <ul id="alertList" style="margin:0; padding-left:18px; color:#333; font-size:14px; line-height:2;"></ul>
    <div style="margin-top:12px; font-size:12px; color:#999;">AI 발주 초안에 자동 반영됩니다.</div>
</div>

<!-- Hero -->
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">INVENTORY · 실시간</div>
        <h1>재고 <span class="accent" id="heroLowCount">0개</span>이 부족해요.</h1>
        <p class="hero-brief">
            안전재고 미달 품목은 자동으로 AI 발주 초안에 반영됩니다.
            지금 발주하면 모레까지 입고 예정이에요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date" id="heroTotal">총 0 품목 관리 중</div>
    </div>
</section>

<!-- 미니 통계 -->
<div class="mini-stats">
    <div class="mini-stat">
        <div class="ms-label">전체 품목</div>
        <div class="ms-value"><span id="statTotal">0</span><span class="unit">개</span></div>
    </div>
    <div class="mini-stat accent">
        <div class="ms-label">정상</div>
        <div class="ms-value"><span id="statOk">0</span><span class="unit">개</span></div>
    </div>
    <div class="mini-stat alert">
        <div class="ms-label">부족</div>
        <div class="ms-value"><span id="statLow">0</span><span class="unit">개</span></div>
    </div>
    <div class="mini-stat">
        <div class="ms-label">주의</div>
        <div class="ms-value"><span id="statWarn">0</span><span class="unit">개</span></div>
    </div>
</div>

<!-- Chart.js 재고 현황 차트 -->
<div class="card" style="padding:24px; margin-bottom:16px;">
    <div style="font-weight:600; font-size:15px; margin-bottom:16px;">📊 재고 현황 차트</div>
    <canvas id="stockChart" height="80"></canvas>
</div>

<!-- 툴바 -->
<div class="toolbar">
    <div class="chip-group">
        <button class="chip active" data-filter="ALL">전체 <span class="count" id="chipAll">0</span></button>
        <button class="chip" data-filter="OK">정상 <span class="count" id="chipOk">0</span></button>
        <button class="chip" data-filter="LOW">부족 <span class="count" id="chipLow">0</span></button>
        <button class="chip" data-filter="WARN">주의 <span class="count" id="chipWarn">0</span></button>
    </div>
    <div class="search-input">
        <input type="text" id="searchInput" placeholder="품목명으로 검색...">
    </div>
    <div class="toolbar-spacer"></div>
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
                <th class="text-right" style="width:100px;"></th>
            </tr>
        </thead>
        <tbody id="inventoryTableBody">
            <tr>
                <td colspan="5" style="text-align:center;padding:40px;">로딩 중...</td>
            </tr>
        </tbody>
    </table>
</div>

<script>
    let allData = [];
    let currentFilter = 'ALL';
    let stockChartInstance = null;

    const statusMap = {
        OK:   { label: '정상', cls: 'ok' },
        WARN: { label: '주의', cls: 'warn' },
        LOW:  { label: '부족', cls: 'low' }
    };

    async function loadInventory() {
        try {
            const res = await fetch('/api/inventory');
            allData = await res.json();
            updateStats();
            renderTable(allData);
            renderChart(allData);
            checkLowStockAlert(allData);
        } catch (e) {
            document.getElementById('inventoryTableBody').innerHTML =
                '<tr><td colspan="5" style="text-align:center;color:red;">데이터 로딩 실패</td></tr>';
        }
    }

    function updateStats() {
        const total = allData.length;
        const ok    = allData.filter(d => d.status === 'OK').length;
        const low   = allData.filter(d => d.status === 'LOW').length;
        const warn  = allData.filter(d => d.status === 'WARN').length;

        document.getElementById('statTotal').textContent = total;
        document.getElementById('statOk').textContent    = ok;
        document.getElementById('statLow').textContent   = low;
        document.getElementById('statWarn').textContent  = warn;
        document.getElementById('chipAll').textContent   = total;
        document.getElementById('chipOk').textContent    = ok;
        document.getElementById('chipLow').textContent   = low;
        document.getElementById('chipWarn').textContent  = warn;
        document.getElementById('heroLowCount').textContent = low + '개';
        document.getElementById('heroTotal').textContent = '총 ' + total + ' 품목 관리 중';
    }

    function renderChart(data) {
        const labels  = data.map(d => d.ingredientName);
        const current = data.map(d => d.currentStock);
        const safety  = data.map(d => d.safetyStock);

        const bgColors = data.map(d => {
            if (d.status === 'LOW')  return 'rgba(231,76,60,0.7)';
            if (d.status === 'WARN') return 'rgba(243,156,18,0.7)';
            return 'rgba(39,174,96,0.7)';
        });

        if (stockChartInstance) stockChartInstance.destroy();

        stockChartInstance = new Chart(document.getElementById('stockChart'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: '현재 재고',
                        data: current,
                        backgroundColor: bgColors,
                        borderRadius: 6
                    },
                    {
                        label: '안전재고 기준',
                        data: safety,
                        backgroundColor: 'rgba(149,165,166,0.3)',
                        borderColor: 'rgba(149,165,166,0.8)',
                        borderWidth: 2,
                        borderRadius: 6,
                        type: 'bar'
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ctx.dataset.label + ': ' + ctx.parsed.y.toLocaleString() + ' ' + (data[ctx.dataIndex] ? data[ctx.dataIndex].unit : '');
                            }
                        }
                    }
                },
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    }

    // 안전재고 미달 팝업
    function checkLowStockAlert(data) {
        const lowItems = data.filter(d => d.status === 'LOW' || d.status === 'WARN');
        if (lowItems.length === 0) return;

        const list = document.getElementById('alertList');
        list.innerHTML = lowItems.map(d => {
            const icon = d.status === 'LOW' ? '🔴' : '🟡';
            return '<li>' + icon + ' <strong>' + d.ingredientName + '</strong> — 현재 ' +
                   d.currentStock.toLocaleString() + d.unit + ' (안전재고 ' + d.safetyStock.toLocaleString() + d.unit + ')</li>';
        }).join('');

        document.getElementById('alertPopup').style.display = 'block';

        // 5초 후 자동 닫힘
        setTimeout(closeAlert, 5000);
    }

    function closeAlert() {
        document.getElementById('alertPopup').style.display = 'none';
    }

    function renderTable(data) {
        const tbody = document.getElementById('inventoryTableBody');
        if (data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:40px;">해당 품목이 없습니다.</td></tr>';
            return;
        }

        tbody.innerHTML = data.map(item => {
            const s = statusMap[item.status] || { label: item.status, cls: 'ok' };
            return '<tr>' +
                '<td><strong>' + item.ingredientName + '</strong></td>' +
                '<td>' +
                    '<div class="stock-cell">' +
                        '<div class="stock-bar">' +
                            '<div class="fill ' + s.cls + '" style="width:' + item.stockPercent + '%;"></div>' +
                        '</div>' +
                        '<span class="stock-text">' + item.currentStock.toLocaleString() + ' / ' + (item.safetyStock * 2).toLocaleString() + '</span>' +
                    '</div>' +
                '</td>' +
                '<td>' + item.unit + '</td>' +
                '<td><span class="status ' + s.cls + '"><span class="dot"></span>' + s.label + '</span></td>' +
                '<td class="text-right">' +
                    '<div class="row-actions">' +
                        '<button>수정</button>' +
                        '<button class="danger">삭제</button>' +
                    '</div>' +
                '</td>' +
            '</tr>';
        }).join('');
    }

    // 필터 칩 클릭
    document.querySelectorAll('.chip').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.chip').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentFilter = btn.dataset.filter;
            applyFilterAndSearch();
        });
    });

    // 검색
    document.getElementById('searchInput').addEventListener('input', applyFilterAndSearch);

    function applyFilterAndSearch() {
        const keyword = document.getElementById('searchInput').value.trim().toLowerCase();
        let filtered = currentFilter === 'ALL'
            ? allData
            : allData.filter(d => d.status === currentFilter);
        if (keyword) {
            filtered = filtered.filter(d => d.ingredientName.toLowerCase().includes(keyword));
        }
        renderTable(filtered);
    }

    loadInventory();

    // SSE 연결 - 주문 발생 시 서버가 푸시하면 재고 자동 갱신
    const evtSource = new EventSource('/api/sse/stock');
    evtSource.addEventListener('stockUpdate', function(e) {
        console.log('[SSE] 재고 변경 감지 - 화면 갱신');
        loadInventory();
    });
    evtSource.onerror = function() {
        console.warn('[SSE] 연결 끊김 - 자동 재연결 시도 중...');
    };

</script>

<jsp:include page="../layout/footer.jsp" />
