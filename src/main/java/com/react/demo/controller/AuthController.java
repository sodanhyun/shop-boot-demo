package com.react.demo.controller;

import com.react.demo.dto.LoginDto;
import com.react.demo.dto.TokenRequest;
import com.react.demo.dto.UserFormDto;
import com.react.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid UserFormDto dto,
                                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()) ValidUtil.getStringResponseEntity(bindingResult);
        try{
            userService.signUp(dto);
            return new ResponseEntity<>("회원가입이 완료되었습니다", HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid LoginDto dto) {
        try {
            return new ResponseEntity<>(userService.login(dto), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid TokenRequest request) {
        try{
            userService.logout(request);
        }catch(Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.ok("로그아웃 처리 되었습니다");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid TokenRequest request) {
        try{
            return new ResponseEntity<>(userService.tokenRefresh(request), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}
