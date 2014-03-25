package simulation;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ListWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2608096954971610548L;
	private JPanel contentPane;
	private DefaultListModel listModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ListWindow frame = new ListWindow("Sample", 100,100);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ListWindow(String name, int x, int y) {
		setTitle("DVD Factory Simulation - " + name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(x, y, 400, 314);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblEventList = new JLabel(name);
		lblEventList.setBounds(20, 11, 286, 14);
		contentPane.add(lblEventList);
		listModel = new DefaultListModel();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 36, 354, 238);
		JList list = new JList();
		list.setModel(listModel);
		
		list.setBounds(20, 36, 274, 399);
		scrollPane.setViewportView(list);
		//contentPane.add(list);
		contentPane.add(scrollPane);
	}
	
	public void newList(ArrayList<String> stringList)
	{
		listModel.clear();
		for(String s : stringList) {
			listModel.addElement(s);
		}
	}

}
