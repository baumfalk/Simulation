package events;

import simulation.Simulation;

public abstract class Event implements Comparable<Event> {
	protected final int timeOfOccurence;
	protected final int timeOfScheduling;
	
	public Event(int t, int tos)
	{
		this.timeOfOccurence = t;
		this.timeOfScheduling = tos;
	}
	public int getTimeOfOccurence() {
		return timeOfOccurence;
	}
	
	public int getTimeOfScheduling() {
		return timeOfScheduling;
	}

	public void execute(Simulation sim) {
		scheduleEvents(sim);
		updateStatistics(sim);
	}
	public abstract void scheduleEvents(Simulation sim);
	public abstract void updateStatistics(Simulation sim);
	
	
	@Override
	public int compareTo(Event event) {
		int output = 0;
		if(this.getTimeOfOccurence() < event.getTimeOfOccurence())
			output = -1;
		else if(this.getTimeOfOccurence() == event.getTimeOfOccurence())
			output = 0;
		else output = 1;
		return output;
	}
	
	
}
