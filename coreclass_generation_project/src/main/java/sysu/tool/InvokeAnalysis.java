package sysu.tool;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.tool.CodeMatcher;
import sysu.bean.MethodInvoke;



public class InvokeAnalysis {

	public static List<MethodInvoke> findInvoke(ClassChange class1, List<ClassChange> acClassList, String class1Type,
			int newClassSize) {

		List<MethodInvoke> methodInvokeList = new ArrayList<MethodInvoke>();
		String classname1 = class1.getNewClass().getName().getFullyQualifiedName();

		
		for (ClassChange class2 : acClassList) {
			String classname2 = class2.getNewClass().getName().getFullyQualifiedName();
			Integer[] relations = class1.getRelationship().get(class2.getId());
			boolean isInvoke = (relations[17]
					+ /* relations[21]+relations[22]+ */ relations[23] + relations[24] + relations[25] + relations[26] +
					/* relations[31]+relations[32]+ */relations[33] + relations[34] + relations[35] + relations[36] +
					/* relations[41]+relations[42]+ */relations[43] + relations[44] + relations[45] + relations[46]) > 0;

			if (!classname1.equals(classname2) && isInvoke) {

				List<MethodDeclaration> acMethodList1 = new ArrayList<MethodDeclaration>();

				if (class1Type.equals("change")) {
					for (MethodDeclaration method : class1.getAddedMethods()) {
						acMethodList1.add(method);
					}
					for (int j = 0; j < class1.getChangedMethods().size(); j++) {
						acMethodList1.add(class1.getChangedMethods().get(j).getNewMethod());
					}
				} else {
					for (int k = 0; k < class1.getNewClass().getMethods().length; k++) {
						acMethodList1.add(class1.getNewClass().getMethods()[k]);
					}
				}

				// ���ҷ����б�
				for (int j = 0; j < acMethodList1.size(); j++) {
					MethodDeclaration method1 = acMethodList1.get(j);
					List<String> vars1 = new ArrayList<String>();// �洢class2���͵Ĳ�����

					if (method1.getBody() != null) {

						// �ڷ�������Ѱ��class2���͵Ĳ���
						vars1.addAll(CodeMatcher.ifnewPara2s(method1.getBody().toString(), classname2));

						// �ڷ��������б���Ѱ��class2���Ͳ���
						String[] temps;
						for (Object para : method1.parameters()) {
							temps = para.toString().split(" ");
							if (temps[0].equals(classname2)) {
								vars1.add(temps[1]);
							} else if (temps.length > 2) {
								if (temps[1].equals(classname2)) {
									vars1.add(temps[2]);
								}
							}
						}

						List<String> varList = CodeMatcher.changeoftype(method1.getBody().toString(), classname2);
						vars1.addAll(varList);

						// ��class1������Ѱ��class2���Ͳ���
						String classname;
						String varname;
						FieldDeclaration[] fields = class1.getNewClass().getFields();
						for (FieldDeclaration field : fields) {
							classname = field.getType().toString();
							if (!field.fragments().isEmpty()) {
								for (int k = 0; k < field.fragments().size(); k++) {
									int index1 = field.fragments().get(k).toString().indexOf("=");
									int index2 = field.fragments().get(k).toString().indexOf("new ");
									int index3 = field.fragments().get(k).toString().indexOf("(");
									if (index1 != -1) {
										varname = field.fragments().get(k).toString().substring(0,
												field.fragments().get(k).toString().indexOf("="));
										if (index2 != -1 && index3 != -1&&index2+4<index3) {
											classname = field.fragments().get(k).toString().substring(index2 + 4,
													index3);
										}
									} else {
										varname = field.fragments().get(k).toString();
									}
									if (classname.equals(classname2)) {
										vars1.add(varname);
									}
								}

							}
						}

						List<MethodDeclaration> acMethodList = new ArrayList<MethodDeclaration>();
						if (class2.getId() >= newClassSize) {
							for (MethodDeclaration method : class2.getAddedMethods()) {
								acMethodList.add(method);
							}
							for (MethodChange cmethod : class2.getChangedMethods()) {
								MethodDeclaration method = cmethod.getNewMethod();
								acMethodList.add(method);
							}
						} else {
							for (MethodDeclaration method : class2.getNewClass().getMethods()) {
								acMethodList.add(method);
							}
						}

						// ѭ���Ƚ�class2�еĸı��˵ķ����������ķ����Ƿ�class1�е�method����
						for (int k = 0; k < acMethodList.size(); k++) {
							MethodDeclaration method2 = acMethodList.get(k);
							String var2 = CodeMatcher.useofMethod(method1.getBody().toString(),
									method2.getName().getFullyQualifiedName(), method2.parameters().size());

							for (String var : vars1) {
								if (var != null && var2 != null) {
									if (var.equals(var2)) {

										MethodInvoke methodInvoke = new MethodInvoke();
										methodInvoke.setClass1(classname1);
										methodInvoke.setClass2(classname2);
										methodInvoke.setMethod1(method1.getName().getFullyQualifiedName());
										methodInvoke.setMethod2(method2.getName().getFullyQualifiedName());
										if (j < class1.getAddedMethods().size()) {
											methodInvoke.setMethod1_type("new");
										} else {
											methodInvoke.setMethod1_type("change");
										}
										if (k < class2.getAddedMethods().size()) {
											methodInvoke.setMethod2_type("new");
										} else {
											methodInvoke.setMethod2_type("change");
										}
										methodInvokeList.add(methodInvoke);

										break;
									}
								}
								if (var2 != null) {
									if (var2.equals(classname2)) {
										MethodInvoke methodInvoke = new MethodInvoke();
										methodInvoke.setClass1(classname1);
										methodInvoke.setClass2(classname2);
										methodInvoke.setMethod1(method1.getName().getFullyQualifiedName());
										methodInvoke.setMethod2(method2.getName().getFullyQualifiedName());
										if (j < class1.getAddedMethods().size()) {
											methodInvoke.setMethod1_type("new");
										} else {
											methodInvoke.setMethod1_type("change");
										}
										if (k < class2.getAddedMethods().size()) {
											methodInvoke.setMethod2_type("new");
										} else {
											methodInvoke.setMethod2_type("change");
										}

										methodInvokeList.add(methodInvoke);

									}
								}
							}

						}
					}
				}
			}
		}

		return methodInvokeList;
	}

