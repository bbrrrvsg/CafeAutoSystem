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
</body>
</html>
