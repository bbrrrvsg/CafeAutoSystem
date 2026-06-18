/* ============================================
   Smart Cafe - 공통 JS
   - 사이드바 핀 토글
   - 명령 팔레트 (Cmd+K / Ctrl+K)
   ============================================ */

// 메뉴 데이터 (명령 팔레트 검색용)
const MENU_ITEMS = [
    { group: '운영',     icon: 'bi-grid-1x2',       label: '대시보드',         url: '/' },
    { group: '운영',     icon: 'bi-cart3',          label: 'POS 주문',         url: '/pos' },
    { group: '운영',     icon: 'bi-box-seam',       label: '재고 관리',         url: '/inventory' },
    { group: '발주 · AI', icon: 'bi-robot',          label: 'AI 발주 관리',     url: '/ai-order' },
    { group: '발주 · AI', icon: 'bi-check2-square',  label: '발주 승인',         url: '/approval' },
    { group: '발주 · AI', icon: 'bi-pencil-square',  label: '발주 작성',         url: '/order' },
    { group: '발주 · AI', icon: 'bi-clock-history',  label: '발주 이력',         url: '/order-history' },
    { group: '관리',     icon: 'bi-people',         label: '거래처 관리',       url: '/vendor' },
    { group: '관리',     icon: 'bi-people',         label: '거래처 등록',       url: '/vendor/register' },
    { group: '관리',     icon: 'bi-star',           label: '리뷰 관리',         url: '/review/manage' },
    { group: '관리',     icon: 'bi-graph-up',       label: '리뷰 인사이트',     url: '/review/insight' },
    { group: '관리',     icon: 'bi-journal-text',   label: '로그 추적',         url: '/log' },
];

// ============================================
// 사이드바 핀 토글
// ============================================
(function initSidebarPin() {
    const sidebar = document.getElementById('sidebar');
    const pinBtn = document.getElementById('pinBtn');
    const pinIcon = document.getElementById('pinIcon');
    if (!sidebar || !pinBtn) return;

    // 저장된 상태 복원
    if (localStorage.getItem('sidebar-pinned') === '1') {
        sidebar.classList.add('pinned');
        pinIcon.classList.replace('bi-pin-angle', 'bi-pin-angle-fill');
    }

    pinBtn.addEventListener('click', () => {
        sidebar.classList.toggle('pinned');
        const pinned = sidebar.classList.contains('pinned');
        localStorage.setItem('sidebar-pinned', pinned ? '1' : '0');
        if (pinned) {
            pinIcon.classList.replace('bi-pin-angle', 'bi-pin-angle-fill');
        } else {
            pinIcon.classList.replace('bi-pin-angle-fill', 'bi-pin-angle');
        }
    });
})();

