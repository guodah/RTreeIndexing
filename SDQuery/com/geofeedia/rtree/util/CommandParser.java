package com.geofeedia.rtree.util;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
	public static Map<String, String> parse(String args[]){
		if(args.length==0){
			return null;
		}
		
		Map<String, String> result = new HashMap<String, String>();
		int count=0;
		while(exists(args, count)){
			if(args[count].equals("-m")){
				if(exists(args, count+1) && checkMode(args[count+1])){
					result.put("mode", args[count+1]);
					count += 2;
				}else{
					return null;
				}
			}else if(args[count].equals("-f")){
				if(exists(args, count+1)){
					result.put("data", args[count+1]);
					count+=2;
				}else{
					return null;
				}
				if(exists(args, count) && !args[count].startsWith("-")){
					result.put("data_encode", args[count]);
					count++;
				}
			}else if(args[count].equals("-i")){
				if(exists(args, count+1)){
					result.put("ips_file", args[count+1]);
					count+=2;
				}
			}
		}
		
		if(!result.containsKey("data")){
			return null;
		}else if(result.get("mode").equals("jvm") && !result.containsKey("ips_file")){
			return null;
		}
		return result;
	}

	public static void showUsage(){
		System.out.println("java -cp rtree[-x.y.z].jar YourClientProgram\\ \n"+
				"\t -m [local|jvm] -f data_file_path [encode] -i file_w_ip_addrs");
	}
	
	private static boolean checkMode(String string) {
		return string.equals("local") || string.equals("jvm");
	}

	private static boolean exists(String[] args, int i) {
		return i<args.length;
	}
}
