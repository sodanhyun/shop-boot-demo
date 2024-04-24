package com.react.demo.service;

import com.react.demo.dto.CartDetailDto;
import com.react.demo.dto.CartItemDto;
import com.react.demo.dto.CartOrderDto;
import com.react.demo.dto.OrderDto;
import com.react.demo.entity.Cart;
import com.react.demo.entity.CartItem;
import com.react.demo.entity.Item;
import com.react.demo.entity.User;
import com.react.demo.repository.CartItemRepository;
import com.react.demo.repository.CartRepository;
import com.react.demo.repository.UserRepository;
import com.react.demo.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String id){

        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Cart cart = cartRepository.findByUserId(user.getId());
        if(cart == null){
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String id){


        User user = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Cart cart = cartRepository.findByUserId(user.getId());
        if(cart == null){
            return new ArrayList<>();
        }

        return cartItemRepository.findCartDetailDtoList(cart.getId());
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String id){
        User curMember = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        User savedMember = cartItem.getCart().getUser();

        if(!curMember.getId().equals(savedMember.getId())){
            return false;
        }

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}