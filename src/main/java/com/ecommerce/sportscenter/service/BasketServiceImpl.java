package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.Basket;
import com.ecommerce.sportscenter.entity.BasketItem;
import com.ecommerce.sportscenter.exceptions.BasketAlreadyExistsException;
import com.ecommerce.sportscenter.exceptions.BasketNotFoundException;
import com.ecommerce.sportscenter.model.BasketItemResponse;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.repository.BasketRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BasketServiceImpl implements BasketService{
    private final BasketRepository basketRepository;

    public BasketServiceImpl(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Override
    public List<BasketResponse> getAllBaskets() {
        log.info("Fetching All Baskets");
        List<Basket> basketList = (List<Basket>) basketRepository.findAll();
        //now we will use stream operator to map with response
        List<BasketResponse> basketResponses = basketList.stream()
                .map(this::convertToBasketResponse)
                .collect(Collectors.toList());
        log.info("Fetched all Baskets");
        return basketResponses;
    }

    @Override
    public BasketResponse getBasketById(String basketId) {
        log.info("Fetching Basket by Id: {}", basketId);
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket with ID " + basketId + " not found"));
        log.info("Fetched Basket by Id: {}", basketId);
        return convertToBasketResponse(basket);
    }

    @Override
    public void deleteBasketById(String basketId) {
        log.info("Deleting Basket by Id: {}", basketId);
        if (!basketRepository.existsById(basketId)) {
            throw new BasketNotFoundException("Basket with ID " + basketId + " not found");
        }
        basketRepository.deleteById(basketId);
        log.info("Deleted Basket by Id: {}", basketId);
    }

    @Override
    public BasketResponse createBasket(Basket basket) {
        log.info("Creating Basket with ID: {}", basket.getId());
        // Check if basket already exists
        if (basket.getId() != null && basketRepository.existsById(basket.getId())) {
            throw new BasketAlreadyExistsException("Basket with ID " + basket.getId() + " already exists");
        }
        Basket savedBasket = basketRepository.save(basket);
        log.info("Basket created with Id: {}", savedBasket.getId());
        return convertToBasketResponse(savedBasket);
    }

    private BasketResponse convertToBasketResponse(Basket basket) {
        if(basket == null){
            return null;
        }
        List<BasketItemResponse> itemResponses = basket.getItems().stream()
                .map(this::convertToBasketItemResponse)
                .collect(Collectors.toList());
        return BasketResponse.builder()
                .id(basket.getId())
                .items(itemResponses)
                .build();
    }

    private BasketItemResponse convertToBasketItemResponse(BasketItem basketItem) {
        return BasketItemResponse.builder()
                .id(basketItem.getId())
                .name(basketItem.getName())
                .description(basketItem.getDescription())
                .price(basketItem.getPrice())
                .pictureUrl(basketItem.getPictureUrl())
                .productBrand(basketItem.getProductBrand())
                .productType(basketItem.getProductType())
                .quantity(basketItem.getQuantity())
                .build();
    }
}
