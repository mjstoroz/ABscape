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

public class Source {

	int type;
	double max;
	protected double population;
	protected double cerealGradient;
	double growth;
	double repopulateSize;
	double viable;
	Cell myCell;
	double NAR;
	double maxNAR;
	Parameters p;
	double carryingCapacity;
	public double potentialNARmax;
	
	double staticZeroNar;
	public double maxFarmKilosNeeded;
	
	public Source (int type, Cell myCell) {
		this.type = type;
		this.myCell = myCell;
		p = myCell.world.p;
	}
	
	public void initialize() {
		population = max;
		updateNAR();
		maxNAR = NAR;
	}

	public void grow() {
		if (population <= 0 && max>0) {
			rePopulate();
		}
		if (population != 0) {
			population=Math.max(0,Math.min(max,
					population*(max*growth)/(max-(population*(1-growth)))
					));
		}
	}
	
	public void rePopulate() {
		Cell [] neighbourhood = myCell.habitat.getNeigbours(myCell, 4);//world.getCells(myCell, 1);
		for (int i = 0; i < neighbourhood.length; i++) {
			if (neighbourhood[i].foodSource[type].population >= repopulateSize && myCell.world.r.nextDouble() < p.rePopChance) {
				population = repopulateSize;
				break;
			}
		}
	}	
	
	public void updateNAR() {
		NAR = calculateNAR();
	}
	
	public double calculateNAR() {
		System.out.println("calculateNAR Parent-method!");
		return 0;
	}
	
	public double [] getEnergy(double energyNeeded, double timeAvailable) {
		double [] output = calculateEnergy(energyNeeded, timeAvailable);
		if (output[0] < 0) {
			System.out.println("You're not supposed to input more energy then out");
		}
		return output;
	}
	
	public double [] calculateEnergy(double energyNeeded, double timeAvailable) {
		System.out.println("calculateEnergy Parent-method!");
		return null;
	}
	
	
	public double getRelativeFood() {
		if (type == 0) {
		return population/(8*myCell.sqKM);
		}
		else if (type == 1) {
			return population/(600 * myCell.sqKM);
		}
		return population / (max);
	}
	
	public boolean hasFreeLand(int individuals) {
		System.out.println("Is a farm method, do not call unless source is a farm!");
		return true;
	}
	
	public boolean canSustainMoreFarmers(int individuals) {
		System.out.println("Is a farm method, do not call unless source is a farm!");
		return true;
	}
	
}
