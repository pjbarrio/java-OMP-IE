package feature.extractor.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import feature.extractor.FeatureExtractor;

public class TokensFeatureExtractor extends FeatureExtractor<String> {

	@Override
	protected Map<String, Double> getFeatures(String snippet) {

		Map<String, Double> feats = new HashMap<String, Double>();

		String[] sentences = sfe.getFeatureSequences(snippet);

		for (String sentence : sentences) {

			String[] tokens = getFeatureSequences(sentence);

			for (String token : tokens) {

				feats.put(token, 1.0);

			}

		}

		return feats;

	}

	@Override
	protected String[] getFeatureSequences(String input) {
		return getTokenizer().tokenize(input);
	}

	@Override
	public void initialize(String relation) {
		;
	}



}
