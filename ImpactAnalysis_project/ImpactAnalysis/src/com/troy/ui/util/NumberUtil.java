package com.troy.ui.util;

import java.text.DecimalFormat;

public class NumberUtil {
	
	/**
	 * ����С�����2λ
	 * @param d
	 * @return
	 */
	
	public static String Num4Double(double d){
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(d); 
	}

}
