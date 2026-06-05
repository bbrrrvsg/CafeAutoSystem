<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="거래처 등록" scope="request" />
<c:set var="menu" value="vendor" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input,.crud-form select{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">VENDOR · 등록</div>
        <h1>새 거래처를 등록합니다.</h1>
        <p class="hero-brief">거래처 정보와 함께 이 거래처가 공급하는 식자재·단가를 등록하면 우선순위가 자동 계산됩니다.</p>
    </div>
</section>

<!-- 1) 거래처 정보 -->
<div class="form-section crud-form">
    <h3>거래처 정보</h3>
    <div class="sec-sub">* 표시는 필수 항목입니다.</div>
    <div style="display:grid; grid-template-columns: repeat(3, 1fr); gap:16px; margin-top:8px;">
        <div><label>거래처명 *</label><input id="vName" type="text" placeholder="예) 서울원두유통"></div>
        <div><label>담당자 이메일 *</label><input id="vEmail" type="email" placeholder="예) seoul@coffee.com"></div>
        <div><label>담당자 연락처 *</label><input id="vPhone" type="text" placeholder="예) 02-111-2222"></div>
    </div>
</div>

<!-- 2) 공급 식자재 -->
<div class="form-section crud-form">
    <h3>공급 식자재 <span style="color:var(--text-muted); font-size:13px; font-weight:500;">(선택)</span></h3>
    <div class="sec-sub">이 거래처가 공급하는 식자재와 단가를 추가하세요. 거래처 저장 시 함께 등록됩니다.</div>
    <div style="display:grid; grid-template-columns: 2fr 1fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>식자재</label><select id="sIng"><option value="">— 선택 —</option></select></div>
        <div><label>공급 단가(원)</label><input id="sPrice" type="number" placeholder="2500"></div>
        <div><button id="addBtn" class="btn btn-secondary btn-sm">+ 추가</button></div>
    </div>

    <table class="data-table" style="margin-top:14px; border:1px solid var(--border-light); border-radius:8px;">
        <thead><tr><th>식자재</th><th>공급 단가</th><th class="text-right"></th></tr></thead>
        <tbody id="supplyBody"><tr><td colspan="3" class="text-muted">추가된 식자재가 없습니다.</td></tr></tbody>
    </table>
</div>

<div style="display:flex; gap:8px; justify-content:flex-end; margin-top:20px;">
    <a href="/vendor" class="btn btn-secondary">취소</a>
    <button id="submitBtn" class="btn btn-primary">등록하기</button>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
let INGS=[], SUPPLY=[];

async function loadIngredients(){
    try{ INGS = await (await fetch('/api/ingredient')).json(); }catch(e){ INGS=[]; }
    $('sIng').innerHTML='<option value="">— 선택 —</option>'+INGS.map(i=>'<option value="'+i.ingredientId+'">'+esc(i.ingredientName)+' ('+esc(i.unit)+')</option>').join('');
}
function addSupply(){
    const id=$('sIng').value, price=$('sPrice').value;
    if(!id){ alert('식자재를 선택하세요.'); return; }
    if(price===''||Number(price)<0){ alert('단가를 입력하세요.'); return; }
    if(SUPPLY.some(s=>s.ingredientId===Number(id))){ alert('이미 추가된 식자재입니다.'); return; }
    const ing=INGS.find(i=>String(i.ingredientId)===id);
    SUPPLY.push({ ingredientId:Number(id), name:ing.ingredientName, unitPrice:Number(price) });
    $('sPrice').value=''; renderSupply();
}
function renderSupply(){
    if(!SUPPLY.length){ $('supplyBody').innerHTML='<tr><td colspan="3" class="text-muted">추가된 식자재가 없습니다.</td></tr>'; return; }
    $('supplyBody').innerHTML=SUPPLY.map((s,i)=>
        '<tr><td><strong>'+esc(s.name)+'</strong></td>'+
        '<td class="num">'+s.unitPrice.toLocaleString()+'원</td>'+
        '<td class="text-right"><button class="danger" data-rm="'+i+'">×</button></td></tr>').join('');
    $('supplyBody').querySelectorAll('[data-rm]').forEach(b=>b.onclick=()=>{ SUPPLY.splice(Number(b.dataset.rm),1); renderSupply(); });
}
async function submit(){
    const body={ vendorName:$('vName').value.trim(), managerEmail:$('vEmail').value.trim(), managerPhone:$('vPhone').value.trim() };
    if(!body.vendorName||!body.managerEmail||!body.managerPhone){ alert('거래처명 · 이메일 · 연락처는 필수입니다.'); return; }

    // 1) 거래처 저장
    let vendorId;
    try{
        const r=await fetch('/api/vendor',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
        if(!r.ok){ alert('거래처 등록 실패 ('+r.status+')\n'+await r.text()); return; }
        vendorId=(await r.json()).vendorId;
    }catch(e){ alert('요청 실패: '+e.message); return; }

    // 2) 공급 식자재 매핑들 저장
    let ok=0, fail=0;
    for(const s of SUPPLY){
        const r=await fetch('/api/vendor-ingredient',{method:'POST',headers:{'Content-Type':'application/json'},
            body:JSON.stringify({ vendorId, ingredientId:s.ingredientId, unitPrice:s.unitPrice })});
        if(r.ok) ok++; else fail++;
    }
    alert('거래처 등록 완료'+(SUPPLY.length? (' · 공급 식자재 '+ok+'건'+(fail?(' / 실패 '+fail+'건'):'')) : ''));
    location.href='/vendor';
}
$('addBtn').onclick=addSupply; $('submitBtn').onclick=submit;
loadIngredients();
</script>

<jsp:include page="../layout/footer.jsp" />
