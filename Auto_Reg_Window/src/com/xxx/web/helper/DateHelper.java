package com.xxx.web.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 日期处理帮助类
 * @author 门士松  20121027
 * @version 1.0
 * @since
 */
public class DateHelper{
	//一天的毫秒数
	private final static int MSEL_OF_DAY = 60 * 1000 * 60 * 24;
	
	/**
	 * 描述：把Date转换为缺省的日期格式字串，缺省的字转换格式为yyyy-MM-dd HH:mm:ss
	 * 如：2004-10-10 20:12:10
	 */
	public static String formatDate(Date date){
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}
    /**
     * 把Date转换为字符，转换格式为yyyy-MM-dd
     */
    public static String formatDateNoTime(Date date){
        return formatDate(date, "yyyy-MM-dd");
    }  	
    
	/**
	 * 将日期中的“-”去掉，并补0 ， 如 “2012-9-1” 转换为“20120901”
	 */
	public static String removeLine(String date){
		if(date == null || date.equals("")){
			return date;
		}
		StringBuffer buff = new StringBuffer();
		String[] arrDate = date.split("-");
		for (int i = 0; i < arrDate.length; i++){
			if(arrDate[i].length() < 2){
				buff.append("0" + arrDate[i]);
			}else{
				buff.append(arrDate[i]);
			}
		}
		return buff.toString();
	}  
	/**
	 * 添加“-”如 “20120101” 转换为“2012-10-10”
	 */
	public static  String addLine(String date){
		if(date == null || date.equals("")){
			return date;
		}
		StringBuffer buff = new StringBuffer();
		buff.append(date.subSequence(0,4));
		buff.append("-");
		buff.append(date.subSequence(4,6));
		buff.append("-");
		buff.append(date.subSequence(6,8));
		return buff.toString();
	}
	
	/**
	 * 描述：根据特定的Pattern,把Date转换为相应的日期格式字符串
	 */
	public static String formatDate(Date date, String pattern){
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}
	/**
	 * 以缺省的yyyy-MM-dd HH:mm:ss格式转换字符串为Date 
	 */
	public static Date parseString(String dateStr)throws ParseException{
		return parseString(dateStr, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 以特定的格式转换字符串为Date
	 * @param dateStr 日期字符
	 * @param pattern 日期格式
	 */
	public static Date parseString(String dateStr, String pattern) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.parse(dateStr);
	}
	/**
	 * 获得特定日期的星期字串如 '星期日'
	 * @param date 日期
	 * @return 中文星期几
	 */
	public static String getWeekStr(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		week = week - 1;
		String weekStr = "";
		switch (week){
			case 0:
				weekStr = "星期日";
				break;
			case 1:
				weekStr = "星期一";
				break;
			case 2:
				weekStr = "星期二";
				break;
			case 3:
				weekStr = "星期三";
				break;
			case 4:
				weekStr = "星期四";
				break;
			case 5:
				weekStr = "星期五";
				break;
			case 6:
				weekStr = "星期六";
		}
		return weekStr;
	}
	/**
	 * 比较两个时间的差，返回天数，用时间1-时间2
	 * @param date1 时间1
	 * @param date2 时间2
	 * @return 两个时间相差的天数
	 */
	public static int getDateDiff(Date date1, Date date2){
		long time1 = date1.getTime();
		long time2 = date2.getTime();		
		long diff = time1 - time2;		
		Long longValue = new Long(diff / MSEL_OF_DAY);
		return longValue.intValue();
	}
}
