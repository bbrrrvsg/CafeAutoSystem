package com.example.CafeAutoSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 대시보드
    @GetMapping("/")              public String dashboard()       { return "dashboard"; }

    // 운영
    @GetMapping("/pos")           public String pos()             { return "pos"; }
    @GetMapping("/inventory")     public String inventory()       { return "inventory"; }

    // 발주 · AI
    @GetMapping("/ai-order")      public String aiOrder()         { return "ai-order"; }
    @GetMapping("/ai-check")      public String aiCheck()         { return "ai-check"; }
    @GetMapping("/approval")      public String approval()        { return "approval"; }
    @GetMapping("/order")         public String order()           { return "order"; }
    @GetMapping("/order-history") public String orderHistory()    { return "order-history"; }

    // 관리
    @GetMapping("/vendor")          public String vendor()         { return "vendor"; }
    @GetMapping("/vendor/register") public String vendorRegister() { return "vendor-register"; }
    @GetMapping("/log")             public String log()            { return "log"; }
}
