package com.sophia.cms.sm.repository;

import com.sophia.cms.sm.domain.DataView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zkning on 2017/8/13.
 */
@Repository
public interface DataViewRepository extends JpaRepository<DataView, Long> {
}
