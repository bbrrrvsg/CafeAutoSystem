<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>디지털 영수증 | Smart Cafe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/receipt.css">
</head>
<body>
<div class="receipt-page">
    <div class="popup-shell">
        <div class="popup-titlebar">
            <div class="title-left">
                <span class="title-dot"></span>
                <span>디지털 영수증</span>
            </div>
            <button type="button" class="window-close" onclick="window.close()">×</button>
        </div>

        <main class="receipt-card">
            <section class="brand-block">
                <div class="brand-icon">☕</div>
                <div class="brand-name">Smart Cafe</div>
                <div class="brand-sub">CafeOS Digital Receipt</div>
            </section>

            <section class="receipt-head">
                <h1>디지털 영수증</h1>
                <p>주문해 주셔서 감사합니다. 좋은 하루 되세요!</p>
            </section>

            <section class="order-info">
                <div class="info-row">
                    <span>주문번호</span>
                    <strong id="orderNumber">-</strong>
                </div>
                <div class="info-row">
                    <span>결제 일시</span>
                    <strong id="orderDate">-</strong>
                </div>
                <div class="info-row">
                    <span>결제 상태</span>
                    <strong><em class="paid-badge">결제 완료</em></strong>
                </div>
            </section>

            <section class="receipt-items">
                <div class="item-header">
                    <span>메뉴명</span>
                    <span>수량</span>
                    <span>금액</span>
                </div>
                <div id="itemRows"></div>
            </section>

            <section class="total-box">
                <div class="total-final">
                    <span>총 결제금액</span>
                    <strong id="orderPrice">-</strong>
                </div>
            </section>

            <section class="qr-section">
                <div class="qr-box">
                    <img id="qrImage" src="" alt="QR 코드" class="qr-img">
                    <div id="qrPlaceholder" class="qr-placeholder">QR 로딩 중...</div>
                </div>
                <div class="qr-copy">
                    <h2>리뷰 작성하고 쿠폰 받아가세요!</h2>
                    <p>휴대폰 카메라로 QR을 스캔하면<br>리뷰 작성 페이지로 이동합니다.</p>
                    <div class="review-url-bar">
                        <span id="reviewUrlText" class="review-url-text">-</span>
                        <button id="copyUrlBtn" class="copy-url-btn" onclick="copyReviewUrl()">복사</button>
                    </div>
                </div>
            </section>

            <section class="receipt-actions">
                <button type="button" class="btn primary" onclick="window.print()">영수증 인쇄</button>
                <button type="button" class="btn secondary" onclick="window.close()">닫기</button>
            </section>
        </main>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        var params = new URLSearchParams(window.location.search);
        var orderId = params.get('orderId');

        if (!orderId) {
            document.getElementById('orderNumber').textContent = '주문번호를 찾을 수 없습니다.';
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

                var itemRows = document.getElementById('itemRows');
                (order.orderDetails || []).forEach(function (item) {
                    var row = document.createElement('div');
                    row.className = 'item-row';
                    row.innerHTML =
                        '<span>' + item.menuName + '</span>' +
                        '<span>' + item.quantity + '</span>' +
                        '<span>' + item.totalPrice.toLocaleString() + '원</span>';
                    itemRows.appendChild(row);
                });

                if (order.qrUrl) {
                    var img = document.getElementById('qrImage');
                    img.src = order.qrUrl;
                    img.style.display = 'block';
                    document.getElementById('qrPlaceholder').style.display = 'none';
                }

                document.getElementById('reviewUrlText').textContent =
                    window.location.origin + '/review/write?orderId=' + order.orderId;
            })
            .catch(function (err) {
                document.getElementById('orderNumber').textContent = '조회 실패';
                console.error(err);
            });
    });

    function copyReviewUrl() {
        var url = document.getElementById('reviewUrlText').textContent;
        if (!url || url === '-') return;
        navigator.clipboard.writeText(url).then(function () {
            var btn = document.getElementById('copyUrlBtn');
            btn.textContent = '복사됨 ✓';
            btn.style.color = '#2e7d32';
            setTimeout(function () {
                btn.textContent = '복사';
                btn.style.color = '';
            }, 2000);
        }).catch(function () {
            window.prompt('아래 URL을 복사하세요:', url);
        });
    }
</script>
</body>
</html>
