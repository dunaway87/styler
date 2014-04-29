package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Rule extends Model{

	
	public String uuid;
	public String shapetype;
	public String columnname;
	public String tablename;
	public String property;
	public double minvalue;
	public double maxvalue;
	public String wkn;
	public String fillcolor;
	public double fillopacity;
	public String strokecolor;
	public double strokeopacity;
	public int strokewidth;
	public int size;
	public int rotation;
	public String strokelinecap;
	public int localId;
	public String scaleProperty;
	public String scaleColorProperty;
}
