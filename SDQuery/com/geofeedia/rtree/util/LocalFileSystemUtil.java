package com.geofeedia.rtree.util;

import java.io.File;

public class LocalFileSystemUtil {
	
	public static boolean exist(String path){
		File file = new File(path);
		return file.exists();
	}
	
	public static void createPath(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
	}

	public static void clearDirectory(String path) {
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		
		if(file.isFile()){
			if(file.delete()){
				return;
			}else{
				throw new RuntimeException("Unable to delete "+path);
			}
		}
		
		File files [] = file.listFiles();
		for(File file1:files){
			clearDirectory(file1.getAbsolutePath());
		}
		if(file.delete()){
			return;
		}else{
			throw new RuntimeException("Unable to delete "+path);
		}
	}

	public static boolean find(String path, String name) {
		File file = new File(path);
		if(!file.exists() || file.isFile()){
			return false;
		}
		File files [] = file.listFiles();
		for(File file1:files){
			if(file1.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
}
