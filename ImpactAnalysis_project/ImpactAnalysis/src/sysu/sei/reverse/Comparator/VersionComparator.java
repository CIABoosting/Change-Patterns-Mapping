package sysu.sei.reverse.Comparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

//import com.sun.org.apache.bcel.internal.generic.NEW;
import com.troy.ui.util.TemporarySpace;

import sysu.sei.reverse.designDecision.ClassChange;
import sysu.sei.reverse.designDecision.MethodChange;
import sysu.sei.reverse.designDecision.VersionChange;


public class VersionComparator {

	//记录版本之前的变化
	private VersionChange change;
	
	public VersionChange getChange() {
		if(change == null){
			change = new VersionChange();
		}
		//change.printChange();
		return change;
	}


	public void setChange(VersionChange change) {
		this.change = change;
	}

// 比较两个不同路径project的项目修改情况，结果存储在change里面，所以函数中第一步首先将change清空
	public void compareProject(String fileLocation1, String fileLocation2){
		setChange (null);
		File file1 = new File(fileLocation1);
		File file2 = new File(fileLocation2);
		
		findProjectDCElement(file1, file2);
	//	System.out.println("can not find class ");
		findProjectNewElement(file1, file2);
	//	System.out.println("can not find class ");
		
		
	}
	
	//针对两个路径，查找新版本中删除的元素，以及修改的元素
	//结果记录在change里面
	public void findProjectDCElement(File file1, File file2){
		String targetPath = null; 
		if(file1.isDirectory()){
			File[] files1 = file1.listFiles();
			for(int i = 0 ; i < files1.length; i ++){
				if(files1[i].isFile() && files1[i].getName().endsWith("java")){
					targetPath = findFile(files1[i], file2);
					
					if(targetPath == null){
						//System.out.println("can not find class  :  "+ files1[i].getAbsolutePath());
						deletedUnits(getCompilationUnit(files1[i].getAbsolutePath()));
					}
					else{
						CompilationUnit unit1 = getCompilationUnit(files1[i].getAbsolutePath());
						CompilationUnit unit2 = getCompilationUnit(targetPath);
						compareUnits(unit1, unit2);					
					}
				}
				else {
					findProjectDCElement(files1[i],file2);
				}
			}
		}
		
	}
	//在两个文件路径，查找新版本中新增的类
	public void findProjectNewElement(File file1, File file2){
		String targetPath2 = null;
		if(file2.isDirectory()){
			File[] files2 = file2.listFiles();
			
			for(int i = 0; i < files2.length; i++){
			
				//System.out.println(files2.length+"  "+ i);
				if(files2[i].isFile()&& files2[i].getName().endsWith(".java")){
					targetPath2 = findFile(files2[i], file1);
					if(targetPath2 == null){
						newUnits(getCompilationUnit(files2[i].getAbsolutePath()));
						
//						System.out.println("adding new class ---------"+files2[i]);
					}
				}
				else{
					
						findProjectNewElement(file1,files2[i]);
					
				}
			}
			
		}
		
	}
	public void class2class(String path1, String path2) {
		newUnits(getCompilationUnit(path1));
		newUnits(getCompilationUnit(path2));
	}
	
