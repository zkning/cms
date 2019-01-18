package com.fast.admin.mybatis;

import com.sophia.cms.framework.exception.ServiceException;
import com.fast.admin.mybatis.entity.TRbacDict;
import com.fast.admin.mybatis.mapper.TRbacDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service
public class TRbacDictService {

    @Autowired
    TRbacDictMapper tRbacDictMapper;

    @Transactional
    public void transactional() {
        TRbacDict tRbacDict = new TRbacDict();
        tRbacDict.setId(12L);
        tRbacDict.setRemark("需要更新的数据");
        Example example = new Example(TRbacDict.class);
        example.createCriteria().andEqualTo("id", tRbacDict.getId());
        int count = tRbacDictMapper.updateByExampleSelective(tRbacDict, example);
        log.info("修改数量" + count);

        tRbacDict = new TRbacDict();
        tRbacDict.setId(1L);
        tRbacDict.setRemark("事务测试");
        example = new Example(TRbacDict.class);
        example.createCriteria().andEqualTo("id", tRbacDict.getId());
        count = tRbacDictMapper.updateByExampleSelective(tRbacDict, example);
        if (count < 1) {
            throw new ServiceException("db ex");
        }
    }

}
