package ImpactAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sysu.sei.reverse.Comparator.FileFilter;
import sysu.sei.reverse.Comparator.VersionComparator;

public class test {
	static String sourceCodePath = "E:\\ImpactAnalysis\\实验\\hsqldb\\source\\hsqldb1.7.2";
	
	public static String SourceClassPath(String classname) {
		
		List<String> list = new ArrayList<>();
		getSourceClassPath(sourceCodePath,
				classname, list);
		if(list.size() > 0) {
			return list.get(0);
		}else {
			return null;
		}
		
	}
	public static void getSourceClassPath(String projectPath, String classname, List<String> sourceClassPath) {
		File[] files = new File(projectPath).listFiles();
	
		for(File f:files) {
			if (f.isDirectory()) {
				getSourceClassPath(f.getAbsolutePath(), classname,sourceClassPath);
				}
			else if(f.isFile()) {
				if(f.getName().equals(classname + ".java")) {
					sourceClassPath.add(f.getAbsolutePath());
					}
				}
		}
	}
	public static boolean getCoupleRel(String Path) {
		String CoreClass = getCoreClass(Path);
		File file = new File(Path + "\\CouplingReult.txt");
		File file2 = new File(Path + "\\old\\src");
		if (!file2.exists()) {
			file2 = new File(Path + "\\old");
		}
		if(!file2.exists()) return false;
		double fileNum = file2.listFiles().length-1;
		if(!file.exists()) 
			return false;
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
//		System.out.println(class2couple.size()/fileNum);
		if(class2couple.size()/fileNum>0.4 || class2couple.size()>3) {
			return true;
		}else {
			return false;
		}
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
//			e.printStackTrace();
			return null;
		}
	}
	public static List<String> getCommitData() {
		String[] paths = {"E:\\ImpactAnalysis\\data","E:\\ImpactAnalysis\\data3"};
		List<String> commitPath = new ArrayList<String>();
		for(String path:paths) {
			File f = new File(path);
			File[] projectlist = f.listFiles();
			for(File version:projectlist) {
				File f1 = new File(version.getPath());
				File[] commits = f1.listFiles();
				for(File commit: commits) {
					commitPath.add(commit.getPath());
				}
				
			}
		}
		return commitPath;
	}
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
	public static void main(String[] args) {
//		List<String> commitPath = new ArrayList<>();
//		BufferedReader reader =null;
//		try {
//			reader = new BufferedReader(new FileReader("E:\\ImpactAnalysis\\实验\\allcommit.txt"));
//			String temp = null;
//			while((temp =reader.readLine())!=null) {
//				commitPath.add(temp);
//			}
//			reader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//		}
//		System.out.println(commitPath.size());
//		System.out.println(Evaluate.class2class("E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2151\\new\\src\\JEditTextArea.java",
//				"E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2151\\new\\src\\Buffer.java"));

	}
}
