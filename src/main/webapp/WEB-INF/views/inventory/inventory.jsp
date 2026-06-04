<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="pageTitle" value="재고 관리" scope="request" />
        <c:set var="menu" value="inventory" scope="request" />
        <jsp:include page="../layout/header.jsp" />

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

            // 상태 한글 + CSS 클래스 매핑
            const statusMap = {
                OK: { label: '정상', cls: 'ok' },
                WARN: { label: '주의', cls: 'warn' },
                LOW: { label: '부족', cls: 'low' }
            };

            async function loadInventory() {
                try {
                    const res = await fetch('/api/inventory');
                    allData = await res.json();
                    updateStats();
                    renderTable(allData);
                } catch (e) {
                    document.getElementById('inventoryTableBody').innerHTML =
                        '<tr><td colspan="5" style="text-align:center;color:red;">데이터 로딩 실패</td></tr>';
                }
            }

            function updateStats() {
                const total = allData.length;
                const ok = allData.filter(d => d.status === 'OK').length;
                const low = allData.filter(d => d.status === 'LOW').length;
                const warn = allData.filter(d => d.status === 'WARN').length;

                document.getElementById('statTotal').textContent = total;
                document.getElementById('statOk').textContent = ok;
                document.getElementById('statLow').textContent = low;
                document.getElementById('statWarn').textContent = warn;
                document.getElementById('chipAll').textContent = total;
                document.getElementById('chipOk').textContent = ok;
                document.getElementById('chipLow').textContent = low;
                document.getElementById('chipWarn').textContent = warn;
                document.getElementById('heroLowCount').textContent = low + '개';
                document.getElementById('heroTotal').textContent = '총 ' + total + ' 품목 관리 중';
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

            // 초기 로드
            loadInventory();
        </script>

        <jsp:include page="../layout/footer.jsp" />