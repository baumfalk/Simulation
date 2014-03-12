package events;

import simulation.Simulation;

public class SimulationFinished extends Event{

	public SimulationFinished(int t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		// print statistics
		sim.simulationFinished = true;
		System.out.println(sim.DVDsprocessed/Simulation.hours);
		System.out.println(sim.statistics);
	}

	

}
