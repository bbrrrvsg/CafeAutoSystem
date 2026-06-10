<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Cafe - ${pageTitle}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="app-container">

    <!-- 사이드바 (컴팩트 → hover 확장) -->
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-logo">
            <div class="sidebar-logo-icon"><i class="bi bi-cup-hot-fill"></i></div>
            <div class="sidebar-logo-text">
                <h1>Smart Cafe</h1>
                <p>재고·발주 자동화</p>
            </div>
        </div>

        <ul class="sidebar-menu">
            <!-- 그룹 1: 운영 -->
            <li class="menu-group">
                <div class="menu-group-label">운영</div>
            </li>
            <li class="${menu == 'dashboard' ? 'active' : ''}">
                <a href="/"><span class="menu-icon"><i class="bi bi-grid-1x2"></i></span><span class="menu-label">대시보드</span></a>
            </li>
            <li class="${menu == 'pos' ? 'active' : ''}">
                <a href="/pos"><span class="menu-icon"><i class="bi bi-cart3"></i></span><span class="menu-label">POS 주문</span></a>
            </li>
            <li class="${menu == 'inventory' ? 'active' : ''}">
                <a href="/inventory"><span class="menu-icon"><i class="bi bi-box-seam"></i></span><span class="menu-label">재고 관리</span></a>
            </li>

            <!-- 그룹 2: 발주 / AI -->
            <li class="menu-group">
                <div class="menu-group-label">발주 · AI</div>
            </li>
            <li class="${menu == 'ai-order' ? 'active' : ''}">
                <a href="/ai-order"><span class="menu-icon"><i class="bi bi-robot"></i></span><span class="menu-label">AI 발주 관리</span></a>
            </li>
            <li class="${menu == 'approval' ? 'active' : ''}">
                <a href="/approval"><span class="menu-icon"><i class="bi bi-check2-square"></i></span><span class="menu-label">발주 승인</span></a>
            </li>
            <li class="${menu == 'order' ? 'active' : ''}">
                <a href="/order"><span class="menu-icon"><i class="bi bi-pencil-square"></i></span><span class="menu-label">발주 작성</span></a>
            </li>
            <li class="${menu == 'order-history' ? 'active' : ''}">
                <a href="/order-history"><span class="menu-icon"><i class="bi bi-clock-history"></i></span><span class="menu-label">발주 이력</span></a>
            </li>

            <!-- 그룹 3: 관리 -->
            <li class="menu-group">
                <div class="menu-group-label">관리</div>
            </li>
            <li class="${menu == 'vendor' ? 'active' : ''}">
                <a href="/vendor"><span class="menu-icon"><i class="bi bi-people"></i></span><span class="menu-label">거래처 관리</span></a>
            </li>
            <li class="${menu == 'vendor-ingredient' ? 'active' : ''}">
                <a href="/vendor-ingredient"><span class="menu-icon"><i class="bi bi-link-45deg"></i></span><span class="menu-label">거래처-식자재</span></a>
            </li>
            <li class="${menu == 'ingredient' ? 'active' : ''}">
                <a href="/ingredient"><span class="menu-icon"><i class="bi bi-basket3"></i></span><span class="menu-label">식자재 관리</span></a>
            </li>
            <li class="${menu == 'menu' ? 'active' : ''}">
                <a href="/menu"><span class="menu-icon"><i class="bi bi-cup-straw"></i></span><span class="menu-label">메뉴 &amp; 레시피</span></a>
            </li>
            <li class="${menu == 'review-manage' ? 'active' : ''}">
                <a href="/review/manage"><span class="menu-icon"><i class="bi bi-chat-left-text"></i></span><span class="menu-label">리뷰 관리</span></a>
            </li>
            <li class="${menu == 'review-insight' ? 'active' : ''}">
                <a href="/review/insight"><span class="menu-icon"><i class="bi bi-bar-chart-line"></i></span><span class="menu-label">리뷰 대시보드</span></a>
            </li>
            <li class="${menu == 'log' ? 'active' : ''}">
                <a href="/log"><span class="menu-icon"><i class="bi bi-journal-text"></i></span><span class="menu-label">로그 추적</span></a>
            </li>
        </ul>

        <!-- 핀 고정 버튼 -->
        <div class="sidebar-footer">
            <button class="sidebar-pin-btn" id="pinBtn" title="사이드바 고정">
                <span class="menu-icon"><i class="bi bi-pin-angle" id="pinIcon"></i></span>
                <span class="label">사이드바 고정</span>
            </button>
        </div>
    </aside>

    <!-- 메인 영역 -->
    <div class="main-content">

        <!-- 상단 헤더 -->
        <header class="topbar">
            <div class="topbar-title">
                <h2>${pageTitle}</h2>
                <c:if test="${not empty breadcrumb}">
                    <div class="topbar-breadcrumb">${breadcrumb}</div>
                </c:if>
            </div>

            <!-- 명령 팔레트 트리거 -->
            <button class="topbar-cmdk" id="cmdkBtn">
                <i class="bi bi-search"></i>
                <span class="placeholder">메뉴, 품목 검색...</span>
                <span class="kbd">⌘ K</span>
            </button>
        </header>

        <!-- 페이지 본문 -->
        <main class="page-content">
