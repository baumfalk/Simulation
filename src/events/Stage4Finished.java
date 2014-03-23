package events;

import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateStage3;
import states.StateStage4;

public class Stage4Finished extends MachineXEvent {

	private DVD dvd;

	public Stage4Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos, m, scheduledBy);
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
			if(s3m1.getState() == StateStage3.Blocked) {
				Event stage3finished =  new Stage3Step3Finished(sim.getCurrentTime(),sim.getCurrentTime(), s3m1.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(stage3finished);
				s3m1.setRunning();
			}
			if(s3m2.getState()  == StateStage3.Blocked) {
				
				Event stage3finished =  new Stage3Step3Finished(sim.getCurrentTime(),sim.getCurrentTime(), s3m2.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(stage3finished);
				s3m2.setRunning();
			}
		} else {
			int delay = 0;
			if(m.dvdLeft() == 0) 
			{
				delay = m.generateCartridgeRenewalTime();
				m.generateCartridgeRenewal();
				System.out.println("CARTRIDGE RENEWAL");
			} 
			m.addDVD(m.leftBuffer().removeFromBuffer());
			int processingTime = m.generateProcessingTime()+delay;
			int machineFinishedTime = sim.getCurrentTime() + processingTime ; 
			Event stage4finished =  new Stage4Finished(machineFinishedTime,sim.getCurrentTime(), machineNumber,this.getClass().getSimpleName());
			sim.addToEventQueue(stage4finished);
		}
		updateStatistics(sim);
	}

	@Override
	protected void updateMachines(Simulation sim) {
		
	}

	@Override
	protected void scheduleEvents(Simulation sim) {
		
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		if(dvd !=null) {
			sim.statistics.addToStatistic("Total DVDs processed", 1);
			sim.statistics.updateAverage("Throughput time per DVD",timeOfOccurrence-dvd.timeOfEnteringPipeLine);
		}
	}

}
