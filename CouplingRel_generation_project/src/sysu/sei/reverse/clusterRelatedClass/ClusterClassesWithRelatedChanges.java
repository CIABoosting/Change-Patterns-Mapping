package sysu.sei.reverse.clusterRelatedClass;

import java.util.List;

import ClassifyRCV1.RelatedChangesDetect;
import ClassifyRCV46DimenV3.ClassifyRCV46DimenV3;



/**
 * the output of VersionComparator is the RCVs(Related Changes Vector) represents 
 * the change relevance of any two updated classes.
 *  ClusterClassesWithRelatedChanges uses the RCVs to cluster the classes with related changes.
 * @author Troy
 *
 */

public class ClusterClassesWithRelatedChanges {

	public static void clusterRelatedClasses18Dimen(String path){
		RelatedChangesDetect pnn =null; //18άRCV		
		if(!path.equals(null) || path!=""){
			try {
				pnn = new RelatedChangesDetect();
				pnn.PNN4RelatedClass(1,path);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			System.out.println("input the correct file path");
		}
	}
	
	public static String[] clusterRelatedClasses46Dimen(String path){//46άRCV
		ClassifyRCV46DimenV3 pnn =null; 
		Object[] num = null;
		if(!path.equals(null) || path!=""){
			try{
				pnn = new ClassifyRCV46DimenV3();
				num = pnn.PNN4RelatedClass46Dimen(1,path);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else{
			System.out.println("input the correct file path");
		}
		String [] strs=new String[num.length];
		for(int i=0;i<num.length;i++){
		  strs[i]=num[i].toString();
		}
		String[] lables = null;
		for(int j = 0; j<strs.length;j++){
			System.out.println(strs[j]);
			String lable = strs[j];			
			lables = lable.split("\n");			
		}
		System.out.println("--------"+strs.length);
		for(int t = 0; t<lables.length;t++){			
			System.out.println(lables[t].trim());
		}
		System.out.print("--------"+lables.length);
		return lables;
	}
	
	public List<Object> clusterRelatedClasses(String path, String[] lables){
		
		return null;
	}
	
	
	public static void main(String[] args){
		//ClusterClassesWithRelatedChanges.clusterRelatedClasses18Dimen("D:/workspaces4Matlab/samplesdata2.txt");
		Object[] num = ClusterClassesWithRelatedChanges.clusterRelatedClasses46Dimen("D:/workspaces4Matlab/46DimenV2/TestSet_CouplingRelations46Dimen.txt");		
		String [] strs=new String[num.length];
		for(int i=0;i<num.length;i++){
		  strs[i]=num[i].toString();
		}
		String[] lables = null;
		for(int j = 0; j<strs.length;j++){
			System.out.print(strs[j].trim());
			String lable = strs[j];			
			lables = lable.split("\n");			
		}
		System.out.println("--------"+strs.length);
		for(int t = 0; t<lables.length;t++){			
			System.out.print(lables[t].trim());
		}
		System.out.print("--------"+lables.length);
	}
}
