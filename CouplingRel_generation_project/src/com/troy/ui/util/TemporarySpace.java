package com.troy.ui.util;

import java.util.ArrayList;
/**
 * 临时空间,储存程序执行过程中产生的提示信息。
 * @author Troy
 *
 */
public class TemporarySpace {
	
	public static ArrayList<String> consoleString = new ArrayList<String>();//控制台显示字符
	public static ArrayList<String> designDecisionString = new ArrayList<String>();//右侧design decision区域显示字符
	public static ArrayList<String> LDASubjectString = new ArrayList<String>();//右侧LDA Subject区域显示字符
	
	
	public static ArrayList<String> getLDASubjectString() {
		return LDASubjectString;
	}

	public static void setLDASubjectString(String str) {
		LDASubjectString.add(str);
	}

	public static ArrayList<String> getDesignDecisionString() {
		return designDecisionString;
	}

	public static void setDesignDecisionString(String str) {
		TemporarySpace.designDecisionString.add(str);
	}

	public static ArrayList<String> getConsoleString() {
		return consoleString;
	}

	public static void setConsoleString(String str) {
		TemporarySpace.consoleString.add(str);
	}

	

}
