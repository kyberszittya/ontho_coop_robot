package hu.bme.mit.inf.testing.artifact.mircontext.generator;


import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MonitorConfiguration.Configuration;
import MonitorConfiguration.ObserverSetting;
import extendedmircontext.Mission;
import extendedmircontext.TestRoom;
import gazeboobject.BoundingPrimitive;
import gazeboobject.Cube;
import gazeboobject.Primitive;
import gazeboobject.Sphere;
import objectcatalogue.CatalogueItem;
import objectcatalogue.ObjectCatalogue;


public class DataCollectorConfiguration {
	private static DocumentBuilder doc_builder;
	
	
	public DataCollectorConfiguration(
			DocumentBuilder doc_builder,
			TransformerFactory transformerfactory) {
		DataCollectorConfiguration.doc_builder = doc_builder;		
	}
	
	private static Element generateEnvironmentSettings(Document conf_doc, TestRoom t) {
		Element environment_configuration = conf_doc.createElement("environment");
		Element environment_name_element = conf_doc.createElement("name");
		environment_name_element.appendChild(
				conf_doc.createTextNode(t.getName())
				);
		environment_configuration.appendChild(environment_name_element);
		return environment_configuration;
	}
	
	private static Element generateSimulationConfig(Document conf_doc, Configuration monconf) {
		Element sim_configuration = conf_doc.createElement("config");
		Element sample_time_element = conf_doc.createElement("sampleTime");
		sample_time_element.appendChild(
				conf_doc.createTextNode(
						Float.toString(monconf.getDatacollectorsettings().getSampleTime()))
				);
		Element geom_error_element = conf_doc.createElement("geomError");
		geom_error_element.appendChild(
				conf_doc.createTextNode(
						Float.toString(monconf.getSimulator().getProperties().getGeomError()))
		);
		sim_configuration.appendChild(sample_time_element);
		sim_configuration.appendChild(geom_error_element);
		return sim_configuration;
	}
	
	private static Element generateTargetSettings(Document conf_doc, TestRoom t) {
		Element targets_element = conf_doc.createElement("targets");
		for (Mission m: t.getMissions()) {
			Element target_element = conf_doc.createElement("target");
			if (m.getRobot()==null) {
				Element target_element_name = conf_doc.createElement("name");
				target_element_name.appendChild(conf_doc.createTextNode("waffle"));
				target_element.appendChild(target_element_name);
			}
			else {
				Element target_element_name = conf_doc.createElement("name");
				target_element_name.appendChild(conf_doc.createTextNode(m.getRobot().getName()));
				target_element.appendChild(target_element_name);
			}
			targets_element.appendChild(target_element);
		}
		return targets_element;
	}
	
