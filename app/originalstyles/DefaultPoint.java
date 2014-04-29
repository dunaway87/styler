package originalstyles;

import java.util.UUID;

import play.vfs.VirtualFile;

import models.Rule;
import models.SLD;

public class DefaultPoint {


	public static String makeDefaultPoint(String layer, String columnName, String uuid){
		SLD sld = new SLD();
		sld.columnName = columnName;
		sld.tablename = layer;
		sld.uuid = uuid;
		sld.save();
		Rule rule = new Rule();
		rule.tablename = layer;
		rule.shapetype = "point";
		rule.uuid = uuid;
		rule.columnname = "noColumn";
		rule.wkn = "cirlce";
		rule.fillcolor = "cccccc";
		rule.fillopacity = 1;
		rule.size = 10;
		rule.rotation = 0;
		rule.save();
		return uuid;
		
	}
	
	
	
	
	
	
	
	
	
	
}
