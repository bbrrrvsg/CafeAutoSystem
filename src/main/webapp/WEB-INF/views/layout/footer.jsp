<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        </main>
    </div>
</div>
<script src="/js/app.js"></script>

<!-- 공통 빠른입력: 숫자칸 포커스 시에만 아래로 +5 +10 +100 +1000 팝업 (레이아웃 안 밀림 → 정렬 유지) -->
<style>
.quickfill{position:absolute;top:100%;left:0;z-index:20;display:none;flex-wrap:wrap;gap:4px;margin-top:4px;padding:6px 7px;background:var(--bg-card);border:1px solid var(--border-light);border-radius:8px;box-shadow:var(--shadow-md);}
.quickfill button{border:1px solid var(--border);background:var(--bg-content);border-radius:6px;padding:3px 10px;font-size:11px;font-weight:600;cursor:pointer;color:var(--text-secondary);line-height:1.5;transition:all .1s;}
.quickfill button:hover{border-color:var(--primary);color:var(--primary);background:var(--primary-soft);}
.quickfill button:active{transform:translateY(1px);}
.quickfill button.qf-clear{color:var(--text-muted);font-weight:400;}
.quickfill button.qf-clear:hover{border-color:var(--danger);color:var(--danger);background:var(--danger-bg);}
</style>
<script>
(function(){
  var PRESETS=[5,10,100,1000];
  function fire(inp){ inp.dispatchEvent(new Event('input',{bubbles:true})); inp.dispatchEvent(new Event('change',{bubbles:true})); }
  function enhance(inp){
    if(inp.dataset.qf) return; inp.dataset.qf='1';
    var cell=inp.parentElement;
    if(cell && getComputedStyle(cell).position==='static') cell.style.position='relative';
    var box=document.createElement('div'); box.className='quickfill';
    PRESETS.forEach(function(p){
      var b=document.createElement('button'); b.type='button'; b.tabIndex=-1; b.textContent='+'+p;
      b.addEventListener('click',function(){ if(inp.disabled)return; var cur=inp.value===''?0:Number(inp.value); if(isNaN(cur))cur=0; var v=cur+p; if(inp.max!==''&&v>Number(inp.max))v=Number(inp.max); inp.value=v; fire(inp); });
      box.appendChild(b);
    });
    var c=document.createElement('button'); c.type='button'; c.tabIndex=-1; c.className='qf-clear'; c.textContent='×'; c.title='지우기';
    c.addEventListener('click',function(){ if(inp.disabled)return; inp.value=''; fire(inp); });
    box.appendChild(c);
    box.addEventListener('mousedown',function(e){ e.preventDefault(); }); // 칩 클릭해도 input 포커스 유지
    inp.insertAdjacentElement('afterend', box);
    inp.addEventListener('focus',function(){ box.style.display='flex'; });
    inp.addEventListener('blur',function(){ setTimeout(function(){ box.style.display='none'; },150); });
  }
  function run(root){ (root||document).querySelectorAll('input[type=number]:not([data-qf])').forEach(enhance); }
  if(document.readyState!=='loading') run(); else document.addEventListener('DOMContentLoaded', function(){ run(); });
  new MutationObserver(function(muts){ muts.forEach(function(m){ if(m.addedNodes){ m.addedNodes.forEach(function(n){ if(n.nodeType===1){ if(n.matches && n.matches('input[type=number]')) enhance(n); run(n); } }); } }); }).observe(document.body,{childList:true,subtree:true});
})();
</script>

<!-- ===== AI 챗봇 플로팅 위젯 (전 페이지 공통) ===== -->
<style>
#aiFab{position:fixed;bottom:24px;right:24px;width:64px;height:64px;border-radius:50%;
  background:var(--primary,#4f46e5);color:#fff;border:none;cursor:pointer;z-index:9998;
  box-shadow:0 6px 20px rgba(0,0,0,.25);font-size:28px;display:flex;align-items:center;justify-content:center;transition:transform .15s;}
