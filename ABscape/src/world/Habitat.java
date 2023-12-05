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

public class Habitat {
	
	public Cell[] cells =  new Cell[0];
	Cell [] otherCells;
	public int cellIndex = 0;
	boolean featureDefiner;
	public String name;
	public int ID;
	public int IDgroup;
	public Habitat habGroup;
	
	public double size;
	
	public World world;
	
	public int bestMerged = -1;
	public boolean [] mergedNextTurn = new boolean[50];
	public double [] mergedNar = new double [50];
	
	public Habitat [] otherHabitats = new Habitat [50];
	public double [] distanceFactors = new double [50];
	public double [] chanceFactors = new double[50];
	public double totalChance;
	public int otherHabitatIndex = 0;
	
	double maxPrey;
	double maxCereal;
	double cerealGradient;
	
	public double lat;
	public double lon;
	public int islandX;
	public int islandY;
	
	public Habitat(int ID, String name) {
		this.ID = ID;
		this.name = name;
		featureDefiner = false;
		//this.world = world; //should not need this
	}
	
	public Habitat(World world, int ID, int IDgroup, String name, double maxPrey, double maxCereal, double cerealGradient) {
		this.IDgroup = IDgroup;
		this.ID = ID;
		this.world = world;
		this.name = name;
		featureDefiner = true;
		this.maxPrey = maxPrey;
		this.maxCereal = maxCereal;
		this.cerealGradient = cerealGradient;
	}
	
	public void turn() {
		bestMerged = -1;
		for (int i = 0; i < otherHabitatIndex; i++) {
			mergedNar[i] = 0;
			if (mergedNextTurn[i]) {
				mergedNar[i] = otherHabitats[i].getBestNar();
				if (bestMerged == -1 || mergedNar[i] > mergedNar[bestMerged]) {
					bestMerged = i;
				}
			}
			mergedNextTurn[i] = false;
		}
		
	}
	
	public void setEngineCoordinates (double lat, double lon) {
		// .2
		this.lat = lat;
		this.lon = lon;
		int xSquareSize = (int)(1+Math.sqrt(cellIndex));
		int tempIndexX = 0;
		int tempIndexY = 0;
		for (int i = 0; i < cellIndex; i++) {
			cells[i].lat = lat + tempIndexX * 0.2;
			cells[i].lon = lon + tempIndexY * 0.2;
			tempIndexX++;
			if (tempIndexX >= xSquareSize) {
				tempIndexX = 0;
				tempIndexY++;
			}
		}
	}
	
	public void setHabitat(String habitatName) {
		for (int i = 0; i < cellIndex; i++) {
			cells[i].setHabitat(this);
		}
	}
	
	public void addCell(Cell [] newCells) {
		for (int i = 0; i < newCells.length; i++) {
			addCell(newCells[i]);
		}
	}
	
	public void addCell(Cell newCell) {
		for (int i = 0; i < cellIndex; i++) {
			if (cells[i].equals(newCell)) {
				System.out.println("This habitat already contains this cell");
				return;
			}
		}
		if (cellIndex>=cells.length) {
			Cell[] oldCells = cells;
			cells = new Cell[oldCells.length+1];
			for (int i = 0; i < oldCells.length; i++) {
				cells[i] = oldCells[i];
			}
		}
		cells[cellIndex] = newCell;
		if (featureDefiner) {cells[cellIndex].setHabitat(this);}
		cellIndex++;
	}
	
	public Cell[] getCells() {
		/*if (cellIndex==cells.length) {
			return cells;
		}*/
		Cell [] output = new Cell[cellIndex];
		for (int i = 0; i < cellIndex; i++) {
			output[i] = cells[i];
		}
		return output;
	}
	
	public Cell[] getOtherHabitatsCells() {
		if (otherCells != null) {
			return otherCells;
		}
		int size = 0;
		for (int i = 0; i < otherHabitatIndex; i++) {
			size+=otherHabitats[i].cellIndex;
		}
		otherCells = new Cell[size];
		int index = 0;
		for (int i = 0; i < otherHabitatIndex; i++) {
			for (int j = 0; j < otherHabitats[i].cellIndex; j++) {
				otherCells[index] = otherHabitats[i].cells[j];
				index++;
			}
		}
		//System.out.println(""+otherHabitatIndex);
		return otherCells;
	}
	
	public double distanceFactorToHabitat(Habitat otherHabitat) {
		if (otherHabitat.equals(this)) {
			return 1;
		}
		for (int i = 0; i < otherHabitatIndex; i++) {
			if (otherHabitats[i].equals(otherHabitat)) {
				return 0.1+Math.max(0,0.9-distanceFactors[i]/200);
			}
		}
		System.out.println("Error: No predefined distance factor!");
		return -1;
	}
	
