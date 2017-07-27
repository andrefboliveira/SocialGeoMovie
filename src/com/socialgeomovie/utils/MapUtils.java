package com.socialgeomovie.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

public class MapUtils {

	public static Map<String, Object> mergeMapCombine(Map<String, Object> mainMap, Map<String, Object> aditionalMap){
		Map<String, Object> resultMap = new HashMap<String, Object>(mainMap);
		List<Object> nullList = Arrays.asList("N/A", "NA", "null", "", " ", "empty", "[]", "[ ]", "{}", "{ }", new ArrayList<>(), null);
		aditionalMap.values().removeIf(val -> nullList.contains(val));
		
		for (String aditionalKey : aditionalMap.keySet()) {
			if (resultMap.containsKey(aditionalKey)) {
				Object mainValue = resultMap.get(aditionalKey);
				Object otherValue = aditionalMap.get(aditionalKey);
				if (mainValue instanceof List) {
					List mainValueList = (List) mainValue;
					
					if (otherValue instanceof List) {
						List otherValueList = (List) otherValue;
						if (!mainValueList.containsAll(otherValueList)) {
							resultMap.put(aditionalKey, new ArrayList<>(new HashSet<>(ListUtils.union(mainValueList, otherValueList))));
						}
						
					} else if (otherValue instanceof String || otherValue instanceof Number) {
						if (!mainValueList.contains(otherValue)) {
							ArrayList<Object> newList = new ArrayList<Object>();
							newList.addAll(mainValueList);
							newList.add(otherValue);
							resultMap.put(aditionalKey, newList);
						}
					}
				} else if (mainValue instanceof String) {
					String mainValueString = (String) mainValue;
					 if (otherValue instanceof String) {
						String otherValueString = (String) otherValue;
						if (!mainValueString.toLowerCase().contains(otherValueString.toLowerCase())) {
							ArrayList<Object> newList = new ArrayList<Object>();
							newList.add(mainValueString);
							newList.add(otherValueString);
							resultMap.put(aditionalKey, newList);
						}	

					 } else if (otherValue instanceof List) {
							List otherValueList = (List) otherValue;
							if (!otherValueList.contains(mainValueString)) {
								ArrayList<Object> newList = new ArrayList<Object>();
								newList.add(mainValueString);
								newList.addAll(otherValueList);
								resultMap.put(aditionalKey, newList);
							}
					 }
			
					
				} else if (mainValue instanceof Number) {
					Number mainValueNumber = (Number) mainValue;
					 if (otherValue instanceof Number) {
						 Double mainValueDouble =  mainValueNumber.doubleValue();
						 Number otherValueNumber = (Number) otherValue;
						 Double otherValueDouble =  otherValueNumber.doubleValue();
//						if (!mainValueNumber.equals(otherValueDouble)) {
						if (!(Math.abs(mainValueDouble - otherValueDouble) < 0.1)) {

							ArrayList<Object> newList = new ArrayList<Object>();
							newList.add(mainValueNumber);
							newList.add(otherValueDouble);
							resultMap.put(aditionalKey, newList);
						}	

					 } else if (otherValue instanceof List) {
							List otherValueList = (List) otherValue;
							if (!otherValueList.contains(mainValueNumber)) {
								ArrayList<Object> newList = new ArrayList<Object>();
								newList.add(mainValueNumber);
								newList.addAll(otherValueList);
								resultMap.put(aditionalKey, newList);
							}
					 }
			
					
				}
			} else {
				resultMap.putIfAbsent(aditionalKey, aditionalMap.get(aditionalKey));
			}
		}
		
		return resultMap;
		
	}
	
	public static Map<String, Object> mergeMapOverwrite(Map<String, Object> mainMap, Map<String, Object> aditionalMap){
		List<Object> nullList = Arrays.asList("N/A", "NA", "null", "", " ", "empty", "[]", "[ ]", "{}", "{ }", new ArrayList<>(), null);
		Map<String, Object> resultMap = new HashMap<String, Object>(mainMap);
		resultMap.putAll(aditionalMap);
		resultMap.values().removeIf(val -> nullList.contains(val));
		
		return resultMap;
	}
	
}

