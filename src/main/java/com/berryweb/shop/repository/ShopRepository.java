package com.berryweb.shop.repository;

import com.berryweb.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Page<Shop> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    List<Shop> findByOwnerUserIdAndIsActiveTrue(Long ownerUserId);
    Optional<Shop> findByIdAndIsActiveTrue(Long id);
    Page<Shop> findByNameContainingAndIsActiveTrueOrderByCreatedAtDesc(String name, Pageable pageable);

}
