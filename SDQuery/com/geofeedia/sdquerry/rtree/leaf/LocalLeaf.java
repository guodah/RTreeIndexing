package com.geofeedia.sdquerry.rtree.leaf;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.rtree.util.LocalFileSystemUtil;
import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.BasicRTreeDirectory;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.Platform;
import com.geofeedia.sdquerry.rtree.SystemUtil;

public class LocalLeaf {
	private DataConnection dataConn;
	private Splitter splitter;
	private String path;
	
	public LocalLeaf(String path, Splitter splitter) throws IlegalArgumentsException{
		this.splitter = splitter; 
		this.path = path;
	}
			
	public DataConnection getDataConn() {
		LocalFileSystemUtil.createPath(path);
		dataConn = Platform.getDataConn(LeafType.LOCAL, path);
		return dataConn;
	}

	public void insert(Collection<Datum> data){
		if(dataConn==null){
			getDataConn();
		}
		dataConn.save(data);
	}

	public void insert(Datum datum){
		if(dataConn==null){
			getDataConn();
		}
		dataConn.save(datum);
	}
	
	public Collection<Datum> search(GeoMBB range){
		if(dataConn==null){
			getDataConn();
		}
		return dataConn.load(range);
	}

	public void split(Datum datum, LeafStub source, LeafStub newLeaf) {
		
		if(dataConn==null){
			getDataConn();
		}

		List<Datum> data = dataConn.loadAll();
		data.add(datum);
		List<List<Datum>> groups = splitter.split(data, Platform.getMinUtilization());
		dataConn.clear();
		dataConn.save(groups.get(0));
		source.setMBB(new MBB(groups.get(0)));
		newLeaf.batchInsert(groups.get(1));
		newLeaf.setMBB(new MBB(groups.get(1)));
	}

	public List<Datum> loadAll() {
		if(dataConn==null){
			getDataConn();
		}
		return dataConn.loadAll();
	}

}
