# LLM 챗봇 모듈

설치형 LLM(**Ollama**)을 호출하는 카페 AI 비서 챗봇.

## 폴더 구조
```
LLM/
├── controller/ChatController.java   # POST /api/chat
├── service/ChatService.java         # Ollama(localhost:11434) 호출
├── dto/ChatRequest.java             # {"message":"..."}
└── dto/ChatResponse.java            # {"answer":"..."}
```

## 사용 전 준비
1. **Ollama 설치**: https://ollama.com
2. **모델 받기** (터미널):
   ```
   ollama pull qwen3:4b
   # GTX 1650(4GB) 등 VRAM 작으면 ↓ 가 더 빠름 (GPU에 올라감)
   ollama pull exaone3.5:2.4b
   ```
3. **확인**: `ollama ps` → `PROCESSOR`가 `100% GPU` 면 빠름 / `100% CPU` 면 모델이 커서 느림

## 설정 (선택)
`application.properties` 에서 변경 가능 (없으면 기본값 사용):
```properties
ollama.url=http://localhost:11434
ollama.model=qwen3:4b
```
> 배포 시 `ollama.url`만 Ollama 서버 주소로 바꾸면 됨 (코드 수정 불필요)

## 테스트
```bash
curl -X POST http://localhost:8080/api/chat ^
  -H "Content-Type: application/json" ^
  -d "{\"message\":\"안녕\"}"
# → {"answer":"안녕하세요! ..."}
```

## 다음 단계 (TODO)
- [ ] 프론트 `chat.jsp` + 헤더 메뉴 (음성 STT/TTS: Web Speech API)
- [ ] 스트리밍 응답 (`stream:true`) — 체감 속도 개선
- [ ] **말로 등록**: LLM이 JSON 추출 → 기존 CRUD 서비스 호출
- [ ] **RAG**: 질문 시 재고/발주 DB를 컨텍스트로 주입 → 실제 데이터 기반 답변
