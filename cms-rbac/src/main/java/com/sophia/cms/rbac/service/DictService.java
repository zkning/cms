package com.sophia.cms.rbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.rbac.constants.CacheableConstants;
import com.sophia.cms.rbac.domain.Dict;
import com.sophia.cms.rbac.mapper.DictMapper;
import com.sophia.cms.rbac.model.DictEditModel;
import com.sophia.cms.rbac.model.DictFetchModel;
import com.sophia.cms.rbac.model.DictSearchModel;
import com.sophia.cms.rbac.model.TreeNodeModel;
import com.sophia.cms.rbac.utils.RecursiveTools;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ningzuokun
 * 数据字段服务
 */
@Service
public class DictService {

    @Autowired
    DictMapper dictMapper;

    private static final Long TOP_NODE = 0L;

    public Response edit(DictEditModel editModel) {
        Dict entity = new Dict();
        Boolean editFlag = null != editModel.getId();
        if (editFlag) {
            entity = dictMapper.selectById(editModel.getId());
            entity.setId(editModel.getId());
        }
        entity.setPid(null != editModel.getPid() ? editModel.getPid() : 0L);
        entity.setSort(editModel.getSort());
        entity.setText(editModel.getText());
        entity.setValue(editModel.getValue());
        entity.setVersion(editModel.getVersion());
        entity.setRemark(editModel.getRemark());
        if (editFlag) {
            dictMapper.updateById(entity);
        } else {
            dictMapper.insert(entity);
        }
        return Response.SUCCESS();
    }

    public Response delete(Long id) {
        dictMapper.deleteById(id);
        return Response.SUCCESS();
    }

    @Cacheable(value = CacheableConstants.CacheableName, key = "#root.method.name + #id", unless = "#result.code != 0")
    public Response<DictFetchModel> fetch(Long id) {
        Dict dict = dictMapper.selectById(id);
        if (null == dict) {
            return Response.FAILURE("记录不存在");
        }
        DictFetchModel entity = new DictFetchModel();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(dict, entity);
        return Response.SUCCESS(entity);
    }

    public List<DictFetchModel> findByPValue(String value) {
        Dict pDict = dictMapper.findByValue(value);
        List<Dict> entitys = dictMapper.findByPidOrderBySortDesc(pDict.getId());
        List<DictFetchModel> items = Lists.newArrayList();
        ModelMapper modelMapper = new ModelMapper();
        entitys.forEach(dict -> {
            DictFetchModel dictFetchModel = new DictFetchModel();
            modelMapper.map(dict, dictFetchModel);
            items.add(dictFetchModel);
        });
        return items;
    }

    public Pager<DictFetchModel> list(DictSearchModel dictSearchModel) {
        Page page = new Page(dictSearchModel.getPageNo(), dictSearchModel.getPageSize());
        List<DictFetchModel> list = dictMapper.list(page, dictSearchModel);
        Pager<DictFetchModel> pager = new Pager<>();
        pager.setPageNo(page.getCurrent());
        pager.setPageSize(page.getSize());
        pager.setTotalElements(page.getTotal());
        pager.setContent(list);
        return pager;
    }

    public List<TreeNodeModel> treeNodes() {
        List<Dict> entityList = dictMapper.findByPidOrderBySortDesc(TOP_NODE);
        if (CollectionUtils.isEmpty(entityList)) {
            return Lists.newArrayList();
        }
        List<TreeNodeModel> treeNodeModels = Lists.newArrayList();
        entityList.forEach(group -> {
            treeNodeModels.add(TreeNodeModel.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(treeNodeModels, (TreeNodeModel item) -> {
            List<Dict> entitys = dictMapper.findByPidOrderBySortDesc(Long.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(entitys)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeModel> list = Lists.newArrayList();
            entitys.forEach(dict -> {
                list.add(TreeNodeModel.builder()
                        .key(dict.getId() + "")
                        .selectable(true)
                        .title(dict.getText())
                        .pid(dict.getPid() + "")
                        .build());
            });
            item.setChildren(list);
            return item.getChildren();
        });
    }
}
