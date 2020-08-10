package sysu.sei.reverse.designDecision;

import org.eclipse.jdt.core.dom.MethodDeclaration;

//记录method的变化
public class MethodChange {

	MethodDeclaration oldMethod; 
	MethodDeclaration newMethod; 
	
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



	public MethodChange(MethodDeclaration oldMethod, MethodDeclaration newMethod ){
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
	}
	
	
	

	


}
