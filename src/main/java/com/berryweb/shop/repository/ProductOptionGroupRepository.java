package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Long> {

    List<ProductOptionGroup> findByProductOrderBySortOrderAsc(Product product);
    Optional<ProductOptionGroup> findByIdAndProduct(Long id, Product product);

}
