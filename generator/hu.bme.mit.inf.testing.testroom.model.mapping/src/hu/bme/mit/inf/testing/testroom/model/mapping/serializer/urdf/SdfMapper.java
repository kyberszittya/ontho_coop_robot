package hu.bme.mit.inf.testing.testroom.model.mapping.serializer.urdf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gazeboobject.CollisionBody;
import gazeboobject.Cube;
import gazeboobject.Material;
import gazeboobject.Mesh;
import gazeboobject.Orientation;
import gazeboobject.Pose;
import gazeboobject.Position;
import gazeboobject.Script;
import gazeboobject.SimulatorObject;
import gazeboobject.VisualBody;

public class SdfMapper {
	private Document doc;	
	private Element rootelement;
	private Element worldelement;
	
	private List<String> container_objects;
	
	public List<String> initializeContainerNameList(Collection<String> list) {
		container_objects.addAll(list);
		return container_objects;
	}
	
	public SdfMapper(Document docu) {		
		this.doc = docu;
		container_objects = new ArrayList<>();
		
		rootelement = doc.createElement("sdf");
		rootelement.setAttribute("version", "1.5");
		
		worldelement = doc.createElement("world");
		worldelement.setAttribute("name", "complexai");
		
		Element sun_element = doc.createElement("include");
		Element sun_include = doc.createElement("uri");
		sun_include.appendChild(doc.createTextNode("model://sun"));
		sun_element.appendChild(sun_include);
		worldelement.appendChild(sun_element);
		rootelement.appendChild(worldelement);
		doc.appendChild(rootelement);
	}
	
	public Element generateScriptElement(Script s) {
		Element e = doc.createElement("script");
		Element e_name = doc.createElement("name");
		e_name.appendChild(doc.createTextNode(s.getName()));
		Element euri = doc.createElement("uri");
		euri.appendChild(doc.createTextNode(s.getUri()));
		e.appendChild(euri);
		e.appendChild(e_name);
		return e;
	}
	
	public Element generateMaterialElement(Material m) {
		Element e = doc.createElement("material");
		e.appendChild(generateScriptElement(m.getScript()));
		
		return e;
	}
	
	public Element generateCubeElement(Cube c) {
		Element e = doc.createElement("box");
		Element size_element = doc.createElement("size");
		size_element.appendChild(doc.createTextNode(
				c.getWidth()+" "+c.getDepth()+" "+c.getHeight()));
		e.appendChild(size_element);
		return e;
	}
	
	public Element generateGeometryElement(Mesh c) {
		Element e = doc.createElement("geometry");
		if (c instanceof Cube) {
			e.appendChild(generateCubeElement((Cube)c));
		}
		return e;
	}
	
	public Element generateCollisionElement(CollisionBody c, String objname) {
		Element e = doc.createElement("collision");
		e.setAttribute("name", "collision"+objname);
		e.appendChild(generateGeometryElement(c.getMesh()));
		return e;
	}
	
	public Element generateVisualElement(VisualBody visualBody, String objname) {
		Element e = doc.createElement("visual");
		e.setAttribute("name", "visual"+objname);
		e.appendChild(generateGeometryElement(visualBody.getMesh()));
		e.appendChild(generateMaterialElement(visualBody.getMaterial()));
		return e;
	}
	
	public Element generatePoseElement(Pose p) {
		Element e = doc.createElement("pose");
		float roll = 0.0f;
		float pitch = 0.0f;
		Orientation q = p.getOrientation();
		float siny = +2.0f * (q.getW() * q.getZ() + q.getX() * q.getY());
		float cosy = +1.0f - 2.0f * (q.getY() * q.getY() + q.getZ() * q.getZ());  
		float yaw = (float)Math.atan2(siny, cosy);
		Position pos = p.getPosition();
		e.appendChild(doc.createTextNode(
				pos.getX()+" "+pos.getY()+" "+pos.getZ()+" "+roll+" "+pitch+" "+yaw));
		
		return e;
	}
	
	
	public String getRandomContainerModelPath() {
		Random r = new Random();
		
		//return "model://"+"container_"+container_objects.get(r.nextInt(container_objects.size())).toLowerCase();
		return "model://"+"container_"+container_objects.get(r.nextInt(container_objects.size())).toLowerCase();
	}
	
	public Element generateIncludeUriElement(gazeboobject.File f) {
		Element res = doc.createElement("uri");
		String model_path="";
		switch (f.getPath()) {
		case "generate_container":
			model_path = getRandomContainerModelPath();
			break;
		default:
			model_path = f.getPath();
			break;
		}
		res.appendChild(doc.createTextNode(model_path));
		return res;
	}

	
	public Element simulatorobjectToModelElement(SimulatorObject simobj) {
		if (simobj.getFile()==null) {
			Element res = doc.createElement("model");
			res.setAttribute("name", simobj.getName());
			res.appendChild(generatePoseElement(simobj.getPose()));
			Element name_element = doc.createElement("name");
			name_element.appendChild(doc.createTextNode(simobj.getName()));
			//res.appendChild(name_element);
			Element staticelement = doc.createElement("static");
			staticelement.appendChild(doc.createTextNode(
					Boolean.toString(simobj.getPhysical_attributes().isStatic())));
			res.appendChild(staticelement);
			
			Element link_element = doc.createElement("link");
			link_element.setAttribute("name", "link_"+simobj.getName());
			link_element.appendChild(generateCollisionElement(
					simobj.getLink().get(0).getCollisionbody().get(0), 
					simobj.getName()));
			link_element.appendChild(generateVisualElement(
					simobj.getLink().get(0).getBody().get(0), 
					simobj.getName()));
			// TODO: inertia and friends
			if (!simobj.getPhysical_attributes().isStatic()) {
				
			}
			
			
			res.appendChild(link_element);
			return res;
		}else {
			Element res = doc.createElement("include");
			res.appendChild(generatePoseElement(simobj.getPose()));
			res.setAttribute("name", simobj.getName());
			Element name_element = doc.createElement("name");
			name_element.appendChild(doc.createTextNode(simobj.getName()));
			res.appendChild(name_element);
			Element staticelement = doc.createElement("static");
			staticelement.appendChild(doc.createTextNode(
					Boolean.toString(simobj.getPhysical_attributes().isStatic())));
			res.appendChild(staticelement);
			res.appendChild(generateIncludeUriElement(simobj.getFile()));
			return res;
			
		}
	}
	
	
	public void appendNewSimobjToTree(SimulatorObject simobj) {
		worldelement.appendChild(simulatorobjectToModelElement(simobj));
	}
	
	public void writeToPath(String fn) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		File f = new File(fn);
		transformer.transform(new DOMSource(doc), new StreamResult(f));
	}

}
