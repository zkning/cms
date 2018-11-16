package com.fast.admin.rbac.repository;

import com.fast.admin.rbac.domain.Dict;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictRepository extends CrudRepository<Dict, Long> {
    List<Dict> findByPidOrderBySortDesc(Long pid);

    Dict findByValue(String value);
}
