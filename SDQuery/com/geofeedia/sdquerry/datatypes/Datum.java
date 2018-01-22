package com.geofeedia.sdquerry.datatypes;

public abstract class Datum {
	abstract public long[]  getCoordinates();
	abstract public int getDimensions(); 
	abstract public long getId();
	
	@Override
	public boolean equals(Object _datum){
		System.out.println("In Datum.equals()...");
		if(_datum instanceof Datum){
			return ((Datum)_datum).getId()==getId();
		}
		return false;
	}
}
