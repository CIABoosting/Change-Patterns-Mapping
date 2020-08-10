package sysu.coreclass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Generated;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;



import sysu.bean.ClassBean;
import sysu.bean.MethodBean;
import sysu.bean.MethodInvoke;
import sysu.bean.StatementBean;
import sysu.tool.ClassChange;
import sysu.tool.InvokeAnalysis;
import sysu.tool.VersionChange;
import sysu.tool.VersionComparator;


public class CoreClass {

	public  List<ClassBean> classBeanList = new ArrayList<ClassBean>();
	public List<MethodBean> methodBeanList = new ArrayList<MethodBean>();
	private  CoreClassClassifier coreClassClassifier = new CoreClassClassifier();
	
	public Map<String, Double> getProbabilities() {
		Map<String, Double> map = new HashMap<String, Double>();
		
		List<List<Integer>> vectors = generate();	
		List<Double> result = new ArrayList<Double>();
		try {
			result = coreClassClassifier.classify(vectors);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < classBeanList.size(); i++) {
			classBeanList.get(i).setCoreProbability(result.get(i));
		}

		Collections.sort(classBeanList, new Comparator<ClassBean>() {
			public int compare(ClassBean class1, ClassBean class2) {
				return class1.getCoreProbability() > class2.getCoreProbability() ? -1 : 1;
			}
		});

		for(ClassBean bean: classBeanList) {
			map.put(bean.getClassName(), bean.getCoreProbability());
		}
		return map;
	}

