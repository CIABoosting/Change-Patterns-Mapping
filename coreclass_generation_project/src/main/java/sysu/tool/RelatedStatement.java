package sysu.tool;

import java.util.ArrayList;
import java.util.List;

public class RelatedStatement {

	private int classId;
	private List<Integer> statementIdList = new ArrayList<Integer>();
	private List<Integer> methodIdList = new ArrayList<Integer>();

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public List<Integer> getStatementIdList() {
		return statementIdList;
	}

	public void setStatementIdList(List<Integer> statementIdList) {
		this.statementIdList = statementIdList;
	}

	public List<Integer> getMethodIdList() {
		return methodIdList;
	}

	public void setMethodIdList(List<Integer> methodIdList) {
		this.methodIdList = methodIdList;
	}

	public void addStatementId(int id) {
		statementIdList.add(id);
	}

	public void addMethodId(int id) {
		methodIdList.add(id);
	}

}