	private static Element generateBoundingPrimitiveElement(Document conf_doc, ObjectCatalogue catalog) {
		Element bprim_element = conf_doc.createElement("boundingprimitives");
		for (CatalogueItem ci: catalog.getCatalogueitem()) {
			for (BoundingPrimitive bi: ci.getSimulatorobject().getBoundingprimitive()){
				Element ci_bprim_element = conf_doc.createElement("objecttemplate");
				Element ci_bprim_name_element = conf_doc.createElement("name");
				ci_bprim_name_element.appendChild(conf_doc.createTextNode(ci.getSimulatorobject().getName()));
				ci_bprim_element.appendChild(ci_bprim_name_element);
				Element type_element = conf_doc.createElement("type");
				if (bi.getPrimitive() instanceof Cube) {
					type_element.appendChild(conf_doc.createTextNode("cube"));
					Element width_element = conf_doc.createElement("width");
					width_element.appendChild(
							conf_doc.createTextNode(Float.toString(((Cube)bi.getPrimitive()).getWidth()))
							);
					Element height_element = conf_doc.createElement("height");
					height_element.appendChild(
							conf_doc.createTextNode(Float.toString(((Cube)bi.getPrimitive()).getHeight()))
							);
					Element depth_element = conf_doc.createElement("depth");
					depth_element.appendChild(
							conf_doc.createTextNode(Float.toString(((Cube)bi.getPrimitive()).getDepth()))
							);
					ci_bprim_element.appendChild(width_element);
					ci_bprim_element.appendChild(height_element);
					ci_bprim_element.appendChild(depth_element);
					Element ci_bcenter_of_mass = conf_doc.createElement("centerOfMass");
					Element ci_bcenter_of_mass_x = conf_doc.createElement("x");
					ci_bcenter_of_mass_x.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getX())));
					Element ci_bcenter_of_mass_y = conf_doc.createElement("y");
					ci_bcenter_of_mass_y.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getY())));
					Element ci_bcenter_of_mass_z = conf_doc.createElement("z");
					ci_bcenter_of_mass_z.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getZ())));
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_x);
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_y);
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_z);
					ci_bprim_element.appendChild(ci_bcenter_of_mass);
				}
				else if (bi.getPrimitive() instanceof Sphere) {
					type_element.appendChild(conf_doc.createTextNode("sphere"));
					Element radius_element = conf_doc.createElement("radius");
					radius_element.appendChild(
							conf_doc.createTextNode(Float.toString(((Sphere)bi.getPrimitive()).getRadius()))
							);
					ci_bprim_element.appendChild(radius_element);
					Element ci_bcenter_of_mass = conf_doc.createElement("centerOfMass");
					Element ci_bcenter_of_mass_x = conf_doc.createElement("x");
					ci_bcenter_of_mass_x.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getX())));
					Element ci_bcenter_of_mass_y = conf_doc.createElement("y");
					ci_bcenter_of_mass_y.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getY())));
					Element ci_bcenter_of_mass_z = conf_doc.createElement("z");
					ci_bcenter_of_mass_z.appendChild(conf_doc.createTextNode(Float.toString(bi.getOffset().getZ())));
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_x);
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_y);
					ci_bcenter_of_mass.appendChild(ci_bcenter_of_mass_z);
					ci_bprim_element.appendChild(ci_bcenter_of_mass);
				}
				ci_bprim_element.appendChild(type_element);
				bprim_element.appendChild(ci_bprim_element);
			}
		}
		return bprim_element;
	}
	
	private static Element generateFilterElement(Document conf_doc, Configuration conf) {
		Element filter_settings_element = conf_doc.createElement("filterSettings");
		for (ObserverSetting obs: conf.getMonitoringsettings().getFiltersetting()) {
			Element filter_element = conf_doc.createElement("filter");
			Element name_element = conf_doc.createElement("name");
			name_element.appendChild(conf_doc.createTextNode(obs.getName()));
			filter_element.appendChild(name_element);
			Element enabled_element = conf_doc.createElement("enabled");
			enabled_element.appendChild(conf_doc.createTextNode(Boolean.toString(obs.isEnabled())));
			filter_element.appendChild(enabled_element);
			filter_settings_element.appendChild(filter_element);
		}
		return filter_settings_element;
	}
	
	private static Element generateDynamicObjectSetElement(Document conf_doc, List<String> dyn_objs) {
		Element dyn_set_element = conf_doc.createElement("dynamicObjects");
		for (String dyn_obj: dyn_objs) {
			Element target_dynamic_object = conf_doc.createElement("object_name");
			target_dynamic_object.appendChild(conf_doc.createTextNode(dyn_obj));
			dyn_set_element.appendChild(target_dynamic_object);
		}
		return dyn_set_element;
	}
	
	private static Element generateCatalogueMappingElement(Document conf_doc, Map<String, String> mapping) {
		Element catalog_element = conf_doc.createElement("objectMap");
		mapping.forEach((k,v)->{
			Element catalog_object_elem = conf_doc.createElement("objectmapping");
			Element catalog_object_elem_name = conf_doc.createElement("name");
			catalog_object_elem_name.appendChild(conf_doc.createTextNode(k));
			catalog_object_elem.appendChild(catalog_object_elem_name);
			Element catalog_object_elem_type = conf_doc.createElement("type");
			catalog_object_elem_type.appendChild(conf_doc.createTextNode(v));
			catalog_object_elem.appendChild(catalog_object_elem_type);
			catalog_element.appendChild(catalog_object_elem);
		});
		return catalog_element;
	}
	
	public static Document generateDataCollectorConfiguration(
			TestRoom t, Configuration monconf, ObjectCatalogue catalog,
			List<String> dyn_objs, Map<String, String> obj_mapping) {
		Document conf_doc = doc_builder.newDocument();
		monconf.getDatacollectorsettings().getSampleTime();
		Element conf_root = conf_doc.createElement("GazeboCollector");
		
		
		conf_root.appendChild(generateSimulationConfig(conf_doc, monconf));
		conf_root.appendChild(generateEnvironmentSettings(conf_doc, t));
		conf_root.appendChild(generateTargetSettings(conf_doc, t));
		conf_root.appendChild(generateDynamicObjectSetElement(conf_doc, dyn_objs));
		conf_root.appendChild(generateFilterElement(conf_doc, monconf));
		conf_root.appendChild(generateBoundingPrimitiveElement(conf_doc, catalog));
		conf_root.appendChild(generateCatalogueMappingElement(conf_doc, obj_mapping));
		conf_doc.appendChild(conf_root);
		return conf_doc;
	}
	
	
	
	
}