	public void initClassBeanListAndMeanBeanList(String oldFileDir, String newFileDir) {
		VersionComparator comp = new VersionComparator();
		comp.compareProject(oldFileDir, newFileDir);
		
		VersionChange change = comp.getChange();
		change.compareChangType4Direct60(newFileDir + "/");
		
		List<ClassChange> addClassList = change.getAddedClassList();
		//System.out.println(addClassList.size());
		// List<ClassBean> classBeanList = new ArrayList<ClassBean>();
		// List<MethodBean> methodBeanList = new ArrayList<MethodBean>();
		for (int i = 0; i < addClassList.size(); i++) {
			List<MethodInvoke> methodInvokeList = InvokeAnalysis.findInvoke(addClassList.get(i),
					change.getAcClassList(), "new", change.getAddedClassList().size());

			
			List<Integer> addStatementNodeTypeList = new ArrayList<Integer>();
			List<Integer> changeStatementNodeTypeList = new ArrayList<Integer>();
			List<Integer> deleteStatementNodeTypeList = new ArrayList<Integer>();

			ClassBean classBean = new ClassBean();
			classBean.setClassID(i + 1);
			classBean.setClassName(addClassList.get(i).getNewClass().getName().getFullyQualifiedName());

			classBean.setCodes(addClassList.get(i).getCodes());
			classBean.setNewCodeLines(addClassList.get(i).getNewCodeLines());
			classBean.setOldCodeLines(addClassList.get(i).getOldCodeLines());
			classBean.setNewMethodNum(addClassList.get(i).getNewMethodNum());
			classBean.setOldMethodNum(addClassList.get(i).getOldMethodNum());
			classBean.setAddMethodNum(addClassList.get(i).getNewClass().getMethods().length);
			if (addClassList.get(i).getNewClass().getSuperclassType() != null) {
				classBean.setParentName(addClassList.get(i).getNewClass().getSuperclassType().toString());
			} else {
				classBean.setParentName("Object");
			}
			classBean.setClassType("new");
			List<String> interfaces = new ArrayList<String>();
			for (int j = 0; j < addClassList.get(i).getNewClass().superInterfaceTypes().size(); j++) {
				interfaces.add(addClassList.get(i).getNewClass().superInterfaceTypes().get(j).toString());
			}
			classBean.setInterfaceList(interfaces);
			classBean.setOutterCount(
					computeOuterCount(classBean, addClassList.get(i).getNewClass(), change.getAcClassList()));
			classBean.setMethodInvokeList(methodInvokeList);

			for (int j = 0; j < addClassList.get(i).getNewClass().getMethods().length; j++) {
				MethodDeclaration method = addClassList.get(i).getNewClass().getMethods()[j];
				MethodBean methodBean = new MethodBean();
				methodBean.setClassID(classBean.getClassID());
				methodBean.setClassName(classBean.getClassName());
				// methodBean.setVersionID(versionID);
				methodBean.setMethodID(j);
				methodBean.setMethodType("new");
				methodBean.setMethodName(method.getName().getFullyQualifiedName());

				if (method.getReturnType2() != null) {
					methodBean.setReturnType(method.getReturnType2().toString());
				} else {
					methodBean.setReturnType("null");
				}

				List<String> methodParameters = new ArrayList<String>();
				for (int k = 0; k < method.parameters().size(); k++) {
					methodParameters.add(method.parameters().get(k).toString());
				}
				methodBean.setParameters(methodParameters);

				methodBean.setOutterCount(computeMethodOuterCount(methodBean.getMethodName(), methodInvokeList));

				List<StatementBean> statementList = new ArrayList<StatementBean>();
				if (method.getBody() != null) {
					for (int l = 0, n = method.getBody().statements().size(); l < n; l++) {
						StatementBean statementBean = new StatementBean();
						Statement sta = (Statement) method.getBody().statements().get(l);
						statementBean.setStatement(sta.toString());
						statementBean.setStatementeType("add");
						statementBean.setStatementID(l);
						statementBean.setNodeType(sta.getNodeType());
						statementList.add(statementBean);
						addStatementNodeTypeList.add(statementBean.getNodeType());
					}
				}
				methodBean.setStatementList(statementList);
				methodBeanList.add(methodBean);
			}

			classBean.setAddStatementNodeTypeList(addStatementNodeTypeList);
			classBean.setChangeStatementNodeTypeList(changeStatementNodeTypeList);
			classBean.setDeleteStatementNodeTypeList(deleteStatementNodeTypeList);
			classBeanList.add(classBean);
		}

		
		List<ClassChange> changeClassList = change.getChangedClassList();
		// System.out.println(changeClassList.size());
		for (int i = 0; i < changeClassList.size(); i++) {
			List<MethodInvoke> methodInvokeList = InvokeAnalysis.findInvoke(changeClassList.get(i),
					change.getAcClassList(), "new", change.getAddedClassList().size());

			List<Integer> addStatementNodeTypeList = new ArrayList<Integer>();
			List<Integer> changeStatementNodeTypeList = new ArrayList<Integer>();
			List<Integer> deleteStatementNodeTypeList = new ArrayList<Integer>();

			ClassBean classBean = new ClassBean();
			classBean.setClassID(addClassList.size() + i);
			// classBean.setVersionID(versionID);
			// classBean.setUserName(userName);
			classBean.setClassName(changeClassList.get(i).getNewClass().getName().getFullyQualifiedName());

			classBean.setNewCodeLines(changeClassList.get(i).getNewCodeLines());
			classBean.setOldCodeLines(changeClassList.get(i).getOldCodeLines());
			classBean.setNewMethodNum(changeClassList.get(i).getNewMethodNum());
			classBean.setOldMethodNum(changeClassList.get(i).getOldMethodNum());
			classBean.setCodes(changeClassList.get(i).getCodes());
			classBean.setChangeMethodNum(changeClassList.get(i).getChangedMethods().size());
			classBean.setAddMethodNum(changeClassList.get(i).getAddedMethods().size());
			classBean.setDeleteMethodNum(changeClassList.get(i).getDeletedMethods().size());
			if (changeClassList.get(i).getNewClass().getSuperclassType() != null) {
				classBean.setParentName(changeClassList.get(i).getNewClass().getSuperclassType().toString());
			} else {
				classBean.setParentName("Object");
			}
			classBean.setClassType("change");
			List<String> interfaces = new ArrayList<String>();
			for (int j = 0; j < changeClassList.get(i).getNewClass().superInterfaceTypes().size(); j++) {
				interfaces.add(changeClassList.get(i).getNewClass().superInterfaceTypes().get(j).toString());
			}
			classBean.setInterfaceList(interfaces);
			classBean.setMethodInvokeList(methodInvokeList);
			classBean.setOutterCount(
					computeOuterCount(classBean, changeClassList.get(i).getNewClass(), change.getAcClassList()));

			for (int j = 0; j < changeClassList.get(i).getAddedMethods().size(); j++) {
				MethodDeclaration method = changeClassList.get(i).getAddedMethods().get(j);
				MethodBean methodBean = new MethodBean();
				methodBean.setClassID(classBean.getClassID());
				methodBean.setClassName(classBean.getClassName());
				// methodBean.setVersionID(versionID);
				methodBean.setMethodID(j);
				methodBean.setMethodType("new");
				methodBean.setMethodName(method.getName().getFullyQualifiedName());

				if (method.getReturnType2() != null) {
					methodBean.setReturnType(method.getReturnType2().toString());
				} else {
					methodBean.setReturnType("null");
				}

				List<String> methodParameters = new ArrayList<String>();
				for (int k = 0; k < method.parameters().size(); k++) {
					methodParameters.add(method.parameters().get(k).toString());
				}
				methodBean.setParameters(methodParameters);

				methodBean.setOutterCount(computeMethodOuterCount(methodBean.getMethodName(), methodInvokeList));

				List<StatementBean> statementList = new ArrayList<StatementBean>();
				if (method.getBody() != null) {
					for (int l = 0, n = method.getBody().statements().size(); l < n; l++) {
						StatementBean statementBean = new StatementBean();
						Statement sta = (Statement) method.getBody().statements().get(l);
						statementBean.setStatement(sta.toString());
						statementBean.setStatementeType("add");
						statementBean.setStatementID(l);
						statementBean.setNodeType(sta.getNodeType());
						addStatementNodeTypeList.add(sta.getNodeType());
						statementList.add(statementBean);
					}
				}
				methodBean.setStatementList(statementList);
				methodBeanList.add(methodBean);
			}
			for (int j = 0; j < changeClassList.get(i).getChangedMethods().size(); j++) {
				MethodDeclaration method = changeClassList.get(i).getChangedMethods().get(j).getNewMethod();
				MethodBean methodBean = new MethodBean();
				methodBean.setClassID(classBean.getClassID());
				// methodBean.setVersionID(versionID);
				methodBean.setClassName(classBean.getClassName());
				methodBean.setMethodID(changeClassList.get(i).getAddedMethods().size() + j);
				methodBean.setMethodType("change");
				methodBean.setMethodName(method.getName().getFullyQualifiedName());

				if (method.getReturnType2() != null) {
					methodBean.setReturnType(method.getReturnType2().toString());
				} else {
					methodBean.setReturnType("null");
				}

				List<String> methodParameters = new ArrayList<String>();
				for (int k = 0; k < method.parameters().size(); k++) {
					methodParameters.add(method.parameters().get(k).toString());
				}
				methodBean.setParameters(methodParameters);
				methodBean.setOutterCount(computeMethodOuterCount(methodBean.getMethodName(), methodInvokeList));

				methodBean.setStatementList(changeClassList.get(i).getChangedMethods().get(j).getStatementList());

				for (StatementBean statement : methodBean.getStatementList()) {
					if (statement.getStatementeType().equals("change")) {
						changeStatementNodeTypeList.add(statement.getNodeType());
					} else if (statement.getStatementeType().equals("delete")) {
						deleteStatementNodeTypeList.add(statement.getNodeType());
					} else {
						addStatementNodeTypeList.add(statement.getNodeType());
					}
				}


				methodBeanList.add(methodBean);
			}
			for (int j = 0; j < changeClassList.get(i).getDeletedMethods().size(); j++) {
				MethodDeclaration method = changeClassList.get(i).getDeletedMethods().get(j);
				MethodBean methodBean = new MethodBean();
				methodBean.setClassID(classBean.getClassID());
				methodBean.setClassName(classBean.getClassName());
				methodBean.setMethodID(j);
				// methodBean.setVersionID(versionID);
				methodBean.setMethodType("delete");
				methodBean.setMethodName(method.getName().getFullyQualifiedName());

				if (method.getReturnType2() != null) {
					methodBean.setReturnType(method.getReturnType2().toString());
				} else {
					methodBean.setReturnType("null");
				}

				List<String> methodParameters = new ArrayList<String>();
				for (int k = 0; k < method.parameters().size(); k++) {
					methodParameters.add(method.parameters().get(k).toString());
				}
				methodBean.setParameters(methodParameters);
				methodBean.setOutterCount(computeMethodOuterCount(methodBean.getMethodName(), methodInvokeList));

				List<StatementBean> statementList = new ArrayList<StatementBean>();
				if (method.getBody() != null) {
					for (int l = 0, n = method.getBody().statements().size(); l < n; l++) {
						StatementBean statementBean = new StatementBean();
						Statement sta = (Statement) method.getBody().statements().get(l);
						statementBean.setStatement(sta.toString());
						statementBean.setStatementeType("delete");
						statementBean.setStatementID(l);
						statementBean.setNodeType(sta.getNodeType());
						statementList.add(statementBean);

						deleteStatementNodeTypeList.add(sta.getNodeType());
					}
				}
				methodBean.setStatementList(statementList);
				methodBeanList.add(methodBean);
			}
			classBean.setAddStatementNodeTypeList(addStatementNodeTypeList);
			classBean.setChangeStatementNodeTypeList(changeStatementNodeTypeList);
			;
			classBean.setDeleteStatementNodeTypeList(deleteStatementNodeTypeList);
			classBeanList.add(classBean);

			// System.out.println("debug");
		}
		// System.out.println(classBeanList.size());

		computeInnerCount(classBeanList);
	}

