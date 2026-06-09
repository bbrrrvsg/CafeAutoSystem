<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="리뷰 관리" scope="request" />
<c:set var="menu" value="review-manage" scope="request" />
<c:set var="breadcrumb" value="관리 / 리뷰 관리" scope="request" />
<jsp:include page="../layout/header.jsp" />
<link rel="stylesheet" href="/css/review-manage.css">

<!-- 페이지 헤더 -->
<div class="review-page-head">
    <div>
        <h1>리뷰 관리</h1>
        <p>고객 리뷰를 확인하고 답글을 작성하여 고객 만족도를 높여보세요.</p>
    </div>
</div>

<!-- 통계 카드 -->
<div class="review-stats">
    <div class="review-stat-card">
        <span>전체 리뷰</span>
        <strong id="statTotal">-</strong>
        <p>현재 등록된 전체 리뷰</p>
    </div>
    <div class="review-stat-card warning">
        <span>답글 대기</span>
        <strong id="statPending">-</strong>
        <p>아직 답글이 없는 리뷰</p>
    </div>
    <div class="review-stat-card success">
        <span>답글 완료</span>
        <strong id="statDone">-</strong>
        <p>답글을 완료한 리뷰</p>
    </div>
</div>

<!-- 목록 + 상세 -->
<div class="review-split">
    <!-- 리뷰 목록 -->
    <div class="review-list-card">
        <div class="review-toolbar">
            <div class="review-tabs">
                <button class="active" onclick="switchTab('all', this)">전체</button>
                <button onclick="switchTab('pending', this)">답글 대기</button>
                <button onclick="switchTab('done', this)">답글 완료</button>
            </div>
        </div>
        <table class="review-table">
            <thead>
            <tr>
                <th>리뷰번호</th>
                <th>주문번호</th>
                <th>리뷰 내용</th>
                <th>작성일</th>
                <th>상태</th>
            </tr>
            </thead>
            <tbody id="reviewTableBody">
            <tr><td colspan="5" class="table-empty">로딩 중...</td></tr>
            </tbody>
        </table>
        <div class="review-pagination">
            <button id="btnPrev" class="page-btn" onclick="prevPage()" disabled>← 이전</button>
            <span id="pageInfo">1 / 1 페이지</span>
            <button id="btnNext" class="page-btn" onclick="nextPage()" disabled>다음 →</button>
        </div>
    </div>

    <!-- 상세 패널 -->
    <aside class="review-detail-panel" id="detailPanel">
        <div class="panel-head">
            <div>
                <h2>리뷰 상세</h2>
                <p>선택한 리뷰의 상세 정보입니다.</p>
            </div>
            <button type="button" class="panel-close" onclick="closePanel()">×</button>
        </div>

        <div class="panel-meta">
            <div class="panel-meta-row"><span>리뷰번호</span><strong id="detailReviewId">-</strong></div>
            <div class="panel-meta-row"><span>주문번호</span><strong id="detailOrderId">-</strong></div>
            <div class="panel-meta-row"><span>작성일</span><strong id="detailCreatedAt">-</strong></div>
        </div>

        <div class="panel-section">
            <h3>리뷰 본문</h3>
            <div class="review-body" id="detailContent">-</div>
        </div>

        <div class="panel-section">
            <div class="reply-head">
                <h3 style="margin:0;">사장님 답글</h3>
                <em class="reply-status" id="replyStatus">답글 대기</em>
            </div>
            <textarea class="reply-textarea" id="replyTextarea"
                      placeholder="고객에게 답글을 남겨주세요. (최대 500자)" maxlength="500"></textarea>
            <div class="reply-counter"><span id="replyCharCount">0</span> / 500</div>
            <div id="replyMsg" class="reply-msg"></div>

            <!-- 답글 없을 때: 등록만 -->
            <div class="reply-actions no-reply" id="actionsNoReply">
                <button class="reply-btn primary" onclick="createReply()">답글 등록</button>
            </div>
            <!-- 답글 있을 때: 수정 + 삭제 -->
            <div class="reply-actions has-reply" id="actionsHasReply" style="display:none;">
                <button class="reply-btn" onclick="updateReply()">답글 수정</button>
                <button class="reply-btn danger" onclick="deleteReply()">답글 삭제</button>
            </div>

            <p class="auto-note">미답글 리뷰는 자정에 AI 기반 자동 답글이 등록됩니다.</p>
        </div>
    </aside>
</div>

<script src="/js/review-manage.js"></script>
<jsp:include page="../layout/footer.jsp" />
