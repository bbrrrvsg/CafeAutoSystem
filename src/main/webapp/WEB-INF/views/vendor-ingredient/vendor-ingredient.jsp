<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="거래처-식자재 매핑" scope="request" />
<c:set var="menu" value="vendor-ingredient" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input,.crud-form select{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
.rank-badge{display:inline-block;min-width:20px;padding:2px 8px;border-radius:20px;font-weight:700;font-size:12px;background:var(--bg-content);}
.rank-1{background:#e6f7ee;color:#1a7f4b;}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">VENDOR · INGREDIENT 매핑</div>
        <h1>거래처별 <span class="accent">공급 단가</span>를 관리합니다.</h1>
        <p class="hero-brief">같은 식자재를 공급하는 거래처와 단가를 등록하면, 단가가 싼 순으로 1·2·3순위가 자동 계산됩니다.</p>
    </div>
</section>

<div class="form-section crud-form">
    <h3 id="formTitle">매핑 등록</h3>
    <div style="display:grid; grid-template-columns: 1.6fr 1.6fr 1fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>식자재 *</label><select id="fIng"><option value="">— 선택 —</option></select></div>
        <div><label>거래처 *</label><select id="fVen"><option value="">— 선택 —</option></select></div>
        <div><label>공급 단가(원) *</label><input id="fPrice" type="number" placeholder="2500"></div>
        <div style="display:flex; gap:6px;">
            <button id="saveBtn" class="btn btn-primary btn-sm">저장</button>
            <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">취소</button>
        </div>
    </div>
</div>

<div class="toolbar">
    <label style="font-size:13px; color:var(--text-muted);">식자재 필터</label>
    <select id="filterIng" style="padding:8px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content);">
        <option value="">전체</option>
    </select>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th>순위</th><th>식자재</th><th>거래처</th><th>공급 단가</th><th>ID</th><th class="text-right">관리</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="6" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const API='/api/vendor-ingredient';
let editId=null, DATA=[], INGS=[], VENS=[];
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}

async function loadRefs(){
    try{ INGS=await (await fetch('/api/ingredient')).json(); }catch(e){ INGS=[]; }
    try{ VENS=await (await fetch('/api/vendor')).json(); }catch(e){ VENS=[]; }
    $('fIng').innerHTML='<option value="">— 선택 —</option>'+INGS.map(i=>'<option value="'+i.ingredientId+'">'+esc(i.ingredientName)+'</option>').join('');
    $('filterIng').innerHTML='<option value="">전체</option>'+INGS.map(i=>'<option value="'+i.ingredientId+'">'+esc(i.ingredientName)+'</option>').join('');
    $('fVen').innerHTML='<option value="">— 선택 —</option>'+VENS.map(v=>'<option value="'+v.vendorId+'">'+esc(v.vendorName)+'</option>').join('');
}

async function load(){
    try{ DATA=await (await fetch(API)).json(); render(); }
    catch(e){ $('tbody').innerHTML='<tr><td colspan="6" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function render(){
    const f=$('filterIng').value;
    let list = f? DATA.filter(x=>String(x.ingredientId)===f) : DATA.slice();
    // 식자재별 → 순위순 보기 좋게 정렬
    list.sort((a,b)=> (a.ingredientId-b.ingredientId) || (a.priorityRank-b.priorityRank));
    if(!list.length){ $('tbody').innerHTML='<tr><td colspan="6" class="text-muted">매핑이 없습니다.</td></tr>'; return; }
    $('tbody').innerHTML=list.map(x=>
        '<tr><td><span class="rank-badge rank-'+x.priorityRank+'">'+x.priorityRank+'</span></td>'+
        '<td>'+esc(x.ingredientName||'')+'</td>'+
        '<td><strong>'+esc(x.vendorName||'')+'</strong></td>'+
        '<td class="num">'+(x.unitPrice!=null?x.unitPrice.toLocaleString():'')+'원</td>'+
        '<td class="num">'+x.vendorIngredientId+'</td>'+
        '<td class="text-right"><div class="row-actions">'+
            '<button data-edit="'+x.vendorIngredientId+'">단가수정</button>'+
            '<button class="danger" data-del="'+x.vendorIngredientId+'">삭제</button>'+
        '</div></td></tr>').join('');
    $('tbody').querySelectorAll('[data-edit]').forEach(b=>b.onclick=()=>startEdit(Number(b.dataset.edit)));
    $('tbody').querySelectorAll('[data-del]').forEach(b=>b.onclick=()=>del(Number(b.dataset.del)));
}
function startEdit(id){
    const x=DATA.find(d=>d.vendorIngredientId===id); if(!x)return;
    editId=id; $('formTitle').textContent='단가 수정 (ID '+id+')';
    $('fIng').value=x.ingredientId||''; $('fVen').value=x.vendorId||''; $('fPrice').value=x.unitPrice;
    $('fIng').disabled=true; $('fVen').disabled=true; // 수정은 단가만
    $('cancelBtn').style.display=''; window.scrollTo({top:0,behavior:'smooth'});
}
function resetForm(){
    editId=null; $('formTitle').textContent='매핑 등록';
    $('fIng').value=''; $('fVen').value=''; $('fPrice').value='';
    $('fIng').disabled=false; $('fVen').disabled=false;
    $('cancelBtn').style.display='none';
}
async function save(){
    if(editId){
        const price=$('fPrice').value===''?null:Number($('fPrice').value);
        if(price==null){ alert('단가는 필수입니다.'); return; }
        const r=await fetch(API+'/'+editId,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({unitPrice:price})});
        if(r.ok){ resetForm(); load(); } else alert('수정 실패 ('+r.status+')\n'+await r.text());
        return;
    }
    const body={ ingredientId:$('fIng').value===''?null:Number($('fIng').value),
        vendorId:$('fVen').value===''?null:Number($('fVen').value),
        unitPrice:$('fPrice').value===''?null:Number($('fPrice').value) };
    if(!body.ingredientId||!body.vendorId||body.unitPrice==null){ alert('식자재·거래처·단가는 필수입니다.'); return; }
    const r=await fetch(API,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
    if(r.ok){ resetForm(); load(); } else alert('등록 실패 ('+r.status+')\n'+await r.text());
}
async function del(id){
    if(!confirm('매핑 #'+id+' 삭제할까요? (해당 식자재 순위가 재정렬됩니다)'))return;
    const r=await fetch(API+'/'+id,{method:'DELETE'});
    if(r.ok) load(); else alert('삭제 실패 ('+r.status+')');
}
$('saveBtn').onclick=save; $('cancelBtn').onclick=resetForm;
$('filterIng').onchange=render;
(async()=>{ await loadRefs(); await load(); })();
</script>

<jsp:include page="../layout/footer.jsp" />
