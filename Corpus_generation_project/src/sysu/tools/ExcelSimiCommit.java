package sysu.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelSimiCommit {
	public static Map<String, List<String>> readSimilarity(String path) {
		DecimalFormat df = new DecimalFormat("0.0000");
		Map<String,List<String>> commit2simi = new LinkedHashMap<>();
		File f = new File(path + "\\result\\version2.2Similarity.txt");
		if(f.exists()) {
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(f));
				String temp = null;
				while((temp=reader.readLine())!=null) {
					List<String> simi = new ArrayList<>();
					String[] similarity = temp.split(",");
					String[] commit2simmilarity = similarity[0].split(":");
					String[] commit = commit2simmilarity[1].split("\\\\");
					String commitname = commit[commit.length-2] + "\\" + commit[commit.length-1];
					for(String singlesimi:similarity) {
						String[] str = singlesimi.split(":");
						simi.add(df.format(Double.parseDouble(str[str.length-1])));
					}
					commit2simi.put(commitname, simi);
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return commit2simi;
	}
	public static Map<String, List<String>> readSimiModify(String path) {
		File file = new File(path + "\\result\\version2.2Simimodify.txt");
		Map<String, List<String>> simiModify = new LinkedHashMap<>();
		if(file.exists()) {
			BufferedReader reader=null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String temp=null;
				while((temp=reader.readLine())!=null) {
					List<String> modifyClass = new ArrayList<>();
					String[] strings = temp.split(":");
					String[] commit = strings[1].split("\\\\");
					String commitname = commit[commit.length-2] + "\\" + commit[commit.length-1];
					if(strings.length>2) {
						String[] modifyclass = strings[strings.length-1].split(",");
						for(String classname:modifyclass) {
							if(!modifyClass.contains(classname)) {
								modifyClass.add(classname);
							}
							
						}
					}
					simiModify.put(commitname, modifyClass);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return simiModify;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String resultPath = "E:\\ImpactAnalysis\\实验\\实验分析\\hsqldb\\hsqldb2.3.2_相似提交分析2.2.xls";
		String commitPath = "E:\\ImpactAnalysis\\实验\\hsqldb\\commit\\hsqldb2.3.2";
		
		File file = new File(resultPath);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("sheet1");
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		style.setFont(font);
		font.setColor(IndexedColors.RED.getIndex());
		HSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue("commit");
		row0.createCell(1).setCellValue("前20相似提交");
		row0.createCell(2).setCellValue("相似度");
		row0.createCell(3).setCellValue("修改前代码相似度");
		row0.createCell(4).setCellValue("修改后代码相似度");
		row0.createCell(5).setCellValue("注释相似度");
		row0.createCell(6).setCellValue("受调整的类");
		row0.createCell(7).setCellValue("提交内包含的类");
		row0.createCell(8).setCellValue("有效调整的类");
		row0.createCell(9).setCellValue("结果是否提升");
		List<String> commitpath = writeExcel.CommitPath(commitPath);
		int row = 1;
		for(String commit:commitpath) {
			String path = commitPath + "\\" + commit;
			Map<String, List<String>>SimiModify = readSimiModify(path);
			Map<String, List<String>> commit2similarity = readSimilarity(path);
			List<String> allclass = writeExcel.getValidatSet(path);
			int start = row;
			List<String> simicommit = new ArrayList<>();
			for(String key:commit2similarity.keySet()) {
				simicommit.add(key);
			}
			if(simicommit.size()==0) continue;
			for(int i=simicommit.size()-1; i>0; i--) {
				String simicommitname = simicommit.get(i);
				List<String> Similarity = commit2similarity.get(simicommitname);
				List<String> modifiyclass = SimiModify.get(simicommitname);
				List<String> validmodify = new ArrayList<>();
				
				if(modifiyclass!=null) {
					for(String classname:modifiyclass) {
						if(allclass.contains(classname)) {
							validmodify.add(classname);
						}
					}
				}

				HSSFRow rows = sheet.createRow(row);
				rows.createCell(0).setCellValue(commit);
				rows.createCell(1).setCellValue(simicommitname);
				rows.createCell(2).setCellValue(Similarity.get(0));
				rows.createCell(3).setCellValue(Similarity.get(1));
				rows.createCell(4).setCellValue(Similarity.get(2));
				rows.createCell(5).setCellValue(Similarity.get(3));
				if(modifiyclass!=null) {
					rows.createCell(6).setCellValue(String.join("、", modifiyclass));
				}
				rows.createCell(7).setCellValue(String.join("、", allclass));
				
				if(validmodify!=null) {
					Cell cell8 = rows.createCell(8);
					cell8.setCellValue(String.join("、", validmodify));
					cell8.setCellStyle(style);
				}
				
				rows = sheet.createRow(++row);	
			}
//			HSSFRow rows = sheet.createRow(row);
			int end = row-1;
			sheet.addMergedRegion(new CellRangeAddress(start, end, 0,0));
			sheet.addMergedRegion(new CellRangeAddress(start,end, 7,7));

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
