/* ============================================
   통합검색 전용 결과 페이지 (/search)
   - JSP의 EL(${...}) 충돌을 피하려고 외부 JS로 분리
   ============================================ */
(function(){
    const CATEGORY_ICON = {
        '메뉴':         'bi-cup-straw',
        '레시피 식자재': 'bi-cup-hot',
        '레시피 재고이력': 'bi-journal-text',
        '원자재':       'bi-basket3',
        '거래처':       'bi-people',
        '발주이력':     'bi-clock-history',
        '재고로그':     'bi-journal-text',
    };
    const STATUS_COLOR = { PENDING:'#f59e0b', COMPLETED:'#10b981', REJECTED:'#ef4444' };

    const input   = document.getElementById('srchInput');
    const summary = document.getElementById('srchSummary');
    const results = document.getElementById('srchResults');
    const form    = document.getElementById('srchForm');

    function esc(s){ return (s==null?'':String(s)).replace(/[&<>"]/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c])); }
    function getKeyword(){ return new URLSearchParams(location.search).get('keyword') || ''; }

    function render(data){
        if(!data || !data.results || data.total === 0){
            summary.innerHTML = `'<b>${esc(data ? data.keyword : input.value)}</b>' 검색 결과가 없습니다.`;
            results.innerHTML = `<div class="srch-empty"><i class="bi bi-search"></i>일치하는 항목이 없습니다.<br>다른 키워드로 검색해보세요.</div>`;
            return;
        }
        summary.innerHTML = `'<b>${esc(data.keyword)}</b>' 검색 결과 <b>${data.total}</b>건`;

        let html = '';
        for(const [category, items] of Object.entries(data.results)){
            if(!items.length) continue;
            html += `<div class="srch-cat">
                <div class="srch-cat-head"><i class="bi ${CATEGORY_ICON[category]||'bi-search'}"></i>${esc(category)}<span class="cnt">${items.length}건</span></div>`;
            items.forEach(item=>{
                const badge = item.status
                    ? `<span class="srch-badge" style="background:${(STATUS_COLOR[item.status]||'#9ca3af')}22;color:${STATUS_COLOR[item.status]||'#9ca3af'};">${esc(item.status)}</span>` : '';
                const sub  = item.subtitle ? `<div class="sub">${esc(item.subtitle)}</div>` : '';
                const meta = item.createdAt ? `<div class="meta">${esc(item.createdAt.substring(0,10))}</div>` : '';
                html += `<div class="srch-item">
                    <div class="ico"><i class="bi ${CATEGORY_ICON[category]||'bi-circle'}"></i></div>
                    <div class="body"><div class="ttl">${esc(item.title)}${badge}</div>${sub}</div>
                    ${meta}
                </div>`;
            });
            html += `</div>`;
        }
        results.innerHTML = html;
    }

    async function doSearch(keyword){
        const q = (keyword||'').trim();
        if(!q){ summary.textContent=''; results.innerHTML=`<div class="srch-empty"><i class="bi bi-search"></i>검색어를 입력하세요.</div>`; return; }
        summary.innerHTML = `'<b>${esc(q)}</b>' 검색 중...`;
        results.innerHTML = '';
        try{
            const res = await fetch(`/api/search?keyword=${encodeURIComponent(q)}`);
            if(!res.ok){ throw new Error('HTTP '+res.status); }
            render(await res.json());
        }catch(e){
            summary.textContent='';
            results.innerHTML = `<div class="srch-empty"><i class="bi bi-exclamation-triangle"></i>검색 중 오류가 발생했습니다.<br>${esc(e.message)}</div>`;
        }
    }

    // 검색 시 URL도 갱신 (새로고침/공유 가능)
    form.addEventListener('submit', ()=>{
        const q = input.value.trim();
        history.replaceState(null, '', q ? `/search?keyword=${encodeURIComponent(q)}` : '/search');
        doSearch(q);
    });

    // 진입 시 URL 키워드로 자동 검색
    const initial = getKeyword();
    input.value = initial;
    if(initial) doSearch(initial); else input.focus();
})();
