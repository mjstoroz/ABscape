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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tools.FileHandler;
import world.*;



class StatScreen extends JFrame implements MouseListener, WindowListener, ComponentListener {
	
	private static final long serialVersionUID = 1L;
	
	public JPanel canvas;
	public GUI gui;
	public World world;
	FileHandler fh;
	
	private Graphics g;
	private int xSize;
	public int ySize;
	public int yBorder = 30;

	private double[] values;
	public int index = 0;
	private double yMax = 0;
	private double yMin = 0;
	
	Field variable;
	Method method;
	boolean isCell = false;
	boolean isMethod = false;
	public StatScreen [] kids = new StatScreen[10];
	int kidsIndex = 0;
	public boolean iAmAKid = false;
	public StatScreen mother;
	public Cell [] area;
	
	public String variableName;
	
	public StatScreen(GUI gui, boolean cell, boolean method, String variableName, StatScreen canvasMother, Cell [] area) {
		this.variableName = variableName;
		this.area = area;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		xSize = 1200;//((int)(dim.getWidth()/200))*100;
		ySize = (int)(dim.height/12);
		values = new double[xSize];
		this.isCell = cell;
		this.isMethod = method;
		this.gui = gui;
		this.world = gui.engine.world;
		setSize(xSize, ySize);
		addWindowListener(this);
		addComponentListener(this);
		addKeyListener(gui);
		Container contentPane = getContentPane();
	//	fh = gui.engine.fh;
		
		if (canvasMother!=null) {
			mother = canvasMother;
			canvas = mother.canvas;
			mother.addKid(this);
			iAmAKid = true;
		}
		else {
			canvas = new JPanel();
			contentPane.add(canvas, "Center");
	        addMouseListener(this);
	        this.setResizable(true);
	        canvas.setVisible(true);
	        this.setVisible(true);
	        this.setVisible(true);
		}
		
        setStaticVariable(cell, method, variableName);
		g = canvas.getGraphics();
	}
	
	public void addKid(StatScreen kid) {
		kids[kidsIndex] = kid;
		kidsIndex++;
	}
	
	private void setStaticVariable (boolean isCell, boolean isMethod, String variableName) {
        try {
        	if (!isMethod) {
        		if (!isCell) {
        			variable = Band.class.getField(variableName);	
        		}
        		else {
        			variable = Cell.class.getField(variableName);	
        		}
        	}
        	else {
        		if (!isCell) {
        			method = Band.class.getMethod(variableName);
        		}
        		else {
        			method = Cell.class.getMethod(variableName);
        		}
        	}
        }
        catch (Exception e) {
        	System.out.println("StatScreen init error: " + e.getLocalizedMessage());
        }
	}
	
	public void draw() {
		if (!iAmAKid) {
			drawGraph(gui.engine.habitatColor[0]);
			for (int i = 0; i < kidsIndex; i++) {
				kids[i].drawGraph(gui.engine.habitatColor[i+1]);
			}
		}
	}
	
	public void drawGraph(Color lineColor) {
		/*if (!this.isVisible()) {
			gui.removeStatScreen(this);
		}*/
		double yScale = (double)(ySize-(yBorder))/(yMax-yMin);
		if (!iAmAKid) { // only parent statScreen need to start with a blank screen
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, xSize, ySize);
			g.setColor(Color.BLACK);
			g.drawLine(index+1, 0, index+1, ySize);
			for (int i = 0; i < xSize; i+=100) {
				g.drawLine(i, (ySize-yBorder)-((int)((0-yMin)*yScale)), i, (ySize-yBorder)-((int)((0-yMin)*yScale))-3);
			}
			g.drawLine(0, (ySize-yBorder)-((int)((0-yMin)*yScale)), xSize, (ySize-yBorder)-((int)((0-yMin)*yScale)));
		}
		g.setColor(lineColor);
		for (int i = 1; i < values.length; i++) {
			if (i!=index) {
				g.drawLine(i-1, (ySize-yBorder)-((int)((values[i-1]-yMin)*yScale)), i, (ySize-yBorder)-((int)((values[i]-yMin)*yScale)));
				//System.out.println(""+ySize+ " " + ((int)(values[i-1]*yScale)));
			}
		}
		if (index > 1) {
			int textCap = Math.max(yBorder/2,(ySize-yBorder)-(int)((values[index-1]-yMin)*yScale));
			g.drawString(""+values[index-1],index+3,textCap);
		}
	}
	
	public void updateStat() {
		if (!isCell) {
		values[index] = getBandValue();
		}
		else {
			values[index] = getCellValue();
		}
		index++;
		if (index == values.length) {
			index = 0;
		}
		for (int i = 0; i < kidsIndex; i++) {
			if (kids[i].yMax > yMax) {
				yMax = kids[i].yMax;
			}
			if (kids[i].yMin < yMin) {
				yMin = kids[i].yMin;
			}
		}
		for (int i = 0; i < kidsIndex; i++) {
			kids[i].yMax = yMax;
			kids[i].yMin = yMin;
		}
	}
	
	public double getBandValue() {
		double output = 0;
		int individuals = 0;
		try {
			for (int a = 0; a < area.length; a++) {
				for (int i = 0; i < area[a].index; i++) {
					individuals+=area[a].getTotalIndividuals();
					if (isMethod) {
						//System.out.println(""+method.invoke(world.band[i], (Object)null));
						output+=(Double)(method.invoke(area[a].band[i]));
					}
					else {
						output+=variable.getInt(area[a].band[i]);
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("StatScreen band read error: " + e.getLocalizedMessage());
		}
		//System.out.println("ou: " + individuals + " " + output);
		if (individuals!=0) {
			output/=(double)(individuals*365);
			
		}
		else {
			output = 0;
		}
		//System.out.println("ou: " + output);
		if (output > yMax) {
			yMax = output;
		}
		if (output < yMin) {
			yMin = output;
		}
		return output;
	}
	
	public double getCellValue() {
		double output = 0;
		try {
			for (int i = 0; i < area.length; i++) {
				if (isMethod) {
					//System.out.println(""+method.invoke(world.cell[i][j], null));
					output+=(Double)(method.invoke(area[i]));
				}
				else {
					output+=variable.getInt(area[i]);
				}
			}
			//fh.outln(variableName, ""+output);
			output/=(double)(area.length);
		}
		catch (Exception e) {
			System.out.println("StatScreen cell read error: " + e.getLocalizedMessage());
		}
		if (output > yMax) {
			yMax = output;
		}
		if (output < yMin) {
			yMin = output;
		}
		return output;
	}
	
	public Point getNextCanvasLocation() {
		Point output = new Point();
		if (iAmAKid) {
			output = mother.getLocation();
			output.y+=mother.ySize;
		}
		else {
			output = getLocation();
			output.y+=ySize;
		}
		return output;
	}
	
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {
	}
	public void windowClosed(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {
		for (int i = 0; i < kidsIndex; i++) {
			gui.removeStatScreen(kids[i]);
		}
		gui.removeStatScreen(this);
		}
	public void windowDeactivated(WindowEvent arg0) {
	}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent arg0) {
		ySize = this.getHeight();
		setSize(xSize, ySize);
		canvas.setSize(xSize, ySize);
		g = canvas.getGraphics();
		for (int i = 0; i < kidsIndex; i++) {
			kids[i].ySize = ySize;
			kids[i].g = g;
		}
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
