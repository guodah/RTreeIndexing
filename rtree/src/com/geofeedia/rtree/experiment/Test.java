package com.geofeedia.rtree.experiment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public static void main(String args[]) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS X");
		
		
		String time = "2000-01-01 00:00:00.0000000 +00:00";
		System.out.println(format.parse(time).getTime());
		
		
		
	}
}
