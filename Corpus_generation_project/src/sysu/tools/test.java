package sysu.tools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class test {

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
		return class2index;
		}
		//获取提交里关键类的耦合关系
		public static Map<String, List<Integer>> getCoupleRel(String Path) {
			String CoreClass = getCoreClass(Path);
			File file = new File(Path + "\\CouplingReult.txt");
			if(!file.exists()) 
				return null;
			Map<String, String> class2index= getClass2Index(Path + "\\CouplingReult.txt");
			String coreClassIndex = null;
			for(String key:class2index.keySet()) {
				if(class2index.get(key).equals(CoreClass)) {
					coreClassIndex = key;
				}
			}
			Map<String, List<Integer>> class2couple = new HashMap<>();
			String tarClassIndex = null;
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(Path + "\\CouplingReult.txt"));
				String line = null;
				while((line = reader.readLine())!=null && coreClassIndex!=null) {
					
					if(line.contains("vs") && line.contains(coreClassIndex)) {
						System.out.println(line);
						List<Integer> couple = new ArrayList<>();
						String[] str1 = line.split("  vs  ");
						String[] str2 = line.split(":");
						if(!str1[0].equals(coreClassIndex)) {		
							tarClassIndex = str1[0];
						}
						else {
							tarClassIndex = str1[1].split("：")[0];
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
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return class2couple;
		}
		public static String getCoreClass(String path) {
			String coreclass = null;
			File f = new File(path+ "\\coreclassName.txt");
			if(!f.exists()) {
				return coreclass;
			}else {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(f));
					if((coreclass = reader.readLine())!=null) {
						return coreclass;
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					return coreclass;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return coreclass;
				}
				return coreclass;
			}
		}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2147";
		System.out.println(getCoupleRel(path));
		
	}

}
