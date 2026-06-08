<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="거래처 관리" scope="request" />
<c:set var="menu" value="vendor" scope="request" />
<jsp:include page="../layout/header.jsp" />

<!-- Hero -->
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">VENDORS · 거래처 <span id="heroCount">0</span>곳</div>
        <h1>거래처를 <span class="accent">관리</span>합니다.</h1>
        <p class="hero-brief">
            등록된 거래처 목록입니다. 행을 클릭하면 우측에서 상세 정보를 볼 수 있어요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">전체 거래처</div>
        <div class="time"><span id="heroCount2">0</span></div>
    </div>
</section>

<!-- 미니 통계 -->
<div class="mini-stats">
    <div class="mini-stat accent">
        <div class="ms-label">전체 거래처</div>
        <div class="ms-value"><span id="statTotal">0</span><span class="unit">곳</span></div>
    </div>
</div>

<!-- 툴바 -->
<div class="toolbar">
    <div class="search-input">
        <input type="text" id="searchInput" placeholder="거래처명, 담당자, 이메일 검색...">
    </div>
    <div class="toolbar-spacer"></div>
    <a href="/vendor/register" class="btn btn-primary btn-sm">+ 거래처 등록</a>
</div>

<!-- 좌 테이블 / 우 상세 패널 -->
<div class="split-layout">

    <div class="card flush">
        <table class="data-table" id="vendorTable">
            <thead>
                <tr>
                    <th>거래처명</th>
                    <th>이메일</th>
                    <th>연락처</th>
                    <th>ID</th>
                    <th class="text-right">관리</th>
                </tr>
            </thead>
            <tbody id="vendorTbody">
                <tr><td colspan="5" class="text-muted">불러오는 중...</td></tr>
            </tbody>
        </table>
    </div>

    <!-- 상세 패널 -->
    <aside class="detail-panel" id="detailPanel">
        <div class="panel-head">
            <div>
                <div class="panel-title" id="dpName">거래처를 선택하세요</div>
                <div class="panel-sub" id="dpSub">—</div>
            </div>
            <div style="display:flex; gap:4px;">
                <button class="panel-close" title="거래처 정보 수정" onclick="startVendorEdit()" style="font-size:14px;">✎</button>
                <button class="panel-close" title="닫기" onclick="document.getElementById('detailPanel').style.display='none'">×</button>
            </div>
        </div>

        <div class="detail-list" id="vViewBox">
            <div class="detail-row"><span class="key">거래처명</span><span class="val" id="dpVendorName">—</span></div>
            <div class="detail-row"><span class="key">담당자 이메일</span><span class="val" id="dpEmail">—</span></div>
            <div class="detail-row"><span class="key">담당자 연락처</span><span class="val num" id="dpPhone">—</span></div>
            <div class="detail-row"><span class="key">등록일</span><span class="val" id="dpCreated">—</span></div>
            <div class="detail-row"><span class="key">최근 수정</span><span class="val" id="dpUpdated">—</span></div>
        </div>

        <!-- 인라인 수정 폼 -->
        <div class="detail-list" id="vEditBox" style="display:none;">
            <div class="detail-row"><span class="key">거래처명</span><input id="evName" style="flex:1;text-align:right;border:1px solid var(--border);border-radius:6px;padding:4px 8px;font-size:13px;background:var(--bg-content);"></div>
            <div class="detail-row"><span class="key">담당자 이메일</span><input id="evEmail" style="flex:1;text-align:right;border:1px solid var(--border);border-radius:6px;padding:4px 8px;font-size:13px;background:var(--bg-content);"></div>
            <div class="detail-row"><span class="key">담당자 연락처</span><input id="evPhone" style="flex:1;text-align:right;border:1px solid var(--border);border-radius:6px;padding:4px 8px;font-size:13px;background:var(--bg-content);"></div>
            <div style="display:flex;gap:6px;justify-content:flex-end;margin-top:10px;">
                <button class="btn btn-primary btn-sm" onclick="saveVendor()">저장</button>
                <button class="btn btn-secondary btn-sm" onclick="cancelVendorEdit()">취소</button>
            </div>
        </div>

        <div class="detail-section">
            <h5>공급 식자재</h5>
            <div id="dpSupply" class="mini-list"><div class="text-muted">거래처를 선택하세요</div></div>
        </div>
    </aside>

</div>

