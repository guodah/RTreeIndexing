package com.geofeedia.sdquerry.rtree.leaf;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.sdquerry.datatypes.Datum;

public class RMILeaf extends UnicastRemoteObject implements RMILeafInterface{
	private LocalLeaf leaf;
	
	public RMILeaf(String path, Splitter splitter) 
			throws IlegalArgumentsException, RemoteException{
		leaf = new LocalLeaf(path, splitter);
	}
	
	@Override
	public void insert(Collection<Datum> data) {
		leaf.insert(data);
	}

	@Override
	public List<Datum> loadAll() {
		return leaf.loadAll();
	}

	@Override
	public Map<Class, Object> split(Datum datum, RMILeafStub rmiLeafStub, RMILeafStub newLeaf) {
		leaf.split(datum, rmiLeafStub, newLeaf);
//		System.out.println("In leaf: size:"+ rmiLeafStub.getSize());
//		System.out.println("In Leaf: size:"+ newLeaf.getSize());
//		System.out.println(rmiLeafStub.getMbb());
		Map<Class, Object> map = new HashMap<Class, Object>();
		map.put(MBB.class, rmiLeafStub.getMbb());
		map.put(RMILeafStub.class, newLeaf);
		return map;
	}

	@Override
	public void insert(Datum datum) {
		leaf.insert(datum);
	}

	@Override
	public Collection<Datum> search(GeoMBB mbb) throws RemoteException {
		return leaf.search(mbb);
	}

	

}
