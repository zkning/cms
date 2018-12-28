package com.fast.admin.mybatis.service;

import com.alibaba.fastjson.JSON;
import com.fast.admin.mybatis.MyApplication;
import com.fast.admin.mybatis.TRbacDictService;
import com.fast.admin.mybatis.entity.TRbacDict;
import com.fast.admin.mybatis.mapper.TRbacDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Example;

/**
 * DicetService Tester.
 *
 * @author <Authors name>
 * @version 1.0
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MyApplication.class)
public class TRbacDictMapperTest {

    @Autowired
    TRbacDictMapper tRbacDictMapper;

    @Autowired
    TRbacDictService tRbacDictService;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: test()
     */
    @Test
    public void selectByExample() throws Exception {
        Example example = new Example(TRbacDict.class);
        example.createCriteria().andEqualTo("id", "476105050620428288");
        log.info(JSON.toJSONString(tRbacDictMapper.selectByExample(example)));
    }

    @Test
    public void transactional() {
        tRbacDictService.transactional();
    }

    @Test
    public void insertSelective() {
        TRbacDict tRbacDict = new TRbacDict();
        tRbacDict.setRemark("当前数据是新添加的数据");
        int result = tRbacDictMapper.insertSelective(tRbacDict);
        log.info("insert record: {} , id:{}", result, tRbacDict.getId());
    }

} 
