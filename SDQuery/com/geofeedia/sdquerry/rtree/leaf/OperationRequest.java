package com.geofeedia.sdquerry.rtree.leaf;

import java.util.Map;


public class OperationRequest {
	private OperationRequestType type;
	private Map<String, Object> arguments; 
	
	protected OperationRequest(OperationRequestType type, Map<String, Object> arguments){
		this.type = type;
		this.arguments = arguments;
	}
}
