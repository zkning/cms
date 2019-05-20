package com.sophia.cms.rbac.service;


import com.august.rbac.domain.Dict;
import com.august.rbac.dto.DictEditDTO;
import com.august.rbac.dto.DictFetchDTO;
import com.august.rbac.dto.DictSearchDTO;
import com.august.rbac.dto.TreeNodeDTO;
import com.august.rbac.mapper.DictMapper;
import com.august.website.utils.RecursiveTools;
import com.august.website.utils.Pager;
import com.august.website.utils.Resp;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.collections.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ningzuokun
 *         数据字段服务
 */
@Service
public class DictService {
    private static final Long TOP_NODE = 0L;

    @Autowired
    DictMapper dictMapper;

    public Resp edit(DictEditDTO editModel) {
        Dict t = new Dict();
        Boolean isEdit = null != editModel.getId();
        if (isEdit) {
            t = dictMapper.selectById(editModel.getId());
        }
        new ModelMapper().map(editModel, t);
        t.setPid(null != editModel.getPid() ? editModel.getPid() : TOP_NODE);
        if (isEdit) {
            dictMapper.updateById(t);
        } else {
            dictMapper.insert(t);
        }
        return Resp.SUCCESS();
    }

    public Resp delete(Long id) {
        dictMapper.deleteById(id);
        return Resp.SUCCESS();
    }

    public Resp<DictFetchDTO> fetch(Long id) {
        Dict t = dictMapper.selectById(id);
        if (null == t) {
            return Resp.FAILURE("记录不存在");
        }
        DictFetchDTO d = new DictFetchDTO();
        new ModelMapper().map(t, d);
        return Resp.SUCCESS(d);
    }

    public List<DictFetchDTO> findByPValue(String value) {
        Dict pDict = dictMapper.findByValue(value);
        List<Dict> ds = dictMapper.findByPidOrderBySortDesc(pDict.getId());
        List<DictFetchDTO> items = new ArrayList<>();
        ds.forEach(dict -> {
            DictFetchDTO dm = new DictFetchDTO();
            new ModelMapper().map(dict, dm);
            items.add(dm);
        });
        return items;
    }

    public Pager<DictFetchDTO> list(DictSearchDTO dsm) {
        Page p = new Page(dsm.getPageNo(), dsm.getPageSize());
        List<DictFetchDTO> list = dictMapper.list(p, dsm);

        // 分页响应
        Pager<DictFetchDTO> pager = new Pager();
        pager.setPageNo(p.getCurrent());
        pager.setPageSize(p.getSize());
        pager.setTotalElements(p.getTotal());
        pager.setContent(list);
        return pager;
    }

    public List<TreeNodeDTO> treeNodes() {
        List<Dict> dicts = dictMapper.findByPidOrderBySortDesc(TOP_NODE);
        if (CollectionUtils.isEmpty(dicts)) {
            return new ArrayList<>();
        }
        List<TreeNodeDTO> nodes = new ArrayList<>();
        dicts.forEach(group -> {
            nodes.add(TreeNodeDTO.builder()
                    .key(group.getId() + "")
                    .pid(group.getPid() + "")
                    .selectable(true)
                    .expanded(true)
                    .title(group.getText())
                    .build());
        });
        return RecursiveTools.forEachTreeItems(nodes, (TreeNodeDTO item) -> {
            List<Dict> entitys = dictMapper.findByPidOrderBySortDesc(Long.valueOf(item.getKey()));
            if (CollectionUtils.isEmpty(entitys)) {
                item.setLeaf(true);
                return null;
            }
            List<TreeNodeDTO> list = new ArrayList<>();
            entitys.forEach(dict -> {
                list.add(TreeNodeDTO.builder()
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
