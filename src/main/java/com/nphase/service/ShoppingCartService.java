package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ShoppingCartService {
    public static BigDecimal DISCOUNT_VALUE = BigDecimal.valueOf(0.1);
    public static int DISCOUNT_AMOUNT = 3;

    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getPricePerUnit()
                        .subtract(calculateDiscount(product, getDiscountMap(shoppingCart.getProducts())))
                        .multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateDiscount(Product product, Map<String, BigDecimal> discountMap) {
        return product.getPricePerUnit().multiply(discountMap.get(product.getCategory()));
    }

    public Map<String, BigDecimal> getDiscountMap(List<Product> products) {
        Map<String, List<Product>> gropedProducts = products
                .stream()
                .collect(groupingBy(Product::getCategory));
        return gropedProducts.keySet()
                .stream().collect(Collectors
                        .toMap(value -> value, value -> (gropedProducts.get(value)
                                .stream()
                                .mapToInt(Product::getQuantity).sum() > DISCOUNT_AMOUNT)
                                ? DISCOUNT_VALUE : BigDecimal.ZERO));
    }
}
