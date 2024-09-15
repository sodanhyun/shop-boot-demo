package com.react.demo.controller;

import com.react.demo.dto.LoginDto;
import com.react.demo.dto.TokenRequest;
import com.react.demo.dto.UserFormDto;
import com.react.demo.service.UserService;
import com.react.demo.util.ValidUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final UserService userService;

    @GetMapping("test")
    public String test() {
        return "nice test";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserFormDto dto,
                                         BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return ValidUtil.getStringResponseEntity(bindingResult);
        try{
            userService.signUp(dto);
            return new ResponseEntity<>("회원가입이 완료되었습니다", HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto dto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            return new ResponseEntity<>(userService.login(dto, request, response), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request,
                                         HttpServletResponse response) {
        try{
            userService.logout(request, response);
        }catch(Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.ok("로그아웃 처리 되었습니다");
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try{
            return new ResponseEntity<>(userService.tokenRefresh(request, response), HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}
