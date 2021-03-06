package classification;

import java.awt.Label;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import utils.MalletUtils;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntPRTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.MalletProgressMessageLogger;

public class ZeroShotClassifier {

	static{
		((MalletLogger) MalletProgressMessageLogger.getLogger("Logger")).getRootLogger().setLevel(MalletLogger.LoggingLevels[2]);
	}

	public static void main(String[] args) {
		Boolean trainPRClassifier=true;
		int numTrials=10;
		//learnClassifierFromDescriptionAndDataset(args[0], args[1], trainPRClassifier,numTrials);
		learnClassifierSuit(args[0],trainPRClassifier,numTrials);
	}
	
	public static void learnClassifierSuit(String dataAndDescriptionFiles, Boolean trainPRClassifier, int numTrials) {
		try {
			ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(Paths.get(dataAndDescriptionFiles));
			for(String line:lines) {
				String[] toks = line.split("\\s+");
				System.out.println("Learning classifier for "+toks[0]+" from descriptions "+toks[1]);
				learnClassifierFromDescriptionAndDataset(toks[0], toks[1], trainPRClassifier, numTrials);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void learnClassifierFromDescriptionAndDataset(String libSVMFile, String constraintFile, Boolean trainPRClassifier, int numTrials) {

		//String libSVMFile = args[0];
		//Load data instances from file
		//InstanceList origInstances = MalletUtils.getInstancesFromTextFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo.txt", true);
		//InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile("data/zoo/zoo_features.libsvm");
		//InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile("data/shapes/datasets/features.alpha__0_1.dataset/features.alpha__0_1.160.dataset.all.instances.libsvm");
		InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile(libSVMFile);


		System.out.println("Set of labels:");
		for(int i=0; i<origInstances.getTargetAlphabet().size();i++){
			System.out.println(origInstances.getTargetAlphabet().lookupObject(i));
		}

		System.out.println("Set of features:");
		for(int i=0; i<origInstances.getDataAlphabet().size();i++){
			System.out.println(i+" "+origInstances.getDataAlphabet().lookupObject(i));
		}

		//HashMap<String, HashMap<String, Double>> constraintHashMap = ConstraintGenerator.parseFileToConstraints(origInstances,"/Users/shashans/Desktop/descriptions.txt");


		//String classLabel = "mammal";

		for(String classLabel:Arrays.stream(origInstances.getTargetAlphabet().toArray()).map(Object::toString).collect(Collectors.toList())){

			InstanceList instances = MalletUtils.binarizeLabelsNew(origInstances, classLabel);

			/*
			Instance inst1 = origInstances.get(0);
			Instance inst2 = instances.get(0);
			System.out.println("INST1");
			System.out.println(inst1.getTargetAlphabet());
			System.out.println("INST2");
			System.out.println(inst2.getTargetAlphabet());
			Preconditions.checkState(inst1.getAlphabet().equals(inst2.getAlphabet()),"Error1");
			Preconditions.checkState(inst1.getData().equals(inst2.getData()),"Error2");
			Preconditions.checkState(inst1.getDataAlphabet().equals(inst2.getDataAlphabet()),"Error3");
			Preconditions.checkState(inst1.getLabeling().equals(inst2.getLabeling()),"Error4");
			Preconditions.checkState(inst1.getName().equals(inst2.getName()),"Error5");
			Preconditions.checkState(inst1.getTarget().equals(inst2.getTarget()),"Error6");
			Preconditions.checkState(inst1.getTargetAlphabet().equals(inst2.getTargetAlphabet()),"Error7");
			//Preconditions.checkState(inst1.hashCode() == inst2.hashCode(),"Error8");
			//Preconditions.checkState(inst1..equals(inst2.getTargetAlphabet()),"Error7");		
			//System.exit(0);
			 */

			//Define training algorithm
			/**/
			ClassifierTrainer trainer;
			if(!trainPRClassifier) {
				trainer = new MaxEntTrainer();
				//MaxEntTrainer trainer = new MaxEntTrainer();
			}else {
				trainer = new MaxEntPRTrainer();
				//MaxEntPRTrainer trainer = new MaxEntPRTrainer();
				//String constraintFile = "data/zoo/descriptions/"+classLabel+".txt";
				//String constraintFile = args[1]; //"data/shapes/descriptions/features.alpha__0_1.160.A3MF31JQ350ABS.3SUWZRL0MZDAEHGXC080F4OGK0B6EN.txt";
				HashMap<String, HashMap<String, Double>> constraintHashMap = ConstraintGenerator.parseFileToConstraints(instances,constraintFile);
				//trainer.setConstraintsFile("/Users/shashans/Work/tools/mallet-2.0.8/constraints.txt");
				((MaxEntPRTrainer) trainer).setConstraintsHashMap(constraintHashMap);
				((MaxEntPRTrainer) trainer).setMinIterations(5);
				((MaxEntPRTrainer) trainer).setMaxIterations(100);
				((MaxEntPRTrainer) trainer).setPGaussianPriorVariance(0.1);
				((MaxEntPRTrainer) trainer).setQGaussianPriorVariance(1000);
				((MaxEntPRTrainer) trainer).setUseValues(false);
			}
			
			/**/
			//runExperimentSplit(origInstances, trainer, numTrials, 0.7, classLabel);
			
			double acc;
			if(!trainPRClassifier) {
				acc = runExperimentSplit(instances, trainer, numTrials, 0.05, classLabel);
			}else {
				acc = runExperimentSplitTrainCompleteData(instances, trainer, numTrials, 0.0, classLabel);
			}
			
			System.out.println(libSVMFile+"\t"+constraintFile+"\tAVG_ACCURACY:\t"+acc);
			break;
		}

	}

	public static double runExperimentSplitTrainCompleteData(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){

		Trial[] trials = new Trial[numTrials];

		for(int i=0; i< numTrials; i++){
			//Test train split
			InstanceList[] instanceLists = MalletUtils.testTrainSplit(instances, trainProp);		
			InstanceList trainingInstances = instances;
			InstanceList testInstances = instanceLists[1];
			System.out.println("Size of training set: "+trainingInstances.size()+"\nSize of test set: "+testInstances.size());

			//Train and evaluate model			
			MaxEnt classifier = (MaxEnt) trainer.train(trainingInstances);
			trials[i] = new Trial(classifier, testInstances);
		}

		//Evaluator.evaluateMultiClasses(trials);
		return Evaluator.evaluateForClassLabel(trials, classLabel);
	}

	public static double runExperimentSplit(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){

		Trial[] trials = new Trial[numTrials];

		for(int i=0; i< numTrials; i++){
			//Test train split
			InstanceList[] instanceLists = MalletUtils.testTrainSplit(instances, trainProp);		
			InstanceList trainingInstances = instanceLists[0];
			InstanceList testInstances = instanceLists[1];
			System.out.println("Size of training set: "+trainingInstances.size()+"\nSize of test set: "+testInstances.size());

			//Train and evaluate model			
			MaxEnt classifier = (MaxEnt) trainer.train(trainingInstances);
			trials[i] = new Trial(classifier, testInstances);
		}

		//Evaluator.evaluateMultiClasses(trials);
		return Evaluator.evaluateForClassLabel(trials, classLabel);
	}

}
