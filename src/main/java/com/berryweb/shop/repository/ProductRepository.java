package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Product;
import com.berryweb.shop.entity.ProductCategory;
import com.berryweb.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByShopAndStatusOrderByCreatedAtDesc(Shop shop, Product.ProductStatus status, Pageable pageable);
    Page<Product> findByShopIdAndStatusOrderByCreatedAtDesc(Long shopId, Product.ProductStatus status, Pageable pageable);
    Page<Product> findByCategoryAndStatusOrderByCreatedAtDesc(ProductCategory category, Product.ProductStatus status, Pageable pageable);
    Page<Product> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(Product.ProductStatus status, Pageable pageable);
    Optional<Product> findByIdAndStatus(Long id, Product.ProductStatus status);
    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId AND p.status = :status AND " +
            "(p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.shortDescription LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    Page<Product> searchByKeyword(@Param("shopId") Long shopId, @Param("status") Product.ProductStatus status,
                                  @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
            "(p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.shortDescription LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    Page<Product> searchAllByKeyword(@Param("status") Product.ProductStatus status,
                                     @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.price BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY p.price ASC")
    Page<Product> findByPriceRange(@Param("status") Product.ProductStatus status,
                                   @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    long countByShopAndStatus(Shop shop, Product.ProductStatus status);

}
