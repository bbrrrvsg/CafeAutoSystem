<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="메뉴 & 레시피" scope="request" />
<c:set var="menu" value="menu" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.crud-form label{display:block;font-size:11.5px;color:var(--text-muted);font-weight:600;margin-bottom:5px;}
.crud-form input,.crud-form select{width:100%;padding:8px 11px;border:1px solid var(--border);border-radius:8px;font-size:13px;background:var(--bg-content);}
.recipe-chip{display:inline-block;background:var(--primary-soft);border:1px solid var(--border-light);border-radius:6px;padding:2px 8px;font-size:12px;margin:2px 3px 2px 0;}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">MENU · 메뉴 & 레시피</div>
        <h1>메뉴와 <span class="accent">레시피</span>를 함께 관리합니다.</h1>
        <p class="hero-brief">메뉴(이름·가격)와 그 메뉴에 들어가는 재료(소요량)를 한 번에 등록합니다. 판매 시 이 레시피로 재고가 자동 차감됩니다.</p>
    </div>
</section>

<div class="form-section crud-form">
    <h3 id="formTitle">메뉴 등록</h3>
    <div style="display:grid; grid-template-columns: 2fr 1fr auto; gap:12px; align-items:end; margin-top:8px;">
        <div><label>메뉴명 *</label><input id="fMenu" type="text" placeholder="예) 카페 라떼"></div>
        <div><label>판매가(원) *</label><input id="fPrice" type="number" placeholder="4500"></div>
        <div>
            <label>사진</label>
            <div style="display:flex; align-items:center; gap:8px;">
                <img id="fImgPreview" src="" alt="" style="width:44px;height:44px;object-fit:cover;border-radius:8px;border:1px solid var(--border);display:none;">
                <input id="fImgFile" type="file" accept="image/*" style="display:none;">
                <button type="button" id="fImgBtn" class="btn btn-secondary btn-sm">사진 선택</button>
                <button type="button" id="fImgClear" class="btn btn-secondary btn-sm" style="display:none;">제거</button>
            </div>
        </div>
    </div>

    <!-- 재료(레시피) -->
    <div style="margin-top:16px;">
        <label>재료 (레시피) *</label>
        <div style="display:grid; grid-template-columns: 2fr 1.2fr auto; gap:10px; align-items:end;">
            <div><select id="fIng"><option value="">— 재료 선택 —</option></select></div>
            <div><input id="fQty" type="number" placeholder="소요량"> </div>
            <div><button id="addLineBtn" type="button" class="btn btn-secondary btn-sm">+ 재료 추가</button></div>
        </div>
        <table class="data-table" style="margin-top:10px; border:1px solid var(--border-light); border-radius:8px;">
            <thead><tr><th>재료</th><th style="width:140px;">소요량</th><th class="text-right" style="width:60px;"></th></tr></thead>
            <tbody id="linesBody"><tr><td colspan="3" class="text-muted">추가된 재료가 없습니다.</td></tr></tbody>
        </table>
    </div>

    <div style="display:flex; gap:6px; margin-top:14px; justify-content:flex-end;">
        <button id="saveBtn" class="btn btn-primary btn-sm">저장</button>
        <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">취소</button>
    </div>
</div>

<div class="card flush">
    <table class="data-table">
        <thead><tr><th style="width:56px;">사진</th><th style="width:180px;">메뉴명</th><th style="width:110px;">판매가</th><th>재료 (레시피)</th><th class="text-right" style="width:120px;">관리</th></tr></thead>
        <tbody id="menuBody"><tr><td colspan="5" class="text-muted">불러오는 중...</td></tr></tbody>
    </table>
</div>

