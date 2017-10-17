package utils;

import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShapesJSONDataReader {

	public static void main(String[] args) {
		// Loop over all data-files listed in provided index-file, and write each of them to a libsvm format
		
		String listOfConcepts = "data/shapes/datasets/listOfConceptsWithDescriptions.txt";
		try {
			List<String> lines = Files.readAllLines(Paths.get(listOfConcepts));
			for(String line:lines) {
				String[] toks = line.split("\\s+");
				String inputFile = toks[0];
				String libsvmFile = toks[1];
				
				ArrayList<ShapeInstance> data = readShapesJSONData(inputFile);
				writeToLibSVMFile(data, libsvmFile);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static ArrayList<ShapeInstance> readShapesJSONData(String inputFile) {	
		ArrayList<ShapeInstance> data = new ArrayList<>();	
		JSONParser parser = new JSONParser();
		try {
			JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(inputFile));
			for (int i = 0; i < jsonObject.size(); i++) {	
			    String shape = (String) ( (JSONObject) jsonObject.get(i)).get("outside.shape");
			    String color = (String) ( (JSONObject) jsonObject.get(i)).get("outside.fill");
			    String border = (String) ( (JSONObject) jsonObject.get(i)).get("outside.border");
			    String label = (String) ( (JSONObject) jsonObject.get(i)).get("label");
			    System.out.println(i+"\t"+shape+"\t"+color+"\t"+border+"\t"+label);
			    data.add(new ShapeInstance(shape, color, border, label));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void writeToLibSVMFile(ArrayList<ShapeInstance> data, String libsvmFile) {
		try {
			PrintWriter writer = new PrintWriter(libsvmFile);
			data.stream().forEach( d -> writer.println(d.diplay()));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
