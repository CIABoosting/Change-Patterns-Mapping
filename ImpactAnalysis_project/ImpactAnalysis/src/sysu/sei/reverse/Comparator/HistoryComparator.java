package sysu.sei.reverse.Comparator;

import java.util.ArrayList;
import java.util.List;

import com.troy.ui.util.TemporarySpace;

import sysu.sei.reverse.designDecision.ClassChange;
import sysu.sei.reverse.designDecision.ClassRevisionHistory;
import sysu.sei.reverse.designDecision.VersionChange;
//
public class HistoryComparator {

	//历史变化信息
	public ArrayList<VersionChange> history;
	public String[] paths;
	public ArrayList<ClassRevisionHistory> classRevisionHistory;
	
	public ArrayList<ClassRevisionHistory> getClassRevisionHistory() {
		return classRevisionHistory;
	}

	public void setClassRevisionHistory(
			ArrayList<ClassRevisionHistory> classRevisionHistory) {
		this.classRevisionHistory = classRevisionHistory;
	}

	public void generateHistory(String[] paths){
		history = new ArrayList<VersionChange>();
		VersionComparator comparator = new VersionComparator();
		FileFilter filter = new FileFilter();
		if(paths.length >1){
		  filter.filteringAnnotation4folder(paths[0]);
			for(int i = 1; i < paths.length; i ++){
				filter.filteringAnnotation4folder(paths[i]);
				comparator.compareProject(paths[i-1],paths[i]); 
		//		comparator.getChange().writeLog("D:/a/log"+i);
				history.add(comparator.getChange());
			}
		}
		setHistory(history);
		

	}
	
