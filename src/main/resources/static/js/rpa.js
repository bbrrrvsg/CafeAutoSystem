function sendRpaMail(targetEmail) {
    console.log("🚀 [RPA 엔진 가동] 대상 거래처 이메일 주소: " + targetEmail);

    // 1화면 중앙에 "메일 쏘는 중..."  로딩 오버레이 켜기
    const loadingOverlay = document.getElementById("rpaLoading");
    if (loadingOverlay) {
        loadingOverlay.style.display = "flex";
    }

    // 백엔드 메일 전송 API 비동기(Fetch) 호출
    fetch('/api/jms-rpa/send-test?to=' + encodeURIComponent(targetEmail))
        .then(response => {
            // 메일 전송 통신이 끝나면 우선 로딩창부터 끄기
            if (loadingOverlay) {
                loadingOverlay.style.display = "none";
            }

            if (response.ok) {
                // 토스트 알림 띄우기
                const toast = document.getElementById("rpaToast");
                if (toast) {
                    toast.style.display = "block";
                    toast.style.opacity = "1"; // 확실하게 보이도록 처리

                    // 3초 뒤에 토스트 알림 부드럽게 사라지게 설정
                    setTimeout(() => {
                        toast.style.opacity = "0";
                        setTimeout(() => {
                            toast.style.display = "none";
                            toast.style.opacity = "1";
                        }, 500);
                    }, 3000);
                }

                // 모달창이 열려있으면 깔끔하게 닫기
                if (typeof closeOrderModal === 'function') {
                    closeOrderModal();
                }
            } else {
                throw new Error("RPA 메일 엔진 연동 실패 (서버 에러)");
            }
        })
        .catch(error => {
            // 에러 발생 시에도 로딩창은 확실하게 꺼주기
            if (loadingOverlay) {
                loadingOverlay.style.display = "none";
            }
            console.error("RPA 장애 발생:", error);
            alert("❌ RPA 메일 전송 중 장애 발생: " + error.message);
        });
}