package hu.bme.mit.inf.testing.artifact.mircontext.generator;


import javax.xml.parsers.DocumentBuilder;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extendedmircontext.Floor;
import extendedmircontext.Mission;
import extendedmircontext.TestRoom;
import hu.bme.mit.inf.mircontext.model.generator.simulatordescription.FloorState;
import hu.bme.mit.inf.mircontext.model.generator.simulatordescription.MapMircontext2SDF;
import hu.bme.mit.inf.robotics.util.geometry.Quaternion;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.AbstractGoal;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.Agent;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.Configuration;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.GeometricGoal;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.MissionTarget;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.MissiondescriptionFactory;
import hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.StarcraftAgent;

public class GenerateMissionArtifact {
	
	public static void generateGeometricGoalElement(Document d, GeometricGoal g, 
			Element goalTypeElement, 
			Element goalElement) {

		goalTypeElement.appendChild(d.createTextNode("geometric"));
		Element pointX = d.createElement("x");
		pointX.appendChild(d.createTextNode(
				Float.toString(
				g.getX()))
		);
		goalElement.appendChild(pointX);
		
		Element pointY = d.createElement("y");
		pointY.appendChild(d.createTextNode(
				Float.toString(
				g.getY()))
		);
		goalElement.appendChild(pointY);
		
		Element pointZ = d.createElement("z");
		pointZ.appendChild(d.createTextNode(
				Float.toString(
				g.getZ()))
		);
		goalElement.appendChild(pointZ);
		
		Element pointOx = d.createElement("ox");
		pointOx.appendChild(d.createTextNode(
				Float.toString(
				g.getOx()))
		);
		goalElement.appendChild(pointOx);
		
		Element pointOy = d.createElement("oy");
		pointOy.appendChild(d.createTextNode(
				Float.toString(
				g.getOy()))
		);
		goalElement.appendChild(pointOy);
		
		Element pointOz = d.createElement("oz");
		pointOz.appendChild(d.createTextNode(
				Float.toString(
				g.getOz()))
		);
		goalElement.appendChild(pointOz);
		
		Element pointOw = d.createElement("ow");
		pointOw.appendChild(d.createTextNode(
				Float.toString(
				g.getOw()))
		);
		goalElement.appendChild(pointOw);
	}
	
	public static Element generateAgentDescriptionElement(Document d, Agent a) throws Exception{
		Element resRoot = d.createElement("agent");
		Element nameElement = d.createElement("name");
		nameElement.appendChild(d.createTextNode(a.getName()));
		resRoot.appendChild(nameElement);
		// Get type
		Element typeElement = d.createElement("type");
		String type="";
		if (a instanceof StarcraftAgent){
			type = "starcraft";
		}
		else if (a instanceof hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.Robot){
			type = "robot";
		} 
		if (type == "") {
			throw new Exception("Null element");
		}
		typeElement.appendChild(d.createTextNode(type));
		resRoot.appendChild(typeElement);
		
		Element configElement = d.createElement("config");		
		Element topicElement = d.createElement("wamptopic");		
		topicElement.appendChild(d.createTextNode(a.getConfiguration().getTopic()));
		Element prefixElement = d.createElement("prefix");
		prefixElement.appendChild(d.createTextNode(a.getConfiguration().getPrefix()));
		configElement.appendChild(topicElement);
		configElement.appendChild(prefixElement);
		resRoot.appendChild(configElement);
		
		Element missionTargetElement = d.createElement("missiontarget");		
		for (MissionTarget t: a.getMissiontarget()){
			Element targetIdElement = d.createElement("target");
			targetIdElement.appendChild(d.createTextNode(t.getTarget_id()));
			missionTargetElement.appendChild(targetIdElement);
			Element goalsElement = d.createElement("goals");
			for (AbstractGoal g: t.getGoal()){
				
				Element goalElement = d.createElement("goal");
				Element goalTypeElement = d.createElement("goalType");
				if (g instanceof GeometricGoal){
					System.out.println(((GeometricGoal) g).getX()+" "+((GeometricGoal) g).getY());				
					generateGeometricGoalElement(d, 
							(GeometricGoal)g, goalTypeElement, goalElement);
					
				}
				goalElement.appendChild(goalTypeElement);
				goalsElement.appendChild(goalElement);
			}
			missionTargetElement.appendChild(goalsElement);
		}
		
		resRoot.appendChild(missionTargetElement);
		return resRoot;
	}
	
	public static void createMission(Resource mission_res, Document mission_document, 
			MapMircontext2SDF mr2, TestRoom t, String prefix) {
		hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.Mission m = 
				MissiondescriptionFactory.eINSTANCE.createMission();
		for (Mission o: t.getMissions()) {
			hu.bme.mit.inf.testing.robotics.mission.description.missiondescription.Robot r =
					MissiondescriptionFactory.eINSTANCE.createRobot();
			r.setName(o.getRobot().getName());
			r.setRobottype(o.getRobot().getRobot_type());
			
			GeometricGoal g_start = MissiondescriptionFactory.eINSTANCE.createGeometricGoal();
			FloorState start_state = mr2.getCoordState((Floor)o.getStart()); 
			MissionTarget target = MissiondescriptionFactory.eINSTANCE.createMissionTarget();
			target.getGoal().add(g_start);
			g_start.setX(start_state.getX()+0.5f);
			g_start.setY(start_state.getY()+0.5f);
			g_start.setZ(0.0f);
			
			GeometricGoal g_end = MissiondescriptionFactory.eINSTANCE.createGeometricGoal();
			FloorState end_state = mr2.getCoordState((Floor)o.getEnd());
			target.setTarget_id("map");
			target.getGoal().add(g_end);
			System.out.println(end_state.getX()+" "+end_state.getY());
			g_end.setX(end_state.getX()+0.5f);
			g_end.setY(end_state.getY()+0.5f);
			g_end.setZ(0.0f);
			
			Quaternion o_goal = CommonUtils.getPossibleOrientation(end_state.getFloor(), mr2);
			g_end.setOx((float)o_goal.getX());
			g_end.setOy((float)o_goal.getY());
			g_end.setOz((float)o_goal.getZ());
			g_end.setOw((float)o_goal.getW());
					
			r.getMissiontarget().add(target);
			//r.getMissiontarget().add(t_end);
			
			Configuration c = MissiondescriptionFactory.eINSTANCE.createConfiguration();
			c.setTopic("hu.bme.mit.inf.testing.robotics.goals");
			c.setPrefix("/");
			
			r.setConfiguration(c);
			
			m.getAgents().add(r);
		}
		mission_res.getContents().add(m);
		m.setName(prefix+t.getName());
		
		Element missionElement = mission_document.createElement("mission");
		Element missionName = mission_document.createElement("name");
		missionName.appendChild(mission_document.createTextNode(m.getName()));
		Element descriptionElement = mission_document.createElement("description");
		for (Agent a: m.getAgents()){
			try {
				descriptionElement.appendChild(generateAgentDescriptionElement(
						mission_document, a));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		missionElement.appendChild(descriptionElement);
		missionElement.appendChild(missionName);
		mission_document.appendChild(missionElement);
	}
	
	
	public static Document generateMission(ResourceSet resSet, DocumentBuilder doc_builder, 
			MapMircontext2SDF mr2, 
			TestRoom t, String outputfilename, String prefix) {
		Resource mission_res = resSet.createResource(URI.createURI("ws://newmission.mission"));
		
		
		Document mission_document = doc_builder.newDocument();
		createMission(mission_res, mission_document, mr2, t, prefix);
		
		return mission_document;
	}
}
