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

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Tim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileToStringArray {

	
	int index = 0;
	String [] line = new String[100];
	FileReader fstream;
	BufferedReader in;
	
	public String [][] get(String file, int length) {
		index = 0;
		line = new String[100];
		 if(openFile(file)) {
		 	getMatrix();
		 	return splitRows(minimizeMatrix(),length);
		 }
		 return null;
	}

	private String[][] splitRows(String[] matrix, int length) {
		String [][] output = new String[matrix.length][length];
		for (int i = 0; i < matrix.length; i++) {
        	int tempIndex = 0;
        	String temp;
        	for (int j = 0; j < length; j++) {
        		int nextIndex = matrix[i].indexOf(",", tempIndex);
        		if (nextIndex==-1) {
        			nextIndex = matrix[i].length();
        		}
        		temp = matrix[i].substring(tempIndex, tempIndex = nextIndex);
        		tempIndex++;
        		output[i][j] = temp;
        	}
		}
		return output;
	}

	public String [] get(String file) {
		index = 0;
		line = new String[100];
		 if(openFile(file)) {
		 	getMatrix();
		 	return minimizeMatrix();
		 }
		 return null;
	}
	
	public void addLine(String data) {
		if (data!=null) {
			if (index >= line.length) {
				increaseLineArray();
			}
			line[index] = data;
			index++;
		}
	}
	
	public void increaseLineArray() {
		int oldLength = line.length;
		String [] oldLine = new String[oldLength];
		for (int i = 0; i < oldLength; i++) {
			oldLine[i] = line[i];
		}
		line = new String[oldLength+100];
		for (int i = 0; i < oldLength; i++) {
			line[i] = oldLine[i];
		}
	}
	
	public boolean openFile(String file) {
		try {
			fstream = new FileReader(file);
			in = new BufferedReader(fstream);
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getCause() + " :: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public String [] minimizeMatrix() {
		for (int i = index-1; i > 0; i--) {
			if (line[i]=="" || line[i]==null) {
				index--;
			}
			else {
				break;
			}
		}
		String [] output = new String[index];
		for (int i = 0; i < index; i++) {
			output[i] = line[i];
		}
		return output;
	}

	public void getMatrix() {
		String input;
		while(true) {
			try{
				// Read file

				input = in.readLine();
				addLine(input);
				if (input == null) {
					break;
				}
			}catch (Exception e){//End of line
				System.err.println("Error: " + e.getCause() + " :: " + e.getMessage());
				break;
			}
		}
		try {
			in.close();
			fstream.close();
		}catch (Exception e){//End of line
			System.err.println("Error: " + e.getCause() + " :: " + e.getMessage());
		}
	}	
}