package models;

import com.google.gson.JsonArray;

public class Style {

	public String sld;
	
	public JsonArray properties;

	public String getSld() {
		return sld;
	}

	public JsonArray getProperties() {
		return properties;
	}

	public void setSld(String sld) {
		this.sld = sld;
	}

	public void setProperties(JsonArray properties) {
		this.properties = properties;
	}

}
