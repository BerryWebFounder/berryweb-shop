package com.berryweb.shop.repository;

import com.berryweb.shop.entity.ProductCategory;
import com.berryweb.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByShopAndParentIsNullAndIsActiveTrueOrderBySortOrderAsc(Shop shop);
    List<ProductCategory> findByShopAndIsActiveTrueOrderBySortOrderAsc(Shop shop);
    List<ProductCategory> findByParentAndIsActiveTrueOrderBySortOrderAsc(ProductCategory parent);
    Optional<ProductCategory> findByIdAndShopAndIsActiveTrue(Long id, Shop shop);

}
