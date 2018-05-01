package classification;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.primitives.Ints;

import utils.QuantifierExpressions;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.stanford.nlp.util.ArrayUtils;

public class QuantificationConstraintBirds {
	
	private String text;
	private InstanceList instances;
	private int yIdx;
	private int xIdx;
	private String type;
	private String quantifierExpression;
	private Boolean featureNegation;
	private Boolean labelNegation;
	private HashMap<String, Double> probs = new HashMap<String, Double>();;
	private double p;
	
	public QuantificationConstraintBirds(String line, InstanceList instances, int yIdx, int xIdx, String type, String quantifierExpression, Boolean featureNegation, Boolean labelNegation){
		
		this.text = line;
		this.instances = instances;
		this.yIdx = yIdx;
		this.xIdx = xIdx;
		this.type = type;
		this.quantifierExpression = quantifierExpression;
		this.featureNegation = featureNegation;
		this.labelNegation = labelNegation;
		
		if(featureNegation) {
			this.xIdx = this.xIdx + 1;
		}
	}
	
	protected HashMap<String, Double> validateAndSetProbabilities(){	
		//Populate hashmap with probability values for all target labels
		String label = (String) instances.getTargetAlphabet().lookupObject(this.yIdx);
		p = QuantifierExpressions.quantExpressionProbs.get(this.quantifierExpression);
		System.out.println("QExp: "+this.quantifierExpression+" corresponds to prob "+p);
		

		/**/
		if(this.type.equals("XY")) {
			
			int found = 0;
			for(Instance inst:instances) {
				int[] indices = ((FeatureVector) inst.getData()).getIndices();
				double[] values = ((FeatureVector) inst.getData()).getValues();
				if(Ints.contains(indices, xIdx) && values[Ints.indexOf(indices, xIdx)]!=0) {
					found++;
				}
			}
			
			double px = 1.0 * found/instances.size();  //1.0 * instances.stream().filter( inst -> Arrays.stream(((FeatureVector) inst.getData()).getIndices()).anyMatch(((Integer) xIdx)::equals)).count()/instances.size();
			double py = 1.0 * instances.stream().filter( inst -> inst.getLabeling().toString().equals( (String) instances.getTargetAlphabet().lookupObject(yIdx)) ).count()/instances.size();
			py = this.labelNegation ? 1.0 - py : py;
			p = p * py / px; 
			System.out.println("px = "+px+"\npy = "+py+" \np(y|x) = "+p);
			p = Math.min(p, 1.0);
		}/**/
		
		p = this.labelNegation ? 1.0 - p : p;
		probs.put(label, p);
		
		//Rest on prob on THE other label (and also need to empty other labels)
		int alterIdx = (this.yIdx!=0) ? 0 : 1;
		String alterLabel = (String) instances.getTargetAlphabet().lookupObject(alterIdx);
		probs.put(alterLabel, 1.0 - p);
		
		for(int i=0; i<instances.getTargetAlphabet().size(); i++){
			if(i!=this.yIdx && i!=alterIdx){
				String label1 = (String) instances.getTargetAlphabet().lookupObject(i);
				probs.put(label1, 0.0);
			}
		}
		
		//For now, let's assume a uniform distribution over other labels
		/*
		double p1 = (1.0 - p)/(instances.getTargetAlphabet().size()-1);
		System.out.println("Other labels have probability "+p1);
		for(int i=0; i<instances.getTargetAlphabet().size(); i++){
			if(i!=this.yIdx){
				String label1 = (String) instances.getTargetAlphabet().lookupObject(i);
				probs.put(label1, p1);
			}
		}
		*/
		
		this.display();
		return probs;
	}
	
	public void display() {
		System.out.println("Sentence: "+this.text);
		System.out.println("xIdx: "+this.xIdx);
		if(this.type.equals("YX")){
			System.out.println("Type Y|X : P ( y = true | "+instances.getDataAlphabet().lookupObject(this.xIdx) + ") = " + this.p);
		}else {
			System.out.println("Type X|Y, which maps to : P ( y = true | "+instances.getDataAlphabet().lookupObject(this.xIdx) + ") = " + this.p);
		}
	}

	public InstanceList getInstances() { return this.instances; }
	public int getyIdx() { return yIdx; }
	public int getxIdx() { return xIdx; }
	public String getType() {return type; }
	public String getQuantifierExpression() {return quantifierExpression;}
	public Boolean getFeatureNegation() { return featureNegation; }
	public Boolean getLabelNegation() { return labelNegation; }
	public HashMap<String, Double> getProbs() {	return probs;}
	
}