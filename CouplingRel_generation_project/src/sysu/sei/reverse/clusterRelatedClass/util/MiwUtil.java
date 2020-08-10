package sysu.sei.reverse.clusterRelatedClass.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MiwUtil {
	/**
	 * 根据MODEL类实例，生成xml文件。对应class的MODEL应该使用XSTREAM的注解方式进行定义，
	 * 
	 * @param object
	 *            用XSTREAM的注解方式进行定义的MODEL
	 * @return 对应的xml串
	 */
	public static String genXML(Object object) {
		String xml = null;
		try {
			XStream xs = new XStream();
			xs.processAnnotations(object.getClass());
			xml = xs.toXML(object);
		} catch (Exception e) {
			xml = null;
		}
		return xml;
	}

	/**
	 * 根据XML生成对象实例，对应class的MODEL应该使用XSTREAM的注解方式进行定义
	 * 
	 * @param response
	 *            得到的XML
	 * @param type
	 *            用XSTREAM的注解方式进行定义的MODEL
	 * @return 对应的实例
	 */
	public static Object parseXML(String response, Class<?> type) {
		Object object = null;
		if (response != "") {
			try {
				XStream xs = new XStream(new DomDriver());
				xs.processAnnotations(type);
				object = xs.fromXML(response);
			} catch (Exception e) {
				object = null;
			}
		}
		return object;
	}

	/**
	 * 获取当前时间并将其转换为"yyyyMMddHHmmssSSS"格式，生成时间戳
	 * 
	 * @return 时间戳
	 */
	public static long getTimeStamp() {
		long timestamp = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyyMMddHHmmssSSS");
			String time = formatter.format(new Date());
			timestamp = Long.parseLong(time);

		} catch (Exception e) {
		}
		return timestamp;
	}
	

	public static String NumToString(int num) {
		try {
			String s = Integer.toString(num);
			return s;
		} catch (Exception e) {
		}
		return null;
	}
}