	public void prepareIslandpick() {
		double diameter = Math.sqrt(size)*2;
		totalChance = 0;
		for (int i = 0; i < otherHabitatIndex; i++) {
			chanceFactors[i] = ((diameter)/(distanceFactors[i]*2*Math.PI))*(Math.min(1,100/distanceFactors[i]));
			//System.out.println(name + " > " + otherHabitats[i].name + " = "+ chanceFactors[i]);
			totalChance+=chanceFactors[i];
		}
		
	}
	
	public Cell pickIsland() {
		if (totalChance == 0) {
			prepareIslandpick();
		}
		double number = world.r.nextDouble();
		for (int i = 0; i < otherHabitatIndex; i++) {
			number-=chanceFactors[i];
			if (number<0) {
				return otherHabitats[i].pickCell();
			}
		}
		return null;
	}
	
	public Cell pickCell () {
		return cells[world.r.nextInt(cellIndex)];
	}
	
	public void merge(Habitat otherHabitat) {
		mergedNextTurn[getOtherHabitatIndex(otherHabitat)] = true;
		otherHabitat.mergedNextTurn[otherHabitat.getOtherHabitatIndex(this)] = true;
	}
	
	public int getOtherHabitatIndex(Habitat otherHabitat) {
		for (int i = 0; i < otherHabitatIndex; i++) {
			if (otherHabitats[i].equals(otherHabitat)) {
				//distanceFactors[i] = distanceFactor;
				return i;
			}
		}
		return -1;
	}
	
	public void connectHabitats(Habitat otherHabitat, double distanceFactor) {
		// do not connect to yourself
		if (otherHabitat.equals(this)) {
			return;
		}
		// each connection is always reciprocal
		addConnection(otherHabitat, distanceFactor);
		otherHabitat.addConnection(this, distanceFactor);
	}
	
	public void addConnection(Habitat otherHabitat, double distanceFactor) {
		// overwrite distance factor, if you are already connected to this habitat
		for (int i = 0; i < otherHabitatIndex; i++) {
			if (otherHabitats[i].equals(otherHabitat)) {
				//distanceFactors[i] = distanceFactor;
				return;
			}
		}
		// otherwise, connect to this habitat and define the distance factor
		otherHabitats[otherHabitatIndex] = otherHabitat;
		distanceFactors[otherHabitatIndex] = distanceFactor;
		// each connection is always reciprocal
		otherHabitatIndex++;
	}
	
	
	
	public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		// code in this method copied and adapted from http://www.movable-type.co.uk/scripts/latlong.html
		// formula based on Haversine formula
		lat1 = Math.toRadians(lat1); lat2 = Math.toRadians(lat2);
		lon1 = Math.toRadians(lon1); lon2 = Math.toRadians(lon2);
		double R = 6371; // km
		double dLat = (lat2-lat1);
		double dLon = (lon2-lon1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.cos(lat1) * Math.cos(lat2) * 
		        Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c;
		return d;
	}
	
	public Cell[] getNeigbours(Cell parentCell, int max) {
		int parentIndex = -1;
		max = Math.min(max, cellIndex-1);
		for (int i = 0; i < cellIndex; i++) {
			if (cells[i].equals(parentCell)) {
				parentIndex = i;
				break;
			}
		}
		if (parentIndex==-1) {
			System.out.println("parentIndex still -1");
		}
		Cell[] neigbourHood = new Cell[max];
		int tempIndex = 0;
		for (int i = parentIndex+1; i < parentIndex+max+1; i++) {
			neigbourHood[tempIndex] = cells[i%cellIndex];
			tempIndex++;
		}
		return neigbourHood;
	}
	
	public double getPopulation() {
		double output = 0;
		for (int i = 0; i < cellIndex; i++) {
			output+=cells[i].getTotalIndividuals();
		}
		return output;
	}
	
	public double getFoodDensity(int type) {
		double output = 0;
		for (int i = 0; i < cellIndex; i++) {
			output+=cells[i].foodSource[type].getRelativeFood();
		}
		return output/(double)cellIndex;
	}
	
	public double getBestNar() {
		double output = 0;
			for (int j = 0; j < 2; j++) {
				double typeNAR = getBestNar(j);
				if (typeNAR > output) {
					output = typeNAR;
			}
		}	
		return output;
	}
	
	public double getBestNar(int type) {
		double output = 0;
		for (int i = 0; i < cellIndex; i++) {
			if (cells[i].foodSource[type].NAR > output) {
				output = cells[i].foodSource[type].NAR;
			}	
		}
		return output;
	}
	
	public double getAverageTimeLeft() {
		double output = 0;
		double pop = getPopulation();
		if (pop==0) {
			return 24;
		}
		for (int i = 0; i < cellIndex; i++) {
			output+=cells[i].getTotalTimeLeft();
		}
		return (output/365)/pop;
	}

}
