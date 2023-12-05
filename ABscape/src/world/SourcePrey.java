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

public class SourcePrey extends Source{

	public double preyDensity;
	public double preySearchTime;
	public double preyHuntTime;
	
	public SourcePrey(Habitat habitat, Cell myCell) {
		super (0, myCell);
		growth = Math.pow(Math.E, p.preyGrowthRate);
		repopulateSize = p.minNumToMigrate;

		max = habitat.maxPrey*myCell.sqKM;

		// max = 0; // use to remove all traces of prey from the world
		
		initialize();
	}
	
	public double calculateNAR() {
		if (population == 0) {
			return staticZeroNar;
		}
		preyDensity = population/myCell.sqKM;
		preySearchTime = (1/(p.searchSpeed * p.searchRadius * 2 * preyDensity));
		preyHuntTime = preySearchTime + p.preyCatchTime;
		//preyHuntTime*=Math.max(0.5,1-Math.pow(this.myCell.getTotalIndividuals()/40,2));
		staticZeroNar = -1000000;
		return (p.preyEnergy/preyHuntTime) - ((p.preyCatchTime/preyHuntTime) * p.catchCost) - ((preySearchTime/preyHuntTime) * p.searchCost);
	}
	
	public double [] calculateEnergy(double energyNeeded, double timeAvailable) {
		double [] output = {0,0};
		if (population == 0) {return output;}
		double preyCatchable = Math.min(population,timeAvailable/preyHuntTime);	
		double preyNeeded = (energyNeeded/NAR)/preyHuntTime;//(energyNeeded/p.preyEnergy)/(1-((preySearchTime*4*60 + p.preyCatchTime*6*60)/p.preyEnergy));
		double preyCaught = Math.max(0,Math.min(preyCatchable,preyNeeded));
		if (timeAvailable < energyNeeded/NAR && preyNeeded < preyCaught) {
			return output;
		}
		//double energyExpended = preyCaught*(preySearchTime*4*60 + p.preyCatchTime*6*60);
		output[0] = NAR*preyHuntTime*preyCaught;//preyCaught*p.preyEnergy-energyExpended;
		output[1] = preyCaught*preyHuntTime;
		//System.out.println("Needed: " + energyNeeded + " Prey: " + preyCaught + " TE: "+ output[0] + " ES: " + preyCaught*preySearchTime*4*60 + " EC: " + preyCaught*p.preyCatchTime*6*60);
		population=Math.max(0,population-preyCaught);
		updateNAR();
		return output;
	}
	
}
