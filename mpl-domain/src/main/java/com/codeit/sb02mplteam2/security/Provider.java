package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import lombok.Getter;

@Getter
public enum Provider {
  LOCAL("local"),
  GOOGLE("google");

  private final String registrationId;

  Provider(String registrationId) {
    this.registrationId = registrationId;
  }

  public static Provider fromRegistrationId(String registrationId) {
    for(Provider provider : values()) {
      if(provider.registrationId.equals(registrationId)) {
        return provider;
      }
    }
    throw new MplException(ErrorCode.UNKNOWN_PROVIDER);
  }
}