	//在folder中查找与file同名的文件，如果找到返回同名文件的绝对路径，如果找不到返回null
	public String findFile(File file, File folder){
	    String targetLocation = null;
	   // System.out.println(folder.isFile());
	    if(folder.isDirectory()){
			File[] files = folder.listFiles();
			//System.out.println(files.length);
			for(int i = 0; i < files.length; i ++){
			//	System.out.println((file.getName().equals(files[i].getName()))+"   ；  "+file.getName()+"   vs   "+ files[i].getName());
				if((files[i].isFile())&& files[i].getName().endsWith(".java")&& (file.getName().equals(files[i].getName()))){
					
					if(getCompilationUnit(files[i].getAbsolutePath()).getPackage()!=null &&
							getCompilationUnit(file.getAbsolutePath()).getPackage()!=null&&
							getCompilationUnit(files[i].getAbsolutePath()).getPackage().getName().getFullyQualifiedName().equals(getCompilationUnit(file.getAbsolutePath()).getPackage().getName().getFullyQualifiedName()))
					
						targetLocation = files[i].getAbsolutePath();
					if(getCompilationUnit(files[i].getAbsolutePath()).getPackage()==null &&
							getCompilationUnit(file.getAbsolutePath()).getPackage()==null)
						targetLocation = files[i].getAbsolutePath();
				
				}
				if(targetLocation == null && files[i].isDirectory()){
					targetLocation = findFile(file , files[i]);

				}
			}
		}
		return targetLocation;
	}
	
//转换格式，从文件获得AST解析的CompilationUnit
	public CompilationUnit getCompilationUnit(String fileLocation){
		String content = "";   
		try {   
			File file = new File(fileLocation);
			BufferedReader reader = null;
			reader = new BufferedReader(new FileReader(file));
			String tempString ;
			while ((tempString = reader.readLine()) != null) {
	                
		          content = content+tempString;   
		          }
		} catch (IOException e) { 
			e.printStackTrace();   
		}  
		//创建解析器    
		ASTParser parsert = ASTParser.newParser(AST.JLS2);   
		//设定解析器的源代码字符    
		parsert.setSource(content.toCharArray());   
		//使用解析器进行解析并返回AST上下文结果(CompilationUnit为根节点)    
	//	System.out.println(fileLocation);
		CompilationUnit result = (CompilationUnit) parsert.createAST(null); 
		return result;
	}

	public void newUnits(CompilationUnit u){
		List types = u.types();
		for(int i = 0 ; i < types.size(); i ++){
		//	System.out.println("testing new file "+u.getPackage().getName()+"--"+((TypeDeclaration)types.get(i)).getName());
			
			
			if(u.getPackage()!=null){
				
				this.getChange().addAddedClass((TypeDeclaration) types.get(i), u.getPackage().getName().getFullyQualifiedName(), u.imports());
			}
			else{
				this.getChange().addAddedClass((TypeDeclaration) types.get(i), "", u.imports());
				
			}
		}                            
	}
	
