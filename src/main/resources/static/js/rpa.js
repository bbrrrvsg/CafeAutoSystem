function sendRpaMail(targetEmail, orderId) {
    console.log("[RPA 엔진 가동] 수신 테스트 메일: " + targetEmail + " | 발주 ID: " + orderId);

    const loadingOverlay = document.getElementById("rpaLoading");
    if (loadingOverlay) {
        loadingOverlay.style.display = "flex";
    }

    fetch('/api/jms-rpa/send-test?to=' + encodeURIComponent(targetEmail) + '&orderItemId=' + orderId)
        .then(response => {
            if (loadingOverlay) {
                loadingOverlay.style.display = "none";
            }

            if (response.ok) {
                const toast = document.getElementById("rpaToast");
                if (toast) {
                    toast.style.display = "block";
                    toast.style.opacity = "1";
                    setTimeout(() => {
                        toast.style.opacity = "0";
                        setTimeout(() => {
                            toast.style.display = "none";
                            toast.style.opacity = "1";
                            location.reload();
                        }, 500);
                    }, 3000);
                } else {
                    // 만약 토스트 엘리먼트가 없다면 모달을 닫고 바로 리로드 처리
                    if (typeof closeOrderModal === 'function') {
                        closeOrderModal();
                    }
                    location.reload();
                }

                if (toast && typeof closeOrderModal === 'function') {
                    closeOrderModal();
                }
            } else {
                throw new Error("RPA 메일 엔진 연동 실패 (서버 에러)");
            }
        })
        .catch(error => {
            if (loadingOverlay) {
                loadingOverlay.style.display = "none";
            }
            console.error("RPA 장애 발생:", error);
            alert("RPA 메일 전송 중 장애 발생: " + error.message);
        });
}