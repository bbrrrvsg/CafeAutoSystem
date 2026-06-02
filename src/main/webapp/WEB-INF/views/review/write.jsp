<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>리뷰 작성 | Smart Cafe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/review-write.css">
</head>
<body>
<main class="mobile-stage">
    <section class="phone-screen">
        <header class="app-topbar">
            <button type="button" class="back-btn" onclick="history.back()">‹</button>
            <div class="brand">
                <div class="brand-icon">☕</div>
                <div>
                    <strong>Smart Cafe</strong>
                    <span>CafeOS</span>
                </div>
            </div>
            <button type="button" class="bell-btn">⌕</button>
        </header>

        <section class="hero">
            <div class="hero-icon">💬</div>
            <h1>주문은 어떠셨나요?</h1>
            <p>남겨주신 리뷰는 해당 주문에 자동으로 저장됩니다.</p>
        </section>

        <section class="order-card">
            <div class="order-row">
                <div class="row-icon">#</div>
                <div class="row-copy">
                    <span>주문번호</span>
                    <strong id="orderNumber">-</strong>
                </div>
            </div>
            <div class="order-row">
                <div class="row-icon">⌚</div>
                <div class="row-copy">
                    <span>주문일시</span>
                    <strong id="orderDate">-</strong>
                </div>
            </div>
            <div class="order-row">
                <div class="row-icon">☕</div>
                <div class="row-copy">
                    <span>주문 메뉴</span>
                    <strong id="orderMenus">-</strong>
                </div>
            </div>
            <div class="order-row total">
                <div class="row-icon">₩</div>
                <div class="row-copy">
                    <span>총 결제금액</span>
                    <strong id="orderPrice">-</strong>
                </div>
            </div>
        </section>

        <section class="review-card">
            <div class="section-title">
                <h2>리뷰 내용</h2>
                <span id="charCount">0 / 500</span>
            </div>
            <textarea id="reviewContent" maxlength="500"
                      placeholder="카페 이용 경험을 자유롭게 남겨주세요.&#10;맛, 서비스, 분위기 등 어떤 내용도 좋아요!"></textarea>
            <div class="notice">
                <span>i</span>
                주문 건당 리뷰는 1회만 작성할 수 있습니다.
            </div>

            <div id="resultMsg" class="result-msg"></div>

            <button type="button" class="submit-btn" id="submitBtn">리뷰 등록</button>
            <button type="button" class="later-btn" onclick="history.back()">나중에 작성</button>
        </section>

        <div class="home-indicator"></div>
    </section>
</main>

<script>
    var orderId = null;

    document.addEventListener('DOMContentLoaded', function () {
        var params = new URLSearchParams(window.location.search);
        orderId = params.get('orderId');

        if (!orderId) {
            document.getElementById('orderNumber').textContent = '주문 정보를 찾을 수 없습니다.';
            document.getElementById('submitBtn').disabled = true;
            return;
        }

        fetch('/api/orders/' + orderId)
            .then(function (res) {
                if (!res.ok) throw new Error('주문 조회 실패');
                return res.json();
            })
            .then(function (order) {
                document.getElementById('orderNumber').textContent = order.orderId;
                document.getElementById('orderDate').textContent = order.createdAt
                    ? order.createdAt.replace('T', ' ').substring(0, 16) : '-';
                document.getElementById('orderPrice').textContent =
                    order.orderPrice.toLocaleString() + '원';
                var menus = (order.orderDetails || [])
                    .map(function (d) { return d.menuName + ' ' + d.quantity + '잔'; })
                    .join(', ');
                document.getElementById('orderMenus').textContent = menus || '-';
            })
            .catch(function (err) {
                document.getElementById('orderNumber').textContent = '조회 실패';
                console.error(err);
            });

        document.getElementById('reviewContent').addEventListener('input', function () {
            document.getElementById('charCount').textContent = this.value.length + ' / 500';
        });

        document.getElementById('submitBtn').addEventListener('click', function () {
            var content = document.getElementById('reviewContent').value.trim();
            if (!content) { showMsg('리뷰 내용을 입력해주세요.', 'msg-warning'); return; }

            document.getElementById('submitBtn').disabled = true;

            fetch('/api/reviews', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({orderId: parseInt(orderId), reviewContent: content})
            })
                .then(function (res) {
                    if (!res.ok) return res.json().then(function (e) { throw new Error(e.message || '등록 실패'); });
                    return res.json();
                })
                .then(function () {
                    showMsg('리뷰가 등록되었습니다. 감사합니다! 😊', 'msg-success');
                    document.getElementById('reviewContent').disabled = true;
                })
                .catch(function (err) {
                    showMsg(err.message || '리뷰 등록에 실패했습니다.', 'msg-error');
                    document.getElementById('submitBtn').disabled = false;
                });
        });
    });

    function showMsg(text, type) {
        var el = document.getElementById('resultMsg');
        el.textContent = text;
        el.className = 'result-msg ' + type;
        el.style.display = 'block';
    }
</script>
</body>
</html>