	public void deletedUnits(CompilationUnit u){
		List types = u.types();
		for(int i = 0 ; i < types.size(); i ++){
			this.getChange().addDeletedClass((TypeDeclaration) types.get(i));
		}
	}
	
	
	//比较两个文件对应的类
	public void compareUnits(CompilationUnit u1, CompilationUnit u2){
		List types1 = u1.types();
		List types2 = u2.types();
		
		if(types1.size() != 0 && types2.size() != 0 ){
		///	System.out.println(types1.size()+"   "+ types2.size());
			for(int i = 0; i < types1.size(); i++){
				if(types1.get(i) instanceof TypeDeclaration){
					TypeDeclaration def1 = (TypeDeclaration) types1.get(i);
					for(int j = 0; j < types2.size(); j++){
						if(types2.get(j) instanceof TypeDeclaration){
							
							TypeDeclaration def2 = (TypeDeclaration) types2.get(j);
						//	System.out.println((def1.getName().equals(def2.getName().getFullyQualifiedName()))+"   "+def1.getName()+"    "+def2.getName());
							if( (u2.getPackage()== null && u1.getPackage()== null) &&
									def1.getName().getFullyQualifiedName().equals(def2.getName().getFullyQualifiedName())){
								//a.getFullyQualifiedName()
								
							    compareClassDef(def1, def2, "", "", u1.imports(), u2.imports());
							    if(def1.getTypes()!= null &&def2.getTypes()!= null &&def1.getTypes()!= null && def2.getTypes()!= null && def1.getTypes().length > 0 && def2.getTypes().length>0){
							    	//System.out.println("the number of class definitions inside the class :"+def1.getTypes().length+"  vs  "+def2.getTypes().length);
							    	for(int k =0; k < def1.getTypes().length; k++){
							    		for(int l = 0; l < def2.getTypes().length; l++){
							    			if(def1.getTypes()[k].getName().getFullyQualifiedName().equals(def2.getTypes()[l].getName().getFullyQualifiedName())){
							    				compareClassDef(def1.getTypes()[k], def2.getTypes()[l], "", "", u1.imports(), u2.imports());
							    				 def1.getTypes()[k].delete();
								    			    def2.getTypes()[l].delete();
								    			   // System.out.println(def1.getTypes().length+"   ");
									    			k=k-1;   
								    				l= def2.getTypes().length+1;
							    			}
							    		
							    		}
							    	}
							    }
						    	if(def1.getTypes()!= null &&def1.getTypes().length > 0){
							    	for(int k =0; k < def1.getTypes().length; k++){
							    		if(def1.getTypes()[k]!= null){
							    			this.getChange().addDeletedClass(def1.getTypes()[k]);
							    		}
							    	}
						    	}
						    	if(def2.getTypes()!= null &&def2.getTypes().length > 0){	
						    	    for(int l = 0; l < def2.getTypes().length; l++){	
							    		if(def2.getTypes()[l]!= null){
							    			this.getChange().addAddedClass(def2.getTypes()[l],"",u2.imports());
							    		}
							    	}
						    	}
						    	
							    
							    types1.remove(i);
								types2.remove(j);
								i=i-1;
								j=types2.size()+1;							
							}	
							if(( (u2.getPackage()!= null && u1.getPackage()!= null) &&
									u1.getPackage().getName().getFullyQualifiedName().equals( u2.getPackage().getName().getFullyQualifiedName()))&&
									def1.getName().getFullyQualifiedName().equals(def2.getName().getFullyQualifiedName())){
								//a.getFullyQualifiedName()
								
							    compareClassDef(def1, def2, u1.getPackage().getName().getFullyQualifiedName(), u2.getPackage().getName().getFullyQualifiedName(), u1.imports(), u2.imports());
							    if(def1.getTypes()!= null && def2.getTypes()!= null &&def1.getTypes().length > 0 && def2.getTypes().length>0){
							    //	System.out.println("the number of class definitions inside the class :"+def1.getTypes().length+"  vs  "+def2.getTypes().length);
								    //System.out.println(def1.getName()+"----"+def1.getTypes()+"----"+def2.getName()+"----"+def2.getTypes());
							    	for(int k =0; k < def1.getTypes().length; k++){
							    		for(int l = 0; l < def2.getTypes().length; l++){
							    			if(def1.getTypes()[k].getName().getFullyQualifiedName().equals(def2.getTypes()[l].getName().getFullyQualifiedName())){
							    				compareClassDef(def1.getTypes()[k], def2.getTypes()[l], u1.getPackage().getName().getFullyQualifiedName(), u2.getPackage().getName().getFullyQualifiedName(), u1.imports(), u2.imports());
							    				//System.out.print(def1.getTypes().length+"   vs");
							    			    def1.getTypes()[k].delete();
							    			    def2.getTypes()[l].delete();
							    			   // System.out.println(def1.getTypes().length+"   ");
								    			k=k-1;   
							    				l= def2.getTypes().length+1;
							    	//			System.out.print(def1.getTypes()[k].getName());
							    				
									    	//	System.out.print(def1.getTypes()[k]);
							    			}
							    		
							    		}
							    	}
							    }
							    if(def1.getTypes()!= null && def1.getTypes().length > 0){
							    	for(int k =0; k < def1.getTypes().length; k++){
							    		 System.out.println("deleted class inside: "+def1.getName()+"   "+def1.getTypes().length+"   ");
							    	//	if(def1.getTypes()[k].getFlags() != -1){
							    			this.getChange().addDeletedClass(def1.getTypes()[k]);
							    	//	}
							    	}
							    	
							    }
							    if(def2.getTypes()!= null &&def2.getTypes().length > 0){
							        for(int l = 0; l < def2.getTypes().length; l++){	
							    	//	if(def2.getTypes()[l].getFlags() !=-1){
							       	 System.out.println("adding class inside: "+def2.getName()+"   "+def2.getTypes().length+"   ");
								    	
							    			this.getChange().addAddedClass(def2.getTypes()[l],u2.getPackage().getName().getFullyQualifiedName(),u2.imports());
							    	//	}
							    
							    	}
							    }
							    types1.remove(i);
								types2.remove(j);
								i=i-1;
								j=types2.size()+1;							
							}		
						}
					}					
				}
			}
		}
		if(types1.size()>0){
			for(int i = 0; i < types1.size(); i++){
			//	System.out.print("deleted class: "+( (TypeDeclaration) types1.get(i)).getName().getFullyQualifiedName()+"   in  "+u1.getPackage());
				TemporarySpace.setConsoleString("deleted class: "+( (TypeDeclaration) types1.get(i)).getName().getFullyQualifiedName()+"   in  "+u1.getPackage());
				this.getChange().addDeletedClass((TypeDeclaration) types1.get(i));
			}
		}
		
		
		if(types2.size()>0){
			for(int i = 0; i < types2.size(); i++){
				
				//System.out.println("testing "+i+" :++++ "+((TypeDeclaration)types2.get(i)).getName()+"   in  "+u2.getPackage()+u2);
				TemporarySpace.setConsoleString("testing "+i+" :"+((TypeDeclaration)types2.get(i)).getName()+"   in  "+u2.getPackage());
				if(u2.getPackage() != null && u2.getPackage().getName()!=null)
					this.getChange().addAddedClass((TypeDeclaration) types2.get(i), u2.getPackage().getName().getFullyQualifiedName(), u2.imports());
				else 
					this.getChange().addAddedClass((TypeDeclaration) types2.get(i), "", u2.imports());
					
			}
		}
	}
	
