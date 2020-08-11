package sysu.sei.reverse.designDecision;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;

//import sysu.sei.reverse.clusterRelatedClass.ClusterClassesWithRelatedChanges;

import com.troy.ui.util.TemporarySpace;


//该类用于记录不同版本之间的变化信息

public class VersionChange {
	//用于记录
	private int version= 0;
	//用于记录两个版本之间新增的类定义列表
	private List<ClassChange> addedClassList ;
	//用于记录两个版本之间修改的类定义列表
	private List<ClassChange> changedClassList ;
	//用于记录两个版本之间删除的类定义列表
	private List<TypeDeclaration> deletedClassList ;
	//用于记录两个版本之间的设计决策列表
	private List<DesignDecision> designDecisionList;
	//用于记录两个版本之间的设计决策推理结果
	private List<DesignDecision> deducedDesignDecisionList;
	
	private List<ClassChange> acClassList;

	public List<ClassChange> getAcClassList() {
		
			acClassList = new ArrayList<ClassChange>();
		
			if(this.getAddedClassList().size() >0 || this.getChangedClassList().size() >0){
				for(int i = 0; i < this.getAddedClassList().size(); i++){
					acClassList.add(this.getAddedClassList().get(i));
				}
				for(int i = 0; i < this.getChangedClassList().size(); i++){
					acClassList.add(this.getChangedClassList().get(i));
				}	
			}
		
		return acClassList;
	}

	public void setAcClassList(List<ClassChange> acClassList) {
		this.acClassList = acClassList;
	}

	public Integer[] getCochangeRecord(){
		List<ClassChange> relatedClasses = this.getAcClassList();
		Integer[] cochangeRecord= new Integer[100];
		for(int i= 0 ; i < 100; i++){
			cochangeRecord[i]= new Integer(0);
			for(int j = 0; j < relatedClasses.size(); j++){
				for(int k = 0; k < relatedClasses.get(j).getRelationship().size(); k++){
				//	System.out.println("gggggggg"+i+j+k+relatedClasses.get(j).getRelationship().get(k));
				cochangeRecord[i]= cochangeRecord[i]+ relatedClasses.get(j).getRelationship().get(k)[i];
				}
			}
		}
		return cochangeRecord;
	}
	public int getVersion() {
		return version;
		
	}

	public void setVersion(int version) {
		this.version = version;
	}
	public List<ClassChange> getAddedClassList() {
		if(addedClassList == null){
			addedClassList = new ArrayList<ClassChange>();
		}
		return addedClassList;
	}

	public void setAddedClassList(List<ClassChange> addedClassList) {
		this.addedClassList = addedClassList;
	}

	public List<ClassChange> getChangedClassList() {
		if(changedClassList == null){
			changedClassList = new ArrayList<ClassChange>();
		}
		return changedClassList;
	}

	public void setChangedClassList(List<ClassChange> changedClassList) {
		this.changedClassList = changedClassList;
	}

	public List<TypeDeclaration> getDeletedClassList() {
		if(deletedClassList == null){
			deletedClassList = new ArrayList<TypeDeclaration>();
		}
		return deletedClassList;
	}

	public void setDeletedClassList(List<TypeDeclaration> deletedClassList) {
		this.deletedClassList = deletedClassList;
	}


	public void setDesignDecisionList(List<DesignDecision> designDecisionList) {
		this.designDecisionList = designDecisionList;
	}
	
	public void addClassChange2(TypeDeclaration oldClass, TypeDeclaration newClass, String oldPackageName, String newPackageName,List<ImportDeclaration> oldImportList, List<ImportDeclaration> newImportList){
		ClassChange change = new ClassChange(oldClass, newClass, oldPackageName, newPackageName, oldImportList, newImportList);
		this.getChangedClassList().add(change);
		
	}
	
	public void addAddedClass(TypeDeclaration newClass, String newPackageName, List<ImportDeclaration> newImportList){
		ClassChange newChange = new ClassChange(null, newClass, null, newPackageName, null, newImportList);
		this.getAddedClassList().add(newChange);
	}
	
	public void addDeletedClass(TypeDeclaration deletedClass){
		this.getDeletedClassList().add(deletedClass);
	}

