package com.geofeedia.rtree.client;

import com.geofeedia.sdquerry.datatypes.Datum;

public interface TextDatumLoader {
	Datum loadDatum(String text);
}
