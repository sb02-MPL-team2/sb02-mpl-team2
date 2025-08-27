package com.codeit.sb02mplteam2.domain.mail.entity;

import lombok.Getter;

@Getter
public enum EmailTemplate {
  TEST("test"),
  RESET("아래 링크를 클릭하여 비밀번호를 재설정하세요: ")
  ;

  EmailTemplate(String template) {
    Template = template;
  }

  private String Template;




}
