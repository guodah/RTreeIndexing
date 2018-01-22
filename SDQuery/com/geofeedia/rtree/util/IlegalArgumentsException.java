package com.geofeedia.rtree.util;

public class IlegalArgumentsException extends Exception{
	private String msg;
	public IlegalArgumentsException(String msg){
		this.msg = msg;
	}
	
	public String toString(){
		return msg;
	}
}
