<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 작성" scope="request" />
<c:set var="menu" value="order" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input,.crud-form select{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">NEW ORDER · 발주서 작성</div>
        <h1>새 발주서를 작성합니다.</h1>
        <p class="hero-brief">거래처-식자재와 발주량을 선택해 품목을 담고, 발주서를 생성하세요. 생성된 발주는 승인 화면에서 처리됩니다.</p>
    </div>
</section>

<!-- 품목 추가 폼 -->
<div class="form-section crud-form">
    <h3>발주 품목 추가</h3>
    <div style="display:grid; grid-template-columns: 2.4fr 1fr 1.4fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>거래처-식자재 (단가순) *</label><select id="fVi"><option value="">— 선택 —</option></select></div>
        <div><label>발주량 * <span class="text-muted">(단가 기준 단위: 예 우유=팩/L)</span></label><input id="fQty" type="number" placeholder="단가 기준 수량"></div>
        <div><label>유통기한 (선택)</label><input id="fExp" type="date"></div>
        <div><button id="addBtn" class="btn btn-secondary btn-sm">+ 품목 담기</button></div>
    </div>
</div>

<!-- 발주 목록(장바구니) -->
<div class="split-layout">
    <div class="card flush">
        <table class="data-table">
            <thead><tr><th>식자재</th><th>거래처</th><th>단가</th><th>발주량</th><th class="text-right">금액</th><th></th></tr></thead>
            <tbody id="cartBody"><tr><td colspan="6" class="text-muted">담은 품목이 없습니다.</td></tr></tbody>
        </table>
    </div>

    <aside class="detail-panel">
        <div class="panel-head"><div><div class="panel-title">발주 요약</div><div class="panel-sub">생성 시 품목별 발주서가 만들어집니다</div></div></div>
        <div class="detail-list">
            <div class="detail-row"><span class="key">품목 수</span><span class="val num"><span id="sumCount">0</span>개</span></div>
            <div class="detail-row"><span class="key">총 발주 금액</span><span class="val num"><span id="sumPrice">0</span>원</span></div>
        </div>
        <div style="display:flex; flex-direction:column; gap:8px; margin-top:16px;">
            <button id="submitBtn" class="btn btn-primary" style="padding:12px;">발주서 생성하기</button>
        </div>
        <div style="margin-top:16px; padding:12px; background:#FEF3C7; border-radius:10px; font-size:12px; color:#92400E;">
            발주량 단위는 선택한 식자재의 단위를 따릅니다. 생성된 발주는 <strong>PENDING</strong> 상태로 승인 대기합니다.
        </div>
    </aside>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
let VIS=[], UNIT={}, CART=[];

async function loadRefs(){
    try{ VIS = await (await fetch('/api/vendor-ingredient')).json(); }catch(e){ VIS=[]; }
    try{ (await (await fetch('/api/ingredient')).json()).forEach(i=>UNIT[i.ingredientId]=i.unit); }catch(e){}
    // 식자재 → 순위순 정렬해서 표시
    VIS.sort((a,b)=>(a.ingredientId-b.ingredientId)||(a.priorityRank-b.priorityRank));
    $('fVi').innerHTML='<option value="">— 선택 —</option>'+VIS.map(v=>
        '<option value="'+v.vendorIngredientId+'">'+esc(v.ingredientName)+' ← '+esc(v.vendorName)+' ('+(v.unitPrice||0).toLocaleString()+'원, '+v.priorityRank+'순위)</option>').join('');
}
// 발주량은 '단가 기준 단위(발주 단위)'로 입력 — 재고 단위(ingredient.unit, 예 ml)와 다를 수 있어 단위 라벨은 표시하지 않음

function addItem(){
    const v=VIS.find(x=>String(x.vendorIngredientId)===$('fVi').value);
    const qty=$('fQty').value===''?null:Number($('fQty').value);
    if(!v){ alert('거래처-식자재를 선택하세요.'); return; }
    if(qty==null||qty<=0){ alert('발주량은 1 이상이어야 합니다.'); return; }
    CART.push({ vendorIngredientId:v.vendorIngredientId, ingredientName:v.ingredientName, vendorName:v.vendorName,
        unitPrice:v.unitPrice||0, unit:UNIT[v.ingredientId]||'', qty, expirationDate:$('fExp').value||null });
    $('fQty').value=''; renderCart();
}
function renderCart(){
    if(!CART.length){ $('cartBody').innerHTML='<tr><td colspan="6" class="text-muted">담은 품목이 없습니다.</td></tr>'; }
    else{
        $('cartBody').innerHTML=CART.map((c,i)=>
            '<tr><td><strong>'+esc(c.ingredientName)+'</strong></td>'+
            '<td>'+esc(c.vendorName)+'</td>'+
            '<td class="num">'+c.unitPrice.toLocaleString()+'원</td>'+
            '<td class="num">'+c.qty.toLocaleString()+'</td>'+
            '<td class="text-right num">'+(c.unitPrice*c.qty).toLocaleString()+'원</td>'+
            '<td class="text-right"><button class="danger" data-rm="'+i+'">×</button></td></tr>').join('');
        $('cartBody').querySelectorAll('[data-rm]').forEach(b=>b.onclick=()=>{ CART.splice(Number(b.dataset.rm),1); renderCart(); });
    }
    $('sumCount').textContent=CART.length;
    $('sumPrice').textContent=CART.reduce((s,c)=>s+c.unitPrice*c.qty,0).toLocaleString();
}
async function submit(){
    if(!CART.length){ alert('담은 품목이 없습니다.'); return; }
    let ok=0, fail=0;
    for(const c of CART){
        const body={ vendorIngredientId:c.vendorIngredientId, suggestedQty:c.qty, finalQty:c.qty, expirationDate:c.expirationDate };
        const r=await fetch('/api/order',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
        if(r.ok) ok++; else fail++;
    }
    alert('발주서 생성 완료: 성공 '+ok+'건'+(fail?(' / 실패 '+fail+'건'):''));
    if(ok){ CART=[]; renderCart(); location.href='/order-history'; }
}
$('addBtn').onclick=addItem; $('submitBtn').onclick=submit;
loadRefs();
</script>

<jsp:include page="../layout/footer.jsp" />
