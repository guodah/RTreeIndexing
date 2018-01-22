package com.geofeedia.rtree.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	static public Date parse(String time, SimpleDateFormat format) 
			throws ParseException{
		return format.parse(time);
	}
	
	static public Date parse(String time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return parse(time, format);
	}
}
