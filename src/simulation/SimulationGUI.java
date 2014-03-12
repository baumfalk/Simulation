package simulation;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.OverlayLayout;

import machines.ConveyorBelt;
import machines.MachineStageOne;
import machines.MachineStageTwo;

public class SimulationGUI {

	private JFrame frmDvdFactorySimulation;
	private static EventList eventListWindow;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private Simulation simulation;
	private ArrayList<JLabel> stage1Labels;
	private ArrayList<JLabel> stage2Labels;
	private ArrayList<JLabel> stage3Labels;
	private ArrayList<JLabel> stage4Labels;
	private ArrayList<JLabel> conveyorBeltLabels;
	private ArrayList<JLabel> buffersOne;
	private ArrayList<JLabel> buffersTwo;
	private ArrayList<JLabel> buffersThree;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationGUI window = new SimulationGUI();
					window.frmDvdFactorySimulation.setVisible(true);
					eventListWindow = new EventList();
					eventListWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public SimulationGUI() throws IOException {
		initialize();
	}

	
	private void updateGUI()
	{
		for(int i=1;i<=4;i++) {
			MachineStageOne m = simulation.getMachineStage1(i);
			stage1Labels.get(i-1).setText(m.state.name());
			
		}
		for(int i=1;i<=2;i++) {
			MachineStageTwo m = simulation.getMachineStage2(i);
			stage2Labels.get(i-1).setText(m.state.name());
			buffersOne.get(i-1).setText(m.leftBuffer().currentDVDCount()+"");
			ConveyorBelt cb = simulation.getConveyorBelt(i);
			conveyorBeltLabels.get(i-1).setText(cb.state.name());
			buffersTwo.get(i-1).setText(cb.rightBuffer().currentDVDCount()+"");
		}
		eventListWindow.newList(simulation.getEventListString());
		eventListWindow.repaint();
		frmDvdFactorySimulation.repaint();
	}
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frmDvdFactorySimulation = new JFrame();
		frmDvdFactorySimulation.setTitle("DVD Factory Simulation");
		frmDvdFactorySimulation.setBounds(100, 100, 991, 653);
		frmDvdFactorySimulation.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDvdFactorySimulation.getContentPane().setLayout(null);
		
	
		JButton btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				simulation = new Simulation(60*60*24*30*6, 20, 20);
				updateGUI();
			}
		});
		btnStart.setBounds(10, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnStart);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				simulation = new Simulation(60*60*24*30*6, 20, 20);
				updateGUI();
			}
		});
		btnReset.setBounds(208, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnReset);
		
		JButton btnPause = new JButton("Pause");
		btnPause.setBounds(109, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnPause);
		
		JButton btnNextEvent = new JButton("Next Event");
		btnNextEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.nextStep();
				updateGUI();
			}
		});
		btnNextEvent.setBounds(307, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnNextEvent);
		
		JLabel lblSimulationSpeed = new JLabel("Simulation Speed:");
		lblSimulationSpeed.setBounds(406, 15, 119, 14);
		frmDvdFactorySimulation.getContentPane().add(lblSimulationSpeed);
		
		JRadioButton rdbtnMsevent = new JRadioButton("10 ms/event");
		rdbtnMsevent.setBounds(512, 11, 95, 23);
		rdbtnMsevent.setSelected(true);
		buttonGroup.add(rdbtnMsevent);
		frmDvdFactorySimulation.getContentPane().add(rdbtnMsevent);
		
		JRadioButton rdbtnMsevent_1 = new JRadioButton("50 ms/event");
		rdbtnMsevent_1.setBounds(623, 11, 105, 23);
		buttonGroup.add(rdbtnMsevent_1);
		frmDvdFactorySimulation.getContentPane().add(rdbtnMsevent_1);
		
		JRadioButton rdbtnMsevent_2 = new JRadioButton("300 ms/event");
		rdbtnMsevent_2.setBounds(730, 11, 102, 23);
		buttonGroup.add(rdbtnMsevent_2);
		frmDvdFactorySimulation.getContentPane().add(rdbtnMsevent_2);
		
		JLabel lblState = new JLabel("State:");
		lblState.setBounds(838, 15, 35, 14);
		frmDvdFactorySimulation.getContentPane().add(lblState);
		
		JLabel lblPaused = new JLabel("Paused");
		lblPaused.setBounds(873, 15, 46, 14);
		lblPaused.setBackground(Color.YELLOW);
		lblPaused.setOpaque(true);
		frmDvdFactorySimulation.getContentPane().add(lblPaused);
		
		initBuffers();
		initStage1();
		initConveyorBelts();
		initStage2();
		initStage3();
		initStage4();
	
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 45, 955, 559);
		frmDvdFactorySimulation.getContentPane().add(panel);
		panel.setLayout(new OverlayLayout(panel));
		
		JLabel lblFlowchart = new JLabel();
		lblFlowchart.setIcon(new ImageIcon("E:\\Projects\\Simulation\\flowchart.png"));
		panel.add(lblFlowchart);
		lblFlowchart.setBounds(10, 45, 955, 559);
	}
	
	private void initStage1() {
		stage1Labels = new ArrayList<JLabel>();
		
		JLabel lblS1M1State = new JLabel("S1M1 State");
		lblS1M1State.setBounds(65, 149, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M1State);
		stage1Labels.add(lblS1M1State);
		
		JLabel lblS1M2State = new JLabel("S1M2 State");
		lblS1M2State.setBounds(65, 258, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M2State);
		stage1Labels.add(lblS1M2State);
		
		JLabel lblS1M3State = new JLabel("S1M3 State");
		lblS1M3State.setBounds(65, 366, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M3State);
		stage1Labels.add(lblS1M3State);
		
		JLabel lblS1M4State = new JLabel("S1M4 State");
		lblS1M4State.setBounds(65, 471, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M4State);
		stage1Labels.add(lblS1M4State);
	}
	
	private void initBuffers() {
		buffersOne = new ArrayList<JLabel>();
		JLabel lblL1B1 = new JLabel("L1B1 State");
		lblL1B1.setBounds(197, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL1B1);
		buffersOne.add(lblL1B1);
		
		JLabel lblL1B2 = new JLabel("L1B2 State");
		lblL1B2.setBounds(197, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL1B2);
		buffersOne.add(lblL1B2);
		
		buffersTwo = new ArrayList<JLabel>();
		JLabel lblL2B1 = new JLabel("L2B1 State");
		lblL2B1.setBounds(500, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL2B1);
		buffersTwo.add(lblL2B1);
		
		JLabel lblL2B2 = new JLabel("L2B2 State");
		lblL2B2.setBounds(500, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL2B2);
		buffersTwo.add(lblL2B2);
		
		buffersThree = new ArrayList<JLabel>();
		JLabel lblL3B1 = new JLabel("L3B1 State");
		lblL3B1.setBounds(727, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL3B1);
		buffersThree.add(lblL3B1);
		
		JLabel lblL3B2 = new JLabel("L3B2 State");
		lblL3B2.setBounds(727, 508, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL3B2);
		buffersThree.add(lblL3B2);
	}
	
	private void initConveyorBelts() {
		conveyorBeltLabels = new ArrayList<JLabel>();
		JLabel lblCB1 = new JLabel("CB1 State");
		lblCB1.setBounds(392, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblCB1);
		conveyorBeltLabels.add(lblCB1);
		
		JLabel lblCB2 = new JLabel("CB2 State");
		lblCB2.setBounds(392, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblCB2);
		conveyorBeltLabels.add(lblCB2);
	}
	
	private void initStage2() {
		stage2Labels = new ArrayList<JLabel>();
		JLabel lblS2M1State = new JLabel("S2M1 State");
		lblS2M1State.setBounds(283, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS2M1State);
		stage2Labels.add(lblS2M1State);
		
		JLabel lblS2M2State = new JLabel("S2M2 State");
		lblS2M2State.setBounds(283, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS2M2State);
		stage2Labels.add(lblS2M2State);
	}
	
	private void initStage3() {
		
		stage3Labels = new ArrayList<JLabel>();
		JLabel lblS3M1State = new JLabel("S3M1 State");
		lblS3M1State.setBounds(606, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS3M1State);
		stage3Labels.add(lblS3M1State);
		
		JLabel lblS3M2State = new JLabel("S3M2 State");
		lblS3M2State.setBounds(606, 508, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS3M2State);
		stage3Labels.add(lblS3M2State);
	}
	
private void initStage4() {
		
		stage4Labels = new ArrayList<JLabel>();
		JLabel lblS4M1State = new JLabel("S4M1 State");
		lblS4M1State.setBounds(832, 202, 110, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS4M1State);
		stage4Labels.add(lblS4M1State);
		
		JLabel lblS4M2State = new JLabel("S4M2 State");
		lblS4M2State.setBounds(838, 421, 119, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS4M2State);
		stage4Labels.add(lblS4M2State);
	}
}
