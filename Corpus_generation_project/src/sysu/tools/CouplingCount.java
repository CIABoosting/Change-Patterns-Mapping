package sysu.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CouplingCount {
	
//	获取关键类名称
	public String getCoreClass(String path) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path + "\\coreclassName.txt"));
			String coreclass = reader.readLine();
			return coreclass;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static List<String> getCommitPath() {
		String DataPath = "E:\\ImpactAnalysis\\data";
		File f = new File(DataPath);
		File[] projectlist = f.listFiles();
		List<String> commitPath = new ArrayList<String>();
		for(File version:projectlist) {
			File f1 = new File(version.getPath());
			File[] commits = f1.listFiles();
			for(File commit: commits) {
				commitPath.add(commit.getPath());
			}
		}
		return commitPath;
	}
	
	public String getIndex(String line) {
		String[] str1 = line.split(":");
		String[] str2 = str1[0].split(" ");
		String index = str2[str2.length - 1];
		return index;
	}
	public boolean Contain(String line, String coreClass) {
		String[] str1 = line.split(":");
		if(str1.length > 1) {
			String[] str2 = str1[1].split("\\.");
			if(coreClass.equals(str2[str2.length - 1]))
				return true;
		}
		return false;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DecimalFormat df=new DecimalFormat("0.00");
		CouplingCount cc = new CouplingCount();
		List<String> commitPath = cc.getCommitPath();
		int commitNum = commitPath.size();
		int count0 = 0;
		int count3 = 0;
		int count5 = 0;
		int count7 = 0;
		int count10 = 0;
		int NoneCoreClass = 0;
		for(String Path:commitPath) {
//			Path = "E:\\ImpactAnalysis\\data\\aeron\\1000";
			String CoreClass = cc.getCoreClass(Path);
			if(CoreClass == null) {
				NoneCoreClass ++;
				continue;
			}
			File file = new File(Path + "\\old");
			if(!file.exists()) continue;
			int ClassCount = file.listFiles().length-1;
			int CoupleCount = 0;
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(Path + "\\CouplingReult.txt"));
				String temp = null;
				String coreindex = "999";
				while((temp = reader.readLine()) != null && CoreClass != null) {
					if(cc.Contain(temp, CoreClass)) {
						coreindex ="class "+ cc.getIndex(temp);
						continue;
					}
					if(temp.contains(coreindex)) {
						CoupleCount ++;
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(CoupleCount);
			System.out.println(df.format((float)CoupleCount / ClassCount));
			if(Float.parseFloat(df.format((float)CoupleCount / ClassCount)) == 0) {
				count0 ++;
			}
			else if(Float.parseFloat(df.format((float)CoupleCount / ClassCount)) == 1) {
				count10 ++;
			}
			else if (Float.parseFloat(df.format((float)CoupleCount / ClassCount)) >= 0.7) {
				count7++;
			}
			else if (Float.parseFloat(df.format((float)CoupleCount / ClassCount)) >= 0.5) {
				count5 ++;
			}
			else if (Float.parseFloat(df.format((float)CoupleCount / ClassCount)) >= 0.3) {
				count3++;
			}
		}
		System.out.println("NoneCoreClass " + df.format((float)NoneCoreClass / commitNum));
		System.out.println("0 " + df.format((float)(count0 -NoneCoreClass) / commitNum));
		System.out.println("30% " + df.format((float)count3 / commitNum));
		System.out.println("50% " +df.format((float)count5 / commitNum));
		System.out.println("70% " +df.format((float)count7 / commitNum));
		System.out.println("100% " +df.format((float)count10 / commitNum));
	}

}
