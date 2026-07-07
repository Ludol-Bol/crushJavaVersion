package com.crushVers.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerificationRequest {
    private String email;
    private String nickname;
    private String birthDate;
    private String password;
    private String verificationCode;
}
