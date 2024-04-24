package com.react.demo.controller;

import com.react.demo.dto.CartDetailDto;
import com.react.demo.dto.CartItemDto;
import com.react.demo.dto.CartOrderDto;
import com.react.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {
    private  final CartService cartService;

    @PostMapping(value = "/cart")
    public ResponseEntity<String> order(@RequestBody @Valid CartItemDto cartItemDto,
                                        BindingResult bindingResult,
                                        Principal principal){
        if(bindingResult.hasErrors()) ValidUtil.getStringResponseEntity(bindingResult);
        try {
            cartService.addCart(cartItemDto, principal.getName());
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("장바구니에 담겼습니다.", HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public ResponseEntity<?> orderHist(Principal principal){
        try{
            return new ResponseEntity<List<CartDetailDto>>(
                    cartService.getCartList(principal.getName()), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                            int count,
                                            Principal principal){
        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try{
            cartService.updateCartItemCount(cartItemId, count);
        }catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                            Principal principal){
        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        try{
            cartService.deleteCartItem(cartItemId);
        }catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @PostMapping(value = "/cart/orders")
    public ResponseEntity<String> orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if(cartOrderDtoList == null || cartOrderDtoList.isEmpty()){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.BAD_REQUEST);
        }
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        try{
            cartService.orderCartItem(cartOrderDtoList, principal.getName());
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("주문이 완료되었습니다.");
    }

}
