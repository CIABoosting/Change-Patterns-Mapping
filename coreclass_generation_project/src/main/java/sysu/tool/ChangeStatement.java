package sysu.tool;

import java.util.ArrayList;
import java.util.List;

public class ChangeStatement {

	private List<String> statements;

	private int classId;

	private String classname;

	private int methodId;

	private boolean isChangeMethod;

	private String methodname;

	private List<RelatedStatement> relatedStatementList = new ArrayList<RelatedStatement>();

	private int statementId;

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	public boolean isChangeMethod() {
		return isChangeMethod;
	}

	public void setChangeMethod(boolean isChangeMethod) {
		this.isChangeMethod = isChangeMethod;
	}

	public String getMethodname() {
		return methodname;
	}

	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}

	public ChangeStatement() {
		statements = new ArrayList<String>();
	}

	public List<String> getStatements() {
		return statements;
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}

	public List<RelatedStatement> getRelatedStatementList() {
		return relatedStatementList;
	}

	public void setRelatedStatementList(List<RelatedStatement> relatedStatementList) {
		this.relatedStatementList = relatedStatementList;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	public void addStatement(String sta) {
		statements.add(sta);
	}

	public void addRelatedStatement(RelatedStatement rs) {
		relatedStatementList.add(rs);
	}

}
