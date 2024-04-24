package com.react.demo.controller;

import com.react.demo.dto.ItemFormDto;
import com.react.demo.dto.ItemSearchDto;
import com.react.demo.dto.PageDto;
import com.react.demo.entity.Item;
import com.react.demo.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.react.demo.controller.ValidUtil.getStringResponseEntity;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping(value = "/admin/item/new")
    public ResponseEntity<String> itemNew(@Valid @RequestPart("data") ItemFormDto itemFormDto,
                                          BindingResult bindingResult,
                                          @RequestPart(value = "itemImgFile", required = false)
                                              List<MultipartFile> itemImgFileList){
        if(bindingResult.hasErrors()) return getStringResponseEntity(bindingResult);
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            return new ResponseEntity<>("첫번째 상품 이미지는 필수 입력 값 입니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            return new ResponseEntity<>("상품 등록 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("상품이 정상적으로 등록되었습니다.");
    }

    @PostMapping(value = "/admin/item")
    public ResponseEntity<String> itemUpdate(@Valid @RequestPart("data") ItemFormDto itemFormDto,
                                             BindingResult bindingResult,
                                             @RequestPart(value = "itemImgFile", required = false)
                                                 List<MultipartFile> itemImgFileList){
        if(bindingResult.hasErrors()) ValidUtil.getStringResponseEntity(bindingResult);
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            return new ResponseEntity<>("상품 수정 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("상품이 정상적으로 수정되었습니다.");
    }

    @PostMapping(value = {"/admin/items", "/admin/items/{page}"})
    public ResponseEntity<PageDto<Item>> itemManage(@RequestBody ItemSearchDto itemSearchDto,
                                        @PathVariable("page") Optional<Integer> page){
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        return new ResponseEntity<>(new PageDto<>(items, 5), HttpStatus.OK);
    }

    @GetMapping(value = "/item/{itemId}")
    public ResponseEntity<?> itemDtl(@PathVariable("itemId") Long itemId){
        try {
            return new ResponseEntity<>(itemService.getItemDtl(itemId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 상품입니다.", HttpStatus.OK);
        }
    }

}
