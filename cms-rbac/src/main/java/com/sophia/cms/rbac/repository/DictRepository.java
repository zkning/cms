package com.sophia.cms.rbac.repository;

import com.sophia.cms.rbac.domain.Dict;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictRepository extends CrudRepository<Dict, Long> {
    List<Dict> findByPidOrderBySortDesc(Long pid);

    Dict findByValue(String value);
}
