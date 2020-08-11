package ImpactAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Score {
	public static List<String> FiltCommit(List<String> commits) {
		List<String> coreclassList = new ArrayList<>();
		List<String> filtcommitlist = new ArrayList<>();
		for(String commitpath:commits) {
			String coreclass = getCoreClass(commitpath + "\\coreclassName.txt");
			if(coreclass!=null) {
				if(!coreclassList.contains(coreclass)) {
					coreclassList.add(coreclass);
					filtcommitlist.add(commitpath);
				}
			}
		}
		return filtcommitlist;
	}
	public static boolean equal(Double num1, Double num2) {
		if((num1 - num2 )> -0.000001 && (num1-num2)<0.000001) {
			return true;
		}else {
			return false;
		}
	}
	public static void ClassDistri(List<String> commits) {
		int[] distribution = new int[100];
		for(int i=0;i<distribution.length;i++) {
			distribution[i] = 0;
		}
		for(String commit:commits) {
			File file = new File(commit +"\\old\\src");
			int idx = file.listFiles().length;
			distribution[idx] ++;
		}
		for(int i=0;i<distribution.length;i++) {
			if(distribution[i]!=0) {
				System.out.println(i + " class: " + distribution[i]);
			}
		}
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
	public static boolean getCoupleRelRate(String Path) {
		String CoreClass = getCoreClass(Path + "\\coreclassName.txt");
		File file = new File(Path + "\\CouplingReult.txt");
		File file2 = new File(Path + "\\old\\src");
		if(!file2.exists()){
			file2 = new File(Path + "\\old");
		}
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
				if(line.contains("vs") && line.contains(coreClassIndex) && line.contains("r0:")) {
					List<Integer> couple = new ArrayList<>();
					String[] str1 = line.split("  vs  ");
					String[] str2 = line.split(":");
					if(!str1[0].equals(coreClassIndex)) {		
						tarClassIndex = str1[0];
					}
					else {
						tarClassIndex = str1[1].split("£º")[0];
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
		if(class2couple.size()/fileNum>0.3 || class2couple.size()>3 ) {
			return true;
		}else {
			return false;
		}
	}
	public static void getCommitScore(String path) {
		String coreclass = getCoreClass(path + "\\coreclassName.txt");
		if(coreclass!=null) {
			List<String> J_impactset = getImpactSet(path + "\\JripplesResult\\" + coreclass +".txt");
			List<String> impactset = getImpactSet(path + "\\Result\\" +"version1.2.txt");
			List<String> validatset = getValidatSet(path + "\\old\\src");
			System.out.println(J_impactset);
			System.out.println(impactset);
			System.out.println(validatset);
			System.out.println("our method: " + "recall5: " + RecallScore(impactset, validatset, 5) +"  "+
					"recall10: " + RecallScore(impactset, validatset, 10));
			System.out.println("Jrriples: " + "recall5: " + RecallScore(J_impactset, validatset, 5) +"  "+
					"recall10: " + RecallScore(J_impactset, validatset, 10));
			
			System.out.println("our method: " + "precision5: " + PrecisionScore(impactset, validatset, 5) + "  "+
			"precision10: " + PrecisionScore(impactset, validatset, 10));
			System.out.println("Jrriples: " + "precision5: " + PrecisionScore(J_impactset, validatset, 5) +"  "+
			"precision10: " + PrecisionScore(J_impactset, validatset, 10));
		}
	}
	public static List<String> getImpactSet(String path) {
		List<String> impactSet = new ArrayList<>();
		File file = new File(path);
		if(!file.exists()) return impactSet;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			String temp = null;
			while((temp=reader.readLine())!=null) {
				temp = temp.replace(".java", "");
				impactSet.add(temp);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return impactSet;
	}
	public static List<String> getValidatSet(String path) {
		List<String> validatSet = new ArrayList<>();
		File file = new File(path);
		File[] files = file.listFiles();
		for(File f:files) {
			if(f.getName().endsWith(".java")) {
				validatSet.add(f.getName().split("\\.")[0]);
			}
		}
		return validatSet;
	}
	public static boolean positiveResult(List<String> impactset, List<String> J_impactset, List<String> validatset) {
		if (RecallScore(impactset, validatset, 5) > RecallScore(J_impactset, validatset, 5) 
				|| RecallScore(impactset, validatset, 10) > RecallScore(J_impactset, validatset, 10)) {
			return true;
		}
		if (PrecisionScore(impactset, validatset, 5) > PrecisionScore(J_impactset, validatset, 5)
				|| PrecisionScore(impactset, validatset, 10) > PrecisionScore(J_impactset, validatset, 10)) {
			return true;
		}
		return false;
	}
	public static boolean equalResult(List<String> impactset, List<String> J_impactset, List<String> validatset) {
		if (equal(RecallScore(impactset, validatset, 5),RecallScore(J_impactset, validatset, 5))
				&& equal(RecallScore(impactset, validatset, 10),RecallScore(J_impactset, validatset, 10))
				&&equal(PrecisionScore(impactset, validatset, 5),PrecisionScore(J_impactset, validatset, 5))
				&&equal(PrecisionScore(impactset, validatset, 10), PrecisionScore(J_impactset, validatset, 10))) {	
			return true;
		}
//		if (equal(PrecisionScore(impactset, validatset, 5),PrecisionScore(J_impactset, validatset, 5))
//				&&equal(PrecisionScore(impactset, validatset, 10), PrecisionScore(J_impactset, validatset, 10))) {
//			return true;
//		}
		return false;
	}
	public static boolean negativeResult(List<String> impactset, List<String> J_impactset, List<String> validatset) {
		if (RecallScore(impactset, validatset, 5) < RecallScore(J_impactset, validatset, 5) 
				&& RecallScore(impactset, validatset, 10) < RecallScore(J_impactset, validatset, 10)
				&&PrecisionScore(impactset, validatset, 5) < PrecisionScore(J_impactset, validatset, 5)
				&& PrecisionScore(impactset, validatset, 10) < PrecisionScore(J_impactset, validatset, 10)) {
			return true;
		}
//		if (PrecisionScore(impactset, validatset, 5) < PrecisionScore(J_impactset, validatset, 5)
//				&& PrecisionScore(impactset, validatset, 10) < PrecisionScore(J_impactset, validatset, 10)) {
//			return true;
//		}
		return false;
	}

	public static boolean classD10(String path, int left,int right) {
		File f = new File(path + "\\old\\src\\");
		if( f.listFiles().length <= right  && f.listFiles().length>=left) {
			return true;
		}else {
			return false;
		}
	}
	public static List<String> getAllcommits(String[] projects) {
		List<String> allcommits = new ArrayList<>();
		for(String project:projects) {
			File file = new File(project);
			File[] files = file.listFiles();
			for(File file2 : files) {
				allcommits.add(file2.getAbsolutePath());
			}
			
		}
		return allcommits;
	}
	
	public static List<String> CommitPath(List<String> projects) {
		List<String> commitpath = new ArrayList<String>();
		for( String project:projects){
			File file = new File(project);
			File[] files = file.listFiles();
			for(File f:files) {
				commitpath.add(f.getAbsolutePath());
			}
			
		}
		return commitpath;
	}
	
	public static List<String> CommitPath(String[] projects) {
		List<String> commitpath = new ArrayList<String>();
		for( String project:projects){
			File file = new File(project);
			File[] files = file.listFiles();
			for(File f:files) {
				commitpath.add(f.getAbsolutePath());
			}
			
		}
		return commitpath;
	}
	
	public static double PrecisionScore(List<String> impactset, List<String> validatset, Integer cutPoint) {
		int temp = cutPoint-1;
		double count = .0;
		while(temp>=0) {
			if(temp<impactset.size()) {
				String impactclass = impactset.get(temp);
				if(validatset.contains(impactclass)) {
					count++;
				}	
			}
			temp--;
		}	
		return count/cutPoint;
	}

	public static Double RecallScore(List<String> impactset, List<String> validatset, Integer cutPoint) {
		int temp = cutPoint-1;
		double count = .0;
		while(temp>=0) {
			if(temp<impactset.size()) {
				String impactclass = impactset.get(temp);
				if(validatset.contains(impactclass)) {
					count++;
				}	
			}
			temp--;
		}	
		
		return count/(validatset.size()-1);
	}
	
	public List<String> CoreClass(String path){
		List<String> className = new ArrayList<String>();
		File coreclass = new File(path);
		if(coreclass.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(coreclass));
				int i = 0;
				while(i<3) {
					className.add(reader.readLine());
					i++;
				}
				reader.close();
//				System.out.println(className);
				return className;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return className;
	}
	public static String getCoreClass(String path) {
		String coreclass = null;
		File f = new File(path);
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
}
