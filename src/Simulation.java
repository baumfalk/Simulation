import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Events.Event;
import Events.MachineXStage1Breakdown;
import Events.MachineXStage1FinishedDVD;
import Events.MachineXStage1Repaired;
import Events.MachineXStage2FinishedDVD;
import Events.SimulationFinished;
import Machines.Machine;
import Machines.MachineStageOne;
import Machines.MachineStageTwo;
import Misc.DVD;
import Misc.Statistics;
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
	
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		runTime = 500;
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
		}
	}
	
	private void createStatistics() {
		statistics = new Statistics();
		for (int i = 1; i <= 4;i++) {
			statistics.addStatistic("Average processing time S1M"+i, 0f,"seconds/DVD");
		}
		for (int i = 1; i <= 2;i++) {
			statistics.addStatistic("Average processing time S2M"+i, 0f,"seconds/DVD");
		}
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
			int machineProcTime = Math.round(stageOneMachines.get(m.machineNumber-1).generateProcessingTime());
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			
			//and breakdown
			int breakdownTime = currentTime+Math.round(stageOneMachines.get(m.machineNumber-1).generateBreakDownTime());
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

		for(int i =0;i<4;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageOneMachines.get(0).state);
		}
		for(int i =0;i<2;i++)
		{
			System.out.println("Buffer " +(i+1)+" size: "+ layerOneBuffers.get(i).size());
		}
		for(int i =0;i<2;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageTwoMachines.get(0).state);
		}
		
	}
	
	public void run() {
		do {
			Event event = eventQueue.remove();
			
			currentTime = event.getTimeOfOccurence();
			System.out.println("The new time is " + currentTime);
			System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
			
			printState();
			
			// stage 1
			if(event instanceof MachineXStage1FinishedDVD) {
				int machineNumber = ((MachineXStage1FinishedDVD) event).getMachineNumber();
				System.out.println("\t DVD finished (maybe) for stage 1 and machine " + machineNumber);
				process_MachineXStage1FinishedDVD_event((MachineXStage1FinishedDVD) event);
			} 
			else if(event instanceof MachineXStage1Breakdown) {
				int machineNumber = ((MachineXStage1Breakdown) event).getMachineNumber();
				System.out.println("\t ALERT: BREAKDOWN FOR MACHINE " + machineNumber+"!");
				process_MachineXStage1Breakdown_event((MachineXStage1Breakdown) event);
			} 
			else if(event instanceof MachineXStage1Repaired) {
				int machineNumber = ((MachineXStage1Repaired) event).getMachineNumber();
				System.out.println("\t Machine " + machineNumber+" repaired!!");
				process_MachineXStage1Repaired_event((MachineXStage1Repaired) event);
			}
			
			// stage 2
			else if(event instanceof MachineXStage2FinishedDVD) {
				int machineNumber = ((MachineXStage2FinishedDVD) event).getMachineNumber();
				System.out.println("\t DVD finished (maybe) for stage 2 and machine " + machineNumber);
				process_MachineXStage2Finished_event((MachineXStage2FinishedDVD) event);

			}
			
			else if (event instanceof SimulationFinished) {
				System.out.println("\t Finishing up the simulation at time " + currentTime);
				process_SimulationFinished_event(event);
			}
		} while(!simulationFinished);
	}

	private void process_MachineXStage1FinishedDVD_event(MachineXStage1FinishedDVD event) {
		MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
		switch(stageOneMachines.get(event.getMachineNumber()-1).state)
		{
		case Normal:
			int machineProcTime = m.generateProcessingTime();
			int machineFinishedTime = machineProcTime + currentTime;
			int machine2Number = (m.machineNumber <= 2) ? 0 : 1;
			MachineStageTwo m2 = stageTwoMachines.get(machine2Number);
			
			// directly feed the dvd into machine two
			if(m2.state == StateStageTwo.Idle){
				System.out.println("\t Reactivating machine at stage 2!");
				m2.state = StateStageTwo.Normal;
				int machineProcTimeM2 = m2.generateProcessingTime(); 
				int machineFinishedTimeM2 = machineProcTimeM2 + currentTime;
				Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, event.getFinishedDVD(), machineProcTimeM2);
				eventQueue.add(event_m2);
			}
			else if(m.rightBuffer.size() == maxBufferSize)
			{
				m.state = StateStageOne.Idle;
				m.dvdBeingProcessed = event.getFinishedDVD();
				m.processingTimeLeft = 0;
				m.totalProcessingTime = event.getProcTime();
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to machine " + m.machineNumber +" is full!");
			} else {
				m.rightBuffer.add(event.getFinishedDVD());
				DVD dvd = new DVD(currentTime);
				Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
				eventQueue.add(machinestage1);
				System.out.println("\t DVD succesfully processed in machine " +event.getMachineNumber());
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
			System.out.println("\t Machine " + m.machineNumber + "broke down and was repaired before it could finish it's dvd. Rescheduling.");
			m.state = StateStageOne.Normal;
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

	private void process_MachineXStage2Finished_event(MachineXStage2FinishedDVD event) {
		MachineStageTwo m = stageTwoMachines.get(event.getMachineNumber()-1);
		// normal
		if(!m.breakDVD()) {
			System.out.println("\t Didn't break the DVD!");
			
		} else {
			System.out.println("\t Machine " + m.machineNumber + " broke a DVD. :-(");
		}
		
		//schedule new event
		int machineProcTime = m.generateProcessingTime(); 
		int machineFinishedTime = machineProcTime + currentTime;
		if(layerOneBuffers.get(m.machineNumber-1).isEmpty())
		{
			m.state = StateStageTwo.Idle;
			System.out.println("\t Buffer empty, going idle");
		}
		else {
			DVD dvd = layerOneBuffers.get(m.machineNumber-1).pop();
			Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTime, m.machineNumber, dvd , machineProcTime);
			eventQueue.add(event_m2);
			
			MachineStageOne m1 = stageOneMachines.get((m.machineNumber-1)*2);
			MachineStageOne m2 = stageOneMachines.get((m.machineNumber-1)*2+1);
			if(m1.state == StateStageOne.Idle)
			{
				m1.state = StateStageOne.Normal;
				Event event_s1_m1 = new MachineXStage1FinishedDVD(currentTime,m1.machineNumber,m1.dvdBeingProcessed,m1.totalProcessingTime);
				eventQueue.add(event_s1_m1);
				System.out.println("\t Reactivating machine at stage 1");
			} 
			if(m2.state == StateStageOne.Idle)
			{
				m2.state = StateStageOne.Normal;
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
			m.state = StateStageOne.Normal;
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
		System.out.println(statistics);
	}
}
