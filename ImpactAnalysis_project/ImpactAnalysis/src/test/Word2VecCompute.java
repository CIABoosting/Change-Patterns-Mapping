package test;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.nlp.util.Tokenizer;
import org.nlp.vec.VectorModel;
import org.nlp.vec.Word2Vec;

public class Word2VecCompute {
	private static final String modelFilePath = "E:\\ImpactAnalysis\\实验\\corpus1.nn";
	
	private static final VectorModel vm;
	private static final Map<String, float[]> wordMap;

	static {
		vm = VectorModel.loadFromFile(modelFilePath);
		wordMap = vm.getWordMap();
	}	
	/**
	 *	璁＄畻涓や釜鍚戦噺鐨勪綑寮﹁窛绂� 
	 */
	public static double cos(float[] vector1, float[] vector2) {	
		if(vector1 == null || vector2 == null)
			return 0;
		int length = vector1.length;
		double numerator = 0;
		double elem1 = 0;
		double elem2 = 0;
		for(int i=0; i<length; i++) {
			numerator += vector1[i]*vector2[i];
			elem1 += vector1[i]*vector1[i];
			elem2 += vector2[i]*vector2[i];
		}
		double denominator = Math.sqrt(elem1)*Math.sqrt(elem2);
		
		return numerator/denominator;
		
	}
	/**
	 *	璁＄畻涓や釜鎻愪氦鐨勭浉浼煎害 
	 */
	public static double similarity2Commits(List<String> commit1, List<String> commit2){
		double result = 0;
		double max = Double.MIN_VALUE;
		double sum1 = 0;      //commit1闆嗗悎->commit2闆嗗悎
		double sum2 =0;         //commit2闆嗗悎->commit1闆嗗悎
		float[] vect1 = null;
		float[] vect2 = null;
		double length1 = commit1.size();
		double length2 = commit2.size();
		for(String c1: commit1) {
			vect1 = wordMap.get(c1);
//			System.out.println("c1's vector is锛� " + vect1.toString());
			for(String c2: commit2) {
				vect2 = wordMap.get(c2);
//				System.out.println("c2's vector is锛� " + vect1.toString());
				max = Math.max(max, cos(vect1, vect2));
			}
			sum1 += max;
			max = 0;
		}		
		for(String c2: commit2) {
			vect2 = wordMap.get(c2);
//			System.out.println("c2's vector is锛� " + vect1.toString());
			for(String c1: commit1) {
				vect1 = wordMap.get(c1);
//				System.out.println("c1's vector is锛� " + vect1.toString());
				max = Math.max(max, cos(vect1, vect2));
			}
			sum2 += max;
			max = 0;
		}
		result = (sum1/length1 + sum2/length2)/2.0;		
		return result;
	}	
	/**
	 *	璁粌鍘熷璇枡搴�
	 */
	public static void readByJava(String textFilePath, String modelFilePath) {
		Word2Vec wv = new Word2Vec.Factory().setMethod(Word2Vec.Method.Skip_Gram).setNumOfThread(1).build();
		try (BufferedReader br = new BufferedReader(new FileReader(textFilePath))) {
			int lineCount = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				wv.readTokens(new Tokenizer(line, " "));
				lineCount++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		wv.training();
		wv.saveModel(new File(modelFilePath));
	}
	
	public static void testVector(String modelFilePath) {
		System.out.println(wordMap.size());
		Iterator<String> iter = wordMap.keySet().iterator();
		String key = null;
		float[] val = null;
		while (iter.hasNext()) {
			key = iter.next();
			val = wordMap.get(key);
			System.out.print(key + ":");
			for (int j = 0; j < val.length; j++) {
				if(j < val.length-1){
					System.out.print(val[j] + ",");
				}else{
					System.out.print(val[j]);
				}				
			}
			System.out.print("\n");
		}
	}
	public static void getWord2WordSimi() {
		long startTime1=System.currentTimeMillis();
		int count = wordMap.size();
		for(String key1:wordMap.keySet()) {
			System.out.println(count--);
			Map<String, Double> word2wordSimi = new HashMap<>();
			for(String key2:wordMap.keySet()) {
				if(word2wordSimi.get(key1+"2"+key2)==null && word2wordSimi.get(key2+"2"+key1)==null) {
					word2wordSimi.put(key1+"2"+key2, cos(wordMap.get(key1), wordMap.get(key2)));
				}
				
			}
			try {
				FileWriter fWriter = new FileWriter("E:\\ImpactAnalysis\\实验\\word2wordSimi.txt",true);
				BufferedWriter bw= new BufferedWriter(fWriter);
				for(String key:word2wordSimi.keySet()) {
					bw.write(key+":"+word2wordSimi.get(key)+"\r\n");
				}
				bw.close();
				fWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
		long endTime1=System.currentTimeMillis();
		int time = (int)(endTime1 - startTime1) / 1000;
		System.out.println("spent: "+time/2400 +"h "+ (time%2400)/60 +"min "+ (time%2400%60)%60 +"s");
	}
	public static void main(String[] args) {
//		String textFilePath = "E:\\ImpactAnalysis\\瀹為獙\\corpus.dat";
//		String outputFile = "E:\\ImpactAnalysis\\瀹為獙\\corpus1.nn";
//		readByJava(textFilePath, outputFile);
//		testVector(outputFile);
//		List<String> comment1 = readText("E:\\ImpactAnalysis\\data\\aeron\\64\\corpus\\RecordingLog\\newcode.txt");
//		List<String> comment2 = readText("E:\\ImpactAnalysis\\data\\aeron\\98\\corpus\\BasicArchiveTest\\newcode.txt");
//		String text1 = readData("E:\\ImpactAnalysis\\data\\aeron\\64\\corpus\\RecordingLog\\newcode.txt");
//		String text2 = readData("E:\\ImpactAnalysis\\data\\aeron\\98\\corpus\\BasicArchiveTest\\newcode.txt");
//		long startTime1=System.currentTimeMillis();
//		for(int i=1; i<10000; i++) {
//			Text2TextSimilarity(text1, text2);
//		}
//		long endTime1=System.currentTimeMillis();
//		double time = (endTime1 - startTime1) / 1000.0;
//		System.out.println(time);
		getWord2WordSimi();
		
	}
	public static double Text2TextSimilarity(String identifier1, String idetifier2) {

		if(!identifier1.equals("none") && !idetifier2.equals("none")) {
			return similarity2Commits(java.util.Arrays.asList(identifier1.split(" ")), java.util.Arrays.asList(idetifier2.split(" ")));
		}else{
			return 0;
		}
		
	}
	public static List<String> readText(String Path) {
		List<String> text = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String temp = null;
			if((temp = reader.readLine()) != null) {
				text = java.util.Arrays.asList(temp.split(" "));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("file not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		}
		return text;
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
}
