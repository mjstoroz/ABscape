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

public class Parameters {

	//public int tileSize = 300;
	public int worldSizeX = 21;
	public int worldSizeY = 21;
	
	public int sightRadius = 100;
	
	public double searchSpeed = 0.5;			// in kilometers/hour
	public double searchRadius = 0.0175;		// in kilometers/hour
	public double catchCost = 6*60;				// in kcals/hour
	public double searchCost = 4*60;			// in kcals/hour
	public double energyRequirement = 2000;		// in kcals/day
	
	public double rePopChance = 0.10;		// *was 0.05, now checks with paper*
	public double minNumToMigrate = 100;	// *was 10, now checks with paper*
	public double minKilosToSpread = 400;	// *was 1000, now checks with paper*
	
	public double preyMaxCarryingCapacity = 8;	// in individuals/kilometer
	public double preyMinCarryingCapacity = 0;	// in individuals/kilometer
	public double preyEnergy = 13800;			// in kcals per prey caught
	public double preyCatchTime = 3.916667;		// in hours per prey caught
	public double preyGrowthRate = 0.7;			// in individuals/year
	public double preyMaxNARAtPopulation = 200;
	
	public double cerealMaxKilos = 600;			// in kilo's/hectare of cereal
	public double cerealMinKilos = 0;			// in kilo's/hectare of cereal
	public double cerealMaxHectaresPerKM = 1;	// percent/kilometer
	public double cerealMinHectaresPerKM = 1;	// percent/kilometer
	public double cerealEnergy = 3390;			// in kcals per kilo gathered
	public double cerealHarvestTime = 2;		// in hours per kilo gathered
	public double cerealGrowthRate = 1;
	public double cerealMaxNARAtPopulation = 400;
	
	public double maxFarmHectares = 3000;
	public double maxCerealKilosPerHectare = 600;
	public double hectaresPerHour = 2;
	public double farmMaxNARAtPopulation = 800;
	
	public int maxMovement = Math.max(worldSizeX,worldSizeY);
	public double reproductionChance = 0.02;
	public double moveAversion = 1;
	public int minBandSize = 20;
	public int maxBandSize = 40;
	public boolean minMeat = false;
	public double minMeatPercent = 0.1;
	public double maxForageTime = 14;
	
}
