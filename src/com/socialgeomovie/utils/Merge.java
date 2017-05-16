package com.socialgeomovie.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

public class Merge {

	public static Map<String, Object> mergeMap(Map<String, Object> mainMap, Map<String, Object> aditionalMap){
		List<Object> nullList = Arrays.asList("N/A", "NA", "null", "empty", null);
		aditionalMap.values().removeIf(val -> nullList.contains(val));
		
		for (String aditionalKey : aditionalMap.keySet()) {
			if (mainMap.containsKey(aditionalKey)) {
				Object mainValue = mainMap.get(aditionalKey);
				Object otherValue = aditionalMap.get(aditionalKey);
				if (mainValue instanceof List) {
					List mainValueList = (List) mainValue;
					
					if (otherValue instanceof List) {
						List otherValueList = (List) otherValue;
						if (!mainValueList.containsAll(otherValueList)) {
							mainMap.put(aditionalKey, new ArrayList<>(new HashSet<>(ListUtils.union(mainValueList, otherValueList))));
						}
						
					} else if (otherValue instanceof String || otherValue instanceof Number) {
						if (!mainValueList.contains(otherValue)) {
							ArrayList<Object> newList = new ArrayList<Object>();
							newList.addAll(mainValueList);
							newList.add(otherValue);
							mainMap.put(aditionalKey, newList);
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
							mainMap.put(aditionalKey, newList);
						}	

					 } else if (otherValue instanceof List) {
							List otherValueList = (List) otherValue;
							if (!otherValueList.contains(mainValueString)) {
								ArrayList<Object> newList = new ArrayList<Object>();
								newList.add(mainValueString);
								newList.addAll(otherValueList);
								mainMap.put(aditionalKey, newList);
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
							mainMap.put(aditionalKey, newList);
						}	

					 } else if (otherValue instanceof List) {
							List otherValueList = (List) otherValue;
							if (!otherValueList.contains(mainValueNumber)) {
								ArrayList<Object> newList = new ArrayList<Object>();
								newList.add(mainValueNumber);
								newList.addAll(otherValueList);
								mainMap.put(aditionalKey, newList);
							}
					 }
			
					
				}
			} else {
				mainMap.putIfAbsent(aditionalKey, aditionalMap.get(aditionalKey));
			}
		}
		
		return mainMap;
		
	}
}

