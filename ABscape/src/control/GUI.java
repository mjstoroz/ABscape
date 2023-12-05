/* 
	Copyright Tim Dorscheidt, Amsterdam, The Netherlands, 2012

 	This file is part of ABscape.
 	ABscape is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ABscape is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ABscape.  If not, see <http://www.gnu.org/licenses/>.
    
    Note: ABscape was created during a course at The University of
    Groningen in 2006, and was heavily inspired by a previous model and
    scientific work by Van der Vaart et al. (2006): 
    Albert Hankel and Elske van der Vaart, see: Van der Vaart, E., de Boer,
    B., Hankel, A. & Verheij, B. (2006). Agents Adopting Agriculture:
    Modeling the Agricultural Transition. Proceedings of the Ninth
    International Conference on the Simulation of Adaptive Behavior
    (SAB'06), 750-762.
 */

package control;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tools.CompRow;
import world.Cell;
import world.Habitat;
import world.World;


class GUI extends JFrame implements ActionListener, ChangeListener, KeyListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	private JButton startButton, maxSpeedButton, viewButton;
	public JSlider turnsPerSecondSlider, threadPrioritySlider;
	public JLabel turnsPerSecondLabel, threadPriorityLabel, manualLabel, totalTurnsLabel;
	private JTextField manualField;
	public JComboBox globalStatBox;
	public JComboBox cellStatBox;
	private JPanel canvas;
	private boolean worldInitialized = false;
	private boolean manual = false;
	private int worldSize;
	
	public StatScreen[] statScreen = new StatScreen[1000];
	public int statScreenIndex = 0;
	
	public Engine engine;
	public World w;
	
	Dimension dim;
    String globalChoices[] = {
			"Global graphs:",
			"AgentCount per Habitat", "AgentCount Total",
			"Deaths per Habitat", "Deaths Total",
			"Births per Habitat", "Births Total",
			"PreyCount per Habitat", "PreyCount Total" ,
			"Prey NAR per Habitat", "Prey NAR Total" ,
			"CerealCount per Habitat", "CerealCount Total",
			"Cereal NAR per Habitat", "Cereal NAR Total",
			"FarmCount per Habitat", "FarmCount Total",
			"Farm NAR per Habitat", "Farm NAR Total",
			"Migrated perHabitat", "Migrated Total",
			"TimeLeft perHabitat", "TimeLeft Total"};
    String cellChoices[] = {
			"Cell graphs:",
			"Individual density", "Death density" ,
			"Birth density", "Prey density",
			"Prey NAR", "Cereal density" ,
			"Cereal NAR", "Farm density" ,
			"Farm NAR" };
	
	public GUI() {
		//setSize(worldSize+5, worldSize+86);
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		worldSize = Math.min(dim.height-20, (dim.width)/2)+40;
		//worldSize = 700;
		setSize(worldSize+193, worldSize);
		setLocation(dim.width-(worldSize+193),20);
		setTitle("World screen");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (worldInitialized) {w.close();}
				System.exit(0);
			}
		});
		Container contentPane = getContentPane();
		
		canvas = new JPanel();
		contentPane.add(canvas, "Center");
		
		JPanel p = new JPanel();
		p.setSize(10,20);
		
		startButton = new JButton();
		startButton.setText("Pause/continue (spacebar)");
		startButton.setFocusable(false);
		startButton.addActionListener(this);
		
		maxSpeedButton = new JButton();
		maxSpeedButton.setText("Max Speed");
		maxSpeedButton.setFocusable(false);
		maxSpeedButton.addActionListener(this);
		maxSpeedButton.setEnabled(false);
		
		viewButton = new JButton();
		viewButton.setText("Island view");
		viewButton.setFocusable(false);
		viewButton.addActionListener(this);
		viewButton.setEnabled(false);
		
		turnsPerSecondSlider = new JSlider(0, 100, 5);
        turnsPerSecondSlider.setMinorTickSpacing(5);
        turnsPerSecondSlider.setMajorTickSpacing(20);
        turnsPerSecondSlider.setSnapToTicks(false);
        turnsPerSecondSlider.setPaintTicks(true);
        turnsPerSecondSlider.setPaintLabels(true);
        turnsPerSecondSlider.setFocusable(false);
        turnsPerSecondSlider.setEnabled(false);
        turnsPerSecondSlider.addChangeListener(this);
        turnsPerSecondLabel = new JLabel();
        turnsPerSecondLabel.setFocusable(false);
        turnsPerSecondLabel.setText("Turns per second: 5");
   
        threadPrioritySlider = new JSlider(1, 5, 2);
        threadPrioritySlider.setMinorTickSpacing(1);
        threadPrioritySlider.setMajorTickSpacing(1);
        threadPrioritySlider.setSnapToTicks(true);
        threadPrioritySlider.setPaintTicks(true);
        threadPrioritySlider.setPaintLabels(true);
        threadPrioritySlider.setFocusable(false);
        threadPrioritySlider.setEnabled(false);
        threadPrioritySlider.addChangeListener(this);
        threadPriorityLabel = new JLabel();
        threadPriorityLabel.setFocusable(false);
        threadPriorityLabel.setText("Thread priority: 2");
        
        manualField = new JTextField();
        manualField.setFocusable(true);
        manualField.setEnabled(false);
        manualField.setText("1");
        
        manualLabel = new JLabel();
        manualLabel.setFocusable(false);
        manualLabel.setText("Run turns (press enter):");
        
        totalTurnsLabel = new JLabel();
        totalTurnsLabel.setFocusable(false);
        totalTurnsLabel.setText("Total turns: 0");
        
        globalStatBox = new JComboBox();
        globalStatBox.setFocusable(false);
        for (int i=0;i<globalChoices.length;i++) {
        	globalStatBox.addItem (globalChoices[i]);
		}
        globalStatBox.addActionListener(this);
        globalStatBox.setEnabled(false);
        
        cellStatBox = new JComboBox();
        cellStatBox.setFocusable(false);
        for (int i=0;i<cellChoices.length;i++) {
        	cellStatBox.addItem (cellChoices[i]);
		}
        cellStatBox.addActionListener(this);
        cellStatBox.setEnabled(false);

        //statBox.add addChangeListener(this);
        
        Component [] rij0 = {startButton,maxSpeedButton,turnsPerSecondLabel,turnsPerSecondSlider,threadPriorityLabel,threadPrioritySlider,manualLabel,manualField,totalTurnsLabel,globalStatBox, cellStatBox, viewButton};
		CompRow balk0 = new CompRow (rij0,0,1);
		
		p.add(balk0, "West");
		contentPane.add(p,"West");
		
		manualField.addKeyListener(this);
        addKeyListener(this);
        addMouseListener(this);
        this.setResizable(false);
	}
	
	public void startWorld() {
		turnsPerSecondSlider.setEnabled(true);
		threadPrioritySlider.setEnabled(true);
		maxSpeedButton.setEnabled(true);
		manualField.setEnabled(true);
		viewButton.setEnabled(true);
		globalStatBox.setEnabled(true);
		worldInitialized = true;
		engine = new Engine(canvas, this);
		engine.start();
		w = engine.world;
		// default stats
		//addStatChoice(1);addStatChoice(3);addStatChoice(5);
	}

	public void actionPerformed(ActionEvent e) {
		Object sourceObject = e.getSource();
    	if (sourceObject == startButton) { 
    		/*if (!worldInitialized) {
    			startWorld();
    			startButton.setText("Pause/continue (spacebar)");
    		}
    		else {
    			pause();
    		}*/
    		pause();
    	}
    	else if (sourceObject == maxSpeedButton) { 
    		if (engine.turnsPerSecond==-1 || engine.pause) {pause();}
    		engine.turnsPerSecond=-1;
    	}
    	else if (sourceObject == globalStatBox) { 
    		addGlobalStatChoice(globalStatBox.getSelectedIndex());
    		globalStatBox.setSelectedIndex(0);
    	}
    	else if (sourceObject == cellStatBox) { 
    		addCellStatChoice(cellStatBox.getSelectedIndex());
    		cellStatBox.setSelectedIndex(0);
    	}
    	else if (sourceObject == viewButton) { 
    		engine.changeView();
    	}
	}
	
	
	
    public void stateChanged(ChangeEvent e) {
    	Object sourceObject = e.getSource();
    	if (sourceObject == turnsPerSecondSlider) {
    		engine.turnsPerSecond = Math.max(1,turnsPerSecondSlider.getValue());   
    		turnsPerSecondLabel.setText("Turns per second: " + engine.turnsPerSecond);
    	}  
    	else if (sourceObject == threadPrioritySlider) {
    		engine.modelPriority = Math.max(1,threadPrioritySlider.getValue());
    		 threadPriorityLabel.setText("Thread priority: " + engine.modelPriority);
    		engine.threadChanged = true;
    	}
    }
    
    public void goDraw() {
    	totalTurnsLabel.setText("Total turns: " + engine.world.turns);
    	for (int i = 0; i < statScreenIndex; i++) {
    		statScreen[i].draw();
    	}
    }
    
    public void updateStats() {
    	for (int i = 0; i < statScreenIndex; i++) {
    		statScreen[i].updateStat();
    	}	
    }
    
    public void addGlobalStatChoice(int choice) {
		switch (choice) {
			case 1: multipleStatScreens(globalChoices[choice], true, true, "getIndividualDensity"); break;
			case 2: addStatScreen(globalChoices[choice], true, true, "getIndividualDensity", null, w.all); break;
			case 3: multipleStatScreens(globalChoices[choice], true, true, "getDeathDensity");break;
			case 4: addStatScreen(globalChoices[choice], true, true, "getDeathDensity", null, w.all); break;
			case 5: multipleStatScreens(globalChoices[choice], true, true, "getBirthDensity");break;
			case 6: addStatScreen(globalChoices[choice], true, true, "getBirthDensity", null, w.all); break;
			case 7: multipleStatScreens(globalChoices[choice], true, true, "getPreyDensity");break;
			case 8: addStatScreen(globalChoices[choice], true, true, "getPreyDensity", null, w.all);break;
			case 9: multipleStatScreens(globalChoices[choice], true, true, "getPreyNar");break;
			case 10: addStatScreen(globalChoices[choice], true, true, "getPreyNar", null, w.all);break;
			case 11: multipleStatScreens(globalChoices[choice], true, true, "getCerealDensity");break;
			case 12: addStatScreen(globalChoices[choice], true, true, "getCerealDensity", null, w.all);break;
			case 13: multipleStatScreens(globalChoices[choice], true, true, "getCerealNar");break;
			case 14: addStatScreen(globalChoices[choice], true, true, "getCerealNar", null, w.all);break;
			case 15: multipleStatScreens(globalChoices[choice], true, true, "getfarmDensity");break;
			case 16: addStatScreen(globalChoices[choice], true, true, "getfarmDensity", null, w.all);break;
			case 17: multipleStatScreens(globalChoices[choice], true, true, "getFarmNar");break;
			case 18: addStatScreen(globalChoices[choice], true, true, "getFarmNar", null, w.all);break;
			case 19: multipleStatScreens(globalChoices[choice], true, true, "getMigrated");break;
			case 20: addStatScreen(globalChoices[choice], true, true, "getMigrated", null, w.all);break;
			case 21: multipleStatScreens(globalChoices[choice], false, true, "getTimeLeft");break;
			case 22: addStatScreen(globalChoices[choice], false, true, "getTimeLeft", null, w.all);break;
		}
    }
    
    public void addCellStatChoice(int choice) {
    	if (!engine.cellSelected) {
    		return;
    	}
    	Cell [] cellSelected = w.cell[engine.selectedCellX][engine.selectedCellY].habitat.getCells();//{w.cell[engine.selectedCellX][engine.selectedCellY]};
    	String cellText = " for cell at location (" + engine.selectedCellX + "," + engine.selectedCellY + ")";
		switch (choice) {
			case 1: addStatScreen(cellChoices[choice] + cellText, true, true, "getIndividualDensity", null, cellSelected); break;
			case 2: addStatScreen(cellChoices[choice] + cellText, true, true, "getDeathDensity", null, cellSelected); break;
			case 3: addStatScreen(cellChoices[choice] + cellText, true, true, "getBirthDensity", null, cellSelected); break;
			case 4: addStatScreen(cellChoices[choice] + cellText, true, true, "getPreyDensity", null, cellSelected);break;
			case 5: addStatScreen(cellChoices[choice] + cellText, true, true, "getPreyNar", null, cellSelected);break;
			case 6: addStatScreen(cellChoices[choice] + cellText, true, true, "getCerealDensity", null, cellSelected);break;
			case 7: addStatScreen(cellChoices[choice] + cellText, true, true, "getCerealNar", null, cellSelected);break;
			case 8: addStatScreen(cellChoices[choice] + cellText, true, true, "getfarmDensity", null, cellSelected);break;
			case 9: addStatScreen(cellChoices[choice] + cellText, true, true, "getFarmNar", null, cellSelected);break;
		}
		return;
    }
    
    public void multipleStatScreens(String windowName, boolean cell, boolean method, String variableName) {
    	StatScreen motherScreen = addStatScreen(windowName, cell, method, variableName, null, w.ecology[0]);
    	for (int i = 1; i < w.ecologyIndex; i++) {
    		addStatScreen("", cell, method, variableName, motherScreen, w.ecology[i]);
    	}
    }
    
    public StatScreen addStatScreen(String windowName, boolean cell, boolean method, String variableName, StatScreen mother, Habitat habitat) {
    	return addStatScreen(windowName, cell, method, variableName, mother, habitat.getCells());
    }
    
    public StatScreen addStatScreen(String windowName, boolean cell, boolean method, String variableName, StatScreen mother, Cell [] area) {
    	statScreen[statScreenIndex] = new StatScreen(this, cell, method, variableName, mother, area);
    	statScreen[statScreenIndex].index = statScreen[0].index;
    	/*int screensPerColumn = (int)((double)(dim.height-25)/(double)statScreen[statScreenIndex].ySize);
    	if (statScreenIndex < screensPerColumn) {
    		statScreen[statScreenIndex].setLocation(0, 25+statScreenIndex*(statScreen[statScreenIndex].ySize));
    	}
    	else if (statScreenIndex < 2* screensPerColumn) {
    		statScreen[statScreenIndex].setLocation(dim.width/2, 25+(statScreenIndex-screensPerColumn)*(statScreen[statScreenIndex].ySize));
    	}
    	else {
    		statScreen[statScreenIndex].setLocation(0, 0);
    	}*/
    	if (statScreenIndex>0) {
    		statScreen[statScreenIndex].setLocation(statScreen[statScreenIndex-1].getNextCanvasLocation());
    	}
    	statScreen[statScreenIndex].setTitle(windowName);
    	statScreen[statScreenIndex].updateStat();
    	//statScreen[statScreenIndex].setVisible(true);
    	statScreenIndex++;
    	return statScreen[statScreenIndex-1];
    }
    
    public void removeStatScreen(StatScreen stat) {
    	int location = -1;
    	for (int i = 0; i < statScreenIndex; i++) {
    		if (stat.equals(statScreen[i])) {
    			location = i; break;
    		}
    	}
    	if (location != -1) {
	    	statScreen[location] = null;
	    	for (int i = location; i < statScreenIndex - 1; i++) {
	    		statScreen[i] = statScreen[i+1];
	    	}
	    	statScreenIndex--;
	    	//System.out.println("Statscreen removed " + statScreenIndex);
    	}
    	else {
    		System.out.println("I'm not in here as a statscreen");
    	}
    }
    
    private int stringToInt(String input) {
    	int output;
    	try {
    		output = Integer.parseInt(input);
    	}
    	catch(Exception e) {
    		output = 0;
    	}
    	return output;
    }
    
    private String removeNonNumbers(String input) {
    	String output = "";
    	for (int i = 0; i < input.length(); i++) {
    		if ("0123456789".indexOf(input.charAt(i))!=-1) {
    			output=output+input.charAt(i);
    		}
    	}
    	return output;
    }
    
    private void doManual() {
    	int turns = stringToInt(removeNonNumbers(manualField.getText()));
    	manual = true;
    	manualField.setEnabled(false);
    	startButton.setEnabled(false);
		turnsPerSecondSlider.setEnabled(false);
		threadPrioritySlider.setEnabled(false);
		maxSpeedButton.setEnabled(false);
		engine.framesPerSecond = 1;
		engine.turnsPerSecond = -1;
    	engine.manual = turns;
    	engine.pause = false;
    }
    
    public void manualDone() {
    	engine.framesPerSecond = 10;
    	engine.turnsPerSecond = Math.max(1,turnsPerSecondSlider.getValue());
    	manualField.setEnabled(true);
    	startButton.setEnabled(true);
		turnsPerSecondSlider.setEnabled(true);
		threadPrioritySlider.setEnabled(true);
		maxSpeedButton.setEnabled(true);
    	manual = false;
    }
    
    private void pause() {
    	if (worldInitialized && !manual) {
    		engine.pause();
    	}
    }

	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			manualField.setText(removeNonNumbers(manualField.getText()));
			manualField.setCaretPosition(0);
			pause();		
		}	
		if (worldInitialized && engine.pause && (e.getKeyChar() == '\r' || e.getKeyChar() == '\n')) {
			doManual();
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		if (worldInitialized && engine.pause) {
			if (engine.setSelected(arg0.getX()-3,arg0.getY()-29)) {
				cellStatBox.setEnabled(true);
				//System.out.println(""+w.cell[engine.selectedCellX][engine.selectedCellY].foodSource[0].calculateNAR());
			}
			else {
				cellStatBox.setEnabled(false);
			}
		}
	}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}
