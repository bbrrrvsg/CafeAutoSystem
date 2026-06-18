<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="통합 검색" scope="request" />
<c:set var="menu" value="search" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.srch-bar{display:flex;gap:8px;max-width:640px;margin:4px 0 22px;}
.srch-bar input{flex:1;border:1px solid var(--border);border-radius:12px;padding:13px 18px;font-size:16px;outline:none;background:var(--bg-content);transition:border-color .12s;}
.srch-bar input:focus{border-color:var(--primary);}
.srch-bar button{border:none;background:var(--primary);color:#fff;border-radius:12px;padding:0 22px;font-size:15px;font-weight:600;cursor:pointer;}
.srch-bar button:hover{filter:brightness(1.05);}

.srch-summary{font-size:14px;color:var(--text-secondary);margin-bottom:18px;}
.srch-summary b{color:var(--primary);}

.srch-cat{background:var(--bg-card);border:1px solid var(--border-light);border-radius:14px;padding:18px 20px;margin-bottom:16px;box-shadow:var(--shadow-sm,0 1px 2px rgba(0,0,0,.04));}
.srch-cat-head{display:flex;align-items:center;gap:8px;font-size:15px;font-weight:700;color:var(--text);margin-bottom:12px;padding-bottom:10px;border-bottom:1px solid var(--border-light);}
.srch-cat-head .cnt{font-weight:500;font-size:13px;color:var(--text-muted);}
.srch-cat-head i{color:var(--primary);}

.srch-item{display:flex;align-items:center;gap:12px;padding:10px 6px;border-radius:8px;transition:background .1s;}
.srch-item:hover{background:var(--bg-content);}
.srch-item .ico{width:34px;height:34px;border-radius:9px;background:var(--primary-soft,#eef2ff);color:var(--primary);display:flex;align-items:center;justify-content:center;font-size:16px;flex-shrink:0;}
.srch-item .body{min-width:0;flex:1;}
.srch-item .ttl{font-size:14px;font-weight:600;color:var(--text);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.srch-item .sub{font-size:12.5px;color:var(--text-muted);margin-top:2px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.srch-item .meta{font-size:11.5px;color:var(--text-muted);flex-shrink:0;}
.srch-badge{font-size:10px;padding:2px 8px;border-radius:10px;font-weight:600;margin-left:6px;}

.srch-empty{text-align:center;padding:60px 20px;color:var(--text-muted);}
.srch-empty i{font-size:42px;display:block;margin-bottom:12px;opacity:.5;}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">INTEGRATED SEARCH · 통합 검색</div>
        <h1>키워드 하나로 <span class="accent">전부</span> 찾습니다.</h1>
        <p class="hero-brief">메뉴 · 레시피 식자재 · 재고이력 · 원자재 · 거래처 · 발주이력 · 재고로그를 카테고리별로 한 페이지에서 봅니다.</p>
    </div>
</section>

<form class="srch-bar" id="srchForm" onsubmit="return false;">
    <input type="text" id="srchInput" placeholder="메뉴, 식자재, 거래처, 로그 검색..." autocomplete="off">
    <button type="submit">검색</button>
</form>

<div class="srch-summary" id="srchSummary"></div>
<div id="srchResults"></div>

<script>
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
</script>

<jsp:include page="../layout/footer.jsp" />
