import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class Panel extends JPanel implements ActionListener {

	private JButton button, delete;
	private JFileChooser chooser;
	private JPanel buttonPanel, deletePanel, checkPanel;
	private JList<String> displayList;
	private DefaultListModel<String> display;
	private JRadioButton cQuick, cSave;
	private ButtonGroup buttonGroup;
	private boolean hashType;

	Panel() {
		super(new BorderLayout());

		button = new JButton("Browse Directory");
		button.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.add(button);

		delete = new JButton("Delete Files");
		deletePanel = new JPanel();
		deletePanel.add(delete);

		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		display = new DefaultListModel<String>();
		displayList = new JList<String>(display);
		displayList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 				// MULTIPLE FILES SELECTION

		cSave = new JRadioButton("Memory Saver");
		cQuick = new JRadioButton("Quick Finder");
		buttonGroup = new ButtonGroup();											  				// ONLY ONE RADIO BUTTON ACTIVE
		cSave.setSelected(true);
		buttonGroup.add(cSave);
		buttonGroup.add(cQuick);
		checkPanel = new JPanel();
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
		checkPanel.add(cQuick);
		checkPanel.add(cSave);

		add(buttonPanel, BorderLayout.PAGE_START);
		add(checkPanel, BorderLayout.WEST);
		add(new JScrollPane(displayList), BorderLayout.CENTER);
		add(deletePanel, BorderLayout.PAGE_END);

		hashType = true;																			// DEFAULT QUICK SEARCH
		
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		
		final int returnOption = chooser.showOpenDialog(this); 

		findDuplicates(returnOption);

		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure to delete these files?");
				if(dialogResult == JOptionPane.YES_OPTION){
					Object item = null;
					int[] itemIndex = displayList.getSelectedIndices();
					for (int i = 0; i < itemIndex.length; i++) {
						item = displayList.getModel().getElementAt(itemIndex[i]);
						//	display.addElement(item.toString());
						try {
							Files.deleteIfExists(new File(item.toString()).toPath());				// DELETE FILES AT LOCATION
						} catch (IOException e) {
								e.printStackTrace();
						}
					}
					findDuplicates(returnOption);
				}	
			}
			});
	}

	private void findDuplicates(int returnOption) {
		display = (DefaultListModel<String>) displayList.getModel();								// CLEAR DISPLAY LIST
		display.removeAllElements();
					
		hashType = cQuick.isSelected();																// TYPE OF HASH CALCULATION
	
		if (returnOption == JFileChooser.APPROVE_OPTION) {
			File directory = chooser.getSelectedFile();
			Map<String, List<String>> duplicateList = new HashMap<String, List<String>>();
			try {
				Finder.find(duplicateList, directory, hashType);									// FIND DUPLICATE FILES
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			display.addElement(	"Finding duplicates in directory '" + directory.getName()+"'");
			display.addElement("Directory location : " + directory.getParent());
			display.addElement("[Double Click on file to open in directory and select the files to delete]");
			
			for (List<String> list : duplicateList.values()) {
				if (list.size() > 1) {
					display.addElement("---------------------------------------------------------------------------------------------------------------");
					display.addElement("Duplicates found :");
					for (String name : list)
						display.addElement(name);
				}
			}
			display.addElement("---------------------------------------------------------------------------------------------------------------");

			displayList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						JList list = (JList) evt.getSource();
						int index = list.locationToIndex(evt.getPoint());
						String str = display.getElementAt(index).toString();
						if (new File(str) != null) {
							try {
								Desktop.getDesktop().open(new File(str).getParentFile());			// DOUBLE CLICK OPENS FILE DIRECTORY
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
	}

}
