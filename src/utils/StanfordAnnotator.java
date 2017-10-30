package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Preconditions;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

public class StanfordAnnotator {

	private static Properties props = new Properties();
	private static StanfordCoreNLP pipeline;
	static{
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
		//RedwoodConfiguration.empty().capture(System.err).apply();
		pipeline = new StanfordCoreNLP(props);
	}
	
	public ArrayList<Token> tokenList = new ArrayList<Token>();
	public SemanticGraph dependencies;

	public StanfordAnnotator(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		
		for(CoreMap sentence: document.get(SentencesAnnotation.class)) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String originalForm = token.originalText(); 
				String word = token.get(TextAnnotation.class).toLowerCase();
				String lemma = token.get(LemmaAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				String ner = token.get(NamedEntityTagAnnotation.class);
				this.tokenList.add(new Token(originalForm, word, lemma, pos, ner));
			}
			this.dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			
		}
	}

	public static void main(String[] args){
		
		// active passive
		// negation
		// type classification
		StanfordAnnotator sa = new StanfordAnnotator("Viagara is not mentioned in spam emails");
		Preconditions.checkState(sa.dependencies!=null);
		SemgrexPattern pattern = SemgrexPattern.compile("{tag:/VB.*/}=verb >neg {}=negation [ >nsubj {}=subject | >nsubjpass {idx:1}=subject]");
	    SemgrexMatcher matcher = pattern.matcher(sa.dependencies);
	    
	    if(matcher.find()) {
	    		System.out.println("Found match!");
	    		String verb = matcher.getNode("verb").word();
	    		String negation = matcher.getNode("negation").word();
	    		String subject = matcher.getNode("subject").word();
	    		System.out.println("Verb: "+verb+"\nNegation: "+negation+"\nSubject: "+subject);
	    		System.out.println(matcher.getNode("subject").index()+" "+matcher.getNode("subject").beginPosition()+" "+matcher.getNode("subject").endPosition());
	    }
		
	    System.exit(0);
	    
		List<Token> tokens = new StanfordAnnotator("Barack Obama is the 44th president of the USA, and America's president.").tokenList;
		for(Token t:tokens){
			t.display();
		}
	}


}
