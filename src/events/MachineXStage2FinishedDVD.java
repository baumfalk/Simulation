package events;

import machines.ConveyorBelt;
import machines.MachineStageOne;
import machines.MachineStageTwo;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStageOne;
import states.StateStageTwo;

public class MachineXStage2FinishedDVD extends MachineXEvent {
	
	public MachineXStage2FinishedDVD(int t, int m, DVD d, int p) {
		super(t, m);
	
		finishedDVD = d;
		procTime = p;
	}

	private final DVD finishedDVD;
	private final int procTime;

	public DVD getFinishedDVD() {
		return finishedDVD;
	}

	public int getProcTime() {
		return procTime;
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		System.out.println("\t Looking at Stage 2, machine " + machineNumber);
		MachineStageTwo m = sim.getMachineStage2(machineNumber);
		// normal
		if(!m.breakDVD()) {
			System.out.println("\t Didn't break the DVD!");
			ConveyorBelt cb = sim.getConveyorBelt(m.machineNumber);
			
			if(cb.state == StateConveyorBelt.Idle)
			{
				m.state = StateStageTwo.Idle;//TODO: enhance this
				m.dvd = finishedDVD;
			} else {
				sim.DVDsprocessed++;
				cb.dvdsOnBelt.add(finishedDVD);
				cb.dvdsOnBeltTime.add(sim.getCurrentTime());
				Event conveyorEvent = new ConveyorBeltXFinishedDVD(sim.getCurrentTime()+5, m.machineNumber, finishedDVD);
				sim.addToEventQueue(conveyorEvent);
			}
			
		} else {
			System.out.println("\t Machine " + m.machineNumber + " broke a DVD. :-(");
		
		}
		
		//schedule new event
		int machineProcTime = m.generateProcessingTime(); 
		int machineFinishedTime = machineProcTime + sim.getCurrentTime();
		// buffer to the left empty?
		if(m.leftBuffer.isEmpty())
		{
			m.state = StateStageTwo.Idle;
			System.out.println("\t Buffer empty, going idle");
		} else {
			DVD dvd = sim.popFromLayerOneBuffer(m.machineNumber);
			Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTime, m.machineNumber, dvd , machineProcTime);
			sim.addToEventQueue(event_m2);
			
			MachineStageOne m1 = sim.getMachineStage1(m.machineNumber*2-1);
			MachineStageOne m2 = sim.getMachineStage1(m.machineNumber*2);
			if(m1.state == StateStageOne.Idle)
			{
				m1.state = StateStageOne.Running;
				Event event_s1_m1 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),m1.machineNumber,m1.dvdBeingProcessed,m1.totalProcessingTime);
				sim.addToEventQueue(event_s1_m1);
				System.out.println("\t Reactivating machine at stage 1");
			}
			if(m2.state == StateStageOne.Idle)
			{
				m2.state = StateStageOne.Running;
				Event event_s1_m2 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),m1.machineNumber,m1.dvdBeingProcessed,m1.totalProcessingTime);
				sim.addToEventQueue(event_s1_m2);
				System.out.println("\t Reactivating machine at stage 1");
			}
		}
	}

}
