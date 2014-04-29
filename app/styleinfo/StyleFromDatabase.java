package styleinfo;


import java.util.List;

import javax.persistence.Query;

import net.sf.oval.constraint.MinSize;

import org.apache.commons.configuration.PropertiesConfiguration.PropertiesReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.xml.internal.fastinfoset.sax.Properties;

import play.Logger;
import play.db.jpa.JPA;
import play.vfs.VirtualFile;

import models.MinMax;
import models.Rule;
import models.RuleParams;
import models.Style;

public class StyleFromDatabase {


	private static final String GEOSERVER_STORE = "Test";
	private static final int MIN_SIZE = 5;
	private static final int MAX_SIZE = 20;
	private static final String MIN_COLOR = "#00ff00";
	private static final String MAX_COLOR = "#ff0000";



	public static Style getStyle(String uuid, String tableName){
		List<Rule> rules = Rule.find("byUuid", uuid).fetch();
		Logger.info("number of rules %s", rules.size());
		String shapeType = rules.get(0).shapetype;
		Style style = new Style();
		if(shapeType.equals("point")){
			style = buildPointNoVar(rules);
		} else if (shapeType.equals("line")){
			style = buildLineNoVar(rules);
		} else{
			style = buildPolygonNoVar(rules);
		}
		String body = style.getSld();

		String intro = VirtualFile.fromRelativePath("app/templates/sldintro.xml").contentAsString().replace("ENTER_NAME_HERE", GEOSERVER_STORE+":"+tableName);
		String exit = VirtualFile.fromRelativePath("app/templates/sldexit.xml").contentAsString();
		String sldStr = intro+body+exit;

		style.setSld(sldStr);
		return style;
	}


	private static Style buildPolygonNoVar(List<Rule> rules){
		boolean scaleColor = false;
		String body = "";
		boolean noFilter = rules.get(0).columnname.equals("noColumn");
		Style style = new Style();
		style.properties = new JsonArray();
		RuleParams params = new RuleParams();
		for(int i = 0; i < rules.size(); i++){
			String ruleStr = "";
			Rule rule = rules.get(i);
			style.properties.add(addProperty(rule));
			ruleStr += params.getRuleIntro(rule.property);
			if (noFilter == false){
				if(rule.property != null){
					ruleStr += params.equalToFilter(rule.columnname, rule.property);
				} else {
					ruleStr += params.betweenFilter(rule.columnname, rule.minvalue, rule.maxvalue);
				} 
			}			
			ruleStr += "<PolygonSymbolizer>\n";

			if(rule.fillcolor != null && rule.fillcolor.equals("scale") == false){
				ruleStr+= "<Fill>\n"+params.fillColor(rule.fillcolor);
			} else if(rule.fillcolor.equals("scale") == true){
				ruleStr+= "%addColorHere%";
				scaleColor = true;
			}

			if( rule.fillopacity != 0){
				ruleStr+=params.fillOpacity(rule.fillopacity) + "</Fill>\n";
			}
			if(rule.strokecolor != null){
				ruleStr += "<Stroke>\n"+params.strokeColor(rule.strokecolor);
				ruleStr += params.strokeOpacity(rule.strokeopacity);
				ruleStr += params.strokeWidth(rule.strokewidth)+"</Stroke>\n";
			}

			ruleStr += "</PolygonSymbolizer>\n</Rule>\n";
			body+= ruleStr;
		}
		if( scaleColor ==true){
			MinMax minmax = getMinMax(rules.get(0).tablename, rules.get(0).scaleColorProperty);
			body = body.replace("%addColorHere%", params.colorScalePoly(minmax.getMin(), minmax.getMax(), MIN_COLOR, MAX_COLOR, rules.get(0).scaleColorProperty));
		}
		style.sld = body;
		return style;
	}


	private static Style buildLineNoVar(List<Rule> rules){

		boolean scaleColor = false;
		String body = "";
		Style style = new Style();
		style.properties = new JsonArray();
		RuleParams params = new RuleParams();
		boolean noFilter = rules.get(0).columnname.equals("noColumn");
		Logger.info("now number of rules %s", rules.size());
		for(int i = 0; i < rules.size(); i++){
			String ruleStr = "";
			Rule rule = rules.get(i);
			style.properties.add(addProperty(rule));
			ruleStr += params.getRuleIntro(rule.property);
			if (noFilter == false){ 
				if(rule.property != null){
					ruleStr += params.equalToFilter(rule.columnname, rule.property);
				} else {
					ruleStr += params.betweenFilter(rule.columnname, rule.minvalue, rule.maxvalue);
				}
			}			

			ruleStr += "<LineSymbolizer>\n<Stroke>\n";
			if(rule.strokecolor.equals("scale")){
				ruleStr += "%addStrokeColorHere%";
				scaleColor = true;
			} else {
				ruleStr += params.strokeColor(rule.strokecolor);
			}
			ruleStr += params.strokeWidth(rule.strokewidth);
			ruleStr += params.strokeOpacity(rule.strokeopacity);
			if (rule.strokelinecap != null){
				ruleStr += params.strokeLineCap(rule.strokelinecap);
			}
			ruleStr+= "</Stroke>\n</LineSymbolizer>\n</Rule>\n";
			body+= ruleStr;
		}
		if( scaleColor ==true){
			MinMax minmax = getMinMax(rules.get(0).tablename, rules.get(0).scaleColorProperty);
			body = body.replace("%addStrokeColorHere%", params.colorScaleLine(minmax.getMin(), minmax.getMax(), MIN_COLOR, MAX_COLOR, rules.get(0).scaleColorProperty));
		}
		style.sld = body;
		return style;
	}


