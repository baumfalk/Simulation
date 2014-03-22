package events;

import simulation.Simulation;

public abstract class Event implements Comparable<Event> {
	protected final int timeOfOccurrence;
	protected final int timeOfScheduling;
	protected final String scheduler;

	public Event(int t, int tos, String scheduledBy)
	{
		this.timeOfOccurrence = t;
		this.timeOfScheduling = tos;
		this.scheduler = scheduledBy;
	}
	public int getTimeOfOccurrence() {
		return timeOfOccurrence;
	}
	
	public int getTimeOfScheduling() {
		return timeOfScheduling;
	}
	
	public String getScheduler() {
		return scheduler;
	}

	public void execute(Simulation sim) {
		scheduleEvents(sim);
		updateStatistics(sim);
	}
	protected abstract void scheduleEvents(Simulation sim);
	protected abstract void updateStatistics(Simulation sim);
	
	
	@Override
	public int compareTo(Event event) {
		int output = 0;
		if(this.getTimeOfOccurrence() < event.getTimeOfOccurrence())
			output = -1;
		else if(this.getTimeOfOccurrence() == event.getTimeOfOccurrence())
			output = 0;
		else output = 1;
		return output;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + " scheduled:" + timeOfScheduling + " occurrence:" + timeOfOccurrence);
		return sb.toString();
	}
	
}
