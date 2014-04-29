package styleinfo;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.MinMax;
import models.Rule;

import play.Logger;
import play.db.jpa.JPA;

public class StyleToDatabase {
	public static int TOTAL_COLORS= 1279;

	public static void makePointRules(String tableName, String columnName, String sldUuid, int steps){
		JsonArray properties = new JsonArray();
		List list;
		int rulesCount = 0;
		if(steps == 0){
			list = getUniqueValues(tableName, columnName);
			rulesCount = list.size();
		} else {
			list = getNumbericalValues(tableName, columnName, steps);
			rulesCount = list.size()-1;
		}		
		List<String> colorsList = new LinkedList<String>();
		colorsList = getColorsList(rulesCount);
		
		for(int i = 0; i < rulesCount; i++){
			JsonObject property = new JsonObject();
			property.addProperty("property", list.get(i).toString());
			property.addProperty("id", i);
			JsonArray rules = new JsonArray();

			Rule rule = new Rule();
			rule.localId = i;
			Logger.info("id %s",rule.id);
			rule.uuid = sldUuid;
			rule.columnname = columnName;
			rule.tablename = tableName;

			
			rule.fillcolor = colorsList.get(i);

			rule.wkn = "square";
			rule.fillopacity = 1;

			if(steps != 0){
				rule.minvalue = (double) list.get(i);
				rule.maxvalue = (double) list.get(i+1);
			} else {
				rule.property = list.get(i).toString();
			}			
			rule.shapetype = "point";

			rule.size = 10;
			
			rule.rotation = 0;
			rule.save();
			property.add("properties", rules);
			properties.add(property);
		}
	}

	public static void makeLineRules(String tableName, String columnName, String sldUuid, int steps){
		JsonArray properties = new JsonArray();
		List list;
		int rulesCount = 0;
		if(steps == 0){
			list = getUniqueValues(tableName, columnName);
			rulesCount = list.size();
		} else {
			list = getNumbericalValues(tableName, columnName, steps);
			rulesCount = list.size()-1;
		}		
		Logger.info("unique values  %s", list.size());


		List<String> colorsList = new LinkedList<String>();
		
			colorsList = getColorsList(rulesCount);
		

		for(int i = 0; i < rulesCount; i++){
			JsonObject property = new JsonObject();
			property.addProperty("property", list.get(i).toString());
			property.addProperty("id", i);
			JsonArray rules = new JsonArray();
			Rule rule = new Rule();
			Logger.info("id %s",rule.getId());
			rule.localId = i;
			rule.uuid = sldUuid;
			rule.columnname = columnName;
			rule.tablename = tableName;
				String color = colorsList.get(i);
				rule.strokecolor = color;
			
			rule.strokeopacity = 1;
			if(steps != 0){
				rule.minvalue = (double) list.get(i);
				rule.maxvalue = (double) list.get(i+1);
			} else {
				rule.property = list.get(i).toString();
			}			rule.shapetype = "line";
			rule.strokewidth = 4;
			rule.strokelinecap = "butt";

			rule.save();
			property.add("properties", rules);
			properties.add(property);
		}
	}


	public static void makePolygonRules(String tableName, String columnName, String sldUuid, int steps){
		JsonArray properties = new JsonArray();
		List list;
		int rulesCount =0;

		if(steps == 0){
			list = getUniqueValues(tableName, columnName);
			rulesCount = list.size();
		} else {
			list = getNumbericalValues(tableName, columnName, steps);
			rulesCount = list.size();
		}
		List<String> colorsList = new LinkedList<String>();
		
			colorsList = getColorsList(rulesCount);
		


		for(int i = 0; i < list.size()-1; i++){

			JsonObject property = new JsonObject();
			property.addProperty("property", list.get(i).toString());
			property.addProperty("id", i);
			JsonArray rules = new JsonArray();


			Rule rule = new Rule();
			if(steps != 0){
				rule.minvalue = (double) list.get(i);
				rule.maxvalue = (double) list.get(i+1);
			} else {
				rule.property = list.get(i).toString();
			}
			rule.localId = i;
			rule.uuid = sldUuid;
			rule.columnname = columnName;
			rule.tablename = tableName;
				rule.fillcolor = colorsList.get(i);
				rule.strokecolor = colorsList.get(i);
			
			rule.fillopacity = .75;
			rule.shapetype = "polygon";
			rule.strokeopacity = 1;
			rule.strokewidth = 2;
			rule.save();
			JPA.em().flush();
			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();
			property.add("properties", rules);
			properties.add(property);
		}
	}





	private static List<Double> getNumbericalValues(String tableName, String columnName, int steps){
		List<Object[]> list =  JPA.em().createNativeQuery("Select stddev(\""+columnName+"\"), avg(\""+columnName+"\"), min(\""+columnName+"\"), max(\""+columnName+"\") from \""+tableName+"\"").getResultList();
		double stdDev;
		double average;
		double min;
		double max;
		if(list.size() >0){

			stdDev = ((Number) list.get(0)[0]).doubleValue();
			average = ((Number) list.get(0)[1]).doubleValue();
			min = ((Number) list.get(0)[2]).doubleValue();
			max = ((Number) list.get(0)[3]).doubleValue();

		} else {
			return null;
		}
		double aveLessStdDev = average-(2*stdDev);
		double avePlusStdDev = average+(2*stdDev);
		if (aveLessStdDev < min){
			aveLessStdDev = min;
		}
		if (avePlusStdDev > max){
			avePlusStdDev = max;
		}

		double stepSize = Math.abs(aveLessStdDev-avePlusStdDev)/steps;

		List<Double> values = new LinkedList<Double>();
		values.add(min);
		double currentValue = aveLessStdDev;
		for(int i = 0; i < steps-1; i ++){
			values.add(currentValue+stepSize);
			currentValue = currentValue+stepSize;
		}
		values.add(max);

		return values;
	}





	private static List<Object> getUniqueValues(String tableName, String columnName){
		Logger.info("Select distinct \""+columnName+"\" from \""+tableName+"\"");
		Query q = JPA.em().createNativeQuery("Select distinct \""+columnName+"\" from \""+tableName+"\"");
		List<Object> list = q.getResultList();
		return list;
	}

	private static List<String> getColorsList (int colors) {
		int stepSize = TOTAL_COLORS/colors;
		Query q = JPA.em().createNativeQuery("Select \"value\" from \"color\" where MOD(\"color_id\",?) = 1");
		q.setParameter(1, stepSize+1);
		List colorsList = q.getResultList();
		List inverseList = new LinkedList<String>(); 
		for (int i = 0; i < colorsList.size(); i++) {
			inverseList.add(colorsList.get(colorsList.size()-1-i));
			Logger.info("%s", inverseList);
		}

		return inverseList;

	}
}
