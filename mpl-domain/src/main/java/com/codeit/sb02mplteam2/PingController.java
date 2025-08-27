package com.codeit.sb02mplteam2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
  @GetMapping("/ping")
  public String ping() {
    return "MPL!";
  }
}
