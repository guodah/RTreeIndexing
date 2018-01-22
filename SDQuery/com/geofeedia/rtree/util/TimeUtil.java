package com.geofeedia.rtree.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.geofeedia.sdquerry.rtree.Platform;

public class TimeUtil {
	static public Date parse(String time, SimpleDateFormat format) {
		try{
			return format.parse(time);
		}catch(ParseException e){
			return null;
		}
	}
	
	static public Date parse(String time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS X");
		return parse(time, format);
	}
	
	public static String convert(long milliseconds){
		Date date = new Date(milliseconds+Platform.getBaseTime());
		return (new SimpleDateFormat()).format(date);
	}
	
	public static void main(String args[]){
		convert(-946684800000L);
	}
}
