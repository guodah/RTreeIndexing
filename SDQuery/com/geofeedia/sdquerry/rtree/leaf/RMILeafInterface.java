package com.geofeedia.sdquerry.rtree.leaf;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.geofeedia.sdquerry.datatypes.Datum;

public interface RMILeafInterface extends Remote{

	void insert(Collection<Datum> data) throws RemoteException;

	List<Datum> loadAll() throws RemoteException;

	Map<Class, Object> split(Datum datum, RMILeafStub rmiLeafStub, RMILeafStub newLeaf) throws RemoteException;

	void insert(Datum datum) throws RemoteException;
	
	Collection<Datum> search(GeoMBB mbb) throws RemoteException;
	
}
