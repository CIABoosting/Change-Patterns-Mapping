package ImpactAnalysis;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.Thread.State;
import java.nio.file.Path;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.STRING;
import javax.xml.bind.SchemaOutputResolver;

import org.apache.poi.util.TempFile;

import liuyang.nlp.lda.main.LdaGibbsSampling.parameters;
import sysu.sei.reverse.Comparator.FileFilter;
import sysu.sei.reverse.Comparator.VersionComparator;
import test.Word2VecCompute;
public class Evaluate {
	
	static String modifyLog = "\\result\\Impactminer_limitnum07_Simimodify.txt";
	static String simiCommitLog = "\\result\\Impactminer_limitnum07_Similarity.txt";
	static String resultLog = "Impactminer_limitnum07.txt";
	
	static int find_num = 0;
	static int sum_of_time = 0;
	
	public static void write_to_file(String fileName, String content) {   
        try {   
            FileWriter writer = new FileWriter(fileName, true);   
            writer.write(content);   
            writer.close();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }  
	
	
	public static List<Integer> class2class(String path1, String path2) {
		VersionComparator comparator = new VersionComparator();
		File file1 = new File(path1);
		File file2 = new File(path2);
		FileFilter filter = new FileFilter();
		filter.filteringAnnotation4file(file1);
		filter.filteringAnnotation4file(file2);
		comparator.class2class(path1, path2);
		try {
			return comparator.getChange().getRelations();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	public static String getCoreClass(String path){
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
				if(line.contains("vs") && line.contains(coreClassIndex) && line.contains("r0:")) {
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

	public static List<String> getTestSet(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		List<String> testset = new ArrayList<>();
		for(File f : files) {
			testset.add(f.getAbsolutePath());
		}
		return testset;
	}
	

	public static List<String> getCommitData(Boolean filted) {
		List<String> commitPath = new ArrayList<String>();
		if(!filted) {
		String[] paths = {"E:\\ImpactAnalysis\\data","E:\\ImpactAnalysis\\data3","E:\\ImpactAnalysis\\data4"};
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
	}else {
		BufferedReader reader =null;
		try {
			reader = new BufferedReader(new FileReader("E:\\ImpactAnalysis\\实验\\allcommit.txt"));
			String temp = null;
			while((temp =reader.readLine())!=null) {
				commitPath.add(temp);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return commitPath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return commitPath;
		}
		return commitPath;
	}
		}
	

	public static List<String> getInitImpactSet(String path) {
		String classname = getCoreClass(path);
//		File jrippleResult = new File(path + "/JripplesResult/" + classname + ".txt");
//		File jrippleResult = new File(path + "/RoseResult/"+classname+".txt");
		File jrippleResult = new File(path + "/ImpactminerResult/" + classname + ".txt");
		
		List<String> ImpactedSet = new ArrayList<String>();
		if(jrippleResult.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(jrippleResult));
				String temp = null;
				while((temp = reader.readLine())!=null) {
					ImpactedSet.add(temp);
				}
			return ImpactedSet;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}else {
				return null;
			}
		return ImpactedSet;
	}
	
	public static String SourceClassPath(String classname, String sourceCodePath) {
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
			if(sourceClassPath.size()>0) {
				return;
			}
			if (f.isDirectory()) {
				getSourceClassPath(f.getAbsolutePath(), classname,sourceClassPath);
				}
			else if(f.isFile()) {
//				if(f.getName().equals(classname)) {
				if(f.getName().equals(classname+".java")) {
					sourceClassPath.add(f.getAbsolutePath());
					}
				}
		}
	}
	
	public static double commit2commitSimilarity(Map<String, String> corp1, Map<String, String> corp2, List<Double> recordSimi) {
		double oldcodeSimi = Word2VecCompute.Text2TextSimilarity(corp1.get("oldcode"), corp2.get("oldcode"));
		double newcodeSimi = Word2VecCompute.Text2TextSimilarity(corp1.get("newcode"), corp2.get("newcode"));
		double commentSimi = Word2VecCompute.Text2TextSimilarity(corp1.get("comment"), corp2.get("comment"));
		if(recordSimi.size()==0) {
			recordSimi.add(oldcodeSimi);
			recordSimi.add(newcodeSimi);
			recordSimi.add(commentSimi);
		}else {
			recordSimi.set(0, oldcodeSimi);
			recordSimi.set(1, newcodeSimi);
			recordSimi.set(2, commentSimi);
		}
		return (oldcodeSimi + newcodeSimi + commentSimi)/3;
//	
	}
	
	public static Map<String, Integer> InitialScore(List<String> ImpactSet) {
		Map<String, Integer> ImpactSet2Score = new HashMap<>();
		int score = ImpactSet.size() * 10;
		for(String impactClass : ImpactSet) {
			ImpactSet2Score.put(impactClass, score);
			score -= 10;
		}
		return ImpactSet2Score;
	}
	
	public static Map<String, Double> findNSimiCommit(Map<String, String> testDataCorp, int n, String path, int size,Map<String, Map<String, String>> corpusBase) throws IOException {
		double starttime = System.currentTimeMillis();
		List<String> commitDataSet = getCommitData(true);
		int commitNum = commitDataSet.size();
//		Map<String, Map<String, String>> corpusBase = MergeCorpus.readCorpus();
		
		Map<String, Double> SimiCommit = new LinkedHashMap<String, Double>();
		
		Map<String, List<Double>> simiCommitRecord = new LinkedHashMap<>();
		
		int countMiss = 0;
		
		for(String commit:commitDataSet) {
			File temp=new File(commit);
			File currentCommit=new File(path);
			String project_name=currentCommit.getParentFile().getParentFile().getParentFile().getName();
			int current_num=Integer.parseInt(currentCommit.getName());
			String simi_project_name=temp.getParentFile().getName();
			int simi_num=Integer.parseInt(temp.getName());
			if(simi_project_name.equals(project_name) && simi_num>=current_num) {
				continue;
			}
			
			//---------------------------------------------------------------
			
			
			if(!Score.getCoupleRelRate(commit)) continue;
			List<Double> recordSimi = new ArrayList<>();
			Map<String, String> commitCorpus = corpusBase.get(commit);
			if (commitCorpus == null) {
				countMiss++;
				continue;
			}		
			double similarity = commit2commitSimilarity(testDataCorp, commitCorpus, recordSimi);
			if(SimiCommit.size() < n) {
				SimiCommit.put(commit, similarity);
				simiCommitRecord.put(commit, recordSimi);
			}
			else {
				SimiCommit = SortHashmap.sortDoubleMap(SimiCommit, -1);
				for(String key:SimiCommit.keySet()) {
					if(SimiCommit.get(key) < similarity) {
						SimiCommit.remove(key);
						SimiCommit.put(commit, similarity);
						simiCommitRecord.remove(key);
						simiCommitRecord.put(commit,recordSimi);
					}
					break;
				}
			}
		}
		saveSimiCommit(SimiCommit, simiCommitRecord, path);
		double endtime = System.currentTimeMillis();
		System.out.println("find "+SimiCommit.size()+" similar commit spent " + (int)(endtime - starttime)/1000);
		
		sum_of_time+=(int)(endtime - starttime)/1000;
		find_num+=1;
		System.out.println("All "+find_num + ", sum_of_time "+sum_of_time);	
		
		
		return SimiCommit;
	}
	public static void saveSimiCommit(Map<String, Double> SimiCommit, Map<String, List<Double>> simiCommitRecord, String path) {
		File f = new File(path + "\\result");
		if(!f.isDirectory()) {
			f.mkdirs();
		}
		File file = new File(path + simiCommitLog);
		if(!file.exists()) {
			try {
				FileWriter fw = new FileWriter(path + simiCommitLog);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String simicommit : SimiCommit.keySet()) {
					bw.write(simicommit +":" + SimiCommit.get(simicommit) + "," + "oldcodesimi:" + simiCommitRecord.get(simicommit).get(0)+
							",newcodesimi:"+simiCommitRecord.get(simicommit).get(1) + ",commentsimi:"+ simiCommitRecord.get(simicommit).get(2)+
							"\r\n");				
				}
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	public static Map<String, Integer> modifyImpactSet(Map<String, Integer> ImpactSet2Score, Map<String, Double> SimiNCommits, 
			String testData, String testDataCoreclass, String sourceCodePath) {
		Map<String, List<String>> simiModify = new LinkedHashMap<>();
		int flag = 0;
		double readCoupleRelTime = .0;
		double sourceclassPathTime = .0;
		double readSimilarCommitCoupleTime = .0;
		double computeCoupleSimiTime = .0;
		Map<String, String> allImpactClassPath = new HashMap<>();
		Map<String, List<Integer>> allImpactClassCouple = new HashMap<>();
		for(String simiCommit:SimiNCommits.keySet()) {
			List<String> modifyclass = new ArrayList<>();
			
			Map<String, List<Integer>> couplingSet = getCoupleRel(simiCommit);

			if(couplingSet.size() == 0 || couplingSet == null)
				continue;
			
			for(String ImpactClass : ImpactSet2Score.keySet()) {

				String ImpactClassPath;
				List<Integer> ImpactClassCouple;
				if(flag == 1) {
					ImpactClassPath = allImpactClassPath.get(ImpactClass);
				}else {					
					ImpactClassPath = SourceClassPath(ImpactClass,sourceCodePath);	
					allImpactClassPath.put(ImpactClass, ImpactClassPath);
				}
				if(ImpactClassPath == null) 
					continue;
				
				if(flag == 1) {
					ImpactClassCouple = allImpactClassCouple.get(ImpactClassPath);
				}else {
					ImpactClassCouple = class2class(ImpactClassPath, 
							testData + "\\old\\src\\" + testDataCoreclass + ".java");
					allImpactClassCouple.put(ImpactClassPath, ImpactClassCouple);
				}
				
				for(List<Integer> simiCouple : couplingSet.values()) {
					if(computeCoupleSimi( ImpactClassCouple,simiCouple)) {
						ImpactSet2Score.put(ImpactClass, ImpactSet2Score.get(ImpactClass) + 20);
						modifyclass.add(ImpactClass);
					}
				}
			}	
			flag = 1;
			simiModify.put(simiCommit, modifyclass);
		}
		saveSimiModify(simiModify, testData);
		return ImpactSet2Score;
	}
	public static void saveSimiModify(Map<String, List<String>> simiModify, String path) {
		File f = new File(path + "\\result");
		if(!f.isDirectory()) {
			f.mkdirs();
		}
		File file = new File(path + modifyLog);
		if(!file.exists()) {
			try {
				FileWriter fw = new FileWriter(path + modifyLog);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String simicommit : simiModify.keySet()) {
					String temp = simicommit + ":";
					for(String str:simiModify.get(simicommit)) {
						temp = temp + str+",";
					}
					bw.write(temp + "\r\n");
				}
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean JaccardCoupleSimi(List<Integer> coupleRel1,List<Integer> coupleRel2) {
		for(int i=0; i<coupleRel1.size();i++) {
			if(coupleRel1.get(i)!=0) {
				coupleRel1.set(i, 1);
			}else {
				coupleRel1.set(i, 0);
			}
		}
		for(int i=0; i<coupleRel2.size();i++) {
			if(coupleRel2.get(i)!=0) {
				coupleRel2.set(i, 1);
			}else {
				coupleRel2.set(i, 0);
			}
		}
		double both = 0;
		double all = 0;
		for(int i=0; i<coupleRel2.size(); i++) {
			if(coupleRel1.get(i)>0 && coupleRel1.get(i)>0) {
				both+=1;
			}
		}
		for(int i=0; i<coupleRel2.size(); i++) {
			if(coupleRel1.get(i)>0 || coupleRel1.get(i)>0) {
				all+=1;
			}
		}
		double jaccard = both/all;
		if(jaccard>0.5) {
			return true;
		}else {
			return false;
		}
	}
	public static boolean computeCoupleSimi(List<Integer> coupleRel1, List<Integer> coupleRel2) {
		if(coupleRel1 == null || coupleRel2 == null || coupleRel1.size() == 0 || coupleRel2.size() == 0 ) return false;
		double numerator= 0;
		double elem1 = 0;
		double elem2 = 0;
		for(int i=0; i<coupleRel2.size(); i++) {
			numerator += coupleRel1.get(i) * coupleRel2.get(i);
			elem1 += coupleRel1.get(i) * coupleRel1.get(i);
			elem2 += coupleRel2.get(i) * coupleRel2.get(i);
		}
		//耦合关系  相似度
//		System.out.println(numerator/(Math.sqrt(elem1)*Math.sqrt(elem2)));
		if(numerator/(Math.sqrt(elem1) * Math.sqrt(elem2)) > 0.7) {
			return true;
		}else {
			return false;
		}

	}
	public static String readData(String datapath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(datapath));
			String temp = null;
			while((temp=reader.readLine())!=null) {
				return temp;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return "none";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "none";
		}
		return "none";
	}
	public static Map<String, String> readCommitCorp(String path, String coreclass) {
		Map<String, String> corpus = new LinkedHashMap<>();
		String[] paths  = {path + "\\corpus\\" + coreclass +"\\oldcode.txt",
				path + "\\corpus\\" + coreclass +"\\newcode.txt",
				path + "\\corpus\\" + coreclass +"\\comment.txt"
		};
		corpus.put("oldcode", readData(paths[0]));
		corpus.put("newcode", readData(paths[1]));
		corpus.put("comment", readData(paths[2]));
		
		return corpus;
	}
	public static void saveResult(Map<String, Integer> impactset2score, String path, String version) {
		File f = new File(path + "\\result");
		if(!f.isDirectory()) {
			f.mkdirs();
		}
		File file = new File(path + "\\result\\" + version);
		if(!file.exists()) {
			try {
				FileWriter fw = new FileWriter(path + "\\result\\" + version);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String impactClass : impactset2score.keySet()) {
					bw.write(impactClass +"\r\n");
				}
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	public static void main(String[] args) throws IOException {
		Map<String, Map<String, String>> corpusBase = MergeCorpus.readCorpus();
		System.out.println("Done loadding corpus");
		HashMap<String, String> commit2source = 
				new HashMap<String, String>() { 
				{ 
					// Change dataPath here
					put("E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2", "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit3.2.0source");
					put("E:\\ImpactAnalysis\\实验\\jedit\\commit\\4.0", "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit4.0.0source");
					put("E:\\ImpactAnalysis\\实验\\jedit\\commit\\4.1", "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit4.1.0source");
					put("E:\\ImpactAnalysis\\实验\\jedit\\commit\\4.2", "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit4.2.0source");
				} 
		};
		for(String testdataPath:commit2source.keySet()) { 
			List<String> testDataSet = getTestSet(testdataPath);
			
			int count = testDataSet.size();
			
			long startTime1=System.currentTimeMillis();
			
			// commit
			for(String testData:testDataSet) {
				File fileSize = new File(testData+"\\old\\src");
				
				if(!fileSize.exists()) {
					fileSize = new File(testData +"\\old");
				}
				
				int size = fileSize.listFiles().length;
				
				System.out.println("computing " + testData);
				System.out.println("remain " + count-- + " commits");
				
				boolean rate = Score.getCoupleRelRate(testData);
				System.out.println(rate);
				if(!rate) {
					System.out.println(rate);
					continue;
				}
				
				File file = new File(testData + "\\result\\" + resultLog);
				if(file.exists()) continue;
				
				String testDataCoreclass = getCoreClass(testData);
				if(testDataCoreclass == null) continue;
				
				Map<String, String> testdataCorp = readCommitCorp(testData, testDataCoreclass);
				List<String> impactset = getInitImpactSet(testData);
				if(impactset==null) continue;
				Map<String, Integer> ImpactSet2Score = SortHashmap.sortIntegerMap(InitialScore(impactset), 1);
				Map<String, Integer> ImpactSet2Score2 = SortHashmap.sortIntegerMap(InitialScore(impactset), 1);
				int siminum=20;
				Map<String, Double> SimiNCommits = findNSimiCommit(testdataCorp, siminum, testData,size,corpusBase);
				System.out.println("Under Imactminer had found "+siminum+" simi commiits");
				ImpactSet2Score2 = SortHashmap.sortIntegerMap(
						modifyImpactSet(ImpactSet2Score2, SimiNCommits, testData, testDataCoreclass, commit2source.get(testdataPath)), 1);
				
				System.out.println("finish modify impactset");
				System.out.println();
				
				saveResult(ImpactSet2Score2, testData, resultLog);			
			}
			long endTime1=System.currentTimeMillis();
			int time = (int)(endTime1 - startTime1) / 1000;
			System.out.println("compute commits spent: "+time/2400 +"h "+ (time%2400)/60 +"min "+ (time%2400%60)%60 +"s");
		}
	}
}


