	public static List<String> findMethodAttributeUsed(MethodDeclaration method, List<ClassChange> acClassList) {

		List<String> attributeList = new ArrayList<String>();

		for (int i = 0; i < acClassList.size(); i++) {
			if (method.getBody() != null) {
				String attribute = CodeMatcher.ifnewPara2(method.getBody().toString(),
						acClassList.get(i).getNewClass().getName().getFullyQualifiedName());
				if (attribute != null) {
					attributeList.add(acClassList.get(i).getNewClass().getName().getFullyQualifiedName());
				}
			}
		}

		return attributeList;
	}

	public static List<String> findClassAttributeUsed(ClassChange class1, List<ClassChange> acClassList) {

		List<String> attributeList = new ArrayList<String>();
		List<FieldDeclaration> fields = class1.getAddedFields();

		String classname;

		for (FieldDeclaration field : fields) {
			classname = field.getType().toString();

			for (int i = 0; i < acClassList.size(); i++) {
				String classname2 = acClassList.get(i).getNewClass().getName().getFullyQualifiedName();
				if (classname.equals(classname2)) {
					attributeList.add(classname2);
				}
			}
		}

		return attributeList;
	}

	public static String findClassComment(TypeDeclaration class1) {
		String comment;
		if (class1.getJavadoc() != null) {
			comment = class1.getJavadoc().toString();
		} else {
			comment = "";
		}
		return comment;
	}

	public static String findMethodComment(MethodDeclaration method) {
		String comment;

		if (method.getJavadoc() != null) {
			comment = method.getJavadoc().toString();
		} else {
			comment = "";
		}
		return comment;
	}
}
