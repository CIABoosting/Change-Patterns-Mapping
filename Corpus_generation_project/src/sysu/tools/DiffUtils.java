package sysu.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.Role;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

public class DiffUtils {
	 //变化后代码片段
	 private List<String> newChangeCode = new ArrayList<>();
	 //变化前代码片段
	 private List<String> oldChangeCode = new ArrayList<>();
	 public List<String> getNewChangecode() {
		return newChangeCode;
	}

	 public void setNewChangecode(List<String> newChangeCode) {
		this.newChangeCode = newChangeCode;
	}

	 public List<String> getOldChangecode() {
		return oldChangeCode;
	}

	 public void setOldChangecode(List<String> oldChangeCode) {
		this.oldChangeCode = oldChangeCode;
	}	 
	 
	public DiffUtils(String oldPath, String newPath) {
		// TODO Auto-generated method stub
		List<String> oldList = null;
		List<String> newList = null;
		try {
			oldList = FileUtils.readLines(new File(oldPath), "UTF-8");
			newList = FileUtils.readLines(new File(newPath), "UTF-8");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true).inlineDiffByWord(true)
				.oldTag(f -> "").newTag(f -> "").build();
		List<DiffRow> rows = null;
		try {
			rows = generator.generateDiffRows(oldList, newList);
		} catch (DiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		for (DiffRow row : rows) {
			if(row.getTag().toString() == "INSERT") {
				if(row.getNewLine().contains("import")
						|| row.getNewLine().contains("/") 
						|| row.getNewLine().contains("*")) {
					continue;
				}
				
				if(!row.getNewLine().equals("") && row.getNewLine().length() != 0 && row.getNewLine() != null)
					newChangeCode.add(row.getNewLine());
			}
			if(row.getTag().toString().equals("DELETE")) {
				if(row.getOldLine().contains("import")
						|| row.getOldLine().contains("/") 
						|| row.getOldLine().contains("*")) {
					continue;
				}
				if(!row.getOldLine().equals("") && row.getOldLine().length() != 0 && row.getOldLine() != null)
					oldChangeCode.add(row.getOldLine());
			}
			if(row.getTag().toString().equals("CHANGE")) {
				if(row.getNewLine().contains("import")
						|| row.getNewLine().contains("/") 
						|| row.getNewLine().contains("*")) {
					continue;
				}
				if(row.getOldLine().contains("import")
						|| row.getOldLine().contains("/") 
						|| row.getOldLine().contains("*")) {
					continue;
				}
				if(!row.getOldLine().equals("") && !(row.getOldLine().length() == 0) && row.getOldLine() != null)
					oldChangeCode.add(row.getOldLine());
				if(!row.getNewLine().equals("") && !(row.getNewLine().length() == 0) && row.getNewLine() != null)
					newChangeCode.add(row.getNewLine());
			}
		}
//		for(String oldline : oldChangeCode)
//			System.out.println(oldline);
//		System.out.println("-----------------------------------------------------");
//		for(String newline:newChangeCode)
//			System.out.println(newline);

//		System.out.println(newChangeCode.size());

	}
	public static void main(String[] args) {
		String path1 = "E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2149\\old\\src\\Buffer.java";
		String path2 = "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit3.2.0source\\jEdit\\org\\gjt\\sp\\jedit\\View.java";
		
		DiffUtils df = new DiffUtils(path1, path2);
	}
}
