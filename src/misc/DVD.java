package misc;

public class DVD {
	private final int timeOfEnteringPipeLine;
	public int timeOfEnteringConveyorBelt;
	public int expectedLeavingTimeConveyorBelt;
	public DVD(int t)
	{
		this.timeOfEnteringPipeLine = t;
		timeOfEnteringConveyorBelt = -1;
	}

	public int getTimeOfEnteringPipeLine() {
		return timeOfEnteringPipeLine;
	}

}
