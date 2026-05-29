<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="거래처 관리" scope="request" />
<c:set var="menu" value="vendor" scope="request" />
<jsp:include page="../layout/header.jsp" />

<!-- Hero -->
<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">VENDORS · 거래처 12곳</div>
        <h1>이번 달 <span class="accent">324만원</span>의 거래가 진행 중이에요.</h1>
        <p class="hero-brief">
            서울원두유통과의 거래가 가장 활발합니다. 거래처를 클릭하면 우측에서 상세 정보를 볼 수 있어요.
        </p>
    </div>
    <div class="hero-side">
        <div class="date">활성 거래처</div>
        <div class="time">9<span style="font-size:14px;color:var(--text-muted);">/12</span></div>
    </div>
</section>

<!-- 미니 통계 -->
<div class="mini-stats">
    <div class="mini-stat">
        <div class="ms-label">전체 거래처</div>
        <div class="ms-value">12<span class="unit">곳</span></div>
    </div>
    <div class="mini-stat accent">
        <div class="ms-label">활성 거래처</div>
        <div class="ms-value">9<span class="unit">곳</span></div>
    </div>
    <div class="mini-stat">
        <div class="ms-label">비활성</div>
        <div class="ms-value">3<span class="unit">곳</span></div>
    </div>
    <div class="mini-stat">
        <div class="ms-label">이번 달 발주 건수</div>
        <div class="ms-value">18<span class="unit">건</span></div>
    </div>
    <div class="mini-stat">
        <div class="ms-label">이번 달 발주 금액</div>
        <div class="ms-value">3,240,000<span class="unit">원</span></div>
    </div>
</div>

<!-- 툴바 -->
<div class="toolbar">
    <div class="search-input">
        <input type="text" placeholder="거래처명, 담당자, 이메일 검색...">
    </div>
    <select style="padding:9px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content); cursor:pointer;">
        <option>상태 전체</option>
        <option>활성</option>
        <option>비활성</option>
    </select>
    <select style="padding:9px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content); cursor:pointer;">
        <option>공급 품목 전체</option>
        <option>원두</option>
        <option>우유/생크림</option>
        <option>시럽/파우더</option>
        <option>일회용품</option>
        <option>베이커리</option>
        <option>주방용품</option>
    </select>
    <div class="toolbar-spacer"></div>
    <button class="btn btn-secondary btn-sm">엑셀 업로드</button>
    <a href="/vendor/register" class="btn btn-primary btn-sm">+ 거래처 등록</a>
</div>

