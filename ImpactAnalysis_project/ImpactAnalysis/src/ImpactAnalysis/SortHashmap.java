package ImpactAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortHashmap {
	public static Map<String, Double> sortDoubleMap(Map<String, Double> map, int sign) {
		Map<String, Double> sortedMap = new LinkedHashMap();
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {  
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {   
            	if (sign > 0) {
            		return o2.getValue().compareTo(o1.getValue());  
				}
                else {
                	return o1.getValue().compareTo(o2.getValue()); 
				}
            }  
        });  
        for (Entry<String, Double> mapping : list) {  
        	sortedMap.put(mapping.getKey(), mapping.getValue());
//            System.out.println(mapping.getKey() + " : " + mapping.getValue());  
        } 
        return sortedMap;
	}
	
	public static Map<String, Integer> sortIntegerMap(Map<String, Integer> map, int sign) {
		Map<String, Integer> sortedMap = new LinkedHashMap();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
            	if (sign > 0) {
            		return o2.getValue().compareTo(o1.getValue());  
				}
                else {
                	return o1.getValue().compareTo(o2.getValue()); 
				}
            }  
        });  

        for (Entry<String, Integer> mapping : list) {  
        	sortedMap.put(mapping.getKey(), mapping.getValue());
//            System.out.println(mapping.getKey() + " : " + mapping.getValue());  
        } 
        return sortedMap;
	}
	
	
	
	public static void  main(String[] args) throws IOException {
		long startTime=System.currentTimeMillis();
		Map<String, Integer> map = new HashMap<String, Integer>();
//		
		map.put("kjk", 1);
		map.put("yys", 2);
		map.put("yns", 4);
		map.put("asf", 7);
        int d = 3;  
		map = SortHashmap.sortIntegerMap(map, -1);
		System.out.println(map);
		for(String key : map.keySet()) {
			if(map.get(key) < d) {
				map.remove(key);
				map.put("test", d);
			}
			break;
		}
		map = SortHashmap.sortIntegerMap(map, 1);
		System.out.println(map);
		long endTime=System.currentTimeMillis();
		int time = (int)(50001123) / 1000;
		System.out.println(time/2400 +"h"+ (time%2400)/60 +"min"+ (time%2400%60)%60);
	}

}

