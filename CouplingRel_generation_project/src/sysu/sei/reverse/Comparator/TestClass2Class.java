package sysu.sei.reverse.Comparator;
import java.io.File;

import sysu.sei.reverse.designDecision.*;

public class TestClass2Class {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VersionComparator comparator = new VersionComparator();
		String path1 = "E:\\ImpactAnalysis\\data\\aeron\\383\\old\\Receiver.java";
		String path2 = "E:\\ImpactAnalysis\\data\\aeron\\383\\old\\MinMulticastFlowControl.java";
		File file1 = new File(path1);
		File file2 = new File(path2);
		FileFilter filter = new FileFilter();
		filter.filteringAnnotation4file(file1);
		filter.filteringAnnotation4file(file2);
		comparator.class2class(path1, path2);
		System.out.println(comparator.getChange().getRelations().size());
	}

}
