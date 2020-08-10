package sysu.sei.reverse.clusterRelatedClass.XMLModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Clusters")
public class Clusters {
private Head head;
private Body body;

public Clusters() {
}
public Clusters(Head head, Body body) {
	super();
	this.head = head;
	this.body = body;
}
public Head getHead() {
	return head;
}
public void setHead(Head head) {
	this.head = head;
}
public Body getBody() {
	return body;
}
public void setBody(Body body) {
	this.body = body;
}


}