	public void compareHistory(){		
		classRevisionHistory = new ArrayList<ClassRevisionHistory> ();
		boolean tag;
		for(int i = 0; i < history.size(); i ++){
			List<ClassChange> changeList = history.get(i).getChangedClassList();
			
			for(int j = 0; j < changeList.size(); j++){
				 tag = false;
				for(int p = 0; p < classRevisionHistory.size(); p++){
					if(classRevisionHistory.get(p).getClassa().getNewClass().getName().getFullyQualifiedName().equals(
							changeList.get(j).getOldClass().getName().getFullyQualifiedName())){
						classRevisionHistory.get(p).addRevisionTime();
						tag = true;
					}
				}
				if(tag == false){
					ClassRevisionHistory classRevision = new ClassRevisionHistory();
					classRevision.setClassa(changeList.get(j));
					classRevision.addRevisionTime();
					classRevisionHistory.add(classRevision);
				}
			}
		}
		
		for(int i =0; i < classRevisionHistory.size(); i++){
			System.out.println("Class "+ classRevisionHistory.get(i).getClassa().getNewClass().getName()+ " Revisioin Time :  "+ classRevisionHistory.get(i).getRevisionTime());
			TemporarySpace.setConsoleString("Class "+ classRevisionHistory.get(i).getClassa().getNewClass().getName()+ " Revisioin Time :  "+ classRevisionHistory.get(i).getRevisionTime());
		}
	}
	public ArrayList<VersionChange> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<VersionChange> history) {
		this.history = history;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 String[] pathList = {
			//	 "E:/reverse/case study/emf sourcecode 1.0.2",
			//	 "E:/reverse/case study/emf sourcecode 1.1.0",
			// "E:/reverse/case study/emf sourcecode 1.1.1",
			//	 "E:/reverse/case study/emf sourcecode 2.4.2",
				// "E:/reverse/case study/emf sourcecode 2.5.0",

				// "E:/reverse/case study/emf sourcecode 2.6.0",
			//	 "E:/reverse/case study/emf sourcecode 2.6.1",
				// "E:/reverse/case study/emf sourcecode 2.7.0",
				 
				// "H:/case study reverse/jfreechart-1.0.12/source/org/jfree/chart",
				// "H:/case study reverse/jfreechart-1.0.13/source/org/jfree/chart"
				// 	"D:/2013/workspace/case study reverse/jfreechart-0.5.6/source",
			//	 "D:/2013/workspace/case study reverse/jfreechart-0.6.0/source",			
			//	  "D:/2013/workspace/case study reverse/jfreechart-0.7.0/source",
			//		  "D:/2013/workspace/case study reverse/jfreechart-0.8.0/source",
			//		 "D:/2013/workspace/case study reverse/jfreechart-0.9.0/source",
					//	"D:/2013/workspace/case study reverse/jfreechart-1.0.0/source",
					//	"D:/2013/workspace/case study reverse/jfreechart-1.0.12/source",
					//	"D:/2013/workspace/case study reverse/jfreechart-1.0.13/source"
				//	"D:/2013/workspace/case study reverse/emf sourcecode 2.4.2",		
				// "D:/2013/workspace/case study reverse/emf sourcecode 2.5.0",
				//		"D:/2013/workspace/case study reverse/emf sourcecode 2.6.0"
				// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.1.0",
				/// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.2.0",
				// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.2.1",
				// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.2.2",
				// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.3.0",
				// "D:/workspace/SOURCE/edtFTPj20130913/edtftpj-2.4.0", 
				 
				//"E:/reverse/case study/emf sourcecode 2.8.0",
				//"E:/reverse/case study/emf sourcecode 2.9.0",
			//"E:/reverse/case study/argouml/ArgoUML-0.10.1-src/org/argouml",
//
		//	"E:/reverse/case study/argouml/ArgoUML-0.12-src/org/argouml",
			
			//"E:/coding/reverse/case study/argouml/ArgoUML-0.19.1-src",
		//	"E:/coding/reverse/case study/argouml/ArgoUML-0.19.2-src",
		//	"E:/coding/reverse/case study/argouml/ArgoUML-0.19.3-src",
		//	"E:/coding/reverse/case study/argouml/ArgoUML-0.19.4-src",
		//	"E:/coding/reverse/case study/argouml/ArgoUML-0.19.5-src",
	//		"E:/coding/reverse/case study/argouml/ArgoUML-0.19.6-src",
			
			//	"E:/reverse/case study/argouml/ArgoUML-0.28-src/argouml/src",

			//	"E:/reverse/case study/argouml/ArgoUML-0.30-src/argouml/src",

				//"E:/reverse/case study/argouml/ArgoUML-0.14-src",
				// "D:/reverse/case study/rel1_2_1/FreeTTS/src",

				// "D:/reverse/case study/rel1_2_2/src",
			//"D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-5.2.0/sources"	, 
			//"D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-5.3.0/src"	, 
			//"D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-5.4.0/src",	 
			//	 "D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-7.1.0/jhotdraw7/src/main/java",	 
			//	 "D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-7.2.0/Source/jhotdraw7/src/main/java",	 
			//	 "D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-7.3.0/Source/jhotdraw7/src/main/java"	, 
			//	 "D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-7.3.1/Source/jhotdraw7/src/main/java",	 
			//	 "D:/快盘/sharebox/546690311@qq.com/设计决策共享文件夹/案例分析/jhotdraw/源码/jhotdraw-7.4.1/Source/jhotdraw7/src/main/java"	, 
//"D:/case study/argouml/ArgoUML-0.19.1-src",
//"D:/case study/argouml/ArgoUML-0.19.2-src"				
//"D:/working/case study/emf/emf sourcecode 2.7.0",
//"D:/working/case study/emf/emf sourcecode 2.8.0",
//"D:/working/case study/edtftpj/edtftpj-2.1.0/edtftpj-2.1.0/src/com",

//"D:/working/case study/edtftpj/edtftpj-2.2.0/edtftpj-2.2.0/src/com",
//"E:/reverse/case study/emf sourcecode 2.5.0/org/eclipse/emf",
//"E:/reverse/case study/emf sourcecode 2.6.0/org/eclipse/emf",
//"E:/reverse/case study/emf sourcecode 2.7.0/org/eclipse/emf",
				 "D:/workspace/SOURCE/jdom source code/jdom-b4/src/java/org/jdom",
				 "D:/workspace/SOURCE/jdom source code/jdom-b5/src/java/org/jdom",
				 "D:/workspace/SOURCE/jdom source code/jdom-b6/src/java/org/jdom",
				 "D:/workspace/SOURCE/jdom source code/jdom-b7/src/java/org/jdom",
				 "D:/workspace/SOURCE/jdom source code/jdom-b8/src/java/org/jdom",
// "E:/recovery case study/jdom source code/jdom-b7/src",
//"E:/recovery case study/jdom source code/jdom-b8/src",


		 };
		 
		HistoryComparator c = new HistoryComparator();
		c.setPaths(pathList);
		c.generateHistory(c.paths);
		//System.out.println("test---------");
		for(int i = 0; i < c.history.size(); i ++){
			c.history.get(i).writeLog("D:/a/change"+i +"vs"+ (i+1)+"/"+"change.txt");
			c.history.get(i).generateLDAInput("D:/a/change"+i +"vs"+ (i+1)+"/"+"version("+i+"vs"+(i+1)+")"+".txt");
		}
		c.compareHistory();
	

	}

}
