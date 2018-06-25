package com.kuanquan.qudao.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by on 2017/3/20.
 */

public class CollectionsUtil {
    public static boolean isListEmpty(List list) {
        if (list == null) {
            return true;
        } else {
            return list.isEmpty();
        }
    }

    public static boolean isMapEmpty(Map map) {
        if (map == null) {
            return true;
        } else {
            return map.isEmpty();
        }
    }

    public static boolean isSetEmpty(Set set) {
        if (set == null) {
            return true;
        } else {
            return set.isEmpty();
        }
    }
}
