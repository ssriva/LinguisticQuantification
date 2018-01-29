package utils;

import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BirdsJSONDescriptionsReader {

	private static int numValidConcepts = 0, numValidStatements = 0;

	public static void main(String[] args) {		

		String readDirectory = "data/birds/results_gen/";
		String writeDirectory = "data/birds/descriptions/";

		try {

			Files.list(Paths.get(readDirectory))
			.filter(file -> file.getFileName().toString().endsWith("json"))
			.forEach(f -> processJSONShapesDescriptions(f.toString(),f.getFileName().toString(), writeDirectory));

			System.out.println("\nNumber of valid concepts: "+numValidConcepts + "\nTotal valid statements: "+numValidStatements);
			System.out.println("Avg statements per concept: "+1.0*numValidStatements/numValidConcepts);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//readShapesFile("data/shapes/descriptions/features.alpha__0_1.160.A3MF31JQ350ABS.3SUWZRL0MZDAEHGXC080F4OGK0B6EN.result");
	}

	public static void processJSONShapesDescriptions(String fileName, String fileString, String writeDirectory) {
		JSONParser parser = new JSONParser();

		try {

			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(fileName));

			List<String> statements = IntStream.range(0, 10).mapToObj(i -> (String) jsonObject.get("sentence__"+i)).filter(obj -> obj!=null).collect(Collectors.toList());

			//JSONArray all = (JSONArray) jsonObject.get("all");
			//double score = (Double) jsonObject.get("score");
			//double accuracy = (Double) jsonObject.get("accuracy");
			//long total_correct = (Long) jsonObject.get("total_correct");

			if(statements!=null && statements.size()>0 && statements.get(0).split(" ").length>2) {
				System.out.println("\nFileName: "+fileName);
				//System.out.println("Score: "+score);
				//System.out.println("Accuracy: "+accuracy);
				//statements.stream().forEach(s -> System.out.println(s));
				numValidConcepts++;
				numValidStatements+=statements.size();
				writeDescriptionsToFile(fileString,writeDirectory,statements);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void writeDescriptionsToFile(String fileString, String writeDirectory, List<String> statements) {
		try {

			String outFile = writeDirectory+"/"+String.join(".", Arrays.copyOfRange(fileString.split("\\."), 0, 5))+ ".txt";
			System.out.println("Writing to "+outFile);

			PrintWriter writer = new PrintWriter(outFile);
			statements.stream().filter(s -> s.length()>2).forEach(s -> writer.println(s));
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
