package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class BirdsLibSVMWriter {

	/*
	 * Class that reads birdsSmall directory, and writes libsvm files for all 10 classes.
	 * For the birds data, we don't use negation features, since the descriptions rarely use them (4/400 =  ~1%).
	 * 
	 * */
	
	public static HashMap<Integer,String> hm = new HashMap<Integer, String>();
	static {
		try {
			for(String line:Files.readAllLines(Paths.get("data/birds/splits/selected_attributes.txt"))) {
				String[] toks = line.split("\\s+");
				hm.put(Integer.parseInt(toks[0]), toks[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String readDirectory = "data/birds/splits/";
		try {
			Files.list(Paths.get(readDirectory))
			.filter(file -> file.getFileName().toString().endsWith("filtered.txt"))
			.forEach(f -> processDataFile(f.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processDataFile(String fileName) {
		
		String outFile = fileName +".libsvm";
		
		try {
				
			PrintWriter writer = new PrintWriter(outFile);
			for(String line:Files.readAllLines(Paths.get(fileName))) {
				String[] toks = line.split("\\s+");
			
				String printLine = (toks[2].equals("POS")) ? "select":"non_select";
				for(int i=5; i<toks.length;i++) {
					printLine=printLine+" "+hm.get(Integer.parseInt(toks[i]))+":1";
				}
				writer.println(printLine);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
