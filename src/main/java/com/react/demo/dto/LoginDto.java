package com.react.demo.dto;

import com.react.demo.constant.SocialType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    private String id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public void setEmail(String email) {
        this.id = email + "_" + SocialType.OWN.getKey();
        this.email = email;
    }

}
