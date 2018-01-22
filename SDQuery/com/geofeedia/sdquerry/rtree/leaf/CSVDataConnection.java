package com.geofeedia.sdquerry.rtree.leaf;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import com.geofeedia.rtree.util.IlegalArgumentsException;
import com.geofeedia.rtree.util.LocalFileSystemUtil;
import com.geofeedia.sdquerry.datatypes.BasicDatumBuilder;
import com.geofeedia.sdquerry.datatypes.Datum;

public class CSVDataConnection implements DataConnection{

	private File file;
	private FileOutputStream out;
	public CSVDataConnection(String path) 
			throws IlegalArgumentsException, IOException{
		if(!LocalFileSystemUtil.exist(path)){
			throw new IllegalStateException(path+
					" does not exist for an CSVDataConnection.");
		}
		
		file = new File(path+"\\data.txt");
		file.deleteOnExit();
		file.createNewFile();
		
		out = new FileOutputStream(file);
	}
	

	@Override
	public void save(Collection<Datum> data) {
		if(data==null){
			throw new IllegalStateException("Null saved to ASCIIDataConnection");
		}
		
		for(Datum datum:data){
			save(datum);
		}
	}

	@Override
	public void save(Datum datum) {
		if(datum==null){
			throw new IllegalStateException("Null saved to ASCIIDataConnection");
		}
		Formatter format = new Formatter(out);
		format.format("%d,", datum.getId());
		for(int i=0;i<datum.getDimensions();i++){
			format.format("%d"+(i==datum.getDimensions()-1?"\n":","), datum.getCoordinates()[i]);
		}
		format.flush();
	}

	@Override
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			throw new IllegalStateException("Unable to close ASCIIDataConnection.");
		}
	}

	@Override
	public List<Datum> loadAll() {
				
		try {
			FileInputStream in = new FileInputStream(file);
			Scanner scan = new Scanner(in);			
			List<Datum> data = new ArrayList<Datum>();
			while(scan.hasNext()){
				String line = scan.nextLine();
				String [] fields = line.split(",");
				BasicDatumBuilder builder = new BasicDatumBuilder();
				builder.createNewDatum();
				builder.setId(Long.parseLong(fields[0]));
				builder.setTime(Long.parseLong(fields[1]));
				builder.setLongitude(Long.parseLong(fields[2]));
				builder.setLatitude(Long.parseLong(fields[3]));
				data.add(builder.build());
			}
			scan.close();
			return data;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void clear() {
		try {
			out.close();
			file.deleteOnExit();
			file.createNewFile();
			out = new FileOutputStream(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		

	}



	@Override
	public Collection<Datum> load(GeoMBB range) {
		Collection<Datum> data = new ArrayList<Datum>();
		try {
			Scanner scanner = new Scanner(new FileInputStream(file));
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				String [] fields = line.split(",");
				BasicDatumBuilder builder = new BasicDatumBuilder();
				builder.createNewDatum();
				builder.setId(Long.parseLong(fields[0]));
				builder.setTime(Long.parseLong(fields[1]));
				builder.setLongitude(Long.parseLong(fields[2]));
				builder.setLatitude(Long.parseLong(fields[3]));
				Datum datum = builder.build();
				if(range.include(datum)){
					data.add(datum);
				}
			}
			return data;
		} catch (FileNotFoundException e) {
			return data;
		}
	}

}
