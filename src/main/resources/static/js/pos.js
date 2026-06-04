/* ============================================
   pos.js — POS 장바구니 · 주문 · 영수증 팝업
============================================ */

(function initPos() {
    var cart = []; // { menuId, menuName, price, quantity }

    // ── 제품 카드 클릭 → 장바구니 추가 ──────────────
    document.querySelectorAll('.product-card').forEach(function (card) {
        card.addEventListener('click', function () {
            var menuId   = parseInt(card.dataset.menuId);
            var menuName = card.querySelector('.p-name').textContent.trim();
            var price    = parseInt(card.dataset.price);
            addToCart(menuId, menuName, price);
        });
    });

    function addToCart(menuId, menuName, price) {
        var existing = cart.find(function (item) { return item.menuId === menuId; });
        if (existing) {
            existing.quantity++;
        } else {
            cart.push({ menuId: menuId, menuName: menuName, price: price, quantity: 1 });
        }
        renderCart();
    }

    // ── 장바구니 렌더링 ───────────────────────────
    function renderCart() {
        var cartItems = document.getElementById('cartItems');
        var cartTotal = document.getElementById('cartTotal');

        if (cart.length === 0) {
            cartItems.innerHTML =
                '<div style="text-align:center;color:#aaa;padding:32px 0;font-size:13px;">장바구니가 비어 있습니다.</div>';
            cartTotal.textContent = '0원';
            return;
        }

        cartItems.innerHTML = cart.map(function (item) {
            return '<div class="cart-item">' +
                '<div>' +
                '<span class="ci-name">' + item.menuName + '</span>' +
                '<span class="ci-qty">× ' + item.quantity + '</span>' +
                '</div>' +
                '<div style="display:flex;align-items:center;gap:6px;">' +
                '<span class="ci-price">' + (item.price * item.quantity).toLocaleString() + '</span>' +
                '<button onclick="POS.remove(' + item.menuId + ')" ' +
                'style="background:none;border:none;cursor:pointer;color:#e74c3c;font-size:18px;line-height:1;padding:0;">×</button>' +
                '</div>' +
                '</div>';
        }).join('');

        var total = cart.reduce(function (sum, item) {
            return sum + item.price * item.quantity;
        }, 0);
        cartTotal.textContent = total.toLocaleString() + '원';
    }

    // ── 초기화 ────────────────────────────────────
    document.getElementById('clearBtn').addEventListener('click', function () {
        cart = [];
        renderCart();
    });

    // ── 결제하기 → POST /api/orders → 영수증 팝업 ─
    document.getElementById('payBtn').addEventListener('click', function () {
        if (cart.length === 0) {
            alert('장바구니에 상품을 담아주세요.');
            return;
        }

        var btn = this;
        btn.disabled = true;
        btn.textContent = '처리 중...';

        var items = cart.map(function (item) {
            return { menuId: item.menuId, quantity: item.quantity };
        });

        fetch('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ items: items })
        })
            .then(function (res) {
                if (!res.ok) throw new Error('주문 생성 실패 (' + res.status + ')');
                return res.json();
            })
            .then(function (order) {
                cart = [];
                renderCart();
                window.open(
                    '/receipt?orderId=' + order.orderId,
                    'receipt_' + order.orderId,
                    'width=480,height=720,scrollbars=yes,resizable=yes'
                );
            })
            .catch(function (err) {
                alert('결제 처리 중 오류가 발생했습니다.\n' + err.message);
                console.error(err);
            })
            .finally(function () {
                btn.disabled = false;
                btn.textContent = '결제하기';
            });
    });

    // ── 외부(인라인 onclick)에서 remove 접근용 ────
    window.POS = {
        remove: function (menuId) {
            cart = cart.filter(function (item) { return item.menuId !== menuId; });
            renderCart();
        }
    };

    renderCart();
})();
