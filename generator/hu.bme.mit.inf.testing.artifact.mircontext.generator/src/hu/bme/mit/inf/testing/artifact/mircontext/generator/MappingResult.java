package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import java.util.List;
import java.util.Map;

import extendedmircontext.TestRoom;

public class MappingResult {
	private final TestRoom testroom;
	private final List<String> dyn_objects;
	private final Map<String, String> cat_map;
	
	
	public MappingResult(TestRoom testroom, List<String> dyn_objects, Map<String, String> cat_map) {
		super();
		this.testroom = testroom;
		this.dyn_objects = dyn_objects;
		this.cat_map = cat_map;
	}


	public TestRoom getTestroom() {
		return testroom;
	}


	public List<String> getDyn_objects() {
		return dyn_objects;
	}


	public Map<String, String> getCat_map() {
		return cat_map;
	}
	
		
}
