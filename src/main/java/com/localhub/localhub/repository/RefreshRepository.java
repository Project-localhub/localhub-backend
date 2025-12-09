package com.localhub.localhub.repository;

import com.localhub.localhub.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity,Long> {



    @Transactional
    void deleteByRefresh(String refresh);

    Boolean existsByRefresh(String refresh);
}
