package sysu.tool;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import sysu.bean.StatementBean;



//��¼method�ı仯
/**
 * A data class for method changes. It consists only of getter and setter
 * methods.
 * <p>
 * It provides access to:
 * <ul>
 * <li>new method declaration; and</li>
 * <li>old method declaration.</li>
 * </ul>
 * <p>
 * It allows managing:
 * <ul>
 * <li>new method; and</li>
 * <li>old method.</li>
 * </ul>
 */
public class MethodChange {

	private MethodDeclaration oldMethod;
	private MethodDeclaration newMethod;

	private List<Statement> changedStatements;
	private List<Statement> addedStatements;
	private List<Statement> deletedStatements;
	
	private List<StatementBean> statementList = new ArrayList<StatementBean>();

	// states״̬˵����
	// 0����ʼ״̬
	// 1��ƥ��״̬
	// 2������״̬
	// 3��ɾ��״̬
	// 4: �޸�״̬
	private int[] oldStates;
	private int[] newStates;

	
	
	
	public List<StatementBean> getStatementList() {
		return statementList;
	}

	public void setStatementList(List<StatementBean> statementList) {
		this.statementList = statementList;
	}

	public MethodDeclaration getOldMethod() {
		return oldMethod;
	}

	public void setOldMethod(MethodDeclaration oldMethod) {
		this.oldMethod = oldMethod;
	}

	public MethodDeclaration getNewMethod() {
		return newMethod;
	}

	public void setNewMethod(MethodDeclaration newMethod) {
		this.newMethod = newMethod;
	}

	public MethodChange(MethodDeclaration oldMethod, MethodDeclaration newMethod) {
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
		 if(oldMethod.getBody()!=null&&newMethod.getBody()!=null){
		 compareMethod();
		 }else if(oldMethod.getBody()!=null){
			 for(int i=0;i<oldMethod.getBody().statements().size();i++){
				 Statement sta = (Statement)oldMethod.getBody().statements().get(i);
				 addDeletedStatement(sta);
				 
				 StatementBean statementBean = new StatementBean();
				 statementBean.setStatement(sta.toString());
				 statementBean.setStatementeType("delete");
				 statementBean.setStatementID(i);
				 statementList.add(statementBean);
				 
				 
			 }
		 }else{
			 for(int i=0;i<newMethod.getBody().statements().size();i++){
				 Statement sta = (Statement)newMethod.getBody().statements().get(i);
				 addAddedStatement(sta);
				 
				 StatementBean statementBean = new StatementBean();
				 statementBean.setStatement(sta.toString());
				 statementBean.setStatementeType("add");
				 statementBean.setStatementID(i);
				 statementList.add(statementBean);
				 
				 
			 }
		 }
	}

	public void addChangedStatement(Statement s) {
		if (changedStatements == null) {
			changedStatements = new ArrayList<Statement>();
		}
		changedStatements.add(s);
	}

	public void addAddedStatement(Statement s) {
		if (addedStatements == null) {
			addedStatements = new ArrayList<Statement>();
		}
		addedStatements.add(s);
	}

	public void addDeletedStatement(Statement s) {
		if (deletedStatements == null) {
			deletedStatements = new ArrayList<Statement>();
		}
		deletedStatements.add(s);
	}

	public int[] getOldStates() {
		return oldStates;
	}

	public void setOldStates(int[] oldStates) {
		this.oldStates = oldStates;
	}

	public int[] getNewStates() {
		return newStates;
	}

	public void setNewStates(int[] newStates) {
		this.newStates = newStates;
	}

	public List<Statement> getChangedStatements() {
		return changedStatements;
	}

	public void setChangedStatements(List<Statement> changedStatements) {
		this.changedStatements = changedStatements;
	}

	public List<Statement> getAddedStatements() {
		return addedStatements;
	}

	public void setAddedStatements(List<Statement> addedStatements) {
		this.addedStatements = addedStatements;
	}

	public List<Statement> getDeletedStatements() {
		return deletedStatements;
	}

	public void setDeletedStatements(List<Statement> deletedStatements) {
		this.deletedStatements = deletedStatements;
	}