<!-- 좌 테이블 / 우 상세 패널 -->
<div class="split-layout">

    <div class="card flush">
        <table class="data-table" id="vendorTable">
            <thead>
                <tr>
                    <th>거래처명</th>
                    <th>담당자</th>
                    <th>연락처</th>
                    <th>이메일</th>
                    <th>공급 품목</th>
                    <th>거래 조건</th>
                    <th>최근 발주일</th>
                    <th>상태</th>
                    <th class="text-right">관리</th>
                </tr>
            </thead>
            <tbody>
                <tr class="clickable selected" data-id="1">
                    <td><strong>서울원두유통</strong></td>
                    <td>김유리</td>
                    <td class="num">02-123-4567</td>
                    <td class="text-muted">seoulmilk@milk.com</td>
                    <td><span class="tag">원두</span><span class="tag">생크림</span></td>
                    <td>월말 30일</td>
                    <td class="num">2026-05-16</td>
                    <td><span class="status ok"><span class="dot"></span>활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="2">
                    <td><strong>커피빈코리아</strong></td>
                    <td>이지훈</td>
                    <td class="num">02-987-6543</td>
                    <td class="text-muted">bean@coffeebean.co.kr</td>
                    <td><span class="tag">원두</span><span class="tag">드립백</span><span class="tag">시럽</span></td>
                    <td>월말 30일</td>
                    <td class="num">2026-05-15</td>
                    <td><span class="status ok"><span class="dot"></span>활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="3">
                    <td><strong>삼양식품(시럽)</strong></td>
                    <td>박소연</td>
                    <td class="num">02-222-3333</td>
                    <td class="text-muted">syrup@samyang.com</td>
                    <td><span class="tag">시럽</span><span class="tag">소스</span></td>
                    <td>선불 결제</td>
                    <td class="num">2026-05-14</td>
                    <td><span class="status ok"><span class="dot"></span>활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="4">
                    <td><strong>일회용품 마트</strong></td>
                    <td>최민수</td>
                    <td class="num">02-444-5555</td>
                    <td class="text-muted">disposable@mart.com</td>
                    <td><span class="tag">컵</span><span class="tag">빨대</span><span class="tag">뚜껑</span></td>
                    <td>월말 30일</td>
                    <td class="num">2026-05-10</td>
                    <td><span class="status ok"><span class="dot"></span>활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="5">
                    <td><strong>삼일유통</strong></td>
                    <td>정대현</td>
                    <td class="num">02-555-6666</td>
                    <td class="text-muted">samil@foodsupply.co.kr</td>
                    <td><span class="tag">초콜릿</span><span class="tag">파우더</span></td>
                    <td>월말 30일</td>
                    <td class="num">2026-05-08</td>
                    <td><span class="status off"><span class="dot"></span>비활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="6">
                    <td><strong>푸드라인</strong></td>
                    <td>이영수</td>
                    <td class="num">02-666-7777</td>
                    <td class="text-muted">foodline@food.com</td>
                    <td><span class="tag">베이커리</span><span class="tag">샌드위치</span></td>
                    <td>선불 결제</td>
                    <td class="text-muted">—</td>
                    <td><span class="status off"><span class="dot"></span>비활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
                <tr class="clickable" data-id="7">
                    <td><strong>한빛 주방용품</strong></td>
                    <td>오지은</td>
                    <td class="num">02-777-8888</td>
                    <td class="text-muted">hanbit@kitchen.com</td>
                    <td><span class="tag">기기</span><span class="tag">용기</span></td>
                    <td>월말 15일</td>
                    <td class="num">2026-04-30</td>
                    <td><span class="status off"><span class="dot"></span>비활성</span></td>
                    <td class="text-right"><div class="row-actions"><button>수정</button><button class="danger">삭제</button></div></td>
                </tr>
            </tbody>
        </table>
        <div class="pagination">
            <button>←</button>
            <button class="active">1</button>
            <button>2</button>
            <button>→</button>
        </div>
    </div>

    <!-- 상세 패널 -->
    <aside class="detail-panel" id="detailPanel">
        <div class="panel-head">
            <div>
                <div class="panel-title">서울원두유통</div>
                <div class="panel-sub"><span class="status ok"><span class="dot"></span>활성</span> · ID #1</div>
            </div>
            <div style="display:flex; gap:4px;">
                <button class="btn btn-secondary btn-sm">수정</button>
                <button class="panel-close" title="닫기">×</button>
            </div>
        </div>

        <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; margin-bottom:18px;">
            <div style="padding:10px 12px; background:var(--bg-content); border-radius:8px;">
                <div style="font-size:10px; color:var(--text-muted); font-weight:600; text-transform:uppercase; letter-spacing:0.4px;">등록일</div>
                <div style="font-size:12px; font-weight:600; margin-top:3px;">2024-02-10</div>
            </div>
            <div style="padding:10px 12px; background:var(--bg-content); border-radius:8px;">
                <div style="font-size:10px; color:var(--text-muted); font-weight:600; text-transform:uppercase; letter-spacing:0.4px;">최근 수정</div>
                <div style="font-size:12px; font-weight:600; margin-top:3px;">2026-05-01</div>
            </div>
        </div>

        <div class="detail-list">
            <div class="detail-row">
                <span class="key">담당자</span>
                <span class="val">김유리</span>
            </div>
            <div class="detail-row">
                <span class="key">연락처</span>
                <span class="val num">02-123-4567</span>
            </div>
            <div class="detail-row">
                <span class="key">이메일</span>
                <span class="val">seoulmilk@milk.com</span>
            </div>
            <div class="detail-row">
                <span class="key">주소</span>
                <span class="val">서울특별시 중구 세종대로 1길 50</span>
            </div>
            <div class="detail-row">
                <span class="key">거래 조건</span>
                <span class="val">월말 결제 (30일)</span>
            </div>
            <div class="detail-row">
                <span class="key">계좌</span>
                <span class="val">기업은행 123-456789-01-017<br><span class="text-muted" style="font-size:12px;">(주)서울원두유통</span></span>
            </div>
            <div class="detail-row">
                <span class="key">공급 품목</span>
                <span class="val"><span class="tag">원두</span><span class="tag">생크림</span><span class="tag">버터</span></span>
            </div>
        </div>

        <div class="detail-section">
            <h5>최근 거래 내역</h5>
            <div class="mini-list">
                <div class="mini-list-item">
                    <span class="name">PO-20260516-006</span>
                    <span class="val">210,000원</span>
                </div>
                <div class="mini-list-item">
                    <span class="name">PO-20260512-005</span>
                    <span class="val">198,000원</span>
                </div>
                <div class="mini-list-item">
                    <span class="name">PO-20260508-004</span>
                    <span class="val">192,000원</span>
                </div>
            </div>
            <a href="#" class="section-link" style="font-size:12px; margin-top:12px; display:inline-block;">전체 거래 보기 →</a>
        </div>

        <div style="display:flex; gap:8px; margin-top:24px;">
            <button class="btn btn-secondary btn-sm" style="flex:1;">수정</button>
            <button class="btn btn-primary btn-sm" style="flex:1;">발주하기</button>
        </div>
    </aside>

</div>

<!-- 행 클릭으로 상세 패널 갱신 (간단) -->
<script>
    document.querySelectorAll('#vendorTable tbody tr').forEach(tr => {
        tr.addEventListener('click', () => {
            document.querySelectorAll('#vendorTable tbody tr').forEach(r => r.classList.remove('selected'));
            tr.classList.add('selected');
            // 실제 데이터 fetch는 추후 백엔드 연동 시
            document.querySelector('.panel-title').textContent = tr.querySelector('td strong').textContent;
        });
    });
</script>

<jsp:include page="../layout/footer.jsp" />
