package com.react.demo.repository;

import com.react.demo.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "where o.user.id = :id " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("id") String id, Pageable pageable);

    @Query("select count(o) from Order o " +
            "where o.user.id = :id"
    )
    Long countOrder(@Param("id") String id);

}
