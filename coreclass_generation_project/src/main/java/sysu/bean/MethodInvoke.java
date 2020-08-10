package sysu.bean;

public class MethodInvoke {

	private String method1;
	private String method2;

	private String method1_type;
	private String method2_type;
	private String class1;
	private String class2;

	public MethodInvoke(String class1, String method1, String class2, String method2, int class1Id, int class2Id) {
		this.method1 = method1;
		this.method2 = method2;
		this.class1 = class1;
		this.class2 = class2;
	}

	public MethodInvoke() {

	}

	public String getMethod1_type() {
		return method1_type;
	}

	public void setMethod1_type(String method1_type) {
		this.method1_type = method1_type;
	}

	public String getMethod2_type() {
		return method2_type;
	}

	public void setMethod2_type(String method2_type) {
		this.method2_type = method2_type;
	}

	public String getMethod1() {
		return method1;
	}

	public void setMethod1(String method1) {
		this.method1 = method1;
	}

	public String getMethod2() {
		return method2;
	}

	public void setMethod2(String method2) {
		this.method2 = method2;
	}

	public String getClass1() {
		return class1;
	}

	public void setClass1(String class1) {
		this.class1 = class1;
	}

	public String getClass2() {
		return class2;
	}

	public void setClass2(String class2) {
		this.class2 = class2;
	}

}
