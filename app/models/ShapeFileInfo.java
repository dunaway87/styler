package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

import com.google.gson.JsonArray;

import models.ShapeType;
@Entity
public class ShapeFileInfo extends Model{
	public String uuid;
	public String shapeType;
	public int epsg;
	@Column(columnDefinition = "TEXT")
	public String columnsArray;
	public String original_layer_name;
	public String activeColumn;
	public String activeStyle;
}
