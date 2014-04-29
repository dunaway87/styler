package controllers;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import styleinfo.StyleFromDatabase;
import styleinfo.StyleToDatabase;
import utils.ColumnNames;
import utils.TableNames;

import java.net.MalformedURLException;
import java.util.*;

import javax.persistence.Query;
import javax.xml.xpath.XPathExpressionException;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import originalstyles.DefaultLine;
import originalstyles.DefaultPoint;
import originalstyles.DefaultPolygon;

import layerInfo.LayerInfo;
import layerInfo.LayersInfo;
import models.*;

public class Application extends Controller {

	public static void makeOriginalStyle(String layer, String callback){ //nothing change
		ShapeFileInfo sfi = ShapeFileInfo.find("byUuid", layer).first();
		String uuid = UUID.randomUUID().toString();

		if(sfi == null){
			renderText("layer not avalable");
		}
		String shapeType = sfi.shapeType;
		String sldUuid = "";
		if(shapeType.toLowerCase().equals("point")){
			sldUuid = DefaultPoint.makeDefaultPoint(layer, "default",uuid);
		} else if (shapeType.toLowerCase().equals("line")){
			sldUuid = DefaultLine.makeDefaultLine(layer, "default", uuid);
		} else{
			sldUuid = DefaultPolygon.makeDefaultPolygon(layer, "default", uuid);
		}
		
		sfi.activeColumn = null;
		sfi.activeStyle = sldUuid;
		sfi.save();
	}

	public static void createStyleInDatabase(String layer, String styleName, String callback, int steps){
		if(styleName == null){
			styleName = "noColumn";
		}
		Query deleteQuery =JPA.em().createNativeQuery("DELETE FROM sld WHERE tablename ='"+layer+"' AND columnname = '"+styleName+"'");
		Logger.info("DELETE FROM sld WHERE tablename ='"+layer+"' AND columnname = '"+styleName+"'");
		deleteQuery.executeUpdate();
		JsonObject obj = new JsonObject();
		obj.addProperty("layer", layer);
		obj.addProperty("styleName",styleName);
		String uuid = UUID.randomUUID().toString();
		
		obj.addProperty("uuid", uuid);

		ShapeFileInfo sfi = ShapeFileInfo.find("byUuid", layer).first();
		if(sfi == null){
			renderText("layer not avalable");
		}
		String shapeType = sfi.shapeType;
		obj.addProperty("shape-type", shapeType.toLowerCase());

		if(styleName.equals("noColumn")){
			String sldUuid = "";
			if(shapeType.toLowerCase().equals("point")){
				sldUuid = DefaultPoint.makeDefaultPoint(layer, styleName,uuid);
			} else if (shapeType.toLowerCase().equals("line")){
				sldUuid = DefaultLine.makeDefaultLine(layer, styleName, uuid);
			} else{
				sldUuid = DefaultPolygon.makeDefaultPolygon(layer, styleName, uuid);
			}
			
			sfi.activeColumn = null;
			sfi.activeStyle = sldUuid;
			sfi.save();
			if(callback != null){
				renderText(callback+"("+obj+")");
			} else {
				renderText(obj);
			}
		}
		SLD sld = new SLD();
		sld.columnName = styleName;
		sld.tablename = layer;
		sld.uuid = uuid.toString();
		sld.save();
		Logger.info("shapeType:  %s", shapeType);

		
		if(shapeType.toLowerCase().equals("point")){
			StyleToDatabase.makePointRules(layer, styleName, uuid.toString(), steps);
		} else if (shapeType.toLowerCase().equals("line")){
			StyleToDatabase.makeLineRules(layer, styleName, uuid.toString(), steps);
		} else if (shapeType.toLowerCase().equals("polygon")){
			StyleToDatabase.makePolygonRules(layer, styleName, uuid.toString(), steps);
		}

		Style style =  StyleFromDatabase.getStyle(uuid.toString(), layer);
		obj.add("properties", style.getProperties());
		//obj.addProperty("sld", style.getSld());
		sfi.activeStyle = uuid.toString();
		sfi.activeColumn=styleName;
		sfi.save();
		if(callback != null){
			renderText(callback+"("+obj+")");
		} else {
			renderText(obj);
		}
	}

