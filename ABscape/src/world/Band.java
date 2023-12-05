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

public class Band {

	protected World world;
	private Parameters p;
	public int ID;
	//private int radius = 21;

	public Cell myCell;
	public double energy;
	public int individuals = 20;
	public int individualsAtStart = 20;
	public boolean newBorn;
	public int deaths = 0;
	public int births = 0;
	public double timeLeft, timeHunted, timeGathered, timeFarmed;
	public double energyHunted, energyGathered, energyFarmed;
	boolean migrated = false;

	public Band(World w, int index, Cell startCell) {
		this.ID = index;
		world = w;
		p = world.p;
		energy = 1000;
		myCell = startCell;
		individuals = p.minBandSize;
		addMeToNewCell(myCell);
		newBorn = true;
	}	

	public void turn() {
		removeMeFromOldCell(); // at start, agents are roaming, so no longer present in old cell (but it's very possible they'll choose current cell again)
		individualsAtStart = individuals;
		newBorn = false;
		act();
	}

	private void procreate() {
		births = 0;
		for (int i = 0; i < individuals; i++){
			if (world.r.nextDouble()<0.02) {
				births++;
			}
		}
		individuals+=births;
		if (individuals > p.maxBandSize) {
			individuals-=p.minBandSize;
			world.addNewBand(myCell);
		}
	}

	private void act() {
		// decide where to move to, and go there
		Cell newCell = decide();
		migrated = false;
		move(newCell);
		
		// hunt gather and farm on current tile
		eatAndDie();
		/*Cell migrateCell = migrate();
		if (migrateCell != null) {
			newCell = migrateCell;
			move(newCell);
		}*/
		procreate();
	}

	public Cell decideAdvanced() {
		Cell [] visibleCells = world.getAllCells(myCell, true);
		//Cell bestCell = myCell;
		Cell bestCell = visibleCells[0];
		// prey
		for (int i = 0; i < visibleCells.length; i++) {
			if (visibleCells[i].foodSource[0].NAR > Math.max(0,bestCell.foodSource[0].NAR)) {
				bestCell = visibleCells[i];
			}
		}
		if (bestCell.foodSource[0].NAR > 0) {
			return bestCell;
		}
		// cereal
		for (int i = 0; i < visibleCells.length; i++) {
			if (visibleCells[i].foodSource[1].NAR > Math.max(0,bestCell.foodSource[1].NAR)) {
				bestCell = visibleCells[i];
			}
		}
		if (bestCell.foodSource[1].NAR > 0) {
			return bestCell;
		}
		// farm
//		bestCell = myCell;
//		if (myCell.foodSource[2].population >= (myCell.foodSource[2].max-myCell.foodSource[2].maxFarmKilosNeeded)) { // always move if farm-land is full
//			for (int i = 0; i < visibleCells.length; i++) {
//				if (visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded)) {
//					bestCell = visibleCells[i]; // pick the first cell that has available farmland
//				}
//			}
//			//System.out.println("not enough farmland, let's move along");
//		}
		for (int i = 0; i < visibleCells.length; i++) { // look for a potential better farm spot, first higher NAR, then more farmground, then less farmers
			if (//visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded) 
					
					(visibleCells[i].foodSource[2].NAR > bestCell.foodSource[2].NAR && visibleCells[i].foodSource[2].hasFreeLand(individuals) && visibleCells[i].foodSource[2].canSustainMoreFarmers(individuals))
						||
					(visibleCells[i].foodSource[2].NAR >= bestCell.foodSource[2].NAR && visibleCells[i].foodSource[2].population < bestCell.foodSource[2].population && visibleCells[i].foodSource[2].canSustainMoreFarmers(individuals))	
						||
					(visibleCells[i].foodSource[2].NAR >= bestCell.foodSource[2].NAR && visibleCells[i].foodSource[2].population <= bestCell.foodSource[2].population && visibleCells[i].getIndividualDensity() < bestCell.getIndividualDensity())
				){
				bestCell = visibleCells[i];
			}
		}
		if (!bestCell.foodSource[2].hasFreeLand(individuals)) {
			for (int i = 0; i < visibleCells.length; i++) { // look for a potential better farm spot, either higher NAR or fewer farmers (closer to overfarming)
				if (visibleCells[i].foodSource[2].population < bestCell.foodSource[2].population){
					bestCell = visibleCells[i];
				}
			}
		}
		//System.out.println("Pop: " + bestCell.foodSource[2].population + " of " + bestCell.foodSource[2].max);
		return bestCell;
	}

