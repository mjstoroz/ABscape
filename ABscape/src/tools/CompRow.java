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

/*
 * Created by:
 * Gert Stulp (s1276506)
 * Tim Dorscheidt (s1344226)
 */

import java.awt.*;
import javax.swing.*;

/**
 * Class CompRow handles arrays of visual components
 * for further handling 
 */
public class CompRow extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public CompRow (Component [] comp, int x, int y) {
		setLayout (new GridLayout (x,y));
		for (int i = 0; i < comp.length; i++) {
			add(comp[i]);
		}
	}
}