package sysu.sei.reverse.designDecision;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

//用来记录不同版本之间的类的变化
//该类中的方法主要包含getter和setter，对于list类型的属性，包含adder函数，用于增加一个子元素
public class ClassChange {

	private int id;
	private ArrayList<Integer[]> relationship;
	
	
	
	//旧的类
	private TypeDeclaration oldClass;
	//新的类
	private TypeDeclaration newClass;
	//旧的类的package名字
	//由于AST解析的时候，是一个文件里面包含一个package的声明，但是可能包含多个类，所以类的描述中 没有包括package
	//可能可以从类定义的parent元素获得，但是目前没有使用
	private String oldPackageName;
	// 新的类的package
	private String newPackageName;
	//旧的类对应的import文件的列表，理由和package类似
	//这个import信息会用于判断两个类之间是否存在依赖关系，这种判断可能会有错误，因为一个文件中可能包含多个类，而import中描述的是多个类对应的import文件
	//由于一般一个类存储在一个单独的文件中，所以目前还没处理由此造成的一些错误
	private List<ImportDeclaration> oldImportList;
	//新的类对应的import文件的列表
	private List<ImportDeclaration> newImportList;
	
	private List a;
	

	//新增的方法定义
	private List<MethodDeclaration> addedMethods;
	//方法修改记录
	private List<MethodChange> changedMethods;
	//删除掉的方法定义
	private List<MethodDeclaration> deletedMethods;
	//新增的属性定义
	private List<FieldDeclaration> addedFields;
	//删除掉的属性定义

	private List<FieldDeclaration> deletedFields;
	public ClassChange(){
		
	}
	//初始化函数
	public ClassChange(TypeDeclaration oldClass, TypeDeclaration newClass,String oldPackageName, String newPackageName,  List<ImportDeclaration> oldImportList, List<ImportDeclaration> newImportList) {
		this.oldPackageName = oldPackageName;
		this.newPackageName = newPackageName;
		this.oldImportList = oldImportList;
		this.newImportList = newImportList;
		
		this.oldClass = oldClass;
		this.newClass = newClass;

	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList <Integer[]> getRelationship() {
		
		return relationship;
	}
	public void setRelationship(ArrayList<Integer[]> relationship) {
		this.relationship = relationship;
	}


	public boolean addRevisionRelationship(int classId,int type){
		boolean tag = false;
	//	System.out.println("testing");
		if(relationship != null && relationship.size()> classId){
	//	System.out.println("testing  added");
			tag = true;
			if(relationship.get(classId)== null){
				relationship.set(classId, new Integer[100]);
			}
			if(relationship.get(classId)[type] == null){
				relationship.get(classId)[type] = new Integer(0);
			}
			relationship.get(classId)[type]++;
			
		}
		
		
		return tag;
	}
	
	public boolean deleteRevisionRelationship(int classId,int type){
		boolean tag = false;
	//	System.out.println("testing");
		if(relationship != null && relationship.size()> classId){
	//	System.out.println("testing  added");
			tag = true;
			if(relationship.get(classId)== null){
				relationship.set(classId, new Integer[200]);
			}
			if(relationship.get(classId)[type] == null){
				relationship.get(classId)[type] = new Integer(0);
			}
			relationship.get(classId)[type]--;
			
		}
		
		
		return tag;
	}
	public boolean ifRelated(int classId){
		boolean tag = false;
		if(relationship != null && relationship.size()>= classId){		
			if(relationship.get(classId)!= null){
				for(int i = 0; i < 18; i++){
					if(relationship.get(classId)[i] != null && relationship.get(classId)[i].intValue() !=0){
						tag = true;
					}
				}
			}
		}
		return tag;
	}

	public void initializeRelationship(int classId){
		
		if(relationship != null && relationship.size()>= classId){		
			if(classId < relationship.size()&& relationship.get(classId)!= null){
				for(int i = 0; i < 18; i++){
					relationship.get(classId)[i] = new Integer(0);
						
					}
				
			}
		}
		}
	public void initRelationship(int size){
		if(relationship == null){
			relationship = new ArrayList<Integer[]>();
			for(int i = 0; i < size;i++){
				relationship.set(i, new Integer[100]);
			}
		}
	}
	public List<ImportDeclaration> getOldImportList() {
		return oldImportList;
	}


	public void setOldImportList(List<ImportDeclaration> oldImportList) {
		this.oldImportList = oldImportList;
	}


	public List<ImportDeclaration> getNewImportList() {
		return newImportList;
	}


	public void setNewImportList(List<ImportDeclaration> newImportList) {
		this.newImportList = newImportList;
	}


	public String getOldPackageName() {
		return oldPackageName;
	}


	public void setOldPackageName(String oldPackageName) {
		this.oldPackageName = oldPackageName;
	}


	public String getNewPackageName() {
		return newPackageName;
	}


	public void setNewPackageName(String newPackageName) {
		this.newPackageName = newPackageName;
	}


	public TypeDeclaration getOldClass() {
		return oldClass;
	}


	public void setOldClass(TypeDeclaration oldClass) {
		this.oldClass = oldClass;
	}


	public TypeDeclaration getNewClass() {
		return newClass;
	}


	public void setNewClass(TypeDeclaration newClass) {
		this.newClass = newClass;
	}


	public List<MethodDeclaration> getAddedMethods() {
		if(addedMethods == null){
			addedMethods = new ArrayList<MethodDeclaration>();
		}
		return addedMethods;
	}

	
	public void setAddedMethods(List<MethodDeclaration> addedMethods) {
		
		this.addedMethods = addedMethods;
	}

	public void addAddedMethod(MethodDeclaration method) {
		
		this.getAddedMethods().add(method);
	}

	public List<MethodChange> getChangedMethods() {
		if(changedMethods == null){
			changedMethods = new ArrayList<MethodChange>();
		}
		return changedMethods;
	}


	public void setChangedMethods(List<MethodChange> changedMethods) {
		this.changedMethods = changedMethods;
	}


	public void addChangedMethod(MethodChange method) {
		
		this.getChangedMethods().add(method);
	}
	
	public List<MethodDeclaration> getDeletedMethods() {
		if(deletedMethods == null){
			deletedMethods = new ArrayList<MethodDeclaration>();
		}
		return deletedMethods;
	}


	public void setDeletedMethods(List<MethodDeclaration> deletedMethods) {
		
		this.deletedMethods = deletedMethods;
	}

	public void addDeletedMethod(MethodDeclaration method) {
		
		this.getDeletedMethods().add(method);
	}

	public List<FieldDeclaration> getAddedFields() {
		if(addedFields == null){
			addedFields = new ArrayList<FieldDeclaration>();
		}
		return addedFields;
	}


	public void setAddedFields(List<FieldDeclaration> addedFields) {
		
		this.addedFields = addedFields;
	}

	public void addAddedField(FieldDeclaration filed) {
		
		this.getAddedFields().add(filed);
	}

	


	public List<FieldDeclaration> getDeletedFields() {
		if(deletedFields == null){
			deletedFields = new ArrayList<FieldDeclaration>();
		}
		return deletedFields;
	}


	public void setDeletedFields(List<FieldDeclaration> deletedFields) {
		
		this.deletedFields = deletedFields;
	}


	public void addDeletedField(FieldDeclaration filed) {
		
		this.getDeletedFields().add(filed);
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
	}

}
