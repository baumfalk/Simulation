package misc;

public class DVD {
	private final int timeOfEnteringPipeLine;
	private int timeOfLeavingPipeLine;
	
	public DVD(int t)
	{
		this.timeOfEnteringPipeLine = t;
	}

	public int getTimeOfEnteringPipeLine() {
		return timeOfEnteringPipeLine;
	}

	public int getTimeOfLeavingPipeLine() {
		return timeOfLeavingPipeLine;
	}

	public void setTimeOfLeavingPipeLine(int timeOfLeavingPipeLine) {
		this.timeOfLeavingPipeLine = timeOfLeavingPipeLine;
	}

	public int throughputTime() {
		// TODO Auto-generated method stub
		return timeOfLeavingPipeLine - timeOfEnteringPipeLine;
	}
}
