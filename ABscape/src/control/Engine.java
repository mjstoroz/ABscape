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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;


import tools.FileHandler;
import tools.MyRandom;
import world.Habitat;
import world.Parameters;
import world.World;



public class Engine extends Thread{
	
	public int iteration;

	public MyRandom random = new MyRandom();

	//public FileHandler fh;
	public Parameters p;
	
	public int size; //  world is a square, xSize and ySize is equal
	public int nrOfCellsX, nrOfCellsY;
	public double sizePerCell;
	public int selectedCellX, selectedCellY;
	boolean cellSelected = false;
	
	Color [] habitatColor = {Color.GREEN, Color.BLUE, Color.CYAN, Color.GRAY, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.LIGHT_GRAY};
	
	Graphics g;
	Dimension d;
	private JPanel box;
	
	public int modelPriority = 1;//NORM_PRIORITY;
	public boolean threadChanged = false; // used to change thread-priority
	boolean keepRunning = true;
	public boolean pause = true;
	public int turnsPerSecond = 5;
	public int framesPerSecond = 10;
	public int manual;
	
	public World world;
	private GUI gui;
	
	public boolean islandView = false;
	public Habitat[][] islandGrid;
	public boolean firstIslandDraw = true;
	
	public double minLat = -21.5;
	public double minLon = -34;
	public double maxLat = -1.5;
	public double maxLon = -0.05;
	public double minUnit = 0.2;
	
	
	public Engine(JPanel b, GUI gui) {

		//fh = new FileHandler();
		this.gui = gui;
		iteration = 0;
		box = b;
		p = new Parameters();
		islandGrid = new Habitat[p.worldSizeX][p.worldSizeY];
		for (int i = 0; i < p.worldSizeX; i++) {
			for (int j = 0; j < p.worldSizeY; j++) {
				islandGrid[i][j] = null;
			}
		}
		g = box.getGraphics();
		d = box.getSize();
		size = Math.min(d.height, d.width);
		System.out.println("Ready");
		
		world = new World(p, 666);
		nrOfCellsX = world.sizeX;
		nrOfCellsY = world.sizeY;
		sizePerCell = Math.min((size-10)/nrOfCellsX,(size-10)/nrOfCellsY);
		//sizePerCell = (size-10)/Math.max((maxLon-minLon)/minUnit, (maxLat-minLat)/minUnit);
		//System.out.println("spc" + sizePerCell);
		world.initialize();
		
		g.setFont(new Font("Arial",0,d.height/70));
	}
	
	public void draw() {
		if (islandView) {
			drawIslandView();
		}
		else {
			drawGridView();
		}
	}
	
