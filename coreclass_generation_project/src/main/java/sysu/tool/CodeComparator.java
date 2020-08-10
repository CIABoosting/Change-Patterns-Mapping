package sysu.tool;

//import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.io.FileUtils;

import sysu.bean.Line;


public class CodeComparator {

	public static List<Line> compare(List<String> oldList, List<String> newList) {
		List<Line> codes = new ArrayList<Line>();
		int oldIndex = 1;
		int newIndex = 1;
		int currentIndex = 1;
		while (oldIndex <= oldList.size() && newIndex <= newList.size()) {
			String oldCode = oldList.get(oldIndex - 1);
			String newCode = newList.get(newIndex - 1);
			if (oldCode.equals(newCode)) {
				Line line = new Line();
				line.setLine(newCode);
				line.setLineNumber(currentIndex);
				line.setType("notChange");
				line.setOffset(currentIndex - newIndex);
				codes.add(line);

				oldIndex++;
				newIndex++;
				currentIndex++;
			} else {
				boolean isAddCode = false;
				for (int i = newIndex + 1; i <= newList.size(); i++) {
					if (oldCode.equals(newList.get(i - 1))) {
						isAddCode = true;
						for (int j = newIndex; j < i; j++) {
							Line line = new Line();
							line.setLine(newList.get(j - 1));
							line.setLineNumber(currentIndex);
							line.setOffset(currentIndex - newIndex);
							line.setType("add");
							codes.add(line);
							currentIndex++;
							newIndex++;
						}
						break;
					}
				}
				if (!isAddCode) {
					Line line = new Line();
					line.setLine(oldCode);
					line.setLineNumber(currentIndex);
					line.setOffset(currentIndex - oldIndex);
					line.setType("delete");
					codes.add(line);
					currentIndex++;
					oldIndex++;
				}

			}
		}

		for (int i = oldIndex; i <= oldList.size(); i++) {
			Line line = new Line();
			line.setLine(oldList.get(i - 1));
			line.setLineNumber(currentIndex);
			line.setOffset(currentIndex - oldIndex);
			line.setType("delete");
			codes.add(line);
			currentIndex++;
			oldIndex++;
		}

		for (int i = newIndex; i <= newList.size(); i++) {
			Line line = new Line();
			line.setLine(newList.get(i - 1));
			line.setLineNumber(currentIndex);
			line.setOffset(currentIndex - newIndex);
			line.setType("add");
			codes.add(line);
			currentIndex++;
			newIndex++;
		}

		return codes;
	}
	
	public static void main(String[] args) throws IOException {
	/*	List<String> list1 = FileUtils.readLines(new File("tempfile/AbstractFigure.java"),"UTF-8");
		List<String> list2 = FileUtils.readLines(new File("tempfile/AbstractFigure1.java"),"UTF-8");
		
		List<Line> codes = compare(list1, list2);
		for(Line line:codes) {
			System.out.println(line.getLineNumber()+" "+line.getType()+":"+line.getLine());
		}*/
	}

}