package com.floatingmuseum.androidtest.utils;

import com.floatingmuseum.androidtest.functions.catchtime.AppTimeUsingInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/3/20.
 */

public class ListUtil {

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 根据page数和limit数查询list中的数据
     * 类似于某些网络接口的使用
     *
     * @param list  被查询集合
     * @param page  被查询页码
     * @param limit 被查询页码中所包含的数据量
     * @return 返回查询结果, 如果查询起始点大于集合长度, 返回null
     */
    public static List subList(List list, int page, int limit) {
        int fromIndex = (page - 1) * limit;
        int toIndex = page * limit;

        if (fromIndex >= list.size()) {
            return null;
        } else if (list.size() - fromIndex == 1) {
            List oneElementList = new ArrayList<>();
            oneElementList.add(list.get(list.size() - 1));
            return oneElementList;
        } else if (toIndex > list.size() - 1) {
            toIndex = list.size() - 1;
        }
        return list.subList(fromIndex, toIndex);
    }

    public static List<List<AppTimeUsingInfo>> subList(List<AppTimeUsingInfo> list) {
        List<List<AppTimeUsingInfo>> subLists = new ArrayList<>();
        Map<Long, List<AppTimeUsingInfo>> maps = new HashMap<>();
        for (AppTimeUsingInfo info : list) {
            long dayStartTime = info.getDayStartTime();
            if (maps.containsKey(dayStartTime)) {
                maps.get(dayStartTime).add(info);
            } else {
                List<AppTimeUsingInfo> timeUsingInfoList = new ArrayList<>();
                timeUsingInfoList.add(info);
                maps.put(dayStartTime, timeUsingInfoList);
            }
        }

        if (maps.size() > 0) {
            for (Long key : maps.keySet()) {
                subLists.add(maps.get(key));
            }
        }
        return subLists;
    }
}
