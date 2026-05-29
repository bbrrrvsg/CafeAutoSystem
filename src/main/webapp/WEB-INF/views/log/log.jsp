<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="로그 추적" scope="request" />
<c:set var="menu" value="log" scope="request" />
<jsp:include page="../layout/header.jsp" />

<section class="hero">
    <div class="hero-text">
        <div class="hero-meta">SYSTEM LOG · 활동 기록</div>
        <h1>오늘 <span class="accent">8개</span>의 활동이 기록됐어요.</h1>
        <p class="hero-brief">시스템, AI, 사용자, 액션별로 발생한 모든 이벤트를 추적합니다.</p>
    </div>
    <div class="hero-side">
        <div class="date">마지막 활동</div>
        <div class="time" style="font-size:14px; font-weight:600;">23:00:00</div>
    </div>
</section>

<!-- 툴바 -->
<div class="toolbar">
    <input type="date" value="2026-05-16" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">
    <span style="color:var(--text-muted);">~</span>
    <input type="date" value="2026-05-16" style="padding:8px 12px; border:1px solid var(--border); border-radius:8px; font-size:13px;">

    <select style="padding:8px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content);">
        <option>구분 전체</option>
        <option>SYSTEM</option>
        <option>AI</option>
        <option>USER</option>
        <option>ACTION</option>
    </select>

    <select style="padding:8px 12px; border:1px solid var(--border); border-radius:10px; font-size:13px; background:var(--bg-content);">
        <option>사용자 전체</option>
        <option>시스템</option>
        <option>AI 모델</option>
        <option>사장님</option>
    </select>

    <button class="btn btn-primary btn-sm">조회</button>

    <div class="toolbar-spacer"></div>

    <!-- 보기 전환 -->
    <div class="chip-group" style="padding:2px;">
        <button class="chip active" id="viewTable" style="padding:5px 12px; font-size:12px;">테이블</button>
        <button class="chip" id="viewTimeline" style="padding:5px 12px; font-size:12px;">타임라인</button>
    </div>
    <button class="btn btn-secondary btn-sm">내보내기</button>
</div>

<!-- 테이블 뷰 -->
<div class="card flush" id="tableView">
    <table class="data-table">
        <thead>
            <tr>
                <th style="width:130px;">시간</th>
                <th style="width:90px;">구분</th>
                <th>내용</th>
                <th style="width:140px;">사용자/시스템</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="num">23:00:00</td>
                <td><span class="timeline-tag system">SYSTEM</span></td>
                <td>스케줄러 가동 — 일말 마감 프로세스 시작</td>
                <td class="text-muted">시스템</td>
            </tr>
            <tr>
                <td class="num">23:00:10</td>
                <td><span class="timeline-tag system">SYSTEM</span></td>
                <td>원두 재고 부족 감지 (현재고: 1.2kg &lt; 안전재고: 5kg)</td>
                <td class="text-muted">시스템</td>
            </tr>
            <tr>
                <td class="num">23:01:05</td>
                <td><span class="timeline-tag ai">AI</span></td>
                <td>AI 발주서 작성 완료 (원두 5kg, 우유 20팩 등)</td>
                <td class="text-muted">AI 모델</td>
            </tr>
            <tr>
                <td class="num">23:01:20</td>
                <td><span class="timeline-tag ai">AI</span></td>
                <td>AI 오발주 검사 결과 — 모든 항목 정상 범위</td>
                <td class="text-muted">AI 모델</td>
            </tr>
            <tr>
                <td class="num">09:00:15</td>
                <td><span class="timeline-tag user">USER</span></td>
                <td>사장님 로그인</td>
                <td class="text-muted">사장님</td>
            </tr>
            <tr>
                <td class="num">09:00:45</td>
                <td><span class="timeline-tag action">ACTION</span></td>
                <td>발주 승인 (PO-20260516-001)</td>
                <td class="text-muted">사장님</td>
            </tr>
            <tr>
                <td class="num">09:01:10</td>
                <td><span class="timeline-tag action">ACTION</span></td>
                <td>발주 이메일 전송 완료 (서울원두유통)</td>
                <td class="text-muted">시스템</td>
            </tr>
            <tr>
                <td class="num">14:30:00</td>
                <td><span class="timeline-tag system">SYSTEM</span></td>
                <td>일일 매출 집계 시작</td>
                <td class="text-muted">시스템</td>
            </tr>
        </tbody>
    </table>
