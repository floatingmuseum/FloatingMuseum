package floatingmuseum.userhunter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import floatingmuseum.userhunter.AppTimeUsingInfo;

/**
 * Created by Floatingmuseum on 2017/6/12.
 */

public class ListUtil {

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 当新一页没有数据时返回null
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
