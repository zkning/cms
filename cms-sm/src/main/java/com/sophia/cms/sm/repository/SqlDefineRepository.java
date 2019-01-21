package com.sophia.cms.sm.repository;

import com.sophia.cms.sm.domain.SqlDefine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lenovo on 2017/8/30.
 */
@Repository
public interface SqlDefineRepository extends JpaRepository<SqlDefine, Long> {
}
