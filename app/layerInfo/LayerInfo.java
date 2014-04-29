package layerInfo;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTResource;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import models.ShapeFileInfo;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.vfny.geoserver.global.GeoserverDataDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.libs.WS;
import play.vfs.VirtualFile;
import utils.ColumnNames;

public class LayerInfo {
	public static final String RESTURL  =Play.configuration.getProperty("RESTURL");
	public static final String RESTUSER = Play.configuration.getProperty("RESTUSER");
	public static final String RESTPW   = Play.configuration.getProperty("RESTPW");





	public static JsonObject getInfo(String layerName) throws MalformedURLException, XPathExpressionException, NoSuchAuthorityCodeException, FactoryException{
		GeoServerRESTReader grr = new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
		Logger.info("getting info for layer %s", layerName);
		RESTLayer layer = grr.getLayer(layerName);		
		RESTResource resource = grr.getResource(layer);
		JsonObject obj = new JsonObject();
		String ogcName = resource.getName();
		List<Object[]> activeList = JPA.em().createNativeQuery("Select activecolumn, activestyle from shapefileinfo where uuid = '"+layerName+"'").getResultList();

		String activecolumm;
		String activestyle;
		if(activeList.size() == 0){
			activecolumm = "null";
			activestyle = "null";
		} else {

			activecolumm = (String)activeList.get(0)[0];
			activestyle = (String)activeList.get(0)[1];
		}
		obj.addProperty("ogcName", ogcName);
		obj.addProperty("minLon", resource.getMinX());
		obj.addProperty("minLat", resource.getMinY());
		obj.addProperty("maxLon", resource.getMaxX());
		obj.addProperty("maxLat", resource.getMaxY());
		Logger.info("and the uuid for the layer is %s", ogcName);
		ShapeFileInfo sfi = ShapeFileInfo.find("byUuid", ogcName).first();
		//	obj.addProperty("preferredEpsg", sfi.epsg);
		if(sfi != null){
			obj.addProperty("label", sfi.original_layer_name);
			obj.addProperty("activeColumn", activecolumm);
			obj.addProperty("activeStyle", activestyle);


			//obj.addProperty("preferredEpsg", crsCode);
			obj.addProperty("wmsUrl", "http://staging1.axiom:8080/geoserver/wms");
			obj.add("styles", ColumnNames.get(ogcName));
			return obj;
		} else {
			return null;
		}

	}


}
