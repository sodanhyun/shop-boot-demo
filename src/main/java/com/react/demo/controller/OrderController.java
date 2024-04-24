package com.react.demo.controller;

import com.react.demo.dto.OrderDto;
import com.react.demo.dto.OrderHistDto;
import com.react.demo.dto.PageDto;
import com.react.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/order")
    public ResponseEntity<String> order(@RequestBody @Valid OrderDto orderDto,
                                        BindingResult bindingResult,
                                        Principal principal){
        if(bindingResult.hasErrors()) ValidUtil.getStringResponseEntity(bindingResult);
        try {
            orderService.order(orderDto, principal.getName());
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("주문이 완료 되었습니다.");
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public ResponseEntity<PageDto<OrderHistDto>> orderHist(@PathVariable("page") Optional<Integer> page,
                                                           Principal principal){
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        return new ResponseEntity<>(new PageDto<>(ordersHistDtoList, 5), HttpStatus.OK);
    }

    @PostMapping("/order/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderId") Long orderId ,
                                              Principal principal){
        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try{
            orderService.cancelOrder(orderId);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("주문이 정상적으로 취소되었습니다.");
    }
}
