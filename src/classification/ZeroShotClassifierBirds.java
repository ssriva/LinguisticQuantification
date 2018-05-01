package classification;

import java.awt.Label;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import utils.MalletUtils;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntPRTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.MalletProgressMessageLogger;

public class ZeroShotClassifierBirds {

	static{
		((MalletLogger) MalletProgressMessageLogger.getLogger("Logger")).getRootLogger().setLevel(MalletLogger.LoggingLevels[2]);
	}

	public static void main(String[] args) {
		
		//Load data instances from file
		//InstanceList origInstances = MalletUtils.getInstancesFromTextFile("/Users/shashans/Work/tools/mallet-2.0.8/sample-data/numeric/zoo.txt", true);
		InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile("data/birds/datasets/164.libsvm");
		//InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile("data/shapes/datasets/features.alpha__0_1.dataset/features.alpha__0_1.160.dataset.all.instances.libsvm");
		//InstanceList origInstances = MalletUtils.getInstancesFromLibSVMFile(args[0]);

		
		System.out.println("Set of labels:");
		for(int i=0; i<origInstances.getTargetAlphabet().size();i++){
			System.out.println(origInstances.getTargetAlphabet().lookupObject(i));
		}
		
		System.out.println("Set of features:");
		for(int i=0; i<origInstances.getDataAlphabet().size();i++){
			System.out.println(i+" "+origInstances.getDataAlphabet().lookupObject(i));
		}
		

	
		for(String classLabel:Arrays.stream(origInstances.getTargetAlphabet().toArray()).map(Object::toString).collect(Collectors.toList())){

			InstanceList instances = MalletUtils.binarizeLabelsNew(origInstances, classLabel);
			
			
			//Define training algorithm
			MaxEntTrainer trainer = new MaxEntTrainer();
			/*
			MaxEntPRTrainer trainer = new MaxEntPRTrainer();
			String constraintFile = "data/birds/filtered_statements_processed/164.txt.filtered";
			//String constraintFile = "data/birds/individual_descriptions_processed/AOIYMAKATLETX__3Q5C1WP23N1FBCM7VT3GGERMDFH15K__46.filtered.txt.json.null.txt";
			HashMap<String, HashMap<String, Double>> constraintHashMap = ConstraintGeneratorBirds.parseFileToConstraints(instances,constraintFile);
			trainer.setConstraintsHashMap(constraintHashMap);
			trainer.setMinIterations(5);
			trainer.setMaxIterations(100);
			trainer.setPGaussianPriorVariance(0.1);
			trainer.setQGaussianPriorVariance(1000);
			trainer.setUseValues(false);
			*/

			runExperimentSplit(instances, trainer, 100, 0.10, classLabel);
			//runExperimentSplit(origInstances, trainer, 1, 0.7, classLabel);
			//runExperimentSplitTrainCompleteData(instances, trainer, 1, 0.0, classLabel);
			
			break;
		}
		
	}

	public static void runExperimentSplitTrainCompleteData(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){
	
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
		Evaluator.evaluateForClassLabel(trials, classLabel);
	}
	
	public static void runExperimentSplit(InstanceList instances, ClassifierTrainer trainer, int numTrials, double trainProp, String classLabel){
		
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

		Evaluator.evaluateForClassLabel(trials, classLabel);
	}

}
