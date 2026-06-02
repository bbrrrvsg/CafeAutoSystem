package com.example.CafeAutoSystem.jspcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 전체 JSP 페이지 라우팅
 *
 * 폴더 구조:
 *   /WEB-INF/views/dashboard/dashboard.jsp
 *   /WEB-INF/views/pos/pos.jsp
 *   /WEB-INF/views/inventory/inventory.jsp
 *   /WEB-INF/views/ai-order/ai-order.jsp
 *   /WEB-INF/views/ai-check/ai-check.jsp
 *   /WEB-INF/views/approval/approval.jsp
 *   /WEB-INF/views/order/order.jsp
 *   /WEB-INF/views/order-history/order-history.jsp
 *   /WEB-INF/views/vendor/vendor.jsp
 *   /WEB-INF/views/vendor/vendor-register.jsp
 *   /WEB-INF/views/log/log.jsp
 *   /WEB-INF/views/receipt/receipt.jsp
 *   /WEB-INF/views/review/write.jsp
 *   /WEB-INF/views/review/manage.jsp
 *   /WEB-INF/views/review/insight.jsp
 */
@Controller
public class JspController {

    // 대시보드
    @GetMapping("/")              public String dashboard()       { return "dashboard/dashboard"; }

    // 운영
    @GetMapping("/pos")           public String pos()             { return "pos/pos"; }
    @GetMapping("/inventory")     public String inventory()       { return "inventory/inventory"; }

    // 발주 · AI
    @GetMapping("/ai-order")      public String aiOrder()         { return "ai-order/ai-order"; }
    @GetMapping("/ai-check")      public String aiCheck()         { return "ai-check/ai-check"; }
    @GetMapping("/approval")      public String approval()        { return "approval/approval"; }
    @GetMapping("/order")         public String order()           { return "order/order"; }
    @GetMapping("/order-history") public String orderHistory()    { return "order-history/order-history"; }

    // 관리
    @GetMapping("/vendor")          public String vendor()         { return "vendor/vendor"; }
    @GetMapping("/vendor/register") public String vendorRegister() { return "vendor/vendor-register"; }
    @GetMapping("/log")             public String log()            { return "log/log"; }

    // 리뷰 · 영수증
    @GetMapping("/receipt")        public String receipt()        { return "receipt/receipt"; }
    @GetMapping("/review/write")   public String reviewWrite()    { return "review/write"; }
    @GetMapping("/review/manage")  public String reviewManage()   { return "review/manage"; }
    @GetMapping("/review/insight") public String reviewInsight()  { return "review/insight"; }
}
