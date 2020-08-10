package sysu.sei.reverse.clusterRelatedClass.XMLModel;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("Body")
public class Body implements Serializable{
	public Body(){
		
	}
	public Body(List<DesignDecision> designDecision) {
		super();
		this.designDecision = designDecision;
	}

	private List<DesignDecision> designDecision;

	public List<DesignDecision> getClusterModel() {
		return designDecision;
	}

	public void setClusterModel(List<DesignDecision> clusterModel) {
		this.designDecision = clusterModel;
	}
	
}
