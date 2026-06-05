<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="메뉴 관리" scope="request" />
<c:set var="menu" value="menu" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">MENU · 메뉴</div>
        <h1>판매 <span class="accent">메뉴</span>를 관리합니다.</h1>
        <p class="hero-brief">메뉴명과 판매가를 등록합니다. 메뉴별 재료 소요량은 레시피 관리에서 설정합니다.</p>
    </div>
</section>

<div class="form-section crud-form">
    <h3 id="formTitle">메뉴 등록</h3>
    <div style="display:grid; grid-template-columns: 2fr 1fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>메뉴명 *</label><input id="fName" type="text" placeholder="예) 카페 라떼"></div>
        <div><label>판매가(원) *</label><input id="fPrice" type="number" placeholder="4500"></div>
        <div style="display:flex; gap:6px;">
            <button id="saveBtn" class="btn btn-primary btn-sm">저장</button>
            <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">취소</button>
        </div>
    </div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th>ID</th><th>메뉴명</th><th>판매가</th><th class="text-right">관리</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="4" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const API='/api/menu';
let editId=null, DATA=[];
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}

async function load(){
    try{ DATA=await (await fetch(API)).json(); render(); }
    catch(e){ $('tbody').innerHTML='<tr><td colspan="4" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function render(){
    if(!DATA.length){ $('tbody').innerHTML='<tr><td colspan="4" class="text-muted">등록된 메뉴가 없습니다.</td></tr>'; return; }
    $('tbody').innerHTML=DATA.map(x=>
        '<tr><td class="num">'+x.menuId+'</td>'+
        '<td><strong>'+esc(x.menuName)+'</strong></td>'+
        '<td class="num">'+(x.menuPrice!=null?x.menuPrice.toLocaleString():'')+'원</td>'+
        '<td class="text-right"><div class="row-actions">'+
            '<button data-edit="'+x.menuId+'">수정</button>'+
            '<button class="danger" data-del="'+x.menuId+'">삭제</button>'+
        '</div></td></tr>').join('');
    $('tbody').querySelectorAll('[data-edit]').forEach(b=>b.onclick=()=>startEdit(Number(b.dataset.edit)));
    $('tbody').querySelectorAll('[data-del]').forEach(b=>b.onclick=()=>del(Number(b.dataset.del)));
}
function startEdit(id){
    const x=DATA.find(d=>d.menuId===id); if(!x)return;
    editId=id; $('formTitle').textContent='메뉴 수정 (ID '+id+')';
    $('fName').value=x.menuName||''; $('fPrice').value=x.menuPrice;
    $('cancelBtn').style.display=''; window.scrollTo({top:0,behavior:'smooth'});
}
function resetForm(){
    editId=null; $('formTitle').textContent='메뉴 등록';
    $('fName').value=''; $('fPrice').value=''; $('cancelBtn').style.display='none';
}
async function save(){
    const body={ menuName:$('fName').value.trim(), menuPrice:$('fPrice').value===''?null:Number($('fPrice').value) };
    if(!body.menuName||body.menuPrice==null){ alert('메뉴명·판매가는 필수입니다.'); return; }
    const url = editId? API+'/'+editId : API;
    const r=await fetch(url,{method: editId?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
    if(r.ok){ resetForm(); load(); } else { alert('저장 실패 ('+r.status+')\n'+await r.text()); }
}
async function del(id){
    if(!confirm('메뉴 #'+id+' 삭제할까요?'))return;
    const r=await fetch(API+'/'+id,{method:'DELETE'});
    if(r.ok) load(); else alert('삭제 실패 ('+r.status+')');
}
$('saveBtn').onclick=save; $('cancelBtn').onclick=resetForm;
load();
</script>

<jsp:include page="../layout/footer.jsp" />