	private static int computeOuterCount(ClassBean classBean, TypeDeclaration class1, List<ClassChange> class2List) {

		int outerCount = 0;
		for (ClassChange cc : class2List) {
			for (MethodDeclaration md : class1.getMethods()) {
				if (md.getBody() != null
						&& md.getBody().toString().contains(cc.getNewClass().getName().getFullyQualifiedName())) {
					classBean.getUseClassList().add(cc.getNewClass().getName().getFullyQualifiedName());
					outerCount++;
					break;
				}
			}
		}
		return outerCount;
	}

	private static void computeInnerCount(List<ClassBean> classBeanList) {

		for (ClassBean classBean1 : classBeanList) {
			int innerCount = 0;
			String classBean1Name = classBean1.getClassName();
			for (ClassBean classBean2 : classBeanList) {
				if (classBean2.getUseClassList().contains(classBean1Name)) {
					innerCount++;
				}
			}
			classBean1.setInnerCount(innerCount);
		}
	}

	private  int computeMethodOuterCount(String methodName, List<MethodInvoke> methodInvokeList) {
		int result = 0;
		for (MethodInvoke methodInvoke : methodInvokeList) {
			if (methodInvoke.getMethod1().equals(methodName)) {
				result++;
			}
		}
		return result;
	}
	public  List<List<Integer>> generate() {

		List<List<Integer>> vectorList = new ArrayList<List<Integer>>();

		int changeMethodsNum = 0;
		int newMethodsNum = 0;
		int deleteMethodsNum = 0;
		int statementsNum = 0;
		int newStatementsNum = 0;
		int changeStatementsNum = 0;
		int deleteStatementsNum = 0;

		int maxInnerCount = 0;
		int maxOuterCount = 0;
		int aveInnerCount = 0;
		int aveOuterCount = 0;

		int maxStatementNum = 0;
		int aveStatementNum = 0;

		for (ClassBean clazz : classBeanList) {

			int classInnerCount = clazz.getInnerCount();
			int classOuterCount = clazz.getOutterCount();
			aveInnerCount += classInnerCount;
			aveOuterCount += classOuterCount;
			if (maxInnerCount < classInnerCount) {
				maxInnerCount = classInnerCount;
			}
			if (maxOuterCount < classOuterCount) {
				maxOuterCount = classOuterCount;
			}

			newMethodsNum += clazz.getNewMethodNum();
			changeMethodsNum += clazz.getChangeMethodNum();
			deleteMethodsNum += clazz.getDeleteMethodNum();

			int statementNum = clazz.getAddStatementNodeTypeList().size()
					+ clazz.getChangeStatementNodeTypeList().size() + clazz.getDeleteStatementNodeTypeList().size();
			int newStatementNum = clazz.getAddStatementNodeTypeList().size();
			int changeStatementNum = clazz.getChangeStatementNodeTypeList().size();
			int deleteStatementNum = clazz.getDeleteStatementNodeTypeList().size();

			newStatementsNum += newStatementNum;
			changeStatementsNum += changeStatementNum;
			deleteStatementsNum += deleteStatementNum;

			aveStatementNum += statementNum;
			if (statementNum > maxStatementNum) {
				maxStatementNum = statementNum;
			}
		}

		int methodsNum = changeMethodsNum + newMethodsNum + deleteMethodsNum;
		statementsNum = newStatementsNum + changeStatementsNum + deleteStatementsNum;
		for (ClassBean clazz : classBeanList) {
			int classIndex = clazz.getClassIndex();
			List<Integer> vector = new ArrayList<Integer>();
			int classInnerCount = clazz.getInnerCount();
			int classOuterCount = clazz.getOutterCount();

			// 6.
			if (classInnerCount == 0) {
				vector.add(0);
			} else if (classInnerCount == 1) {
				vector.add(1);
			} else if (classInnerCount == 2) {
				vector.add(2);
			} else if (classInnerCount == 3) {
				vector.add(3);
			} else if (classInnerCount == 4) {
				vector.add(4);
			} else if (classInnerCount == 5) {
				vector.add(5);
			} else {
				vector.add(6);
			}

			// 7.
			if (classOuterCount == 0) {
				vector.add(0);
			} else if (classOuterCount == 1) {
				vector.add(1);
			} else if (classOuterCount == 2) {
				vector.add(2);
			} else if (classOuterCount == 3) {
				vector.add(3);
			} else if (classOuterCount == 4) {
				vector.add(4);
			} else if (classOuterCount == 5) {
				vector.add(5);
			} else {
				vector.add(6);
			}

			// 8:classInnerCount:maxInnerCount
			if (maxInnerCount == 0) {
				vector.add(0);
			} else {
				vector.add(classInnerCount * 5 / maxInnerCount);
			}

			// 9:classInnerCount:aveInnerCount
			int classInnerCount_aveInnerCount = 0;
			if (aveInnerCount > 0) {
				classInnerCount_aveInnerCount = classInnerCount * classBeanList.size() / aveInnerCount;
			}
			if (classInnerCount_aveInnerCount < 5) {
				vector.add(classInnerCount_aveInnerCount);
			} else {
				vector.add(5);
			}

			// 10:classOuterCount:maxOuterCount
			if (maxOuterCount == 0) {
				vector.add(0);
			} else {
				vector.add(classOuterCount * 5 / maxOuterCount);
			}

			// 11:classOuterCount:aveOuterCount
			int classOuterCount_aveOuterCount = 0;
			if (aveOuterCount > 0) {
				classOuterCount_aveOuterCount = classOuterCount * classBeanList.size() / aveOuterCount;
			}
			if (classOuterCount_aveOuterCount < 5) {
				vector.add(classOuterCount_aveOuterCount);
			} else {
				vector.add(5);
			}

			// 12.ClassType
			if (clazz.getClassType().equals("change")) {
				vector.add(1);
			} else {
				vector.add(0);
			}

			// 13:classIndex
			if (classIndex < 5) {
				vector.add(classIndex);
			} else {
				vector.add(5);
			}
			// 14.isCoreType1
			if (classInnerCount == 0 && classOuterCount > 0) {
				vector.add(1);
			} else if (classInnerCount > 0 && classOuterCount > 0) {
				vector.add(2);
			} else if (classInnerCount == 0 && classOuterCount == 0) {
				vector.add(3);
			} else {
				vector.add(4);
			}

			// 15.isCoreType2
			if (classOuterCount == 0 && classInnerCount > 1) {
				vector.add(1);
			} else {
				vector.add(0);
			}

			// 16.isCoreType3
			if (classInnerCount + classOuterCount > 3) {
				vector.add(1);
			} else {
				vector.add(0);
			}

			// 17.isCoreType4
			if (classOuterCount - classInnerCount > 2) {
				vector.add(1);
			} else if (classOuterCount - classInnerCount == 2) {
				vector.add(2);
			} else if (classOuterCount - classInnerCount == 1) {
				vector.add(3);
			} else if (classOuterCount - classInnerCount == 0) {
				vector.add(4);
			} else if (classOuterCount - classInnerCount == -1) {
				vector.add(5);
			} else if (classOuterCount - classInnerCount == -2) {
				vector.add(6);
			} else {
				vector.add(7);
			}

			// 18.isCoreType5
			if (classInnerCount - classOuterCount > 2) {
				vector.add(1);
			} else {
				vector.add(0);
			}

			// 19.
			for (int i = 0; i < 1; i++) {
				double classInnerWeight = classInnerCount * 1.0d / classBeanList.size();
				if (classInnerWeight == 0) {
					vector.add(0);
				} else if (classInnerWeight <= 0.1) {
					vector.add(1);
				} else if (classInnerWeight <= 0.2) {
					vector.add(2);
				} else if (classInnerWeight <= 0.3) {
					vector.add(3);
				} else if (classInnerWeight <= 0.4) {
					vector.add(4);
				} else if (classInnerWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 20.
			double classOuterWeight = classOuterCount * 1.0d / classBeanList.size();
			if (classOuterWeight == 0) {
				vector.add(0);
			} else if (classOuterWeight <= 0.1) {
				vector.add(1);
			} else if (classOuterWeight <= 0.2) {
				vector.add(2);
			} else if (classOuterWeight <= 0.3) {
				vector.add(3);
			} else if (classOuterWeight <= 0.4) {
				vector.add(4);
			} else if (classOuterWeight <= 0.5) {
				vector.add(5);
			} else {
				vector.add(6);
			}

			// 21.
			if (classBeanList.size() == 2) {
				vector.add(0);
			} else if (classBeanList.size() == 3) {
				vector.add(1);
			} else if (classBeanList.size() == 4) {
				vector.add(2);
			} else if (classBeanList.size() == 5) {
				vector.add(3);
			} else if (classBeanList.size() > 5 && classBeanList.size() <= 10) {
				vector.add(4);
			} else {
				vector.add(5);
			}

			int newMethodNum = (Integer) clazz.getNewMethodNum();
			int changeMethodNum = (Integer) clazz.getChangeMethodNum();
			int deleteMethodNum = (Integer) clazz.getDeleteMethodNum();

			int methodNum = newMethodNum + changeMethodNum + deleteMethodNum;

			// 22.
			if (methodNum <= 5) {
				vector.add(methodNum);
			} else if (methodNum <= 10) {
				vector.add(6);
			} else if (methodNum <= 20) {
				vector.add(7);
			} else {
				vector.add(8);
			}

			// 23.
			if (methodsNum == 0) {
				vector.add(0);
			} else {
				double methodNumWeight = methodNum * 1.0d / methodsNum;
				if (methodNumWeight == 0) {
					vector.add(0);
				} else if (methodNumWeight <= 0.1) {
					vector.add(1);
				} else if (methodNumWeight <= 0.2) {
					vector.add(2);
				} else if (methodNumWeight <= 0.3) {
					vector.add(3);
				} else if (methodNumWeight <= 0.4) {
					vector.add(4);
				} else if (methodNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 24.
			if (newMethodNum <= 5) {
				vector.add(newMethodNum);
			} else if (newMethodNum <= 10) {
				vector.add(6);
			} else {
				vector.add(7);
			}

			// 25.
			if (newMethodsNum == 0) {
				vector.add(0);
			} else {
				double newMethodNumWeight = newMethodNum * 1.0d / newMethodsNum;
				if (newMethodNumWeight == 0) {
					vector.add(0);
				} else if (newMethodNumWeight <= 0.1) {
					vector.add(1);
				} else if (newMethodNumWeight <= 0.2) {
					vector.add(2);
				} else if (newMethodNumWeight <= 0.3) {
					vector.add(3);
				} else if (newMethodNumWeight <= 0.4) {
					vector.add(4);
				} else if (newMethodNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 26.
			if (changeMethodNum <= 5) {
				vector.add(changeMethodNum);
			} else if (changeMethodNum <= 10) {
				vector.add(6);
			} else {
				vector.add(7);
			}

			// 27.
			if (changeMethodsNum == 0) {
				vector.add(0);
			} else {
				double changeMethodNumWeight = changeMethodNum * 1.0d / changeMethodsNum;
				if (changeMethodNumWeight == 0) {
					vector.add(0);
				} else if (changeMethodNumWeight <= 0.1) {
					vector.add(1);
				} else if (changeMethodNumWeight <= 0.2) {
					vector.add(2);
				} else if (changeMethodNumWeight <= 0.3) {
					vector.add(3);
				} else if (changeMethodNumWeight <= 0.4) {
					vector.add(4);
				} else if (changeMethodNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 28.
			if (deleteMethodNum <= 3) {
				vector.add(deleteMethodNum);
			} else if (deleteMethodNum <= 5) {
				vector.add(4);
			} else if (deleteMethodNum <= 10) {
				vector.add(5);
			} else {
				vector.add(6);
			}

			// 29.
			if (deleteMethodsNum == 0) {
				vector.add(0);
			} else {
				double deleteMethodNumWeight = deleteMethodNum * 1.0d / deleteMethodsNum;
				if (deleteMethodNumWeight == 0) {
					vector.add(0);
				} else if (deleteMethodNumWeight <= 0.1) {
					vector.add(1);
				} else if (deleteMethodNumWeight <= 0.2) {
					vector.add(2);
				} else if (deleteMethodNumWeight <= 0.3) {
					vector.add(3);
				} else if (deleteMethodNumWeight <= 0.4) {
					vector.add(4);
				} else if (deleteMethodNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			int methodsInnerCount = 0;
			int methodsOuterCount = 0;
			int changeStatementNum = clazz.getChangeStatementNodeTypeList().size();
			int newStatementNum = clazz.getAddStatementNodeTypeList().size();
			int deleteStatementNum = clazz.getDeleteStatementNodeTypeList().size();
			int statementNum = newStatementNum + changeStatementNum + deleteStatementNum;

			//List<MethodBean> methodList = methodRepo.findByVersionIDAndClassID(versionID, clazz.getClassID());
			List<MethodBean> methodList = findMehodByClassID(clazz.getClassID());
			
			for (MethodBean method : methodList) {
				methodsInnerCount += method.getInnerCount();
				methodsOuterCount += method.getOutterCount();

			}

			// 30.
			if (methodsInnerCount == 0) {
				vector.add(0);
			} else if (methodsInnerCount <= 3) {
				vector.add(1);
			} else if (methodsInnerCount <= 6) {
				vector.add(2);
			} else {
				vector.add(3);
			}

			// 31.
			if (methodsOuterCount == 0) {
				vector.add(0);
			} else if (methodsOuterCount <= 3) {
				vector.add(1);
			} else if (methodsOuterCount <= 6) {
				vector.add(2);
			} else {
				vector.add(3);
			}

			// 32.
			if (statementNum <= 5) {
				vector.add(statementNum);
			} else if (statementNum <= 10) {
				vector.add(6);
			} else if (statementNum <= 20) {
				vector.add(7);
			} else if (statementNum <= 30) {
				vector.add(8);
			} else if (statementNum <= 40) {
				vector.add(9);
			} else {
				vector.add(10);
			}

			// 33.
			if (statementsNum == 0) {
				vector.add(0);
			} else {
				double statementNumWeight = statementNum * 1.0d / statementsNum;
				if (statementNumWeight == 0) {
					vector.add(0);
				} else if (statementNumWeight <= 0.1) {
					vector.add(1);
				} else if (statementNumWeight <= 0.2) {
					vector.add(2);
				} else if (statementNumWeight <= 0.3) {
					vector.add(3);
				} else if (statementNumWeight <= 0.4) {
					vector.add(4);
				} else if (statementNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 34. statementNum/maxStatementNum
			if (maxStatementNum == 0) {
				vector.add(0);
			} else {
				vector.add(statementNum * 5 / maxStatementNum);
			}

			// 35. statementNum/aveStatementNum
			int statementNum_aveStatementNum = 0;
			if (aveStatementNum > 0) {
				statementNum_aveStatementNum = statementNum * classBeanList.size() / aveStatementNum;
			}
			if (statementNum_aveStatementNum < 5) {
				vector.add(statementNum_aveStatementNum);
			} else {
				vector.add(5);
			}

			// 36
			if (newStatementNum == 0) {
				vector.add(0);
			} else if (newStatementNum <= 5) {
				vector.add(newStatementNum);
			} else if (newStatementNum <= 10) {
				vector.add(6);
			} else if (newStatementNum <= 20) {
				vector.add(7);
			} else {
				vector.add(8);
			}

			// 37
			if (newStatementsNum == 0) {
				vector.add(0);
			} else {
				double newStatementNumWeight = newStatementNum * 1.0d / newStatementsNum;
				if (newStatementNumWeight == 0) {
					vector.add(0);
				} else if (newStatementNumWeight <= 0.1) {
					vector.add(1);
				} else if (newStatementNumWeight <= 0.2) {
					vector.add(2);
				} else if (newStatementNumWeight <= 0.3) {
					vector.add(3);
				} else if (newStatementNumWeight <= 0.4) {
					vector.add(4);
				} else if (newStatementNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 38
			if (changeStatementNum == 0) {
				vector.add(0);
			} else if (changeStatementNum <= 5) {
				vector.add(changeStatementNum);
			} else if (changeStatementNum <= 10) {
				vector.add(6);
			} else {
				vector.add(7);
			}

			// 39
			if (changeStatementsNum == 0) {
				vector.add(0);
			} else {
				double changeStatementNumWeight = changeStatementNum * 1.0d / changeStatementsNum;
				if (changeStatementNumWeight == 0) {
					vector.add(0);
				} else if (changeStatementNumWeight <= 0.1) {
					vector.add(1);
				} else if (changeStatementNumWeight <= 0.2) {
					vector.add(2);
				} else if (changeStatementNumWeight <= 0.3) {
					vector.add(3);
				} else if (changeStatementNumWeight <= 0.4) {
					vector.add(4);
				} else if (changeStatementNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			// 40
			if (deleteStatementNum == 0) {
				vector.add(0);
			} else if (deleteStatementNum <= 3) {
				vector.add(deleteStatementNum);
			} else if (deleteStatementNum <= 10) {
				vector.add(4);
			} else {
				vector.add(5);
			}

			// 41
			if (deleteStatementsNum == 0) {
				vector.add(0);
			} else {
				double deleteStatementNumWeight = deleteStatementNum * 1.0d / deleteStatementsNum;
				if (deleteStatementNumWeight == 0) {
					vector.add(0);
				} else if (deleteStatementNumWeight <= 0.1) {
					vector.add(1);
				} else if (deleteStatementNumWeight <= 0.2) {
					vector.add(2);
				} else if (deleteStatementNumWeight <= 0.3) {
					vector.add(3);
				} else if (deleteStatementNumWeight <= 0.4) {
					vector.add(4);
				} else if (deleteStatementNumWeight <= 0.5) {
					vector.add(5);
				} else {
					vector.add(6);
				}
			}

			vector.add(0);
			vectorList.add(vector);
		}
		return vectorList;
	}

	/**
	 *	����classID��ȡ����methodBean 
	 */
	public List<MethodBean> findMehodByClassID(int classID){
		
		List<MethodBean> methodBeans = new ArrayList<MethodBean>();
		for(MethodBean bean: methodBeanList) {
			if(bean.getClassID()==classID) {
				methodBeans.add(bean);
			}
		}
		return methodBeans;
	}
	
	

	
	public static List<String> getCommitPath(String DataPath) {
		List<String> commitPath = new ArrayList<String>();
		File f=new File(DataPath);
		File [] commits=f.listFiles();
			for(File commit:commits) {
				if(commit.isDirectory()) {
					commitPath.add(commit.getPath());
				}
				
			}
		return commitPath;
	}
	
	public void writeCoreClass(List<Map.Entry<String, Double>> list, String path) {
	      try {
	    	  File file = new File(path+"/coreclass.txt");
	    	  if(!file.exists()) {
	          FileWriter fw = new FileWriter(path+"\\coreclass.txt", true);  
	          BufferedWriter bw = new BufferedWriter(fw);
	          for (Entry<String, Double> mapping : list) {
		          bw.write(mapping.getKey() +':' + mapping.getValue() +"\r\n");
	          }	            
	    	  
	          bw.close();  
	          fw.close(); 
	          System.out.println("Successfully write coreclass " + path + "\\coreclass.txt");
	    	  }
	      } catch (Exception e) {  
	          // TODO Auto-generated catch block  
	          e.printStackTrace();  
	      }  
	      
	      try {
	    	  File file1 = new File(path+"/coreclassName.txt");
	    	  if(!file1.exists()) {
	          FileWriter fw1 = new FileWriter(path+"\\coreclassName.txt", true);  
	          BufferedWriter bw1 = new BufferedWriter(fw1);
	          for (Entry<String, Double> mapping : list) {
		          bw1.write(mapping.getKey()+"\r\n");
	          }	            
	    	  
	          bw1.close();  
	          fw1.close(); 
	          System.out.println("Successfully write coreclassName " + path + "\\coreclass.txt");
	          System.out.println("");
	    	  }
	      } catch (Exception e) {  
	          // TODO Auto-generated catch block  
	          e.printStackTrace();  
	      }  
	}

	public static void saveFeatureVec(String vectors, String path) {
		try {
			File file = new File(path + "/featurevec.txt");
			if(!file.exists()) {
				FileWriter fw = new FileWriter(path + "/featurevec.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(vectors);
				bw.close();
				fw.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	public static void main(String[] args) {	
		String DataPath="C:\\Users\\jjy\\Desktop\\ImpactAnalysis\\code\\sample_project_commit\\aeron\\";
		List<String> commitPath= getCommitPath(DataPath);
		int count = commitPath.size();
		int finish = 0;
		for(String path:commitPath) {
			finish ++;
			System.out.println("computing " + path);
			File testFile=new File(path);
			if(!testFile.isDirectory()) continue;
			// File to save coreclass result
			File file = new File(path+"/coreclass.txt");
	    	  if(!file.exists()) {
	    		  try {
	    			CoreClass coreClass = new CoreClass();  
	    			
	    			// The parameter is the old code and new code path of this commit
					coreClass.initClassBeanListAndMeanBeanList(path + "/old", path + "/new");
					
					Map<String, Double> map = coreClass.getProbabilities();
	
					SortHashmap sort = new SortHashmap();
					List<Map.Entry<String, Double>> list = sort.sortMap(map);
					System.out.println(list);
					// Save the Information of the coreclass
					coreClass.writeCoreClass(list, path);
	    		  	}catch (Exception e) {
	    		  		// TODO: handle exception
	    		  		e.printStackTrace();
	    		  		System.out.println("IllegalArgumentException: " + path);
	    		  		continue;
	    		  		}
		}
	  }
	}
}
