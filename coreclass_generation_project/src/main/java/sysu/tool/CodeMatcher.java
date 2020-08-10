package sysu.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * A boundary class for code matchers. It communicates with the classes
 * {@link ArrayList}, and CodeMatcher}.
 * <p>
 * It allows:
 * <ul>
 * <li>handling useof method;</li>
 * <li>processing code matcher code matcher if W museclassdef;</li>
 * <li>getting variable;</li>
 * <li>processing code matcher code matcher if include attribute list;</li>
 * <li>ifnewing para;</li>
 * <li>processing code matcher code matcher if W minstanceof;</li>
 * <li>deleting blank;</li>
 * <li>processing code matcher code matcher if changeoftype;</li>
 * <li>handling if word matched;</li>
 * <li>getting sequencewords index;</li>
 * <li>handling method list include;</li>
 * <li>ifnewing para 2; and</li>
 * <li>processing code matcher code matcher if W msequencewords.</li>
 * </ul>
 */
public class CodeMatcher {
	// ����name�Ƿ���methodBody��
	public static boolean ifWordMatched(String methodBody, String name) {
		boolean tag = false;
		if (methodBody != null && methodBody.indexOf(name) >= 0
				&& methodBody.substring(methodBody.indexOf(name) - 1, methodBody.indexOf(name))
						.matches("^[a-zA-Z]*") == false
				&& (methodBody.indexOf(name) + name.length() <= methodBody.length())
				&& methodBody.substring(methodBody.indexOf(name) + name.length(),
						methodBody.indexOf(name) + name.length() + 1).matches("^[a-zA-Z]*") == false) {
			tag = true;
		}
		return tag;
	}

	// ����sm���Ƿ��ж�classname����instanceof
	public static boolean ifWMinstanceof(String sm, String className) {
		boolean tag = false;
		if (sm != null && className != null && sm.indexOf("instanceof") >= 0 && sm.indexOf(className) >= 0
				&& ifWMsequencewords(sm, "instanceof", className)) {
			tag = true;
		}
		return tag;
	}

	// �����Ƿ����������ת��?
	public static boolean ifChangeoftype(String sm, String className) {

		boolean tag = false;
		int index = 0;
		if (sm.indexOf(className) >= 0 && ifWMsequencewords(sm, className, ")")
				&& ifWMsequencewords(sm, "(", className)) {
			sm = sm.substring(sequencewordsIndex(sm, className, ")"));
			index = sm.indexOf(")") + 1;
			while (sm.substring(index, index + 1).equals("(")) {
				index++;
			}
			if (sm.substring(index, index + 1).matches("^[a-zA-Z]*")) {
				tag = true;
			}
		}

		return tag;
	}

	public static List<String> changeoftype(String sm, String className) {
		List<String> var1s = new ArrayList<String>();

		int index1, index2, index3;
		index1 = sm.indexOf(className);
		while (index1 > 0) {
			if (sm.substring(index1 - 1, index1).equals("(")
					&& sm.substring(index1 + className.length(), index1 + className.length() + 1).equals(")")) {
				String var1;
				index2 = index1 + className.length() + 1;
				sm = sm.substring(index2);
				index3 = sm.indexOf(".");
				if (index2 < sm.length() && index3 >= 0) {
					var1 = sm.substring(0, index3);
					var1s.add(var1);
					sm = sm.substring(index3);
				}

				index1 = sm.indexOf(className);
			} else {
				sm = sm.substring(index1 + className.length());
				index1 = sm.indexOf(className);
			}
		}

		return var1s;
	}

	// �����Ƿ�ʹ����classname��.class
	public static boolean ifWMuseclassdef(String sm, String className) {

		boolean tag = false;
		if (sm != null && className != null && sm.indexOf(className) >= 0
				&& ifWMsequencewords(sm, className, ".class")) {// ifWMsequencewords(sm,className,".")&&
																// ifWMsequencewords(sm,".","class")){
			tag = true;
		}
		return tag;
	}

