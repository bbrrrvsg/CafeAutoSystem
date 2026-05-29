<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="발주 작성" scope="request" />
<c:set var="menu" value="order" scope="request" />
<jsp:include page="layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">NEW ORDER · 발주서 작성</div>
        <h1>새 발주서를 작성합니다.</h1>
        <p class="hero-brief">거래처와 발주 목적을 선택하고 품목을 추가하세요.</p>
    </div>
</section>

<!-- 스텝퍼 -->
<div class="stepper">
    <div class="step active">
        <span class="step-num">1</span>
        <span>발주 정보 입력</span>
    </div>
    <div class="step-line"></div>
    <div class="step">
        <span class="step-num">2</span>
        <span>품목 선택</span>
    </div>
    <div class="step-line"></div>
    <div class="step">
        <span class="step-num">3</span>
        <span>확인 및 발주</span>
    </div>
</div>

<!-- 본문: 좌(폼) / 우(요약) -->
<div class="split-layout">

    <div>
        <!-- 기본 정보 -->
        <div class="form-section">
            <h3>기본 정보</h3>
            <div class="sec-sub">발주서의 핵심 정보를 입력하세요.</div>

            <div class="form-row">
                <div class="fr-label">
                    거래처 선택
                    <span class="help">발주를 보낼 거래처를 선택합니다.</span>
                </div>
                <div class="fr-control" style="display:flex; gap:8px;">
                    <select style="flex:1;">
                        <option>서울원두유통 (milk@seoulyu.co.kr)</option>
                        <option>커피빈코리아</option>
                        <option>삼양식품</option>
                    </select>
                    <button class="btn btn-secondary btn-sm">+ 새 거래처</button>
                </div>
            </div>

            <div class="form-row">
                <div class="fr-label">발주 목적</div>
                <div class="fr-control">
                    <label style="margin-right:16px; font-size:13px;"><input type="radio" name="purpose"> AI 추천 발주</label>
                    <label style="margin-right:16px; font-size:13px;"><input type="radio" name="purpose" checked> 수동 발주</label>
                    <label style="font-size:13px;"><input type="radio" name="purpose"> 긴급 발주</label>
                </div>
            </div>

            <div class="form-row">
                <div class="fr-label">희망 납품일</div>
                <div class="fr-control">
                    <input type="date" value="2026-05-18">
                </div>
            </div>

            <div class="form-row">
                <div class="fr-label">메모 <span class="help">선택사항</span></div>
                <div class="fr-control">
                    <textarea rows="3" placeholder="발주 관련 메모를 입력하세요..."></textarea>
                </div>
            </div>
        </div>

        <!-- 첨부파일 -->
        <div class="form-section">
            <h3>첨부파일 <span style="color:var(--text-muted); font-size:13px; font-weight:500;">(선택)</span></h3>
            <div class="sec-sub">발주 관련 참고 자료가 있다면 첨부하세요. PDF, Excel, 이미지 파일 지원.</div>

            <div class="dropzone" onclick="document.getElementById('attachFile').click()">
                <input type="file" id="attachFile" style="display:none;" multiple accept=".pdf,.xlsx,.xls,.png,.jpg,.jpeg">
                <div style="font-size:30px; margin-bottom:10px; opacity:0.4;">⬆</div>
                <div class="dz-title">파일을 드래그하거나 클릭하여 업로드</div>
                <div class="dz-sub">PDF, Excel, 이미지 파일 지원 · 최대 10MB</div>
            </div>
        </div>

        <!-- 발주 품목 리스트 -->
        <div class="form-section">
            <div style="display:flex; align-items:baseline; justify-content:space-between; margin-bottom:14px;">
                <div>
                    <h3 style="margin:0;">발주 품목 리스트</h3>
                    <div class="sec-sub" style="margin:0;">5개 품목 · 총 228개</div>
                </div>
                <button class="btn btn-secondary btn-sm">+ 품목 추가</button>
            </div>

            <table class="data-table" style="border:1px solid var(--border-light); border-radius:8px;">
                <thead>
                    <tr>
                        <th>품목</th>
                        <th>규격</th>
                        <th>현재고</th>
                        <th>안전재고</th>
                        <th style="width:130px;">발주량</th>
                        <th>단가</th>
                        <th class="text-right">금액</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><strong>원두 (블렌드)</strong></td>
                        <td>1kg</td>
                        <td class="num">1.2</td>
                        <td class="num">5</td>
                        <td><input type="number" value="5" style="width:80px; padding:6px 8px; border:1px solid var(--border); border-radius:6px;"></td>
                        <td class="num">32,000</td>
                        <td class="text-right num font-bold">160,000</td>
                        <td><button class="row-actions"><button class="danger">×</button></button></td>
                    </tr>
                    <tr>
                        <td><strong>우유 (1L)</strong></td>
                        <td>1L</td>
                        <td class="num">3</td>
                        <td class="num">20</td>
                        <td><input type="number" value="20" style="width:80px; padding:6px 8px; border:1px solid var(--border); border-radius:6px;"></td>
                        <td class="num">2,500</td>
                        <td class="text-right num font-bold">50,000</td>
                        <td><button class="row-actions"><button class="danger">×</button></button></td>
                    </tr>
                    <tr>
                        <td><strong>플라스틱 컵</strong></td>
                        <td>16oz</td>
                        <td class="num">40</td>
                        <td class="num">100</td>
                        <td><input type="number" value="100" style="width:80px; padding:6px 8px; border:1px solid var(--border); border-radius:6px;"></td>
                        <td class="num">150</td>
                        <td class="text-right num font-bold">15,000</td>
                        <td><button class="row-actions"><button class="danger">×</button></button></td>
                    </tr>
                    <tr>
                        <td><strong>바닐라 시럽</strong></td>
                        <td>1L</td>
                        <td class="num">0.3</td>
                        <td class="num">2</td>
                        <td><input type="number" value="3" style="width:80px; padding:6px 8px; border:1px solid var(--border); border-radius:6px;"></td>
                        <td class="num">12,000</td>
                        <td class="text-right num font-bold">36,000</td>
                        <td><button class="row-actions"><button class="danger">×</button></button></td>
                    </tr>
                    <tr>
                        <td><strong>빨대</strong></td>
                        <td>개별포장</td>
                        <td class="num">30</td>
                        <td class="num">100</td>
                        <td><input type="number" value="100" style="width:80px; padding:6px 8px; border:1px solid var(--border); border-radius:6px;"></td>
                        <td class="num">50</td>
                        <td class="text-right num font-bold">5,000</td>
                        <td><button class="row-actions"><button class="danger">×</button></button></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- 발주 요약 (sticky) -->
    <aside class="detail-panel">
        <div class="panel-head">
            <div>
                <div class="panel-title">발주 요약</div>
                <div class="panel-sub">최종 확인 후 발주서 생성</div>
            </div>
        </div>

        <div class="detail-list">
            <div class="detail-row"><span class="key">거래처</span><span class="val">서울원두유통</span></div>
            <div class="detail-row"><span class="key">발주 목적</span><span class="val"><span class="badge badge-info">수동 발주</span></span></div>
            <div class="detail-row"><span class="key">희망 납품일</span><span class="val num">2026-05-18</span></div>
            <div class="detail-row"><span class="key">품목 수</span><span class="val num">5개</span></div>
            <div class="detail-row"><span class="key">총 수량</span><span class="val num">228개</span></div>
            <div class="detail-row"><span class="key">공급가 합계</span><span class="val num">266,000원</span></div>
            <div class="detail-row"><span class="key">부가세 (10%)</span><span class="val num">26,600원</span></div>
        </div>

        <div style="padding: 18px 0; border-top: 2px solid var(--text-primary); display:flex; justify-content:space-between; align-items:baseline; margin-top: 8px;">
            <span style="font-size:13px; font-weight:600;">총 발주 금액</span>
            <span style="font-size:22px; font-weight:700; color:var(--primary); font-feature-settings:'tnum';">292,600원</span>
        </div>

        <div style="display:flex; flex-direction:column; gap:8px; margin-top: 8px;">
            <button class="btn btn-primary" style="padding:12px;">발주서 생성하기</button>
            <button class="btn btn-secondary" style="padding:12px;">미리보기 (PDF)</button>
        </div>

        <div style="margin-top:20px; padding:14px; background:#FEF3C7; border-radius:10px; font-size:12px; color:#92400E;">
            <strong>안내</strong><br>
            발주서 생성 후 승인 절차를 거쳐 거래처로 자동 발송됩니다.
        </div>
    </aside>

</div>

<jsp:include page="layout/footer.jsp" />