<script>
const $=id=>document.getElementById(id);
function esc(s){return (s==null?'':String(s)).replace(/[&<>"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));}
let INGS=[], UNIT={}, MENUS=[], RECIPES=[], LINES=[], editMenuId=null, editRecipeIds=[], MENU_IMG=null;

async function loadIngredients(){
    try{ INGS=await (await fetch('/api/ingredient')).json(); }catch(e){ INGS=[]; }
    INGS.forEach(i=>UNIT[i.ingredientId]=i.unit);
    $('fIng').innerHTML='<option value="">— 재료 선택 —</option>'+INGS.map(i=>'<option value="'+i.ingredientId+'">'+esc(i.ingredientName)+' ('+esc(i.unit)+')</option>').join('');
}
function recipesOf(name){ return RECIPES.filter(r=>r.menuName===name); }

async function load(){
    try{
        const [m,r]=await Promise.all([fetch('/api/menu').then(x=>x.json()), fetch('/api/menu-recipe').then(x=>x.json())]);
        MENUS=m; RECIPES=r; renderMenus();
    }catch(e){ $('menuBody').innerHTML='<tr><td colspan="4" class="text-muted">불러오기 실패: '+e.message+'</td></tr>'; }
}
function renderMenus(){
    if(!MENUS.length){ $('menuBody').innerHTML='<tr><td colspan="5" class="text-muted">등록된 메뉴가 없습니다.</td></tr>'; return; }
    $('menuBody').innerHTML=MENUS.map(m=>{
        const rs=recipesOf(m.menuName);
        const chips = rs.length? rs.map(r=>'<span class="recipe-chip">'+esc(r.ingredientName)+' '+r.requiredQuantity+esc(r.ingredientUnit||'')+'</span>').join('') : '<span class="text-muted">재료 없음</span>';
        const img = m.menuImage? '<img src="'+esc(m.menuImage)+'" style="width:40px;height:40px;object-fit:cover;border-radius:6px;border:1px solid var(--border);">' : '<span class="text-muted">—</span>';
        return '<tr><td>'+img+'</td><td><strong>'+esc(m.menuName)+'</strong></td>'+
            '<td class="num">'+(m.menuPrice!=null?m.menuPrice.toLocaleString():'')+'원</td>'+
            '<td>'+chips+'</td>'+
            '<td class="text-right"><div class="row-actions">'+
                '<button data-edit="'+m.menuId+'">수정</button>'+
                '<button class="danger" data-del="'+m.menuId+'">삭제</button>'+
            '</div></td></tr>';
    }).join('');
    $('menuBody').querySelectorAll('[data-edit]').forEach(b=>b.onclick=()=>startEdit(Number(b.dataset.edit)));
    $('menuBody').querySelectorAll('[data-del]').forEach(b=>b.onclick=()=>delMenu(Number(b.dataset.del)));
}

// ----- 폼 재료 줄 -----
function addLine(){
    const id=$('fIng').value, qty=$('fQty').value;
    if(!id){ alert('재료를 선택하세요.'); return; }
    if(qty===''||Number(qty)<=0){ alert('소요량은 1 이상이어야 합니다.'); return; }
    if(LINES.some(l=>l.ingredientId===Number(id))){ alert('이미 추가된 재료입니다.'); return; }
    const ing=INGS.find(i=>String(i.ingredientId)===id);
    LINES.push({ ingredientId:Number(id), name:ing.ingredientName, unit:ing.unit, qty:Number(qty) });
    $('fQty').value=''; renderLines();
}
function renderLines(){
    if(!LINES.length){ $('linesBody').innerHTML='<tr><td colspan="3" class="text-muted">추가된 재료가 없습니다.</td></tr>'; return; }
    $('linesBody').innerHTML=LINES.map((l,i)=>
        '<tr><td><strong>'+esc(l.name)+'</strong></td>'+
        '<td class="num">'+l.qty.toLocaleString()+' '+esc(l.unit||'')+'</td>'+
        '<td class="text-right"><button class="danger" data-rm="'+i+'">×</button></td></tr>').join('');
    $('linesBody').querySelectorAll('[data-rm]').forEach(b=>b.onclick=()=>{ LINES.splice(Number(b.dataset.rm),1); renderLines(); });
}

function startEdit(menuId){
    const m=MENUS.find(x=>x.menuId===menuId); if(!m)return;
    editMenuId=menuId; $('formTitle').textContent='메뉴 수정 (ID '+menuId+')';
    $('fMenu').value=m.menuName||''; $('fPrice').value=m.menuPrice;
    MENU_IMG=m.menuImage||null; showImg(MENU_IMG);
    const rs=recipesOf(m.menuName);
    editRecipeIds=rs.map(r=>r.recipeId);
    LINES=rs.map(r=>({ ingredientId:r.ingredientId, name:r.ingredientName, unit:r.ingredientUnit, qty:r.requiredQuantity }));
    renderLines();
    $('cancelBtn').style.display=''; window.scrollTo({top:0,behavior:'smooth'});
}
function resetForm(){
    editMenuId=null; editRecipeIds=[]; LINES=[]; $('formTitle').textContent='메뉴 등록';
    $('fMenu').value=''; $('fPrice').value=''; $('fIng').value=''; $('fQty').value=''; MENU_IMG=null; showImg(null); renderLines();
    $('cancelBtn').style.display='none';
}
async function save(){
    const menuName=$('fMenu').value.trim(), price=$('fPrice').value===''?null:Number($('fPrice').value);
    if(!menuName||price==null){ alert('메뉴명·판매가는 필수입니다.'); return; }
    if(!LINES.length){ alert('재료(레시피)를 1개 이상 추가하세요.'); return; }
    try{
        let menuId=editMenuId;
        if(editMenuId){
            await fetch('/api/menu/'+editMenuId,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({menuName,menuPrice:price,menuImage:MENU_IMG})});
            for(const rid of editRecipeIds){ await fetch('/api/menu-recipe/'+rid,{method:'DELETE'}); } // 기존 레시피 줄 교체
        }else{
            const mr=await (await fetch('/api/menu',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({menuName,menuPrice:price,menuImage:MENU_IMG})})).json();
            menuId=mr.menuId;
        }
        for(const l of LINES){
            await fetch('/api/menu-recipe',{method:'POST',headers:{'Content-Type':'application/json'},
                body:JSON.stringify({ menuName, price, ingredientId:l.ingredientId, requiredQuantity:l.qty })});
        }
        resetForm(); load();
    }catch(e){ alert('저장 실패: '+e.message); }
}
async function delMenu(menuId){
    const m=MENUS.find(x=>x.menuId===menuId); if(!m)return;
    if(!confirm('메뉴 "'+m.menuName+'" 와 그 레시피를 모두 삭제할까요?'))return;
    for(const r of recipesOf(m.menuName)){ await fetch('/api/menu-recipe/'+r.recipeId,{method:'DELETE'}); }
    await fetch('/api/menu/'+menuId,{method:'DELETE'});
    load();
}

function showImg(url){ const p=$('fImgPreview'); if(url){ p.src=url; p.style.display=''; $('fImgClear').style.display=''; } else { p.removeAttribute('src'); p.style.display='none'; $('fImgClear').style.display='none'; } }
$('fImgBtn').onclick=()=>$('fImgFile').click();
$('fImgClear').onclick=()=>{ MENU_IMG=null; showImg(null); };
$('fImgFile').onchange=async()=>{
    const f=$('fImgFile').files[0]; if(!f) return;
    const fd=new FormData(); fd.append('file', f);
    try{
        const r=await fetch('/api/upload',{method:'POST',body:fd});
        const j=await r.json();
        if(r.ok && j.url){ MENU_IMG=j.url; showImg(j.url); } else alert('업로드 실패: '+(j.error||r.status));
    }catch(e){ alert('업로드 실패: '+e.message); }
    $('fImgFile').value='';
};
$('addLineBtn').onclick=addLine;
$('saveBtn').onclick=save; $('cancelBtn').onclick=resetForm;
(async()=>{ await loadIngredients(); await load(); })();
</script>

<jsp:include page="../layout/footer.jsp" />
