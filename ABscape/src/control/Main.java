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

import world.Parameters;
import world.World;

public class Main {
	public static void main(String[] args) {
		boolean visual = true; // set to false in case you wish to run non-visual automatic runs
		if (visual) {
			GUI gui = new GUI();
			gui.setVisible(true);
			gui.startWorld();
		}
		else {
			// here one can define how many turns per run you wish to have the simulation, and adjust each run seperately
			int nrOfTurns = 1000;
			for (int r = 17; r < 20; r++) { // adjust this to set the number of runs, and the iteration-number per run (used in filenames of output)
				long startTime = System.currentTimeMillis();
				Parameters p = new Parameters();
				// this would be the spot to change parameters per run
				World world = new World(p, r);
				world.initialize();
				for (int i = 0; i < nrOfTurns; i++) {
					if (i%(int)(nrOfTurns*0.1)==0){
						System.out.println("  Turns "  + i + " at "+ (System.currentTimeMillis()-startTime));
					}
					world.turn();
				}
				world.close();
				System.out.println("Done run " + r + " " + (System.currentTimeMillis()-startTime));
			}
		}
		System.out.println("All runs done");
		//frame.show();
	}
}




