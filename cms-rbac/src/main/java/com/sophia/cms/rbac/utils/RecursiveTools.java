package com.sophia.cms.rbac.utils;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 递归工具
 *
 * @author ingzuokun
 */
public class RecursiveTools {

    /**
     * 递归返回一个树形结构数据
     *
     * @param items
     * @param itemCircleRule
     * @param <T>
     * @return
     */
    public static <T> List<T> forEachTreeItems(List<T> items, ItemCircleRule<T> itemCircleRule) {
        if (CollectionUtils.isEmpty(items)) {
            return items;
        }
        items.forEach(item -> {

            // 查询下级
            List<T> childrens = itemCircleRule.findChildren(item);

            // 下级不为空继续遍历
            if (CollectionUtils.isNotEmpty(childrens)) {
                forEachTreeItems(childrens, itemCircleRule);
            }
        });
        return items;
    }

    /**
     * 递归返回一个树形结构数据
     *
     * @param item
     * @param itemCircleRule
     * @param <T>
     * @return
     */
    public static <T> T forEachTreeItem(T item, ItemCircleRule<T> itemCircleRule) {
        if (null == item) {
            return item;
        }
        // 查询下级
        List<T> childrens = itemCircleRule.findChildren(item);

        // 下级不为空继续遍历
        if (CollectionUtils.isNotEmpty(childrens)) {
            forEachTreeItems(childrens, itemCircleRule);
        }
        return item;
    }

    /**
     * 递归获取所有下级
     *
     * @param item
     * @param itemCircleRule
     * @param <T>
     * @return
     */
    public static <T> List<T> forEachItem(T item, ItemCircleRule<T> itemCircleRule) {
        List<T> items = Lists.newArrayList();
        if (null == item) {
            return items;
        }
        // 查询下级
        List<T> childrens = itemCircleRule.findChildren(item);
        if (CollectionUtils.isNotEmpty(childrens)) {
            items.addAll(childrens);
            childrens.forEach(child -> {
                items.addAll(forEachItem(child, itemCircleRule));
            });
        }
        return items;
    }

    /**
     * 递归获取所有下级
     *
     * @param items
     * @param itemCircleRule
     * @param <T>
     * @return
     */
    public static <T> List<T> forEachItems(List<T> items, ItemCircleRule<T> itemCircleRule) {
        List<T> list = Lists.newArrayList();
        if(CollectionUtils.isEmpty(items)){
            return Lists.newArrayList();
        }
        items.forEach(item -> {
            list.addAll(forEachItem(item, itemCircleRule));
        });
        return list;
    }


    /**
     * 子项查询规则
     *
     * @param <T>
     */
    public interface ItemCircleRule<T> {

        public List<T> findChildren(T item);
    }
}
