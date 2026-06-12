/**
 * AI 발주 관리 대시보드 전용 통합 스크립트
 */
document.addEventListener("DOMContentLoaded", function() {

    // =================================================================
    // 1. 날짜 설정 및 초기화
    // =================================================================
    function getFormattedDate(date) {
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        return yyyy + '-' + mm + '-' + dd;
    }

    const today = new Date();

    const snapshotTarget = document.getElementById('predictSnapshot');
    if (snapshotTarget) {
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        snapshotTarget.textContent = getFormattedDate(yesterday) + ' 22:00 (배치)';
    }

    const orderDateInput = document.getElementById('orderTargetDate');
    if (orderDateInput) {
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        orderDateInput.value = getFormattedDate(tomorrow);
    }

    const rangeInput = document.getElementById('learningLogRange');
    if (rangeInput) {
        rangeInput.value = "정기 가중치 학습 모델 적용 (매월 1회 정밀 갱신)";
    }


    // =================================================================
    // 2. AI 재분석 API 호출
    // =================================================================
    const reanalyzeBtn = document.getElementById("btn-ai-reanalyze");

    if (reanalyzeBtn) {
        reanalyzeBtn.addEventListener("click", function() {
            if (!confirm("최신 현재고 기준의 실시간 데이터 재분석을 요청하시겠습니까?")) {
                return;
            }

            reanalyzeBtn.disabled = true;
            reanalyzeBtn.innerText = "AI 분석 데이터 수거 중...";

            fetch("/api/order/reanalyze", {
                method: "POST"
            })
                .then(response => {
                    if (!response.ok) throw new Error("AI 분석 서버 응답 실패");
                    return response.json();
                })
                .then(data => {
                    if (data.status === "SUCCESS") {
                        alert("최신 재고 기준으로 AI 추천 수량이 갱신되었습니다.");
                        location.reload();
                    } else {
                        alert("AI 재분석 실패: " + data.message);
                        resetReanalyzeButton();
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("AI 분석 서버 통신 중 시스템 장애가 발생했습니다.");
                    resetReanalyzeButton();
                });
        });
    }

    function resetReanalyzeButton() {
        if (reanalyzeBtn) {
            reanalyzeBtn.disabled = false;
            reanalyzeBtn.innerText = "AI 재분석";
        }
    }


    // =================================================================
    // 3. 테이블 동적 이벤트 (수정 / 제외) 및 합계 계산기
    // =================================================================

    function recalculateTotalOrderPrice() {
        let grandTotal = 0;
        document.querySelectorAll('.ai-order-row').forEach(row => {
            const priceText = row.querySelector('td:nth-child(8)').textContent.replace(/[^0-9]/g, '');
            grandTotal += parseInt(priceText) || 0;
        });

        const formattedPrice = grandTotal.toLocaleString() + '원';

        const heroAccent = document.querySelector('.hero .accent');
        if (heroAccent) heroAccent.textContent = formattedPrice;

        const footerTotal = document.querySelector('tr[style*="background: #FAFAF9;"] td.num');
        if (footerTotal) footerTotal.textContent = formattedPrice;
    }

    document.querySelectorAll('.ai-order-row').forEach(row => {
        const ingredientId = parseInt(row.dataset.ingredientId, 10);

        row.querySelector('.danger').addEventListener('click', function() {
            if (confirm(`'${row.querySelector('strong').textContent}' 품목을 이번 발주에서 제외하시겠습니까?`)) {
                row.remove();
                recalculateTotalOrderPrice();
            }
        });

        row.querySelector('button:not(.danger)').addEventListener('click', function() {
            const currentQty = row.dataset.suggestedQty;
            const newQtyStr = prompt("변경할 발주 제안량을 입력하세요 (숫자만 입력):", currentQty);

            if (newQtyStr === null) return;

            const newQty = parseInt(newQtyStr, 10);
            if (isNaN(newQty) || newQty < 0) {
                alert('올바른 수량을 입력해주세요.');
                return;
            }

            row.dataset.suggestedQty = newQty;

            const qtyTd = row.querySelector('td:nth-child(5)');
            const unitStr = qtyTd.textContent.replace(/[0-9,\s]/g, '');
            qtyTd.textContent = newQty.toLocaleString() + ' ' + unitStr;

            const unitPrice = parseInt(row.querySelector('td:nth-child(6)').textContent.replace(/[^0-9]/g, ''), 10);
            let newTotalPrice = 0;

            if (ingredientId === 1 || ingredientId === 4) {
                let packQty = Math.ceil(newQty / 1000.0);
                newTotalPrice = packQty * unitPrice;
            } else {
                newTotalPrice = newQty * unitPrice;
            }

            row.querySelector('td:nth-child(8)').textContent = newTotalPrice.toLocaleString() + '원';

            recalculateTotalOrderPrice();
        });
    });


    // =================================================================
    // 4. 승인 및 발주 전송
    // =================================================================
    const submitBtn = document.getElementById("btn-submit-bulk-order");

    if (submitBtn) {
        submitBtn.addEventListener("click", function() {
            const rows = document.querySelectorAll(".ai-order-row");
            const orderListPayload = [];
            let validationFailed = false;

            rows.forEach(row => {
                const ingredientId = row.dataset.ingredientId;
                const suggestedQty = parseInt(row.dataset.suggestedQty, 10);
                // 입력 필드에서 유통기한 값 추출
                const expDate = row.querySelector('.order-exp-date').value;

                if (ingredientId && suggestedQty > 0) {
                    // 수량이 존재하지만 유통기한이 누락된 경우 전송 차단 규칙 적용
                    if (!expDate) {
                        const name = row.querySelector('strong').textContent;
                        alert(`'${name}' 품목의 유통기한을 지정해 주세요.`);
                        validationFailed = true;
                        return;
                    }

                    orderListPayload.push({
                        ingredientId: parseInt(ingredientId, 10),
                        suggestedQty: suggestedQty,
                        expirationDate: expDate
                    });
                }
            });

            if (validationFailed) return;

            if (orderListPayload.length === 0) {
                alert("AI 발주 추천 수량이 없거나 제외되어 전송할 내역이 존재하지 않습니다.");
                return;
            }

            if (!confirm(`총 ${orderListPayload.length}개의 자재를 지정한 유통기한 및 수량 기준으로 일괄 발주 등록하시겠습니까?`)) {
                return;
            }

            submitBtn.disabled = true;
            submitBtn.innerText = "발주 처리 중...";

            fetch("/api/order/bulk-ai", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(orderListPayload)
            })
                .then(response => {
                    if (!response.ok) throw new Error("발주 요청 처리 실패");
                    return response.json();
                })
                .then(data => {
                    if (data.status === "SUCCESS") {
                        alert("일괄 발주 장부 생성이 완료되었습니다.");
                        clearAiOrderTable();
                    } else {
                        alert("발주 등록 실패: " + data.message);
                        resetSubmitButton();
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("서버 통신 중 에러가 발생했습니다. 장부 적재를 확인하세요.");
                    resetSubmitButton();
                });
        });
    }

    function resetSubmitButton() {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerText = "승인 및 발주생성";
        }
    }

    function clearAiOrderTable() {
        const tbody = document.querySelector('.data-table tbody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" class="text-center" style="padding: 60px; color: var(--text-muted); font-weight: 500; background-color: #fafafa;">
                        전송이 완료되었거나 처리할 AI 추천 발주 항목이 존재하지 않습니다.
                    </td>
                </tr>
            `;
        }
        const heroAccent = document.querySelector('.hero .accent');
        if (heroAccent) heroAccent.textContent = '0원';

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerText = "발주 전송 완료";
        }
    }
});