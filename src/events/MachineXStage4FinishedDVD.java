package events;

import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateStage3;
import states.StateStage4;

public class MachineXStage4FinishedDVD extends MachineXEvent {

	private DVD dvd;

	public MachineXStage4FinishedDVD(int t, int tos, int m,String scheduledBy) {
		super(t, tos, m, scheduledBy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * Cases:
		 * 
		 */
		MachineStage4 m = sim.getMachineStage4(machineNumber);
		dvd = m.removeDVD();
		if(m.leftBuffer().isEmpty())
		{
			m.state = StateStage4.Idle;
			// machines blocked?
			MachineStage3 s3m1 = sim.getMachineStage3(machineNumber);
			MachineStage3 s3m2 = sim.getMachineStage3(3-machineNumber);
			if(s3m1.state == StateStage3.Blocked) {
				Event stage3finished =  new MachineXStage3Step3FinishedBatch(sim.getCurrentTime(),sim.getCurrentTime(), s3m1.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(stage3finished);
			}
			if(s3m1.state == StateStage3.Blocked) {
				Event stage3finished =  new MachineXStage3Step3FinishedBatch(sim.getCurrentTime(),sim.getCurrentTime(), s3m2.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(stage3finished);
			}
			
		} else {
			m.addDVD(m.leftBuffer().removeFromBuffer());
			int processingTime = m.generateProcessingTime();
			int machineFinishedTime = sim.getCurrentTime() + processingTime; 
			Event stage4finished =  new MachineXStage4FinishedDVD(machineFinishedTime,sim.getCurrentTime(), machineNumber,this.getClass().getSimpleName());
			sim.addToEventQueue(stage4finished);
		}
		updateStatistics(sim);
	}

	@Override
	public void updateMachines(Simulation sim) {
		
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		
	}

	@Override
	public void updateStatistics(Simulation sim) {
		if(dvd !=null) {
			sim.statistics.addToStatistic("Total DVDs processed", 1);
			sim.statistics.updateAverage("Throughput time per DVD",timeOfOccurence-dvd.getTimeOfEnteringPipeLine() );
			System.out.println(timeOfOccurence-dvd.getTimeOfEnteringPipeLine());
		}
	}

}
