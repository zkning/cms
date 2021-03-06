package com.sophia.cms.sm.repository;

import com.sophia.cms.sm.domain.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

}
