var allReviews = [];
var currentTab = 'all';
var currentReviewId = null;
var currentPage = 0;
var totalCount = 0;
var totalPages = 1;
var PAGE_SIZE = 10;

document.addEventListener('DOMContentLoaded', function () {
    loadReviews();

    var textarea = document.getElementById('replyTextarea');
    if (textarea) {
        textarea.addEventListener('input', function () {
            document.getElementById('replyCharCount').textContent = this.value.length;
        });
    }
});

function loadReviews() {
    document.getElementById('reviewTableBody').innerHTML =
        '<tr><td colspan="5" class="table-empty">로딩 중...</td></tr>';

    fetch('/api/owner/reviews?page=' + currentPage + '&size=' + PAGE_SIZE)
        .then(function (res) {
            if (!res.ok) {
                throw new Error('리뷰 목록 조회 실패: ' + res.status);
            }
            return res.json();
        })
        .then(function (data) {
            /*
             * 새 백엔드 응답:
             * {
             *   reviews: [],
             *   page: 0,
             *   size: 10,
             *   totalElements: 1,
             *   totalPages: 1
             * }
             *
             * 예전 응답 totalCount도 혹시 남아있으면 같이 대응.
             */
            totalCount = Number(data.totalElements ?? data.totalCount ?? 0);
            totalPages = Number(data.totalPages ?? Math.max(1, Math.ceil(totalCount / PAGE_SIZE)));

            var reviews = data.reviews || data.content || [];

            var promises = reviews.map(function (r) {
                return fetch('/api/owner/reviews/' + r.reviewId + '/reply')
                    .then(function (res) {
                        if (!res.ok) {
                            return {
                                hasReply: false,
                                replyContent: null
                            };
                        }
                        return res.json();
                    })
                    .then(function (replyData) {
                        r.hasReply = Boolean(replyData.hasReply);
                        r.replyContent = replyData.replyContent || '';
                        return normalizeReview(r);
                    })
                    .catch(function () {
                        r.hasReply = false;
                        r.replyContent = '';
                        return normalizeReview(r);
                    });
            });

            return Promise.all(promises);
        })
        .then(function (enrichedReviews) {
            allReviews = enrichedReviews;
            updateStats();
            renderTable(currentTab);
            updatePagination();
        })
        .catch(function (error) {
            console.error(error);

            allReviews = [];
            totalCount = 0;
            totalPages = 1;

            updateStats();
            updatePagination();

            document.getElementById('reviewTableBody').innerHTML =
                '<tr><td colspan="5" class="table-error">리뷰를 불러오지 못했습니다.</td></tr>';
        });
}

function normalizeReview(r) {
    return {
        reviewId: r.reviewId,
        orderId: r.orderId,
        reviewContent: r.reviewContent || '',
        createdAt: r.createdAt || r.customerCreatedAt || '',
        status: r.status || 'REVIEW_RECEIVED',
        hasReply: Boolean(r.hasReply),
        replyContent: r.replyContent || ''
    };
}

function updateStats() {
    var pending = allReviews.filter(function (r) { return !r.hasReply; }).length;
    var done = allReviews.filter(function (r) { return r.hasReply; }).length;

    document.getElementById('statTotal').textContent = totalCount + '건';
    document.getElementById('statPending').textContent = pending + '건';
    document.getElementById('statDone').textContent = done + '건';
}

function switchTab(tab, btn) {
    currentTab = tab;

    document.querySelectorAll('.review-tabs button').forEach(function (b) {
        b.classList.remove('active');
    });

    btn.classList.add('active');
    renderTable(tab);
}

