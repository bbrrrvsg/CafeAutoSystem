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
                document.getElementById('writeSection').style.display = 'none';
                document.getElementById('doneContent').textContent    = content;
                document.getElementById('doneSection').style.display  = 'block';
            })
            .catch(function (err) {
                showMsg(err.message || '리뷰 등록에 실패했습니다.', 'msg-error');
                document.getElementById('submitBtn').disabled = false;
            });
    });
});

function showMsg(text, type) {
    var el = document.getElementById('resultMsg');
    el.textContent   = text;
    el.className     = 'result-msg ' + type;
    el.style.display = 'block';
}
