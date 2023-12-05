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


package world;

import tools.FileHandler;
import tools.FileToStringArray;
import tools.MyRandom;

public class World {

	public MyRandom r;
	public FileHandler fh;
	
	private int maxBands = 999;
	public Band[] band = new Band[maxBands];
	public int bandIndex = 0;
	private int newBands = 0;
	public int sizeX, sizeY;
	public Cell[][] cell;
	Parameters p;
	
	public int turns = 0;
	
	// areas of interest
	public Habitat all;
	public Habitat[] land = new Habitat[60];
	public Habitat[] ecology = new Habitat[10];
	public int landIndex = 0;
	public int ecologyIndex = 0;
	
	
	public World(Parameters p, int run) {
		this.p = p;
		sizeX = p.worldSizeX;
		sizeY = p.worldSizeY;
		r = new MyRandom();
		fh = new FileHandler(run);
	}
	
	public boolean initialize() {
		cell = new Cell[sizeX][sizeY];
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				cell[i][j] = new Cell(i,j,this);
			}
		}
		/*for (int i = 0; i < 5; i++) {
			addNewBand();
		}*/
		// define habitats
		all = new Habitat(-1,"all");
		/*
		habitat[0] = new Habitat("lush",8 * p.tileSize, 600 * p.tileSize, 1);
		habitat[1] = new Habitat("medium",5.4 * p.tileSize, 402 * p.tileSize, 0.67);
		habitat[2] = new Habitat("desert",2.6 * p.tileSize, 198 * p.tileSize, 0.33);
		// Three vertical habitats
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ocean.addCell(cell[i][j]);
				if (i < size/3) 			{	habitat[0].addCell(cell[i][j]);	}
				else if (i < (size/3)*2) 	{	habitat[1].addCell(cell[i][j]);	}
				else						{	habitat[2].addCell(cell[i][j]);	}
			}
		}*/
		// islands
		//FileToStringArray ftsa =  new FileToStringArray();
		//String [][] islands = ftsa.get("islands.csv", 5);
		//int tileIndex = 0;
		//String currentHabGroup = "Group";//islands[0][2];
		ecologyIndex = 1;
		land[0] = new Habitat(this,0,ecologyIndex-1,"land1",8,600,1);
		land[1] = new Habitat(this,0,ecologyIndex-1,"land2",5.4,402,0.67);
		land[2] = new Habitat(this,0,ecologyIndex-1,"land3",2.6,198,0.33);
		ecology[0] = new Habitat(0, "lush");
		ecology[1] = new Habitat(0, "medium");
		ecology[2] = new Habitat(0, "desert");
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (i < sizeX/3) {
					land[0].addCell(cell[i][j]);
				}
				else if (i < 2*(sizeX/3)) {
					land[1].addCell(cell[i][j]);
				}
				else {
					land[2].addCell(cell[i][j]);
				}
			}
		}
		land[0].size = 441*100;ecology[0].addCell(land[0].getCells());land[0].setEngineCoordinates(0, 0);
		land[1].size = 441*100;ecology[1].addCell(land[1].getCells());land[1].setEngineCoordinates(20, 20);
		land[2].size = 441*100;ecology[2].addCell(land[2].getCells());land[2].setEngineCoordinates(40, 40);
		landIndex=3;ecologyIndex=3;
		all.addCell(land[0].getCells());
		all.addCell(land[1].getCells());
		all.addCell(land[2].getCells());

			
		System.out.println("Prepared " + landIndex + " new lands");
		for (int i = 0; i < 5; i++) {
			addNewBand(cell[r.nextInt(sizeX)][r.nextInt(sizeY)]);
		}
		return true;
		
		/*for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ocean.addCell(cell[i][j]);
			}
		}
		for (int i = 0; i < 10; i++) {
			habitat[i] = new Habitat(this,i,5.4, 402, 0.67);
		}
		int [] islandX1 = {2,	3,	4,	4,	3,	2, 2,	1, 1,	0, 0};
		int [] islandY1 = {0,	0,	1,	2,	3,	3, 4,	3, 4,	3, 4};
		for (int i = 0; i < islandX1.length; i ++) {habitat[0].addCell(cell[islandX1[i]][islandY1[i]]);}
		int [] islandX2 = {6,	7,	7, 8,	8, 9};
		int [] islandY2 = {5,	6,	7, 6,	7, 7};
		for (int i = 0; i < islandX2.length; i ++) {habitat[1].addCell(cell[islandX2[i]][islandY2[i]]);}
		int [] islandX3 = {11,	11};
		int [] islandY3 = {11,	12};
		for (int i = 0; i < islandX3.length; i ++) {habitat[2].addCell(cell[islandX3[i]][islandY3[i]]);}
		int [] islandX4 = {11,	11,	11};
		int [] islandY4 = {14,	15,	17};
		for (int i = 0; i < islandX4.length; i ++) {habitat[3].addCell(cell[islandX4[i]][islandY4[i]]);}
		int [] islandX5 = {9,	10,	11};
		int [] islandY5 = {19,	20,	20};
		for (int i = 0; i < islandX5.length; i ++) {habitat[4].addCell(cell[islandX5[i]][islandY5[i]]);}
		int [] islandX6 = {15,	16,	16};
		int [] islandY6 = {14,	13,	15};
		for (int i = 0; i < islandX6.length; i ++) {habitat[5].addCell(cell[islandX6[i]][islandY6[i]]);}
		int [] islandX7 = {18,	18};
		int [] islandY7 = {17,	18};
		for (int i = 0; i < islandX7.length; i ++) {habitat[6].addCell(cell[islandX7[i]][islandY7[i]]);}
		int [] islandX8 = {19};
		int [] islandY8 = {11};
		for (int i = 0; i < islandX8.length; i ++) {habitat[7].addCell(cell[islandX8[i]][islandY8[i]]);}
		int [] islandX9 = {0};
		int [] islandY9 = {0};
		for (int i = 0; i < islandX9.length; i ++) {habitat[8].addCell(cell[islandX9[i]][islandY9[i]]);}
		int [] islandX10 = {0,	0,	1,	2};
		int [] islandY10 = {6,	7,	7,	7};
		for (int i = 0; i < islandX10.length; i ++) {habitat[9].addCell(cell[islandX10[i]][islandY10[i]]);}
		for (int i = 0; i < 10; i++) {
			all.addCell(habitat[i].getCells());
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.println(i + ">" + j + " = " + habitat[i].distanceFactorToHabitat(habitat[j]));
			}
		}
		
		addNewBand(cell[0][6]);
		return true;
		*/
	}
	
	public void turn() {
		// deaths;
		removeDeadBands();
		// first, all cells get to update
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				cell[i][j].turn();
			}
		}
		int [] randomBandIndex = r.getRandomIndexOrder(bandIndex);
		for (int i = 0; i < bandIndex; i++) {
			//band[i].turn();
			band[randomBandIndex[i]].turn();
		}
		bandIndex+=newBands;
		newBands = 0;
		for (int i = 0; i < landIndex; i++) {
			land[i].turn();
		}
		statFileOutput();
		turns++;
	}
	

	
	public void addNewBand() {
		Cell randomCell = cell[r.nextInt(sizeX)][r.nextInt(sizeY)];
		addNewBand(randomCell);
	}
	
	public void addNewBand(Cell location) {
		if (bandIndex+newBands >= band.length) {
			Band [] oldBand = band;
			band = new Band[band.length+100];
			for (int i = 0; i < oldBand.length; i++) {
				band[i] = oldBand[i];
			}
			//System.out.println("increasedmemory: " + band.length);
		}
		band[bandIndex+newBands] = new Band(this,bandIndex+newBands,location);
		newBands++;
		//bandIndex++; // do not increase bandIndex yet, this band only gets to act next turn
	}
	
	public void removeDeadBands() {
		for(int i = 0; i < bandIndex; i++) {
			if (band[i].individuals <= 0) {
				band[i].removeMeFromOldCell();
				removeBand(i);
				i--;
				bandIndex--;
			}
		}
	}
	
	public void removeBand(int index) {
		for(int i = index; i < bandIndex - 1; i++) {
			band[i] = band[i+1];
		}
	}
	
	public Cell [] getAllCells(Cell centre, boolean include) {
		int xMin, xMax, yMin, yMax;

		xMin = Math.max(0,centre.x - p.sightRadius);
		xMax = Math.min(sizeX,centre.x + p.sightRadius + 1);
		yMin = Math.max(0,centre.y - p.sightRadius);
		yMax = Math.min(sizeY,centre.y + p.sightRadius + 1);
		
		Cell [] output = new Cell[(xMax-xMin)*(yMax-yMin)-(include?0:1)];
		int index = 0;
		int [] randomIndexOrder = r.getRandomIndexOrder(output.length);
		for (int i = xMin; i < xMax; i++) {
			for (int j = yMin; j < yMax; j++) {
				if (include || cell[i][j]!=centre) {
					output[randomIndexOrder[index]] = cell[i][j];
					index++;
				}
			}
		}
		return output;
	}
	
	public void close() {
		fh.out("header",""+r.seed1+","+r.seed2+","+r.doubles+","+r.gaussians+"\n");//+p.parametersToString());	
		fh.close();
		//fh.out("header",""+random.seed1+","+random.seed2+","+random.doubles+","+random.gaussians+"\n"+p.parametersToString());	
		//fh.close();
		//System.exit(0);
	}

	public void statFileOutput() {
		Habitat [] habitatMatrix = this.ecology;
		int maxIndex = ecologyIndex;
		double [] AllCellsIndividualPerHabitatPerKM2 = new double[maxIndex];
		double [] AllCellsIndividualFinalPerHabitatPerKM2 = new double[maxIndex];
		double [][] AllCellsFoodPerTypePerHabitatPerKM2 = new double[3][maxIndex];
		double [][] AllCellsGatherTimePerTypePerHabitatPerIndividual = new double[3][maxIndex];
		double [] AllCellsAvailableTimePerHabitatPerIndividual = new double[maxIndex];
		double [][] AllCellsEnergyPerTypePerHabitatPerIndividual = new double[3][maxIndex];
		double [] InhCellsIndividualPerHabitatPerKM2 = new double[maxIndex];
		double [] InhCellsIndividualFinalPerHabitatPerKM2 = new double[maxIndex];
		double [][] InhCellsFoodPerTypePerHabitatPerKM2 = new double[3][maxIndex];
		double [][] InhCellsGatherTimePerTypePerHabitatPerIndividual = new double[3][maxIndex];
		double [] InhCellsAvailableTimePerHabitatPerIndividual = new double[maxIndex];
		double [][] InhCellsEnergyPerTypePerHabitatPerIndividual = new double[3][maxIndex];
		int [] inhabitedCellsPerHabitat = new int[maxIndex];
		// info gathering
		for (int h = 0; h < maxIndex; h++) {// go through each habitat h
			for (int c = 0; c < habitatMatrix[h].cellIndex; c++) { // go through each cell c
				// inhabited part
				if (habitatMatrix[h].cells[c].index > 0) { // cell is inhabited
					inhabitedCellsPerHabitat[h]++;
					for (int b = 0; b < habitatMatrix[h].cells[c].index; b++) { // go through each band b	
						InhCellsIndividualPerHabitatPerKM2[h]+=habitatMatrix[h].cells[c].band[b].individualsAtStart;
						InhCellsIndividualFinalPerHabitatPerKM2[h]+=habitatMatrix[h].cells[c].band[b].individuals;
						InhCellsGatherTimePerTypePerHabitatPerIndividual[0][h]+=habitatMatrix[h].cells[c].band[b].timeHunted;
						InhCellsGatherTimePerTypePerHabitatPerIndividual[1][h]+=habitatMatrix[h].cells[c].band[b].timeGathered;
						InhCellsGatherTimePerTypePerHabitatPerIndividual[2][h]+=habitatMatrix[h].cells[c].band[b].timeFarmed;
						InhCellsAvailableTimePerHabitatPerIndividual[h]+=Math.max(0,habitatMatrix[h].cells[c].band[b].timeLeft);
						InhCellsEnergyPerTypePerHabitatPerIndividual[0][h]+=habitatMatrix[h].cells[c].band[b].energyHunted;
						InhCellsEnergyPerTypePerHabitatPerIndividual[1][h]+=habitatMatrix[h].cells[c].band[b].energyGathered;
						InhCellsEnergyPerTypePerHabitatPerIndividual[2][h]+=habitatMatrix[h].cells[c].band[b].energyFarmed;
					}
					InhCellsFoodPerTypePerHabitatPerKM2[0][h]+=habitatMatrix[h].cells[c].foodSource[0].population;
					InhCellsFoodPerTypePerHabitatPerKM2[1][h]+=habitatMatrix[h].cells[c].foodSource[1].population;
					InhCellsFoodPerTypePerHabitatPerKM2[2][h]+=habitatMatrix[h].cells[c].foodSource[2].population;
				}
				// uninhabited part
				for (int b = 0; b < habitatMatrix[h].cells[c].index; b++) { // go through each band b	
					AllCellsIndividualPerHabitatPerKM2[h]+=habitatMatrix[h].cells[c].band[b].individualsAtStart;
					AllCellsIndividualFinalPerHabitatPerKM2[h]+=habitatMatrix[h].cells[c].band[b].individuals;
					AllCellsGatherTimePerTypePerHabitatPerIndividual[0][h]+=habitatMatrix[h].cells[c].band[b].timeHunted;
					AllCellsGatherTimePerTypePerHabitatPerIndividual[1][h]+=habitatMatrix[h].cells[c].band[b].timeGathered;
					AllCellsGatherTimePerTypePerHabitatPerIndividual[2][h]+=habitatMatrix[h].cells[c].band[b].timeFarmed;
					AllCellsAvailableTimePerHabitatPerIndividual[h]+=Math.max(0,habitatMatrix[h].cells[c].band[b].timeLeft);
					AllCellsEnergyPerTypePerHabitatPerIndividual[0][h]+=habitatMatrix[h].cells[c].band[b].energyHunted;
					AllCellsEnergyPerTypePerHabitatPerIndividual[1][h]+=habitatMatrix[h].cells[c].band[b].energyGathered;
					AllCellsEnergyPerTypePerHabitatPerIndividual[2][h]+=habitatMatrix[h].cells[c].band[b].energyFarmed;
				}
				AllCellsFoodPerTypePerHabitatPerKM2[0][h]+=habitatMatrix[h].cells[c].foodSource[0].population;
				AllCellsFoodPerTypePerHabitatPerKM2[1][h]+=habitatMatrix[h].cells[c].foodSource[1].population;
				AllCellsFoodPerTypePerHabitatPerKM2[2][h]+=habitatMatrix[h].cells[c].foodSource[2].population;
			}
		}
		// post processing
		for (int h = 0; h < maxIndex; h++) {// go through each habitat h
			// inhabited part
			for (int f = 0; f < 3; f++) { // go through each food type f
				if (InhCellsIndividualPerHabitatPerKM2[h]==0) {
					InhCellsGatherTimePerTypePerHabitatPerIndividual[f][h] = 0;
					InhCellsEnergyPerTypePerHabitatPerIndividual[f][h] = 0;
				}
				else {
					InhCellsGatherTimePerTypePerHabitatPerIndividual[f][h]/=InhCellsIndividualPerHabitatPerKM2[h];
					InhCellsEnergyPerTypePerHabitatPerIndividual[f][h]/=InhCellsIndividualPerHabitatPerKM2[h];
				}
				if (inhabitedCellsPerHabitat[h]==0) {
					InhCellsFoodPerTypePerHabitatPerKM2[f][h] = 0;
				}
				else {
					InhCellsFoodPerTypePerHabitatPerKM2[f][h]/=(double)(inhabitedCellsPerHabitat[h]*300);
				}
			}
			if (InhCellsIndividualPerHabitatPerKM2[h]==0) {
				InhCellsAvailableTimePerHabitatPerIndividual[h] = 0;
			}
			else {
				InhCellsAvailableTimePerHabitatPerIndividual[h]/=InhCellsIndividualPerHabitatPerKM2[h];
			}
			if (inhabitedCellsPerHabitat[h]==0) {
				InhCellsIndividualPerHabitatPerKM2[h] = 0;
			}
			else {
				InhCellsIndividualPerHabitatPerKM2[h]/=(double)(inhabitedCellsPerHabitat[h]*300);
				InhCellsIndividualFinalPerHabitatPerKM2[h]/=(double)(inhabitedCellsPerHabitat[h]*300);
				
			}
			// uninhabited part
			for (int f = 0; f < 3; f++) { // go through each food type f
				if (AllCellsIndividualPerHabitatPerKM2[h]==0) {
					AllCellsGatherTimePerTypePerHabitatPerIndividual[f][h] = 0;
					AllCellsEnergyPerTypePerHabitatPerIndividual[f][h] = 0;
				}
				else {
					AllCellsGatherTimePerTypePerHabitatPerIndividual[f][h]/=AllCellsIndividualPerHabitatPerKM2[h];
					AllCellsEnergyPerTypePerHabitatPerIndividual[f][h]/=AllCellsIndividualPerHabitatPerKM2[h];
				}
				AllCellsFoodPerTypePerHabitatPerKM2[f][h]/=(double)(habitatMatrix[h].cellIndex*300);
			}
			if (AllCellsIndividualPerHabitatPerKM2[h]==0) {
				AllCellsAvailableTimePerHabitatPerIndividual[h] = 0;
			}
			else {
				AllCellsAvailableTimePerHabitatPerIndividual[h]/=AllCellsIndividualPerHabitatPerKM2[h];
			}
			AllCellsIndividualPerHabitatPerKM2[h]/=(double)(habitatMatrix[h].cellIndex*300);
			AllCellsIndividualFinalPerHabitatPerKM2[h]/=(double)(habitatMatrix[h].cellIndex*300);
		}
		// output
		for (int h = 0; h < maxIndex; h++) { // go through each habitat h
			fh.out("AllCellsIndividualPerHabitatPerKM2", AllCellsIndividualPerHabitatPerKM2[h] + "\t");
			fh.out("AllCellsIndividualFinalPerHabitatPerKM2", AllCellsIndividualFinalPerHabitatPerKM2[h] + "\t");
			fh.out("AllCellsAvailableTimePerHabitatPerIndividual", AllCellsAvailableTimePerHabitatPerIndividual[h] + "\t");
			fh.out("InhCellsIndividualPerHabitatPerKM2", InhCellsIndividualPerHabitatPerKM2[h] + "\t");
			fh.out("InhCellsIndividualFinalPerHabitatPerKM2", InhCellsIndividualFinalPerHabitatPerKM2[h] + "\t");
			fh.out("InhCellsAvailableTimePerHabitatPerIndividual", InhCellsAvailableTimePerHabitatPerIndividual[h] + "\t");
			fh.out("AllCellsPreyPerHabitatPerKM2", AllCellsFoodPerTypePerHabitatPerKM2[0][h] + "\t");
			fh.out("AllCellsCerealPerHabitatPerKM2", AllCellsFoodPerTypePerHabitatPerKM2[1][h] + "\t");
			fh.out("AllCellsFarmlandPerHabitatPerKM2", AllCellsFoodPerTypePerHabitatPerKM2[2][h] + "\t");
			fh.out("AllCellsGatherTimeForPreyPerHabitatPerIndividual", AllCellsGatherTimePerTypePerHabitatPerIndividual[0][h] + "\t");
			fh.out("AllCellsGatherTimeForCerealPerHabitatPerIndividual", AllCellsGatherTimePerTypePerHabitatPerIndividual[1][h] + "\t");
			fh.out("AllCellsGatherTimeForFarmsPerHabitatPerIndividual", AllCellsGatherTimePerTypePerHabitatPerIndividual[2][h] + "\t");
			fh.out("AllCellsEnergyFromPreyPerHabitatPerIndividual", AllCellsEnergyPerTypePerHabitatPerIndividual[0][h] + "\t");
			fh.out("AllCellsEnergyFromCerealPerHabitatPerIndividual", AllCellsEnergyPerTypePerHabitatPerIndividual[1][h] + "\t");
			fh.out("AllCellsEnergyFromFarmsPerHabitatPerIndividual", AllCellsEnergyPerTypePerHabitatPerIndividual[2][h] + "\t");
			fh.out("InhCellsPreyPerHabitatPerKM2", InhCellsFoodPerTypePerHabitatPerKM2[0][h] + "\t");
			fh.out("InhCellsCerealPerHabitatPerKM2", InhCellsFoodPerTypePerHabitatPerKM2[1][h] + "\t");
			fh.out("InhCellsFarmlandPerHabitatPerKM2", InhCellsFoodPerTypePerHabitatPerKM2[2][h] + "\t");
			fh.out("InhCellsGatherTimeForPreyPerHabitatPerIndividual", InhCellsGatherTimePerTypePerHabitatPerIndividual[0][h] + "\t");
			fh.out("InhCellsGatherTimeForCerealPerHabitatPerIndividual", InhCellsGatherTimePerTypePerHabitatPerIndividual[1][h] + "\t");
			fh.out("InhCellsGatherTimeForFarmsPerHabitatPerIndividual", InhCellsGatherTimePerTypePerHabitatPerIndividual[2][h] + "\t");
			fh.out("InhCellsEnergyFromPreyPerHabitatPerIndividual", InhCellsEnergyPerTypePerHabitatPerIndividual[0][h] + "\t");
			fh.out("InhCellsEnergyFromCerealPerHabitatPerIndividual", InhCellsEnergyPerTypePerHabitatPerIndividual[1][h] + "\t");
			fh.out("InhCellsEnergyFromFarmsPerHabitatPerIndividual", InhCellsEnergyPerTypePerHabitatPerIndividual[2][h] + "\t");
			fh.out("inhabitedCellsPerHabitat", inhabitedCellsPerHabitat[h] + "\t");
		}
		fh.outln("AllCellsIndividualPerHabitatPerKM2","");
		fh.outln("AllCellsIndividualFinalPerHabitatPerKM2","");
		fh.outln("AllCellsAvailableTimePerHabitatPerIndividual","");
		fh.outln("InhCellsIndividualPerHabitatPerKM2","");
		fh.outln("InhCellsIndividualFinalPerHabitatPerKM2","");
		fh.outln("InhCellsAvailableTimePerHabitatPerIndividual","");
		fh.outln("AllCellsPreyPerHabitatPerKM2", "");
		fh.outln("AllCellsCerealPerHabitatPerKM2", "");
		fh.outln("AllCellsFarmlandPerHabitatPerKM2", "");
		fh.outln("AllCellsGatherTimeForPreyPerHabitatPerIndividual", "");
		fh.outln("AllCellsGatherTimeForCerealPerHabitatPerIndividual", "");
		fh.outln("AllCellsGatherTimeForFarmsPerHabitatPerIndividual", "");
		fh.outln("AllCellsEnergyFromPreyPerHabitatPerIndividual", "");
		fh.outln("AllCellsEnergyFromCerealPerHabitatPerIndividual", "");
		fh.outln("AllCellsEnergyFromFarmsPerHabitatPerIndividual", "");
		fh.outln("InhCellsPreyPerHabitatPerKM2", "");
		fh.outln("InhCellsCerealPerHabitatPerKM2", "");
		fh.outln("InhCellsFarmlandPerHabitatPerKM2", "");
		fh.outln("InhCellsGatherTimeForPreyPerHabitatPerIndividual", "");
		fh.outln("InhCellsGatherTimeForCerealPerHabitatPerIndividual", "");
		fh.outln("InhCellsGatherTimeForFarmsPerHabitatPerIndividual", "");
		fh.outln("InhCellsEnergyFromPreyPerHabitatPerIndividual", "");
		fh.outln("InhCellsEnergyFromCerealPerHabitatPerIndividual", "");
		fh.outln("InhCellsEnergyFromFarmsPerHabitatPerIndividual", "");
		fh.outln("inhabitedCellsPerHabitat", "");

	}
	
}
