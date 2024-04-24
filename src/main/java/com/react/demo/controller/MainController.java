package com.react.demo.controller;

import com.react.demo.dto.ItemSearchDto;
import com.react.demo.dto.MainItemDto;
import com.react.demo.dto.PageDto;
import com.react.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;

    @GetMapping(value = {"/main", "/main/{page}"})
    public ResponseEntity<PageDto<MainItemDto>> getMain(ItemSearchDto itemSearchDto,
                                                        @PathVariable("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        return new ResponseEntity<>(new PageDto<>(items, 5), HttpStatus.OK);
    }

}
