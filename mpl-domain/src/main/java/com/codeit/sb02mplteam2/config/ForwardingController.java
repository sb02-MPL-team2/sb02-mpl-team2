package com.codeit.sb02mplteam2.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForwardingController {

  @GetMapping(value = "/{path:^(?!swagger-ui|api|assets|files|ws|sse|.*\\..*).*}/**")
  public String forward() {
    // 서버 내부에서 /index.html 로 요청 전달
    // 브라우저의 URL은 바뀌지 않는다.
    return "forward:/index.html";
  }
}
