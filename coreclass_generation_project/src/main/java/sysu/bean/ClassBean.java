package sysu.bean;

import java.util.ArrayList;
import java.util.List;

import sysu.bean.Line;



public class ClassBean {
	

	private String id;

	private int classID;
	
	private String className;
	
	private String parentName;
	
	private List<String> interfaceList;
	
	private String classType;
	
	private String comment;
	
	private int newCodeLines;
	
	private int oldCodeLines;
	
	private int newMethodNum;
	
	private int oldMethodNum;
	
	private int innerCount=0;
	
	private int outterCount=0;
	
	private boolean isCore=false;
	
	private int changeMethodNum;
	
	private int addMethodNum;
	
	private int deleteMethodNum;
	
	private List<Integer> addStatementNodeTypeList;
	
	private List<Integer> changeStatementNodeTypeList;
	
	private List<Integer> deleteStatementNodeTypeList;
	
	private List<String> useClassList=new ArrayList<String>();
	
	private List<MethodInvoke> methodInvokeList = new ArrayList<MethodInvoke>();
	
	private int classIndex;
	
	private List<Line> codes;
	
	private double coreProbability;
	
	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public List<MethodInvoke> getMethodInvokeList() {
		return methodInvokeList;
	}

	public void setMethodInvokeList(List<MethodInvoke> methodInvokeList) {
		this.methodInvokeList = methodInvokeList;
	}

	public int getChangeMethodNum() {
		return changeMethodNum;
	}

	public void setChangeMethodNum(int changeMethodNum) {
		this.changeMethodNum = changeMethodNum;
	}

	public int getAddMethodNum() {
		return addMethodNum;
	}

	public void setAddMethodNum(int addMethodNum) {
		this.addMethodNum = addMethodNum;
	}

	public int getDeleteMethodNum() {
		return deleteMethodNum;
	}

	public List<Integer> getAddStatementNodeTypeList() {
		return addStatementNodeTypeList;
	}

	public void setAddStatementNodeTypeList(List<Integer> addStatementNodeTypeList) {
		this.addStatementNodeTypeList = addStatementNodeTypeList;
	}

	public List<Integer> getChangeStatementNodeTypeList() {
		return changeStatementNodeTypeList;
	}

	public void setChangeStatementNodeTypeList(List<Integer> changeStatementNodeTypeList) {
		this.changeStatementNodeTypeList = changeStatementNodeTypeList;
	}

	public List<Integer> getDeleteStatementNodeTypeList() {
		return deleteStatementNodeTypeList;
	}

	public void setDeleteStatementNodeTypeList(List<Integer> deleteStatementNodeTypeList) {
		this.deleteStatementNodeTypeList = deleteStatementNodeTypeList;
	}

	public void setDeleteMethodNum(int deleteMethodNum) {
		this.deleteMethodNum = deleteMethodNum;
	}
	public List<String> getUseClassList() {
		return useClassList;
	}

	public void setUseClassList(List<String> useClassList) {
		this.useClassList = useClassList;
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

	public boolean isCore() {
		return isCore;
	}

	public void setCore(boolean isCore) {
		this.isCore = isCore;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getClassID() {
		return classID;
	}

	public void setClassID(int classID) {
		this.classID = classID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<String> getInterfaceList() {
		return interfaceList;
	}

	public void setInterfaceList(List<String> interfaceList) {
		this.interfaceList = interfaceList;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getCoreProbability() {
		return coreProbability;
	}

	public void setCoreProbability(double coreProbability) {
		this.coreProbability = coreProbability;
	}

	public int getNewCodeLines() {
		return newCodeLines;
	}

	public void setNewCodeLines(int newCodeLines) {
		this.newCodeLines = newCodeLines;
	}

	public int getOldCodeLines() {
		return oldCodeLines;
	}

	public void setOldCodeLines(int oldCodeLines) {
		this.oldCodeLines = oldCodeLines;
	}

	public int getNewMethodNum() {
		return newMethodNum;
	}

	public void setNewMethodNum(int newMethodNum) {
		this.newMethodNum = newMethodNum;
	}

	public int getOldMethodNum() {
		return oldMethodNum;
	}

	public void setOldMethodNum(int oldMethodNum) {
		this.oldMethodNum = oldMethodNum;
	}

	public List<Line> getCodes() {
		return codes;
	}

	public void setCodes(List<Line> codes) {
		this.codes = codes;
	}	
	

}