package sysu.coreclass;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortHashmap {
	public List<Map.Entry<String, Double>> sortMap(Map<String, Double> map) {
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() { 
            //降序排序  
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {  
                //return o1.getValue().compareTo(o2.getValue());  
                return o2.getValue().compareTo(o1.getValue());  
            }  
        });  
//        System.out.println("sorted map");
//        for (Entry<String, Double> mapping : list) {  
//            System.out.println(mapping.getKey() + " : " + mapping.getValue());  
//        } 
        return list;
	
	}
	

	public static void  main(String[] args) throws IOException {
//		Map<String, Double> map = new HashMap<String, Double>();
//		
//		map.put("kjk", 1.0);
//		map.put("yys", 2.0);
//		map.put("yns", 4.0);
//		map.put("asf", 7.0);
//          
		SortHashmap sort = new SortHashmap();
//		List<Map.Entry<String, Double>> list = sort.sortMap(map);

	
	}

}

