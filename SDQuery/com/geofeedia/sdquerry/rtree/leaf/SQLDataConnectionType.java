package com.geofeedia.sdquerry.rtree.leaf;

public enum SQLDataConnectionType {
	H2("jdbc:h2");
	
	private String protocol;
	private SQLDataConnectionType(String protocol){
		this.protocol = protocol;
	}
	public String toString(){
		return protocol;
	}
}