	public void drawGridView() {
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, size, size);
		int cellX, cellY, cellSize, barHeight;
		cellSize = (int)(sizePerCell);
		int previousID = 0;
		for (int j = 0; j < nrOfCellsY; j++) {
			for (int i = 0; i < nrOfCellsX; i++) {
				if (world.cell[i][j].active) {
					cellX = 7+(int)(i*sizePerCell+0.5);
					cellY = 7+(int)(j*sizePerCell+0.5);
					if (world.cell[i][j].habitat.ID != previousID) {
						g.setColor(Color.BLACK);
						g.fillRect(cellX-1,cellY+3, 3, cellSize-6);
						previousID = world.cell[i][j].habitat.ID;
					}
					if (world.cell[i][j].habitat.IDgroup == -1) {
						g.setColor(new Color(100,100,255));
						g.fillRect(cellX+3,cellY+3, cellSize-5, cellSize-5);
						g.setColor(Color.BLACK);
						g.drawRect(cellX+2,cellY+2, cellSize-4, cellSize-4);
					}
					else {
						g.setColor(Color.WHITE);
						g.fillRect(cellX+3,cellY+3, cellSize-5, cellSize-5);
						g.setColor(habitatColor[world.cell[i][j].habitat.IDgroup]);
						g.drawRect(cellX+2,cellY+2, cellSize-4, cellSize-4);
					}
					
					// food bar for prey
					g.setColor(new Color(255,0,0));
					barHeight = (int)((cellSize-5)*(world.cell[i][j].foodSource[0].getRelativeFood()));
					g.fillRect(cellX+3, cellY+(cellSize-2-barHeight), (cellSize-4)/2, barHeight);
					// food bar for cereal
					g.setColor(new Color(0,0,255));
					barHeight = (int)((cellSize-5)*(world.cell[i][j].foodSource[1].getRelativeFood()));
					g.fillRect(cellX+3+(cellSize-4)/2, cellY+(cellSize-2-barHeight), (cellSize-4)/3, barHeight);
					// food bar for farms
					g.setColor(new Color(0,255,0));
					barHeight = (int)((cellSize-5)*(world.cell[i][j].foodSource[2].getRelativeFood()));
					g.fillRect((int)(cellX+3+(cellSize-4)*(5/(double)6)), cellY+(cellSize-2-barHeight), ((cellSize-4)/6)-1, barHeight);
		
					if (world.cell[i][j].index != 0) {
						g.setColor(Color.BLACK);
						g.fillOval((int)(cellX+3+cellSize*0.1), (int)(cellY+3+cellSize*0.1), (int)((cellSize-6)*0.8), (int)((cellSize-6)*0.8));
						g.setColor(Color.GREEN);
						g.drawString("" + (int)world.cell[i][j].getTotalIndividuals(),(int)((i+0.5)*sizePerCell+0.5),(int)((j+0.8)*sizePerCell+0.5));
							
					}
				}
				
			}
		}
		if (cellSelected) {
			g.setColor(Color.GREEN);
			g.drawRect(6+(int)(selectedCellX*sizePerCell+0.5),6+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5+2), (int)(sizePerCell+0.5+2));
			g.drawRect(7+(int)(selectedCellX*sizePerCell+0.5),7+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5), (int)(sizePerCell+0.5));
			g.drawRect(8+(int)(selectedCellX*sizePerCell+0.5),8+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-2, (int)(sizePerCell+0.5)-2);
		}
		g.setColor(Color.BLACK);
		g.drawRect(1,1, size-2, size-2);
		gui.goDraw();
	}
	
	public void prepareIslandHabitats() {
		Habitat currentHab;
		int cellX, cellY;
		int cellSize = (int)(sizePerCell);
		for (int i = 0; i < world.landIndex; i++) {
			currentHab = world.land[i];
			cellX = (int)(((int)(((size-cellSize)/(maxLon-minLon))*(currentHab.lon-minLon)))/sizePerCell);
			cellY = (int)(((size-cellSize)-(int)(((size-cellSize)/(maxLat-minLat))*(currentHab.lat-minLat)))/sizePerCell);
			if (islandGrid[cellX][cellY]==null) {
				currentHab.islandX = cellX;
				currentHab.islandY = cellY;
				islandGrid[cellX][cellY] = currentHab;
			}
			else if (cellX < p.worldSizeX-1 && islandGrid[cellX+1][cellY]==null) {
				currentHab.islandX = cellX+1;
				currentHab.islandY = cellY;
				islandGrid[cellX+1][cellY] = currentHab;
			}
			else if (cellX > 0 && islandGrid[cellX-1][cellY]==null) {
				currentHab.islandX = cellX-1;
				currentHab.islandY = cellY;
				islandGrid[cellX-1][cellY] = currentHab;
			}
			else if (cellY < p.worldSizeY-1 && islandGrid[cellX][cellY+1]==null) {
				currentHab.islandX = cellX;
				currentHab.islandY = cellY+1;
				islandGrid[cellX][cellY+1] = currentHab;
			}
			else if (cellY > 0 && islandGrid[cellX][cellY-1]==null) {
				currentHab.islandX = cellX;
				currentHab.islandY = cellY-1;
				islandGrid[cellX][cellY-1] = currentHab;
			}
			else {
				System.out.println("no room");
			}
			
		}
	}
	
	public void drawIslandView() {
		if (firstIslandDraw) {
			firstIslandDraw=false;
			prepareIslandHabitats();
		}
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, size, size);
		int cellX, cellY, cellSize, barHeight;
		cellSize = (int)(sizePerCell);
		int previousID = 0;
		Habitat currentHab;
		for (int j = 0; j < world.landIndex; j++) {
			currentHab = world.land[j];
			//cellX = (int)round(((int)(((size-cellSize)/(maxLon-minLon))*(currentHab.lon-minLon))),cellSize);//7+(int)(i*sizePerCell+0.5);
			//cellY = (int)round(((size-cellSize)-(int)(((size-cellSize)/(maxLat-minLat))*(currentHab.lat-minLat))),cellSize);//7+(int)(j*sizePerCell+0.5);
			
			
			cellX = 7+(int)(currentHab.islandX*sizePerCell+0.5);
			cellY = 7+(int)(currentHab.islandY*sizePerCell+0.5);
			
			
			g.setColor(Color.WHITE);
			g.fillRect(cellX+3,cellY+3, cellSize-5, cellSize-5);
			g.setColor(habitatColor[currentHab.habGroup.ID]);
			g.drawRect(cellX+2,cellY+2, cellSize-4, cellSize-4);
			

			// food bar for prey
			g.setColor(new Color(255,0,0));
			barHeight = (int)((cellSize-5)*(currentHab.getFoodDensity(0)));
			g.fillRect(cellX+3, cellY+(cellSize-2-barHeight), (cellSize-4)/2, barHeight);
			// food bar for cereal
			g.setColor(new Color(0,0,255));
			barHeight = (int)((cellSize-5)*(currentHab.getFoodDensity(1)));
			g.fillRect(cellX+3+(cellSize-4)/2, cellY+(cellSize-2-barHeight), (cellSize-4)/3, barHeight);
			// food bar for farms
			g.setColor(new Color(0,255,0));
			barHeight = (int)((cellSize-5)*(currentHab.getFoodDensity(2)));
			g.fillRect((int)(cellX+3+(cellSize-4)*(5/(double)6)), cellY+(cellSize-2-barHeight), ((cellSize-4)/6)-1, barHeight);

			g.setColor(Color.BLACK);
			g.fillOval((int)(cellX+3+cellSize*0.1), (int)(cellY+3+cellSize*0.1), (int)((cellSize-6)*0.8), (int)((cellSize-6)*0.8));
			g.setColor(Color.GREEN);
			g.drawString("" + (int)currentHab.getPopulation(),(int)((currentHab.islandX+0.5)*sizePerCell+0.5),(int)((currentHab.islandY+0.8)*sizePerCell+0.5));


		}
		if (cellSelected) {
			g.setColor(Color.GREEN);
			g.drawRect(6+(int)(selectedCellX*sizePerCell+0.5),6+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5+2), (int)(sizePerCell+0.5+2));
			g.drawRect(7+(int)(selectedCellX*sizePerCell+0.5),7+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5), (int)(sizePerCell+0.5));
			g.drawRect(8+(int)(selectedCellX*sizePerCell+0.5),8+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-2, (int)(sizePerCell+0.5)-2);
		}
		g.setColor(Color.BLACK);
		g.drawRect(1,1, size-2, size-2);
		gui.goDraw();
	}
	
	
	public void visualize() {
		if (!box.isVisible())
			return;
		Graphics g = box.getGraphics();
		box.setBackground(Color.WHITE);
		g.dispose();
	}


	public void turn() {
		world.turn();
		gui.updateStats();
	}

	public void nextRound() {
	}


	
	public void run() {	
		this.setPriority(modelPriority);
		double timeSpentDrawing = 0;
		double timeSpentRunning = 0;
		double totalTime = 1;
		double timeSpentPre = 0;
		double timeTotalPre = 0;

		while(keepRunning) {
			timeSpentDrawing = 0;
			timeSpentRunning = 0;
			totalTime = 1;
			long timeNow = System.currentTimeMillis();
			int turns = 0; int frames = 0;
			if (!pause && threadChanged) { // when unpaused, change thread priority once
				this.setPriority(modelPriority);
				threadChanged = false;
			}
			int turnsThisSecond = 0;
			while (!pause && timeNow + 1000 > System.currentTimeMillis()) {
				timeTotalPre = System.currentTimeMillis();
				if (turnsPerSecond==-1 || System.currentTimeMillis() > timeNow + ((turns*1000)/turnsPerSecond)) {
					timeSpentPre = System.currentTimeMillis();
					turn();
					timeSpentRunning+=System.currentTimeMillis()-timeSpentPre;
					turnsThisSecond++;
					turns++;
					if (manual > 0) {
						manual--;
						if (manual == 0) {
							pause = true;
							gui.manualDone();
							break;
						}
					}
				}
				if (System.currentTimeMillis() > timeNow + ((frames*1000)/framesPerSecond)) {
					timeSpentPre = System.currentTimeMillis();
					draw();
					visualize();
					timeSpentDrawing+=System.currentTimeMillis()-timeSpentPre;
					turnsThisSecond++;
					frames++;
				}
				totalTime+=System.currentTimeMillis()-timeTotalPre;
			}
			//System.out.println("" + turnsThisSecond);
			if (pause) { 
				if (!threadChanged) { // when paused, change thread priority once
					this.setPriority(MIN_PRIORITY);
					threadChanged = true;
				}
				try{draw();visualize();sleep(100);}catch(Exception e){}
			}
			//System.out.println("Percentage drawing: " + 100*timeSpentDrawing/(totalTime) + "%" + " , percentage running: " + 100*timeSpentRunning/(totalTime) + "%");
		}			
	}
	
	public void pause() {
		if (pause) {pause = false;}
		else {pause = true;}		
	}
	
	public boolean setSelected(int x, int y) {
		int cellX = (int)(((x-215)/sizePerCell));
		int cellY = (int)(((y+1)/sizePerCell));
		if (cellX >= 0 && cellX < nrOfCellsX && cellY >= 0 && cellY < nrOfCellsY) {		
			g.setColor(Color.WHITE);
			g.drawRect(6+(int)(selectedCellX*sizePerCell+0.5),6+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5+1), (int)(sizePerCell+0.5+1));
			g.drawRect(7+(int)(selectedCellX*sizePerCell+0.5),7+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-1, (int)(sizePerCell+0.5)-1);
			g.drawRect(8+(int)(selectedCellX*sizePerCell+0.5),8+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-3, (int)(sizePerCell+0.5)-3);
			selectedCellX = cellX; selectedCellY = cellY;
			if (!islandView) {
				System.out.println("" + world.cell[selectedCellX][selectedCellY].habitat.name + " " + world.cell[selectedCellX][selectedCellY].habitat.ID + " " +
					world.cell[selectedCellX][selectedCellY].habitat.habGroup.name + " " + world.cell[selectedCellX][selectedCellY].habitat.habGroup.ID);
			}
			else {
				System.out.println("" + islandGrid[selectedCellX][selectedCellY].name + " " + islandGrid[selectedCellX][selectedCellY].ID + " " +
						islandGrid[selectedCellX][selectedCellY].habGroup.name + " " + islandGrid[selectedCellX][selectedCellY].habGroup.ID + " " +
						islandGrid[selectedCellX][selectedCellY].size);

			}
			cellSelected = true;
		}
		else {
			g.setColor(Color.WHITE);
			g.drawRect(6+(int)(selectedCellX*sizePerCell+0.5),6+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5+1), (int)(sizePerCell+0.5+1));
			g.drawRect(7+(int)(selectedCellX*sizePerCell+0.5),7+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-1, (int)(sizePerCell+0.5)-1);
			g.drawRect(8+(int)(selectedCellX*sizePerCell+0.5),8+(int)(selectedCellY*sizePerCell+0.5), (int)(sizePerCell+0.5)-3, (int)(sizePerCell+0.5)-3);
			selectedCellX = -1; selectedCellY = -1;
			cellSelected = false;
		}
		return cellSelected;
	}

	public void changeView() {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size, size);
		if (islandView) {
			islandView = false;
		}
		else {
			islandView = true;
		}
		
	}
	
	
	public double round(double input, double rounding) {
		return rounding*(int)((input/rounding)+0.5);
	}
	
}
