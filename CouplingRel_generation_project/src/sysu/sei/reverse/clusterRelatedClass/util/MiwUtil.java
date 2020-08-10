package sysu.sei.reverse.clusterRelatedClass.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MiwUtil {
	/**
	 * ����MODEL��ʵ��������xml�ļ�����Ӧclass��MODELӦ��ʹ��XSTREAM��ע�ⷽʽ���ж��壬
	 * 
	 * @param object
	 *            ��XSTREAM��ע�ⷽʽ���ж����MODEL
	 * @return ��Ӧ��xml��
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
	 * ����XML���ɶ���ʵ������Ӧclass��MODELӦ��ʹ��XSTREAM��ע�ⷽʽ���ж���
	 * 
	 * @param response
	 *            �õ���XML
	 * @param type
	 *            ��XSTREAM��ע�ⷽʽ���ж����MODEL
	 * @return ��Ӧ��ʵ��
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
	 * ��ȡ��ǰʱ�䲢����ת��Ϊ"yyyyMMddHHmmssSSS"��ʽ������ʱ���
	 * 
	 * @return ʱ���
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
