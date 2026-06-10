var allReviews = [];
var currentTab = 'all';
var currentReviewId = null;
var currentPage = 0;
var totalCount = 0;
var totalPages = 1;
var PAGE_SIZE = 10;
var pollingTimer = null;

document.addEventListener('DOMContentLoaded', function () {
    loadReviews(false);

    pollingTimer = setInterval(function () {
        loadReviews(true);
    }, 3000);

    var textarea = document.getElementById('replyTextarea');
    if (textarea) {
        textarea.addEventListener('input', function () {
            document.getElementById('replyCharCount').textContent = this.value.length;
        });
    }
});

function loadReviews(isPolling) {
    if (!isPolling) {
        document.getElementById('reviewTableBody').innerHTML =
            '<tr><td colspan="6" class="table-empty">로딩 중...</td></tr>';
    }

    fetch('/api/owner/reviews?page=' + currentPage + '&size=' + PAGE_SIZE)
        .then(function (res) {
            if (!res.ok) {
                throw new Error('리뷰 목록 조회 실패: ' + res.status);
            }
            return res.json();
        })
        .then(function (data) {
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

            if (currentReviewId) {
                refreshSelectedDetail();
            }
        })
        .catch(function (error) {
            console.error(error);

            if (isPolling) {
                return;
            }

            allReviews = [];
            totalCount = 0;
            totalPages = 1;

            updateStats();
            updatePagination();

            document.getElementById('reviewTableBody').innerHTML =
                '<tr><td colspan="6" class="table-error">리뷰를 불러오지 못했습니다.</td></tr>';
        });
}

function normalizeReview(r) {
    return {
        reviewId: r.reviewId,
        orderId: r.orderId,
        reviewContent: r.reviewContent || '',
        createdAt: r.createdAt || r.customerCreatedAt || '',
        status: r.status || 'REVIEW_RECEIVED',

        analysisStatus: r.analysisStatus || 'PENDING',
        analysisCompleted: Boolean(r.analysisCompleted),
        analysisResultJson: r.analysisResultJson || '',
        analyzedAt: r.analyzedAt || '',

        hasReply: Boolean(r.hasReply),
        replyContent: r.replyContent || ''
    };
}

function updateStats() {
    var pending = allReviews.filter(function (r) {
        return !r.hasReply;
    }).length;

    var done = allReviews.filter(function (r) {
        return r.hasReply;
    }).length;

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
        tbody.innerHTML = '<tr><td colspan="6" class="table-empty">리뷰가 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = filtered.map(function (r) {
        var replyBadge = r.hasReply
            ? '<em class="review-badge done">답글 완료</em>'
            : '<em class="review-badge wait">답글 대기</em>';

        var date = r.createdAt ? String(r.createdAt).substring(0, 16) : '-';

        var safeContent = escapeHtml(r.reviewContent || '-');
        var shortContent = safeContent.length > 28
            ? safeContent.substring(0, 28) + '…'
            : safeContent;

        var selectedClass = Number(currentReviewId) === Number(r.reviewId)
            ? ' class="selected"'
            : '';

        return '<tr onclick="selectReview(' + r.reviewId + ')" id="row-' + r.reviewId + '"' + selectedClass + '>' +
            '<td>' + r.reviewId + '</td>' +
            '<td>' + r.orderId + '</td>' +
            '<td>' + shortContent + '</td>' +
            '<td>' + date + '</td>' +
            '<td>' + renderAnalysisCell(r) + '</td>' +
            '<td>' + replyBadge + '</td>' +
            '</tr>';
    }).join('');
}

function renderAnalysisCell(review) {
    if (review.analysisStatus === 'PENDING') {
        return '<span class="ai-status pending">분석 대기</span>';
    }

    if (review.analysisStatus === 'PROCESSING') {
        return '<span class="ai-status processing"><span class="mini-spinner"></span>분석중</span>';
    }

    if (review.analysisStatus === 'FAILED') {
        return '<span class="ai-status failed">분석 실패</span>';
    }

    if (review.analysisStatus === 'COMPLETED') {
        return renderAnalysisSummary(review.analysisResultJson, true);
    }

    return '<span class="ai-status pending">대기</span>';
}

function selectReview(reviewId) {
    currentReviewId = reviewId;

    document.querySelectorAll('.review-table tbody tr').forEach(function (tr) {
        tr.classList.remove('selected');
    });

    var row = document.getElementById('row-' + reviewId);
    if (row) {
        row.classList.add('selected');
    }

    var review = allReviews.find(function (r) {
        return Number(r.reviewId) === Number(reviewId);
    });

    if (!review) return;

    renderSelectedReview(review, true);
}

function refreshSelectedDetail() {
    var review = allReviews.find(function (r) {
        return Number(r.reviewId) === Number(currentReviewId);
    });

    if (!review) {
        return;
    }

    var row = document.getElementById('row-' + currentReviewId);
    if (row) {
        row.classList.add('selected');
    }

    renderSelectedReview(review, false);
}

function renderSelectedReview(review, resetReplyInput) {
    document.getElementById('detailReviewId').textContent = review.reviewId;
    document.getElementById('detailOrderId').textContent = review.orderId;
    document.getElementById('detailCreatedAt').textContent = review.createdAt
        ? String(review.createdAt).substring(0, 16)
        : '-';

    document.getElementById('detailContent').textContent = review.reviewContent || '-';

    renderDetailAnalysis(review);

    if (resetReplyInput) {
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
    }

    document.getElementById('detailPanel').classList.add('active');
}