	// ����sm���Ƿ���word1���������word2���ַ�����word1��word2֮��ֻ�����N���ո�
	public static boolean ifWMsequencewords(String sm, String word1, String word2) {
		String statement = new String(sm);
		boolean tag = false;
		while (tag == false && statement != null && word1 != null && word2 != null && statement.indexOf(word1) >= 0
				&& statement.substring(statement.indexOf(word1)).indexOf(word2) >= 0) {
			tag = true;
			if (statement.indexOf(word1) + word1.length() < statement.indexOf(word2)) {
				for (int i = statement.indexOf(word1) + word1.length(); i < statement.indexOf(word2); i++) {
					if (!statement.subSequence(i, i + 1).equals(" ")) {
						tag = false;
					}
				}
				if (tag == false) {
					statement = statement.substring(statement.indexOf(word1) + 1);
				}
			}

			else {
				if (statement.indexOf(word1) + word1.length() == statement.indexOf(word2)) {
					statement = null;
				} else {
					statement = statement.substring(statement.indexOf(word1));
					// System.out.println(statement);
					tag = false;
				}
			}

		}
		return tag;
	}

	public static int sequencewordsIndex(String sm, String word1, String word2) {
		String statement = new String(sm);
		int tag = -1;
		while (tag == -1 && statement != null && word1 != null && word2 != null && statement.indexOf(word1) >= 0
				&& statement.substring(statement.indexOf(word1)).indexOf(word2) >= 0) {
			tag = statement.indexOf(word1);
			if (statement.indexOf(word1) + word1.length() < statement.indexOf(word2)) {

				for (int i = statement.indexOf(word1) + word1.length(); i < statement.indexOf(word2); i++) {
					if (!statement.subSequence(i, i + 1).equals(" ")) {
						tag = -1;
					}
				}
				if (tag == -1) {
					statement = statement.substring(statement.indexOf(word1) + 1);
				}
			}

			else {
				if (statement.indexOf(word1) + word1.length() == statement.indexOf(word2)) {
					statement = null;
				} else {
					statement = statement.substring(statement.indexOf(word1));
					tag = -1;
				}
			}
		}
		return tag;
	}

	public static String deleteBlank(String sm) {
		for (int i = 0; i < sm.length(); i++) {
			if (sm.substring(i, i + 1).equals(" ")) {
				if (i + 1 == sm.length()) {
					sm = sm.substring(0, i);
				} else
					sm = sm.substring(0, i) + sm.substring(i + 1);
			}
		}
		return sm;
	}

