package com.react.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class PageDto<T> {

    Page<T> page;

    Integer maxPageNum;

}
