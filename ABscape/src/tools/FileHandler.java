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

import java.io.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class FileHandler 
{	

	String location = "./data/";
	
	int index = 0;
	String [] fileName = new String[index];
	FileWriter [] fileWriter = new FileWriter[index];
	BufferedWriter [] bufferedWriter = new BufferedWriter[index];
	int run;
	
	public FileHandler(int run) {
		this.run = run;
		boolean success = (new File("data")).mkdir();
	}
	
	public boolean out(String name, String output) {
		int access = -1;
		for (int i = 0; i < index; i++) {
			if (name==fileName[i]) {
				access = i;
				break;
			}
		}
		if (access==-1) {
			if (!createFile(name)) {
				return false;
			}
			access = index-1;
		}
		try{
			bufferedWriter[access].write(output);
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean outln(String name, String output) {
		return out(name,output+"\n");
	}
	
	public boolean createFile(String newFileName) {
		String [] oldFileName = new String[index];
		FileWriter [] oldFileWriter = new FileWriter[index];
		BufferedWriter [] oldBufferedWriter = new BufferedWriter[index];
		for (int i = 0; i < index; i++) {
			oldFileName[i] = fileName[i];
			oldFileWriter[i] = fileWriter[i];
			oldBufferedWriter[i] = bufferedWriter[i];
		}
		fileName = new String[index+1];
		fileWriter = new FileWriter[index+1];
		bufferedWriter = new BufferedWriter[index+1];
		for (int i = 0; i < index; i++) {
			fileName[i] = oldFileName[i];
			fileWriter[i] = oldFileWriter[i];
			bufferedWriter[i] = oldBufferedWriter[i];
		}		
		try{
			// Create file
			fileName[index] = newFileName;
			fileWriter[index] = new FileWriter(location + run + fileName[index] + ".txt");
			bufferedWriter[index] = new BufferedWriter(fileWriter[index]);		
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());		
			return false;
		}
		index++;
		return true;
	}
	
	public boolean close() {
		try{
			for (int i = 0; i < index; i++) {
				bufferedWriter[i].close();
				fileWriter[i].close();
			}
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return false;
		}		
		index = 0;
		return true;
	}
	
    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