	public Cell decide() {
		Cell [] visibleCells = world.getAllCells(myCell, true);
		//Cell bestCell = myCell;
		Cell bestCell = visibleCells[0];
		// prey
		for (int i = 0; i < visibleCells.length; i++) {
			if (visibleCells[i].foodSource[0].NAR > Math.max(0,bestCell.foodSource[0].NAR)) {
				bestCell = visibleCells[i];
			}
		}
		if (bestCell.foodSource[0].NAR > 0) {
			return bestCell;
		}
		// cereal
		for (int i = 0; i < visibleCells.length; i++) {
			if (visibleCells[i].foodSource[1].NAR > Math.max(0,bestCell.foodSource[1].NAR)) {
				bestCell = visibleCells[i];
			}
		}
		if (bestCell.foodSource[1].NAR > 0) {
			return bestCell;
		}
		// farm
//		bestCell = myCell;
//		if (myCell.foodSource[2].population >= (myCell.foodSource[2].max-myCell.foodSource[2].maxFarmKilosNeeded)) { // always move if farm-land is full
//			for (int i = 0; i < visibleCells.length; i++) {
//				if (visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded)) {
//					bestCell = visibleCells[i]; // pick the first cell that has available farmland
//				}
//			}
//			//System.out.println("not enough farmland, let's move along");
//		}
		for (int i = 0; i < visibleCells.length; i++) { // look for a potential better farm spot, first higher NAR
			if (//visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded) 
					(visibleCells[i].foodSource[2].NAR > bestCell.foodSource[2].NAR && visibleCells[i].foodSource[2].canSustainMoreFarmers(individuals))
				){
				bestCell = visibleCells[i];
			}
		}
		if (!bestCell.foodSource[2].hasFreeLand(individuals)) {
			for (int i = 0; i < visibleCells.length; i++) { // look for a potential better farm spot, either higher NAR or fewer farmers (closer to overfarming)
				//if (visibleCells[i].foodSource[2].population < bestCell.foodSource[2].population){
				if (visibleCells[i].foodSource[2].canSustainMoreFarmers(individuals)) {
					bestCell = visibleCells[i];
					break;
				}
			}
		}
		//System.out.println("Pop: " + bestCell.foodSource[2].population + " of " + bestCell.foodSource[2].max);
		return bestCell;
	}

	
	public Cell decideOld() {
		// look around
		//Cell [] visibleCells = world.getCells(myCell, p.maxMovement);
		Cell [] visibleCells = world.getAllCells(myCell, true);
		Cell bestCell = visibleCells[0];
		double thisNAR = 0;	
		double bestNAR = 0;

		// I can still farm at my current spot, but look around for better spots in NAR

		/*	for (int i = 0; i < visibleCells.length; i++) { // find the first available farm land
			if (visibleCells[i].foodSource[2].hasFreeLand() && visibleCells[i].getTotalIndividuals() <= visibleCells[i].foodSource[2].carryingCapacity/10) {
				bestCell = visibleCells[i];
				bestNAR = bestCell.foodSource[2].NAR;
				break;
			}
		}*/
		bestNAR = 0;

		/*for (int i = 0; i < visibleCells.length; i++) {
			thisNAR = visibleCells[i].foodSource[2].NAR;//*populationModifier(visibleCells[i].getTotalIndividuals(),2);
			if ((visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded) 
					&& thisNAR >= bestNAR && visibleCells[i].getTotalIndividuals() <= (visibleCells[i].foodSource[2].carryingCapacity-20))) {
				bestCell = visibleCells[i];
				bestNAR = thisNAR;
			}
		}
		if (bestCell != null && bestCell.getTotalIndividuals() <= bestCell.foodSource[2].carryingCapacity-20) {
			return bestCell;
		}*/
		//System.out.println("full");
		for (int i = 0; i < visibleCells.length; i++) { // find the first available farm land
			if (visibleCells[i].foodSource[2].hasFreeLand(individuals)) {
				bestCell = visibleCells[i];
				bestNAR = bestCell.foodSource[2].NAR;
				break;
			}
		}

		for (int i = 0; i < visibleCells.length; i++) {
			thisNAR = visibleCells[i].foodSource[2].NAR;//*populationModifier(visibleCells[i].getTotalIndividuals(),2);		
			if ((thisNAR > bestNAR && visibleCells[i].foodSource[2].hasFreeLand(individuals)) || 
					(thisNAR == bestNAR && visibleCells[i].foodSource[2].hasFreeLand(individuals) && visibleCells[i].getTotalIndividuals() < bestCell.getTotalIndividuals())) {
				bestCell = visibleCells[i];
				bestNAR = thisNAR;
			}
		}

		double bestNAR2 = 0;
		for (int i = 0; i < visibleCells.length; i++) {
			thisNAR = visibleCells[i].foodSource[2].NAR;//*populationModifier(visibleCells[i].getTotalIndividuals(),2);
			if ((visibleCells[i].foodSource[2].population < (visibleCells[i].foodSource[2].max-visibleCells[i].foodSource[2].maxFarmKilosNeeded) 
					&& thisNAR >= bestNAR2 && visibleCells[i].getTotalIndividuals() <= (visibleCells[i].foodSource[2].carryingCapacity-20)
					&& visibleCells[i].foodSource[2].potentialNARmax > bestNAR)) {
				bestCell = visibleCells[i];
				bestNAR2 = thisNAR;
			}
		}

		//System.out.println("" + bestCell.getTotalIndividuals());
		if (bestCell == null) {
			return myCell;
		}
		return bestCell;
	}