	public void compareClassDef(TypeDeclaration def1, TypeDeclaration def2, String oldPackageName, String newPackageName,List<ImportDeclaration> oldImportList, List<ImportDeclaration> newImportList){
		MethodDeclaration[] methods1 = def1.getMethods();
		MethodDeclaration[] methods2 = def2.getMethods();
		
		FieldDeclaration[] fields1 = def1.getFields();
		FieldDeclaration[] fields2 = def2.getFields();
		
		
		ClassChange classChange = null;
		//System.out.println(def1.getName() + "  :  "+ methods1.length+"-------"+def1.getName() + "  :  "+ methods2.length);
		//System.out.println(def1.getFields().length + "  "+ def1.getFields().toString());
		if(methods1.length>0 && methods2.length >0){
			for(int i = 0; i < methods1.length; i++){
				MethodDeclaration m1 = methods1[i];
			//	System.out.println(m1.getName());
				for(int j = 0; j < methods2.length; j++){
					MethodDeclaration m2 = methods2[j];
					if((m1 != null)&&(m2 != null) &&
							(m1.getName().getFullyQualifiedName().equals(m2.getName().getFullyQualifiedName()))
							&&(m1.parameters().size() == m2.parameters().size())
							&& (m1.getReturnType().toString().equals(m2.getReturnType().toString()))
							&& ifTheSameParameterList(m1,m2)){

						methods1[i] = null;						
						methods2[j] = null;
						//System.out.println(m1.getName()+"------"+m1.getBody());
						//System.out.println(m1.getName()+"------"+m2.getBody());
						//函数内容一样，或者都为空
						if((m1.getBody() != null && m2.getBody() != null)){
							if(!m1.getBody().toString().equals(m2.getBody().toString())){
								//System.out.println(def1.getName()+"========"+m1.getName()+"==========="+m1.getBody());
								if(classChange == null){
									classChange = new ClassChange(def1, def2,oldPackageName, newPackageName,oldImportList, newImportList);								
								}
								classChange.addChangedMethod(new MethodChange(m1,m2));																	
							}
						}
						else{
							if(!(m1.getBody() == null&&m2.getBody() == null)){
							//	System.out.println(def1.getName()+"========"+m1.getName()+"==========="+m1.getBody());
								if(classChange == null){
									classChange = new ClassChange(def1, def2 ,oldPackageName, newPackageName,oldImportList, newImportList);								
								}
								classChange.addChangedMethod(new MethodChange(m1,m2));	
							}
						}
						m1 = null;
					}
						
				}
				
			}
			
		}
		for(int i = 0 ; i < methods1.length; i++){
		   if(methods1[i] != null){
			   if(classChange == null){
					classChange = new ClassChange(def1, def2, oldPackageName, newPackageName,oldImportList, newImportList);								
				}
			   classChange.addDeletedMethod(methods1[i]);
		   }
		}   
		for(int i = 0 ; i < methods2.length; i++){
			   if(methods2[i] != null){
				   if(classChange == null){
						classChange = new ClassChange(def1, def2, oldPackageName, newPackageName,oldImportList, newImportList);								
					}
				   classChange.addAddedMethod(methods2[i]);
			   }
			}   
				
		if(fields1.length>0 && fields2.length >0){
			for(int i = 0; i < fields1.length; i++){
				FieldDeclaration f1 = fields1[i];
				boolean tag = false;
				for(int j = 0; j < fields2.length; j++){
					FieldDeclaration f2 = fields2[j];
					if((f2 != null) && (f1.getType().toString().equals(f2.getType().toString()))&& 
							(f1.fragments().size()>0)&& (f2.fragments().size()>0)){
						for(int p = 0; p <f1.fragments().size(); p ++){
							VariableDeclarationFragment frag1 = (VariableDeclarationFragment) f1.fragments().get(p);
							for(int q = 0;q <f2.fragments().size(); q ++){
								VariableDeclarationFragment frag2= (VariableDeclarationFragment) f2.fragments().get(q);
								if(frag1.getName().getFullyQualifiedName().equals(frag2.getName().getFullyQualifiedName())){
									tag = true;
									f2.fragments().remove(q);
								}
							}
						}
					}
				}
				if(tag == false){
					if(classChange == null){
						classChange = new ClassChange(def1, def2,oldPackageName, newPackageName,oldImportList, newImportList);								
					}
					classChange.addDeletedField(f1);
				}
				
			}
			for(int j = 0; j < fields2.length; j++){
				if(fields2[j].fragments().size() > 0){
					if(classChange == null){
						classChange = new ClassChange(def1, def2, oldPackageName, newPackageName,oldImportList, newImportList);								
					}
					classChange.addAddedField(fields2[j]);
				}
			}
			
		}
		if(classChange != null){
			getChange().addClassChange(classChange);
		}
	
	}
	private boolean ifTheSameParameterList(MethodDeclaration m1, MethodDeclaration m2){
		boolean tag = true;
		List pList1 = m1.parameters();
		List pList2 = m2.parameters();
		if(pList1.size()== pList2.size() && pList1.size() >0){
			for(int i =0; i < pList1.size(); i++){
				if((pList1.get(i) instanceof SingleVariableDeclaration)&&
						(pList2.get(i) instanceof SingleVariableDeclaration)){
					if(!(( SingleVariableDeclaration)(pList1.get(i) )).getType().toString().equals((( SingleVariableDeclaration)(pList2.get(i))).getType().toString())){
						tag = false;
					}
				}
			}
		}
		return tag;
	}
	
