package sysu.sei.reverse.designDecision;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassRevisionHistory {

	//���޸ĵĴ���
	int revisionTime;
	//���޸��漰�İ汾�б�
	ArrayList<Integer> versions;
	//���޸ļ�¼
	ClassChange classa;
	
	
	public ArrayList<Integer> getVersions() {
		if(versions == null)
			versions = new ArrayList<Integer>();
		return versions;
	}

	public void setVersions(ArrayList<Integer> verisions) {
		
		this.versions = verisions;
	}
	
	public ClassRevisionHistory(){
		revisionTime = 0;
	}
	
	public int getRevisionTime() {
		return revisionTime;
	}
	public void setRevisionTime(int revisionTime) {
		this.revisionTime = revisionTime;
	}
	
	public void addRevisionTime(){
		this.revisionTime = this.revisionTime+1;
	}
	public ClassChange getClassa() {
		return classa;
	}
	public void setClassa(ClassChange classa) {
		this.classa = classa;
	}
	
	public void addRevisionVersion(int i){
		this.getVersions().add(new Integer(i));
		this.addRevisionTime();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
