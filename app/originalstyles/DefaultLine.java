package originalstyles;

import java.util.UUID;

import models.Rule;
import models.SLD;

public class DefaultLine {

	public static String makeDefaultLine(String layer, String columnName, String uuid){
		SLD sld = new SLD();
		sld.columnName = columnName;
		sld.tablename = layer;
		sld.uuid = uuid;
		sld.save();
		Rule rule = new Rule();
		rule.shapetype = "line";
		rule.uuid = uuid;
		rule.columnname = "noColumn";
		rule.strokecolor = "cccccc";
		rule.strokewidth = 4;
		rule.strokeopacity = 1;
		rule.strokelinecap = "butt";
		rule.tablename = layer;

		rule.save();
		return uuid;

	}

}
