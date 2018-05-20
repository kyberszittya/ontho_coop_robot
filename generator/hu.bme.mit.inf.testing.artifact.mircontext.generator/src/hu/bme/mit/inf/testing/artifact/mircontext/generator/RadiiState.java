package hu.bme.mit.inf.testing.artifact.mircontext.generator;

import extendedmircontext.Floor;

public class RadiiState {
	public int depth;
	public RadiiState parent;
	public Floor state;
	
	public RadiiState(int depth, Floor state, RadiiState parent) {
		this.depth = depth;
		this.parent = parent;
		this.state = state;
	}
}
