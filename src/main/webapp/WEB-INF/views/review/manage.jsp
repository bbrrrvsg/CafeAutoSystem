<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>리뷰 관리 | Smart Cafe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review-manage.css">
</head>
<body>
<div class="app-shell">
    <aside class="sidebar">
        <div class="logo">
            <div class="logo-icon">☕</div>
            <div>
                <strong>CafeOS</strong>
                <span>Smart Cafe</span>
            </div>
        </div>
        <nav class="nav-section">
            <p>운영</p>
            <a href="/">대시보드</a>
            <a href="/pos">POS 주문</a>
            <a href="/inventory">재고 관리</a>
        </nav>
        <nav class="nav-section">
            <p>발주 · AI</p>
            <a href="/ai-order">AI 발주 관리</a>
            <a href="/ai-check">AI 오발주 검사</a>
            <a href="/approval">발주 승인</a>
            <a href="/order">발주 작성</a>
            <a href="/order-history">발주 이력</a>
        </nav>
        <nav class="nav-section">
            <p>관리</p>
            <a href="/vendor">거래처 관리</a>
            <a href="/review/manage" class="active">리뷰 관리</a>
            <a href="/review/insight">리뷰 인사이트</a>
            <a href="/log">로그 추적</a>
        </nav>
    </aside>

    <main class="main">
        <header class="topbar">
            <div>
                <strong>리뷰 관리</strong>
                <span>관리 / 리뷰 관리</span>
            </div>
            <div class="search-box">메뉴, 품목, 주문번호 검색... <kbd>⌘ K</kbd></div>
        </header>

        <section class="content">
            <div class="page-head">
                <div>
                    <h1>리뷰 관리</h1>
                    <p>고객 리뷰를 확인하고 답글을 작성하여 고객 만족도를 높여보세요.</p>
                </div>
            </div>

            <section class="stats-grid">
                <article class="stat-card">
                    <span>전체 리뷰</span>
                    <strong id="statTotal">-</strong>
                    <p>현재 등록된 전체 리뷰</p>
                </article>
                <article class="stat-card warning">
                    <span>답글 대기</span>
                    <strong id="statPending">-</strong>
                    <p>아직 답글이 없는 리뷰</p>
                </article>
                <article class="stat-card success">
                    <span>답글 완료</span>
                    <strong id="statDone">-</strong>
                    <p>답글을 완료한 리뷰</p>
                </article>
            </section>

            <section class="split-layout">
                <div class="list-card">
                    <div class="toolbar">
                        <div class="tabs">
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
                        <tr><td colspan="5" class="table-cell-empty">로딩 중...</td></tr>
                        </tbody>
                    </table>
                </div>

                <aside class="detail-panel" id="detailPanel">
                    <div class="panel-head">
                        <div>
                            <h2>리뷰 상세</h2>
                            <p>선택한 리뷰의 상세 정보입니다.</p>
                        </div>
                        <button type="button" onclick="closePanel()">×</button>
                    </div>

                    <div class="detail-list">
                        <div><span>리뷰번호</span><strong id="detailReviewId">-</strong></div>
                        <div><span>주문번호</span><strong id="detailOrderId">-</strong></div>
                        <div><span>작성일</span><strong id="detailCreatedAt">-</strong></div>
                    </div>

                    <section class="panel-section">
                        <h3>리뷰 본문</h3>
                        <div class="review-body" id="detailContent">-</div>
                    </section>

                    <section class="panel-section reply-section">
                        <div class="reply-title">
                            <h3>사장님 답글</h3>
                            <em id="replyStatus">답글 대기</em>
                        </div>
                        <textarea id="replyTextarea" placeholder="고객에게 답글을 남겨주세요. (최대 500자)" maxlength="500"></textarea>
                        <div class="counter"><span id="replyCharCount">0</span> / 500</div>
                        <div id="replyMsg" class="reply-msg"></div>
                        <div class="reply-actions">
                            <button class="primary" onclick="createReply()">답글 등록</button>
                            <button onclick="updateReply()">답글 수정</button>
                            <button class="danger" onclick="deleteReply()">답글 삭제</button>
                        </div>
                        <p class="auto-note">미답글 리뷰는 자정에 AI 기반 자동 답글이 등록됩니다.</p>
                    </section>
                </aside>
            </section>
        </section>
    </main>
</div>

