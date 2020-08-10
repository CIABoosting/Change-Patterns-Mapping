package sysu.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.tool.CodeMatcher;

public class VersionChange {
	private int version = 0;
	private List<ClassChange> addedClassList;
	private List<ClassChange> changedClassList;
	private List<TypeDeclaration> deletedClassList;

	private List<ClassChange> acClassList;

	public List<ClassChange> getAcClassList() {

		acClassList = new ArrayList<ClassChange>();

		if (this.getAddedClassList().size() > 0 || this.getChangedClassList().size() > 0) {
			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				acClassList.add(this.getAddedClassList().get(i));
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				acClassList.add(this.getChangedClassList().get(i));
			}
		}

		return acClassList;
	}

	public void setAcClassList(List<ClassChange> acClassList) {
		this.acClassList = acClassList;
	}

	public Integer[] getCochangeRecord() {
		List<ClassChange> relatedClasses = this.getAcClassList();
		Integer[] cochangeRecord = new Integer[100];
		for (int i = 0; i < 100; i++) {
			cochangeRecord[i] = new Integer(0);
			for (int j = 0; j < relatedClasses.size(); j++) {
				for (int k = 0; k < relatedClasses.get(j).getRelationship().size(); k++) {
					cochangeRecord[i] = cochangeRecord[i] + relatedClasses.get(j).getRelationship().get(k)[i];
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
		if (addedClassList == null) {
			addedClassList = new ArrayList<ClassChange>();
		}
		return addedClassList;
	}

	public void setAddedClassList(List<ClassChange> addedClassList) {
		this.addedClassList = addedClassList;
	}

	public List<ClassChange> getChangedClassList() {
		if (changedClassList == null) {
			changedClassList = new ArrayList<ClassChange>();
		}
		return changedClassList;
	}

	public void setChangedClassList(List<ClassChange> changedClassList) {
		this.changedClassList = changedClassList;
	}

	public List<TypeDeclaration> getDeletedClassList() {
		if (deletedClassList == null) {
			deletedClassList = new ArrayList<TypeDeclaration>();
		}
		return deletedClassList;
	}

	public void setDeletedClassList(List<TypeDeclaration> deletedClassList) {
		this.deletedClassList = deletedClassList;
	}

	public void addClassChange2(CompilationUnit oldUnit, CompilationUnit newUnit, TypeDeclaration oldClass,
			TypeDeclaration newClass, String oldPackageName, String newPackageName,
			List<ImportDeclaration> oldImportList, List<ImportDeclaration> newImportList, String oldClassPath,
			String newClassPath) {
		ClassChange change = new ClassChange(oldUnit, newUnit, oldClass, newClass, oldPackageName, newPackageName,
				oldImportList, newImportList, oldClassPath, newClassPath);
		this.getChangedClassList().add(change);

	}

	public void addAddedClass(CompilationUnit newUnit, TypeDeclaration newClass, String newPackageName,
			List<ImportDeclaration> newImportList, String newClassPath) {
		ClassChange newChange = new ClassChange(null, newUnit, null, newClass, null, newPackageName, null,
				newImportList, null, newClassPath);
		this.getAddedClassList().add(newChange);
	}

	public void addDeletedClass(TypeDeclaration deletedClass) {
		this.getDeletedClassList().add(deletedClass);
	}

	public void addClassChange(ClassChange classChange) {
		this.getChangedClassList().add(classChange);

	}

	public void compareChangType() {

		List<ClassChange> relatedClasses = this.getAcClassList();
		if (relatedClasses.size() > 0) {

			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				this.getAddedClassList().get(i).setId(i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for (int j = 0; j < relatedClasses.size(); j++) {
					Integer[] r = new Integer[100];
					for (int k = 0; k < 100; k++) {
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getAddedClassList().get(i).setRelationship(relationship);
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				this.getChangedClassList().get(i).setId(this.getAddedClassList().size() + i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for (int j = 0; j < relatedClasses.size(); j++) {
					Integer[] r = new Integer[100];
					for (int k = 0; k < 100; k++) {
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getChangedClassList().get(i).setRelationship(relationship);
			}

			for (int i = 0; i < relatedClasses.size(); i++) {
				for (int j = 0; j < relatedClasses.size(); j++) {
					if (i != j) {
						ifChangeRelated(relatedClasses.get(i), relatedClasses.get(j));
					}
				}
			}
		}

	}

	public void compareChangType4Direct60(String savePath) {

		List<ClassChange> relatedClasses = this.getAcClassList();
		if (relatedClasses.size() > 0) {

			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				this.getAddedClassList().get(i).setId(i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for (int j = 0; j < relatedClasses.size(); j++) {
					Integer[] r = new Integer[100];
					for (int k = 0; k < 100; k++) {
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getAddedClassList().get(i).setRelationship(relationship);
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				this.getChangedClassList().get(i).setId(this.getAddedClassList().size() + i);
				ArrayList<Integer[]> relationship = new ArrayList<Integer[]>();
				for (int j = 0; j < relatedClasses.size(); j++) {
					Integer[] r = new Integer[100];
					for (int k = 0; k < 100; k++) {
						r[k] = new Integer(0);
					}
					relationship.add(r);
				}
				this.getChangedClassList().get(i).setRelationship(relationship);
			}

			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				for (int j = 0; j < this.getAddedClassList().size(); j++) {
					if (i != j) {
						this.compareRelationship60(this.getAddedClassList().get(i), this.getAddedClassList().get(j),
								false, false,savePath);
					}
				}
				for (int j = 0; j < this.getChangedClassList().size(); j++) {
					this.compareRelationship60(this.getAddedClassList().get(i), this.getChangedClassList().get(j),
							false, true,savePath);
				}
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				for (int j = 0; j < this.getAddedClassList().size(); j++) {
					this.compareRelationship60(this.getChangedClassList().get(i), this.getAddedClassList().get(j), true,
							false,savePath);
				}
				for (int j = 0; j < this.getChangedClassList().size(); j++) {
					if (i != j) {
						this.compareRelationship60(this.getChangedClassList().get(i), this.getChangedClassList().get(j),
								true, true,savePath);

					}
				}
			}
		}

	}

	public List<ClassChange> findRelatedClass(ClassChange c) {

		List<ClassChange> relatedClasses = new ArrayList<ClassChange>();
		List<ClassChange> tempDirectRelatedClasses;
		relatedClasses.add(c);
		if (this.getAddedClassList().size() > 0 || this.getChangedClassList().size() > 0) {
			for (int i = 0; i < relatedClasses.size(); i++) {
				tempDirectRelatedClasses = findDirectRelatedClass(relatedClasses.get(i));
				for (int j = 0; j < tempDirectRelatedClasses.size(); j++) {
					boolean tag = false;
					for (int k = 0; k < relatedClasses.size(); k++) {
						if ((relatedClasses.get(k).getNewClass().getName().getFullyQualifiedName().equals(
								tempDirectRelatedClasses.get(j).getNewClass().getName().getFullyQualifiedName()))
								&& relatedClasses.get(k).getNewPackageName()
										.equals(tempDirectRelatedClasses.get(j).getNewPackageName())) {
							tag = true;

						}

					}
					if (tag == false) {
						relatedClasses.add(tempDirectRelatedClasses.get(j));
					}
				}
			}
		}
		return relatedClasses;
	}

	public List<ClassChange> findDirectRelatedClass(ClassChange c) {

		int relationc = 0;
		int relation4c = 0;
		List<ClassChange> relatedClasses = new ArrayList<ClassChange>();
		if (this.getAddedClassList().size() > 0 || this.getChangedClassList().size() > 0) {
			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				ClassChange newClass = this.getAddedClassList().get(i);
				relationc = ifChangeRelated(c, newClass);
				relation4c = ifChangeRelated(newClass, c);
				if (relationc < 0 || relation4c < 0) {
					relatedClasses.add(newClass);
				}
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				ClassChange newClass = this.getChangedClassList().get(i);
				relationc = ifChangeRelated(c, newClass);
				relation4c = ifChangeRelated(newClass, c);
				if (relationc < 0 || relation4c < 0) {
					relatedClasses.add(newClass);
				}
			}
		}
		return relatedClasses;
	}

	@SuppressWarnings("deprecation")
	public int ifChangeRelated(ClassChange class1, ClassChange class2) {
		int relation = 0;
		boolean ifChangedClass1 = false;
		boolean ifChangedClass2 = false;

		String className2 = class2.getNewClass().getName().getFullyQualifiedName();
		TypeDeclaration classDef1 = class1.getNewClass();
		TypeDeclaration classDef2 = class2.getNewClass();

		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<String> newCList = new ArrayList<String>();
		ArrayList<String> pList = new ArrayList<String>();

		for (int i = 0; i < this.getChangedClassList().size(); i++) {
			if (this.getChangedClassList().get(i).getNewClass().getName().getFullyQualifiedName()
					.equals(classDef1.getName().getFullyQualifiedName())) {
				ifChangedClass1 = true;
			}
			if (this.getChangedClassList().get(i).getNewClass().getName().getFullyQualifiedName()
					.equals(classDef2.getName().getFullyQualifiedName())) {
				ifChangedClass2 = true;
			}
		}
		class1.initializeRelationship(class2.getId());

		if (ifChangedClass1 == true) {
			for (int j = 0; j < class1.getAddedFields().size(); j++) {
				if (class1.getAddedFields().get(j).getType().toString().equals(className2)) {
					relation = 3;
					@SuppressWarnings("rawtypes")
					List fragmentList = class1.getAddedFields().get(j).fragments();
					for (int k = 0; k < fragmentList.size(); k++) {

						attributeList.add(fragmentList.get(k).toString());
					}
					class1.addRevisionRelationship(class2.getId(), relation);
				}
			}

			for (int j = 0; j < class1.getAddedMethods().size(); j++) {
				if (class1.getAddedMethods().get(j) != null && class1.getAddedMethods().get(j).getBody() != null) {
					MethodDeclaration method1 = class1.getAddedMethods().get(j);
					@SuppressWarnings("rawtypes")
					List paraList = method1.parameters();
					if (paraList.size() > 0) {
						for (int k = 0; k < paraList.size(); k++) {
							String typeName = ((SingleVariableDeclaration) paraList.get(k)).getType().toString();
							String paraName = ((SingleVariableDeclaration) paraList.get(k)).getName()
									.getFullyQualifiedName();
							if (typeName.equals(className2)) {
								relation = 1;
								pList.add(paraName);
								class1.addRevisionRelationship(class2.getId(), relation);
							}
						}
					}
					if (method1.getReturnType2().toString().equals(className2)) {
						relation = 12;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					@SuppressWarnings("unchecked")
					List<Statement> statements = class1.getAddedMethods().get(j).getBody().statements();
					for (int m = 0; m < statements.size(); m++) {
						String sm = statements.get(m).toString();
						if (CodeMatcher.ifWMinstanceof(sm, className2)) {
							relation = 10;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if (CodeMatcher.ifWMuseclassdef(sm, className2)) {
							relation = 15;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if (ifChangedClass2) {
							for (int k = 0; k < class2.getAddedMethods().size(); k++) {
								String mName = CodeMatcher.useofMethod(sm,
										class2.getAddedMethods().get(k).getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
							for (int k = 0; k < class2.getChangedMethods().size(); k++) {
								String mName = CodeMatcher.useofMethod(sm, class2.getChangedMethods().get(k)
										.getNewMethod().getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						} else {
							for (int k = 0; k < class2.getNewClass().getMethods().length; k++) {
								String mName = CodeMatcher.useofMethod(sm,
										class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
					}
				}

			}

			for (int j = 0; j < class1.getChangedMethods().size(); j++) {
				if ((!class1.getChangedMethods().get(j).getNewMethod().getName().getFullyQualifiedName().equals("main"))
						&& (class1.getChangedMethods().get(j) != null
								&& class1.getChangedMethods().get(j).getNewMethod().getBody() != null)) {
					@SuppressWarnings("rawtypes")
					List paraList = class1.getChangedMethods().get(j).getNewMethod().parameters();
					MethodDeclaration method1 = class1.getChangedMethods().get(j).getNewMethod();
					if (paraList.size() > 0) {
						for (int k = 0; k < paraList.size(); k++) {
							String typeName = ((SingleVariableDeclaration) paraList.get(k)).getType().toString();
							String paraName = ((SingleVariableDeclaration) paraList.get(k)).getName()
									.getFullyQualifiedName();
							if (typeName.equals(className2)) {
								relation = 1;
								pList.add(paraName);
								class1.addRevisionRelationship(class2.getId(), relation);
							}
						}
					}
					if (method1.getReturnType2().toString().equals(className2)) {
						relation = 12;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					@SuppressWarnings("unchecked")
					List<Statement> statements = class1.getChangedMethods().get(j).getNewMethod().getBody()
							.statements();
					for (int m = 0; m < statements.size(); m++) {
						String sm = statements.get(m).toString();
						if (CodeMatcher.ifWMinstanceof(sm, className2)) {
							relation = 10;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if (CodeMatcher.ifWMuseclassdef(sm, className2)) {
							relation = 15;
							class1.addRevisionRelationship(class2.getId(), relation);
						}
						if (ifChangedClass2) {
							for (int k = 0; k < class2.getAddedMethods().size(); k++) {
								String mName = CodeMatcher.useofMethod(sm,
										class2.getAddedMethods().get(k).getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
							for (int k = 0; k < class2.getChangedMethods().size(); k++) {
								String mName = CodeMatcher.useofMethod(sm, class2.getChangedMethods().get(k)
										.getNewMethod().getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						} else {
							for (int k = 0; k < class2.getNewClass().getMethods().length; k++) {
								String mName = CodeMatcher.useofMethod(sm,
										class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());
								if (mName != null) {
									if (CodeMatcher.methodListInclude(pList, mName)) {
										relation = 2;
										class1.addRevisionRelationship(class2.getId(), relation);
										class1.deleteRevisionRelationship(class2.getId(), relation - 1);
									}
									if (CodeMatcher.methodListInclude(newCList, mName)) {
										relation = 6;
										class1.addRevisionRelationship(class2.getId(), relation);
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (relation == 0) {
				relation = ifDirectRelated(class1, class2, ifChangedClass2);
			}

		}
		if ((classDef1.getSuperclass() != null) && (classDef1.getSuperclass().getFullyQualifiedName().equals(className2)
				|| (class2.getNewClass().getSuperclass() != null && classDef1.getSuperclass().getFullyQualifiedName()
						.equals(class2.getNewClass().getSuperclass().getFullyQualifiedName())))) {
			relation = 13;
			class1.addRevisionRelationship(class2.getId(), relation);
		}

		if (ifTheSameInterface(classDef1, class2.getNewClass())) {
			relation = 14;
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		return relation;

	}

	public int getchangetype(boolean ifChanged1, boolean ifChanged2) {
		int newVSnew = 0;
		int newVSchanged = 1;
		int changedVSnew = 2;
		int changedVSchanged = 3;
		int changeType = 0;
		if ((!ifChanged1) && (!ifChanged2))
			changeType = newVSnew;
		if ((!ifChanged1) && (ifChanged2))
			changeType = newVSchanged;
		if ((ifChanged1) && (!ifChanged2))
			changeType = changedVSnew;
		if (ifChanged1 && ifChanged2)
			changeType = changedVSchanged;
		return changeType;

	}

	public int compareRelationship60(ClassChange class1, ClassChange class2, boolean ifChangedClass1,
			boolean ifChangedClass2,String savePath) {

		int relation = 0;

		String className1 = class1.getNewClass().getName().getFullyQualifiedName();
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();

		ArrayList<String> log1 = new ArrayList<String>();
		ArrayList<String> log2 = new ArrayList<String>();
		ArrayList<String> log3 = new ArrayList<String>();
		ArrayList<String> log4 = new ArrayList<String>();
		ArrayList<String> log5 = new ArrayList<String>();
		ArrayList<String> log6 = new ArrayList<String>();
		ArrayList<String> log7 = new ArrayList<String>();
		ArrayList<String> log8 = new ArrayList<String>();
		ArrayList<String> log9 = new ArrayList<String>();
		ArrayList<String> log10 = new ArrayList<String>();
		ArrayList<String> log11 = new ArrayList<String>();
		ArrayList<String> log12 = new ArrayList<String>();
		ArrayList<String> log13 = new ArrayList<String>();
		ArrayList<String> log14 = new ArrayList<String>();
		ArrayList<String> log15 = new ArrayList<String>();
		ArrayList<String> log16 = new ArrayList<String>();
		ArrayList<String> log17 = new ArrayList<String>();
		ArrayList<String> log18 = new ArrayList<String>();
		ArrayList<String> log19 = new ArrayList<String>();

		TypeDeclaration classDef1 = class1.getNewClass();
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<String> mAttributeList = new ArrayList<String>();
		ArrayList<String> pList = new ArrayList<String>();

		List<MethodDeclaration> methodList1, methodList2;
		List<FieldDeclaration> fieldList1, fieldList2;
		methodList1 = new ArrayList<MethodDeclaration>();
		fieldList1 = new ArrayList<FieldDeclaration>();
		methodList2 = new ArrayList<MethodDeclaration>();
		fieldList2 = new ArrayList<FieldDeclaration>();
		int numberofNewMethod1 = 0;
		int numberofNewMethod2 = 0;

		if (ifChangedClass1 == true) {
			numberofNewMethod1 = class1.getAddedMethods().size();
			for (int i = 0; i < class1.getAddedMethods().size(); i++) {
				methodList1.add(class1.getAddedMethods().get(i));

			}
			for (int i = 0; i < class1.getChangedMethods().size(); i++) {
				methodList1.add(class1.getChangedMethods().get(i).getNewMethod());
			}
			for (int i = 0; i < class1.getNewClass().getFields().length; i++) {
				fieldList1.add(class1.getNewClass().getFields()[i]);
			}
		} else {
			numberofNewMethod1 = class1.getNewClass().getMethods().length;
			for (int i = 0; i < class1.getNewClass().getMethods().length; i++) {
				methodList1.add(class1.getNewClass().getMethods()[i]);

			}
			for (int i = 0; i < class1.getNewClass().getFields().length; i++) {
				fieldList1.add(class1.getNewClass().getFields()[i]);
			}
		}

		if (ifChangedClass2 == true) {
			numberofNewMethod2 = class2.getAddedMethods().size();
			for (int i = 0; i < class2.getAddedMethods().size(); i++) {
				methodList2.add(class2.getAddedMethods().get(i));

			}
			for (int i = 0; i < class2.getChangedMethods().size(); i++) {
				methodList2.add(class2.getChangedMethods().get(i).getNewMethod());
			}
			for (int i = 0; i < class2.getAddedFields().size(); i++) {
				fieldList2.add(class2.getAddedFields().get(i));
			}
		} else {
			numberofNewMethod2 = class2.getNewClass().getMethods().length;
			for (int i = 0; i < class2.getNewClass().getMethods().length; i++) {
				methodList2.add(class2.getNewClass().getMethods()[i]);

			}
			for (int i = 0; i < class2.getNewClass().getFields().length; i++) {
				fieldList2.add(class2.getNewClass().getFields()[i]);
			}
		}

		if (!class1.getNewClass().getName().toString().equals(class2.getNewClass().getName().toString())) {
			for (MethodDeclaration method1 : class1.getNewClass().getMethods()) {
				List<String> vars1 = new ArrayList<String>();

				if (method1.getBody() != null) {

					vars1 = CodeMatcher.ifnewPara2s(method1.getBody().toString(),
							class2.getNewClass().getName().toString());

					String[] temps;
					for (Object para : method1.parameters()) {
						temps = para.toString().split(" ");
						if (temps[0].equals(class2.getNewClass().getName().toString())) {
							vars1.add(temps[1]);
						}
					}

					String classname;
					String varname;
					FieldDeclaration[] fields = class1.getNewClass().getFields();
					for (FieldDeclaration field : fields) {
						classname = field.getType().toString();
						if (field.fragments().size() > 0) {
							if (field.fragments().get(0).toString().indexOf("=") != -1) {
								varname = field.fragments().get(0).toString().substring(0,
										field.fragments().get(0).toString().indexOf("="));

							} else {
								varname = field.fragments().get(0).toString();
							}
							if (classname.equals(class2.getNewClass().getName().toString())) {
								vars1.add(varname);
							}
						}
					}
					List<MethodDeclaration> acMethodList = new ArrayList<MethodDeclaration>();
					for (MethodDeclaration method : class2.getAddedMethods()) {
						acMethodList.add(method);
					}
					for (MethodChange cmethod : class2.getChangedMethods()) {
						MethodDeclaration method = cmethod.getNewMethod();
						acMethodList.add(method);
					}

					for (MethodDeclaration method2 : acMethodList) {
						String var2 = CodeMatcher.useofMethod(method1.getBody().toString(),
								method2.getName().getFullyQualifiedName(), method1.parameters().size());
						for (String var : vars1) {
							if (var != null && var2 != null) {
								if (var.equals(var2)) {
									class1.addRevisionRelationship(class2.getId(), 46);
								}
							}
							if (var2 != null) {
								if (var2.equals(class2.getNewClass().getName().toString())) {
									class1.addRevisionRelationship(class2.getId(), 46);
								}
							}
						}
					}
				}
				if (class1.getRelationship().get(class2.getId())[46] == 1) {
					break;
				}
			}
		}

		if (classDef1.getSuperclassType() != null && classDef1.getSuperclassType().toString().equals(className2)) {
			relation = 1 + getchangetype(ifChangedClass1, ifChangedClass2);
			log1.add("Class" + className1 + " extends " + className2 + "\r\n");
			class1.addRevisionRelationship(class2.getId(), relation);
		}

		@SuppressWarnings("rawtypes")
		List interfaceList1 = classDef1.superInterfaceTypes();
		if (classDef1.superInterfaceTypes() != null && classDef1.superInterfaceTypes().size() > 0) {
			for (int i = 0; i < interfaceList1.size(); i++) {
				Object interface1 = interfaceList1.get(i);
				if (interface1 instanceof SimpleName) {
					if (((SimpleName) interface1).getFullyQualifiedName().equals(className2)) {
						relation = 1 + getchangetype(ifChangedClass1, ifChangedClass2);
						log2.add("Class" + class1.getId() + ": " + className1 + " implements " + className2 + "\r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
				}
			}
		}
		for (int i = 0; i < fieldList1.size(); i++) {
			if (fieldList1.get(i).getType().toString().equals(className2)) {
				@SuppressWarnings("rawtypes")
				List fragmentList = fieldList1.get(i).fragments();
				for (int k = 0; k < fragmentList.size(); k++) {
					if (CodeMatcher.getVariable(fragmentList.get(k).toString()).length() > 0)
						attributeList.add(CodeMatcher.getVariable(fragmentList.get(k).toString()));
				}
				relation = 18;
				log18.add("Class" + class1.getId() + ": " + className1 + " defined a variable of the type:  "
						+ className2 + getTag(ifChangedClass2) + "\r\n");
				log18.add("Definition: " + fieldList1.get(i).toString() + "\r\n");
				class1.addRevisionRelationship(class2.getId(), relation);
			}
		}

		for (int j = 0; j < methodList1.size(); j++) {
			if (methodList1.get(j) != null && methodList1.get(j).getBody() != null) {
				@SuppressWarnings("rawtypes")
				List paraList = methodList1.get(j).parameters();
				MethodDeclaration method1 = methodList1.get(j);
				mAttributeList = new ArrayList<String>();

				pList = new ArrayList<String>();

				if (paraList.size() > 0) {
					for (int k = 0; k < paraList.size(); k++) {
						String typeName = ((SingleVariableDeclaration) paraList.get(k)).getType().toString();
						String paraName = ((SingleVariableDeclaration) paraList.get(k)).getName()
								.getFullyQualifiedName();
						if (typeName.equals(className2)) {
							relation = 19;
							log19.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1)
									+ ", method " + getTag(j >= numberofNewMethod1) + method1.getName()
									+ ", with parameter " + paraName + " of the type " + className2
									+ getTag(ifChangedClass2) + "\r\n");
							if (paraName.length() > 0)
								pList.add(paraName);
							class1.addRevisionRelationship(class2.getId(), relation);
						}
					}
				}

				if (method1.getReturnType2() != null) {
					if (method1.getReturnType2().toString().equals(className2)) {
						relation = 5;
						log5.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", with return type "
								+ className2 + getTag(ifChangedClass2) + "\r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
				}

				if (method1.thrownExceptions() != null && method1.thrownExceptions().size() > 0) {
					for (int k = 0; k < method1.thrownExceptions().size(); k++) {
						if (method1.thrownExceptions().get(k).toString().indexOf(className2) >= 0) {
							relation = 7;
							log7.add("Class" + class1.getId() + getTag(ifChangedClass1) + ": " + className1
									+ ", method " + method1.getName() + getTag(j >= numberofNewMethod1)
									+ ", throw exception " + className2 + getTag(ifChangedClass2) + "\r\n");
							class1.addRevisionRelationship(class2.getId(), relation);

						}
					}
				}
				String statements = methodList1.get(j).getBody().toString();
				while (statements.indexOf("\n") > 0 && statements.length() > 0) {

					String sm = statements.substring(0, statements.indexOf("\n"));
					statements = statements.substring(statements.indexOf("\n") + 1);

					if (method1.getName().getFullyQualifiedName().equals("output") && className2.equals("Text")) {

					}
					if (CodeMatcher.ifWMinstanceof(sm, className2)) {
						relation = (3 - 1) * 4 + 1 + getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log4.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", include: instanceof  "
								+ className2 + getTag(ifChangedClass2) + "\r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}

					if (CodeMatcher.ifChangeoftype(sm, className2)) {
						relation = (2 - 1) * 4 + 1 + getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log3.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", include: type convert of  "
								+ className2 + getTag(ifChangedClass2) + "\r\n");
						log3.add(" the code: " + sm + " \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}

					if (CodeMatcher.ifWMuseclassdef(sm, className2)) {
						relation = (4 - 1) * 4 + 1 + getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log6.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", include: " + className2
								+ getTag(ifChangedClass2) + ".class \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}

					String pName = CodeMatcher.ifnewPara2(sm, className2);
					if (pName != null) {
						if (pName.length() > 0) {

							if (pName.equals(className2)) {
								relation = 17;
								log17.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1)
										+ ", method  " + method1.getName() + getTag(j >= numberofNewMethod1) + ", use: "
										+ className2 + getTag(ifChangedClass2) + " 's static method \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);
							} else {
								mAttributeList.add(pName);
							}

						}
					}
					if (CodeMatcher.ifIncludeAttributeList(attributeList, sm) >= 0) {
						relation = (5 - 1) * 4 + 1 + getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log8.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", use the class attribute "
								+ attributeList.get(CodeMatcher.ifIncludeAttributeList(attributeList, sm)) + " \r\n");
						log8.add(" the code: " + sm + " \r\n");

						class1.addRevisionRelationship(class2.getId(), relation);
					}
					if (CodeMatcher.ifIncludeAttributeList(mAttributeList, sm) >= 0) {
						relation = (8 - 1) * 4 - (2) + 1 + getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log11.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1)
								+ ", use the attribute defined inside class"
								+ mAttributeList.get(CodeMatcher.ifIncludeAttributeList(mAttributeList, sm)) + " \r\n");
						log11.add(" the code: " + sm + " \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);

					}

					if (CodeMatcher.ifIncludeAttributeList(pList, sm) >= 0) {
						relation = (11 - 1) * 4 - (2 + 2) + 1
								+ getchangetype((j >= numberofNewMethod1), ifChangedClass2);
						log14.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1) + ", method "
								+ method1.getName() + getTag(j >= numberofNewMethod1) + ", use the input parameter "
								+ pList.get(CodeMatcher.ifIncludeAttributeList(pList, sm)) + " \r\n");
						log14.add(" the code: " + sm + " \r\n");
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					for (int k = 0; k < methodList2.size(); k++) {
						MethodDeclaration method2 = methodList2.get(k);
						String oName = CodeMatcher.useofMethod(sm, method2.getName().getFullyQualifiedName(),
								method2.parameters().size());

						if (oName != null) {
							if (CodeMatcher.methodListInclude(pList, oName)) {
								relation = (13 - 1) * 4 - (2 + 2 + 2) + 1
										+ getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2));
								log16.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1)
										+ ", method " + method1.getName() + getTag(j >= numberofNewMethod1)
										+ ", include input parameter:  " + oName + " (of the type " + className2
										+ getTag(ifChangedClass2) + ")" + " use the method" + k + ": "
										+ method2.getName().getFullyQualifiedName() + method2.parameters()
										+ getTag(k >= numberofNewMethod2) + " \r\n");
								log16.add(" the code: " + sm + " \r\n");
								class1.addRevisionRelationship(class2.getId(), relation);

								class1.deleteRevisionRelationship(class2.getId(),
										(relation - 6
												- getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2))
												+ getchangetype((j >= numberofNewMethod1), ifChangedClass2)));
								log14.add("-----canceled--------" + " \r\n");
								k = methodList2.size();
							}
							if (CodeMatcher.methodListInclude(mAttributeList, oName)) {

								relation = (10 - 1) * 4 - (2 + 2) + 1
										+ getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2));

								log13.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1)
										+ ", method " + method1.getName() + getTag(j >= numberofNewMethod1)
										+ ", include parameter:  " + oName + " (of the type " + className2
										+ getTag(ifChangedClass2) + ")" + " use the method " + k + ": "
										+ method2.getName().getFullyQualifiedName() + method2.parameters()
										+ getTag(k >= numberofNewMethod2) + " \r\n");
								log13.add(" the code: " + sm + " \r\n");

								class1.addRevisionRelationship(class2.getId(), relation);
								class1.deleteRevisionRelationship(class2.getId(),
										relation - 6
												- getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2))
												+ getchangetype((j >= numberofNewMethod1), ifChangedClass2));
								log11.add("-----canceled--------" + " \r\n");
							}
							if (CodeMatcher.methodListInclude(attributeList, oName)) {
								relation = (7 - 1) * 4 - (2) + 1
										+ getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2));

								log10.add("Class" + class1.getId() + ": " + className1 + getTag(ifChangedClass1)
										+ ", method " + method1.getName() + getTag(j >= numberofNewMethod1)
										+ ", include class attribute:  " + oName + " (of the type " + className2
										+ getTag(ifChangedClass2) + ")" + " use the method " + k + ": "
										+ method2.getName().getFullyQualifiedName() + method2.parameters()
										+ getTag(k >= numberofNewMethod2) + "  " + method2.parameters().size()
										+ " \r\n");
								log10.add(" the code: " + sm + " \r\n");

								class1.addRevisionRelationship(class2.getId(), relation);
								class1.deleteRevisionRelationship(class2.getId(),
										relation - 6
												- getchangetype((j >= numberofNewMethod1), (k >= numberofNewMethod2))
												+ getchangetype((j >= numberofNewMethod1), ifChangedClass2));
								log8.add("-----canceled--------" + " \r\n");
							}

						}

					}
					for (int k = 0; k < fieldList2.size(); k++) {
						FieldDeclaration field2 = fieldList2.get(k);
						String oName = CodeMatcher.useofMethod(sm,
								CodeMatcher.getVariable(field2.fragments().get(0).toString()));
						if (oName != null) {
							if (CodeMatcher.methodListInclude(pList, oName)) {
								relation = (12 - 1) * 4 - (2 + 2) + 1 + getchangetype(false, (j >= numberofNewMethod1));
								class1.addRevisionRelationship(class2.getId(), relation);
							}
							if (CodeMatcher.methodListInclude(mAttributeList, oName)) {

								relation = (9 - 1) * 4 - (2) + 1 + getchangetype(false, (j >= numberofNewMethod1));

								class1.addRevisionRelationship(class2.getId(), relation);
							}
							if (CodeMatcher.methodListInclude(attributeList, oName)) {

								relation = (6 - 1) * 4 + 1 + getchangetype(false, (j >= numberofNewMethod1));

								class1.addRevisionRelationship(class2.getId(), relation);
							}

						}

					}
				}
			}
		}
		String location = savePath;
//		this.writeLog(location + "1.txt", log1);
//		this.writeLog(location + "2.txt", log2);
//		this.writeLog(location + "3.txt", log3);
//		this.writeLog(location + "4.txt", log4);
//		this.writeLog(location + "5.txt", log5);
//		this.writeLog(location + "6.txt", log6);
//		this.writeLog(location + "7.txt", log7);
//		this.writeLog(location + "8.txt", log8);
//		this.writeLog(location + "9.txt", log9);
//		this.writeLog(location + "10.txt", log10);
//		this.writeLog(location + "11.txt", log11);
//		this.writeLog(location + "12.txt", log12);
//		this.writeLog(location + "13.txt", log13);
//		this.writeLog(location + "14.txt", log14);
//		this.writeLog(location + "15.txt", log15);
//		this.writeLog(location + "16.txt", log16);
//		this.writeLog(location + "17.txt", log17);
//		this.writeLog(location + "18.txt", log18);
//		this.writeLog(location + "19.txt", log19);
		return relation;

	}

	@SuppressWarnings("deprecation")
	public int ifDirectRelated(ClassChange class1, ClassChange class2, boolean ifChangedClass2) {
		int relation = 0;
		String className2 = class2.getNewClass().getName().getFullyQualifiedName();
		TypeDeclaration classDef1 = class1.getNewClass();
		ArrayList<String> attributeList = new ArrayList<String>();
		ArrayList<String> mAttributeList = new ArrayList<String>();
		ArrayList<String> pList = new ArrayList<String>();

		if (classDef1.getSuperclass().getFullyQualifiedName().equals(className2)) {
			relation = 1;
			class1.addRevisionRelationship(class2.getId(), relation);
		}
		@SuppressWarnings("rawtypes")
		List interfaceList1 = classDef1.superInterfaces();
		for (int i = 0; i < interfaceList1.size(); i++) {
			Object interface1 = interfaceList1.get(i);
			if (interface1 instanceof SimpleName) {
				if (((SimpleName) interface1).getFullyQualifiedName().equals(className2)) {
					relation = 2;
					class1.addRevisionRelationship(class2.getId(), relation);
				}
			}
		}

		for (int i = 0; i < classDef1.getFields().length; i++) {
			if (classDef1.getFields()[i].getType().toString().equals(className2)) {

				@SuppressWarnings("rawtypes")
				List fragmentList = classDef1.getFields()[i].fragments();
				for (int k = 0; k < fragmentList.size(); k++) {
					attributeList.add(CodeMatcher.getVariable(fragmentList.get(k).toString()));
				}
				relation = 8;
				class1.addRevisionRelationship(class2.getId(), relation);
			}

		}

		for (int j = 0; j < classDef1.getMethods().length; j++) {
			if (classDef1.getMethods()[j] != null && classDef1.getMethods()[j].getBody() != null) {
				@SuppressWarnings("rawtypes")
				List paraList = classDef1.getMethods()[j].parameters();
				MethodDeclaration method1 = classDef1.getMethods()[j];
				if (paraList.size() > 0) {
					for (int k = 0; k < paraList.size(); k++) {
						String typeName = ((SingleVariableDeclaration) paraList.get(k)).getType().toString();
						String paraName = ((SingleVariableDeclaration) paraList.get(k)).getName()
								.getFullyQualifiedName();
						if (typeName.equals(className2)) {
							relation = 14;
							pList.add(paraName);
							class1.addRevisionRelationship(class2.getId(), relation);
						}
					}
				}
				if (method1.getReturnType().toString().equals(className2)) {
					relation = 5;
					class1.addRevisionRelationship(class2.getId(), relation);
				}

				String statements = classDef1.getMethods()[j].getBody().toString();

				while (statements.indexOf("\n") > 0) {

					String sm = statements.substring(0, statements.indexOf("\n"));
					statements = statements.substring(statements.indexOf("\n") + 1);

					if (CodeMatcher.ifWMinstanceof(sm, className2)) {
						relation = 10;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					if (CodeMatcher.ifWMuseclassdef(sm, className2)) {
						relation = 15;
						class1.addRevisionRelationship(class2.getId(), relation);
					}
					String pName = CodeMatcher.ifnewPara(sm, className2);
					if (pName != null) {
						relation = 7;
						class1.addRevisionRelationship(class2.getId(), relation);
						if (pName.length() > 0) {
							if (pName.equals(className2)) {
								relation = 9;
								class1.addRevisionRelationship(class2.getId(), relation);
							} else
								mAttributeList.add(pName);
						}
					}

					if (ifChangedClass2) {
						for (int k = 0; k < class2.getAddedMethods().size(); k++) {
							String oName = CodeMatcher.useofMethod(sm,
									class2.getAddedMethods().get(k).getName().getFullyQualifiedName());
							if (oName != null) {
								if (CodeMatcher.methodListInclude(pList, oName)) {
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(mAttributeList, oName)) {
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(attributeList, oName)) {
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
							}
						}
						for (int k = 0; k < class2.getChangedMethods().size(); k++) {
							String oName = CodeMatcher.useofMethod(sm,
									class2.getChangedMethods().get(k).getNewMethod().getName().getFullyQualifiedName());
							if (oName != null) {
								if (CodeMatcher.methodListInclude(pList, oName)) {
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(mAttributeList, oName)) {
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(attributeList, oName)) {
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
							}
						}
					} else {
						for (int k = 0; k < class2.getNewClass().getMethods().length; k++) {
							String oName = CodeMatcher.useofMethod(sm,
									class2.getNewClass().getMethods()[k].getName().getFullyQualifiedName());
							if (oName != null) {
								if (CodeMatcher.methodListInclude(pList, oName)) {
									relation = 2;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(mAttributeList, oName)) {
									relation = 8;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
								if (CodeMatcher.methodListInclude(attributeList, oName)) {
									relation = 4;
									class1.addRevisionRelationship(class2.getId(), relation);
								}
							}
						}
					}
				}
			}
		}
		return relation;

	}

	public boolean ifTheSameInterface(TypeDeclaration class1, TypeDeclaration class2) {
		boolean tag = false;
		@SuppressWarnings({ "rawtypes", "deprecation" })
		List interfaceList1 = class1.superInterfaces();
		@SuppressWarnings({ "rawtypes", "deprecation" })
		List interfaceList2 = class2.superInterfaces();
		if (interfaceList1.size() > 0 && interfaceList2.size() > 0) {
			for (int i = 0; i < interfaceList1.size(); i++) {
				Object interface1 = interfaceList1.get(i);
				if (interface1 instanceof SimpleName) {
					if (((SimpleName) interface1).getFullyQualifiedName()
							.equals(class2.getName().getFullyQualifiedName()))
						tag = true;
				}
				for (int j = 0; j < interfaceList2.size(); j++) {
					Object interface2 = interfaceList2.get(j);
					if (interface2 instanceof SimpleName) {
						if (((SimpleName) interface2).getFullyQualifiedName()
								.equals(((SimpleName) interface2).getFullyQualifiedName()))
							tag = true;
					}
				}
			}
		}
		return tag;
	}

	private String getTag(boolean ifChanged) {
		if (ifChanged == false) {
			return new String(" $new$ ");
		} else {
			return new String(" $changed$ ");
		}
	}

	public void writeLog60(String logFilePath) {
		try {

			File f = new File(logFilePath);
			BufferedWriter output = new BufferedWriter(new FileWriter(f));

			output.write("Compared Result of  two versions" + "\r\n");

			output.write("Number of New Class: " + this.getAddedClassList().size() + "\r\n");

			output.write("Number of Changed Class: " + this.getChangedClassList().size() + "\r\n");

			output.write("Number of Deleted Class: " + this.getDeletedClassList().size() + "\r\n");

			for (int i = 0; i < this.getAddedClassList().size(); i++) {
				output.write("new Added class " + i + " : " + this.getAddedClassList().get(i).getNewPackageName() + "."
						+ this.getAddedClassList().get(i).getNewClass().getName() + ": " + "\r\n");
			}
			for (int i = 0; i < this.getChangedClassList().size(); i++) {
				output.write("Changed class " + (i + this.getAddedClassList().size()) + " : "
						+ this.getChangedClassList().get(i).getOldClass().getName() + "\r\n");
				ClassChange classChange = this.getChangedClassList().get(i);
				for (int j = 0; j < classChange.getAddedFields().size(); j++) {
					output.write("     new Added Field " + j + " : " + classChange.getAddedFields().get(j).getType()
							+ " " + classChange.getAddedFields().get(j).fragments() + "\r\n");
				}
				for (int j = 0; j < classChange.getDeletedFields().size(); j++) {
					output.write(" Deleted Field " + j + " : " + classChange.getDeletedFields().get(j) + "\r\n");
				}
				for (int j = 0; j < classChange.getAddedMethods().size(); j++) {
					output.write("     new Added method " + (j) + " : " + classChange.getAddedMethods().get(j).getName()
							+ classChange.getAddedMethods().get(j).parameters() + "\r\n");
				}
				for (int j = 0; j < classChange.getChangedMethods().size(); j++) {
					output.write("     new changed method " + (j + classChange.getAddedMethods().size()) + " : "
							+ classChange.getChangedMethods().get(j).getNewMethod().getName()
							+ classChange.getChangedMethods().get(j).getNewMethod().parameters().toString() + "\r\n");
				}
				for (int j = 0; j < classChange.getDeletedMethods().size(); j++) {
					output.write("     new deleted method " + j + " : "
							+ classChange.getDeletedMethods().get(j).getName() + "\r\n");
				}
			}
			for (int i = 0; i < this.getDeletedClassList().size(); i++) {
				output.write("Deleted class " + i + " : " + this.getDeletedClassList().get(i).getName() + "\r\n");
			}
			this.compareChangType4Direct60("D:/");
			output.write("testing relationship " + "\r\n");
			List<ClassChange> relatedClasses = this.getAcClassList();

			for (int i = 0; i < relatedClasses.size(); i++) {
				for (int j = 0; j < relatedClasses.size(); j++)
					if (i != j && relatedClasses.get(i).ifRelated(j)) {
						output.write("class " + relatedClasses.get(i).getId() + "  vs  " + relatedClasses.get(j).getId()
								+ "  ");
						for (int k = 1; k < 61; k++) {
							if (relatedClasses.get(i).getRelationship().get(j)[k] > 0)
								output.write("1" + "  ");
							else
								output.write("0" + "  ");

						}
						output.write("\r\n");
					}
			}
			output.write("\r\n");
			output.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void writeLog(String logFilePath, List<String> log) {
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilePath, true)));
			for (int i = 0; i < log.size(); i++) {
				output.write(log.get(i));
				output.write("\r\n");
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
