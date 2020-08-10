package sysu.sei.reverse.clusterRelatedClass.XMLModel;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DesignDecision")
public class DesignDecision {
	private List<ClassInfo> classInfo;

	public DesignDecision(List<ClassInfo> classInfo) {
		super();
		this.classInfo = classInfo;
	}

	public List<ClassInfo> getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(List<ClassInfo> classInfo) {
		this.classInfo = classInfo;
	}
	

}
