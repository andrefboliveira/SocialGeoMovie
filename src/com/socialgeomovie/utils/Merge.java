package com.socialgeomovie.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

public class Merge {

	public static Map<String, Object> mergeMap(Map<String, Object> mainMap, Map<String, Object> aditionalMap){
		for (String aditionalKey : aditionalMap.keySet()) {
			if (mainMap.containsKey(aditionalKey)) {
				Object mainValue = mainMap.get(aditionalKey);
				Object otherValue = aditionalMap.get(aditionalKey);
				if (mainValue instanceof String) {
					String mainValueString = (String) mainValue;
					String otherValueString = (String) otherValue;
					if (!mainValueString.contains(otherValueString)) {
						mainMap.put(aditionalKey, String.join(",", mainValueString, otherValueString));
					}	
				} else if (mainValue instanceof List) {
					List mainValueList = (List) mainValue;
					List otherValueList = (List) otherValue;
					
					if (!mainValueList.containsAll(otherValueList)) {
						mainMap.put(aditionalKey, new HashSet<>(ListUtils.union(mainValueList, otherValueList)));
					}
				} else if (mainValue instanceof Number) {
					Number mainValueNumber = (Number) mainValue;
					Number otherValueNumber = (Number) otherValue;
					
					if (!mainValueNumber.equals(otherValueNumber)) {
						mainMap.put(aditionalKey, String.join(",", String.valueOf(mainValueNumber), String.valueOf(otherValueNumber)));
					}
					
				}
			} else {
				mainMap.putIfAbsent(aditionalKey, aditionalMap.get(aditionalKey));
			}
		}
		
		return mainMap;
		
	}
}
