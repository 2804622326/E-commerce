package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.entity.Basket;
import com.ecommerce.sportscenter.entity.BasketItem;
import com.ecommerce.sportscenter.model.BasketItemResponse;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/baskets")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping
    public ResponseEntity<List<BasketResponse>> getAllBaskets() {
        List<BasketResponse> baskets = basketService.getAllBaskets();
        return ResponseEntity.ok(baskets);
    }

    @GetMapping("/{basketId}")
    public ResponseEntity<BasketResponse> getBasketById(@PathVariable String basketId){
        BasketResponse basket = basketService.getBasketById(basketId);
        return ResponseEntity.ok(basket);
    }
    
    @DeleteMapping("/{basketId}")
    public ResponseEntity<Void> deleteBasketById(@PathVariable String basketId){
        basketService.deleteBasketById(basketId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<BasketResponse> createBasket(@RequestBody BasketResponse basketResponse){
        //convert this basket response to basket entity
        Basket basket = convertToBasketEntity(basketResponse);
        //call the service method to create the basket
        BasketResponse createdBasket = basketService.createBasket(basket);
        //Return the Created Basket
        return new ResponseEntity<>(createdBasket, HttpStatus.CREATED);
    }

    private Basket convertToBasketEntity(BasketResponse basketResponse) {
        Basket basket = new Basket();
        basket.setId(basketResponse.getId());
        basket.setItems(mapBasketItemResponsesToEntities(basketResponse.getItems()));
        return basket;
    }

    private List<BasketItem> mapBasketItemResponsesToEntities(List<BasketItemResponse> itemResponses) {
        return itemResponses.stream()
                .map(this::convertToBasketItemEntity)
                .collect(Collectors.toList());
    }

    private BasketItem convertToBasketItemEntity(BasketItemResponse itemResponse) {
        BasketItem basketItem = new BasketItem();
        basketItem.setId(itemResponse.getId());
        basketItem.setName(itemResponse.getName());
        basketItem.setDescription(itemResponse.getDescription());
        basketItem.setPrice(itemResponse.getPrice());
        basketItem.setPictureUrl(itemResponse.getPictureUrl());
        basketItem.setProductBrand(itemResponse.getProductBrand());
        basketItem.setProductType(itemResponse.getProductType());
        basketItem.setQuantity(itemResponse.getQuantity());
        return basketItem;
    }
}