	public static String useofMethod(String sm, String mName, int paraSize) {
		String objectName = null;
		mName = mName + "(";
		if (sm.indexOf(mName) > 0 && ifWMsequencewords(sm, ".", mName)) {
			int index = sm.indexOf(mName) - 1;
			int endIndex = 0;
			while (index >= 0 && (sm.substring(index, index + 1).equals(" ")
					|| sm.substring(index, index + 1).equals(".") || sm.substring(index, index + 1).equals(")"))) {
				index--;
			}
			endIndex = index + 1;
			index--;
			while (index >= 0 && (!sm.substring(index, index + 1).equals(")"))
					&& (!sm.substring(index, index + 1).equals(" ")) && (!sm.substring(index, index + 1).equals("="))
					&& (!sm.substring(index, index + 1).equals("("))) {// matches("^[a-zA-Z]*")){
				index--;
			}
			index++;
			if(index<0||index>=endIndex)
				return null;
			objectName = sm.substring(index, endIndex);
			index = sm.indexOf(mName) + mName.length() - 2;
			endIndex = sm.indexOf(mName) + mName.length();

			while (endIndex < sm.length() && sm.substring(endIndex).indexOf("(") < sm.substring(endIndex).indexOf(")")
					&& sm.substring(endIndex).indexOf("(") >= 0 && sm.substring(endIndex).indexOf(")") >= 0) {

				sm = sm.substring(0, endIndex + sm.substring(endIndex).indexOf(")"))
						+ (sm.substring(sm.substring(endIndex).indexOf(")") + endIndex + 1));
				endIndex = sm.substring(endIndex).indexOf(")") + 1;
			}

			if (paraSize > 1) {
				for (int i = 0; i < paraSize - 1; i++) {
					if (index >= 0 && sm.length() - 2 > index) {
						sm = sm.substring(index + 1);
						if (sm.indexOf(",") < 0) {
							objectName = null;
							i = paraSize;
						} else
							index = sm.indexOf(",");
					}

					else {

						objectName = null;
						i = paraSize;
					}
				}
			} else {
				if (paraSize == 1&&index>=0&&index<sm.length()) {
					sm = sm.substring(index);
					if (ifWMsequencewords(sm, "(", ")") || (sm.indexOf(",") > 0 && sm.indexOf(",") < sm.indexOf(")"))) {
						objectName = null;
					}
				}
				if (paraSize == 0) {
					if (index <= sm.length() - 1) {
						sm = sm.substring(index);
						if (!ifWMsequencewords(sm, "(", ")")) {
							objectName = null;
						}
					}
				}
			}
		}
		return objectName;
	}

	public static String useofMethod(String sm, String mName) {
		String objectName = null;
		mName = mName + "(";
		if (sm.indexOf(mName) > 0 && ifWMsequencewords(sm, ".", mName)) {
			int index = sm.indexOf(mName) - 1;
			int endIndex = 0;
			while (index >= 0
					&& (sm.substring(index, index + 1).equals(" ") || sm.substring(index, index + 1).equals("."))) {
				index--;
			}
			endIndex = index + 1;
			index--;
			while (index >= 0 && (!sm.substring(index, index + 1).equals(" "))
					&& (!sm.substring(index, index + 1).equals("="))) {// matches("^[a-zA-Z]*")){
				index--;
			}
			index++;
			if (index >= 0 && endIndex >= index) {
				objectName = sm.substring(index, endIndex);
				index = sm.indexOf(mName) + mName.length();
			}
		}
		return objectName;
	}

