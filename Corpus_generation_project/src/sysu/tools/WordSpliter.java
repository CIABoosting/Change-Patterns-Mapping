package sysu.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class WordSpliter {

	public static WordnetStemmer stemmer = null;

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$@!");
		return pattern.matcher(str).matches();
	}

	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			flag = true;
		}
		return flag;
	}

	static {
		// WordNet 用于取词根
		String wnhome = "C:/Program Files (x86)/WordNet/2.1"; // 获取环境变量WNHOME
		String path = wnhome + File.separator + "dict";
		URL url = null;

		try {
			url = new URL("file", null, path);
		} catch (MalformedURLException e) {
			System.out.println("wordnet url is error.");
		} // 创建一个URL对象，指向WordNet的dict目录
		if (url != null) {
			IDictionary dict = new Dictionary(url);
			try {
				dict.open();
			} catch (IOException e) {
				System.out.println("dictionary create is error.");
			} // 打开词典
			stemmer = new WordnetStemmer(dict);
		}
	}

	// 对文本进行过滤
	public static List<String> split(String sentense) {

		String splitToken = " .,;:/&|`~%+=-*<>$#@!?^\\()[]{}''\"\r\n\t\b";
		StringTokenizer st = new StringTokenizer(sentense, splitToken, false);
		List<String> wordList = new ArrayList<String>();
		// 对comment1 和 comment2 进行分词
		// Pattern pattern = Pattern.compile("\\.\\s*|\\s+|,\\s*|/\\*|\\*/|\\*");
		// String[] words = pattern.split(sentense);

		// 对分词好的字符串进行词干提取
		while (st.hasMoreTokens()) {
			String str = st.nextToken();

			char[] charArray = str.toCharArray();
			List<String> tempWordList = new ArrayList<String>();
			int startIndex = 0;
			for (int i = 0, n = charArray.length; i < n; i++) {
				if (charArray[i] >= 65 && charArray[i] <= 90 && i - 1 >= 0
						&& (charArray[i - 1] < 65 || charArray[i - 1] > 90) && i > 0) {
					String word = String.copyValueOf(charArray, startIndex, i - startIndex).toLowerCase();
					startIndex = i;
					tempWordList.add(word);
				}
			}
			tempWordList.add(String.copyValueOf(charArray, startIndex, charArray.length - startIndex).toLowerCase());
			for (String str2 : tempWordList) {

				// 去除停用词
				if (Stopwords.isStopword(str2)) {
					continue;
				}
				List<String> stemList = null;
				try {
					stemList = stemmer.findStems(str2, POS.NOUN);
				} catch (Exception e) {
					stemList = null;
				}
				if (stemList == null || stemList.isEmpty() || stemList.get(0).equals(str2)) {
					try {
						stemList = stemmer.findStems(str2, POS.VERB);
					} catch (Exception e) {
						stemList = null;
					}
				}
				if (stemList == null || stemList.isEmpty()) {
					wordList.add(str2);
				} else {
					wordList.addAll(stemList);
				}
			}
		}
		// 去除数字字符串,对包含下划线单词进行分词
		String elem = null;
		List<String> help = new ArrayList<String>();
		// 去除包含数字的字符串,对包含下划线单词进行分词
		for (Iterator<String> iterator = wordList.iterator(); iterator.hasNext();) {
			elem = iterator.next();
			if (isInteger(elem) || hasDigit(elem)) {
				iterator.remove();
				continue;
			}
			if (elem.contains("_")) {
				help.addAll(Arrays.asList(elem.split("_")));
				iterator.remove();
			}
		}
		if (!help.isEmpty()) {
			wordList.addAll(help);
		}
		return wordList;
	}

	// 对代码片段进行处理，增加过滤掉java关键字
	public static List<String> split2(String sentense) {

		// 对comment1 和 comment2 进行分词
		// Pattern pattern = Pattern.compile("\\.\\s*|\\s+|,\\s*|/\\*|\\*/|\\*");
		// String[] words = pattern.split(sentense);

		String splitToken = " .,;:/&|`~%+=-*<>$#@!^\\()[]{}''\"\r\n\t\b";
		StringTokenizer st = new StringTokenizer(sentense, splitToken, false);
		List<String> wordList = new ArrayList<String>();

		// WordNet 用于取词根
		String wnhome = "C:/Program Files (x86)/WordNet/2.1"; // 获取环境变量WNHOME
		String path = wnhome + File.separator + "dict";
		URL url = null;
		WordnetStemmer stemmer = null;
		try {
			url = new URL("file", null, path);
		} catch (MalformedURLException e) {
			System.out.println("wordnet url is error.");
		} // 创建一个URL对象，指向WordNet的dict目录
		if (url != null) {
			IDictionary dict = new Dictionary(url);
			try {
				dict.open();
			} catch (IOException e) {
				System.out.println("dictionary create is error.");
			} // 打开词典
			stemmer = new WordnetStemmer(dict);
		}

		// 对分词好的字符串进行词干提取
		while (st.hasMoreTokens()) {
			String str = st.nextToken();

			char[] charArray = str.toCharArray();
			List<String> tempWordList = new ArrayList<String>();
			int startIndex = 0;
			for (int i = 0, n = charArray.length; i < n; i++) {
				if (charArray[i] >= 65 && charArray[i] <= 90 && i - 1 >= 0
						&& (charArray[i - 1] < 65 || charArray[i - 1] > 90) && i > 0) {
					String word = String.copyValueOf(charArray, startIndex, i - startIndex).toLowerCase();
					startIndex = i;
					tempWordList.add(word);
				}
			}
			tempWordList.add(String.copyValueOf(charArray, startIndex, charArray.length - startIndex).toLowerCase());
			for (String str2 : tempWordList) {

				// 去除停用词
				if (Stopwords.isStopword(str2)) {
					continue;
				}
				List<String> stemList = null;
				try {
					stemList = stemmer.findStems(str2, POS.NOUN);
				} catch (Exception e) {
					stemList = null;
				}
				if (stemList == null || stemList.isEmpty() || stemList.get(0).equals(str2)) {
					try {
						stemList = stemmer.findStems(str2, POS.VERB);
					} catch (Exception e) {
						stemList = null;
					}
				}
				if (stemList == null || stemList.isEmpty()) {
					wordList.add(str2);
				} else {
					wordList.addAll(stemList);
				}
			}
		}

		String elem = null;
		List<String> help = new ArrayList<String>();
		// 去除包含数字的字符串,对包含下划线单词进行分词
		for (Iterator<String> iterator = wordList.iterator(); iterator.hasNext();) {
			elem = iterator.next();
			if (isInteger(elem) || hasDigit(elem)) {
				iterator.remove();
				continue;
			}
			if (elem.contains("_")) {
				help.addAll(Arrays.asList(elem.split("_")));
				iterator.remove();
			}
		}
		if (!help.isEmpty()) {
			wordList.addAll(help);
		}

		// 去除java关键字
		String[] keys = { "abstract", "assert", "boolean", "break,", "byte", "case", "catch", "char", "class", "const",
				"continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float",
				"for", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null",
				"package", "private", "protected", "public", "return", "short", "static", "super", "switch",
				"synchronized", "this", "throw", "throws", "transient", "try", "true", "void", "volatile", "while" };
		help = Arrays.asList(keys);

		for (Iterator<String> iterator = wordList.iterator(); iterator.hasNext();) {
			elem = iterator.next();
			if (help.contains(elem))
				iterator.remove();
		}

		return wordList;
	}

	public static String doSplit(String sentense) {
		List<String> wordList = split(sentense);
		StringBuilder sb = new StringBuilder();
		for (String word : wordList) {
			sb.append(word).append(" ");
		}
		return sb.toString();
	}

	
	
	//去除重复的标识符
	public List<String> DelectDup(List<String> list) {
		List<String> uniqueList = new ArrayList<>();
		for(String str: list) {
			if(!uniqueList.contains(str)) {
				uniqueList.add(str);
			}
		}
		return uniqueList;
	}
	//读取注释信息
	public String getComment(String path, int flag) {
		BufferedReader reader = null;
		String temp = null;
		try {
			if(flag == 1) {
				reader = new BufferedReader(new FileReader(path + "/commit.txt"));
				int i = 0;
				while(i<3) {
					temp = reader.readLine();
					i++;}
				}
			else {
				String[] s = path.split("\\\\");
				String index = s[s.length - 1];
				reader = new BufferedReader(new FileReader(path + "\\new\\src\\" + index + ".txt"));
//				reader = new BufferedReader(new FileReader(path + "\\commit.txt"));
				
				int i = 0;
				while(i<5) {
					temp = reader.readLine();
					i++;}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(temp == null || temp == "") return " ";
		String[] s = temp.split(":");
		int flag1 = 0;
		String reString = " ";
		for(String str:s) {
			if (flag1==0) {flag1++;continue;}
			reString = reString + str +" " ;
		}
		return reString;
	}
	
	public void writeText(List<String> changecode, String path, String fileName) {
		if(changecode.size() == 0) return;
		File folder = new File(path);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		try {

			FileWriter fw = new FileWriter(path + "\\" + fileName + ".txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(StringUtils.join(changecode.toArray(), " "));
			bw.close();
			fw.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
	
	public static List<String> getCommitPath(String DataPath) {
		File f = new File(DataPath);
		File[] projectlist = f.listFiles();
		List<String> commitPath = new ArrayList<String>();
		for(File version:projectlist) {
//			System.out.println(version.getPath());
			if(!version.isDirectory()) continue;
			File f1 = new File(version.getPath());
			File[] commits = f1.listFiles();
			for(File commit: commits) {
				commitPath.add(commit.getPath());
//				System.out.println(commit.getPath());
			}
		}
//		System.exit(0);
		return commitPath;
//		System.out.println(commitPath.size());
	}
	
//	获取关键类名称
	public String getCoreClass(String path) {
//		File file = new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path + "\\coreclassName.txt"));
			String coreclass = reader.readLine();
//			coreclass = reader.readLine();
			return coreclass;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeCorpus(List<String> l1, List<String> l2, List<String> l3, String path, String coreClass) {
		List<String> str = new ArrayList<String>();
		List<String> oldCode = mergeCorpus(l1, l2, l3);
		writeText(oldCode, path + "\\corpus\\" + coreClass, "oldcode");
		List<String> newCode = mergeCorpus(l2, l1, l3);
		writeText(newCode, path + "\\corpus\\" + coreClass, "newcode");
		List<String> comment = mergeCorpus(l3, l2, l1);
		writeText(comment, path + "\\corpus\\" + coreClass, "comment");
		
		str.addAll(oldCode);
		str.addAll(newCode);
		str.addAll(comment);
		try {
			File file = new File("E:/ImpactAnalysis/实验/corpus.txt");
			FileWriter fw1 = new FileWriter(file, true);  
	        BufferedWriter bw1 = new BufferedWriter(fw1);
	        bw1.write(StringUtils.join(str.toArray(), " ") + "\r\n");
	        bw1.close();  
	        fw1.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
//		System.out.println(str);
	}
	
	public List<String> mergeCorpus(List<String> l1, List<String> l2, List<String> l3) {
		List<String> str = new ArrayList<String>();
		if(l1.size() == 0) return str;
		for(String s:l1) {
			if(l2.size() != 0) {
				str.add(l2.get(new Random().nextInt(l2.size())));
				str.add(l2.get(new Random().nextInt(l2.size())));
				str.add(s);
				str.add(l2.get(new Random().nextInt(l2.size())));
				str.add(l2.get(new Random().nextInt(l2.size())));
			}
			if(l3.size() != 0) {
				str.add(l3.get(new Random().nextInt(l3.size())));
				str.add(l3.get(new Random().nextInt(l3.size())));
				str.add(s);
				str.add(l3.get(new Random().nextInt(l3.size())));
				str.add(l3.get(new Random().nextInt(l3.size())));
			}
		}
		return str;
	}
	
	
	public static void main(String[] args) {
		// String str1 = "Fixed all deprecation warnings produced when compiling with
		// Java 9. Note there are still two deprecated items that are suppressed for
		// Java 8 with comments on how to adjust when jEdit requires Java 9.\r\n" +
		// "\r\n" +
		// "";
		
		WordSpliter ws = new WordSpliter();
		String DataPath = "C:\\Users\\jjy\\Desktop\\ImpactAnalysis\\code\\sample_project_commit";
		List<String> commitPath = ws.getCommitPath(DataPath);
		
		int count = 0;
		for(String path:commitPath) {
			System.out.println("remain " + Integer.toString(commitPath.size() - (count++)) + " commits");
			System.out.println("computing " + path);
			String coreClass = ws.getCoreClass(path);
//			System.out.println(coreClass);
			if(coreClass == null) 
				{ System.out.println("coreClass is not exist \r\n");continue;}

			String oldCodePath = path + "\\old\\" + coreClass + ".java";
			String newCodePath = path + "\\new\\" + coreClass + ".java";
			File oldf = new File(oldCodePath);
			File newf = new File(newCodePath);
			
			if(!oldf.exists() || !newf.exists()) {
				System.out.println("CoreClass"+coreClass);
				System.out.println("Either old or new core class doesn't exits");
				continue;
			} 
			
			 DiffUtils df = new DiffUtils(oldCodePath, newCodePath);
			 
			 int newChangeCount = df.getNewChangecode().size();
//			 System.out.println(path + "\\old\\src\\" + coreClass + ".java");
			 int oldChangeCount = df.getOldChangecode().size();
			 List<String> newChangeCode = new ArrayList<String>();
			 List<String> oldChangeCode = new ArrayList<String>();
			 for(int i=0; i<newChangeCount; i++) {
				 String[] str = doSplit(df.getNewChangecode().get(i)).split(" ");	 
				 for(String c : str) {
					 if(!c.equals(""))
					 newChangeCode.add(c);
				 }
			 }
			 for(int i=0; i<oldChangeCount; i++) {
				 String[] str = doSplit(df.getOldChangecode().get(i)).split(" ");
				 for(String c : str) {
					 if(!c.equals(""))
						 oldChangeCode.add(c);
				 }
			 }
	 		
			newChangeCode = ws.DelectDup(newChangeCode);
			oldChangeCode = ws.DelectDup(oldChangeCode);
			
			String CommentStr = doSplit(ws.getComment(path , 1));
			java.util.List<String> CommentList = java.util.Arrays.asList(CommentStr.split(" "));
			System.out.println("new change has " + Integer.toString(newChangeCode.size()) + "identifiers");
			System.out.println("old change has " + Integer.toString(oldChangeCode.size()) + "identifiers");
			System.out.println("comment has " + Integer.toString(CommentList.size()) + "identifiers");
			System.out.println(" ");
			

			ws.writeText(newChangeCode, path + "\\corpus\\" + coreClass, "newcode");
			ws.writeText(oldChangeCode, path + "\\corpus\\" + coreClass, "oldcode");
			ws.writeText(CommentList, path + "\\corpus\\" + coreClass, "comment");

		}	
	}
}
