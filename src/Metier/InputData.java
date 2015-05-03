package Metier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class InputData {
	
	public int [][] data;
	
	public InputData(int machines, int jobs){
		this(new int[machines][jobs]);
	}
	
	public InputData(int [][] data){
		this.data=data;
	}
	public int [][] getdata()
        {
            return data;
        }
	public int getRows() {
		return data.length;
	}
	public int getColumns() {
		return data[0].length;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				str.append(data[i][j]);
				str.append(", ");
			}
			str.append(System.getProperty("line.separator"));
		}
		return str.toString();
	}
	
	public static InputData read(Readable r) {
		Scanner scanner = new Scanner(r);

		int machines = scanner.nextInt();
		int jobs = scanner.nextInt();
		
		InputData data = new InputData(machines, jobs);
		
		for (int i=0; i<machines; i++) {
			for (int j=0; j<jobs; j++) {
				data.data[i][j] = Integer.parseInt(scanner.next());
			}
		}
	
			scanner.close();
		return data;
	}
	
	public static InputData read(File f) throws IOException{
		return read(new FileReader(f));
	}
	
}