function renderDetailAnalysis(review) {
    var statusEl = document.getElementById('detailAnalysisStatus');
    var box = document.getElementById('detailAnalysisBox');

    if (!statusEl || !box) {
        return;
    }

    if (review.analysisStatus === 'PENDING') {
        statusEl.textContent = '분석 대기';
        statusEl.className = 'ai-analysis-status pending';
        box.innerHTML = '<span class="ai-status pending">AI 분석 대기중</span>';
        return;
    }

    if (review.analysisStatus === 'PROCESSING') {
        statusEl.textContent = '분석중';
        statusEl.className = 'ai-analysis-status processing';
        box.innerHTML = '<span class="ai-status processing"><span class="mini-spinner"></span>AI 분석중</span>';
        return;
    }

    if (review.analysisStatus === 'FAILED') {
        statusEl.textContent = '분석 실패';
        statusEl.className = 'ai-analysis-status failed';
        box.innerHTML = '<span class="ai-status failed">분석 실패</span>';
        return;
    }

    if (review.analysisStatus === 'COMPLETED') {
        statusEl.textContent = '분석 완료';
        statusEl.className = 'ai-analysis-status completed';
        box.innerHTML = renderAnalysisSummary(review.analysisResultJson, false);
        return;
    }

    statusEl.textContent = '분석 대기';
    statusEl.className = 'ai-analysis-status pending';
    box.innerHTML = '<span class="ai-status pending">AI 분석 대기중</span>';
}

function parseAnalysis(analysisResultJson) {
    if (!analysisResultJson) {
        return null;
    }

    try {
        return JSON.parse(analysisResultJson);
    } catch (e) {
        console.error('analysisResultJson 파싱 실패', e);
        return null;
    }
}

/**
 * AI 분석 결과 렌더링
 *
 * 정책:
 * - overallSentiment는 화면에 표시하지 않는다.
 * - riskLevel이 HIGH일 때만 "🚨 빠른 대응 필요"를 표시한다.
 * - 카테고리 태그는 기존처럼 그대로 표시한다.
 * - riskLevel이 HIGH이고 categories가 비어 있어도 "빠른 대응 필요"는 표시한다.
 */
function renderAnalysisSummary(analysisResultJson, compact) {
    var analysis = parseAnalysis(analysisResultJson);

    if (!analysis) {
        return '<span class="ai-status pending">분석 결과 없음</span>';
    }

    var html = [];

    if (analysis.riskLevel === 'HIGH') {
        html.push(
            '<div class="ai-risk-line">' +
            '<span class="ai-risk-high">🚨 빠른 대응 필요</span>' +
            '</div>'
        );
    }

    var categories = Array.isArray(analysis.categories)
        ? analysis.categories
        : [];

    if (categories.length === 0) {
        if (analysis.riskLevel === 'HIGH') {
            return '<div class="ai-analysis-result">' + html.join('') + '</div>';
        }

        return '<span class="ai-status pending">분석 결과 없음</span>';
    }

    var visibleCategories = compact
        ? categories.slice(0, 2)
        : categories;

    var categoryHtml = visibleCategories.map(function (item) {
        var categoryName = toKoreanCategory(item.category);
        var sentimentName = toKoreanSentiment(item.sentiment);
        var sentimentClass = String(item.sentiment || 'NEUTRAL').toLowerCase();

        return '<span class="category-badge ' + sentimentClass + '">' +
            categoryName + ' ' + sentimentName +
            '</span>';
    }).join('');

    if (compact && categories.length > 2) {
        categoryHtml += '<span class="category-badge more">+' + (categories.length - 2) + '</span>';
    }

    html.push(
        '<div class="ai-category-list">' +
        categoryHtml +
        '</div>'
    );

    return '<div class="ai-analysis-result">' + html.join('') + '</div>';
}

function toKoreanCategory(category) {
    var map = {
        TASTE: '맛',
        SERVICE: '서비스',
        PRICE: '가격',
        ATMOSPHERE: '분위기',
        CLEANLINESS: '위생',
        FACILITY: '시설',
        WAITING: '대기',
        SEAT: '좌석',
        PARKING: '주차',
        REVISIT: '재방문'
    };

    return map[category] || category;
}

function toKoreanSentiment(sentiment) {
    var map = {
        POSITIVE: '긍정',
        NEGATIVE: '부정',
        NEUTRAL: '중립'
    };

    return map[sentiment] || sentiment;
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
            loadReviews(false);
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
            loadReviews(false);
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
            loadReviews(false);
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
    loadReviews(false);
}

function nextPage() {
    if (currentPage >= totalPages - 1) return;

    currentPage++;
    closePanel();
    loadReviews(false);
}

function updatePagination() {
    totalPages = Math.max(1, Number(totalPages || Math.ceil(totalCount / PAGE_SIZE) || 1));

    document.getElementById('pageInfo').textContent =
        (currentPage + 1) + ' / ' + totalPages + ' 페이지';

    document.getElementById('btnPrev').disabled = currentPage <= 0;
    document.getElementById('btnNext').disabled = currentPage >= totalPages - 1;
}

function escapeHtml(value) {
    if (value == null) {
        return '';
    }

    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}