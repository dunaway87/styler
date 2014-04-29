package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import models.SLD;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.Logger;
import play.db.jpa.JPA;

public class ColumnNames {



	public static JsonArray get(String tableName){
		ArrayList<Object[]> arrayObj = new ArrayList<Object[]>();
		Query q = JPA.em().createNativeQuery("SELECT column_name, udt_name FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?");
		q.setParameter(1, tableName);
		arrayObj = (ArrayList<Object[]>) q.getResultList();
		JsonArray array = new JsonArray();
		JsonObject column = new JsonObject();

		for(int i = 0;  i < arrayObj.size(); i++){
			column = new JsonObject();
			String columnName = (String) arrayObj.get(i)[0];
			column.addProperty("styleName", columnName);
			Logger.info("columnName %s", columnName);
			String dataType = (String) arrayObj.get(i)[1];
			column.addProperty("dataType", dataType);
			String querySTR = "Select count(distinct(\""+arrayObj.get(i)[0]+"\")) from \""+tableName+"\"";
			Logger.info("%s", arrayObj.get(i)[1]);
			Logger.info("%s",querySTR);
			Query query =JPA.em().createNativeQuery(querySTR);
			String distinctValues = query.getSingleResult().toString();
			column.addProperty("distinctValues", distinctValues);
			
			Query uuidQuery = JPA.em().createNativeQuery("Select uuid from sld where tablename = ? and columnname = ?");
			uuidQuery.setParameter(1, tableName);
			uuidQuery.setParameter(2, columnName);
			List<String> list = uuidQuery.getResultList();
			if(list.size() ==0){
				column.addProperty("styleUuid", "null");
			} else {
				column.addProperty("styleUuid", list.get(0));
			}
	
			
			double disValDoub = Double.parseDouble(distinctValues);
			if(dataType.equals("geometry") || columnName.equals("fid") || disValDoub > 40) {

			} else {
				array.add(column);
			}
		}
		
		JsonObject defaultStyle = new JsonObject();
		Query defaultQuery = JPA.em().createNativeQuery("Select uuid from sld where columnName = 'default' and tablename = '"+tableName+"'");
		Logger.info("Select uuid from sld where columnName = 'default' and tablename = "+tableName);
		Object str = "hehe";//defaultQuery.getSingleResult();
		defaultStyle.addProperty("styleUuid", str.toString());
		defaultStyle.addProperty("styleName", "default");
		array.add(defaultStyle);
		
		return array;

	}




}
