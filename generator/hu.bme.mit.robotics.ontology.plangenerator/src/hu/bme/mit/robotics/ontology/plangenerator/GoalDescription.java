package hu.bme.mit.robotics.ontology.plangenerator;

public class GoalDescription {
	private final double x;
	private final double y;
	private final double z;
	
	private final double o_x;
	private final double o_y;
	private final double o_z;
	private final double o_w;
	
	public GoalDescription(double x, double y, double z, double o_x, double o_y, double o_z, double o_w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.o_x = o_x;
		this.o_y = o_y;
		this.o_z = o_z;
		this.o_w = o_w;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getO_x() {
		return o_x;
	}

	public double getO_y() {
		return o_y;
	}

	public double getO_z() {
		return o_z;
	}

	public double getO_w() {
		return o_w;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return x+" "+y+" "+z+" "+o_x+" "+o_y+" "+o_z+" "+o_w;
	}
}