function renderTable(tab) {
    var filtered = allReviews.filter(function (r) {
        if (tab === 'pending') return !r.hasReply;
        if (tab === 'done') return r.hasReply;
        return true;
    });

    var tbody = document.getElementById('reviewTableBody');

    if (filtered.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="table-empty">리뷰가 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = filtered.map(function (r) {
        var badge = r.hasReply
            ? '<em class="review-badge done">답글 완료</em>'
            : '<em class="review-badge wait">답글 대기</em>';

        var date = r.createdAt ? String(r.createdAt).substring(0, 16) : '-';

        var rawContent = r.reviewContent || '-';
        var safeContent = escapeHtml(rawContent);
        var shortContent = safeContent.length > 28
            ? safeContent.substring(0, 28) + '…'
            : safeContent;

        return '<tr onclick="selectReview(' + r.reviewId + ')" id="row-' + r.reviewId + '">' +
            '<td>' + r.reviewId + '</td>' +
            '<td>' + r.orderId + '</td>' +
            '<td>' + shortContent + '</td>' +
            '<td>' + date + '</td>' +
            '<td>' + badge + '</td>' +
            '</tr>';
    }).join('');
}

function selectReview(reviewId) {
    currentReviewId = reviewId;

    document.querySelectorAll('.review-table tbody tr').forEach(function (tr) {
        tr.classList.remove('selected');
    });

    var row = document.getElementById('row-' + reviewId);
    if (row) row.classList.add('selected');

    var review = allReviews.find(function (r) {
        return Number(r.reviewId) === Number(reviewId);
    });

    if (!review) return;

    document.getElementById('detailReviewId').textContent = review.reviewId;
    document.getElementById('detailOrderId').textContent = review.orderId;
    document.getElementById('detailCreatedAt').textContent = review.createdAt
        ? String(review.createdAt).substring(0, 16)
        : '-';
    document.getElementById('detailContent').textContent = review.reviewContent || '-';

    var textarea = document.getElementById('replyTextarea');

    if (review.hasReply) {
        textarea.value = review.replyContent || '';
        document.getElementById('replyCharCount').textContent = textarea.value.length;
        setReplyState(true);
    } else {
        textarea.value = '';
        document.getElementById('replyCharCount').textContent = '0';
        setReplyState(false);
    }

    document.getElementById('replyMsg').className = 'reply-msg';
    document.getElementById('replyMsg').style.display = 'none';
    document.getElementById('detailPanel').classList.add('active');
}

function setReplyState(hasReply) {
    var statusEl = document.getElementById('replyStatus');
    var noReply = document.getElementById('actionsNoReply');
    var hasReplyEl = document.getElementById('actionsHasReply');

    if (hasReply) {
        statusEl.textContent = '답글 완료';
        statusEl.className = 'reply-status done';
        noReply.style.display = 'none';
        hasReplyEl.style.display = 'grid';
    } else {
        statusEl.textContent = '답글 대기';
        statusEl.className = 'reply-status';
        noReply.style.display = 'grid';
        hasReplyEl.style.display = 'none';
    }
}

function closePanel() {
    document.getElementById('detailPanel').classList.remove('active');
    currentReviewId = null;
}

function createReply() {
    if (!currentReviewId) return;

    var content = document.getElementById('replyTextarea').value.trim();

    if (!content) {
        showReplyMsg('답글 내용을 입력해주세요.', 'msg-warning');
        return;
    }

    var review = allReviews.find(function (r) {
        return Number(r.reviewId) === Number(currentReviewId);
    });

    if (!review) return;

    fetch('/api/owner/reviews/' + currentReviewId + '/reply?orderId=' + review.orderId, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({replyContent: content})
    })
        .then(function (res) {
            if (!res.ok) throw new Error('답글 등록 실패');
            return res.json();
        })
        .then(function () {
            showReplyMsg('답글이 등록되었습니다.', 'msg-success');
            setReplyState(true);
            loadReviews();
        })
        .catch(function () {
            showReplyMsg('등록에 실패했습니다.', 'msg-error');
        });
}

function updateReply() {
    if (!currentReviewId) return;

    var content = document.getElementById('replyTextarea').value.trim();

    if (!content) {
        showReplyMsg('수정할 답글 내용을 입력해주세요.', 'msg-warning');
        return;
    }

    fetch('/api/owner/reviews/' + currentReviewId + '/reply', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({replyContent: content})
    })
        .then(function (res) {
            if (!res.ok) throw new Error('답글 수정 실패');
            return res.json();
        })
        .then(function () {
            showReplyMsg('답글이 수정되었습니다.', 'msg-success');
            loadReviews();
        })
        .catch(function () {
            showReplyMsg('수정에 실패했습니다.', 'msg-error');
        });
}

function deleteReply() {
    if (!currentReviewId) return;

    if (!confirm('답글을 삭제하시겠습니까?')) return;

    fetch('/api/owner/reviews/' + currentReviewId + '/reply', {
        method: 'DELETE'
    })
        .then(function (res) {
            if (!res.ok) throw new Error('답글 삭제 실패');
            return res.json();
        })
        .then(function () {
            showReplyMsg('답글이 삭제되었습니다.', 'msg-success');
            document.getElementById('replyTextarea').value = '';
            document.getElementById('replyCharCount').textContent = '0';
            setReplyState(false);
            loadReviews();
        })
        .catch(function () {
            showReplyMsg('삭제에 실패했습니다.', 'msg-error');
        });
}

function showReplyMsg(text, type) {
    var el = document.getElementById('replyMsg');
    el.textContent = text;
    el.className = 'reply-msg ' + type;
    el.style.display = 'block';
}

function prevPage() {
    if (currentPage <= 0) return;

    currentPage--;
    closePanel();
    loadReviews();
}

function nextPage() {
    if (currentPage >= totalPages - 1) return;

    currentPage++;
    closePanel();
    loadReviews();
}

function updatePagination() {
    totalPages = Math.max(1, Number(totalPages || Math.ceil(totalCount / PAGE_SIZE) || 1));

    document.getElementById('pageInfo').textContent =
        (currentPage + 1) + ' / ' + totalPages + ' 페이지';

    document.getElementById('btnPrev').disabled = currentPage <= 0;
    document.getElementById('btnNext').disabled = currentPage >= totalPages - 1;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}