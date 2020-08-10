package sysu.bean;

import java.util.List;


public class MethodBean {
	

	private String id;

	private int classID;

	private String className;

	private int methodID;

	private String methodName;
	

	private String returnType;
	

	private List<String> parameters;

	private List<String> callMethodList;
	

	private String methodType;
	

	private String comment;
	

	private List<StatementBean> statementList;
	

	private int innerCount=0;

	private int outterCount=0;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}


	public int getMethodID() {
		return methodID;
	}

	public void setMethodID(int methodID) {
		this.methodID = methodID;
	}

	public int getClassID() {
		return classID;
	}

	public void setClassID(int classID) {
		this.classID = classID;
	}


	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public List<String> getCallMethodList() {
		return callMethodList;
	}

	public void setCallMethodList(List<String> callMethodList) {
		this.callMethodList = callMethodList;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<StatementBean> getStatementList() {
		return statementList;
	}

	public void setStatementList(List<StatementBean> statementList) {
		this.statementList = statementList;
	}

	public int getInnerCount() {
		return innerCount;
	}

	public void setInnerCount(int innerCount) {
		this.innerCount = innerCount;
	}

	public int getOutterCount() {
		return outterCount;
	}

	public void setOutterCount(int outterCount) {
		this.outterCount = outterCount;
	}

	

}
