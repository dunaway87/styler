package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import play.db.jpa.JPA;

public class TableNames {

	public static JsonArray	get(){
		Query q = JPA.em().createNativeQuery("SELECT table_name FROM INFORMATION_SCHEMA.TABLES where table_schema = 'public' and table_type != 'VIEW'");
		ArrayList<String> list = (ArrayList<String>) q.getResultList();
		JsonArray ja = new JsonArray();
		for (int i = 0; i < list.size(); i++){
			ja.add(new JsonPrimitive(list.get(i)));
		}
		
		return ja;
		
	}
	
	
	
	
	
	
}
