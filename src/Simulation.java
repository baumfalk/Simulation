import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Events.ConveyorBeltXFinishedDVD;
import Events.Event;
import Events.MachineXStage1Breakdown;
import Events.MachineXStage1FinishedDVD;
import Events.MachineXStage1Repaired;
import Events.MachineXStage2FinishedDVD;
import Events.SimulationFinished;
import Machines.ConveyorBelt;
import Machines.Machine;
import Machines.MachineStageOne;
import Machines.MachineStageTwo;
import Misc.DVD;
import Misc.Statistics;
import Stages.StateConveyorBelt;
import Stages.StateStageOne;
import Stages.StateStageTwo;

public class Simulation {
	
	private int currentTime;
	
	private int runTime;
	private int maxBufferSize; // the same for every buffer
	private int batchSize;
	
	private boolean simulationFinished;
	private PriorityQueue<Event> eventQueue;
	private Statistics statistics;

	private ArrayList<LinkedList<DVD>> layerOneBuffers;
	private ArrayList<LinkedList<DVD>> layerTwoBuffers;
	private ArrayList<LinkedList<DVD>> layerThreeBuffers;
	
	private ArrayList<MachineStageOne> stageOneMachines;
	private ArrayList<MachineStageTwo> stageTwoMachines;
	private ArrayList<ConveyorBelt> conveyorBelts;
	private int DVDsprocessed;
	static int hours = 1;
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		runTime =hours*60*60;
		System.out.println("Starting a simulation with");
		System.out.println("\t running time: " + runTime);
		System.out.println("\t maximum buffer size: " + maxBufferSize);
		System.out.println("\t batch size: " + batchSize);
		new Simulation(runTime, maxBufferSize, batchSize).run();
	}
	
	
	public Simulation(int runTime, int maxBufferSize, int batchSize) 
	{
		this.currentTime = 0;
		this.runTime = runTime;
		this.maxBufferSize = maxBufferSize;
		this.batchSize = batchSize;
		simulationFinished = false;
		eventQueue = new PriorityQueue<Event>();
		DVDsprocessed=0;
		createBuffers();
		createMachines();
		createStatistics();
		
		setup();
	}
	
	private void createBuffers()  {
		layerOneBuffers = new ArrayList<LinkedList<DVD>>();
		layerTwoBuffers = new ArrayList<LinkedList<DVD>>();
		layerThreeBuffers = new ArrayList<LinkedList<DVD>>();
		
		for(int i =0; i<2;i++) {
			layerOneBuffers.add(new LinkedList<DVD>());
			layerTwoBuffers.add(new LinkedList<DVD>());
			layerThreeBuffers.add(new LinkedList<DVD>());
		}
	}
	
	private void createMachines() {
		stageOneMachines = new ArrayList<MachineStageOne>();
		stageTwoMachines = new ArrayList<MachineStageTwo>();
		conveyorBelts = new ArrayList<ConveyorBelt>();
		for (int i = 1; i <= 4;i++) 
		{
			// first two connected to buffer one,
			if(i <= 2) 
				stageOneMachines.add(new MachineStageOne(i,layerOneBuffers.get(0)));
			// second two connected to buffer two
			else 
				stageOneMachines.add(new MachineStageOne(i,layerOneBuffers.get(1)));
		}
		
		for (int i = 1; i <= 2;i++) 
		{
			stageTwoMachines.add(new MachineStageTwo(i,layerOneBuffers.get(i-1),layerTwoBuffers.get(i-1)));
			conveyorBelts.add(new ConveyorBelt(i));
		}
	}
	
	private void createStatistics() {
		statistics = new Statistics();
		/*for (int i = 1; i <= 4;i++) {
			statistics.addStatistic("Average processing time S1M"+i, 0f,"seconds/DVD");
		}
		for (int i = 1; i <= 2;i++) {
			statistics.addStatistic("Average processing time S2M"+i, 0f,"seconds/DVD");
		}*/
		statistics.addStatistic("Throughput time per DVD",0.0f,"seconds");
		statistics.addStatistic("Output per hour",0.0f,"DVD/hour");
	}
	
	private void setup()
	{
		// setup the first stage
		for(Machine m : stageOneMachines) 
		{
			// production
			DVD dvd = new DVD(currentTime);
			int machineProcTime = stageOneMachines.get(m.machineNumber-1).generateProcessingTime();
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			
			//and breakdown
			int breakdownTime = currentTime+stageOneMachines.get(m.machineNumber-1).generateBreakDownTime();
			int repairTime =  Math.round(stageOneMachines.get(m.machineNumber-1).generateRepairTime());
			Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, m.machineNumber, repairTime);
			eventQueue.add(machinestage1breakdown);
		}
		// when are we finished with the simulation
		Event simulationFinished = new SimulationFinished(runTime);
		eventQueue.add(simulationFinished);
	}
	
	public void printState()
	{
		Event[] eventList = new Event[eventQueue.size()];
		eventQueue.toArray(eventList) ;
		Arrays.sort(eventList);
		System.out.println("EventQueue: ");
		for(int i =0; i < eventQueue.size();i++) {
			System.out.println(eventList[i].getClass().getSimpleName() + " at "+  eventList[i].getTimeOfOccurence());
		}
		System.out.println();
		System.out.println("Machines Stage 1");
		for(int i =0;i<4;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageOneMachines.get(i).state);
		}
		System.out.println("Buffers between Stage 1 and 2");
		for(int i =0;i<2;i++)
		{
			System.out.println("Buffer " +(i+1)+" size: "+ layerOneBuffers.get(i).size());
		}
		System.out.println("Machines Stage 2");
		for(int i =0;i<2;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageTwoMachines.get(i).state);
		}
		System.out.println("Conveyor Belts");
		for(int i =0;i<2;i++)
		{
			System.out.println("CB " +(i+1)+" state: " + conveyorBelts.get(i).state);
		}
		System.out.println("Buffers between Conveyor Belt and Stage 3");
		for(int i =0;i<2;i++)
		{
			System.out.println("Buffer " +(i+1)+" size: "+ layerTwoBuffers.get(i).size());
		}
	}
	
	public void run() {
		do {
			System.out.println("The current time is " + currentTime);
			printState();
			
			Event event = eventQueue.remove();
			currentTime = event.getTimeOfOccurence();
			System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
			
			// stage 1
			if(event instanceof MachineXStage1FinishedDVD) {
				process_MachineXStage1FinishedDVD_event((MachineXStage1FinishedDVD) event);
			} 
			else if(event instanceof MachineXStage1Breakdown) {
				process_MachineXStage1Breakdown_event((MachineXStage1Breakdown) event);
			} 
			else if(event instanceof MachineXStage1Repaired) {
				process_MachineXStage1Repaired_event((MachineXStage1Repaired) event);
			}
			
			// stage 2
			else if(event instanceof MachineXStage2FinishedDVD) {
				process_MachineXStage2FinishedDVD_event((MachineXStage2FinishedDVD) event);
			}
			
			// conveyor belt
			else if(event instanceof ConveyorBeltXFinishedDVD) {
				process_ConveyorBeltXFinishedDVD_event((ConveyorBeltXFinishedDVD) event);
			}
			
			else if (event instanceof SimulationFinished) {
				process_SimulationFinished_event(event);
			}
			System.out.println();
		} while(!simulationFinished);
	}

	private void process_ConveyorBeltXFinishedDVD_event(ConveyorBeltXFinishedDVD event) {
		
		ConveyorBelt cb = conveyorBelts.get(event.conveyorbeltNumber-1);
		switch (cb.state) {
		case Idle:
			break;
		case Running:
			//buffer to the right is not full
			if(layerTwoBuffers.get(event.conveyorbeltNumber-1).size()!=maxBufferSize) {
				layerTwoBuffers.get(cb.conveyorBeltNumber-1).add(event.dvd);
				cb.dvdsOnBelt.pop();
			} else {
				System.out.println("\t Buffer right to Conveyor Belt "+ cb.conveyorBeltNumber +" is full! Going Idle");
				cb.state = StateConveyorBelt.Idle;
			}
			break;
		}
		// buffer to the right is full
	}


	private void process_MachineXStage1FinishedDVD_event(MachineXStage1FinishedDVD event) {
		System.out.println("\t Looking at Stage 1, machine " + event.getMachineNumber());
		MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
		switch(stageOneMachines.get(event.getMachineNumber()-1).state)
		{
		case Running:
			int machineProcTime = m.generateProcessingTime();
			int machineFinishedTime = machineProcTime + currentTime;
			int machine2Number = (m.machineNumber <= 2) ? 0 : 1;
			MachineStageTwo m2 = stageTwoMachines.get(machine2Number);
			
			// directly feed the dvd into machine two
			if(m2.state == StateStageTwo.Idle){
				System.out.println("\t Reactivating machine " +m2.machineNumber + " at stage 2!");
				m2.state = StateStageTwo.Running;
				int machineProcTimeM2 = m2.generateProcessingTime(); 
				int machineFinishedTimeM2 = machineProcTimeM2 + currentTime;
				Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, event.getFinishedDVD(), machineProcTimeM2);
				eventQueue.add(event_m2);
			}
			
			if(m.rightBuffer.size() == maxBufferSize)
			{
				m.state = StateStageOne.Idle;
				m.dvdBeingProcessed = event.getFinishedDVD();
				m.processingTimeLeft = 0;
				m.totalProcessingTime = event.getProcTime();
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to Stage 1, machine " + m.machineNumber +" is full!");
			} else {
				m.rightBuffer.add(event.getFinishedDVD());
				DVD dvd = new DVD(currentTime);
				Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
				eventQueue.add(machinestage1);
				System.out.println("\t DVD successfully processed in Stage 1, machine " +event.getMachineNumber());
			}
			break;
		
		// no repair has taken place before:
		// jf-----br------if-----
		// |-------|------|-----
		case Broken:
			m.state = StateStageOne.BrokenAndDVDBeforeRepair;
			int timeSupposedlyFinished = event.getTimeOfOccurence();
			int timeCrashed = stageOneMachines.get(event.getMachineNumber()-1).getLastBreakDownTime();
			int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
			System.out.println("\t Machine broken, DVD stuck! Time still needed in machine: " + processingTimeLeft);
			// machine is broken, DVD is stuck in machine!
			
			m.processingTimeLeft = processingTimeLeft;
			m.totalProcessingTime = event.getProcTime();
			m.dvdBeingProcessed = event.getFinishedDVD();
			break;
		// repair has taken place before:
		// jf-----br------r-----jf
		// |-------|------|-----|
		// time between br and r has to be done again
		case BrokenAndRepairedBeforeDVD:
			System.out.println("\t Machine " + m.machineNumber + " broke down and was repaired before it could finish it's dvd. Rescheduling.");
			m.state = StateStageOne.Running;
			int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
			m.lastRepairTime = m.lastBreakDownTime = -1;
			int newFinishTime = currentTime + timeFalselyRun;
			Event newEvent = new MachineXStage1FinishedDVD(newFinishTime, m.machineNumber,event.getFinishedDVD(), event.getProcTime());
			eventQueue.add(newEvent);
			break;
		default:
			break;
		}
	}

	private void process_MachineXStage2FinishedDVD_event(MachineXStage2FinishedDVD event) {
		System.out.println("\t Looking at Stage 2, machine " + event.getMachineNumber());
		MachineStageTwo m = stageTwoMachines.get(event.getMachineNumber()-1);
		// normal
		if(!m.breakDVD()) {
			System.out.println("\t Didn't break the DVD!");
			ConveyorBelt cb = conveyorBelts.get(m.machineNumber-1);
			
			if(cb.state == StateConveyorBelt.Idle)
			{
				m.state = StateStageTwo.Idle;//TODO: enhance this
			} else {
				DVDsprocessed++;
				conveyorBelts.get(m.machineNumber-1).dvdsOnBelt.add(event.getFinishedDVD());
				conveyorBelts.get(m.machineNumber-1).dvdsOnBeltTime.add(currentTime);
				Event conveyorEvent = new ConveyorBeltXFinishedDVD(currentTime+5, m.machineNumber, event.getFinishedDVD());
				eventQueue.add(conveyorEvent);
			}
			
			
		} else {
			System.out.println("\t Machine " + m.machineNumber + " broke a DVD. :-(");
		
		}
		
		//schedule new event
		int machineProcTime = m.generateProcessingTime(); 
		int machineFinishedTime = machineProcTime + currentTime;
		// buffer to the left empty?
		if(m.leftBuffer.isEmpty())
		{
			m.state = StateStageTwo.Idle;
			System.out.println("\t Buffer empty, going idle");
		} else {
			DVD dvd = layerOneBuffers.get(m.machineNumber-1).pop();
			Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTime, m.machineNumber, dvd , machineProcTime);
			eventQueue.add(event_m2);
			
			MachineStageOne m1 = stageOneMachines.get((m.machineNumber-1)*2);
			MachineStageOne m2 = stageOneMachines.get((m.machineNumber-1)*2+1);
			if(m1.state == StateStageOne.Idle)
			{
				m1.state = StateStageOne.Running;
				Event event_s1_m1 = new MachineXStage1FinishedDVD(currentTime,m1.machineNumber,m1.dvdBeingProcessed,m1.totalProcessingTime);
				eventQueue.add(event_s1_m1);
				System.out.println("\t Reactivating machine at stage 1");
			}
			if(m2.state == StateStageOne.Idle)
			{
				m2.state = StateStageOne.Running;
				Event event_s1_m2 = new MachineXStage1FinishedDVD(currentTime,m1.machineNumber,m1.dvdBeingProcessed,m1.totalProcessingTime);
				eventQueue.add(event_s1_m2);
				System.out.println("\t Reactivating machine at stage 1");
			}
		}
	}

	private void process_MachineXStage1Repaired_event(MachineXStage1Repaired event) {
		MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
		switch(m.state)
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			
			Event dvdFinishedEvent = new MachineXStage1FinishedDVD(m.processingTimeLeft+currentTime, m.machineNumber, m.dvdBeingProcessed, m.totalProcessingTime);
			eventQueue.add(dvdFinishedEvent);
			m.state = StateStageOne.Running;
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			m.state = StateStageOne.BrokenAndRepairedBeforeDVD;
			m.lastRepairTime = event.getTimeOfOccurence();
			break;
		// other cases should not happen
		default:
			
			break;
		}
		
	}

	private void process_MachineXStage1Breakdown_event(MachineXStage1Breakdown event) {
		MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
		m.setLastBreakDownTime(event.getTimeOfOccurence());
			
		m.state = StateStageOne.Broken;
	
		Event repairEvent = new MachineXStage1Repaired(currentTime+event.getRepairTime(),event.getMachineNumber());
		eventQueue.add(repairEvent);
	}	

	private void process_SimulationFinished_event(Event event) {
		// print statistics
		simulationFinished = true;
		System.out.println(DVDsprocessed/hours);
		System.out.println(statistics);
	}
}
