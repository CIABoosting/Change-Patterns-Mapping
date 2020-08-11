package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Test {

	public static Map<String, String> getClass2Index(String Path) {
		BufferedReader reader = null;
		Map<String, String> class2index = new HashMap<String, String>();
		try {
			reader = new BufferedReader(new FileReader(Path));
			String temp = null;
			while((temp = reader.readLine())!=null) {
				if(temp.startsWith("new")) {
					String[] str = temp.split(":");
					String index = str[0].split(" ")[2] + " " + str[0].split(" ")[3];
					String[] str1 = str[1].split("\\.");
					String className = str1[str1.length -1];
					class2index.put(index, className);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	System.out.println(class2index);
	return class2index;
	}
	
	public static void getCoupleRel(String Path, String CoreClass) {
		Map<String, String> class2index = getClass2Index(Path);
		BufferedReader reader = null;
		String coreClassIndex = null;
		for(String key:class2index.keySet()) {
			if(class2index.get(key).equals(CoreClass)) {
				coreClassIndex = key;
			}
		}
		System.out.println(coreClassIndex);
		Map<String, List<Integer>> class2couple = new HashMap<>();
		String tarClassIndex = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line = null;
			while((line = reader.readLine())!=null) {
				if(line.contains("vs") && line.contains(coreClassIndex)) {
					List<Integer> couple = new ArrayList<>();
					String[] str1 = line.split("  vs  ");
					String[] str2 = line.split(":");
//					for(String s:str1) System.out.println(s);
					if(!str1[0].equals(coreClassIndex)) {		
						tarClassIndex = str1[0];
					}
					else {
						tarClassIndex = str1[1].substring(0, 7);
					}
					for(int i = 1; i<str2.length; i++) {
						couple.add(Integer.parseInt(str2[i].split(" ")[0]));	
					}
					class2couple.put(class2index.get(tarClassIndex), couple);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(class2couple);
	}
	
	public static String getCoreClass(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path + "\\coreclassName.txt"));
			String coreclass = reader.readLine();
			reader.close();
			return coreclass;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) {
		Map<String, String> class2index = getClass2Index("E:\\ImpactAnalysis\\data\\aeron\\383\\CouplingReult.txt");
		System.out.println(class2index.get("class 7"));
		getCoupleRel("E:\\ImpactAnalysis\\data\\aeron\\383\\CouplingReult.txt", "PreferredMulticastFlowControl");
	}
}

