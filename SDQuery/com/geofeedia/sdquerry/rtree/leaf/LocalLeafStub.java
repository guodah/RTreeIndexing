package com.geofeedia.sdquerry.rtree.leaf;

import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.Datum;
import com.geofeedia.sdquerry.rtree.LeafType;
import com.geofeedia.sdquerry.rtree.Platform;

public class LocalLeafStub extends LeafStub {
	private LocalLeaf leaf;
	public LocalLeafStub(int dimension, int capacity, float minUtil, LocalLeaf leaf) {
		super(dimension, capacity, minUtil);
		this.leaf = leaf;
	}
	
	
	@Override
	public void batchInsert(Collection<Datum> data) {
		if(data==null || data.size()==0){
			throw new IllegalStateException("data is either null or empty");
		}
		if(size+data.size()<=capacity){
			leaf.insert(data);
			size+=data.size();
		}else{
			throw new IllegalStateException("batch insert will overfill the leaf");
		}
	}

	@Override
	public LeafStub insert(Datum datum) {
		if(datum==null){
			throw new IllegalStateException("datum is null");
		}

		if(size==capacity){
			LeafStub newLeaf = Platform.checkOutLeafStub(LeafType.LOCAL, capacity);
			leaf.split(datum, this, newLeaf);
			size = (capacity+1-newLeaf.getSize());
			return newLeaf;
		}else{
			leaf.insert(datum);
			this.getMbb().grow(datum);
			size++;
			return null;
		}
	}

	@Override
	public Collection<Datum> search(MBB range) {
		return leaf.search(new GeoMBB(range));
	}


	@Override
	public List<Datum> loadAll() {
		return leaf.loadAll();
	}

}
