package com.react.demo.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.react.demo.dto.QMainItemDto is a Querydsl Projection type for MainItemDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMainItemDto extends ConstructorExpression<MainItemDto> {

    private static final long serialVersionUID = 550603701L;

    public QMainItemDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> itemNm, com.querydsl.core.types.Expression<String> itemDetail, com.querydsl.core.types.Expression<String> imgUrl, com.querydsl.core.types.Expression<Integer> price) {
        super(MainItemDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, int.class}, id, itemNm, itemDetail, imgUrl, price);
    }

}