	public static void returnSLD(String uuid, String format, String callback){
		if(format==null){
			format = "xml";
		}
		JsonObject obj = new JsonObject();
		SLD sld = SLD.find("byUuid", uuid).first();
		obj.addProperty("layer", sld.tablename);
		obj.addProperty("styleName", sld.columnName);
		obj.addProperty("uuid", uuid);
		obj.addProperty("shape-type", "do you need this?");
		Style style = StyleFromDatabase.getStyle(uuid, sld.tablename);
		Logger.info("%s", style.getProperties().size());
		obj.add("properties", style.getProperties());
		//obj.addProperty("sld", style.getSld());
		Query q = JPA.em().createNativeQuery("update shapefileinfo set activecolumn = ? where uuid = ?");
		q.setParameter(1, sld.columnName);
		q.setParameter(2, sld.tablename);
		q.executeUpdate();
		Query qTwo = JPA.em().createNativeQuery("update shapefileinfo set activestyle = ? where uuid = ?");
		qTwo.setParameter(1, sld.uuid);
		qTwo.setParameter(2, sld.tablename);
		qTwo.executeUpdate();
		if(format.equals("json")){
			if(callback != null){
				renderText(callback+"("+obj+")");
			} else {
				renderText(obj);
			}
		} else {
			renderText(style.getSld());
		}
	}



	public static void updateRule(long id, String type, String newValue, String callback){
		JsonObject obj = new JsonObject();
		try{
			Rule rule = Rule.findById(id);
			if(type.equals("fill-color")){
				rule.fillcolor = newValue;
			}
			if(type.equals("fill-opacity")){
				rule.fillopacity = Double.parseDouble(newValue);
			}
			if(type.equals("rotation")){
				rule.rotation = Integer.parseInt(newValue);
			}
			if(type.equals("size")){
				rule.size = Integer.parseInt(newValue);
			}
			if(type.equals("stroke-color")){
				rule.strokecolor = newValue;
			}
			if(type.equals("stroke-linejoin")){
				rule.strokelinecap = newValue;
			}
			if(type.equals("stroke-width")){
				rule.strokewidth = Integer.parseInt(newValue);
			}
			if(type.equals("stroke-opacity")){
				rule.strokeopacity  = Double.parseDouble(newValue);
			}
			if(type.equals("wkn")){
				rule.wkn = newValue;
			}
			rule.save();
			obj.addProperty("success", true);
		} catch(Exception e){
			obj.addProperty("success", false);
		}
		renderText(obj);
		/*JsonObject obj = new JsonObject();
		SLD sld = SLD.find("byUuid", rule.uuid).first();
		obj.addProperty("layer", sld.tablename);
		obj.addProperty("styleName", sld.columnName);
		obj.addProperty("uuid", rule.uuid);
		obj.addProperty("shape-type", "do you need this?");
		Style style = StyleFromDatabase.getStyle(rule.uuid);
		obj.add("properties", style.getProperties());
		obj.addProperty("sld", style.getSld());



		if(callback != null){
			renderText(callback+"("+obj+")");
		} else {
			renderText(obj);
		}
		 */
	}

	public static void updateSizeScale(String uuid, String columnName){
		List<Rule> list = Rule.find("byUuid", uuid).fetch();
		for(int i = 0; i < list.size(); i++){
			Rule rule = list.get(i);
			rule.size = -1;
			rule.scaleProperty = columnName;
			rule.save();
		}
	}
	
	public static void updateColorScale(String uuid, String columnName){
		List<Rule> list = Rule.find("byUuid", uuid).fetch();
		for(int i = 0; i < list.size(); i++){
			Rule rule = list.get(i);
			
			rule.scaleColorProperty = columnName;
			if(rule.shapetype.equals("line")){
				rule.strokecolor = "scale";
			} else {
				rule.strokecolor= "000000";
				rule.fillcolor = "scale";
			}
			rule.save();
		}
	}

	public static void getColumnNames(String layer, String callback){
		if(callback != null){
			renderText(callback+"("+ColumnNames.get(layer)+")");
		} else {
			renderText(ColumnNames.get(layer));
		}
	}


	public static void getLayer(String layerName, String callback) throws MalformedURLException, XPathExpressionException, NoSuchAuthorityCodeException, FactoryException{


		if(callback == null){
			renderText(LayerInfo.getInfo(layerName));
		} else{
			renderText(callback+"("+LayerInfo.getInfo(layerName)+")");

		}

	}


	public static void getLayers(String callback){

		if(callback ==null){
			renderText(LayersInfo.getInfo());
		} else{
			renderText(callback+"("+LayersInfo.getInfo()+")");
		}


	}

	public static void getSLDs(String layer, String callback){
		List<SLD> list = SLD.find("byTablename", layer).fetch();
		JsonArray array = new JsonArray();
		for(int i = 0; i <list.size(); i++){
			JsonObject obj = new JsonObject();
			obj.addProperty("styleName", list.get(i).columnName);
			obj.addProperty("uuid", list.get(i).uuid);
			array.add(obj);
		}
		if(callback != null){
			renderText(callback+"("+array+")");
		} else {
			renderText(array);
		}

		renderText(array);

	}




}