<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="통합 검색" scope="request" />
<c:set var="menu" value="search" scope="request" />
<jsp:include page="../layout/header.jsp" />

<style>
.srch-bar{display:flex;gap:8px;max-width:640px;margin:4px 0 22px;}
.srch-bar input{flex:1;border:1px solid var(--border);border-radius:12px;padding:13px 18px;font-size:16px;outline:none;background:var(--bg-content);transition:border-color .12s;}
.srch-bar input:focus{border-color:var(--primary);}
.srch-bar button{border:none;background:var(--primary);color:#fff;border-radius:12px;padding:0 22px;font-size:15px;font-weight:600;cursor:pointer;}
.srch-bar button:hover{filter:brightness(1.05);}

.srch-summary{font-size:14px;color:var(--text-secondary);margin-bottom:18px;}
.srch-summary b{color:var(--primary);}

.srch-cat{background:var(--bg-card);border:1px solid var(--border-light);border-radius:14px;padding:18px 20px;margin-bottom:16px;box-shadow:var(--shadow-sm,0 1px 2px rgba(0,0,0,.04));}
.srch-cat-head{display:flex;align-items:center;gap:8px;font-size:15px;font-weight:700;color:var(--text);margin-bottom:12px;padding-bottom:10px;border-bottom:1px solid var(--border-light);}
.srch-cat-head .cnt{font-weight:500;font-size:13px;color:var(--text-muted);}
.srch-cat-head i{color:var(--primary);}

.srch-item{display:flex;align-items:center;gap:12px;padding:10px 6px;border-radius:8px;transition:background .1s;}
.srch-item:hover{background:var(--bg-content);}
.srch-item .ico{width:34px;height:34px;border-radius:9px;background:var(--primary-soft,#eef2ff);color:var(--primary);display:flex;align-items:center;justify-content:center;font-size:16px;flex-shrink:0;}
.srch-item .body{min-width:0;flex:1;}
.srch-item .ttl{font-size:14px;font-weight:600;color:var(--text);white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.srch-item .sub{font-size:12.5px;color:var(--text-muted);margin-top:2px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.srch-item .meta{font-size:11.5px;color:var(--text-muted);flex-shrink:0;}
.srch-badge{font-size:10px;padding:2px 8px;border-radius:10px;font-weight:600;margin-left:6px;}

.srch-empty{text-align:center;padding:60px 20px;color:var(--text-muted);}
.srch-empty i{font-size:42px;display:block;margin-bottom:12px;opacity:.5;}
</style>

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">INTEGRATED SEARCH · 통합 검색</div>
        <h1>키워드 하나로 <span class="accent">전부</span> 찾습니다.</h1>
        <p class="hero-brief">메뉴 · 레시피 식자재 · 재고이력 · 원자재 · 거래처 · 발주이력 · 재고로그를 카테고리별로 한 페이지에서 봅니다.</p>
    </div>
</section>

<form class="srch-bar" id="srchForm" onsubmit="return false;">
    <input type="text" id="srchInput" placeholder="메뉴, 식자재, 거래처, 로그 검색..." autocomplete="off">
    <button type="submit">검색</button>
</form>

<div class="srch-summary" id="srchSummary"></div>
<div id="srchResults"></div>

<script src="/js/search.js"></script>

<jsp:include page="../layout/footer.jsp" />
