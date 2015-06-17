package feature.extractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import feature.extractor.impl.SentenceFeatureExtractor;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public abstract class FeatureExtractor<E> {

	protected static SentenceFeatureExtractor sfe = new SentenceFeatureExtractor();
	
	private static Tokenizer tokenizer;
	
	public Map<String, List<Map<Integer,Double>>> createFeatureVectors(
			Map<String, List<String>> map, int size){
		
		System.err.println(size);
		
		Map<String,Integer> featId = new HashMap<String,Integer>();
		
		Map<String,List<Map<Integer,Double>>> ret = new HashMap<String, List<Map<Integer,Double>>>();
		
		for (Entry<String,List<String>> entry : map.entrySet()) {
			
			List<Map<Integer,Double>> feats = new ArrayList<Map<Integer,Double>>();
			
			List<String> texts = entry.getValue();
			
			if (size < texts.size()){
				
				Collections.shuffle(entry.getValue(), new Random(size)); //just so I use the same seed.
				
				texts = entry.getValue().subList(0, size);

			}
			
			for (String snippet : texts) {
				
				Map<String,Double> featsMap = getFeatures(snippet);
				
				Map<Integer, Double> finFeat = new HashMap<Integer,Double>(featsMap.size());
				
				for (Entry<String, Double> feat : featsMap.entrySet()) {
					
					Integer id = featId.get(feat.getKey());
					
					if (id == null){
						id = featId.size();
						featId.put(feat.getKey(), id);
					}
					
					finFeat.put(id, feat.getValue());
					
				}
				
				feats.add(finFeat);
				
			}
			
			ret.put(entry.getKey(), feats);
			
		}
		
		return ret;
		
	}

	protected abstract Map<String, Double> getFeatures(String snippet);

	protected abstract String[] getFeatureSequences(E input);
	
	protected Tokenizer getTokenizer() {

		if (tokenizer == null){

			try {
				InputStream modelIn = new FileInputStream("model\\en-token.bin");
				TokenizerModel model = new TokenizerModel(modelIn);
				tokenizer = new TokenizerME(model);
				modelIn.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		} 

		return tokenizer;

	}

	public Map<String, List<Map<Integer, Double>>> createFeatureVectors(
			Map<String, List<String>> map) {
		return createFeatureVectors(map, Integer.MAX_VALUE);
	}

	public String getSimpleName() {
		return this.getClass().getSimpleName();
	}

	public abstract void initialize(String relation);

	
}
