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

public class Cell {
	
	public int maxPopulation = 20;
	public double sqKM = 300;
	public Band[] band = new Band[maxPopulation];
	public int index = 0;
	public World world;
	public Habitat habitat;
	int x, y;
	double lat, lon;
	private int totalIndividuals = -1;
	int totalDeaths = -1;
	int totalBirths = -1;
	int totalMigrated = -1;
	double timeLeft = -1;
	public boolean active = false;
	
	public Source[] foodSource = new Source[3];
	
	public Cell(int x, int y, World w) {
		this.x = x; this.y = y;
		world = w;
	}
	
	public void setHabitat(Habitat habitat) {
		active = true;
		this.habitat = habitat;
		foodSource[0] = new SourcePrey(habitat,this);
		foodSource[1] = new SourceCereal(habitat,this);
		foodSource[2] = new SourceFarm(habitat,this);
		
	}
	
	public void turn() {
		if (!active) {
			return;
		}
		updateStats();
		growFood();
		totalDeaths = -1;
		totalBirths = -1;
		totalMigrated = -1;
		timeLeft = -1;
	}
	
	public void updateFood() {
		foodSource[0].updateNAR();
		foodSource[1].updateNAR();
		foodSource[2].updateNAR();
	}
	
	public void growFood() {
		foodSource[0].grow();
		foodSource[1].grow();
		foodSource[2].grow();
	}
	
	public void removeBand(Band deadBand) {
		int location = -1;
		for (int i = 0; i < index; i++) {
			if (band[i].equals(deadBand)) {
				location = i;
				break;
			}
		}
		if (location == -1) {
			System.out.println("You are not in this cell!");
		}
		band[location] = null;
		for (int i = location; i < index-1; i++) {
			band[i] = band[i+1];
		}
		//band[index] = null;
		index--;
		updateStats();
	}
	
	public int addBand(Band newBand) {
		if (index < maxPopulation) {
			band[index] = newBand;
			index++;
			updateStats();
			return index-1;
		}
		else {
			Band[] tempDrone = band;
			band = new Band[maxPopulation+20];
			for (int i = 0; i < maxPopulation; i++) {
				band[i] = tempDrone[i];
			}
			tempDrone = null; // for Java's garbage collector
			maxPopulation+=20;
			return addBand(newBand);
		}
	}
	
	public void updateStats() {
		totalIndividuals = 0;
		for (int i = 0; i < index; i++) {
			//if (!band[i].newBorn) {
				totalIndividuals+=band[i].individuals;
			//}
		}
		updateFood();
	}
	
	public double getTotalIndividuals() {
		//if (true || totalIndividuals == -1) {
			//totalIndividuals = 0;
			//for (int i = 0; i < index; i++) {
			//	totalIndividuals+=band[i].individuals;
			//}
			return totalIndividuals;
		//}
		//return totalIndividuals;
	}
	
	public double getTotalDeaths() {
		if (totalDeaths == -1) {
			totalDeaths = 0;
			for (int i = 0; i < index; i++) {
				totalDeaths+=band[i].deaths;
			}
			return totalDeaths;
		}
		return totalDeaths;
	}
	
	public double getTotalBirths() {
		if (totalBirths == -1) {
			totalBirths = 0;
			for (int i = 0; i < index; i++) {
				totalBirths+=band[i].births;
			}
			return totalBirths;
		}
		return totalBirths;
	}
	
	public double getTotalMigrated() {
		if (totalMigrated == -1) {
			totalMigrated = 0;
			for (int i = 0; i < index; i++) {
				if (band[i].migrated) {
					totalMigrated+=band[i].individuals;
				}
			}
			return totalMigrated;
		}
		return totalMigrated;
	}
	
	public double getTotalTimeLeft() {
		if (timeLeft == -1) {
			timeLeft = 0;
			for (int i = 0; i < index; i++) {
				timeLeft+=band[i].timeLeft;
			}
			return timeLeft;
		}
		return timeLeft/(double)index;
	}
	
	public double getIndividualDensity() {
		return getTotalIndividuals()/(double)sqKM;
	}
	
	public double getDeathDensity() {
		return getTotalDeaths()/(double)sqKM;
	}
	
	public double getBirthDensity() {
		return getTotalBirths()/(double)sqKM;
	}
	
	public double getMigrated() {
		return getTotalMigrated();
	}
	
	public double getTimeLeft() {
		if (getTotalIndividuals()==0) {
			return 0;
		}
		return getTotalTimeLeft()/((double)getTotalIndividuals()*365);
	}
	
	public double getPreyDensity() {
		return foodSource[0].population/(double)(sqKM);
	}
	
	public double getCerealDensity() {
		return foodSource[1].population/(double)(sqKM);
	}
	
	public double getfarmDensity() {
		return foodSource[2].population/(double)(sqKM);
	}
	
	public double getPreyNar() {
		return foodSource[0].NAR;
	}
	
	public double getCerealNar() {
		return foodSource[1].NAR;
	}
	
	public double getFarmNar() {
		return foodSource[2].NAR;
	}

}
