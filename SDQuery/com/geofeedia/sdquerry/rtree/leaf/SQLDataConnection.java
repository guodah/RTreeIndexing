package com.geofeedia.sdquerry.rtree.leaf;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.geofeedia.sdquerry.datatypes.BasicDatum;
import com.geofeedia.sdquerry.datatypes.BasicDatumBuilder;
import com.geofeedia.sdquerry.datatypes.Datum;

public class SQLDataConnection implements DataConnection{
	private Connection conn;

	public SQLDataConnection(SQLDataConnectionType type, 
			String driver, String path, String name, boolean create) 
					throws ClassNotFoundException, SQLException{
		Class.forName(driver);
		conn = DriverManager.getConnection(
				String.format("%s:%s", type, path), "sa", "");
		if(create){
			Statement stmt = conn.createStatement();
			stmt.execute("create table geo_data (id bigint, time bigint, "
					+"longitude bigint, latitude bigint) "
					+"constraint pk_id (id)");
			stmt.execute("create index tripplet on geo_data(time, longitude, latitude)");
		}
	}

	@Override
	public Collection<Datum> load(GeoMBB range) {
		Collection<Datum> data = new ArrayList<Datum>();
		if(range==null){
			return data;
		}
		String sql = String.format(
				"select id, time, longitude, latitude from geo_data where time>%d and time<%d "+
				"longitude>%f and longitude<%f and latitude>%f and latitude<%f", 
				range.getTimeLow(),range.getTimeHigh(),
				range.getLongitudeLow(),range.getLongitudeHigh(),
				range.getLatitudeLow(),range.getLatitudeHigh());
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet set = stmt.executeQuery();
			
			while(set.next()){
				long time =	set.getLong("time");
				long id = set.getLong("id");
				long longitude = set.getLong("longitude");
				long latitude = set.getLong("latitude");
				BasicDatumBuilder builder = new BasicDatumBuilder();
				builder.setId(id);
				builder.setTime(time);
				builder.setLatitude(latitude);
				builder.setLongitude(longitude);
				data.add(builder.build());
			}
			
		} catch (SQLException e) {
			// log
		}
		
		return null;
	}

	@Override
	public void save(Collection<Datum> data) {
		if(data==null){
			return;
		}
		
		for(Datum datum:data){
			save(datum);
		}
	}

	@Override
	public void save(Datum datum) {
		BasicDatum bDatum = (BasicDatum)datum;
		String sql = String.format("insert into geo_data(id, time, longitude, latitude) "
				+"values(%d, %d, %f, %f)", datum.getId(), bDatum.getTime(),
				bDatum.getLongitude(), bDatum.getLatitude());
		try{
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		}catch(SQLException e){
			// log
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Datum> loadAll() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void clear() {
		throw new RuntimeException("unimplemented");		
	}
}
