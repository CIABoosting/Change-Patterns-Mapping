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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.Copies;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.soap.Text;

public class MergeCorpus {
	
	public static String readText(String Path) {
		String text = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String temp = null;
			if((temp = reader.readLine()) != null) {
				text = temp;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			System.out.println("file not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			System.out.println("IOException");
		}
		
		return text;
	}
	
	public static Map<String, String> readCorpus(String Path) {
	
		String corpusPath = Path + "\\corpus";
		Map<String, String> corpus = new LinkedHashMap<>();
		if (Evaluate.getCoreClass(Path) == null) {
			return corpus;
		}
		String oldcode = readText(Path + "\\corpus\\" + Evaluate.getCoreClass(Path) + "\\" + "oldcode.txt");
		String newcode = readText(Path + "\\corpus\\" + Evaluate.getCoreClass(Path) + "\\" + "newcode.txt");
		String comment = readText(Path + "\\corpus\\" + Evaluate.getCoreClass(Path) + "\\" + "comment.txt");
		if(oldcode == null && newcode == null && comment == null) return corpus;
		if(oldcode!=null) {
			corpus.put("oldcode", oldcode);
		}else {
			corpus.put("oldcode", "none");
		}
		if (newcode!=null) {
			corpus.put("newcode", newcode);
		}else {
			corpus.put("newcode", "none");
		}
		if(comment != null) {
			corpus.put("comment", comment);
		}else {
			corpus.put("comment", "none");
		}
		return corpus;
	}
	public static boolean isContainChinese(String str) {
	    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
	    Matcher m = p.matcher(str);
	    if (m.find()) {
	        return true;
	    }
	    return false;
	}
	 
	public static Map<String, Map<String, String>> readCorpus() {
		String path = "E:\\ImpactAnalysis\\实验\\allcorpus.txt";
//		String path = "E:\\ImpactAnalysis\\实验\\random_all_corpus.txt";
		
		BufferedReader reader = null;
		Map<String, Map<String, String>> path2Corpus = new LinkedHashMap<>();
		try {
			reader = new BufferedReader(new FileReader(path));
			String temp = null;
			int i = 0;
			while((temp=reader.readLine()) != null) {
				//包含中文就去掉
				if(isContainChinese(temp)) continue;
				i++;
				Map<String, String> corpus = new LinkedHashMap<>();
				String[] lines = temp.split("\\|");
				if(lines.length<=1) continue;
				for(String corps:lines[1].split("\\*")) {
					String[] str = corps.split(":");
					corpus.put(str[0], str[1]);
				}
				path2Corpus.put(lines[0], corpus);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path2Corpus;
	}
	
	
	

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		List<String> CommitBase = Evaluate.getCommitData();
//		Map<String, Map<String, String>> allCorpus = new HashMap<>();
//		int commitNum = CommitBase.size();
//		int commitNum75 = (int) (CommitBase.size() * 0.75);
//		int commitNum50 = (int) (CommitBase.size() * 0.5);
//		int commitNum25 = (int) (CommitBase.size() * 0.25);
//		int countFinishCommit = 0;
//		for(String commitPath:CommitBase) {
//			if(countFinishCommit == 0) {
//				System.out.println("Starting to merge corpus");
//			}
//			if(countFinishCommit == commitNum) {
//				System.out.println("finish------------------------------ 100%");
//			}else if (countFinishCommit == commitNum75) {
//				System.out.println("finish------------------------------ 75%");
//			}else if(countFinishCommit == commitNum50) {
//				System.out.println("finish------------------------------ 50%");
//			}else if (countFinishCommit == commitNum25) {
//				System.out.println("finish------------------------------ 25%");
//			}
//			
//			Map<String, String> corpus = readCorpus(commitPath);
//			if(corpus.size() != 0) {
//				allCorpus.put(commitPath, corpus);
//			}
//			countFinishCommit++;
//		}
//		SaveCorpus(allCorpus);
		System.out.println("In MergeCorpus Main. ");
//		long startTime1=System.currentTimeMillis();
//		Map<String, Map<String, String>> path2Corpus = readCorpus();
//		System.out.println(path2Corpus.size());
//		long endTime1=System.currentTimeMillis();
//		int time = (int)(endTime1 - startTime1) / 1000;
//		System.out.println("readCorpus spent: "+time/2400 +"h "+ (time%2400)/60 +"min "+ (time%2400%60)%60 +"s");

		
//		for(String path:path2Corpus.keySet()) {
//			System.out.println(path + " : ");
//			for(String key:path2Corpus.get(path).keySet()) {
//				System.out.println(key + " : " + path2Corpus.get(path).get(key));
//			}
//			System.out.println("-----------------------------------------------------");
//		}
	}

}
