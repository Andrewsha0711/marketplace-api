package com.andrewsha.marketplace.domain.product_card;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andrewsha.marketplace.domain.product.Product;
import com.andrewsha.marketplace.domain.product_card.request.CreateProductCardForm;
import com.andrewsha.marketplace.domain.product_card.request.UpdateProductCardForm;

@RestController
@RequestMapping(path = "api/v1/product-card")
@Validated
public class ProductCardController {
    @Autowired
    private ProductCardService productCardService;

    @GetMapping
    public ResponseEntity<?> getProductCards(
            // @RequestParam @Min(0) int page, @RequestParam @Min(1) int size,
            // @RequestParam(required = false) Optional<String> category,
            @RequestParam(required = true) MultiValueMap<String, String> params)
            throws MissingServletRequestParameterException {
        Page<ProductCard> cardsPage = this.productCardService.getProductCards(params);
        return ResponseEntity.ok(cardsPage);
    }

    @GetMapping(path = "/{productCardId}")
    public ResponseEntity<?> getProductCard(@PathVariable("productCardId") UUID id) {
        return ResponseEntity.ok(this.productCardService.getProductCard(id));
    }

    @PostMapping()
    @PreAuthorize("hasPermission(#productCard.store, 'ProductCard', 'CREATE')")
    public ResponseEntity<?> createProductCard(
            @Valid @RequestBody CreateProductCardForm productCard) {
        return ResponseEntity.ok(this.productCardService.createProductCard(productCard));
    }

    @PatchMapping(path = "{productCardId}")
    @PreAuthorize("hasPermission(#id, 'ProductCard', 'UPDATE')")
    public ResponseEntity<?> patchProductCard(@PathVariable("productCardId") UUID id,
            @Valid @RequestBody UpdateProductCardForm productCardDetails) {
        return ResponseEntity.ok(this.productCardService.patchProductCard(id, productCardDetails));
    }

    @PutMapping(path = "{productCardId}")
    @PreAuthorize("hasPermission(#id, 'ProductCard', 'UPDATE')")
    public ResponseEntity<?> putProductCard(@PathVariable("productCardId") UUID id,
            @Valid @RequestBody UpdateProductCardForm productCardDetails) {
        return ResponseEntity.ok(this.productCardService.putProductCard(id, productCardDetails));
    }

    @PutMapping(path = "{productCardId}/products/")
    @PreAuthorize("hasPermission(#productCardId, 'ProductCard', 'UPDATE')")
    public ResponseEntity<?> putProductToProductCard(
            @PathVariable("productCardId") UUID productCardId,
            @Valid @RequestBody Product productDetails) {
        return ResponseEntity
                .ok(this.productCardService.putProductToProductCard(productCardId, productDetails));
    }

    @DeleteMapping(path = "{productCardId}")
    @PreAuthorize("hasPermission(#id, 'ProductCard', 'DELETE')")
    public ResponseEntity<?> deleteProductCard(@PathVariable("productCardId") UUID id) {
        this.productCardService.deleteProductCard(id);
        return ResponseEntity.ok("product card with id " + id + " successfully deleted");
    }
}