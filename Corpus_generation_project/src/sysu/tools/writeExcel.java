package sysu.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class writeExcel {
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
	public static List<String> CommitPath(String project) {
		File file = new File(project);
		File[] files = file.listFiles();
		List<String> commitpath = new ArrayList<String>();
		for(File f:files) {
//			File file2 = new File(f.getAbsolutePath());
//			File[] files2 = file2.listFiles();
//			for(File f2:files2) {
//				commitpath.add(f2.getAbsolutePath());
//			}
			commitpath.add(f.getName());
		}
		return commitpath;
	}
	public static String getRelation(String classname, String path) {
		Map<String, List<Integer>> class2couple = getCoupleRel(path);

		List<Integer> relation = class2couple.get(classname);
		if (relation!=null) {
			return relation.toString();
		}else {
			return "none";
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
				impactSet.add(temp);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return impactSet;
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
	public static List<String> getValidatSet(String path) {
		List<String> validatSet = new ArrayList<>();
		File file = new File(path+"\\old\\src");
		File[] files = file.listFiles();
		for(File f:files) {
			if(f.getName().endsWith(".java")) {
				validatSet.add(f.getName().split("\\.")[0]);
			}
		}
		return validatSet;
	}
	public static int getIndex(String classname, List<String> impactset) {

		int index = 1;
		for(String impactclass:impactset) {
			if (impactclass.equals(classname)) {
				return index;
			}
			index++;
		}
		return 0;
	}
	public static boolean equal(Double num1, Double num2) {
		if((num1 - num2 )> -0.000001 && (num1-num2)<0.000001) {
			return true;
		}else {
			return false;
		}
	}
	public static String compareResult(List<String> impactset, List<String> J_impactset, List<String> validatset) {
		if (RecallScore(impactset, validatset, 5) > RecallScore(J_impactset, validatset, 5) 
				|| RecallScore(impactset, validatset, 10) > RecallScore(J_impactset, validatset, 10)) {
			return "yes";
		}
		if (PrecisionScore(impactset, validatset, 5) > PrecisionScore(J_impactset, validatset, 5)
				|| PrecisionScore(impactset, validatset, 10) > PrecisionScore(J_impactset, validatset, 10)) {
			return "yes";
		}
		if (equal(RecallScore(impactset, validatset, 5),RecallScore(J_impactset, validatset, 5))
				&& equal(RecallScore(impactset, validatset, 10),RecallScore(J_impactset, validatset, 10))
				&&equal(PrecisionScore(impactset, validatset, 5),PrecisionScore(J_impactset, validatset, 5))
				&&equal(PrecisionScore(impactset, validatset, 10), PrecisionScore(J_impactset, validatset, 10))) {	
			return "equ";
		}
		return "no";
	}
	//精确率
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
	//召回率
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String resultPath = "E:\\ImpactAnalysis\\实验\\实验分析\\jedit\\jedit3.2_正向.xls";
		String commitPath = "E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2";
		String version = "version2.2.txt";
		
		File file = new File(resultPath);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");
		HSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue("commit");
		row0.createCell(1).setCellValue("coreclass");
		row0.createCell(2).setCellValue("class");
		row0.createCell(3).setCellValue("Jripples");
		row0.createCell(4).setCellValue("our method");
		row0.createCell(5).setCellValue("couplingRelation");
		row0.createCell(6).setCellValue("improve");
		List<String> commitpath = CommitPath(commitPath);
		int row = 1;
		for(String commit:commitpath) {
			String path = commitPath + "\\" + commit;
			System.out.println(path);
			String coreclass = getCoreClass(path);
			if(coreclass==null) continue;
			List<String> allclass = getValidatSet(path);
			List<String> J_impactset = getImpactSet(path + "\\JripplesResult\\" + coreclass +".txt");
			List<String> impactset = getImpactSet(path + "\\Result\\" +"version2.2.txt");
			if(J_impactset.size()==0 || impactset.size()==0) continue;
			if(compareResult(impactset, J_impactset, allclass)!="yes") continue;
			HSSFRow rows = sheet.createRow(row);
			int start = row;
			rows.createCell(0).setCellValue(commit);
			rows.createCell(1).setCellValue(coreclass);
			rows.createCell(6).setCellValue(compareResult(impactset, J_impactset, allclass));
			for(String classname:allclass) {
				int jripple = getIndex(classname, J_impactset);
				int ourmethod = getIndex(classname, impactset);
				rows.createCell(2).setCellValue(classname);
				rows.createCell(3).setCellValue(jripple);
				rows.createCell(4).setCellValue(ourmethod);
				rows.createCell(5).setCellValue(getRelation(classname, path));
				
				rows = sheet.createRow(++row);
			}
			int end = row -1;
			sheet.addMergedRegion(new CellRangeAddress(start, end, 0,0));
			sheet.addMergedRegion(new CellRangeAddress(start, end, 1,1));
			sheet.addMergedRegion(new CellRangeAddress(start,end, 6,6));
		}
		
		FileOutputStream xlsStream;
		try {
			xlsStream = new FileOutputStream(file);
			workbook.write(xlsStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}