	public double populationModifier(double population, int foodType) {
		double maxPreyPoint = 50;
		double maxCerealPoint = 100;
		double maxFarmPoint = 200;
		double output = 0;
		if (foodType == 0) {
			output = 1+-1*(1/(2*(double)Math.pow(maxPreyPoint,2)))*Math.pow(population-maxPreyPoint,2);
		}
		else if (foodType == 1) {
			output = 1+-1*(1/(2*(double)Math.pow(maxCerealPoint,2)))*Math.pow(population-maxCerealPoint,2);
		}
		else if (foodType == 2) {
			output = 1+-1*(1/(2*(double)Math.pow(maxFarmPoint,2)))*Math.pow(population-maxFarmPoint,2);
		}

		//System.out.println("Pop: " + population + " type: " + foodType + " mod: " + output);

		return output;
	}

	public void move(Cell newCell) {
		if (myCell.habitat!=newCell.habitat) {
			//	double distanceF = myCell.habitat.distanceFactorToHabitat(newCell.habitat);
			//	if (world.r.throwDice(1-distanceF)) {
			//		individuals = 0;
			//		return;
			//	}
			migrated = true;
			//System.out.println("Migrated: " + myCell.habitat.distanceFactorToHabitat(newCell.habitat));
		}
		addMeToNewCell(newCell);
	}

