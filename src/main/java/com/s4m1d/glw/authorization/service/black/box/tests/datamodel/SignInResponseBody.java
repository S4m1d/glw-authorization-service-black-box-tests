package com.s4m1d.glw.authorization.service.black.box.tests.datamodel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseBody {
    private boolean success;
    private String token;
    private String errorCode;
    private String message;
}
