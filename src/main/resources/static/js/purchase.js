document.addEventListener("DOMContentLoaded", function() {

    // =================================================================
    // 🚀 1. AI 재분석 API 호출 (PyTorch LSTM 실시간 재가동)
    // =================================================================
    const reanalyzeBtn = document.getElementById("btn-ai-reanalyze");

    if (reanalyzeBtn) {
        reanalyzeBtn.addEventListener("click", function() {
            if (!confirm("PyTorch 모델을 다시 가동하여 실시간 데이터 재분석을 요청하시겠습니까?\n(수 초의 연산 시간이 소요될 수 있습니다.)")) {
                return;
            }

            // 디버깅 및 중복 클릭 락(Lock) 걸기
            reanalyzeBtn.disabled = true;
            reanalyzeBtn.innerText = "AI 딥러닝 분석 중...";

            // 백엔드에 개설할 재분석 비동기 엔드포인트 호출
            fetch("/api/order/reanalyze", {
                method: "POST"
            })
                .then(response => {
                    if (!response.ok) throw new Error("AI 분석 서버 응답 실패");
                    return response.json();
                })
                .then(data => {
                    if (data.status === "SUCCESS") {
                        alert(data.message);
                        location.reload(); // 최신 데이터 바인딩을 위한 화면 새로고침
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
        reanalyzeBtn.disabled = false;
        reanalyzeBtn.innerText = "AI 재분석 API 호출";
    }


    // =================================================================
    // 📦 2. 승인 및 발주 전송 (PENDING 장부 일괄 생성)
    // =================================================================
    const submitBtn = document.getElementById("btn-submit-bulk-order");

    if (submitBtn) {
        submitBtn.addEventListener("click", function() {
            const rows = document.querySelectorAll(".ai-order-row");
            const orderListPayload = [];

            // 테이블 로우를 순회하며 데이터 정제 수거
            rows.forEach(row => {
                const ingredientId = row.getAttribute("data-ingredient-id");
                const suggestedQty = parseInt(row.getAttribute("data-suggested-qty"), 10);

                // 발주 제안 수량이 1개 이상인 경우만 전송 대상에 포함
                if (ingredientId && suggestedQty > 0) {
                    orderListPayload.push({
                        ingredientId: parseInt(ingredientId, 10),
                        suggestedQty: suggestedQty
                    });
                }
            });

            if (orderListPayload.length === 0) {
                alert("AI가 제안한 발주 추천 수량이 모두 0개이므로 전송할 내역이 없습니다.");
                return;
            }

            if (!confirm(`총 ${orderListPayload.length}개의 자재를 AI 추천 수량 기준으로 일괄 발주 등록(PENDING)하시겠습니까?`)) {
                return;
            }

            submitBtn.disabled = true;
            submitBtn.innerText = "발주 처리 중...";

            // 백엔드 일괄 등록 API 호출
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
                        alert(data.message);
                        // 성공 시 대기(PENDING) 리스트 페이지로 이동시키거나 대시보드 리로드
                        location.reload();
                    } else {
                        alert("발주 등록 실패: " + data.message);
                        resetSubmitButton();
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("서버 통신 중 에러가 발생했습니다. 장부를 확인하세요.");
                    resetSubmitButton();
                });
        });
    }

    function resetSubmitButton() {
        submitBtn.disabled = false;
        submitBtn.innerText = "승인 및 발주전송";
    }
});