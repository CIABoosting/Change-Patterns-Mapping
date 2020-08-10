package com.troy.ui.util;

import java.text.DecimalFormat;

public class NumberUtil {
	
	/**
	 * 保留小数点后2位
	 * @param d
	 * @return
	 */
	
	public static String Num4Double(double d){
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(d); 
	}

}
