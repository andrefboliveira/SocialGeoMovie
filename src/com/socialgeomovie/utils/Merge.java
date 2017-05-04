package com.socialgeomovie.utils;

import java.util.Map;

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
						mainMap.put(aditionalKey, String.join(";", mainValueString, otherValueString));
					}	
				}
			} else {
				mainMap.putIfAbsent(aditionalKey, aditionalMap.get(aditionalKey));
			}
		}
		
		return mainMap;
		
	}
}
