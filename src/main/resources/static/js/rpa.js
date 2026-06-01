function sendRpaMail(targetEmail) {
    alert("RPA 자동화 엔진 가동: [" + targetEmail + "] 계정으로 발주서를 전송합니다.");

    // 백엔드 메일 전송 API 호출
    fetch('/api/jms-rpa/send-test?to=' + targetEmail)
        .then(response => {
            if(response.ok) return response.text();
            throw new Error("메일 엔진 연동 실패");
        })
        .then(data => {
            alert("🎉 발주 승인 및 이메일 전송 성공!\n" + data);
            // 만약 모달창이 열려있으면 닫기 (메서드가 존재할 때만 실행)
            if (typeof closeOrderModal === 'function') {
                closeOrderModal();
            }
        })
        .catch(error => {
            alert("❌ RPA 메일 전송 중 장애 발생: " + error.message);
        });
}