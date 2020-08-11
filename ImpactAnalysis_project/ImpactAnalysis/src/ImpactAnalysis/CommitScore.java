package ImpactAnalysis;

import java.io.File;
import java.util.List;
import java.util.Map;

import sysu.sei.reverse.Comparator.FileFilter;
import sysu.sei.reverse.Comparator.VersionComparator;

public class CommitScore {
	
	
	public static List<Integer> class2class(String path1, String path2) {
		VersionComparator comparator = new VersionComparator();
		File file1 = new File(path1);
		File file2 = new File(path2);
		FileFilter filter = new FileFilter();
		filter.filteringAnnotation4file(file1);
		filter.filteringAnnotation4file(file2);
		comparator.class2class(path1, path2);
		return comparator.getChange().getRelations();
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
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
////		String path1 = "E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2149\\old\\src\\Buffer.java";
////		String path2 = "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit3.2.0source\\jEdit\\org\\gjt\\sp\\jedit\\View.java";
//		List<String> commitPath = Score.CommitPath("E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2");
//		int count = 0;
//		for(String commit:commitPath) {
//			Map<String, List<Integer>> couplingSet =Evaluate.getCoupleRel(commit);
//			if(couplingSet.size()==0) {
//				System.out.println(commit);
//				count++;
//			}
//		}
//		System.out.println(count);
//	}

}
