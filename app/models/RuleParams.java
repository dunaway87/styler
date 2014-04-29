package models;

public class RuleParams {

	
	public static String getRuleIntro(String property){
		return  "<Rule>\n<Name>"+property+"</Name>\n<Title>"+property+"</Title>\n<Abstract>"+property+"</Abstract>\n";
	}
	
	public String equalToFilter(String propertyName, String value){
		return "<ogc:Filter>\n<ogc:PropertyIsEqualTo>\n<ogc:PropertyName>"+propertyName+"</ogc:PropertyName>\n<ogc:Literal>"+value+"</ogc:Literal>\n</ogc:PropertyIsEqualTo>\n</ogc:Filter>\n";
		
	}
	
	public String betweenFilter(String propertyName, double minValue, double maxValue){
		return "<ogc:Filter>\n<ogc:PropertyIsBetween>\n<ogc:PropertyName>"+propertyName+"</ogc:PropertyName>\n<LowerBoundary>\n<ogc:Literal>"+minValue+"</ogc:Literal>\n</LowerBoundary>\n" +
				"<UpperBoundary>\n<ogc:Literal>"+maxValue+"</ogc:Literal>\n</UpperBoundary>\n</ogc:PropertyIsBetween>\n</ogc:Filter>\n";
		}


	public String wkn(String wkn){
		return "<WellKnownName>"+wkn+"</WellKnownName>\n";
	}

	
	
	public String fillColor(String color){
		return "<CssParameter name=\"fill\">#"+color+"</CssParameter>\n";
	}

	

	public String fillOpacity(Double fillOpacity){
		return "<CssParameter name=\"fill-opacity\">"+fillOpacity+"</CssParameter>\n";
	}


	public String strokeColor(String color){
		return "<CssParameter name=\"stroke\">#"+color+"</CssParameter>\n";
	}

	public String strokeOpacity(double opacity){
		return "<CssParameter name=\"stroke-opacity\">"+opacity+"</CssParameter>\n";

	}
	
	
	
	public String strokeWidth(int width){
		return "<CssParameter name=\"stroke-width\">"+width+"</CssParameter>\n";
	}


	public String size(int size){
		return "<Size>"+size+"</Size>\n";
	}
	

	
	public String rotation(int rotation){
		return "<Rotation>"+rotation+"</Rotation>\n";
	}
	

	
	public String strokeLineCap(String strokeLineCap){
		return "<CssParameter name=\"stroke-linecap\">"+strokeLineCap+"</CssParameter>\n";
	}

	public String sizeScale(double minProp, double maxProp, int minSize, int maxSize, String scaleProp){
		String size = "<Size>\n <ogc:Function name=\"Interpolate\"><ogc:PropertyName>"+scaleProp+"</ogc:PropertyName>\n<ogc:Literal>"+minProp+"</ogc:Literal>\n<ogc:Literal>"+minSize+"</ogc:Literal>\n\n<ogc:Literal>"+maxProp+"</ogc:Literal>\n<ogc:Literal>"+maxSize+"</ogc:Literal>\n</ogc:Function>\n</Size>\n";
		
		return size;
	}
	public String colorScale(double minProp, double maxProp, String minColor, String maxColor, String property){
		String color = "<Fill>\n<CssParameter name=\"fill\">\n<ogc:Function name=\"Interpolate\"><ogc:PropertyName>"+property+"</ogc:PropertyName>\n<ogc:Literal>"+minProp+"</ogc:Literal>\n<ogc:Literal>"+minColor+"</ogc:Literal>\n\n<ogc:Literal>"+maxProp+"</ogc:Literal>\n<ogc:Literal>"+maxColor+"</ogc:Literal>\n<ogc:Literal>color</ogc:Literal>\n</ogc:Function>\n </CssParameter>\n</Fill>\n";
		return color;
	}
		
	public String colorScalePoly(double minProp, double maxProp, String minColor, String maxColor, String property){
		String color = "<Fill>\n<CssParameter name=\"fill\">\n<ogc:Function name=\"Interpolate\"><ogc:PropertyName>"+property+"</ogc:PropertyName>\n<ogc:Literal>"+minProp+"</ogc:Literal>\n<ogc:Literal>"+minColor+"</ogc:Literal>\n\n<ogc:Literal>"+maxProp+"</ogc:Literal>\n<ogc:Literal>"+maxColor+"</ogc:Literal>\n<ogc:Literal>color</ogc:Literal>\n</ogc:Function>\n </CssParameter>\n";
		return color;
	}
	
	public String colorScaleLine(double min, double max, String minColor, String maxColor, String scaleColorProperty) {
		String color = "<CssParameter name=\"stroke\">\n<ogc:Function name=\"Interpolate\"><ogc:PropertyName>"+scaleColorProperty+"</ogc:PropertyName>\n<ogc:Literal>"+min+"</ogc:Literal>\n<ogc:Literal>"+minColor+"</ogc:Literal>\n\n<ogc:Literal>"+max+"</ogc:Literal>\n<ogc:Literal>"+maxColor+"</ogc:Literal>\n<ogc:Literal>color</ogc:Literal>\n</ogc:Function>\n </CssParameter>\n";
		return color;
	}

}
