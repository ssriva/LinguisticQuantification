package utils;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BirdsDescriptionProcessing {

	
	public static String processLine(String line) {
		
		// Bill shape, body shape and size are easily identified by keywords, nothing to do except merge size features (simply done by changing mapping in selected_attributes file)
			
		// Primary and crown colors: blue, brown, grey, yellow, olive, green, black, white, red, buff
		if(line.toLowerCase().contains("crown") || line.toLowerCase().contains("head") ) {
			line = line.replaceAll("blue", "crown_blue").replaceAll("brown", "crown_brown")
					.replaceAll("grey", "crown_grey").replaceAll("yellow", "crown_yellow").replaceAll("olive", "crown_olive")
					.replaceAll("green", "crown_green").replaceAll("black", "crown_black")
					.replaceAll("white", "crown_white").replaceAll(" red", " crown_red").replaceAll("buff", "crown_buff");
		}else if(!line.toLowerCase().contains("belly")){
			line = line.replaceAll("blue", "primary_blue").replaceAll("brown", "primary_brown")
					.replaceAll("grey", "primary_grey").replaceAll("yellow", "primary_yellow").replaceAll("olive", "primary_olive")
					.replaceAll("green", "primary_green").replaceAll("black", "primary_black")
					.replaceAll("white", "primary_white").replaceAll(" red", " primary_red").replaceAll("buff", "primary_buff");
		}
		
		// Tail, belly and wing patterns: solid, spotted, striped, multicolored
		if(line.toLowerCase().contains("tail")) {
			line = line.replaceAll("solid", "tail_solid").replaceAll("spot", "tail_spot")
					.replaceAll("stripe", "tail_stripe").replaceAll("multi", "tail_multi");
		}else if(line.toLowerCase().contains("belly")) {
			line = line.replaceAll("solid", "belly_solid").replaceAll("spot", "belly_spot")
					.replaceAll("stripe", "belly_stripe").replaceAll("multi", "belly_multi");
		}else if(line.toLowerCase().contains("wing")) {
			line = line.replaceAll("solid", "wing_solid").replaceAll("spot", "wing_spot")
					.replaceAll("stripe", "wing_stripe").replaceAll("multi", "wing_multi");
		}
		
		return line;
	}
	
	public static void main(String[] args) {

		String readDirectory = "data/birds/filtered_statements/";
		try {

			Files.list(Paths.get(readDirectory))
			.filter(file -> file.getFileName().toString().endsWith("txt"))
			.forEach(f -> processDescriptionFile(f.toString()));
	

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void processDescriptionFile(String fileName) {
		try {

			String outFile = fileName+".filtered";
			System.out.println("Writing to "+outFile);

			PrintWriter writer = new PrintWriter(outFile);
			
			for(String line:Files.readAllLines(Paths.get(fileName))) {
				writer.println(processLine(line));
			}
			
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