	public void compareMethod() {

		List<Statement> oldStatements = oldMethod.getBody().statements();
		List<Statement> newStatements = newMethod.getBody().statements();

//		System.out.println("oldStatements:");
//		for (int i = 0; i < oldStatements.size(); i++) {
//			System.out.println(i + ":" + oldStatements.get(i));
//		}
//		System.out.println("newStatements:");
//		for (int i = 0; i < newStatements.size(); i++) {
//			System.out.println(i + ":" + newStatements.get(i));
//		}

		oldStates = new int[oldStatements.size()];
		for (int i = 0; i < oldStatements.size(); i++) {
			oldStates[i] = 0;
		}
		newStates = new int[newStatements.size()];
		for (int i = 0; i < newStatements.size(); i++) {
			newStates[i] = 0;
		}

		for (int i = 0; i < newStatements.size(); i++) {
			int jIndex = 0;
			boolean flag = false;
			while (jIndex < oldStates.length - 1 && oldStates[jIndex] != 0) {
				jIndex++;
			}
			for (int j = jIndex; j < oldStatements.size(); j++) {
				if (newStatements.get(i).toString().equals(oldStatements.get(j).toString())) {
					newStates[i] = 1;
					oldStates[j] = 1;
					flag = true;

					for (int k = jIndex; k < j; k++) {
						oldStates[k] = 3;
					}

					break;
				}
			}
			if (!flag) {
				newStates[i] = 2;
			}
		}
		for (int i = 0; i < oldStatements.size(); i++) {
			if (oldStates[i] == 0) {
				oldStates[i] = 3;
			}
		}

		if (newStates.length > 0 && oldStates.length > 0) {
			if (newStates[0] != 1) {
				if (oldStates[0] != 1) {

					int newIndex = 0;
					while (newIndex < newStates.length && newStates[newIndex] != 1) {
						newStates[newIndex] = 4;
						newIndex++;
					}

					int oldIndex = 0;
					while (oldIndex < oldStates.length && oldStates[oldIndex] != 1) {
						oldStates[oldIndex] = 4;
						oldIndex++;
					}
				}

			} else {
				int newIndex = 0;
				int oldIndex = 0;
				int new1count = 0;
				int old1count = 0;
				while (newIndex < newStates.length && oldIndex < oldStates.length) {
					while (newIndex < newStates.length && newStates[newIndex] == 1) {
						new1count++;
						newIndex++;
					}
					while (oldIndex < oldStates.length && oldStates[oldIndex] == 1) {
						old1count++;
						oldIndex++;
					}
					while (new1count < old1count&&newIndex<newStates.length) {

						if (newStates[newIndex] == 1) {
							new1count++;
						}
						newIndex++;
					}
					while (old1count < new1count&&oldIndex<oldStates.length) {

						if (oldStates[oldIndex] == 1) {
							old1count++;
						}
						oldIndex++;
					}
					if (newIndex < newStates.length && oldIndex < oldStates.length && newStates[newIndex] != 1
							&& oldStates[oldIndex] != 1) {
						while (newIndex < newStates.length && newStates[newIndex] != 1) {
							newStates[newIndex] = 4;
							newIndex++;
						}
						while (oldIndex < oldStates.length && oldStates[oldIndex] != 1) {
							oldStates[oldIndex] = 4;
							oldIndex++;
						}
					}
				}
			}
		}

		for (int i = 0; i < newStates.length; i++) {
			
			if (newStates[i] == 2) {
				StatementBean statementBean = new StatementBean();
				statementBean.setStatement(newStatements.get(i).toString());
				statementBean.setStatementeType("add");
				statementBean.setStatementID(i);
				statementList.add(statementBean);
				addAddedStatement(newStatements.get(i));
			}
			if (newStates[i] == 4) {
				StatementBean statementBean = new StatementBean();
				statementBean.setStatement(newStatements.get(i).toString());
				statementBean.setStatementeType("change");
				statementBean.setStatementID(i);
				statementList.add(statementBean);
				addChangedStatement(newStatements.get(i));
			}
		}
		for (int i = 0; i < oldStates.length; i++) {
			if (oldStates[i] == 3) {
				StatementBean statementBean = new StatementBean();
				statementBean.setStatement(oldStatements.get(i).toString());
				statementBean.setStatementeType("delete");
				statementBean.setStatementID(i);
				statementList.add(statementBean);
				addDeletedStatement(oldStatements.get(i));
			}
		}
	}

}
