<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 이력" scope="request" />
<c:set var="menu" value="order-history" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">HISTORY · 발주 이력</div>
        <h1>총 <span class="accent"><span id="heroTotal">0</span>건</span>의 발주 내역입니다.</h1>
        <p class="hero-brief">발주번호, 거래처, 품목, 금액과 처리 상태를 확인하세요.</p>
    </div>
</section>

<div class="mini-stats">
    <div class="mini-stat"><div class="ms-label">총 발주</div><div class="ms-value"><span id="statTotal">0</span><span class="unit">건</span></div></div>
    <div class="mini-stat accent"><div class="ms-label">완료</div><div class="ms-value"><span id="statDone">0</span><span class="unit">건</span></div></div>
    <div class="mini-stat"><div class="ms-label">대기</div><div class="ms-value"><span id="statPending">0</span><span class="unit">건</span></div></div>
    <div class="mini-stat alert"><div class="ms-label">반려</div><div class="ms-value"><span id="statRej">0</span><span class="unit">건</span></div></div>
</div>

<div class="toolbar">
    <div class="chip-group">
        <button class="chip active" data-f="ALL">전체</button>
        <button class="chip" data-f="COMPLETED">완료</button>
        <button class="chip" data-f="PENDING">대기</button>
        <button class="chip" data-f="REJECTED">반려</button>
    </div>
    <div class="search-input"><input type="text" id="searchInput" placeholder="발주번호, 거래처, 품목 검색..."></div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th>발주번호</th><th>발주일</th><th>거래처</th><th>품목</th><th>발주량</th><th class="text-right">금액</th><th>상태</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="7" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
const STAT={ COMPLETED:{label:'완료',cls:'ok'}, PENDING:{label:'대기',cls:'warn'}, REJECTED:{label:'반려',cls:'off'} };
let ALL=[], filter='ALL';

async function load(){
    try{
        const [p,c,r] = await Promise.all([
            fetch('/api/order/pending').then(x=>x.json()),
            fetch('/api/order/completed').then(x=>x.json()),
            fetch('/api/order/rejected').then(x=>x.json())
        ]);
        ALL=[...p,...c,...r];
        ALL.sort((a,b)=>(b.orderItemId||0)-(a.orderItemId||0));
        stats(); render();
    }catch(e){ $('tbody').innerHTML='<tr><td colspan="7" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function stats(){
    $('statTotal').textContent=ALL.length; $('heroTotal').textContent=ALL.length;
    $('statDone').textContent=ALL.filter(o=>o.status==='COMPLETED').length;
    $('statPending').textContent=ALL.filter(o=>o.status==='PENDING').length;
    $('statRej').textContent=ALL.filter(o=>o.status==='REJECTED').length;
}
function render(){
    const q=$('searchInput').value.trim().toLowerCase();
    let list = filter==='ALL'? ALL : ALL.filter(o=>o.status===filter);
    if(q) list=list.filter(o=> (o.orderDateKey||'').toLowerCase().includes(q) || (o.vendorName||'').toLowerCase().includes(q) || (o.ingredientName||'').toLowerCase().includes(q));
    if(!list.length){ $('tbody').innerHTML='<tr><td colspan="7" class="text-muted">발주 내역이 없습니다.</td></tr>'; return; }
    $('tbody').innerHTML=list.map(o=>{
        const s=STAT[o.status]||{label:o.status,cls:'ok'};
        const amt=(o.finalQty||0)*(o.unitPrice||0);
        const day=(o.createdAt||'').substring(0,10);
        return '<tr><td><strong>'+esc(o.orderDateKey)+'</strong></td>'+
            '<td class="num">'+esc(day)+'</td>'+
            '<td>'+esc(o.vendorName||'—')+'</td>'+
            '<td>'+esc(o.ingredientName||'—')+'</td>'+
            '<td class="num">'+(o.finalQty!=null?o.finalQty.toLocaleString():'')+' '+esc(o.orderUnit||'')+'</td>'+
            '<td class="text-right num font-bold">'+amt.toLocaleString()+'원</td>'+
            '<td><span class="status '+s.cls+'"><span class="dot"></span>'+s.label+'</span></td></tr>';
    }).join('');
}
document.querySelectorAll('.chip').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('.chip').forEach(x=>x.classList.remove('active')); b.classList.add('active');
    filter=b.dataset.f; render();
});
$('searchInput').addEventListener('input', render);
load();
</script>

<jsp:include page="../layout/footer.jsp" />
