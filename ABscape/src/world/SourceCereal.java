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

public class SourceCereal extends Source{

	public double cerealHectaresPerKM;
	//public double cerealHectares;
	public double cerealSearchTime;
	public double cerealGatherTime;
	
	public SourceCereal(Habitat habitat, Cell myCell) {
		super(1, myCell);
		growth = Math.pow(Math.E, p.cerealGrowthRate);

		cerealGradient = habitat.cerealGradient;
		max = habitat.maxCereal*myCell.sqKM;
		
		
		cerealHectaresPerKM = (cerealGradient * (p.cerealMaxHectaresPerKM - p.cerealMinHectaresPerKM)) + p.cerealMinHectaresPerKM;
		repopulateSize = p.minKilosToSpread;
		
		// max = 0; // use to remove all traces of cereal from the world
		initialize();
	}
	
	public double calculateNAR() {
		if (population == 0) {
			return staticZeroNar;
		}
		//cerealHectares = cerealHectaresPerKM * p.tileSize;
		cerealSearchTime = (1/(p.searchSpeed * p.searchRadius * 2 * ((population)/myCell.sqKM)));
		cerealGatherTime = cerealSearchTime + p.cerealHarvestTime;
		//cerealGatherTime*=Math.max(0.9,1-Math.pow(this.myCell.getTotalIndividuals()/100,2));
		//cerealGatherTime*=0.5+2*Math.pow(((max-population)/max)-0.5,2);
		staticZeroNar = -1000000;
		return (p.cerealEnergy/cerealGatherTime) - ((p.cerealHarvestTime/cerealGatherTime) * p.catchCost) - ((cerealSearchTime/cerealGatherTime) * p.searchCost);
	}
	
	public double [] calculateEnergy(double energyNeeded, double timeAvailable) {
		double [] output = {0,0};
		if (population == 0) {return output;}
		double cerealGatherable = Math.min(population,timeAvailable/cerealGatherTime);
		double cerealNeeded = (energyNeeded/NAR)/cerealGatherTime;//(energyNeeded/p.cerealEnergy)/(1-((cerealSearchTime*4*60+p.cerealHarvestTime*6*60)/p.cerealEnergy));
		double cerealGathered = Math.max(0,Math.min(cerealGatherable,cerealNeeded));
		if (timeAvailable < energyNeeded/NAR && cerealGathered < cerealNeeded) {
			output[0] = 0;
			output[1] = 0;
			return output;
		}
		//double energyExpended = cerealGathered * (cerealSearchTime*4*60 + p.cerealHarvestTime*6*60);
		output[0] = NAR*cerealGatherTime*cerealGathered;//(cerealGathered*p.cerealEnergy)-energyExpended;
		output[1] = cerealGathered*cerealGatherTime;
		//System.out.println("Needed: " + energyNeeded + " Cereal: " + cerealGathered + " TE: "+ output[0] + " ES: " + cerealGathered*cerealSearchTime*4*60 + " EC: " + cerealGathered*p.cerealHarvestTime*6*60);
		population=Math.max(0,population-cerealGathered);
		updateNAR();
		return output;
	}
	
}