// ============================================
// 명령 팔레트 (Cmd+K / Ctrl+K)
// ============================================
(function initCommandPalette() {
    const trigger = document.getElementById('cmdkBtn');
    if (!trigger) return;

    // 모달 DOM 생성
    const overlay = document.createElement('div');
    overlay.className = 'cmdk-overlay';
    overlay.innerHTML = `
        <div class="cmdk-modal" role="dialog">
            <div class="cmdk-input-wrap">
                <i class="bi bi-search"></i>
                <input type="text" class="cmdk-input" id="cmdkInput" placeholder="메뉴, 페이지 검색..." autocomplete="off">
                <kbd class="kbd">ESC</kbd>
            </div>
            <div class="cmdk-list" id="cmdkList"></div>
            <div class="cmdk-footer">
                <span><span class="kbd">↑</span><span class="kbd">↓</span> 이동</span>
                <span><span class="kbd">↵</span> 선택</span>
                <span><span class="kbd">esc</span> 닫기</span>
            </div>
        </div>
    `;
    document.body.appendChild(overlay);

    const input = overlay.querySelector('#cmdkInput');
    const list = overlay.querySelector('#cmdkList');
    let activeIdx = 0;
    let filtered = [];       // 페이지 이동용 (키보드 네비게이션)
    let searchDebounce = null;

    // 카테고리별 아이콘
    const CATEGORY_ICON = {
        '메뉴':     'bi-cup-straw',
        '원자재':   'bi-basket3',
        '거래처':   'bi-people',
        '발주이력': 'bi-clock-history',
        '재고로그': 'bi-journal-text',
    };
    // 발주이력 상태 뱃지 색
    const STATUS_COLOR = { PENDING: '#f59e0b', COMPLETED: '#10b981', REJECTED: '#ef4444' };

    function renderNavItems(q) {
        filtered = q
            ? MENU_ITEMS.filter(m => m.label.toLowerCase().includes(q) || m.group.toLowerCase().includes(q))
            : MENU_ITEMS;

        let html = '';
        let currentGroup = '';
        filtered.forEach((item, idx) => {
            if (item.group !== currentGroup) {
                html += `<div class="cmdk-group-label">${item.group}</div>`;
                currentGroup = item.group;
            }
            html += `
                <div class="cmdk-item ${idx === activeIdx ? 'active' : ''}" data-idx="${idx}" data-url="${item.url}">
                    <i class="bi ${item.icon}"></i>
                    <span>${item.label}</span>
                </div>`;
        });
        return html;
    }

    function renderDbSection(data) {
        if (!data || !data.results || data.total === 0) return '';
        let html = '';
        for (const [category, items] of Object.entries(data.results)) {
            if (!items.length) continue;
            html += `<div class="cmdk-group-label"><i class="bi ${CATEGORY_ICON[category] || 'bi-search'}" style="margin-right:5px;"></i>${category} <span style="font-weight:400;color:var(--text-muted)">(${items.length})</span></div>`;
            items.forEach(item => {
                const badge = item.status
                    ? `<span style="font-size:10px;padding:1px 6px;border-radius:10px;background:${STATUS_COLOR[item.status] || '#9ca3af'}22;color:${STATUS_COLOR[item.status] || '#9ca3af'};font-weight:600;margin-left:6px;">${item.status}</span>`
                    : '';
                const sub = item.subtitle ? `<span style="font-size:11px;color:var(--text-muted);margin-left:6px;">${item.subtitle}</span>` : '';
                html += `
                    <div class="cmdk-item cmdk-db-item" style="cursor:default;">
                        <i class="bi ${CATEGORY_ICON[category] || 'bi-circle'}" style="font-size:13px;color:var(--text-muted);"></i>
                        <span>${item.title}${badge}${sub}</span>
                    </div>`;
            });
        }
        return html;
    }

    async function fetchDbSearch(q) {
        try {
            const res = await fetch(`/api/search?keyword=${encodeURIComponent(q)}`);
            if (!res.ok) return null;
            return await res.json();
        } catch { return null; }
    }

    async function render(query = '') {
        const q = query.trim().toLowerCase();

        // 페이지 네비 항목 렌더 (즉시)
        const navHtml = renderNavItems(q);

        if (!q) {
            list.innerHTML = navHtml || `<div style="padding:30px;text-align:center;color:#9CA3AF;font-size:13px;">검색 결과 없음</div>`;
            bindNavEvents();
            return;
        }

        // 로딩 표시 (DB 결과 자리)
        list.innerHTML = navHtml + `<div class="cmdk-group-label">데이터 검색 중...</div>`;
        bindNavEvents();

        // DB 검색 디바운스 300ms
        clearTimeout(searchDebounce);
        searchDebounce = setTimeout(async () => {
            const dbData = await fetchDbSearch(q);
            const dbHtml = renderDbSection(dbData);
            const separator = dbHtml ? `<div style="border-top:1px solid var(--border-light);margin:6px 0;"></div>` : '';
            list.innerHTML = (navHtml || '') + separator + (dbHtml || (navHtml ? '' : `<div style="padding:30px;text-align:center;color:#9CA3AF;font-size:13px;">검색 결과 없음</div>`));
            bindNavEvents();
        }, 300);
    }

    function bindNavEvents() {
        list.querySelectorAll('.cmdk-item:not(.cmdk-db-item)').forEach(el => {
            el.addEventListener('click', () => { window.location.href = el.dataset.url; });
            el.addEventListener('mouseenter', () => {
                activeIdx = parseInt(el.dataset.idx);
                updateActive();
            });
        });
    }

    function updateActive() {
        list.querySelectorAll('.cmdk-item:not(.cmdk-db-item)').forEach((el, i) => {
            el.classList.toggle('active', i === activeIdx);
        });
        const activeEl = list.querySelector('.cmdk-item.active');
        if (activeEl) activeEl.scrollIntoView({ block: 'nearest' });
    }

    function open() {
        overlay.classList.add('open');
        input.value = '';
        activeIdx = 0;
        render();
        setTimeout(() => input.focus(), 50);
    }
    function close() { overlay.classList.remove('open'); }

    trigger.addEventListener('click', open);
    overlay.addEventListener('click', e => { if (e.target === overlay) close(); });
    input.addEventListener('input', e => { activeIdx = 0; render(e.target.value); });

    document.addEventListener('keydown', e => {
        if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
            e.preventDefault();
            overlay.classList.contains('open') ? close() : open();
            return;
        }
        if (!overlay.classList.contains('open')) return;

        if (e.key === 'Escape') { close(); }
        else if (e.key === 'ArrowDown') {
            e.preventDefault();
            activeIdx = Math.min(activeIdx + 1, filtered.length - 1);
            updateActive();
        }
        else if (e.key === 'ArrowUp') {
            e.preventDefault();
            activeIdx = Math.max(activeIdx - 1, 0);
            updateActive();
        }
        else if (e.key === 'Enter') {
            e.preventDefault();
            if (filtered[activeIdx]) window.location.href = filtered[activeIdx].url;
        }
    });
})();