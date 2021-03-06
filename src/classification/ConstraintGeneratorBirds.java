package classification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.common.base.Preconditions;
import utils.QuantifierExpressions;
import utils.StanfordAnnotator;
import utils.Token;
import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;

public class ConstraintGeneratorBirds {

	/*
	 * Inputs:
	 * (0) Task formulation: Concept & features labels, and examples
	 * (1) List of NL sentences describing constraints
	 * (2) lexicon mapping words to features/labels
	 * 
	 * Output:
	 * (1) List of Model constraints that can be fed to PRClassifier. Includes following info:
	 * 	(a) Constraint types: what are the X and Y, what are the values for X and Y (true/false)
	 * 	(b) Constraint strengths: what is the preference strength of the constraint
	 * 	(c) Preference strengths for other classes (in case of multi-class)
	 * */

	public static HashMap<String,HashMap<String, Double>> parseFileToConstraints(InstanceList instances, String descriptionsFile) {

		HashMap<String,HashMap<String, Double>> constraintHashMap = new HashMap<String, HashMap<String,Double>>();
		//String constraintFile = "/Users/shashans/Work/tools/mallet-2.0.8/constraints.txt";
		try {
			List<String> lines = Files.readAllLines(Paths.get(descriptionsFile));
			//instances.getDataAlphabet().lookupIndex("");
			for(String line:lines){
				QuantificationConstraintBirds constraint = parseLineToConstraint(line, instances);
				if(constraint !=null && !constraint.getQuantifierExpression().equals(QuantifierExpressions.DEFAULT_QUANTIFIER_EXP)){
					
					if(constraint.getxIdx()>=0 && constraint.getyIdx()>=0)
						constraintHashMap.put((String) instances.getDataAlphabet().lookupObject(constraint.getxIdx()), constraint.validateAndSetProbabilities());
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return constraintHashMap;
	}


	private static QuantificationConstraintBirds parseLineToConstraint(String line, InstanceList instances) {

		line = line.replace("other", "not selected").replace("Other", "Not selected");
		
		Alphabet targetAlphabet = instances.getTargetAlphabet();
		Alphabet dataAlphabet = instances.getDataAlphabet();
		StanfordAnnotator annotation = new StanfordAnnotator(line);
		List<Token> tokens = annotation.tokenList;
		SemanticGraph dependencies = annotation.dependencies;
		
		System.out.println("\nParsing line "+line);

		//1.) Identify the label mentioned, and the token index
		Boolean foundLabel = false;
		int foundLabelIdx = -1;
		int foundLabelAtToken = -1;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			for(int i=0 ; i< targetAlphabet.size(); i++){
				foundLabel = tokens.get(tokIdx).getWord().contains((String) targetAlphabet.lookupObject(i))	||
						tokens.get(tokIdx).getLemma().contains((String) targetAlphabet.lookupObject(i));
				//|| ((String) targetAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getWord())|| ((String) targetAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getLemma());

				if(foundLabel){
					foundLabelIdx = i;
					foundLabelAtToken = tokIdx;
					System.out.println("Found label at token:"+tokens.get(tokIdx).getWord()+ " corresponding to label "+(String) targetAlphabet.lookupObject(i));
					break;
				}
			}	

			if(foundLabel){
				break;
			}
		}
		
		if(!foundLabel) {
			return null;
			//foundLabelIdx = 0;
			//foundLabelAtToken = 0;
		}

		//System.out.println("Label mentioned in constraint: "+line+" is :"+targetAlphabet.lookupObject(foundLabelIdx)+", corresponding to index: "+foundLabelIdx);

		//2.) Figure out quantifier expression, if any
		String quantifierExpression = QuantifierExpressions.DEFAULT_QUANTIFIER_EXP;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){	
			for(int i=0; i<QuantifierExpressions.quantExpressionsAdverbs.length;i++){
				if(tokens.get(tokIdx).getWord().equals(QuantifierExpressions.quantExpressionsAdverbs[i])){
					quantifierExpression = QuantifierExpressions.quantExpressionsAdverbs[i];
					break;
				}
			}

			for(int i=0; i<QuantifierExpressions.quantExpressionsDeterminers.length;i++){
				if(tokens.get(tokIdx).getWord().equals(QuantifierExpressions.quantExpressionsDeterminers[i])){
					quantifierExpression = QuantifierExpressions.quantExpressionsDeterminers[i];
					break;
				}
			}

			if(!quantifierExpression.equals(QuantifierExpressions.DEFAULT_QUANTIFIER_EXP)){
				break;
			}
		}
		System.out.println("QExp: "+quantifierExpression);

		//3.) Figure out the x. For now, we assume that the data representation already contains evaluations of all possible features
		//String logicalForm = parseExplanationToFeature(line, tokens, dataAlphabet);
		Boolean foundFeature = false;
		int foundFeatureIdx = -1;
		int foundFeatureAtToken = -1;

		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			for(int i=0 ; i< dataAlphabet.size(); i++){
				
				String featureName = ((String) dataAlphabet.lookupObject(i));
				foundFeature = tokens.get(tokIdx).getWord().contains( featureName )	||
						tokens.get(tokIdx).getLemma().contains( featureName );
				//|| ((String) dataAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getWord())|| ((String) dataAlphabet.lookupObject(i)).contains(tokens.get(tokIdx).getLemma());

				if(foundFeature){
					foundFeatureIdx = i;
					foundFeatureAtToken = tokIdx;
					System.out.println("Found feature at token:"+tokens.get(tokIdx).getWord()+ " corresponding to feature "+(String) dataAlphabet.lookupObject(i));
					break;
				}
			}	

			if(foundFeature){
				break;
			}
		}
		
		if(!foundFeature) {
			return null;
		}

		String featureString = (String) dataAlphabet.lookupObject(foundFeatureIdx);
		//System.out.println("Feature mentioned in constraint: "+line+" is :"+featureString+", corresponding to index: "+foundFeatureIdx);

		//4.)Figure out the constraint type
		//String type = classifyType(tokens, foundLabelAtToken, foundFeatureAtToken);
		Boolean passiveVoice = dependencies.edgeListSorted().stream().anyMatch( e -> e.getRelation().toString().contains("nsubjpass"));		
		String type;
		if(!passiveVoice) {
			type = (foundFeatureAtToken > foundLabelAtToken) ? "XY" : "YX";
		}else {
			type = (foundFeatureAtToken > foundLabelAtToken) ? "YX" : "XY";
		}
					
				
		Boolean featureNegation = false, labelNegation = false;
		for(int tokIdx=0; tokIdx<tokens.size(); tokIdx++){
			if(tokens.get(tokIdx).getWord().equals("n't") || tokens.get(tokIdx).getWord().equals("not") || tokens.get(tokIdx).getWord().equals("no")){
				
				SemgrexPattern pattern_verbnegation = SemgrexPattern.compile("{tag:/VB.*/}=verb >neg {idx:"+(tokIdx+1)+"}=negation [ >nsubj {}=subject | >nsubjpass {}=subject]");
			    SemgrexMatcher matcher = pattern_verbnegation.matcher(dependencies);
			    if(matcher.find()) {
			    		IndexedWord subject = matcher.getNode("subject");
			    		if(subject.index()==foundFeatureAtToken+1) {
			    			featureNegation = true;
			    		}else if(subject.index()==foundLabelAtToken+1) {
			    			labelNegation = true;
			    		}
			    		System.out.println("Found negation match for verb: "+ matcher.getNode("verb").word()+" with subject: "+subject.word());
			    }
			    
			    SemgrexPattern pattern_nounnegation = SemgrexPattern.compile("!{tag:/VB.*/}=noun >neg {idx:"+(tokIdx+1)+"}=negation");
			    matcher = pattern_nounnegation.matcher(dependencies);
			    if(matcher.find()) {
			    		IndexedWord noun = matcher.getNode("noun");
			    		if(noun.index()==foundFeatureAtToken+1) {
			    			featureNegation = true;
			    		}else if(noun.index()==foundLabelAtToken+1) {
			    			labelNegation = true;
			    		}
			    		System.out.println("Found negation match for noun: "+ noun.word());
			    }
			    
			    if(tokIdx==foundFeatureAtToken-1){
			    		System.out.println("Negation match for features without dependencies");
			    		featureNegation = true;
			    }
			    if(tokIdx==foundLabelAtToken-1){
			    	System.out.println("Negation match for label without dependencies");
		    			labelNegation = true;
			    }
			    
			    
				//if(foundLabelAtToken > tokIdx && foundLabelAtToken < foundFeatureAtToken) {
				//	labelNegation = true;
				//}
				//if(foundFeatureAtToken > tokIdx && foundFeatureAtToken < foundLabelAtToken) {
				//	featureNegation = true;
				//}
				
			}
		}
		System.out.println("featureNegation: "+featureNegation);

		return new QuantificationConstraintBirds(line, instances, foundLabelIdx, foundFeatureIdx, type, quantifierExpression, featureNegation, labelNegation);
	}

	private static String parseExplanationToFeature(String line, List<Token>tokens, Alphabet dataAlphabet) {
		// In general, this is a semantic parser. Here, we just match against a predetermined list of features
		return null;
	}

	private static String classifyType(List<Token> tokens, int labelTokenIdx, int featureTokenIdx){
		Preconditions.checkState(labelTokenIdx!=featureTokenIdx, "labelTokenIdx should not match featureTokenIdx");

		//Feature-generation, and classify with a statistical classifier
		//List<Double> fVec = extractConstraintTypeFeatures(tokens, labelTokenIdx);
		//For now, lets try a simple rule based system
		return (featureTokenIdx > labelTokenIdx) ? "YX" : "XY";
	}


	private static List<Double> extractConstraintTypeFeatures(List<Token> tokens, int labelTokenIdx) {
		ArrayList<Double> features = new ArrayList<Double>();
		//Relative occurrence of if, that
		return null;
	}
}