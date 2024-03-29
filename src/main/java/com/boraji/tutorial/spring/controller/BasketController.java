package com.boraji.tutorial.spring.controller;

import com.boraji.tutorial.spring.entity.Product;
import com.boraji.tutorial.spring.entity.User;
import com.boraji.tutorial.spring.service.BasketService;
import com.boraji.tutorial.spring.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/user/products")
public class BasketController {

    private final BasketService basketService;
    private final ProductService productService;

    @Autowired
    public BasketController(ProductService productService, BasketService basketService) {
        this.basketService = basketService;
        this.productService = productService;
    }

    @RequestMapping("/buy")
    public String addToBasket(@AuthenticationPrincipal User user, @RequestParam("productID") String id) {
        Optional<Product> optionalProduct = productService.getById(Long.valueOf(id));
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            basketService.addProduct(user, product);
        }
        return "redirect:/user/store";
    }
}
