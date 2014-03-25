package machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import misc.DVD;
import states.StateConveyorBelt;
import buffer.Buffer;
import buffer.DVDBuffer;
import exceptions.InvalidStateException;

public class ConveyorBelt extends Machine {

	private StateConveyorBelt state;
	private int delayStartTime;
	
	
	private HashMap<Integer, Integer> dvdOvertimeLeft;
	private Buffer<Integer> dvdTimeOfEntering;

	private int idleTime;
	
	
	public ConveyorBelt(int conveyorBeltNumber, DVDBuffer rightBuffer) {
		super(conveyorBeltNumber, null, new ArrayList<DVDBuffer>(Arrays.asList(rightBuffer)),-1); // infinity
		state = StateConveyorBelt.Idle;
		dvdTimeOfEntering = new Buffer<Integer>(-1);
		dvdOvertimeLeft = new HashMap<Integer,Integer>();
	}
	
	@Override
	public int generateProcessingTime() {
		return 1;//5*60; //TODO: fix this
	}

	public StateConveyorBelt getState() {
		return state;
	}

	public void setIdle() {
		if(state == StateConveyorBelt.Running)
			state = StateConveyorBelt.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Idle with the state " + state);
				System.exit(1);
			}
		}
		
	}

	public void setBlocked() {
		if(state == StateConveyorBelt.Running)
			state = StateConveyorBelt.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Blocked with the state " + state);
				System.exit(1);
			}
		}
		
	}

	public void setRunning() {
		if(state == StateConveyorBelt.Idle || state == StateConveyorBelt.Blocked)
			state = StateConveyorBelt.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public void addDVD(DVD dvdTemp, int currentTime) {
		addDVD(dvdTemp);
		dvdTimeOfEntering.addToBuffer(currentTime);
		dvdOvertimeLeft.put(dvdTemp.id, 0); // no Overtime by default
	}

	public void startDelayTimer(int currentTime) {
		delayStartTime = currentTime;
	}

	public int getIdleTime() {
		return idleTime;
	}
	
	public void setTimeIdleStarted(int idleTime) {
		this.idleTime = idleTime;
	}

	public void addToDVDOvertime(int dvdID, int overtime) {
		int newValue =  dvdOvertimeLeft.get(dvdID) + overtime;
		dvdOvertimeLeft.put(dvdID, newValue);
	}
	
	public int getDVDOvertime(int dvdID) {
		// TODO Auto-generated method stub
		return dvdOvertimeLeft.get(dvdID);
	}
	
	public void setDVDOvertime(int dvdID, int newValue) {
		dvdOvertimeLeft.put(dvdID, newValue);

	}



}
