package com.react.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserFormDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String id;

    @NotBlank(message = "이름을 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    private String address;

}
