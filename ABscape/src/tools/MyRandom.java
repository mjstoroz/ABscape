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


package tools;

import java.util.Random;

/**
 * @author Tim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyRandom {
	
	public long seed1, seed2;
	public Random doubleRandom;
	public Random gausRandom;
	public int doubles = 0;
	public int gaussians = 0;
	
	public MyRandom() {
		Random temp = new Random();
		seed1 = temp.nextLong();
		seed2 = temp.nextLong();
		doubleRandom = new Random(seed1);
		gausRandom = new Random(seed2);
	}
	
	public MyRandom(long s1, long s2, int d, int g) {
		seed1 = s1;
		seed2 = s2;
		doubleRandom = new Random(seed1);
		gausRandom = new Random(seed2);
		doubles = d;
		gaussians = g;
		for (int i = 0; i < d-1; i++) {
			doubleRandom.nextDouble();
		}
		for (int i = 0; i < g-1; i++) {
			gausRandom.nextGaussian();
		}
	}
	
	public int nextInt(int range) {
		return (int)(nextDouble()*range);
	}
	
	public double nextDouble() {
		doubles++;
		return doubleRandom.nextDouble();
	}
	
	public double nextGaussian() {
		gaussians++;
		return gausRandom.nextGaussian();
	}
	
	public int[] getRandomIndexOrder(int length) {
		int [] output = new int[length];
		for (int i = 0; i < length; i++) {
			output[i] = i;
		}
		int location = 0;
		int temp;
		for (int i = 0; i < length; i++) {
			location = (int)(nextDouble()*length);
			temp = output[location];
			output[location] = output[i];
			output[i] = temp;
		}
		return output;
	}
	
	public boolean throwDice(double chance) {
		return (nextDouble() < chance);
	}
	

}
