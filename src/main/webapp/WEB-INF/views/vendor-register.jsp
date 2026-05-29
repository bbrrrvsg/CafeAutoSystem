<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="거래처 등록" scope="request" />
<c:set var="menu" value="vendor" scope="request" />
<jsp:include page="layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">VENDOR · 등록</div>
        <h1>새 거래처를 등록합니다.</h1>
        <p class="hero-brief">엑셀 파일을 업로드하면 AI가 자동으로 분석해 정보를 입력해드려요.</p>
    </div>
</section>

<div class="stepper">
    <div class="step active"><span class="step-num">1</span><span>엑셀 파일 업로드</span></div>
    <div class="step-line"></div>
    <div class="step"><span class="step-num">2</span><span>데이터 분석 및 확인</span></div>
    <div class="step-line"></div>
    <div class="step"><span class="step-num">3</span><span>등록 완료</span></div>
</div>

<!-- 1행: 업로드 + AI 분석 진행 상황 -->
<div style="display:grid; grid-template-columns: 1.3fr 1fr; gap:20px; margin-bottom:24px;">
    <div class="form-section" style="margin:0;">
        <h3>업로드된 파일</h3>
        <div class="sec-sub">공급업체에서 받은 엑셀 파일을 자동 분석 중입니다.</div>

        <div class="file-attached">
            <div class="fa-info">
                <div class="fa-name">서울원두유통_공급물품목록.xlsx</div>
                <div class="fa-size">24.8KB · 업로드 완료</div>
            </div>
            <button class="btn btn-secondary btn-sm">다시 업로드</button>
        </div>
    </div>

    <div class="ai-box" style="margin:0;">
        <div class="ai-label">AI 분석 진행 상황</div>
        <div class="ai-progress">
            <div class="ai-progress-item"><span class="name">파일 형식 확인</span><span class="badge badge-success">완료</span></div>
            <div class="ai-progress-item"><span class="name">데이터 구조 분석</span><span class="badge badge-success">완료</span></div>
            <div class="ai-progress-item"><span class="name">거래처 정보 추출</span><span class="badge badge-success">완료</span></div>
            <div class="ai-progress-item"><span class="name">공급 물품 정보 추출</span><span class="badge badge-success">완료</span></div>
            <div class="ai-progress-item"><span class="name">이미지 매핑</span><span class="badge badge-success">완료</span></div>
        </div>
    </div>
</div>

<!-- 거래처 정보 -->
<div class="form-section">
    <h3>분석된 거래처 정보</h3>
    <div class="sec-sub">AI가 추출한 정보입니다. 필요시 수정해주세요.</div>

    <div style="display:grid; grid-template-columns: repeat(5, 1fr); gap:16px;">
        <div>
            <div style="font-size:12px; color:var(--text-muted); font-weight:600; margin-bottom:6px;">거래처명</div>
            <input type="text" value="서울원두유통" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        </div>
        <div>
            <div style="font-size:12px; color:var(--text-muted); font-weight:600; margin-bottom:6px;">대표 연락처</div>
            <input type="text" value="02-111-2222" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        </div>
        <div>
            <div style="font-size:12px; color:var(--text-muted); font-weight:600; margin-bottom:6px;">담당자명</div>
            <input type="text" value="김유리" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        </div>
        <div>
            <div style="font-size:12px; color:var(--text-muted); font-weight:600; margin-bottom:6px;">이메일</div>
            <input type="email" value="seoulbeans@coffee.com" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        </div>
        <div>
            <div style="font-size:12px; color:var(--text-muted); font-weight:600; margin-bottom:6px;">설명</div>
            <input type="text" value="원두 및 시럽, 파우더 공급 전문 업체" style="width:100%; padding:9px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
        </div>
    </div>
</div>

<!-- 공급 물품 -->
<div class="form-section">
    <h3>분석된 공급 물품 목록</h3>
    <div class="sec-sub">총 4개의 공급 물품이 분석되었습니다.</div>

    <table class="data-table" style="border:1px solid var(--border-light); border-radius:8px;">
        <thead>
            <tr>
                <th>번호</th>
                <th>품목명</th>
                <th>공급단가</th>
                <th>이미지 경로</th>
                <th>카테고리</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="num">1</td>
                <td>하우스 블렌드 원두</td>
                <td class="num">15,000원</td>
                <td class="text-muted">/images/ingredients/house_blend.jpg</td>
                <td><span class="tag">원두</span></td>
            </tr>
            <tr>
                <td class="num">2</td>
                <td>파우더 및 시럽</td>
                <td class="num">8,500원</td>
                <td class="text-muted">/images/ingredients/powder_syrup.jpg</td>
                <td><span class="tag">시럽/파우더</span></td>
            </tr>
            <tr>
                <td class="num">3</td>
                <td>냉동 딸기 블렌드</td>
                <td class="num">12,000원</td>
                <td class="text-muted">/images/ingredients/frozen_strawberry.jpg</td>
                <td><span class="tag">과일/블렌드</span></td>
            </tr>
            <tr>
                <td class="num">4</td>
                <td>신선한 우유 (1L)</td>
                <td class="num">2,500원</td>
                <td class="text-muted">/images/ingredients/fresh_milk_1l.jpg</td>
                <td><span class="tag">유제품</span></td>
            </tr>
        </tbody>
    </table>
</div>

<div style="display:flex; gap:8px; justify-content:flex-end; margin-top:20px;">
    <button class="btn btn-secondary">취소</button>
    <button class="btn btn-primary">등록하기</button>
</div>

<jsp:include page="layout/footer.jsp" />
