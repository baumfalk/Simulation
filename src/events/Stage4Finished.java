package events;

import misc.DVD;
import simulation.Simulation;

public class Stage4Finished extends MachineXEvent {

	private DVD dvd;

	public Stage4Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos, m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		
	}

	
	protected void updateStatistics(Simulation sim) {
		if(dvd !=null) {
			sim.statistics.addToStatistic("Total DVDs processed", 1);
			sim.statistics.updateAverage("Throughput time per DVD",timeOfOccurrence-dvd.timeOfEnteringPipeLine);
		}
	}

}
