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

	public abstract void execute(Simulation sim);
	
	
	@Override
	public int compareTo(Event event) {
	
		int output = 0;
		if(this.getTimeOfOccurrence() < event.getTimeOfOccurrence())
			output = -1;
		else if(this.getTimeOfOccurrence() == event.getTimeOfOccurrence()) {
			/*
			 *  compare CBFinished to Stage3Step3Finished.
			 *  this is because we always want all CBFinished for the same time as as stage3step3
			 *  to occur before the Stage3Step3Finished, since else we need to look into the future.
			 */
			if (this instanceof CBFinished && event instanceof Stage3Step3Finished) {
				output = -1;
			} else if (this instanceof Stage3Step3Finished && event instanceof CBFinished) {
				output = 1;
			}
			else
				output = 0;
		}
		else output = 1;
		return output;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName() + " scheduled:" + timeOfScheduling + " occurrence:" + timeOfOccurrence);
		return sb.toString();
	}
	
}
