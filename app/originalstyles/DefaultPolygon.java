package originalstyles;

import java.util.UUID;

import models.Rule;
import models.SLD;

public class DefaultPolygon {

	public static String makeDefaultPolygon(String layer, String columnName, String uuid){

		SLD sld = new SLD();
		sld.columnName = columnName;
		sld.tablename = layer;
		sld.uuid = uuid;
		sld.save();
		Rule rule = new Rule();
		rule.uuid = uuid;
		rule.columnname = "noColumn";
		rule.fillcolor = "cccccc";
		rule.fillopacity = 1;
		rule.strokecolor = "000000";
		rule.strokeopacity = 1;
		rule.strokewidth = 1;
		rule.tablename = layer;
		rule.shapetype = "polygon";
		rule.save();
		return uuid;

	}

}
