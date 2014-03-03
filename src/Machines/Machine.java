package Machines;

public abstract class Machine {
	public final int machineNumber;
	
	public Machine(int machineNumber)
	{
		this.machineNumber = machineNumber;
	}
	
	
	
	public abstract float generateProcessingTime();
}
