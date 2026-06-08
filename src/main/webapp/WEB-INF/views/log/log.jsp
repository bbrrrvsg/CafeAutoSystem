<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="로그 추적" scope="request" />
<c:set var="menu" value="log" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">STOCK LOG · 재고 변동 기록</div>
        <h1>재고 변동 로그를 <span class="accent">추적</span>합니다.</h1>
        <p class="hero-brief">현재 장부(CURRENT)와 마감 이력(HISTORICAL)을 따로 봅니다. 월말 마감이 돌면 현재 로그가 이력으로 옮겨집니다. (5초마다 자동 새로고침)</p>
    </div>
</section>

<div class="toolbar">
    <select id="typeFilter" style="padding:8px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content);">
        <option value="">구분 전체</option>
        <option value="STOCK_IN">입고</option>
        <option value="STOCK_OUT">출고/판매</option>
        <option value="STOCK_FORWARD">이월</option>
        <option value="STOCK_DISCARD">폐기</option>
        <option value="STOCK_REJECT">반려</option>
        <option value="STOCK_WARNING">경고</option>
    </select>
    <div class="search-input"><input type="text" id="searchInput" placeholder="내용 검색..."></div>
    <div class="toolbar-spacer"></div>
    <button id="closeBtn" class="btn btn-primary btn-sm">월말 마감 실행</button>
</div>

<!-- 현재 로그 (current_stock_log) -->
<div style="font-weight:600; font-size:15px; margin:8px 0 10px;">📒 현재 장부 (CURRENT_STOCK_LOG) · <span id="curCount" class="text-muted">0건</span></div>
<div class="card flush" style="margin-bottom:24px;">
    <table class="data-table">
        <thead><tr><th style="width:170px;">시간</th><th style="width:120px;">구분</th><th>내용</th><th style="width:90px;">수량</th><th style="width:110px;">사용자</th></tr></thead>
        <tbody id="curBody"><tr><td colspan="5" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<!-- 이력 로그 (historical_stock_log) -->
<div style="font-weight:600; font-size:15px; margin:8px 0 10px;">🗄️ 마감 이력 (HISTORICAL_STOCK_LOG) · <span id="hisCount" class="text-muted">0건</span></div>
<div class="card flush">
    <table class="data-table">
        <thead><tr><th style="width:170px;">시간</th><th style="width:120px;">구분</th><th>내용</th><th style="width:90px;">수량</th><th style="width:110px;">사용자</th></tr></thead>
        <tbody id="hisBody"><tr><td colspan="5" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
function tagClass(t){ if(t==='STOCK_IN'||t==='STOCK_FORWARD')return 'action'; if(t==='STOCK_OUT'||t==='STOCK_DISCARD')return 'system'; if(t==='STOCK_WARNING'||t==='STOCK_REJECT')return 'ai'; return 'user'; }
function fmt(dt){ return (dt||'').replace('T',' ').substring(0,19); }
let LOGS=[];

async function load(){
    try{ LOGS=await (await fetch('/api/stock/logs')).json(); render(); }
    catch(e){ $('curBody').innerHTML=$('hisBody').innerHTML='<tr><td colspan="5" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function rowsHtml(list){
    if(!list.length) return '<tr><td colspan="5" class="text-muted">로그가 없습니다.</td></tr>';
    return list.map(l=>
        '<tr><td class="num">'+esc(fmt(l.createdAt))+'</td>'+
        '<td><span class="timeline-tag '+tagClass(l.logType)+'">'+esc(l.logType)+'</span></td>'+
        '<td>'+esc(l.message||'')+'</td>'+
        '<td class="num">'+(l.amount!=null?(l.amount>0?'+':'')+l.amount.toLocaleString():'')+'</td>'+
        '<td class="text-muted">'+esc(l.userId||'')+'</td></tr>').join('');
}
function render(){
    const t=$('typeFilter').value, q=$('searchInput').value.trim().toLowerCase();
    const match = l => (!t || l.logType===t) && (!q || (l.message||'').toLowerCase().includes(q));
    const cur = LOGS.filter(l => l.source==='현재' && match(l));
    const his = LOGS.filter(l => l.source==='이력' && match(l));
    $('curBody').innerHTML = rowsHtml(cur);
    $('hisBody').innerHTML = rowsHtml(his);
    $('curCount').textContent = cur.length + '건';
    $('hisCount').textContent = his.length + '건';
}
$('typeFilter').onchange=render; $('searchInput').addEventListener('input',render);
$('closeBtn').onclick=async()=>{
    if(!confirm('월말 마감을 실행합니다.\n현재 장부 → 이력 백업 후, 식자재별 잔량만 이월됩니다. 진행할까요?')) return;
    const t=$('closeBtn').textContent; $('closeBtn').disabled=true; $('closeBtn').textContent='처리 중...';
    try{
        const r=await fetch('/api/stock/close',{method:'POST'});
        if(r.ok){ alert('마감 완료 — 현재 장부가 이력으로 이동했습니다.'); load(); }
        else alert('마감 실패 ('+r.status+')\n'+await r.text());
    }catch(e){ alert('요청 실패: '+e.message); }
    finally{ $('closeBtn').disabled=false; $('closeBtn').textContent=t; }
};
load();
setInterval(load, 5000); // 5초마다 자동 새로고침 → 마이그레이션·입출고 변동 실시간 반영
</script>

<jsp:include page="../layout/footer.jsp" />
