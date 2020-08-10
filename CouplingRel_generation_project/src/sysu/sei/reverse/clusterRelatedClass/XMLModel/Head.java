package sysu.sei.reverse.clusterRelatedClass.XMLModel;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Head")
public class Head implements Serializable{

	private String oldVersion;
	private String newVersion;
	private String projectName;
	
	public Head(){
		
	}

	public Head(String oldVersion, String newVersion, String projectName) {
		super();
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		this.projectName = projectName;
	}
	
	public String getOldVersion() {
		return oldVersion;
	}
	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}
	public String getNewVersion() {
		return newVersion;
	}
	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
