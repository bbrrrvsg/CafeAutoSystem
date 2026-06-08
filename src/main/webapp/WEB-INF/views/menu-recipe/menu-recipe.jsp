<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="레시피 관리" scope="request" />
<c:set var="menu" value="menu-recipe" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input,.crud-form select{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">RECIPE · 메뉴 레시피</div>
        <h1>메뉴 <span class="accent">레시피</span>를 관리합니다.</h1>
        <p class="hero-brief">메뉴 1개당 재료 소요량을 등록합니다. 판매 시 이 값으로 재고가 자동 차감됩니다. 소요량 단위는 선택한 재료의 단위를 따릅니다.</p>
    </div>
</section>

<div class="form-section crud-form">
    <h3 id="formTitle">레시피 등록</h3>
    <div style="display:grid; grid-template-columns: 1.4fr 1fr 1.4fr 1fr 1.4fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>메뉴명 *</label><input id="fMenu" type="text" placeholder="예) 카페 라떼"></div>
        <div><label>가격(원) *</label><input id="fPrice" type="number" placeholder="4500"></div>
        <div><label>재료 *</label><select id="fIng"><option value="">— 선택 —</option></select></div>
        <div><label>소요량 * <span id="unitHint" class="text-muted"></span></label><input id="fQty" type="number" placeholder="200"></div>
        <div><label>비고</label><input id="fNote" type="text" placeholder="선택"></div>
        <div style="display:flex; gap:6px;">
            <button id="saveBtn" class="btn btn-primary btn-sm">저장</button>
            <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">취소</button>
        </div>
    </div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th>ID</th><th>메뉴명</th><th>가격</th><th>재료</th><th>소요량</th><th>비고</th><th class="text-right">관리</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="7" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const API='/api/menu-recipe';
let editId=null, DATA=[], INGS=[];
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}

async function loadIngredients(){
    try{ INGS=await (await fetch('/api/ingredient')).json(); }catch(e){ INGS=[]; }
    $('fIng').innerHTML='<option value="">— 선택 —</option>'+INGS.map(i=>
        '<option value="'+i.ingredientId+'" data-unit="'+esc(i.unit)+'">'+esc(i.ingredientName)+' ('+esc(i.unit)+')</option>').join('');
}
$('fIng') && ($('fIng').onchange=()=>{
    const o=$('fIng').selectedOptions[0];
    $('unitHint').textContent = (o && o.dataset.unit)? '('+o.dataset.unit+')' : '';
});

async function load(){
    try{ DATA=await (await fetch(API)).json(); render(); }
    catch(e){ $('tbody').innerHTML='<tr><td colspan="7" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function render(){
    if(!DATA.length){ $('tbody').innerHTML='<tr><td colspan="7" class="text-muted">등록된 레시피가 없습니다.</td></tr>'; return; }
    $('tbody').innerHTML=DATA.map(x=>
        '<tr><td class="num">'+x.recipeId+'</td>'+
        '<td><strong>'+esc(x.menuName)+'</strong></td>'+
        '<td class="num">'+(x.price!=null?x.price.toLocaleString():'')+'원</td>'+
        '<td>'+esc(x.ingredientName||'—')+'</td>'+
        '<td class="num">'+x.requiredQuantity+' '+esc(x.ingredientUnit||'')+'</td>'+
        '<td class="text-muted">'+esc(x.note||'—')+'</td>'+
        '<td class="text-right"><div class="row-actions">'+
            '<button data-edit="'+x.recipeId+'">수정</button>'+
            '<button class="danger" data-del="'+x.recipeId+'">삭제</button>'+
        '</div></td></tr>').join('');
    $('tbody').querySelectorAll('[data-edit]').forEach(b=>b.onclick=()=>startEdit(Number(b.dataset.edit)));
    $('tbody').querySelectorAll('[data-del]').forEach(b=>b.onclick=()=>del(Number(b.dataset.del)));
}
function startEdit(id){
    const x=DATA.find(d=>d.recipeId===id); if(!x)return;
    editId=id; $('formTitle').textContent='레시피 수정 (ID '+id+')';
    $('fMenu').value=x.menuName||''; $('fPrice').value=x.price; $('fIng').value=x.ingredientId||'';
    $('fIng').onchange(); $('fQty').value=x.requiredQuantity; $('fNote').value=x.note||'';
    $('cancelBtn').style.display=''; window.scrollTo({top:0,behavior:'smooth'});
}
function resetForm(){
    editId=null; $('formTitle').textContent='레시피 등록';
    $('fMenu').value=''; $('fPrice').value=''; $('fIng').value=''; $('fQty').value=''; $('fNote').value=''; $('unitHint').textContent='';
    $('cancelBtn').style.display='none';
}
async function save(){
    const body={ menuName:$('fMenu').value.trim(), price:$('fPrice').value===''?null:Number($('fPrice').value),
        ingredientId:$('fIng').value===''?null:Number($('fIng').value),
        requiredQuantity:$('fQty').value===''?null:Number($('fQty').value), note:$('fNote').value.trim()||null };
    if(!body.menuName||body.price==null||!body.ingredientId||body.requiredQuantity==null){ alert('메뉴명·가격·재료·소요량은 필수입니다.'); return; }
    const url = editId? API+'/'+editId : API;
    const r=await fetch(url,{method: editId?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
    if(r.ok){ resetForm(); load(); } else { alert('저장 실패 ('+r.status+')\n'+await r.text()); }
}
async function del(id){
    if(!confirm('레시피 #'+id+' 삭제할까요?'))return;
    const r=await fetch(API+'/'+id,{method:'DELETE'});
    if(r.ok) load(); else alert('삭제 실패 ('+r.status+')');
}
$('saveBtn').onclick=save; $('cancelBtn').onclick=resetForm;
(async()=>{ await loadIngredients(); await load(); })();
</script>

<jsp:include page="../layout/footer.jsp" />