</div>

<!-- 타임라인 뷰 (숨김) -->
<div class="card" id="timelineView" style="display:none;">
    <div class="timeline">
        <div class="timeline-item system">
            <div class="timeline-head">
                <span class="timeline-time">23:00:00</span>
                <span class="timeline-tag system">SYSTEM</span>
                <span class="timeline-actor">시스템</span>
            </div>
            <div class="timeline-content">스케줄러 가동 — 일말 마감 프로세스 시작</div>
        </div>
        <div class="timeline-item system">
            <div class="timeline-head">
                <span class="timeline-time">23:00:10</span>
                <span class="timeline-tag system">SYSTEM</span>
                <span class="timeline-actor">시스템</span>
            </div>
            <div class="timeline-content">원두 재고 부족 감지 (현재고 1.2kg &lt; 안전재고 5kg)</div>
        </div>
        <div class="timeline-item ai">
            <div class="timeline-head">
                <span class="timeline-time">23:01:05</span>
                <span class="timeline-tag ai">AI</span>
                <span class="timeline-actor">AI 모델</span>
            </div>
            <div class="timeline-content">AI 발주 초안 생성 완료 (원두 5kg, 우유 20팩 등)</div>
        </div>
        <div class="timeline-item ai">
            <div class="timeline-head">
                <span class="timeline-time">23:01:20</span>
                <span class="timeline-tag ai">AI</span>
                <span class="timeline-actor">AI 모델</span>
            </div>
            <div class="timeline-content">AI 오발주 검사 결과 통과 — 모든 항목 정상 범위</div>
        </div>
        <div class="timeline-item user">
            <div class="timeline-head">
                <span class="timeline-time">09:00:15</span>
                <span class="timeline-tag user">USER</span>
                <span class="timeline-actor">사장님</span>
            </div>
            <div class="timeline-content">사장님 로그인</div>
        </div>
        <div class="timeline-item action">
            <div class="timeline-head">
                <span class="timeline-time">09:00:45</span>
                <span class="timeline-tag action">ACTION</span>
                <span class="timeline-actor">사장님</span>
            </div>
            <div class="timeline-content">발주 승인 (PO-20260516-001)</div>
        </div>
        <div class="timeline-item action">
            <div class="timeline-head">
                <span class="timeline-time">09:01:10</span>
                <span class="timeline-tag action">ACTION</span>
                <span class="timeline-actor">시스템</span>
            </div>
            <div class="timeline-content">발주 이메일 전송 완료 (서울원두유통)</div>
        </div>
        <div class="timeline-item system">
            <div class="timeline-head">
                <span class="timeline-time">14:30:00</span>
                <span class="timeline-tag system">SYSTEM</span>
                <span class="timeline-actor">시스템</span>
            </div>
            <div class="timeline-content">일일 매출 집계 시작</div>
        </div>
    </div>
</div>

<script>
    document.getElementById('viewTable').addEventListener('click', () => {
        document.getElementById('viewTable').classList.add('active');
        document.getElementById('viewTimeline').classList.remove('active');
        document.getElementById('tableView').style.display = '';
        document.getElementById('timelineView').style.display = 'none';
    });
    document.getElementById('viewTimeline').addEventListener('click', () => {
        document.getElementById('viewTimeline').classList.add('active');
        document.getElementById('viewTable').classList.remove('active');
        document.getElementById('tableView').style.display = 'none';
        document.getElementById('timelineView').style.display = '';
    });
</script>

<jsp:include page="../layout/footer.jsp" />
