package com.geofeedia.sdquerry.rtree.leaf;

import java.util.HashMap;
import java.util.Map;

public class OperationRequestBuilder {
	private OperationRequestType type;
	private Map<String, Object> arguments;
	
	
	public OperationRequestBuilder createOperationRequest(OperationRequestType type){
		arguments = new HashMap<String, Object>();
		this.type = type;
		return this;
	}
	
	public OperationRequestBuilder setRequestArgument(String name, Object value){
		arguments.put(name, value);
		return this;
	}
	
	public OperationRequest build(){
		return new OperationRequest(type, arguments);
	}
}
