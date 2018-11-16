package com.fast.admin.sm.repository;

import com.fast.admin.sm.domain.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

}