	public static void main(String[] args) {
		VersionComparator comparator = new VersionComparator();
	//comparator.compareProject("D:/2013/workspace/case study reverse/jfreechart-1.0.12/source/org/jfree/chart/axis", "D:/2013/workspace/case study reverse/jfreechart-1.0.13/source/org/jfree/chart/axis");
	//System.out.println(comparator.findFile(new File("H:/case study reverse/jfreechart-1.0.12/source/org/jfree/chart/axis/Axis.java"),new File( "H:/case study reverse/jfreechart-1.0.13/source")));
		
	//	FileFilter filter = new FileFilter();
	//	filter.filteringAnnotation4file(new File ("D:/2013/workspace/case study reverse/jfreechart-1.0.12/source/org/jfree/data/xy/XYSeries.java"));
		//filter.filteringAnnotation4file(new File ("D:/2013/workspace/case study reverse/jfreechart-1.0.13/source/org/jfree/data/xy/XYSeries.java"));
		
	//	filter.filteringAnnotation4folder("D:/2013/workspace/case study reverse/jfreechart-1.0.12/source");
	//	filter.filteringAnnotation4folder("D:/2013/workspace/case study reverse/jfreechart-1.0.13/source");
	//	comparator.compareUnits(comparator.getCompilationUnit("D:/2013/workspace/case study reverse/jfreechart-1.0.12/source/org/jfree/data/xy/XYSeries.java"), 
		//		comparator.getCompilationUnit("D:/2013/workspace/case study reverse/jfreechart-1.0.13/source/org/jfree/data/xy/XYSeries.java"));
		
		//CompilationUnit a = comparator.getCompilationUnit("D:/2013/workspace/case study reverse/jfreechart-1.0.13/source/org/jfree/data/xy/XYSeries.java");
	//	CompilationUnit a = comparator.getCompilationUnit("D:/a/a.java" );
	//	TypeDeclaration b = (TypeDeclaration) a.types().get(0);
	//	System.out.println(b.getName());
	//	List e = b.bodyDeclarations();
	//	MethodDeclaration[] c = b.getMethods();
		
	//	System.out.println(c.length);
	//	for(int i = 0 ; i <c.length; i++){
	//		System.out.println(c[i].getName()+"  ");
	//	}
	//	ChildListPropertyDescriptor d = b.getBodyDeclarationsProperty();
	//	System.out.println(a.getPackage());
	//	comparator.compareProject("D:/2013/workspace/case study reverse/jfreechart-1.0.12/source/", 
	//			"D:/2013/workspace/case study reverse/jfreechart-1.0.13/source/");
		VersionComparator versionComparator = new VersionComparator();
		List<String> commitpath = versionComparator.getCommitPath();    //获取所有提交的路径
		FileFilter filter = new FileFilter();
		String voidFolder = "E:\\ImpactAnalysis\\实验\\jedit\\test";
		int i = 0;
		for(String path:commitpath) {
			path = "E:\\ImpactAnalysis\\实验\\jedit\\source\\jedit3.2.0source\\jEdit";
			System.out.println("remain " + Integer.toString(commitpath.size() - i) + "commits");
			String LogPath = path + "\\CouplingReult.txt";
			File logfile = new File(LogPath);
			if(!logfile.exists()) {
				filter.filteringAnnotation4folder(path + "\\old\\src");
				comparator.compareProject(voidFolder, path + "\\old\\src");
				System.out.println("Computing " + path);
				System.out.println(" ");
				comparator.getChange().writeLog(LogPath);
			}
			i++;
			break;
		}
//	   String[] paths = {"E:\\ImpactAnalysis\\实验\\jedit\\test",
//	   	 "E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2147\\old\\src"};
//		if(paths.length >1){
//		  filter.filteringAnnotation4folder(paths[0]);
//		  for(int i = 1; i < paths.length; i ++){
//				filter.filteringAnnotation4folder(paths[i]);
//				comparator.compareProject(paths[i-1],paths[i]); 
//				System.out.println("Computing " + paths[i]);
//				comparator.getChange().writeLog("E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2147\\test.txt");
//				System.out.println(" ");		
//		  }
//		}	


	}
	public List<String> getCommitPath() {
		List<String> CommitPath = new ArrayList<String>();
		File project = new File("E:\\ImpactAnalysis\\实验\\jedit\\commit");
		File[] ProjVersion = project.listFiles();
		for(File f : ProjVersion) {
			File version = new File(f.getAbsolutePath());
			File[] commits = version.listFiles();
			for(File commt: commits) {
				CommitPath.add(commt.getAbsolutePath());			
			}
		}
		return CommitPath;
	}
}
