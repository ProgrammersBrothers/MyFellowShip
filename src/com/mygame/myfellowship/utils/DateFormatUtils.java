package com.mygame.myfellowship.utils;

import java.util.Date;

/**
 * 日期、时间工具类
 * @author lenovo
 *
 */
public class DateFormatUtils {
	
	/**
	 * 把当前时间(ms数)化为指定格式的字符串
	 * @param mills
	 * @return yyyy-MM-dd HH:mm:ss 格式的字符串
	 */
	public static String getDateFormat(long mills){
		java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format1.format(new Date(mills));
        return date;
	}
}
