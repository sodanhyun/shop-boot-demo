package com.react.demo.service;

import com.react.demo.dto.OrderDto;
import com.react.demo.dto.OrderHistDto;
import com.react.demo.dto.OrderItemDto;
import com.react.demo.entity.*;
import com.react.demo.repository.ItemImgRepository;
import com.react.demo.repository.OrderRepository;
import com.react.demo.repository.UserRepository;
import com.react.demo.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email) {
        //1. Item엔티티 조회 - dto-itemId
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        //2. member엔티티 조회 - email
        User user = userRepository.findById(email)
                .orElseThrow(EntityNotFoundException::new);


        List<OrderItem> orderItemList = new ArrayList<>();
        //OrderItem 생성(item엔티티, 주문 개수)
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        //OrderItem add
        orderItemList.add(orderItem);

        //Order 엔티티 생성(user, orderItemList)
        Order order = Order.createOrder(user, orderItemList);
        //Order 엔티티 저장
        orderRepository.save(order);

        //생성된 Order의 Id 리턴
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        User curMember = userRepository.findById(email)
                .orElseThrow(EntityNotFoundException::new);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        User savedMember = order.getUser();

        if(!curMember.getId().equals(savedMember.getId())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Long orders(List<OrderDto> orderDtoList, String email){

        User user = userRepository.findById(email)
                .orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(user, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

}
