package com.berryweb.shop.repository;

import com.berryweb.shop.entity.ProductOption;
import com.berryweb.shop.entity.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByOptionGroupAndIsActiveTrueOrderBySortOrderAsc(ProductOptionGroup optionGroup);
    Optional<ProductOption> findByIdAndOptionGroupAndIsActiveTrue(Long id, ProductOptionGroup optionGroup);

}