	public static boolean methodListInclude(ArrayList<String> list, String mName) {
		boolean tag = false;
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				// ���ڵ��÷����Ķ���������ȡ����һЩ���⣬�ð�����ϵ��ȷ�������ʹ��?
				if (mName.equals(list.get(i))) {

					tag = true;
				}
			}
		}

		return tag;
	}

	// Ѱ��sm���Ƿ�����cNameΪclass�ı������壬���ر�������
	public static String ifnewPara2(String sm, String cName) {
		String pName = null;
		int endIndex = 0;
		if ((sm.indexOf(
				cName) == 0
				&& sm
						.substring(
								sm.indexOf(cName)
										+ cName.length(),
								sm.indexOf(
										cName)
										+ cName
												.length()
										+ 1)
						.equals(" "))
				|| (sm.indexOf(cName) > 0
						&& (sm.substring(sm.indexOf(cName) - 1, sm.indexOf(cName)).equals(" ")
								|| sm.substring(sm.indexOf(cName) - 1, sm.indexOf(cName)).equals(";"))
						&& sm.substring(sm.indexOf(cName) + cName.length(), sm.indexOf(cName) + cName.length() + 1)
								.equals(" "))) {
			int beginIndex = sm.indexOf(cName) + cName.length();
			if (sm.indexOf("=") > 0 && sm.indexOf("=") > beginIndex) {
				endIndex = sm.indexOf("=");
				pName = deleteBlank(sm.substring(beginIndex, endIndex));
			} else {
				if (sm.indexOf(";") > 0 && sm.indexOf(";") > beginIndex) {
					endIndex = sm.indexOf(";");
					pName = deleteBlank(sm.substring(beginIndex, endIndex));
				}
			}
		}
		return pName;
	}

	// ******************Add Method for ifnewPara2************************
	// ******************************************

	/**
	 * ����sm������ΪcName ���͵ı�����
	 * 
	 * @param sm
	 * @param cName
	 * @return
	 */
	public static List<String> ifnewPara2s(String body, String cName) {

		List<String> vars = new ArrayList<String>();

		String[] staments = body.split("\n");
		for (int i = 0; i < staments.length; i++) {
			String sm = staments[i];
			String var1 = CodeMatcher.ifnewPara2(sm, cName);
			if (var1 != null) {
				vars.add(var1);
			}

		}

		return vars;

	}

	// ******************************************
	// ******************************************

	public static String ifnewPara(String sm, String cName) {
		String pName = null;

		if (ifWMsequencewords(sm, "new", cName)) {
			pName = new String();
			if (ifWMsequencewords(sm.substring(0, sm.indexOf(cName)), "=", "new")) {

				int index = sm.substring(0, sm.indexOf(cName)).lastIndexOf("new") - 1;
				int endIndex = 0;
				while (index >= 0
						&& (sm.substring(index, index + 1).equals(" ") || sm.substring(index, index + 1).equals("="))) {
					index--;
				}

				endIndex = index + 1;
				index--;

				while (index >= 0 && sm.substring(index, index + 1).matches("^[a-zA-Z]*")) {
					index--;
				}
				index++;
				pName = sm.substring(index, endIndex);
			}
		}
		return pName;
	}

	public static String getVariable(String sm) {
		String variable = new String(sm);
		if (sm.indexOf("=") > 0)
			variable = sm.substring(0, sm.indexOf("="));

		return variable;
	}

	public static int ifIncludeAttributeList(ArrayList<String> list, String sm) {
		int tag = -1;
		int index = 0;
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				index = sm.indexOf(list.get(i));
				if (index >= 0
						&& (index + 1 + list.get(i).length() == sm.length()
								|| !sm.substring(index + list.get(i).length(), index + list.get(i).length() + 1)
										.matches("^[a-zA-Z]*")
										&& !sm.substring(index + list.get(i).length(), index + list.get(i).length() + 1)
												.equals("\""))
						&& (index == 0 || !sm.substring(index - 1, index).matches("^[a-zA-Z]*")
								|| sm.substring(index - 1, index).equals("\""))
						&& sm.indexOf("System.out.") < 0) {
					tag = i;
				}
			}
		}

		return tag;
	}

	public static void main(String[] args) {
		ArrayList<String> a = new ArrayList();
		a.add("a");
		// .a.add("ad");
		a.add("obj");
		// System.out.println(CodeMatcher.ifWMsequencewords("atts.addAttribute(a.getNamespaceURI(),a.getName(),a.getQualifiedName(),getAttributeTypeName(a.getAttributeType()),a.getValue());
		// ", ".", "addAttribute"));
		// System.out.println("---"+CodeMatcher.ifWMsequencewords("rg.w3c.dom.CDATASection
		// domCdata=domDoc.createCDATASection(cdata.getText());",
		// ".","getText("));
		// System.out.println("---"+CodeMatcher.useofMethod("Attribute
		// attribute=(Attribute)attributes.get(test.getname()); ", "get",1));
		// System.out.println("---"+CodeMatcher.useofMethod("Attribute
		// attribute=(Attribute)()aaattributes.get(a,sh.get() ,a); ", "get",3));

		// String a = "test";
		// System.out.println(CodeMatcher.ifnewPara2("ccc ssa new BC()",
		// "ccc"));
		// System.out.println(a);
		// System.out.println(CodeMatcher.getVariable("f=new format"));
		// System.out.println(CodeMatcher.ifWMinstanceof("ffff instanceof AC)",
		// "AC"));
	}
}