	private static Style buildPointNoVar(List<Rule> rules){

		boolean scaleSize = false;
		boolean scaleColor = false;
		Style style = new Style();
		style.properties = new JsonArray();
		String body = "";
		boolean noFilter = rules.get(0).columnname.equals("noColumn");

		RuleParams params = new RuleParams();
		for(int i = 0; i < rules.size(); i++){
			String ruleStr = "";
			Rule rule = rules.get(i);
			style.properties.add(addProperty(rule));

			ruleStr += params.getRuleIntro(rule.property);
			if (noFilter == false){
				if(rule.property != null){
					ruleStr += params.equalToFilter(rule.columnname, rule.property);
				} else {
					ruleStr += params.betweenFilter(rule.columnname, rule.minvalue, rule.maxvalue);

				}
			}
			ruleStr += "<PointSymbolizer>\n<Graphic>\n<Mark>\n";
			if(rule.wkn != null){
				ruleStr += params.wkn(rule.wkn);
			}
			if(rule.fillcolor != null && rule.fillcolor.equals("scale")==false){
				ruleStr+= "<Fill>\n"+params.fillColor(rule.fillcolor);
				ruleStr+=params.fillOpacity(rule.fillopacity) + "</Fill>\n";
			} else if(rule.fillcolor.equals("scale")){
				ruleStr+= "%addColorHere%";
				scaleColor = true;
			}
			if(rule.strokecolor != null){
				ruleStr += "<Stroke>\n"+params.strokeColor(rule.strokecolor);
				ruleStr += params.strokeOpacity(rule.strokeopacity);
				ruleStr += params.strokeWidth(rule.strokewidth)+"</Stroke>\n";
			}
			ruleStr += "</Mark>";
			if(rule.size != -1){
				ruleStr += params.size(rule.size);
			} else {
				scaleSize = true;

				ruleStr += "%addSizePropHere%";

			}
			ruleStr += params.rotation(rule.rotation);
			ruleStr += "</Graphic>\n</PointSymbolizer>\n</Rule>\n";
			body+= ruleStr;
		}
		if((scaleSize == true)){
			MinMax minmax = getMinMax(rules.get(0).tablename, rules.get(0).scaleProperty);

			body = body.replace("%addSizePropHere%", params.sizeScale(minmax.getMin(), minmax.getMax(), MIN_SIZE,MAX_SIZE, rules.get(0).scaleProperty));
		}

		if(scaleColor == true){
			MinMax minmax = getMinMax(rules.get(0).tablename, rules.get(0).scaleColorProperty);

			body = body.replace("%addColorHere%", params.colorScale(minmax.getMin(), minmax.getMax(), MIN_COLOR, MAX_COLOR, rules.get(0).scaleColorProperty));
		}
		style.sld = body;
		return style;
	}

	public static JsonObject addProperty(Rule rule){ 
		JsonObject property = new JsonObject();
		property.addProperty("property", rule.property);
		property.addProperty("id", rule.getId());
		Logger.info("%s", rule.id);
		JsonArray rules = new JsonArray();
		if(rule.fillcolor != null){
			rules.add(makeProp("fill-color", "fill-color-"+rule.localId, rule.fillcolor));
			if(rule.fillopacity != 1){
				rules.add(makeProp("fill-opacity", "fill-opacity-"+rule.localId, rule.fillopacity));
			}
		}
		if(rule.rotation != 0){
			rules.add(makeProp("rotation", "rotation-"+rule.localId, rule.rotation));
		}
		if(rule.size != 0){
			rules.add(makeProp("size", "size-"+rule.localId, rule.size));
		}
		if(rule.strokecolor != null){
			rules.add(makeProp("stroke-color", "stroke-color-"+rule.localId, rule.strokecolor));
			if(rule.strokeopacity != 1){
				rules.add(makeProp("stroke-opacity", "stroke-opaicty-"+rule.localId, rule.strokeopacity));
			}
			if(rule.strokelinecap != null){
				rules.add(makeProp("stroke-linecap", "stroke-linecap-"+rule.localId, rule.strokelinecap));
			}
			if(rule.strokewidth != 0){
				rules.add(makeProp("stroke-width", "stroke-width-"+rule.localId, rule.strokewidth));
			}
		}
		if(rule.wkn != null){
			JsonObject wkn = makeProp("wkn", "wkn-"+rule.localId, rule.wkn);
			JsonArray options = new JsonArray();
			options.add(new JsonPrimitive("circle"));
			options.add(new JsonPrimitive("square"));
			options.add(new JsonPrimitive("triangle"));
			options.add(new JsonPrimitive("star"));
			options.add(new JsonPrimitive("cross"));
			options.add(new JsonPrimitive("x"));
			wkn.add("options", options);
			rules.add(wkn);
		}
		property.add("properties", rules);
		return property;
	}
	public static JsonObject makeProp(String type, String property, Object value){
		JsonObject obj = new JsonObject();
		obj.addProperty("type", type);
		obj.addProperty("property", property);
		obj.addProperty("value", value.toString());
		return obj;
	}

	private static MinMax getMinMax(String tableName, String columnName){
		MinMax minmax = new MinMax();
		Logger.info("Select MAX(\""+columnName+"\"), MIN(\""+columnName+"\") from \""+tableName+"\"");
		Query q = JPA.em().createNativeQuery("Select MAX(CAST(\""+columnName+"\" as double precision)), MIN(CAST(\""+columnName+"\" as double precision)) from \""+tableName+"\"");
		Object[] array = (Object[]) q.getSingleResult();
		Number max = (Number) array[0];
		Number min = (Number) array[1];

		minmax.setMin(min.doubleValue());
		minmax.setMax(max.doubleValue());
		return minmax;
	}

}