#aiFab:hover{transform:scale(1.08);}
#aiChat{position:fixed;bottom:100px;right:24px;width:440px;max-width:calc(100vw - 32px);
  height:640px;max-height:calc(100vh - 130px);background:var(--bg-card,#fff);
  border:1px solid var(--border,#e5e7eb);border-radius:16px;z-index:9999;display:none;
  flex-direction:column;overflow:hidden;box-shadow:0 12px 40px rgba(0,0,0,.22);}
#aiChat.open{display:flex;}
#aiChatHead{background:var(--primary,#4f46e5);color:#fff;padding:12px 16px;display:flex;align-items:center;justify-content:space-between;}
#aiChatHead b{font-size:16px;}
#aiChatHead .sub{font-size:12px;opacity:.85;margin-top:2px;}
#aiChatClose{background:none;border:none;color:#fff;font-size:20px;cursor:pointer;line-height:1;}
#aiChatBody{flex:1;overflow-y:auto;padding:14px;background:var(--bg-content,#f8fafc);display:flex;flex-direction:column;gap:10px;}
.ai-msg{max-width:82%;padding:11px 14px;border-radius:13px;font-size:14.5px;line-height:1.55;white-space:pre-wrap;word-break:break-word;}
.ai-msg.me{align-self:flex-end;background:var(--primary,#4f46e5);color:#fff;border-bottom-right-radius:3px;}
.ai-msg.bot{align-self:flex-start;background:#fff;border:1px solid var(--border,#e5e7eb);color:var(--text,#111827);border-bottom-left-radius:3px;}
.ai-msg.loading{color:var(--text-muted,#9ca3af);font-style:italic;}
#aiChatFoot{display:flex;gap:6px;padding:10px;border-top:1px solid var(--border,#e5e7eb);background:var(--bg-card,#fff);}
#aiChatInput{flex:1;border:1px solid var(--border,#e5e7eb);border-radius:22px;padding:10px 16px;font-size:14px;outline:none;}
#aiChatInput:focus{border-color:var(--primary,#4f46e5);}
#aiChatSend{background:var(--primary,#4f46e5);color:#fff;border:none;border-radius:22px;padding:10px 18px;font-size:14px;font-weight:600;cursor:pointer;}
#aiChatSend:disabled{opacity:.5;cursor:not-allowed;}
#aiChatHelp{display:flex;gap:6px;flex-wrap:wrap;padding:8px 10px 0;}
.ai-chip{background:var(--primary-soft,#eef2ff);border:1px solid var(--border,#e5e7eb);border-radius:14px;padding:5px 11px;font-size:12px;cursor:pointer;color:var(--primary,#4f46e5);white-space:nowrap;}
.ai-chip:hover{background:var(--primary,#4f46e5);color:#fff;}
</style>

<button id="aiFab" title="AI 비서">💬</button>
<div id="aiChat">
  <div id="aiChatHead">
    <div><b>AI 비서</b><div class="sub">재고·발주를 말로 물어보세요</div></div>
    <div style="display:flex;gap:10px;align-items:center;">
      <button id="aiChatClear" title="대화 비우기" style="background:none;border:none;color:#fff;font-size:12px;cursor:pointer;opacity:.85;">지우기</button>
      <button id="aiChatClose" title="닫기">&times;</button>
    </div>
  </div>
  <div id="aiChatBody"></div>
  <div id="aiChatHelp">
    <button class="ai-chip" data-fill="우유 ml 안전재고 30 등록">🥛 식자재 등록</button>
    <button class="ai-chip" data-fill="거래처 서울유통 이메일 a@b.com 연락처 010-1234-5678 등록">🏢 거래처 등록</button>
    <button class="ai-chip" data-fill="우유 재고 얼마야?">📦 재고 조회</button>
  </div>
  <div id="aiChatFoot">
    <input id="aiChatInput" type="text" placeholder="메시지를 입력하세요..." autocomplete="off">
    <button id="aiChatSend">전송</button>
  </div>
</div>

<script>
(function(){
  var fab=document.getElementById('aiFab'), win=document.getElementById('aiChat');
  var body=document.getElementById('aiChatBody'), input=document.getElementById('aiChatInput'), send=document.getElementById('aiChatSend');
  // ---- 대화 기록 저장/복원 (sessionStorage: 페이지 이동에도 유지, 탭 닫으면 초기화) ----
  var LOG;
  try{ LOG = JSON.parse(sessionStorage.getItem('aiChatLog') || '[]'); }catch(e){ LOG = []; }
  function save(){ try{ sessionStorage.setItem('aiChatLog', JSON.stringify(LOG)); }catch(e){} }

  function renderMsg(text, cls){ var d=document.createElement('div'); d.className='ai-msg '+cls; d.textContent=text; body.appendChild(d); body.scrollTop=body.scrollHeight; return d; }
  function addMsg(text, cls){ LOG.push({t:text,c:cls}); save(); return renderMsg(text, cls); }   // 저장됨
  function addTemp(text, cls){ return renderMsg(text, cls); }                                     // 저장 안 됨(로딩 등)

  function restore(){
    body.innerHTML='';
    if(LOG.length===0){ LOG.push({t:'안녕하세요! 등록·조회를 도와드려요 😊\n아래 예시 버튼을 누르면 형식이 채워져요. 값만 바꿔서 보내보세요!\n\n· 식자재/거래처 등록\n· 재고 조회', c:'bot'}); save(); }
    LOG.forEach(function(m){ renderMsg(m.t, m.c); });
  }
  restore();
  if(sessionStorage.getItem('aiChatOpen')==='1'){ win.classList.add('open'); }   // 열려있던 상태 유지

  function openChat(){ win.classList.add('open'); sessionStorage.setItem('aiChatOpen','1'); input.focus(); }
  function closeChat(){ win.classList.remove('open'); sessionStorage.setItem('aiChatOpen','0'); }
  fab.addEventListener('click', function(){ win.classList.contains('open')?closeChat():openChat(); });
  document.getElementById('aiChatClose').addEventListener('click', closeChat);
  document.getElementById('aiChatClear').addEventListener('click', function(){ LOG=[]; save(); restore(); });
  // 예시 칩: 클릭하면 입력칸에 형식 채워줌 (값만 바꿔서 전송)
  document.querySelectorAll('#aiChatHelp .ai-chip').forEach(function(c){
    c.addEventListener('click', function(){ input.value=c.dataset.fill; input.focus(); });
  });

  async function ask(){
    var msg=input.value.trim(); if(!msg) return;
    addMsg(msg,'me'); input.value=''; send.disabled=true;
    var loading=addTemp('생각 중...', 'bot loading');
    try{
      var r=await fetch('/api/chat',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({message:msg})});
      var j=await r.json();
      loading.remove();
      if(r.ok){ addMsg(j.answer || '(응답이 비어있어요)','bot'); }
      else { addMsg('⚠️ '+(j.error || ('오류 '+r.status)),'bot'); }
    }catch(e){ loading.remove(); addMsg('⚠️ 서버 연결 실패: '+e.message,'bot'); }
    finally{ send.disabled=false; input.focus(); }
  }
  send.addEventListener('click', ask);
  input.addEventListener('keydown', function(e){ if(e.key==='Enter'){ e.preventDefault(); ask(); } });
})();
</script>
</body>
</html>