	public void eatAndDie() {
		timeHunted = timeGathered = timeFarmed = 0;
		double preyNar = myCell.foodSource[0].NAR;
		double cerealNar = myCell.foodSource[1].NAR;
		double farmNar = myCell.foodSource[2].NAR;
		boolean doIFarm = cerealNar < farmNar;
//		if (myCell.foodSource[2].population >= (myCell.foodSource[2].max-myCell.foodSource[2].maxFarmKilosNeeded)) {
//			doIFarm = false;
//		}
		double energyRequired = 365*individuals*p.energyRequirement;
		//System.out.println(individuals + " potential deaths: " + ((int)(energyRequired/(365*p.energyRequirement))+1));
		//System.out.println("Time spent per agent per day: " + (energyRequired/myCell.foodSource[1].NAR)/365/individuals);
		timeLeft = 365*individuals*p.maxForageTime;
		double [] results = new double[2];
		// first, let's decide how much energy to gain from hunting
		double preyEnergyRequired = 0;
		//System.out.println("Energy required: " + energyRequired);
		if (preyNar <= 0 && cerealNar <= 0) { // if there is nothing to gain from either hunting or gathering
			preyEnergyRequired = 0;
		}
		else { // if hunting will be a proportion of hunting, gathering and farming....only count positive nars
			preyEnergyRequired = energyRequired(energyRequired, preyNar, new double[] {cerealNar, (doIFarm?farmNar:0)});
		}
		// go hunt
		energyHunted = 0;
		if (preyEnergyRequired > 0 && myCell.foodSource[0].NAR > 0) {
			results = myCell.foodSource[0].getEnergy(preyEnergyRequired, timeLeft);
			energyHunted = results[0];
			energyRequired-=results[0]; timeLeft-=results[1]; timeHunted = results[1];
			//System.out.println("Energy left after hunting: " + energyRequired);
		}
		// then, decide how much energy to gain from cereal by gathering
		double cerealEnergyRequired = 0;
		if (doIFarm) {
			cerealEnergyRequired = energyRequired(energyRequired, cerealNar, new double[] {farmNar});
		}
		else {
			cerealEnergyRequired = energyRequired;
		}
		//		System.out.println("farm: " + doIFarm + " preyEnergy: " + preyEnergyRequired + " cerealEnergy: " + cerealEnergyRequired + " of total: " + 365*individuals*p.energyRequirement);
		energyGathered = 0;
		if (cerealEnergyRequired > 0 && myCell.foodSource[1].NAR > 0) {
			results = myCell.foodSource[1].getEnergy(cerealEnergyRequired, timeLeft);
			energyGathered = results[0];
			energyRequired-=results[0]; timeLeft-=results[1]; timeGathered = results[1];
			//System.out.println("Energy gained by  gathering: " + results[0]);
			//System.out.println("Energy left after gathering: " + energyRequired);
		}

		// farm the rest
		energyFarmed = 0;
		if (doIFarm) {
			results = myCell.foodSource[2].getEnergy(energyRequired, timeLeft);
			energyFarmed = results[0];
			energyRequired-=results[0]; timeLeft-=results[1]; timeFarmed = results[1];
			//System.out.println("I farmed " + energyRequired + " " + timeLeft);
			//System.out.println("Energy left after farming: " + energyRequired);
		}
		//System.out.println("Time spent per agent per day: " + (p.maxForageTime-(timeLeft/(double)365/(double)individuals)));
		deaths = 0;
		if (energyRequired>1) { // relatively small non-zero comparison to compensate for minute rounding errors

			deaths = (int)(energyRequired/(365*p.energyRequirement))+1;
			if (deaths > individuals) { deaths = individuals; }
			//System.out.println("Deaths: " + deaths + " farmland left: " + ((myCell.foodSource[2].max - myCell.foodSource[2].population)) + " Hab: " + myCell.habitat.ID + " F? " + myCell.foodSource[2].hasFreeLand(individuals));
			individuals-=deaths;
			if (individuals<=0) {individuals=0;}
			individualsAtStart = individuals;
		}
	}

	private double energyRequired(double totalEnergy, double myNAR, double[] otherNAR) {
		double otherTotal = 0;
		myNAR = Math.max(0, myNAR);
		for (int i = 0; i < otherNAR.length; i++) {
			otherTotal+=Math.max(0, otherNAR[i]);
		}
		return totalEnergy * (myNAR/(myNAR+otherTotal));
	}

	protected void removeMeFromOldCell() {
		myCell.removeBand(this);
	}

	public void addMeToNewCell(Cell newCell) {
		myCell = newCell;
		newCell.addBand(this);
	}

	public double getTimeLeft() {
		return timeLeft;

	}

}
