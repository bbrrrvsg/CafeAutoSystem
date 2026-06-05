<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="로그 추적" scope="request" />
<c:set var="menu" value="log" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">STOCK LOG · 재고 변동 기록</div>
        <h1>총 <span class="accent"><span id="logCount">0</span>건</span>의 재고 변동이 기록됐어요.</h1>
        <p class="hero-brief">입고(STOCK_IN)·판매(STOCK_OUT)·이월·폐기 등 모든 재고 변동 이벤트를 추적합니다.</p>
    </div>
    <div class="hero-side">
        <div class="date">마지막 활동</div>
        <div class="time" id="lastTime" style="font-size:14px; font-weight:600;">—</div>
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
    <div class="chip-group" style="padding:2px;">
        <button class="chip active" id="viewTable" style="padding:5px 12px; font-size:12px;">테이블</button>
        <button class="chip" id="viewTimeline" style="padding:5px 12px; font-size:12px;">타임라인</button>
    </div>
</div>

<div class="card flush" id="tableView">
    <table class="data-table">
        <thead><tr><th style="width:170px;">시간</th><th style="width:120px;">구분</th><th>내용</th><th style="width:90px;">수량</th><th style="width:120px;">사용자</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="5" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<div class="card" id="timelineView" style="display:none;">
    <div class="timeline" id="timeline"></div>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
let LOGS=[];
function tagClass(t){ if(t==='STOCK_IN'||t==='STOCK_FORWARD')return 'action'; if(t==='STOCK_OUT'||t==='STOCK_DISCARD')return 'system'; if(t==='STOCK_WARNING'||t==='STOCK_REJECT')return 'ai'; return 'user'; }
function fmt(dt){ return (dt||'').replace('T',' ').substring(0,19); }

async function load(){
    try{ LOGS=await (await fetch('/api/stock/logs')).json(); render(); }
    catch(e){ $('tbody').innerHTML='<tr><td colspan="5" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function filtered(){
    const t=$('typeFilter').value, q=$('searchInput').value.trim().toLowerCase();
    return LOGS.filter(l => (!t || l.logType===t) && (!q || (l.message||'').toLowerCase().includes(q)));
}
function render(){
    $('logCount').textContent=LOGS.length;
    if(LOGS.length) $('lastTime').textContent=fmt(LOGS[0].createdAt).substring(11);
    const list=filtered();
    // 테이블
    $('tbody').innerHTML = list.length? list.map(l=>
        '<tr><td class="num">'+esc(fmt(l.createdAt))+'</td>'+
        '<td><span class="timeline-tag '+tagClass(l.logType)+'">'+esc(l.logType)+'</span></td>'+
        '<td>'+esc(l.message||'')+'</td>'+
        '<td class="num">'+(l.amount!=null?(l.amount>0?'+':'')+l.amount.toLocaleString():'')+'</td>'+
        '<td class="text-muted">'+esc(l.userId||'')+'</td></tr>').join('')
        : '<tr><td colspan="5" class="text-muted">로그가 없습니다.</td></tr>';
    // 타임라인
    $('timeline').innerHTML = list.map(l=>{
        const cls=tagClass(l.logType);
        return '<div class="timeline-item '+cls+'"><div class="timeline-head">'+
            '<span class="timeline-time">'+esc(fmt(l.createdAt))+'</span>'+
            '<span class="timeline-tag '+cls+'">'+esc(l.logType)+'</span>'+
            '<span class="timeline-actor">'+esc(l.userId||'')+'</span></div>'+
            '<div class="timeline-content">'+esc(l.message||'')+'</div></div>';
    }).join('');
}
$('typeFilter').onchange=render; $('searchInput').addEventListener('input',render);
$('viewTable').onclick=()=>{ $('viewTable').classList.add('active'); $('viewTimeline').classList.remove('active'); $('tableView').style.display=''; $('timelineView').style.display='none'; };
$('viewTimeline').onclick=()=>{ $('viewTimeline').classList.add('active'); $('viewTable').classList.remove('active'); $('tableView').style.display='none'; $('timelineView').style.display=''; };
load();
</script>

<jsp:include page="../layout/footer.jsp" />
