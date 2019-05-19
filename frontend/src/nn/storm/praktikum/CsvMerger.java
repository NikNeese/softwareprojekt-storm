package nn.storm.praktikum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvMerger {
	
	
	public static void main(String[] args) {
		CsvMerger cm = new CsvMerger();
		ArrayList<ArrayList<String>> xss = cm.readCsv("test.csv", ";", true);
		ArrayList<ArrayList<String>> yss = xss;
		ArrayList<ArrayList<String>> zss = new ArrayList<ArrayList<String>>();
		zss.addAll(xss);zss.addAll(yss);
		for(ArrayList<String> xs : zss) {
			for(String x : xs) {
				System.out.print(x+" ");
				
			}
			System.out.println();
		}
	}
	//what time is it at the start of the run
	//from this point rearrange lists by column x (sort by ascending order or merge it)
	ArrayList<ArrayList<String>> readCsv(String file, String split, boolean header) {
		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        String csvFile = file;
        String line = "";
        String cvsSplitBy = split;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
            	if(header) {
                    String[] array = line.split(cvsSplitBy);
                    ArrayList<String> tmp = new ArrayList<String>(Arrays.asList(array));
                    ret.add(tmp);
            	} else {
            		header = true;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return ret;
	}
	
	void mergeFilesByColX(int x) {
		
		
	}
}