	public void addClassChange(ClassChange classChange) {
		this.getChangedClassList().add(classChange);
		
	}
	
	
	//根据类之间的关系，聚类版本变化所涉及的元素，得到设计决策
	public List<DesignDecision> getDesignDecisionList() {
		
		designDecisionList = new ArrayList<DesignDecision>();
		List<ClassChange> relatedClasses = this.getAcClassList();
		if(relatedClasses.size() >0 ){
			
			//计算修改模式
			for(int i = 0; i < this.getAddedClassList().size(); i++){
				this.getAddedClassList().get(i).setId(i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					relationship.add(new Integer[100]);
				}
				this.getAddedClassList().get(i).setRelationship(relationship);
			}
			for(int i = 0; i < this.getChangedClassList().size(); i++){
				this.getChangedClassList().get(i).setId(this.getAddedClassList().size()+i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					relationship.add(new Integer[100]);
				}
				this.getChangedClassList().get(i).setRelationship(relationship);
			}
			//对于每一类，找到其相关的类列表，形成一个designdecision，然后删除这个decision涉及的所有类
			for(int i = 0; i < relatedClasses.size(); i++){
					//计算修改模式
								
				ClassChange newClassChange = relatedClasses.get(i);
				if(relatedClasses.get(i).getId()< this.getAddedClassList().size())
					newClassChange = this.getAddedClassList().get(relatedClasses.get(i).getId());
				else{
					newClassChange = this.getChangedClassList().get(relatedClasses.get(i).getId()-this.getAddedClassList().size());
				}
				DesignDecision decision = new DesignDecision();
				List<ClassChange> relatedClass4new = findRelatedClass(newClassChange);
			//	System.out.println(newClassChange.getNewClass().getName()+ "'s "+relatedClass4new.size()+" related classes: ");
				if(relatedClass4new.size() != 0){
			
					decision.addInvolvingClasses(relatedClass4new, this.getVersion());
					for(int j = 0; j <relatedClass4new.size(); j++ ){
						boolean tag  = false;
						for(int k=i+1; k < relatedClasses.size(); k++ ){
						//	System.out.println("test class "+relatedClasses.get(k).getNewPackageName()+"."+relatedClasses.get(k).getNewClass().getName());
							
							if(relatedClasses.get(k).getNewClass().getName().getFullyQualifiedName().equals(relatedClass4new.get(j).getNewClass().getName().getFullyQualifiedName())
								&&((relatedClasses.get(k).getNewPackageName()== null &&relatedClass4new.get(j).getNewPackageName() == null)||
								(relatedClasses.get(k).getNewPackageName()!= null &&relatedClass4new.get(j).getNewPackageName() != null&&relatedClasses.get(k).getNewPackageName().equals(relatedClass4new.get(j).getNewPackageName())))){
						//		System.out.println("find class "+relatedClasses.get(k).getNewPackageName()+"."+relatedClasses.get(k).getNewClass().getName());
								tag = true;
								relatedClasses.remove(k);
								//i = i-1;
								k = relatedClasses.size();
							}
						}
			//		if(tag == false) System.out.println("do not find class "+relatedClass4new.get(j).getNewPackageName()+"."+ relatedClass4new.get(j).getNewClass().getName());
					}
			
				}
				//System.out.println("adding decision "+ designDecisionList.size()+" --related class size "+decision.getInvolvingClassList().size()+" --existing class "+ relatedClasses.size());
			//	System.out.println();
				designDecisionList.add(decision);
		
			}
		}
		
		return designDecisionList;
	}

	
public void compareChangType() {
		
		
		List<ClassChange> relatedClasses = this.getAcClassList();
		if(relatedClasses.size() >0 ){
			
			//计算修改模式
			for(int i = 0; i < this.getAddedClassList().size(); i++){
				this.getAddedClassList().get(i).setId(i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					Integer[] r = new Integer[100];
					for(int k=0; k < 100; k++){
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getAddedClassList().get(i).setRelationship(relationship);
			}
			for(int i = 0; i < this.getChangedClassList().size(); i++){
				this.getChangedClassList().get(i).setId(this.getAddedClassList().size()+i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					Integer[] r = new Integer[100];
					for(int k=0; k < 100; k++){
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getChangedClassList().get(i).setRelationship(relationship);
			}
			
			//循环对比两个classchange，记录他们的关系
			for(int i = 0; i < relatedClasses.size(); i++){
				for(int j =0; j < relatedClasses.size(); j++){
					if(i != j){
						ifChangeRelated(relatedClasses.get(i), relatedClasses.get(j));
					}
				}	
			}
		}					
				
	}

public void compareChangType4Direct60() {
	
	
	List<ClassChange> relatedClasses = this.getAcClassList();
	if(relatedClasses.size() >0 ){
		
		//计算修改模式
		for(int i = 0; i < this.getAddedClassList().size(); i++){
			this.getAddedClassList().get(i).setId(i);
			ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
			for(int j =0; j < relatedClasses.size(); j++){
				Integer[] r = new Integer[100];
				for(int k=0; k < 100; k++){
					r[k] = new Integer(0);
				}
				relationship.add(r);
			}
			this.getAddedClassList().get(i).setRelationship(relationship);
		}
		for(int i = 0; i < this.getChangedClassList().size(); i++){
			this.getChangedClassList().get(i).setId(this.getAddedClassList().size()+i);
			ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
			for(int j =0; j < relatedClasses.size(); j++){
				Integer[] r = new Integer[100];
				for(int k=0; k < 100; k++){
					r[k] = new Integer(0);
				}
				relationship.add(r);
			}
			this.getChangedClassList().get(i).setRelationship(relationship);
		}
		
		//循环对比两个classchange，记录他们的关系
		for(int i = 0; i < this.getAddedClassList().size(); i++){
			for(int j =0; j < this.getAddedClassList().size(); j++){
				if(i != j){
					this.compareRelationship60(this.getAddedClassList().get(i), this.getAddedClassList().get(j),false,false);
				}
			}	
			for(int j =0; j < this.getChangedClassList().size(); j++){
				if(i != j){
					this.compareRelationship60(this.getAddedClassList().get(i), this.getChangedClassList().get(j),false,true);
					
				}
			}	
		}
		for(int i = 0; i < this.getChangedClassList().size(); i++){
			for(int j =0; j < this.getAddedClassList().size(); j++){
				if(i != j){
					this.compareRelationship60(this.getChangedClassList().get(i), this.getAddedClassList().get(j),true,false);
				}
			}	
			for(int j =0; j < this.getChangedClassList().size(); j++){
				if(i != j){
					this.compareRelationship60(this.getChangedClassList().get(i), this.getChangedClassList().get(j),true,true);
					
				}
			}	
		}
	}					
			
}
public void compareChangType4Direct() {
		
		
		List<ClassChange> relatedClasses = this.getAcClassList();
		if(relatedClasses.size() >0 ){
			
			//计算修改模式
			for(int i = 0; i < this.getAddedClassList().size(); i++){
				this.getAddedClassList().get(i).setId(i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					Integer[] r = new Integer[100];
					for(int k=0; k < 100; k++){
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getAddedClassList().get(i).setRelationship(relationship);
			}
			for(int i = 0; i < this.getChangedClassList().size(); i++){
				this.getChangedClassList().get(i).setId(this.getAddedClassList().size()+i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for(int j =0; j < relatedClasses.size(); j++){
					Integer[] r = new Integer[100];
					for(int k=0; k < 100; k++){
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getChangedClassList().get(i).setRelationship(relationship);
			}
			
			//循环对比两个classchange，记录他们的关系
			for(int i = 0; i < this.getAddedClassList().size(); i++){
				for(int j =0; j < this.getAddedClassList().size(); j++){
					if(i != j){
						this.compareRelationship(this.getAddedClassList().get(i), this.getAddedClassList().get(j),false,false);
					}
				}	
				for(int j =0; j < this.getChangedClassList().size(); j++){
					if(i != j){
						this.compareRelationship(this.getAddedClassList().get(i), this.getChangedClassList().get(j),false,true);
						
					}
				}	
			}
			for(int i = 0; i < this.getChangedClassList().size(); i++){
				for(int j =0; j < this.getAddedClassList().size(); j++){
					if(i != j){
						this.compareRelationship(this.getChangedClassList().get(i), this.getAddedClassList().get(j),true,false);
					}
				}	
				for(int j =0; j < this.getChangedClassList().size(); j++){
					if(i != j){
						this.compareRelationship(this.getChangedClassList().get(i), this.getChangedClassList().get(j),true,true);
						
					}
				}	
			}
		}					
				
	}
			
	// 在VersionChange里面查找与类c有联系的其他类
	public List<ClassChange> findRelatedClass(ClassChange c){

		List<ClassChange> relatedClasses = new ArrayList<ClassChange>();
		List<ClassChange> tempDirectRelatedClasses ;
		relatedClasses.add(c);
		if(this.getAddedClassList().size() >0 || this.getChangedClassList().size() >0){
			//将相关的类加入到relatedClasses里面，形成一个设计决策
			for(int i = 0; i < relatedClasses.size(); i++){
				tempDirectRelatedClasses = findDirectRelatedClass(relatedClasses.get(i));
				//if(c.getNewClass().getName().getFullyQualifiedName().endsWith("UMLCommentBodyDocument")){
					//System.out.println(relatedClasses.get(i).getNewClass().getName()+" "+tempDirectRelatedClasses.size());
				//}
				//将不重复的类添加进去	
				for(int j = 0; j < tempDirectRelatedClasses.size(); j++){
					
						boolean tag = false;
					
						for(int k =0; k < relatedClasses.size(); k++){
						
							if((relatedClasses.get(k).getNewClass().getName().getFullyQualifiedName().equals(tempDirectRelatedClasses.get(j).getNewClass().getName().getFullyQualifiedName()))&&
									relatedClasses.get(k).getNewPackageName().equals(tempDirectRelatedClasses.get(j).getNewPackageName())	){
								tag = true;
					
							}
					
						}
					if(tag == false){
						relatedClasses.add(tempDirectRelatedClasses.get(j));
					//	System.out.println();
					//	System.out.println("------"+tempDirectRelatedClasses.get(j).getNewClass().getName());
					}
				}
				}
		
		}
		return relatedClasses;
	
	}

	// 在VersionChange里面查找与类c有联系的其他类
	//
	//1 继承或者实现接口的关系
	//2 class1是修改的类，新增的属性中使用了class2
	//3 class1是修改的类， class2是新增的类，修改/增加的函数中使用了class2
	//4 class1是修改的类，修改/增加的函数中使用了class2，但是没有使用class2修改的函数
	//5 class1是修改的类，修改/增加的函数中使用了class2且使用class2修改的函数
	//6 class1是新增的类，新增的属性中使用了class2（没有细分）
	//7 class1是新增的类， class2是新增的类，class1修改/增加的函数中使用了class2
	//8 class1是新增的类，增加的函数中使用了class2，但是没有使用class2修改的函数（class2是修改）
	//9 class1是新增的类，增加的函数中使用了class2且使用class2修改的函数
	//2<3=4<5
	//6<7=8<9
	public List<ClassChange> findDirectRelatedClass(ClassChange c){
	
		int relationc =0;
		int relation4c =0;
		List<ClassChange> relatedClasses = new ArrayList<ClassChange>();
		if(this.getAddedClassList().size() >0 || this.getChangedClassList().size() >0){
			
			for(int i = 0; i < this.getAddedClassList().size(); i++){
				ClassChange newClass = this.getAddedClassList().get(i);
				relationc = ifChangeRelated(c,  newClass);
				relation4c = ifChangeRelated(newClass,c);
				//if(relationc ==1 ||relation4c==1||relationc == 3||relationc ==7||relation4c ==3 ||relation4c ==5){
				if(relationc <0 || relation4c <0){	
				relatedClasses.add(newClass);
					//System.out.println("adding new class from new class list"+ newClass.getNewClass().getName());
			//	if(c.getNewClass().getName().getFullyQualifiedName().equals("ActionNew"))
				//	System.out.println("related ActionNew class "+newClass.getNewClass().getName());
				//System.out.println("find direct related class -------"+newClass.getNewClass().getName());
				}
			}
			for(int i = 0; i < this.getChangedClassList().size(); i++){
				ClassChange newClass = this.getChangedClassList().get(i);
				relationc = ifChangeRelated(c,  newClass);
				relation4c = ifChangeRelated(newClass,c);
				//if(relationc ==1 ||relation4c==1||relationc == 5||relationc ==9 ||relation4c ==7 ||relation4c ==9){
				if(relationc <0 || relation4c <0){		
			
					relatedClasses.add(newClass);
				//	System.out.println("adding new class from change list "+ newClass.getNewClass().getName());
					
				//if(c.getNewClass().getName().getFullyQualifiedName().equals("ActionNew"))
				//	System.out.println("related ActionNew class "+newClass.getNewClass().getName());
				}
			//	System.out.println("find direct related class -------"+newClass.getNewClass().getName());
					
			
			}
		
		}
	//	System.out.println("find direct related class for "+ c.getNewClass().getName()+ "  and find "+ relatedClasses.size());
	//	for(int i = 0; i < relatedClasses.size(); i++)
		//	System.out.println(relatedClasses.get(i).getNewPackageName()+"."+ relatedClasses.get(i).getNewClass().getName());
		return relatedClasses;
	}
	

		
	//判断两个类之间是否存在继承，关联和包含等关系
	//判断的是类class1的修改是否与class2有关联
	//relation 取值 
	//1 继承或者实现接口的关系
	//2 class1是修改的类，新增的属性中使用了class2
	//3 class1是修改的类， class2是新增的类，修改/增加的函数中使用了class2
	//4 class1是修改的类，修改/增加的函数中使用了class2，但是没有使用class2修改的函数
	//5 class1是修改的类，修改/增加的函数中使用了class2且使用class2修改的函数
	//6 class1是新增的类，新增的属性中使用了class2
	//7 class1是新增的类， class2是新增的类，修改/增加的函数中使用了class2
	//8 class1是新增的类，修改/增加的函数中使用了class2，但是没有使用class2修改的函数
	//9 class1是新增的类，修改/增加的函数中使用了class2且使用class2修改的函数
	//2<3=4<5
	//6<7=8<9
	public int ifChangeRelated(ClassChange class1, ClassChange class2){
		int relation = 0;
		boolean ifChangedClass1 = false;
		boolean ifChangedClass2 = false;
		
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();
//		String classFullName2 = class2.getNewPackageName()+"."+ className2;
		TypeDeclaration classDef1 = class1.getNewClass();
		TypeDeclaration classDef2 = class2.getNewClass();
		
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<String> newCList = new ArrayList<String>();
		ArrayList<String> cAttributeList = new ArrayList<String>();
		ArrayList<String> pList =  new ArrayList<String>();
 	
		//检查class1和class2是新增的类，还是有所修改的类
		for(int i =0; i < this.getChangedClassList().size(); i++){
			if(this.getChangedClassList().get(i).getNewClass().getName().getFullyQualifiedName().equals(classDef1.getName().getFullyQualifiedName())){
		        ifChangedClass1 = true;
			}
			if(this.getChangedClassList().get(i).getNewClass().getName().getFullyQualifiedName().equals(classDef2.getName().getFullyQualifiedName())){
		       ifChangedClass2 = true;
			}
		}  
		class1.initializeRelationship(class2.getId());
		
		//查看新增的属性定义中是否使用了对应的类名	
		if(ifChangedClass1 == true){	
			for(int j = 0 ; j < class1.getAddedFields().size(); j++){			
				if(class1.getAddedFields().get(j).getType().toString().equals(className2)){
				//		||class1.getAddedFields().get(j).getType().toString().indexOf(className2)==0){					
					relation = 3;
					List fragmentList = class1.getAddedFields().get(j).fragments();
					for(int k = 0; k < fragmentList.size(); k++){
					
						attributeList.add( fragmentList.get(k).toString());
					}
					System.out.println(class1.getId()+" : "+class1.getNewClass().getName()+ "  "+class1.getAddedFields().get(j).getType()+"  "+className2);
					class1.addRevisionRelationship(class2.getId(), relation);
				}
			}
			
			//依赖关系
			for(int j = 0 ; j < class1.getAddedMethods().size(); j++){	
				if(class1.getAddedMethods().get(j)!= null && class1.getAddedMethods().get(j).getBody() != null){
					//String methodBody = change1.getAddedMethods().get(j).getBody().toString();
					MethodDeclaration method1 = class1.getAddedMethods().get(j);
					List paraList = method1.parameters();
					if(paraList.size()> 0){
						for(int k = 0; k < paraList.size(); k++){
							String typeName = ((SingleVariableDeclaration)paraList.get(k)).getType().toString();
							String paraName = ((SingleVariableDeclaration)paraList.get(k)).getName().getFullyQualifiedName();
							if(typeName.equals(className2)){
								relation = 1;
								pList.add(paraName);
								class1.addRevisionRelationship(class2.getId(), relation);
							}
						}
					}
					if(method1.getReturnType().toString().equals(className2)){
						relation = 12;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					List<Statement> statements = class1.getAddedMethods().get(j).getBody().statements();
					for(int m =0; m < statements.size(); m++){
						String sm = statements.get(m).toString();
						//sm = CodeMatcher.deleteBlank(sm);
						if(CodeMatcher.ifWMinstanceof(sm, className2)){
							relation = 10;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if(CodeMatcher.ifWMuseclassdef(sm, className2)){
							relation = 15;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if( ifChangedClass2 ){
							for(int k =0; k < class2.getAddedMethods().size(); k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getAddedMethods().get(k).getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
							for(int k =0; k < class2.getChangedMethods().size(); k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
						else{
							for(int k =0; k < class2.getNewClass().getMethods().length; k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
					}
					/*	if( ifWordMatched(methodBody, className2 )&& (!ifChangedClass2)){
							relation = 3;
						}
						if( ifChangedClass2&& change2!=null ){
							relation = 4;
							if(change2.getAddedMethods().size()>0){
								for(int k =0; k < change2.getAddedMethods().size(); k++){
									if(ifWordMatched(methodBody, change2.getAddedMethods().get(k).getName().getFullyQualifiedName())){
										relation = 5;
									}
								}
							}
							if(change2.getChangedMethods().size()>0){
								for(int k =0; k < change2.getChangedMethods().size(); k++){
									if(ifWordMatched(methodBody, change2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName())){
										relation = 5;
									}
								}
							}
						}*/
					//}
				}
				
			}
			
			for(int j = 0 ; j < class1.getChangedMethods().size(); j++){	
				if((!class1.getChangedMethods().get(j).getNewMethod().getName().getFullyQualifiedName().equals("main"))&&(class1.getChangedMethods().get(j)!= null && class1.getChangedMethods().get(j).getNewMethod().getBody() != null)){
					List paraList = class1.getChangedMethods().get(j).getNewMethod().parameters();
					MethodDeclaration method1 = class1.getChangedMethods().get(j).getNewMethod();
					if(paraList.size()> 0){
						for(int k = 0; k < paraList.size(); k++){
							String typeName = ((SingleVariableDeclaration)paraList.get(k)).getType().toString();
							String paraName = ((SingleVariableDeclaration)paraList.get(k)).getName().getFullyQualifiedName();
							if(typeName.equals(className2)){
								relation = 1;
								pList.add(paraName);
								class1.addRevisionRelationship(class2.getId(), relation);
							}
						}
					}
					if(method1.getReturnType().toString().equals(className2)){
						relation = 12;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					List<Statement> statements = class1.getChangedMethods().get(j).getNewMethod().getBody().statements();
					for(int m =0; m < statements.size(); m++){
						String sm = statements.get(m).toString();
						//sm = CodeMatcher.deleteBlank(sm);
						if(CodeMatcher.ifWMinstanceof(sm, className2)){
							relation = 10;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if(CodeMatcher.ifWMuseclassdef(sm, className2)){
							relation = 15;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if( ifChangedClass2 ){
							for(int k =0; k < class2.getAddedMethods().size(); k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getAddedMethods().get(k).getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
							for(int k =0; k < class2.getChangedMethods().size(); k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
						else{
							for(int k =0; k < class2.getNewClass().getMethods().length; k++){
								String mName = CodeMatcher.useofMethod(sm, class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());		
								if(mName!= null){
									if(CodeMatcher.methodListInclude(pList, mName)){
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation-1);
									}
									if(CodeMatcher.methodListInclude(newCList, mName)){
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
					}
					/*	if(ifWordMatched(methodBody, className2)&& (!ifChangedClass2)){
						relation =3;
					}
					if(ifChangedClass2&& change2!=null ){
						relation = 4;
						if(change2.getAddedMethods().size()>0){
							for(int k =0; k < change2.getAddedMethods().size(); k++){
								if(ifWordMatched(methodBody, change2.getAddedMethods().get(k).getName().getFullyQualifiedName())){
									relation = 5;
								}
							}
						}
						if(change2.getChangedMethods().size()>0){
							for(int k =0; k < change2.getChangedMethods().size(); k++){
								if(ifWordMatched(methodBody, change2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName())){
									relation = 5;
								}
							}
						}
						
					}*/
				}				
			}			
		}
		else {
			if(relation ==0){
					relation =ifDirectRelated(class1,class2,ifChangedClass2);//, change2);
				
			}
		
		}
		//继承关系
		if((classDef1.getSuperclass()!= null)&&(classDef1.getSuperclass().getFullyQualifiedName().equals(className2)||(class2.getNewClass().getSuperclass()!=null && classDef1.getSuperclass().getFullyQualifiedName().equals(class2.getNewClass().getSuperclass().getFullyQualifiedName())))){
			relation = 13;
			class1.addRevisionRelationship(class2.getId(), relation);			
		}
		
		if(ifTheSameInterface(classDef1, class2.getNewClass())){
			relation = 14;	
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		return relation;
		
	}
	
	

	//判断两个类之间是否存在继承，关联和包含等关系
	//class1为新增的类，如果class2被使用，且使用了其中修改/新增的函数，则其修改相关
	public int compareRelationship(ClassChange class1, ClassChange class2, boolean ifChangedClass1, boolean ifChangedClass2){//, ClassChange change2){
		
		int relation = 0;
		String className1 = class1.getNewClass().getName().getFullyQualifiedName();
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();	
		
		ArrayList<String > log1 = new ArrayList<String>();
		ArrayList<String > log2 = new ArrayList<String>();
		ArrayList<String > log3 = new ArrayList<String>();
		ArrayList<String > log4 = new ArrayList<String>();
		ArrayList<String > log5 = new ArrayList<String>();
		ArrayList<String > log6 = new ArrayList<String>();
		ArrayList<String > log7 = new ArrayList<String>();
		ArrayList<String > log8 = new ArrayList<String>();
		ArrayList<String > log9 = new ArrayList<String>();
		ArrayList<String > log10 = new ArrayList<String>();
		ArrayList<String > log11 = new ArrayList<String>();
		ArrayList<String > log12 = new ArrayList<String>();
		ArrayList<String > log13 = new ArrayList<String>();
		ArrayList<String > log14 = new ArrayList<String>();
		ArrayList<String > log15 = new ArrayList<String>();
		ArrayList<String > log16 = new ArrayList<String>();
		ArrayList<String > log17 = new ArrayList<String>();
		ArrayList<String > log18 = new ArrayList<String>();
		ArrayList<String > log19 = new ArrayList<String>();
		
		TypeDeclaration classDef1 = class1.getNewClass();
		//类成员
		ArrayList<String> attributeList = new ArrayList<String>();
		//方法成员
		ArrayList<String> mAttributeList = new ArrayList<String>();
		//参数变量
		ArrayList<String> pList = new ArrayList<String>();
		
		List<MethodDeclaration> methodList1, methodList2;
		List<FieldDeclaration> fieldList1, fieldList2;
		methodList1 = new ArrayList<MethodDeclaration>();
		fieldList1 = new ArrayList<FieldDeclaration>();
		methodList2 = new ArrayList<MethodDeclaration>();
		fieldList2 = new ArrayList<FieldDeclaration>();
		int numberofNewMethod1 = 0 ;
		int numberofNewMethod2 = 0;
	
		if(ifChangedClass1 == true){
			numberofNewMethod1 = class1.getAddedMethods().size();
			for(int i= 0; i < class1.getAddedMethods().size(); i++){
				methodList1.add(class1.getAddedMethods().get(i));
				
			}
			for(int i = 0; i < class1.getChangedMethods().size(); i++){
				methodList1.add(class1.getChangedMethods().get(i).getNewMethod());
			}
		//	for(int i =0; i < class1.getAddedFields().size(); i++){
		//		fieldList1 .add(class1.getAddedFields().get(i));
		//}
			for(int i =0; i < class1.getNewClass().getFields().length; i++){
				fieldList1 .add(class1.getNewClass().getFields()[i]);
			}
		}
		else{
			numberofNewMethod1 = class1.getNewClass().getMethods().length;
			for(int i =0; i < class1.getNewClass().getMethods().length; i++){
				methodList1.add(class1.getNewClass().getMethods()[i]);
				
				
			}
			for(int i =0; i < class1.getNewClass().getFields().length; i++){
				fieldList1 .add(class1.getNewClass().getFields()[i]);
			}
		}
	
		if(ifChangedClass2 == true){
			numberofNewMethod2 = class2.getAddedMethods().size();
			for(int i= 0; i < class2.getAddedMethods().size(); i++){
				methodList2.add(class2.getAddedMethods().get(i));
				
			}
			for(int i = 0; i < class2.getChangedMethods().size(); i++){
				methodList2.add(class2.getChangedMethods().get(i).getNewMethod());
			}
			for(int i =0; i < class2.getAddedFields().size(); i++){
				fieldList2 .add(class2.getAddedFields().get(i));
			}
		}
		else{
			numberofNewMethod2 = class2.getNewClass().getMethods().length;
			for(int i =0; i < class2.getNewClass().getMethods().length; i++){
				methodList2.add(class2.getNewClass().getMethods()[i]);
				
			}
			for(int i =0; i < class2.getNewClass().getFields().length; i++){
				fieldList2 .add(class2.getNewClass().getFields()[i]);
			}
		}
		
		//System.out.println(class1.getId()+className1+"-------"+class2.getId()+className2+" : "+ methodList2.size());
		//继承关系
		//类型1
		if(classDef1.getSuperclass()!= null && classDef1.getSuperclass().getFullyQualifiedName().equals(className2)){
			relation = 1;
			log1.add("Class"+className1 + " extends " + className2+ "\r\n");
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		
		//接口实现
		//类型2
		List interfaceList1 = classDef1.superInterfaces();
		if(classDef1.superInterfaces()!= null && classDef1.superInterfaces().size()>0){
			for(int i =0; i < interfaceList1.size(); i++){
				Object interface1 = interfaceList1.get(i);
				if(interface1 instanceof SimpleName){
					if(((SimpleName)interface1).getFullyQualifiedName().equals(className2)){
						relation = 2;
						log2.add("Class"+class1.getId()+": "+className1 + " implements "+ className2+ "\r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
				}
			}
		}		
		//类成员
		//如果新增了类型为class2的变量，记录关系18
		//同时记录attributeList中的变量名称
		for(int i = 0; i < fieldList1.size(); i++){
			if(fieldList1.get(i).getType().toString().equals(className2)){
				List fragmentList = fieldList1.get(i).fragments();
				for(int k = 0; k < fragmentList.size(); k++){
					if(CodeMatcher.getVariable(fragmentList.get(k).toString()).length()>0)				
						attributeList.add( CodeMatcher.getVariable(fragmentList.get(k).toString()));
			//		System.out.println(CodeMatcher.getVariable(fragmentList.get(k).toString()));
				}
				relation = 18;
				log18.add("Class"+class1.getId()+": "+className1+ " defined a variable of the type:  "+className2+getTag(ifChangedClass2)+ "\r\n");								
				log18.add("Definition: "+fieldList1.get(i).toString()+ "\r\n");				
				class1.addRevisionRelationship(class2.getId(), relation);
			}
				
		}
		
		//依赖关系
		for(int j = 0 ; j < methodList1.size(); j++){	
			if(methodList1.get(j)!= null && methodList1.get(j).getBody() != null){
				//方法中的变量
				List paraList = methodList1.get(j).parameters();
				MethodDeclaration method1 =  methodList1.get(j);
				//
				mAttributeList = new ArrayList<String>();
				
				pList = new ArrayList<String>();
				
				//输入参数
				//使用了class2的类定义，关系19
				//同时记录了变量名称
				if(paraList.size()> 0){
					for(int k = 0; k < paraList.size(); k++){
						String typeName = ((SingleVariableDeclaration)paraList.get(k)).getType().toString();
						String paraName = ((SingleVariableDeclaration)paraList.get(k)).getName().getFullyQualifiedName();
						if(typeName.equals(className2)){
							relation = 19;
							log19.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+getTag(j>= numberofNewMethod1)+ method1.getName()+ ", with parameter "+ paraName+" of the type "+className2+getTag(ifChangedClass2)+ "\r\n");
							if(paraName.length()>0)
								pList.add(paraName);
							class1.addRevisionRelationship(class2.getId(), relation);
						}
					}
				}
				
				//返回值
				//类型5
				if(method1.getReturnType().toString().equals(className2)){
					relation = 5;
					log5.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", with return type "+ className2+getTag(ifChangedClass2)+ "\r\n");
					class1.addRevisionRelationship(class2.getId(), relation);
				}
				
				if(method1.thrownExceptions()!= null && method1.thrownExceptions().size()>0){
					for(int k =0; k < method1.thrownExceptions().size(); k ++ ){
						if(method1.thrownExceptions().get(k).toString().indexOf(className2)>=0){
							relation = 7;
							log7.add("Class"+class1.getId()+getTag(ifChangedClass1)+": "+className1 + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", throw exception "+ className2+getTag(ifChangedClass2)+ "\r\n");
							class1.addRevisionRelationship(class2.getId(), relation);
				
						}
					}
				}
				//辨别函数内部的耦合关系
				String statements = methodList1.get(j).getBody().toString();
				if(className1.equals("DOMOutputter")&& className2.equals("Text")){
					System.out.println("*********"+method1.getName());
					}
				while(statements.indexOf("\n")>0 && statements.length()>0){
				
					
					
					String sm = statements.substring(0,statements.indexOf("\n") );
					statements = statements.substring(statements.indexOf("\n")+1);
					
					if(method1.getName().getFullyQualifiedName().equals("output")&& className2.equals("Text")){
					
						System.out.println(sm);
						System.out.println(mAttributeList);
						
					}
						//sm = CodeMatcher.deleteBlank(sm);
					//类型4
					if(CodeMatcher.ifWMinstanceof(sm, className2)){
						relation = 4;
						log4.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: instanceof  "+ className2+getTag(ifChangedClass2)+ "\r\n");					
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//sm = CodeMatcher.deleteBlank(sm);
					//类型3
					if(CodeMatcher.ifChangeoftype(sm, className2)){
						relation = 3;
						log3.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: type convert of  "+ className2+getTag(ifChangedClass2)+ "\r\n");					
						log3.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//类型6 
					//类.class
					if(CodeMatcher.ifWMuseclassdef(sm, className2)){
						relation = 6;
						log6.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: "+ className2+getTag(ifChangedClass2)+ ".class \r\n");					
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//记录方法中定义的变量
					
					String pName = CodeMatcher.ifnewPara2(sm, className2);
					if(pName!= null){
					//	relation = 17;
						//是否使用了class2的静态方法
					//	class1.addRevisionRelationship(class2.getId(), relation);
						if(pName.length()>0){
						
							if(pName.equals(className2)){
								relation = 17;
								log17.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use: "+ className2+getTag(ifChangedClass2)+ " ‘s static method \r\n");					
								class1.addRevisionRelationship(class2.getId(), relation);
								//class1.deleteRevisionRelationship(class2.getId(), relation-2);
							}
							else{
							 mAttributeList.add(pName);}
							
						}
					}
					if(CodeMatcher.ifIncludeAttributeList(attributeList,sm)>=0){
						relation = 8;
						log8.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the class attribute "+ attributeList.get(CodeMatcher.ifIncludeAttributeList(attributeList,sm))+ " \r\n");					
						log8.add(" the code: "+sm+" \r\n");
						
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					if(CodeMatcher.ifIncludeAttributeList(mAttributeList,sm)>=0){
						relation = 11;
						log11.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the attribute defined inside class"+ mAttributeList.get(CodeMatcher.ifIncludeAttributeList(mAttributeList,sm))+ " \r\n");					
						log11.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					if(CodeMatcher.ifIncludeAttributeList(pList,sm)>=0){
						relation = 14;
						log14.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the input parameter "+ pList.get(CodeMatcher.ifIncludeAttributeList(pList,sm))+ " \r\n");					
						log14.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					for(int k =0; k < methodList2.size(); k++){
						MethodDeclaration method2 = methodList2.get(k);						
						String oName = CodeMatcher.useofMethod(sm, method2.getName().getFullyQualifiedName(),method2.parameters().size());		
						
						if(oName!= null){
						/*	if(method1.getName().getFullyQualifiedName().equals("output")&& className2.equals("Element")){
								System.out.println("oname "+oName+mAttributeList+CodeMatcher.methodListInclude(mAttributeList,oName));}
						
							if(method2.getName().getFullyQualifiedName().equals("getText"))							
								System.out.println("<><><><><><><>"+oName+"  "+method2.getName()+"  "+sm);
						*/
						//	System.out.println(sm);
						//	System.out.println(class1.getId()+" use of added methods  "+class2.getAddedMethods().get(k).getName().getFullyQualifiedName()+" in "+ className2+"  with object name: "+ oName );
							//类型16
							if(CodeMatcher.methodListInclude(pList, oName)){
								relation = 16;
								log16.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include input parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method"+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+" \r\n");					
								log16.add(" the code: "+sm+" \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);
								k = methodList2.size();//有bug，如果有多个方法的参数个数一样的话，没有判别参数类型
								//class1.deleteRevisionRelationship(class2.getId(), relation-1);
								class1.deleteRevisionRelationship(class2.getId(),  relation-2);
								log14.add("-----canceled--------"+" \r\n");
							}
							//类型13
							//
							if(CodeMatcher.methodListInclude(mAttributeList,oName)){
								
								relation = 13;
								log13.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method "+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+" \r\n");					
								log13.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								class1.deleteRevisionRelationship(class2.getId(), relation-2);
								log11.add("-----canceled--------"+" \r\n");
							}
							
							if(CodeMatcher.methodListInclude(attributeList,oName)){
								relation = 10;
								log10.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include class attribute:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method "+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+"  "+method2.parameters().size()+" \r\n");					
								log10.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								//System.out.println("use of ");
								class1.deleteRevisionRelationship(class2.getId(), relation-2);
								log8.add("-----canceled--------"+" \r\n");
							}
							
						}
						
					}
					for(int k =0; k < fieldList2.size(); k++){
						FieldDeclaration field2 = fieldList2.get(k);					
						//此处有bug，没有遍历所有的变量名称
						String oName = CodeMatcher.useofMethod(sm, CodeMatcher.getVariable(field2.fragments().get(0).toString()));		
						if(oName!= null){
						//	System.out.println(sm);
						//	System.out.println(class1.getId()+" use of added methods  "+class2.getAddedMethods().get(k).getName().getFullyQualifiedName()+" in "+ className2+"  with object name: "+ oName );
							//类型15
							if(CodeMatcher.methodListInclude(pList, oName)){
								relation = 15;
								log15.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include input parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method"+k+": "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log15.add(" the code: "+sm+" \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);
								//class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							//类型12
							//
							if(CodeMatcher.methodListInclude(mAttributeList,oName)){
								relation = 12;
								log12.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method: "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log12.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
							//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							
							if(CodeMatcher.methodListInclude(attributeList,oName)){
								relation = 9;
								log9.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include class attribute:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method: "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log9.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								//System.out.println("use of ");
							//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							
						}
						
					}
				}
			}
		}
		String location = "e:/a/change/log4relation";
//		this.writeLog(location+"1.txt", log1);
//		this.writeLog(location+"2.txt", log2);
//		this.writeLog(location+"3.txt", log3);
//		this.writeLog(location+"4.txt", log4);
//		this.writeLog(location+"5.txt", log5);
//		this.writeLog(location+"6.txt", log6);
//		this.writeLog(location+"7.txt", log7);
//		this.writeLog(location+"8.txt", log8);
//		this.writeLog(location+"9.txt", log9);
//		this.writeLog(location+"10.txt", log10);
//		this.writeLog(location+"11.txt", log11);
//		this.writeLog(location+"12.txt", log12);
//		this.writeLog(location+"13.txt", log13);
//		this.writeLog(location+"14.txt", log14);
//		this.writeLog(location+"15.txt", log15);
//		this.writeLog(location+"16.txt", log16);
//		this.writeLog(location+"17.txt", log17);
//		this.writeLog(location+"18.txt", log18);
//		this.writeLog(location+"19.txt", log19);
		return relation;
		
}
	

	public int getchangetype(boolean ifChanged1, boolean ifChanged2){
		int newVSnew = 0;
		int newVSchanged = 1;
		int changedVSnew = 2;
		int changedVSchanged = 3;
		int changeType = 0;
		if((!ifChanged1) && (!ifChanged2))  changeType =newVSnew;
		if((!ifChanged1) && (ifChanged2))  changeType = newVSchanged;
		if((ifChanged1) && (!ifChanged2))  changeType =changedVSnew;
		if(ifChanged1 && ifChanged2)  changeType = changedVSchanged;
		return changeType;
		
	}
	
	
	
	//判断两个类之间是否存在继承，关联和包含等关系
	//class1为新增的类，如果class2被使用，且使用了其中修改/新增的函数，则其修改相关
	public int compareRelationship60(ClassChange class1, ClassChange class2, boolean ifChangedClass1, boolean ifChangedClass2){//, ClassChange change2){
		
		int relation = 0;
		
		
		String className1 = class1.getNewClass().getName().getFullyQualifiedName();
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();	
		
		ArrayList<String > log1 = new ArrayList<String>();
		ArrayList<String > log2 = new ArrayList<String>();
		ArrayList<String > log3 = new ArrayList<String>();
		ArrayList<String > log4 = new ArrayList<String>();
		ArrayList<String > log5 = new ArrayList<String>();
		ArrayList<String > log6 = new ArrayList<String>();
		ArrayList<String > log7 = new ArrayList<String>();
		ArrayList<String > log8 = new ArrayList<String>();
		ArrayList<String > log9 = new ArrayList<String>();
		ArrayList<String > log10 = new ArrayList<String>();
		ArrayList<String > log11 = new ArrayList<String>();
		ArrayList<String > log12 = new ArrayList<String>();
		ArrayList<String > log13 = new ArrayList<String>();
		ArrayList<String > log14 = new ArrayList<String>();
		ArrayList<String > log15 = new ArrayList<String>();
		ArrayList<String > log16 = new ArrayList<String>();
		ArrayList<String > log17 = new ArrayList<String>();
		ArrayList<String > log18 = new ArrayList<String>();
		ArrayList<String > log19 = new ArrayList<String>();
		
		TypeDeclaration classDef1 = class1.getNewClass();
		//类成员
		ArrayList<String> attributeList = new ArrayList<String>();
		//方法成员
		ArrayList<String> mAttributeList = new ArrayList<String>();
		//参数变量
		ArrayList<String> pList = new ArrayList<String>();
		
		List<MethodDeclaration> methodList1, methodList2;
		List<FieldDeclaration> fieldList1, fieldList2;
		methodList1 = new ArrayList<MethodDeclaration>();
		fieldList1 = new ArrayList<FieldDeclaration>();
		methodList2 = new ArrayList<MethodDeclaration>();
		fieldList2 = new ArrayList<FieldDeclaration>();
		int numberofNewMethod1 = 0 ;
		int numberofNewMethod2 = 0;
	
		if(ifChangedClass1 == true){
			numberofNewMethod1 = class1.getAddedMethods().size();
			for(int i= 0; i < class1.getAddedMethods().size(); i++){
				methodList1.add(class1.getAddedMethods().get(i));
				
			}
			for(int i = 0; i < class1.getChangedMethods().size(); i++){
				methodList1.add(class1.getChangedMethods().get(i).getNewMethod());
			}
		//	for(int i =0; i < class1.getAddedFields().size(); i++){
		//		fieldList1 .add(class1.getAddedFields().get(i));
		//}
			for(int i =0; i < class1.getNewClass().getFields().length; i++){
				fieldList1 .add(class1.getNewClass().getFields()[i]);
			}
		}
		else{
			numberofNewMethod1 = class1.getNewClass().getMethods().length;
			for(int i =0; i < class1.getNewClass().getMethods().length; i++){
				methodList1.add(class1.getNewClass().getMethods()[i]);
				
				
			}
			for(int i =0; i < class1.getNewClass().getFields().length; i++){
				fieldList1 .add(class1.getNewClass().getFields()[i]);
			}
		}
	
		if(ifChangedClass2 == true){
			numberofNewMethod2 = class2.getAddedMethods().size();
			for(int i= 0; i < class2.getAddedMethods().size(); i++){
				methodList2.add(class2.getAddedMethods().get(i));
				
			}
			for(int i = 0; i < class2.getChangedMethods().size(); i++){
				methodList2.add(class2.getChangedMethods().get(i).getNewMethod());
			}
			for(int i =0; i < class2.getAddedFields().size(); i++){
				fieldList2 .add(class2.getAddedFields().get(i));
			}
		}
		else{
			numberofNewMethod2 = class2.getNewClass().getMethods().length;
			for(int i =0; i < class2.getNewClass().getMethods().length; i++){
				methodList2.add(class2.getNewClass().getMethods()[i]);
				
			}
			for(int i =0; i < class2.getNewClass().getFields().length; i++){
				fieldList2 .add(class2.getNewClass().getFields()[i]);
			}
		}
		
		//System.out.println(class1.getId()+className1+"-------"+class2.getId()+className2+" : "+ methodList2.size());
		//继承关系
		//类型1-4
		if(classDef1.getSuperclass()!= null && classDef1.getSuperclass().getFullyQualifiedName().equals(className2)){
			relation = 1+ getchangetype(ifChangedClass1, ifChangedClass2);
			log1.add("Class"+className1 + " extends " + className2+ "\r\n");
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		
		//接口实现
		//类型1-4
		List interfaceList1 = classDef1.superInterfaces();
		if(classDef1.superInterfaces()!= null && classDef1.superInterfaces().size()>0){
			for(int i =0; i < interfaceList1.size(); i++){
				Object interface1 = interfaceList1.get(i);
				if(interface1 instanceof SimpleName){
					if(((SimpleName)interface1).getFullyQualifiedName().equals(className2)){
						relation = 1+ getchangetype(ifChangedClass1, ifChangedClass2);
						log2.add("Class"+class1.getId()+": "+className1 + " implements "+ className2+ "\r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
				}
			}
		}		
		//类成员
		//如果新增了类型为class2的变量，记录关系18
		//同时记录attributeList中的变量名称
		for(int i = 0; i < fieldList1.size(); i++){
			if(fieldList1.get(i).getType().toString().equals(className2)){
				List fragmentList = fieldList1.get(i).fragments();
				for(int k = 0; k < fragmentList.size(); k++){
					if(CodeMatcher.getVariable(fragmentList.get(k).toString()).length()>0)				
						attributeList.add( CodeMatcher.getVariable(fragmentList.get(k).toString()));
			//		System.out.println(CodeMatcher.getVariable(fragmentList.get(k).toString()));
				}
				//relation = 18;
				log18.add("Class"+class1.getId()+": "+className1+ " defined a variable of the type:  "+className2+getTag(ifChangedClass2)+ "\r\n");								
				log18.add("Definition: "+fieldList1.get(i).toString()+ "\r\n");				
				//class1.addRevisionRelationship(class2.getId(), relation);
			}
				
		}
		
		//依赖关系
		for(int j = 0 ; j < methodList1.size(); j++){	
			if(methodList1.get(j)!= null && methodList1.get(j).getBody() != null){
				//方法中的变量
				List paraList = methodList1.get(j).parameters();
				MethodDeclaration method1 =  methodList1.get(j);
				//
				mAttributeList = new ArrayList<String>();
				
				pList = new ArrayList<String>();
				
				//输入参数
				//使用了class2的类定义，关系19
				//同时记录了变量名称
				if(paraList.size()> 0){
					for(int k = 0; k < paraList.size(); k++){
						String typeName = ((SingleVariableDeclaration)paraList.get(k)).getType().toString();
						String paraName = ((SingleVariableDeclaration)paraList.get(k)).getName().getFullyQualifiedName();
						if(typeName.equals(className2)){
						//	relation = 19;
							log19.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+getTag(j>= numberofNewMethod1)+ method1.getName()+ ", with parameter "+ paraName+" of the type "+className2+getTag(ifChangedClass2)+ "\r\n");
							if(paraName.length()>0)
								pList.add(paraName);
						//	class1.addRevisionRelationship(class2.getId(), relation);
						}
					}
				}
				
				//返回值
				//类型5
				if(method1.getReturnType().toString().equals(className2)){
					relation = 5;
					log5.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", with return type "+ className2+getTag(ifChangedClass2)+ "\r\n");
				//	class1.addRevisionRelationship(class2.getId(), relation);
				}
				
				if(method1.thrownExceptions()!= null && method1.thrownExceptions().size()>0){
					for(int k =0; k < method1.thrownExceptions().size(); k ++ ){
						if(method1.thrownExceptions().get(k).toString().indexOf(className2)>=0){
							relation = 7;
							log7.add("Class"+class1.getId()+getTag(ifChangedClass1)+": "+className1 + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", throw exception "+ className2+getTag(ifChangedClass2)+ "\r\n");
						//	class1.addRevisionRelationship(class2.getId(), relation);
				
						}
					}
				}
				//辨别函数内部的耦合关系
				String statements = methodList1.get(j).getBody().toString();
				if(className1.equals("DOMOutputter")&& className2.equals("Text")){
					System.out.println("*********"+method1.getName());
					}
				while(statements.indexOf("\n")>0 && statements.length()>0){
				
					
					
					String sm = statements.substring(0,statements.indexOf("\n") );
					statements = statements.substring(statements.indexOf("\n")+1);
					
					if(method1.getName().getFullyQualifiedName().equals("output")&& className2.equals("Text")){
					
						System.out.println(sm);
						System.out.println(mAttributeList);
						
					}
						//sm = CodeMatcher.deleteBlank(sm);
					//类型4--变为3
					if(CodeMatcher.ifWMinstanceof(sm, className2)){
						relation = (3-1)*4+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2);
						log4.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: instanceof  "+ className2+getTag(ifChangedClass2)+ "\r\n");					
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//sm = CodeMatcher.deleteBlank(sm);
					//类型3-- 变为2
					if(CodeMatcher.ifChangeoftype(sm, className2)){
						relation = (2-1)*4+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2);
						log3.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: type convert of  "+ className2+getTag(ifChangedClass2)+ "\r\n");					
						log3.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//类型6 --变为4
					//类.class
					if(CodeMatcher.ifWMuseclassdef(sm, className2)){
						relation = (4-1)*4+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2);
						log6.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include: "+ className2+getTag(ifChangedClass2)+ ".class \r\n");					
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					
					//记录方法中定义的变量
					
					String pName = CodeMatcher.ifnewPara2(sm, className2);
					if(pName!= null){
					//	relation = 17;
						//是否使用了class2的静态方法
					//	class1.addRevisionRelationship(class2.getId(), relation);
						if(pName.length()>0){
						
							if(pName.equals(className2)){
								relation = 17;
								log17.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use: "+ className2+getTag(ifChangedClass2)+ " ‘s static method \r\n");					
								class1.addRevisionRelationship(class2.getId(), relation);
								//class1.deleteRevisionRelationship(class2.getId(), relation-2);
							}
							else{
							 mAttributeList.add(pName);}
							
						}
					}
                    //	relation = 8; -- 5
					if(CodeMatcher.ifIncludeAttributeList(attributeList,sm)>=0){
						relation =(5-1)*4+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2) ;
						log8.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the class attribute "+ attributeList.get(CodeMatcher.ifIncludeAttributeList(attributeList,sm))+ " \r\n");					
						log8.add(" the code: "+sm+" \r\n");
						
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					//relation = 11; -- 8
					if(CodeMatcher.ifIncludeAttributeList(mAttributeList,sm)>=0){
						relation = (8-1)*4-(2)+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2);
						log11.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the attribute defined inside class"+ mAttributeList.get(CodeMatcher.ifIncludeAttributeList(mAttributeList,sm))+ " \r\n");					
						log11.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
						
					}
					
					//relation = 14; -- 11
					if(CodeMatcher.ifIncludeAttributeList(pList,sm)>=0){
						relation = (11-1)*4-(2+2)+1+getchangetype((j>= numberofNewMethod1),ifChangedClass2);
						log14.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", use the input parameter "+ pList.get(CodeMatcher.ifIncludeAttributeList(pList,sm))+ " \r\n");					
						log14.add(" the code: "+sm+" \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					for(int k =0; k < methodList2.size(); k++){
						MethodDeclaration method2 = methodList2.get(k);						
						String oName = CodeMatcher.useofMethod(sm, method2.getName().getFullyQualifiedName(),method2.parameters().size());		
						
						if(oName!= null){
						/*	if(method1.getName().getFullyQualifiedName().equals("output")&& className2.equals("Element")){
								System.out.println("oname "+oName+mAttributeList+CodeMatcher.methodListInclude(mAttributeList,oName));}
						
							if(method2.getName().getFullyQualifiedName().equals("getText"))							
								System.out.println("<><><><><><><>"+oName+"  "+method2.getName()+"  "+sm);
						*/
						//	System.out.println(sm);
						//	System.out.println(class1.getId()+" use of added methods  "+class2.getAddedMethods().get(k).getName().getFullyQualifiedName()+" in "+ className2+"  with object name: "+ oName );
							//类型16--13
							if(CodeMatcher.methodListInclude(pList, oName)){
								relation = (13-1)*4-(2+2+2)+1+getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2));
								log16.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include input parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method"+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+" \r\n");					
								log16.add(" the code: "+sm+" \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);
								
								//class1.deleteRevisionRelationship(class2.getId(), relation-1);
								System.out.println("deleted relationship "+"Class"+class1.getId()+"with class2 "+class2.getId()+getTag(ifChangedClass2)+ getchangetype((j>= numberofNewMethod1),ifChangedClass2)  );
								System.out.println( (relation+" "+(j>= numberofNewMethod1)+" " +(k>= numberofNewMethod2)+" " +getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2))+" "+getchangetype((j>= numberofNewMethod1),ifChangedClass2)));
								class1.deleteRevisionRelationship(class2.getId(),  (relation-6-getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2))+getchangetype((j>= numberofNewMethod1),ifChangedClass2)));
								log14.add("-----canceled--------"+" \r\n");
								k = methodList2.size();//有bug，如果有多个方法的参数个数一样的话，没有判别参数类型
							}
							//类型13--10
							//
							if(CodeMatcher.methodListInclude(mAttributeList,oName)){
								
								relation = 	 (10-1)*4-(2+2)+1+getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2));
								
								log13.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method "+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+" \r\n");					
								log13.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								class1.deleteRevisionRelationship(class2.getId(),  relation-6-getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2))+getchangetype((j>= numberofNewMethod1),ifChangedClass2));
								log11.add("-----canceled--------"+" \r\n");
							}
							//类型10--7
							if(CodeMatcher.methodListInclude(attributeList,oName)){
								relation =  (7-1)*4-(2)+1+getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2));
								
								log10.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include class attribute:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method "+k+": "+method2.getName().getFullyQualifiedName()+method2.parameters()+getTag(k>= numberofNewMethod2)+"  "+method2.parameters().size()+" \r\n");					
								log10.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								//System.out.println("use of ");
								class1.deleteRevisionRelationship(class2.getId(),  relation-6-getchangetype((j>= numberofNewMethod1),(k>= numberofNewMethod2))+getchangetype((j>= numberofNewMethod1),ifChangedClass2));
								log8.add("-----canceled--------"+" \r\n");
							}
							
						}
						
					}
					for(int k =0; k < fieldList2.size(); k++){
						FieldDeclaration field2 = fieldList2.get(k);					
						//此处有bug，没有遍历所有的变量名称
						String oName = CodeMatcher.useofMethod(sm, CodeMatcher.getVariable(field2.fragments().get(0).toString()));		
						if(oName!= null){
						//	System.out.println(sm);
						//	System.out.println(class1.getId()+" use of added methods  "+class2.getAddedMethods().get(k).getName().getFullyQualifiedName()+" in "+ className2+"  with object name: "+ oName );
							//类型15--12
							if(CodeMatcher.methodListInclude(pList, oName)){
								relation = (12-1)*4-(2+2)+1+getchangetype(false, (j>= numberofNewMethod1));
								log15.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include input parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method"+k+": "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log15.add(" the code: "+sm+" \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);
								//class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							//类型12--9
							//
							if(CodeMatcher.methodListInclude(mAttributeList,oName)){
								
								relation = (9-1)*4-(2)+1+getchangetype(false, (j>= numberofNewMethod1));
								log12.add("Class"+class1.getId()+": "+className1 +getTag(ifChangedClass1)+ ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include parameter:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method: "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log12.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
							//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							//类型9--6
							if(CodeMatcher.methodListInclude(attributeList,oName)){
								
								relation = (6-1)*4+1+getchangetype(false, (j>= numberofNewMethod1));
								log9.add("Class"+class1.getId()+": "+className1+getTag(ifChangedClass1) + ", method： "+ method1.getName()+getTag(j>= numberofNewMethod1)+ ", include class attribute:  "+ oName+ " (of the type " +className2+getTag(ifChangedClass2)+ ")"+ 
										" use the method: "+methodList2.get(k).getName().getFullyQualifiedName()+" \r\n");					
								log9.add(" the code: "+sm+" \r\n");
								
								class1.addRevisionRelationship(class2.getId(), relation);
								//System.out.println("use of ");
							//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
							}
							
						}
						
					}
				}
			}
		}
		String location = "D:/a/change/log4relation";
		this.writeLog(location+"1.txt", log1);
		this.writeLog(location+"2.txt", log2);
		this.writeLog(location+"3.txt", log3);
		this.writeLog(location+"4.txt", log4);
		this.writeLog(location+"5.txt", log5);
		this.writeLog(location+"6.txt", log6);
		this.writeLog(location+"7.txt", log7);
		this.writeLog(location+"8.txt", log8);
		this.writeLog(location+"9.txt", log9);
		this.writeLog(location+"10.txt", log10);
		this.writeLog(location+"11.txt", log11);
		this.writeLog(location+"12.txt", log12);
		this.writeLog(location+"13.txt", log13);
		this.writeLog(location+"14.txt", log14);
		this.writeLog(location+"15.txt", log15);
		this.writeLog(location+"16.txt", log16);
		this.writeLog(location+"17.txt", log17);
		this.writeLog(location+"18.txt", log18);
		this.writeLog(location+"19.txt", log19);
		return relation;
		
}
	

	//判断两个类之间是否存在继承，关联和包含等关系
	//class1为新增的类，如果class2被使用，且使用了其中修改/新增的函数，则其修改相关
	public int ifDirectRelated(ClassChange class1, ClassChange class2, boolean ifChangedClass2){//, ClassChange change2){
		
		int relation = 0;
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();	
		TypeDeclaration classDef1 = class1.getNewClass();
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<String> mAttributeList = new ArrayList<String>();
		ArrayList<String> pList = new ArrayList<String>();
		
		//继承关系
		//类型1
		if(classDef1.getSuperclass().getFullyQualifiedName().equals(className2)){
			relation = 1;
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		//接口实现
		//类型2
		List interfaceList1 = classDef1.superInterfaces();
		for(int i =0; i < interfaceList1.size(); i++){
			Object interface1 = interfaceList1.get(i);
			if(interface1 instanceof SimpleName){
				if(((SimpleName)interface1).getFullyQualifiedName().equals(className2)){
					relation = 2;
					class1.addRevisionRelationship(class2.getId(), relation);
				}
			}
		}
		
		//类成员
		//如果新增了类型为class2的变量，记录关系8
		//同时记录attributeList中的变量名称
		for(int i = 0; i < classDef1.getFields().length; i++){
			if(classDef1.getFields()[i].getType().toString().equals(className2)){
				
				System.out.println(classDef1.getFields()[i]);
				System.out.println(class1.getId()+" : "+class1.getNewClass().getName()+ " defined a variable of the type:  "+classDef1.getFields()[i].getType()+"  ");								
				List fragmentList = classDef1.getFields()[i].fragments();
				for(int k = 0; k < fragmentList.size(); k++){
					attributeList.add( CodeMatcher.getVariable(fragmentList.get(k).toString()));
					System.out.println(CodeMatcher.getVariable(fragmentList.get(k).toString()));
				}
				relation = 8;
				class1.addRevisionRelationship(class2.getId(), relation);
			}
				
		}
		
		//依赖关系
		for(int j = 0 ; j < classDef1.getMethods().length; j++){	
			if(classDef1.getMethods()[j]!= null && classDef1.getMethods()[j].getBody() != null){
				List paraList = classDef1.getMethods()[j].parameters();
				MethodDeclaration method1 =  classDef1.getMethods()[j];	
				//输入参数
				//使用了class2的类定义，关系14
				//同时记录了变量名称
				if(paraList.size()> 0){
					for(int k = 0; k < paraList.size(); k++){
						String typeName = ((SingleVariableDeclaration)paraList.get(k)).getType().toString();
						String paraName = ((SingleVariableDeclaration)paraList.get(k)).getName().getFullyQualifiedName();
						if(typeName.equals(className2)){
							relation = 14;
							pList.add(paraName);
							class1.addRevisionRelationship(class2.getId(), relation);
						}
					}
				}
				//返回值
				//类型5
				if(method1.getReturnType().toString().equals(className2)){
					relation = 5;
					class1.addRevisionRelationship(class2.getId(), relation);
				}
				
				//辨别函数内部的耦合关系
				String statements = classDef1.getMethods()[j].getBody().toString();
				
				while(statements.indexOf("\n")>0){
				
					String sm = statements.substring(0,statements.indexOf("\n") );
					statements = statements.substring(statements.indexOf("\n")+1);
					
					//sm = CodeMatcher.deleteBlank(sm);
					//类型10
					if(CodeMatcher.ifWMinstanceof(sm, className2)){
						relation = 10;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					//类型15
					if(CodeMatcher.ifWMuseclassdef(sm, className2)){
						relation = 15;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					//类型7
					String pName = CodeMatcher.ifnewPara(sm, className2);
					if(pName!= null){
						relation = 7;
						class1.addRevisionRelationship(class2.getId(), relation);
						if(pName.length()>0){
							if(pName.equals(className2)){
								relation = 9;
								class1.addRevisionRelationship(class2.getId(), relation);
								//class1.deleteRevisionRelationship(class2.getId(), relation-2);
							}
							else
							 mAttributeList.add(pName);
						}
					}
					
					if( ifChangedClass2 ){
						for(int k =0; k < class2.getAddedMethods().size(); k++){
							String oName = CodeMatcher.useofMethod(sm, class2.getAddedMethods().get(k).getName().getFullyQualifiedName());		
							if(oName!= null){
							//	System.out.println(sm);
							//	System.out.println(class1.getId()+" use of added methods  "+class2.getAddedMethods().get(k).getName().getFullyQualifiedName()+" in "+ className2+"  with object name: "+ oName );
								//类型2，同时减少类型1的数量
								if(CodeMatcher.methodListInclude(pList, oName)){
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
									//class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								//类型8，同时减少类型7的数量
								if(CodeMatcher.methodListInclude(mAttributeList,oName)){
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								if(CodeMatcher.methodListInclude(attributeList,oName)){
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
									System.out.println("use of ");
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
							}
						}
						for(int k =0; k < class2.getChangedMethods().size(); k++){
							String oName = CodeMatcher.useofMethod(sm, class2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName());		
							if(oName!= null){
								//类型2，同时减少类型1的数量
								if(CodeMatcher.methodListInclude(pList, oName)){
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
									//class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								//类型8，同时减少类型7的数量
								if(CodeMatcher.methodListInclude(mAttributeList,oName)){
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								//类型4
								if(CodeMatcher.methodListInclude(attributeList,oName)){
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
							}
						}
					}
					else{
						for(int k =0; k < class2.getNewClass().getMethods().length; k++){
							String oName = CodeMatcher.useofMethod(sm, class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());		
							if(oName!= null){
								//类型2，同时减少类型1的数量
								if(CodeMatcher.methodListInclude(pList, oName)){
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
									//class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								//类型8，同时减少类型7的数量
								if(CodeMatcher.methodListInclude(mAttributeList,oName)){
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
								if(CodeMatcher.methodListInclude(attributeList,oName)){
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
								//	class1.deleteRevisionRelationship(class2.getId(), relation-1);
								}
							}
							}
						}
					}
				}
			/*	if( CodeMatcher.ifWordMatched(methodBody, className2)&& (!ifChangedClass2)){
					relation = 7;
				}
				if(ifChangedClass2&& change2!=null ){
					relation = 8;
					if(change2.getAddedMethods().size()>0){
						for(int k =0; k < change2.getAddedMethods().size(); k++){
							if(CodeMatcher.ifWordMatched(methodBody, change2.getAddedMethods().get(k).getName().getFullyQualifiedName())){
								relation = 9;
							}
						}
					}
					if(change2.getChangedMethods().size()>0){
						for(int k =0; k < change2.getChangedMethods().size(); k++){
							if(CodeMatcher.ifWordMatched(methodBody, change2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName())){
								relation = 9;
							}
						}
					}
			*/		
				//}
			
			
		}

	return relation;
		
}
	
	
	//判断class1是否以class2为接口，或者实现相同的接口
	public boolean ifTheSameInterface(TypeDeclaration class1, TypeDeclaration class2){
		boolean tag = false;
		List interfaceList1 = class1.superInterfaces();
		List interfaceList2 = class2.superInterfaces();
		if(interfaceList1.size()>0 && interfaceList2.size()>0){
			//如果一个类实现了某个接口，接口有改变，其改变与该类建立联系
			for(int i =0; i < interfaceList1.size(); i++){
				Object interface1 = interfaceList1.get(i);
				if(interface1 instanceof SimpleName){
					if(((SimpleName)interface1).getFullyQualifiedName().equals(class2.getName().getFullyQualifiedName()))
						tag = true;
				}
				for(int j =0; j < interfaceList2.size(); j++){
					Object interface2 = interfaceList2.get(j);
					if(interface2 instanceof SimpleName){
						if(((SimpleName)interface2).getFullyQualifiedName().equals(((SimpleName)interface2).getFullyQualifiedName()))
							tag = true;
					}
					//System.out.println("----------------testing   "+ interface2.getClass());
				
					//实现了同一个interface的两个不同类，如果他们实现的interface本身没有修改，而这两个类各有修改，这种修改是相对独立的，目前没有纳入考虑
					/*	if((interface1 instanceof SimpleName) && (interface2 instanceof  SimpleName)){
						if(((SimpleName)interface1).getFullyQualifiedName().equals(((SimpleName)interface2).getFullyQualifiedName()))
							tag = true;
					
					
					}*/
				}
			}
		}
		//System.out.println(tag + "  "+class1.getName()+"  "+class2.getName());
		return tag;
	}
	
	
	
//打印类相关的信息
	public void printChange(){
		
		
		for(int i = 0 ; i < this.getAddedClassList().size(); i ++){
			System.out.println("new Added class "+ i + " : "+this.getAddedClassList().get(i).getNewClass().getName());
		}
		for(int i = 0 ; i < this.getChangedClassList().size(); i ++){
			System.out.println("Changed class "+ i + " : "+this.getChangedClassList().get(i).getOldClass().getName());
			ClassChange classChange = this.getChangedClassList().get(i);
	/*		for(int j = 0; j < classChange.getAddedFields().size(); j++){
				System.out.println("     new Added Field "+ j + " : "+ classChange.getAddedFields().get(j));
			}
			for(int j = 0; j < classChange.getDeletedFields().size(); j++){
				System.out.println("     Deleted Field "+ j + " : "+ classChange.getDeletedFields().get(j));
			}
			for(int j = 0; j < classChange.getAddedMethods().size(); j++){
				System.out.println("     new Added method "+ j + " : "+ classChange.getAddedMethods().get(j).getName());
			}
			for(int j = 0; j < classChange.getChangedMethods().size(); j++){
				System.out.println("     new changed method "+ j + " : "+ classChange.getChangedMethods().get(j).getNewMethod().getName());
			} 
			for(int j = 0; j < classChange.getDeletedMethods().size(); j++){
				System.out.println("     new deleted method "+ j + " : "+ classChange.getDeletedMethods().get(j).getName());
			}*/
		}
		for(int i = 0 ; i < this.getDeletedClassList().size(); i ++){
			System.out.println("Deleted class "+ i + " : "+this.getDeletedClassList().get(i).getName());
		}
	
		List<DesignDecision> ddList = this.getDesignDecisionList(); 
		for(int i =0; i < ddList.size(); i++){
			System.out.println("Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes ");
			for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
				System.out.println("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName());
			}
		}
	/*	ddList = this.generateDesignDicisionsFromChange();
		for(int i =0; i < ddList.size(); i++){
			System.out.println("Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes ");
			for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
				System.out.println("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getName());
			}
		}*/
	}
	private String getTag(boolean ifChanged){
		String tag;
		if(ifChanged == false ){
			return new String(" $new$ ");
		}
		else{
		    return new String(" $changed$ ");	
		}
	}
	//将版本变化情况记录到对应文件中
	
	public void writeLog(String logFilePath){
		try {
			
			File f = new File(logFilePath);
		       if (f.exists()) {
		        //  System.out.println("文件存在");
		      //    TemporarySpace.setConsoleString("文件存在");
		       }
		       else {
		          System.out.println("文件不存在，正在创建...");
		          TemporarySpace.setConsoleString("文件不存在，正在创建...");
		          if (f.createNewFile()) {
		       	   System.out.println("文件创建成功！");
		       	TemporarySpace.setConsoleString("文件创建成功！");
		          }
		    	   else {
		            System.out.println("文件创建失败！");
		            TemporarySpace.setConsoleString("文件创建失败！");
		          }
		       }
		          BufferedWriter output = new BufferedWriter(new FileWriter(f));
		      	
		          output.write("Compared Result of  two versions"+"\r\n");
		          TemporarySpace.setConsoleString("Compared Result of  two versions"+"\r\n");
		          TemporarySpace.setDesignDecisionString("Compared Result of  two versions"+"\r\n");

		          output.write("Number of New Class: "+this.getAddedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of New Class: "+this.getAddedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of New Class: "+this.getAddedClassList().size()+"\r\n");

		          output.write("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");

		          output.write("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          
		        
		  		for(int i = 0 ; i < this.getAddedClassList().size(); i ++){
		  			output.write("new Added class "+ i + " : "+this.getAddedClassList().get(i).getNewPackageName()+"."+this.getAddedClassList().get(i).getNewClass().getName()+ ": "+"\r\n");
		  			TemporarySpace.setConsoleString("new Added class "+ i + " : "+this.getAddedClassList().get(i).getNewPackageName()+"."+this.getAddedClassList().get(i).getNewClass().getName()+ ": "+"\r\n");
		  		}
		  		for(int i = 0 ; i < this.getChangedClassList().size(); i ++){
		  			output.write("Changed class "+ (i+this.getAddedClassList().size()) + " : "+this.getChangedClassList().get(i).getOldClass().getName()+"\r\n");
		  			TemporarySpace.setConsoleString("Changed class "+ i + " : "+this.getChangedClassList().get(i).getOldClass().getName()+"\r\n");
		  			ClassChange classChange = this.getChangedClassList().get(i);
		  			for(int j = 0; j < classChange.getAddedFields().size(); j++){
		  				output.write("     new Added Field "+ j + " : "+ classChange.getAddedFields().get(j).getType()+" "+ classChange.getAddedFields().get(j).fragments()+"\r\n");
		  				TemporarySpace.setConsoleString("     new Added Field "+ j + " : "+ classChange.getAddedFields().get(j)+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getDeletedFields().size(); j++){
		  			//	output.write("     Deleted Field "+ j + " : "+ classChange.getDeletedFields().get(j)+"\r\n");
		  				TemporarySpace.setConsoleString("     Deleted Field "+ j + " : "+ classChange.getDeletedFields().get(j)+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getAddedMethods().size(); j++){
		  				output.write("     new Added method "+ (j) + " : "+ classChange.getAddedMethods().get(j).getName()+ classChange.getAddedMethods().get(j).parameters()+"\r\n");
		  				TemporarySpace.setConsoleString("     new Added method "+ j + " : "+ classChange.getAddedMethods().get(j).getName()+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getChangedMethods().size(); j++){
		  				output.write("     new changed method "+ (j+classChange.getAddedMethods().size()) + " : "+ classChange.getChangedMethods().get(j).getNewMethod().getName()+classChange.getChangedMethods().get(j).getNewMethod().parameters().toString()+"\r\n");
		  				TemporarySpace.setConsoleString("     new changed method "+ j + " : "+ classChange.getChangedMethods().get(j).getNewMethod().getName()+
		  						"\r\n");
		  			} 
		  			for(int j = 0; j < classChange.getDeletedMethods().size(); j++){
		  				output.write("     new deleted method "+ j + " : "+ classChange.getDeletedMethods().get(j).getName()+"\r\n");
		  				TemporarySpace.setConsoleString("     new deleted method "+ j + " : "+ classChange.getDeletedMethods().get(j).getName()+"\r\n");
		  			}
		  		}
		  		for(int i = 0 ; i < this.getDeletedClassList().size(); i ++){
		  			output.write("Deleted class "+ i + " : "+this.getDeletedClassList().get(i).getName()+"\r\n");
		  			TemporarySpace.setConsoleString("Deleted class "+ i + " : "+this.getDeletedClassList().get(i).getName()+"\r\n");
		  		}
		  	/*
		          
		        //  output.write(content);
		  		List<DesignDecision> ddList = new ArrayList<DesignDecision> ();
				 ddList = this.getDesignDecisionList(); 
				for(int i =0; i < ddList.size(); i++){
					
					output.write("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
				//	TemporarySpace.setConsoleString("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
				//	TemporarySpace.setDesignDecisionString("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
					for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					//	TemporarySpace.setConsoleString("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					//	TemporarySpace.setDesignDecisionString("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					}
				}
				//	output.write("　-------related Note "+"\r\n");
					
			/*		for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						ClassChange newclass = ddList.get(i).getInvolvingClassList().get(j).getClassa();
				//		output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
						if(newclass.getNewClass().getJavadoc()!= null)
				//		output.write(newclass.getNewClass().getJavadoc().toString());			
					
						if(newclass.getAddedMethods().size() >0){
							for(int k = 0; k < newclass.getAddedMethods().size(); k++){
								if(newclass.getAddedMethods().get(k).getJavadoc()!= null){
									output.write(newclass.getAddedMethods().get(k).getName().getFullyQualifiedName()+"\r\n");
									output.write(newclass.getAddedMethods().get(k).getJavadoc().toString()+"\r\n");
								}
							}
						}
						if(newclass.getChangedMethods().size() >0){
							for(int k = 0; k < newclass.getChangedMethods().size(); k++){
								if(newclass.getChangedMethods().get(k).getNewMethod().getJavadoc()!= null){
									output.write(newclass.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName()+"\r\n");
								
									output.write(newclass.getChangedMethods().get(k).getNewMethod().getJavadoc().toString()+"\r\n");
								}
							}
						}
					}*/
				
		//		ddList = this.generateDesignDicisionsFromChange();
				/*	for(int i =0; i < ddList.size(); i++){
					output.write("Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
					for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getName()+"\r\n");
					}
				}*/
		  		this.compareChangType4Direct();
				output.write("testing relationship "+"\r\n");
				List<ClassChange> relatedClasses =this.getAcClassList();
				
				
				for(int i=0; i < relatedClasses .size(); i++){
					for(int j =0; j < relatedClasses.size(); j++)
					if(i!=j && relatedClasses .get(i).ifRelated(j)){
						output.write("class "+ relatedClasses.get(i).getId()+ "  vs  class "+ relatedClasses.get(j).getId()+"：  ");
						for(int k = 0; k < 18; k++){
							output.write("r"+k+":"+relatedClasses.get(i).getRelationship().get(j)[k]+"  ");
						}
						output.write("\r\n");
					}
				
				}
				 Integer[] record = this.getCochangeRecord();
				 output.write("record  ");
				 for(int i = 0; i < 18; i ++){
					output.write(i + " :  "+record[i]+"   ");
				}
				 output.write("\r\n");
		       output.close();
		      
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getRelations() {
		this.compareChangType4Direct();
		List<ClassChange> relatedClasses =this.getAcClassList();
		List<Integer> relations = new ArrayList<Integer>();
		for(int i=0; i < relatedClasses .size(); i++){
			for(int j =0; j < relatedClasses.size(); j++)
			if(i!=j && relatedClasses .get(i).ifRelated(j)){
				for(int k = 0; k < 18; k++){
					relations.add(relatedClasses.get(i).getRelationship().get(j)[k]);
				}
			}
		
		}
		return relations;
	}
//将版本变化情况记录到对应文件中
	
	public void writeLog60(String logFilePath){
		try {
			
			File f = new File(logFilePath);
		       if (f.exists()) {
		        //  System.out.println("文件存在");
		      //    TemporarySpace.setConsoleString("文件存在");
		       }
		       else {
		          System.out.println("文件不存在，正在创建...");
		          TemporarySpace.setConsoleString("文件不存在，正在创建...");
		          if (f.createNewFile()) {
		       	   System.out.println("文件创建成功！");
		       	TemporarySpace.setConsoleString("文件创建成功！");
		          }
		    	   else {
		            System.out.println("文件创建失败！");
		            TemporarySpace.setConsoleString("文件创建失败！");
		          }
		       }
		          BufferedWriter output = new BufferedWriter(new FileWriter(f));
		      	
		          output.write("Compared Result of  two versions"+"\r\n");
		          TemporarySpace.setConsoleString("Compared Result of  two versions"+"\r\n");
		          TemporarySpace.setDesignDecisionString("Compared Result of  two versions"+"\r\n");

		          output.write("Number of New Class: "+this.getAddedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of New Class: "+this.getAddedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of New Class: "+this.getAddedClassList().size()+"\r\n");

		          output.write("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of Changed Class: "+this.getChangedClassList().size()+"\r\n");

		          output.write("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          TemporarySpace.setConsoleString("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          TemporarySpace.setDesignDecisionString("Number of Deleted Class: "+this.getDeletedClassList().size()+"\r\n");
		          
		        
		  		for(int i = 0 ; i < this.getAddedClassList().size(); i ++){
		  			output.write("new Added class "+ i + " : "+this.getAddedClassList().get(i).getNewPackageName()+"."+this.getAddedClassList().get(i).getNewClass().getName()+ ": "+"\r\n");
		  			TemporarySpace.setConsoleString("new Added class "+ i + " : "+this.getAddedClassList().get(i).getNewPackageName()+"."+this.getAddedClassList().get(i).getNewClass().getName()+ ": "+"\r\n");
		  		}
		  		for(int i = 0 ; i < this.getChangedClassList().size(); i ++){
		  			output.write("Changed class "+ (i+this.getAddedClassList().size()) + " : "+this.getChangedClassList().get(i).getOldClass().getName()+"\r\n");
		  			TemporarySpace.setConsoleString("Changed class "+ i + " : "+this.getChangedClassList().get(i).getOldClass().getName()+"\r\n");
		  			ClassChange classChange = this.getChangedClassList().get(i);
		  			for(int j = 0; j < classChange.getAddedFields().size(); j++){
		  				output.write("     new Added Field "+ j + " : "+ classChange.getAddedFields().get(j).getType()+" "+ classChange.getAddedFields().get(j).fragments()+"\r\n");
		  				TemporarySpace.setConsoleString("     new Added Field "+ j + " : "+ classChange.getAddedFields().get(j)+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getDeletedFields().size(); j++){
		  			//	output.write("     Deleted Field "+ j + " : "+ classChange.getDeletedFields().get(j)+"\r\n");
		  				TemporarySpace.setConsoleString("     Deleted Field "+ j + " : "+ classChange.getDeletedFields().get(j)+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getAddedMethods().size(); j++){
		  				output.write("     new Added method "+ (j) + " : "+ classChange.getAddedMethods().get(j).getName()+ classChange.getAddedMethods().get(j).parameters()+"\r\n");
		  				TemporarySpace.setConsoleString("     new Added method "+ j + " : "+ classChange.getAddedMethods().get(j).getName()+"\r\n");
		  			}
		  			for(int j = 0; j < classChange.getChangedMethods().size(); j++){
		  				output.write("     new changed method "+ (j+classChange.getAddedMethods().size()) + " : "+ classChange.getChangedMethods().get(j).getNewMethod().getName()+classChange.getChangedMethods().get(j).getNewMethod().parameters().toString()+"\r\n");
		  				TemporarySpace.setConsoleString("     new changed method "+ j + " : "+ classChange.getChangedMethods().get(j).getNewMethod().getName()+
		  						"\r\n");
		  			} 
		  			for(int j = 0; j < classChange.getDeletedMethods().size(); j++){
		  				output.write("     new deleted method "+ j + " : "+ classChange.getDeletedMethods().get(j).getName()+"\r\n");
		  				TemporarySpace.setConsoleString("     new deleted method "+ j + " : "+ classChange.getDeletedMethods().get(j).getName()+"\r\n");
		  			}
		  		}
		  		for(int i = 0 ; i < this.getDeletedClassList().size(); i ++){
		  			output.write("Deleted class "+ i + " : "+this.getDeletedClassList().get(i).getName()+"\r\n");
		  			TemporarySpace.setConsoleString("Deleted class "+ i + " : "+this.getDeletedClassList().get(i).getName()+"\r\n");
		  		}
		  	/*
		          
		        //  output.write(content);
		  		List<DesignDecision> ddList = new ArrayList<DesignDecision> ();
				 ddList = this.getDesignDecisionList(); 
				for(int i =0; i < ddList.size(); i++){
					
					output.write("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
				//	TemporarySpace.setConsoleString("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
				//	TemporarySpace.setDesignDecisionString("\r\n"+"Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
					for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					//	TemporarySpace.setConsoleString("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					//	TemporarySpace.setDesignDecisionString("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					}
				}
				//	output.write("　-------related Note "+"\r\n");
					
			/*		for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						ClassChange newclass = ddList.get(i).getInvolvingClassList().get(j).getClassa();
				//		output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
						if(newclass.getNewClass().getJavadoc()!= null)
				//		output.write(newclass.getNewClass().getJavadoc().toString());			
					
						if(newclass.getAddedMethods().size() >0){
							for(int k = 0; k < newclass.getAddedMethods().size(); k++){
								if(newclass.getAddedMethods().get(k).getJavadoc()!= null){
									output.write(newclass.getAddedMethods().get(k).getName().getFullyQualifiedName()+"\r\n");
									output.write(newclass.getAddedMethods().get(k).getJavadoc().toString()+"\r\n");
								}
							}
						}
						if(newclass.getChangedMethods().size() >0){
							for(int k = 0; k < newclass.getChangedMethods().size(); k++){
								if(newclass.getChangedMethods().get(k).getNewMethod().getJavadoc()!= null){
									output.write(newclass.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName()+"\r\n");
								
									output.write(newclass.getChangedMethods().get(k).getNewMethod().getJavadoc().toString()+"\r\n");
								}
							}
						}
					}*/
				
		//		ddList = this.generateDesignDicisionsFromChange();
				/*	for(int i =0; i < ddList.size(); i++){
					output.write("Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
					for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getName()+"\r\n");
					}
				}*/
		  		this.compareChangType4Direct60();
				output.write("testing relationship "+"\r\n");
				List<ClassChange> relatedClasses =this.getAcClassList();
				
				
				for(int i=0; i < relatedClasses .size(); i++){
					for(int j =0; j < relatedClasses.size(); j++)
					if(i!=j && relatedClasses .get(i).ifRelated(j)){
						output.write("class "+ relatedClasses.get(i).getId()+ "  vs  class "+ relatedClasses.get(j).getId()+"：  ");
						for(int k = 1; k < 61; k++){
							if(relatedClasses.get(i).getRelationship().get(j)[k]>0)
						
								//output.write("r"+k+":1"+"  ");
							output.write("1"+"  ");
							else
								//output.write("r"+k+":"+relatedClasses.get(i).getRelationship().get(j)[k]+"  ");
							//output.write(relatedClasses.get(i).getRelationship().get(j)[k]+"  ");
							output.write("0"+"  ");
							
						}
						output.write("\r\n");
					}
				
				}
//				 Integer[] record = this.getCochangeRecord();
//				 output.write("record  ");
//				 for(int i = 1; i < 61; i ++){
//					output.write(i + " :  "+record[i]+"   ");
//				}
				 output.write("\r\n");
		       output.close();
		      
		} catch (Exception e) {
		
			e.printStackTrace();
		    
		}
	}
	/**
	 * The cluster consisted of classes with related changes wrote into a XML file
	 * Troy
	 * 2014-05-27
	 */
	
//	public void writeClusterClasses2XML(String path){
//		List<ClassChange> relatedClasses =this.getAcClassList();
//		HashMap<ClassChange,ClassChange> relatedClassMap = new HashMap<ClassChange,ClassChange>();
//		String[] lable = ClusterClassesWithRelatedChanges.clusterRelatedClasses46Dimen(path);
//		int k=0;
//		for(int i=0; i < relatedClasses.size(); i++){
//			for(int j =0; j < relatedClasses.size(); j++){
//				k++;
//			if(i!=j && relatedClasses .get(i).ifRelated(j)){
//				//HashMap<Object,Object> twoClassChange = new HashMap<Object,Object>();
//				//twoClassChange.put(relatedClasses.get(i), relatedClasses.get(j));
//				if(Integer.getInteger(lable[k]) == 2){
//					relatedClassMap.put(relatedClasses.get(i), relatedClasses.get(j));
//				}												
//			}		
//		  }
//		}
//	}
	
	public List<List<ClassChange>> clusterRelatedClasses(HashMap<ClassChange,ClassChange> map){
		List<ClassChange> list4MapKey = new ArrayList<ClassChange>();
		List<ClassChange> list4MapValue = new ArrayList<ClassChange>();
		List<ArrayList<ClassChange>> list4Clusters = new ArrayList<ArrayList<ClassChange>>();
		
		Set<ClassChange> set = map.keySet();
	       Iterator<ClassChange> it = set.iterator();
	       while(it.hasNext()){	    	   
	    	   ClassChange cc = (ClassChange)it.next();
	    	   list4MapKey.add(cc);
	    	   list4MapValue.add(map.get(cc));
	       }
	       
	    for(int i=0;i<list4MapKey.size();i++){
	    	for(int j=0;j<list4MapValue.size();j++){
	    		
	    	}
	    }
		return null;
	}
	/**
	 * 聚类与主类直接相关的类
	 * @param list4MapKey
	 * @param list4MapValue
	 * @return
	 */
	
	public List<ArrayList<ClassChange>> directRelatedCluster(List<ClassChange> list4MapKey, List<ClassChange> list4MapValue){
		ClassChange ccKey = null;
		ClassChange ccValue = null;
		List<ArrayList<ClassChange>> parentList = new ArrayList<ArrayList<ClassChange>>();
		for(int i=0;i<list4MapKey.size();i++){
			ArrayList<ClassChange> childList = new ArrayList<ClassChange>();
			ccKey = list4MapKey.get(i);
			ccValue = list4MapValue.get(i);
	    	childList.add(ccKey);
	    	childList.add(ccValue);			
			for(int j=i+1;j<list4MapKey.size();j++){
				if(list4MapKey.get(i).equals(list4MapKey.get(j))){   	
			    	ccValue = list4MapValue.get(j);
			    	childList.add(ccValue);
			    	//删除原始队列中比较过后的类，那么队列整体的index会前移吗？
			    	list4MapKey.remove(j);
			    	list4MapValue.remove(j);
			    	--j;
				}				
			}
			
	    	//list4MapKey.remove(i);
	    	//list4MapValue.remove(i);
	    	
			parentList.add(childList);
	    }


		return parentList;
	}
	
	/**
	 * 除了聚类与主类直接相关的类，还聚类上哪些与主类间接相关的类
	 * @param a
	 * @param b
	 * @return
	 */
	public List<ArrayList<ClassChange>> indirectRelatedCluster(List<ClassChange> list4MapKey, List<ClassChange> list4MapValue){
		
		ClassChange ccKey = null;
		ClassChange ccValue = null;
		List<ArrayList<ClassChange>> parentList = new ArrayList<ArrayList<ClassChange>>();
		for(int i=0;i<list4MapKey.size();i++){
			ArrayList<ClassChange> childList = new ArrayList<ClassChange>();
			ccKey = list4MapKey.get(i);
			ccValue = list4MapValue.get(i);
	    	childList.add(ccKey);
	    	childList.add(ccValue);			
			for(int j=i+1;j<list4MapKey.size();j++){
				if(list4MapKey.get(i).equals(list4MapKey.get(j))){   	
			    	ccValue = list4MapValue.get(j);
			    	childList.add(ccValue);
			    	//删除原始队列中比较过后的类，那么队列整体的index会前移吗？会！
			    	list4MapKey.remove(j);
			    	list4MapValue.remove(j);
			    	--j;
				}
								
			}
			for(int k=0; k<list4MapKey.size();k++){
				for(int n=1; n<childList.size();n++){//childList的size()会随着新加入的类不断变成
					if(list4MapKey.get(k).equals(childList.get(n))){   	
				    	ccValue = list4MapValue.get(k);
				    	childList.add(ccValue);
				    	//删除原始队列中比较过后的类，那么队列整体的index会前移吗？会！
				    	list4MapKey.remove(k);
				    	list4MapValue.remove(k);
				    	--k;
					}		
				}
			}
				    	
			parentList.add(childList);
	    }


		return parentList;
	}
	
	public void writeLog(String logFilePath,List <String> log){
		
			try {				
				File f = new File(logFilePath);			       
				if (f.exists()) {
			      //    System.out.println("文件存在");
			   //      TemporarySpace.setConsoleString("文件存在");			    				
				}			    
				else {
			          System.out.println("文件不存在，正在创建...");
			         if (f.createNewFile()) {
			       	   System.out.println("文件创建成功！");
			          }
			    	   else {
			            System.out.println("文件创建失败！");			    
			    	   }			     
				}
			//	 BufferedWriter output = new BufferedWriter(new FileWriter(f));
				BufferedWriter output =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilePath, true)));  

			      for(int i =0; i < log.size(); i++){
		          
			    	  output.write(log.get(i));
			    	  output.write("\r\n");
			      }
		          output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	public void generateLDAInput(String logFilePath){
		try {
		  		
			List<DesignDecision> ddList = new ArrayList<DesignDecision> ();
				 
			ddList = this.getDesignDecisionList(); 
				
			for(int i =0; i < ddList.size(); i++){
					
				File f = new File(logFilePath+"Decision"+i+".txt");
				System.out.println(logFilePath);
				      
				if (f.exists()) {
				          
					System.out.println("文件存在");
					TemporarySpace.setConsoleString("文件存在");  
				}
				       
				else {
				          
					System.out.println("文件不存在，正在创建...");
					TemporarySpace.setConsoleString("文件不存在，正在创建...");
				         
					if (f.createNewFile()) {
				       	  
						System.out.println("文件创建成功！");
						TemporarySpace.setConsoleString("文件创建成功！");  
					}
				    	  
					else {
				           
						System.out.println("文件创建失败！");
						TemporarySpace.setConsoleString("文件创建失败！");  
					}
				      
				}
				          
				BufferedWriter output = new BufferedWriter(new FileWriter(f));
				
				for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
					output.write("\r\n");
					output.write("\r\n");
					ClassChange newclass = ddList.get(i).getInvolvingClassList().get(j).getClassa();
					output.write(ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");
					TemporarySpace.setConsoleString(ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewPackageName()+"."+ddList.get(i).getInvolvingClassList().get(j).getClassa().getNewClass().getName()+"\r\n");  
					if(newclass.getNewClass().getJavadoc()!= null){
						output.write(newclass.getNewClass().getJavadoc().toString()+"\r\n");			
					    TemporarySpace.setConsoleString(newclass.getNewClass().getJavadoc().toString()+"\r\n");  
					}
					if(newclass.getAddedMethods().size() >0){
						for(int k = 0; k < newclass.getAddedMethods().size(); k++){
							output.write("\r\n");
							MethodDeclaration method = newclass.getAddedMethods().get(k);
							output.write(method.getName().getFullyQualifiedName()+"\r\n");
							TemporarySpace.setConsoleString(method.getName().getFullyQualifiedName()+"\r\n"); 
							if(method.parameters().size() >0){
								for(int kk = 0; kk < method.parameters().size(); kk++){
									output.write(method.parameters().get(kk).toString()+"\r\n");
									TemporarySpace.setConsoleString(method.parameters().get(kk).toString()+"\r\n"); 
								}
							}
							output.write(method.getReturnType().toString()+"\r\n");
							TemporarySpace.setConsoleString(method.getReturnType().toString()+"\r\n");
							if(newclass.getAddedMethods().get(k).getJavadoc()!= null){
								
							output.write(method.getJavadoc().toString()+"\r\n");
							TemporarySpace.setConsoleString(method.getJavadoc().toString()+"\r\n");
							}
						
						}
						
					}
					if(newclass.getAddedFields().size() >0){
						for(int k = 0; k < newclass.getAddedFields().size(); k++){
							output.write("\r\n");
							FieldDeclaration field = newclass.getAddedFields().get(k);
							output.write(field.toString()+"\r\n");
							TemporarySpace.setConsoleString(field.toString()+"\r\n");
							if(field.getJavadoc()!= null){
								
							output.write(field.getJavadoc().toString()+"\r\n");
							TemporarySpace.setConsoleString(field.getJavadoc().toString()+"\r\n");
							}
						
						}
						
					}
						if(newclass.getChangedMethods().size() >0){
							for(int k = 0; k < newclass.getChangedMethods().size(); k++){
								output.write("\r\n");	
								MethodDeclaration method = newclass.getChangedMethods().get(k).getNewMethod();
									output.write(method.getName().getFullyQualifiedName()+"\r\n");
									TemporarySpace.setConsoleString(method.getName().getFullyQualifiedName()+"\r\n");
									if(method.parameters().size() >0){
										for(int kk = 0; kk < method.parameters().size(); kk++){
											output.write(method.parameters().get(kk).toString()+"\r\n");
											TemporarySpace.setConsoleString(method.parameters().get(kk).toString()+"\r\n");
										}
									}
									output.write(method.getReturnType().toString()+"\r\n");
									TemporarySpace.setConsoleString(method.getReturnType().toString()+"\r\n");
								
									if(newclass.getChangedMethods().get(k).getNewMethod().getJavadoc()!= null){
										output.write(method.getJavadoc().toString()+"\r\n");
										TemporarySpace.setConsoleString(method.getJavadoc().toString()+"\r\n");
										}
							}
						}
						if(newclass.getChangedMethods().size() == 0 && newclass.getAddedMethods().size() ==0){
							for(int k = 0; k < newclass.getNewClass().getMethods().length; k++){
								output.write("\r\n");
									MethodDeclaration method = newclass.getNewClass().getMethods()[k];
									output.write(method.getName().getFullyQualifiedName()+"\r\n");
									TemporarySpace.setConsoleString(method.getName().getFullyQualifiedName()+"\r\n");
									if(method.parameters().size() >0){
										for(int kk = 0; kk < method.parameters().size(); kk++){
											output.write(method.parameters().get(kk).toString()+"\r\n");
											TemporarySpace.setConsoleString(method.parameters().get(kk).toString()+"\r\n");
										}
									}
									
									output.write(method.getReturnType().toString()+"\r\n");
									TemporarySpace.setConsoleString(method.getReturnType().toString()+"\r\n");
									if(newclass.getNewClass().getMethods()[k].getJavadoc()!= null){
										
									output.write(method.getJavadoc().toString()+"\r\n");
									TemporarySpace.setConsoleString(method.getJavadoc().toString()+"\r\n");
								}
							}
							for(int k = 0; k < newclass.getNewClass().getFields().length; k++){
								FieldDeclaration field = newclass.getNewClass().getFields()[k];
								output.write(field.toString()+"\r\n");
								TemporarySpace.setConsoleString(field.toString()+"\r\n");
															
							}
						}
					}
				
				
				
			
					output.close();
			         
			}
		//		ddList = this.generateDesignDicisionsFromChange();
				/*	for(int i =0; i < ddList.size(); i++){
					output.write("Design Decision "+ i +"　：　involving "+ ddList.get(i).getInvolvingClassList().size()+"  classes "+"\r\n");
					for(int j = 0; j < ddList.get(i).getInvolvingClassList().size(); j++){
						output.write("         class "+ j +"  : "+ddList.get(i).getInvolvingClassList().get(j).getClassa().getName()+"\r\n");
					}
				}*/
			
		  		  
		         
		      
		} catch (Exception e) {
		
			e.printStackTrace();
		    
		}
	}
	
	
	
	

}
