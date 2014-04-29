package layerInfo;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import java.net.MalformedURLException;

import javax.xml.xpath.XPathExpressionException;

import org.codehaus.jettison.json.JSONArray;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.google.gson.JsonArray;

import play.Logger;
import play.Play;

public class LayersInfo {

	public static final String RESTURL  =Play.configuration.getProperty("RESTURL");
	public static final String RESTUSER = Play.configuration.getProperty("RESTUSER");
	public static final String RESTPW   = Play.configuration.getProperty("RESTPW");
	public static JsonArray getInfo() {
		GeoServerRESTReader grr = null;
		try {
			grr = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		RESTLayerList layers = grr.getLayers();
		JsonArray array = new JsonArray();

		for(int i = 0; i < layers.size(); i++){
			try {
				NameLinkElem layer = layers.get(i);
				String name = layer.getName();
				Logger.info("aaaaa"+name);
				array.add(LayerInfo.getInfo(name));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAuthorityCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return array;
		
		
	}


}
