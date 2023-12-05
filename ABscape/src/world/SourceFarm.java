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

public class SourceFarm extends Source{

	public double cerealGradient;
	public double cerealKilos;
	public double extraCerealKilos;
	private double cerealFarmTime;
	private double maxFarmersDensity;
	public double staticNAR;
	
	boolean maximumReached = false;
	
	public SourceFarm(Habitat habitat, Cell myCell) {
		super(2, myCell);
		growth = 0;
		repopulateSize = p.minKilosToSpread;

		cerealGradient = habitat.cerealGradient;
		
		cerealKilos = (cerealGradient * (p.cerealMaxKilos - p.cerealMinKilos)) + p.cerealMinKilos;
		extraCerealKilos = 0.25 * (1000 - cerealKilos); // *was 600, changed to 1000, as paper records*
		cerealFarmTime = (1 / (p.hectaresPerHour * (cerealKilos + extraCerealKilos))) * 365 + p.cerealHarvestTime;
		//System.out.println(p.cerealEnergy - cerealFarmTime * p.catchCost);
		staticNAR = (p.cerealEnergy/(cerealFarmTime) - p.catchCost); // *was - ((cerealFarmTime) * p.catchCost)), changed to -p.catchCost, zoals mailverkeer Bart-Elske dat opmerkte*;
		potentialNARmax = (p.cerealEnergy/(0.96*cerealFarmTime) - p.catchCost);
		max=(0.25*(1000-cerealGradient*p.maxCerealKilosPerHectare)+(cerealGradient*p.maxCerealKilosPerHectare))*p.maxFarmHectares; // *was 600, changed to 1000, as paper records*
		//System.out.println("" + max + " " + ((cerealFarmTime * p.catchCost)-p.cerealEnergy));
		maxFarmKilosNeeded = ((p.maxBandSize*365*p.energyRequirement)/(p.cerealEnergy-((cerealFarmTime)*6*60)));
		maxFarmersDensity = ((max*(p.cerealEnergy-cerealFarmTime*6*60))/(p.energyRequirement*365))/myCell.sqKM;
		//carryingCapacity = 500;
		carryingCapacity = habitat.cerealGradient*4500;
		//max = 0; // use this to disable farming
		//staticNAR = 0; // use this to disable farming
		//System.out.println("" + staticNAR);
		updateNAR();
	}
	
	public boolean hasFreeLand(int individuals) {
		double energyNeeded = individuals*365*p.energyRequirement;
		double timeToFarm = energyNeeded/NAR;
		double cerealNeeded = (energyNeeded+timeToFarm*6*60)/p.cerealEnergy;
		return (max-population) >=cerealNeeded;// maxFarmKilosNeeded;
	}
	
	public boolean canSustainMoreFarmers(int individuals) {
		return (myCell.getIndividualDensity()+individuals/myCell.sqKM) < maxFarmersDensity;
	}
	
	public double calculateNAR() {
		//cerealFarmTime = (1 / (p.hectaresPerHour * (cerealKilos + extraCerealKilos))) * 365 + p.cerealHarvestTime;
		//cerealFarmTime*=0.5+2*Math.pow((population/max)-0.5,2);

		//cerealFarmTime*=Math.min(1,Math.max(0.5,0.5+2*Math.pow((this.myCell.index-12)/(double)24,2)));
		//maxFarmKilosNeeded = ((p.maxBandSize*365*p.energyRequirement)/(p.cerealEnergy-((cerealFarmTime)*6*60)));
		//staticNAR = ((p.cerealEnergy - cerealFarmTime * p.catchCost)/cerealFarmTime);
		//if (population > 0) {
		//	System.out.println("pop: " + population/max + " " + (0.5+2*Math.pow((population/max)-0.5,2)) + " " + ((p.cerealEnergy - cerealFarmTime * p.catchCost)/cerealFarmTime));
		//}

		return staticNAR;//(p.cerealEnergy/(cerealFarmTime) - ((cerealFarmTime) * p.catchCost));
	}
	
	public void grow() { // overwrites parent grow methods
		population = 0;
	}
	
	public double [] calculateEnergy(double energyNeeded, double timeAvailable) {
		double [] output = new double [2];
		if (Double.isNaN(population)) {
			System.out.println("population1!");
			System.exit(1);
		}
		if (Double.isNaN(timeAvailable)) {
			System.out.println("timeAvailable!");
			System.exit(1);
		}
		if (Double.isNaN(energyNeeded)) {
			System.out.println("energyNeeded!");
			System.exit(1);
		}
		double cerealFarmable = Math.min(Math.max(0,max-population),timeAvailable/(cerealFarmTime));
		if (Double.isNaN(cerealFarmable)) {
			System.out.println("cerealFarmable!");
			System.exit(1);
		}
		double timeToFarm = energyNeeded/NAR;
		double cerealNeeded = (energyNeeded+timeToFarm*6*60)/p.cerealEnergy;
		if (Double.isNaN(cerealNeeded)) {
			System.out.println("cerealNeeded!");
			System.exit(1);
		}
			//(energyNeeded/NAR)/(cerealFarmTime);//(energyNeeded/(p.cerealEnergy-(cerealFarmTime*6*60)));
		double cerealFarmed = Math.min(cerealFarmable,cerealNeeded);
		cerealFarmed = Math.max(0, cerealFarmed);
		if (Double.isNaN(cerealFarmed)) {
			System.out.println("CerealFarmed!");
			System.exit(1);
		}
		if (timeAvailable < energyNeeded/NAR && cerealFarmed < cerealNeeded) {
			output[0] = 0;
			output[1] = 0;
			return output;
		}
		//double energyExpended = cerealFarmed * (cerealFarmTime*6*60);
		output[0] = (cerealFarmed*p.cerealEnergy)-(cerealFarmed*(cerealFarmTime)*6*60);
		output[1] = cerealFarmed*(cerealFarmTime);
		//System.out.println("" + cerealFarmed);
		population= Math.min(max,population + cerealFarmed);
		if (Double.isNaN(population)) {
			System.out.println("Population!");
			System.exit(1);
		}
		//updateNAR();
		//System.out.println("" + population/max);
		//System.out.println("total farmland " + population/maxFarmableKilos);
		return output;
	}

	
}
