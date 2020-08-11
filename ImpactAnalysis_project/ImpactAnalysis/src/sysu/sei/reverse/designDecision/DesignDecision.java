package sysu.sei.reverse.designDecision;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.TypeDeclaration;

//���ڼ�¼��ƾ���
public class DesignDecision {

// ��ص��ඨ��	
	ArrayList<ClassRevisionHistory> involvingClassList ;
	
	
	public ArrayList<ClassRevisionHistory> getInvolvingClassList() {
		if(involvingClassList == null)
			involvingClassList = new ArrayList<ClassRevisionHistory>();
		return involvingClassList;
	}


	public void setInvolvingClassList(
			ArrayList<ClassRevisionHistory> involvingClassList) {
		this.involvingClassList = involvingClassList;
	}

	public void addInvolvingClass(ClassChange classa, int version){
		ClassRevisionHistory newClass = new ClassRevisionHistory();
		newClass.setClassa(classa);
		newClass.addRevisionVersion(version);
		this.getInvolvingClassList().add(newClass);
	}

	public void addInvolvingClasses(List<ClassChange> classList, int version){
		for(int i = 0; i < classList.size(); i++){

			ClassRevisionHistory newClass = new ClassRevisionHistory();
		
			newClass.setClassa(classList.get(i));
		
			newClass.addRevisionVersion(version);
			this.getInvolvingClassList().add(newClass);
		}
	}


}
