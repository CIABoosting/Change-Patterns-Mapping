package sysu.sei.reverse.clusterRelatedClass.XMLModel;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ClassInfo")
public class ClassInfo implements Serializable{
	private String className;
	private String packagePath;
	
	public ClassInfo(String className, String packagePath) {
		super();
		this.className = className;
		this.packagePath = packagePath;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}


}
