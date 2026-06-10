<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="식자재 관리" scope="request" />
<c:set var="menu" value="ingredient" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:12px;color:var(--text-muted);font-weight:600;margin-bottom:6px;}
.crud-form input,.crud-form select{width:100%;padding:9px 12px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">INGREDIENTS · 식자재</div>
        <h1>식자재를 <span class="accent">관리</span>합니다.</h1>
        <p class="hero-brief">재고·발주·레시피를 <b>하나의 단위</b>(ml·g·개)로 관리합니다.</p>
    </div>
</section>

<!-- 등록/수정 폼 -->
<div class="form-section crud-form">
    <h3 id="formTitle">식자재 등록</h3>
    <div style="display:grid; grid-template-columns: 2fr 1fr 1fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>식자재명 *</label><input id="fName" type="text" placeholder="예) 신선한 우유"></div>
        <div><label>단위 * <span class="text-muted">(계산 기준)</span></label>
            <select id="fUnit"><option value="">— 선택 —</option><option value="ml">ml (밀리리터)</option><option value="g">g (그램)</option><option value="개">개</option><option value="pack">pack (팩/봉)</option></select>
        </div>
        <div><label>안전재고 *</label><input id="fSafety" type="number" placeholder="5000"></div>
        <div><label>사진</label>
            <div style="display:flex; align-items:center; gap:8px;">
                <img id="fImgPreview" src="" alt="" onerror="this.style.display='none'" style="width:40px;height:40px;object-fit:cover;border-radius:8px;border:1px solid var(--border);display:none;">
                <input id="fImgFile" type="file" accept="image/*" style="display:none;">
                <button type="button" id="fImgBtn" class="btn btn-secondary btn-sm">사진 선택</button>
                <button type="button" id="fImgClear" class="btn btn-secondary btn-sm" style="display:none;">제거</button>
            </div>
        </div>
    </div>

    <div style="display:flex; gap:6px; margin-top:14px; justify-content:flex-end;">
        <button id="saveBtn" class="btn btn-primary btn-sm">저장</button>
        <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">취소</button>
    </div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th style="width:48px;">사진</th><th>ID</th><th>식자재명</th><th>단위</th><th>안전재고</th><th class="text-right">관리</th></tr></thead>
        <tbody id="tbody"><tr><td colspan="6" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const API='/api/ingredient';
let editId=null, DATA=[], ING_IMG=null;
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}

async function load(){
    try{ const r=await fetch(API); DATA=await r.json(); render(); }
    catch(e){ $('tbody').innerHTML='<tr><td colspan="6" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function render(){
    if(!DATA.length){ $('tbody').innerHTML='<tr><td colspan="6" class="text-muted">등록된 식자재가 없습니다.</td></tr>'; return; }
    $('tbody').innerHTML=DATA.map(x=>
        '<tr><td>'+(x.ingredientImage?'<img src="'+esc(x.ingredientImage)+'" onerror="this.style.display=\'none\'" style="width:36px;height:36px;object-fit:cover;border-radius:6px;border:1px solid var(--border);">':'<span class="text-muted">—</span>')+'</td>'+
        '<td class="num">'+x.ingredientId+'</td>'+
        '<td><strong>'+esc(x.ingredientName)+'</strong></td>'+
        '<td>'+esc(x.unit)+'</td>'+
        '<td class="num">'+x.safetyStock+'</td>'+
        '<td class="text-right"><div class="row-actions">'+
            '<button data-edit="'+x.ingredientId+'">수정</button>'+
            '<button class="danger" data-del="'+x.ingredientId+'">삭제</button>'+
        '</div></td></tr>').join('');
    $('tbody').querySelectorAll('[data-edit]').forEach(b=>b.onclick=()=>startEdit(Number(b.dataset.edit)));
    $('tbody').querySelectorAll('[data-del]').forEach(b=>b.onclick=()=>del(Number(b.dataset.del)));
}
function startEdit(id){
    const x=DATA.find(d=>d.ingredientId===id); if(!x)return;
    editId=id; $('formTitle').textContent='식자재 수정 (ID '+id+')';
    $('fName').value=x.ingredientName||''; $('fUnit').value=x.unit||''; $('fSafety').value=x.safetyStock;
    ING_IMG=x.ingredientImage||null; showImg(ING_IMG);
    $('cancelBtn').style.display=''; window.scrollTo({top:0,behavior:'smooth'});
}
function resetForm(){
    editId=null; $('formTitle').textContent='식자재 등록';
    $('fName').value=''; $('fUnit').value=''; $('fSafety').value=''; ING_IMG=null; showImg(null);
    $('cancelBtn').style.display='none';
}
async function save(){
    const body={ ingredientName:$('fName').value.trim(), unit:$('fUnit').value.trim(),
        safetyStock: $('fSafety').value===''?null:Number($('fSafety').value), ingredientImage:ING_IMG };
    if(!body.ingredientName||!body.unit||body.safetyStock==null){ alert('식자재명·단위·안전재고는 필수입니다.'); return; }
    const url = editId? API+'/'+editId : API;
    const r=await fetch(url,{method: editId?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
    if(r.ok){ resetForm(); load(); } else { alert('저장 실패 ('+r.status+')\n'+await r.text()); }
}
async function del(id){
    if(!confirm('식자재 #'+id+' 삭제할까요?'))return;
    const r=await fetch(API+'/'+id,{method:'DELETE'});
    if(r.ok) load(); else alert('삭제 실패 ('+r.status+') — 발주/재고/레시피가 참조 중일 수 있습니다.');
}

function showImg(url){ const p=$('fImgPreview'); if(url){ p.src=url; p.style.display=''; $('fImgClear').style.display=''; } else { p.removeAttribute('src'); p.style.display='none'; $('fImgClear').style.display='none'; } }
$('fImgBtn').onclick=()=>$('fImgFile').click();
$('fImgClear').onclick=()=>{ ING_IMG=null; showImg(null); };
$('fImgFile').onchange=async()=>{
    const f=$('fImgFile').files[0]; if(!f) return;
    const fd=new FormData(); fd.append('file', f);
    try{
        const r=await fetch('/api/upload',{method:'POST',body:fd});
        const j=await r.json();
        if(r.ok && j.url){ ING_IMG=j.url; showImg(j.url); } else alert('업로드 실패: '+(j.error||r.status));
    }catch(e){ alert('업로드 실패: '+e.message); }
    $('fImgFile').value='';
};
$('saveBtn').onclick=save; $('cancelBtn').onclick=resetForm;
load();
</script>

<jsp:include page="../layout/footer.jsp" />
