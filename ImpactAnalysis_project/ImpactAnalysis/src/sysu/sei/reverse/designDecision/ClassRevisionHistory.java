package sysu.sei.reverse.designDecision;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassRevisionHistory {

	//类修改的次数
	int revisionTime;
	//类修改涉及的版本列表
	ArrayList<Integer> versions;
	//类修改记录
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