<script>
    var allReviews = [];
    var currentTab = 'all';
    var currentReviewId = null;

    document.addEventListener('DOMContentLoaded', function () {
        loadReviews();
        document.getElementById('replyTextarea').addEventListener('input', function () {
            document.getElementById('replyCharCount').textContent = this.value.length;
        });
    });

    function loadReviews() {
        fetch('/api/reviews')
            .then(function (res) { return res.json(); })
            .then(function (reviews) {
                allReviews = reviews;
                updateStats(reviews);
                renderTable(currentTab);
            })
            .catch(function (err) {
                document.getElementById('reviewTableBody').innerHTML =
                    '<tr><td colspan="5" class="table-cell-error">리뷰를 불러오지 못했습니다.</td></tr>';
                console.error(err);
            });
    }

    function updateStats(reviews) {
        var pending = reviews.filter(function (r) { return !r.ownerReply; }).length;
        var done    = reviews.filter(function (r) { return !!r.ownerReply; }).length;
        document.getElementById('statTotal').textContent   = reviews.length + '건';
        document.getElementById('statPending').textContent = pending + '건';
        document.getElementById('statDone').textContent    = done + '건';
    }

    function switchTab(tab, btn) {
        currentTab = tab;
        document.querySelectorAll('.tabs button').forEach(function (b) { b.classList.remove('active'); });
        btn.classList.add('active');
        renderTable(tab);
    }

    function renderTable(tab) {
        var filtered = allReviews.filter(function (r) {
            if (tab === 'pending') return !r.ownerReply;
            if (tab === 'done')    return !!r.ownerReply;
            return true;
        });

        var tbody = document.getElementById('reviewTableBody');
        if (filtered.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="table-cell-empty">리뷰가 없습니다.</td></tr>';
            return;
        }

        tbody.innerHTML = filtered.map(function (r) {
            var badge = r.ownerReply
                ? '<em class="badge done">답글 완료</em>'
                : '<em class="badge wait">답글 대기</em>';
            var date    = r.createdAt ? r.createdAt.replace('T', ' ').substring(0, 16) : '-';
            var content = r.reviewContent
                ? r.reviewContent.substring(0, 30) + (r.reviewContent.length > 30 ? '...' : '') : '-';
            return '<tr onclick="selectReview(' + r.reviewId + ')" id="row-' + r.reviewId + '">' +
                '<td>' + r.reviewId + '</td>' +
                '<td>' + r.orderId  + '</td>' +
                '<td><strong>' + content + '</strong></td>' +
                '<td>' + date + '</td>' +
                '<td>' + badge + '</td>' +
                '</tr>';
        }).join('');
    }

    function selectReview(reviewId) {
        currentReviewId = reviewId;
        document.querySelectorAll('.review-table tbody tr').forEach(function (tr) { tr.classList.remove('selected'); });
        var row = document.getElementById('row-' + reviewId);
        if (row) row.classList.add('selected');

        fetch('/api/reviews/' + reviewId)
            .then(function (res) { return res.json(); })
            .then(function (review) {
                document.getElementById('detailReviewId').textContent  = review.reviewId;
                document.getElementById('detailOrderId').textContent   = review.orderId;
                document.getElementById('detailCreatedAt').textContent = review.createdAt
                    ? review.createdAt.replace('T', ' ').substring(0, 16) : '-';
                document.getElementById('detailContent').textContent = review.reviewContent || '-';

                var textarea = document.getElementById('replyTextarea');
                if (review.ownerReply) {
                    textarea.value = review.ownerReply;
                    document.getElementById('replyCharCount').textContent = review.ownerReply.length;
                    document.getElementById('replyStatus').textContent = '답글 완료';
                } else {
                    textarea.value = '';
                    document.getElementById('replyCharCount').textContent = '0';
                    document.getElementById('replyStatus').textContent = '답글 대기';
                }

                document.getElementById('replyMsg').className = 'reply-msg';
                document.getElementById('detailPanel').classList.add('active');
            })
            .catch(function (err) { console.error(err); });
    }

    function closePanel() {
        document.getElementById('detailPanel').classList.remove('active');
        currentReviewId = null;
    }

    function createReply() {
        if (!currentReviewId) return;
        var content = document.getElementById('replyTextarea').value.trim();
        if (!content) { showReplyMsg('답글 내용을 입력해주세요.', 'msg-warning'); return; }

        fetch('/api/reviews/' + currentReviewId + '/reply', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({replyContent: content})
        })
            .then(function (res) { return res.json(); })
            .then(function () {
                showReplyMsg('답글이 등록되었습니다.', 'msg-success');
                document.getElementById('replyStatus').textContent = '답글 완료';
                loadReviews();
            })
            .catch(function (err) { showReplyMsg('등록에 실패했습니다.', 'msg-error'); console.error(err); });
    }

    function updateReply() {
        if (!currentReviewId) return;
        var content = document.getElementById('replyTextarea').value.trim();
        if (!content) { showReplyMsg('수정할 답글 내용을 입력해주세요.', 'msg-warning'); return; }

        fetch('/api/reviews/' + currentReviewId + '/reply', {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({replyContent: content})
        })
            .then(function (res) { return res.json(); })
            .then(function () {
                showReplyMsg('답글이 수정되었습니다.', 'msg-success');
                loadReviews();
            })
            .catch(function (err) { showReplyMsg('수정에 실패했습니다.', 'msg-error'); console.error(err); });
    }

    function deleteReply() {
        if (!currentReviewId) return;
        if (!confirm('답글을 삭제하시겠습니까?')) return;

        fetch('/api/reviews/' + currentReviewId + '/reply', {method: 'DELETE'})
            .then(function (res) { return res.json(); })
            .then(function () {
                showReplyMsg('답글이 삭제되었습니다.', 'msg-success');
                document.getElementById('replyTextarea').value = '';
                document.getElementById('replyCharCount').textContent = '0';
                document.getElementById('replyStatus').textContent = '답글 대기';
                loadReviews();
            })
            .catch(function (err) { showReplyMsg('삭제에 실패했습니다.', 'msg-error'); console.error(err); });
    }

    function showReplyMsg(text, type) {
        var el = document.getElementById('replyMsg');
        el.textContent = text;
        el.className = 'reply-msg ' + type;
        el.style.display = 'block';
    }
</script>
</body>
</html>