<script>
    const TBODY   = document.getElementById('vendorTbody');
    let VENDORS = [];

    async function loadVendors() {
        try {
            const res = await fetch('/api/vendor');
            if (!res.ok) throw new Error('HTTP ' + res.status);
            VENDORS = await res.json();
            render(VENDORS);
        } catch (e) {
            TBODY.innerHTML = '<tr><td colspan="5" class="text-muted">불러오기 실패: ' + e.message + '</td></tr>';
        }
    }

    function render(list) {
        // 통계
        ['statTotal','heroCount','heroCount2'].forEach(id => {
            const el = document.getElementById(id); if (el) el.textContent = list.length;
        });

        if (!list.length) {
            TBODY.innerHTML = '<tr><td colspan="5" class="text-muted">등록된 거래처가 없습니다.</td></tr>';
            return;
        }
        TBODY.innerHTML = list.map(v =>
            '<tr class="clickable" data-id="' + v.vendorId + '">' +
              '<td><strong>' + esc(v.vendorName) + '</strong></td>' +
              '<td class="text-muted">' + esc(v.managerEmail) + '</td>' +
              '<td class="num">' + esc(v.managerPhone) + '</td>' +
              '<td class="num">' + v.vendorId + '</td>' +
              '<td class="text-right"><div class="row-actions">' +
                '<button data-edit="' + v.vendorId + '">수정</button>' +
                '<button class="danger" data-del="' + v.vendorId + '">삭제</button>' +
              '</div></td>' +
            '</tr>'
        ).join('');

        // 행 클릭 → 상세
        TBODY.querySelectorAll('tr.clickable').forEach(tr => {
            tr.addEventListener('click', () => showDetail(Number(tr.dataset.id)));
        });
        // 수정 버튼 → 상세 + 편집모드
        TBODY.querySelectorAll('button[data-edit]').forEach(btn => {
            btn.addEventListener('click', (e) => { e.stopPropagation(); showDetail(Number(btn.dataset.edit)); startVendorEdit(); });
        });
        // 삭제 버튼
        TBODY.querySelectorAll('button[data-del]').forEach(btn => {
            btn.addEventListener('click', (e) => { e.stopPropagation(); del(Number(btn.dataset.del)); });
        });
    }

    let curVendorId = null;
    function showDetail(id) {
        const v = VENDORS.find(x => x.vendorId === id);
        if (!v) return;
        curVendorId = id;
        document.getElementById('detailPanel').style.display = '';
        document.getElementById('dpName').textContent = v.vendorName;
        document.getElementById('dpSub').textContent = 'ID #' + v.vendorId;
        document.getElementById('dpVendorName').textContent = v.vendorName;
        document.getElementById('dpEmail').textContent = v.managerEmail || '—';
        document.getElementById('dpPhone').textContent = v.managerPhone || '—';
        document.getElementById('dpCreated').textContent = (v.createdAt || '—');
        document.getElementById('dpUpdated').textContent = (v.updatedAt || '—');
        loadSupply(id);
    }

    async function loadSupply(vendorId){
        const box=document.getElementById('dpSupply');
        box.innerHTML='<div class="text-muted">불러오는 중...</div>';
        try{
            const all=await (await fetch('/api/vendor-ingredient')).json();
            const mine=all.filter(m=>m.vendorId===vendorId);
            box.innerHTML = mine.length ? mine.map(m=>
                '<div class="mini-list-item">'+
                  '<span class="name">'+esc(m.ingredientName)+'</span>'+
                  '<span class="val">'+(m.unitPrice||0).toLocaleString()+'원 '+
                    '<button class="btn btn-secondary btn-sm" style="padding:2px 8px;" onclick="editSupply('+m.vendorIngredientId+','+(m.unitPrice||0)+')">수정</button> '+
                    '<button class="btn btn-secondary btn-sm danger" style="padding:2px 8px;" onclick="delSupply('+m.vendorIngredientId+')">삭제</button>'+
                  '</span>'+
                '</div>').join('') : '<div class="text-muted">공급 식자재 없음</div>';
        }catch(e){ box.innerHTML='<div class="text-muted">불러오기 실패</div>'; }
    }
    async function editSupply(id, cur){
        const val=prompt('새 공급 단가(원):', cur);
        if(val===null) return;
        const price=Number(val);
        if(isNaN(price)||price<0){ alert('단가가 올바르지 않습니다.'); return; }
        const r=await fetch('/api/vendor-ingredient/'+id,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({unitPrice:price})});
        if(r.ok){ loadSupply(curVendorId); } else alert('수정 실패 ('+r.status+')');
    }
    async function delSupply(id){
        if(!confirm('이 공급 식자재 매핑을 삭제할까요?')) return;
        const r=await fetch('/api/vendor-ingredient/'+id,{method:'DELETE'});
        if(r.ok){ loadSupply(curVendorId); } else alert('삭제 실패 ('+r.status+')');
    }

    // ----- 거래처 정보 수정 -----
    function startVendorEdit(){
        if(curVendorId==null) return;
        const v=VENDORS.find(x=>x.vendorId===curVendorId); if(!v) return;
        document.getElementById('evName').value  = v.vendorName  || '';
        document.getElementById('evEmail').value = v.managerEmail || '';
        document.getElementById('evPhone').value = v.managerPhone || '';
        document.getElementById('vViewBox').style.display='none';
        document.getElementById('vEditBox').style.display='';
    }
    function cancelVendorEdit(){
        document.getElementById('vEditBox').style.display='none';
        document.getElementById('vViewBox').style.display='';
    }
    async function saveVendor(){
        const body={
            vendorName:  document.getElementById('evName').value.trim(),
            managerEmail:document.getElementById('evEmail').value.trim(),
            managerPhone:document.getElementById('evPhone').value.trim()
        };
        if(!body.vendorName){ alert('거래처명은 필수입니다.'); return; }
        const r=await fetch('/api/vendor/'+curVendorId,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
        if(!r.ok){ alert('수정 실패 ('+r.status+')\n'+await r.text()); return; }
        await loadVendors();
        cancelVendorEdit();
        showDetail(curVendorId);
    }

    async function del(id) {
        if (!confirm('거래처 #' + id + ' 를 삭제할까요?')) return;
        const res = await fetch('/api/vendor/' + id, { method: 'DELETE' });
        if (res.ok) { loadVendors(); }
        else { alert('삭제 실패 (' + res.status + ') — 다른 데이터가 참조 중일 수 있습니다.'); }
    }

    // 검색 (이름/이메일/연락처)
    document.getElementById('searchInput').addEventListener('input', (e) => {
        const q = e.target.value.trim().toLowerCase();
        render(VENDORS.filter(v =>
            (v.vendorName||'').toLowerCase().includes(q) ||
            (v.managerEmail||'').toLowerCase().includes(q) ||
            (v.managerPhone||'').toLowerCase().includes(q)
        ));
    });

    function esc(s){ return (s==null?'':String(s)).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c])); }

    loadVendors();
</script>

<jsp:include page="../layout/footer.jsp" />
