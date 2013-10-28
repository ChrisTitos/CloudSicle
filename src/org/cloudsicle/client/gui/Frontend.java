package org.cloudsicle.client.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import org.cloudsicle.client.Session;

public class Frontend extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	
	private Session session = null;

	/**
	 * Launch the application.
	 */
	public static void launch(final Session s) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frontend frame = new Frontend(s);
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
	private Frontend() {
		setResizable(false);
		setTitle("CloudSicle Client Satisfaction Mediator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblFilesToBe = new JLabel("Files to be gifsicled:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblFilesToBe, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblFilesToBe, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblFilesToBe);
		
		JScrollPane listScrollPane = new JScrollPane();
		final JList<String> list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sl_contentPane.putConstraint(SpringLayout.NORTH, listScrollPane, 10, SpringLayout.SOUTH, lblFilesToBe);
		sl_contentPane.putConstraint(SpringLayout.WEST, listScrollPane, 20, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, listScrollPane, 150, SpringLayout.SOUTH, lblFilesToBe);
		sl_contentPane.putConstraint(SpringLayout.EAST, listScrollPane, -20, SpringLayout.EAST, contentPane);
		list.setModel(new DefaultListModel<String>());
		listScrollPane.setViewportView(list);
		contentPane.add(listScrollPane);
		
		JButton btnAddFile = new JButton("Add File");
		btnAddFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter(){

					@Override
					public boolean accept(File f) {
						if (f == null || f.getAbsolutePath().length() < 4)
							return false;
						String path = f.getAbsolutePath();
						return ".gif".equals(path.substring(path.length()-4));
					}

					@Override
					public String getDescription() {
						return ".gif files";
					}
					
				});
				int returnVal = fc.showOpenDialog(list);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
					int selected = list.getSelectedIndex();
					int index = selected != -1 ? selected + 1 : listModel.getSize();
					listModel.add(index, fc.getSelectedFile().getAbsolutePath());
					list.setSelectedIndex(index);
				}
				
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnAddFile, 6, SpringLayout.SOUTH, listScrollPane);
		contentPane.add(btnAddFile);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int selected = list.getSelectedIndex();
				if (selected == -1)
					return;
				DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
				listModel.remove(selected);
			}
		});
		sl_contentPane.putConstraint(SpringLayout.EAST, btnAddFile, -6, SpringLayout.WEST, btnRemove);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnRemove, 6, SpringLayout.SOUTH, listScrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnRemove, 0, SpringLayout.EAST, listScrollPane);
		contentPane.add(btnRemove);
		
		JLabel lblMasterIp = new JLabel("Master IP:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMasterIp, 0, SpringLayout.SOUTH, btnAddFile);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMasterIp, 0, SpringLayout.WEST, lblFilesToBe);
		contentPane.add(lblMasterIp);
		
		textField = new JTextField();
		textField.addCaretListener(new CaretListener() {
			public synchronized void caretUpdate(CaretEvent arg0) {
				String ip = textField.getText();
				if ("".equals(ip)){
					textField.setBackground(Color.RED);
					return;
				}
				Inet4Address i4a = null;
				try {
					i4a = (Inet4Address) Inet4Address.getByName(ip);
				} catch (UnknownHostException e) {
					textField.setBackground(Color.RED);
					return;
				}
				if (!ip.matches("[0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?")) {
					textField.setBackground(Color.RED);
					return;
				}
				try {
					if (i4a.isReachable(300)){
						textField.setBackground(Color.GREEN);
						return;
					} else {
						textField.setBackground(Color.RED);
						return;
					}
				} catch (IOException e) {
					textField.setBackground(Color.RED);
					return;
				}
			}
		});
		textField.setBackground(Color.RED);
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField, 0, SpringLayout.SOUTH, lblMasterIp);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 20, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, -20, SpringLayout.EAST, contentPane);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JPanel panel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.SOUTH, textField);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, contentPane);
		contentPane.add(panel);
		
		final JButton btnPerformCloudsicling = new JButton("Perform CloudSicling");
		btnPerformCloudsicling.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				btnPerformCloudsicling.setEnabled(false);
				if (session == null){
					btnPerformCloudsicling.setEnabled(true);
					JOptionPane.showMessageDialog(null, "Could not submit the job because there is no valid session.", "Woops..", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				ArrayList<String> files = new ArrayList<String>();
				DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
				for (int i = 0; i < listModel.getSize(); i++){
					String file = listModel.getElementAt(i);
					files.add(file);
				}
				InetAddress ia;
				try {
					ia = InetAddress.getByName(textField.getText());
				} catch (UnknownHostException e1) {
					btnPerformCloudsicling.setEnabled(true);
					JOptionPane.showMessageDialog(null, "Could not submit the job because the master IP could not be read.", "Woops..", JOptionPane.ERROR_MESSAGE); 
					return;
				}

				if (!session.requestCloudSicle(files, ia))
					JOptionPane.showMessageDialog(null, "Failed to submit job, could not connect to server.", "Woops..", JOptionPane.ERROR_MESSAGE);
				btnPerformCloudsicling.setEnabled(true);
			}
		});
		panel.add(btnPerformCloudsicling);
		
		JButton btnDown = new JButton("Down");
		btnDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int selected = list.getSelectedIndex();
				if (selected == -1 || selected == list.getModel().getSize()-1)
					return;
				DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
				String top = listModel.get(selected);
				String bottom = listModel.get(selected+1);
				listModel.set(selected, bottom);
				listModel.set(selected+1, top);
				list.setSelectedIndex(selected+1);
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnDown, 6, SpringLayout.SOUTH, listScrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnDown, -6, SpringLayout.WEST, btnAddFile);
		contentPane.add(btnDown);
		
		JButton btnUp = new JButton("Up");
		btnUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int selected = list.getSelectedIndex();
				if (selected == -1 || selected == 0)
					return;
				DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
				String top = listModel.get(selected);
				String bottom = listModel.get(selected-1);
				listModel.set(selected, bottom);
				listModel.set(selected-1, top);
				list.setSelectedIndex(selected-1);
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnUp, 6, SpringLayout.SOUTH, listScrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnUp, -6, SpringLayout.WEST, btnDown);
		contentPane.add(btnUp);
	}
	
	/**
	 * Create the Frontend and have it communicate through the client Session.
	 * 
	 * @param session The client's session.
	 */
	public Frontend(Session session){
		this();
		this.session = session;
	}
}
