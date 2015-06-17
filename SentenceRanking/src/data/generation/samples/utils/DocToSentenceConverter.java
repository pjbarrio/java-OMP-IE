package data.generation.samples.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import utils.SerializationHelper;
import utils.wordmodel.DataGenerationParameterUtils;

import com.google.gdata.util.common.base.Pair;

import data.generation.CreateSentences;
import data.generation.InputFilesGenerator;

public class DocToSentenceConverter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		InputFilesGenerator.resetLineSeparator();
		
		String task = args[0]; //e.g., "Train";
		String outprefix = args[1]; //e.g., "data/omp/test1/";
		String sampleName = args[2]; //e.g., "sample/...";
		
		Map<String,Pair<Integer,Integer>> docSentencesMap = (Map<String,Pair<Integer,Integer>>)SerializationHelper.deserialize(CreateSentences.getDocSentMapName(outprefix, task));

		List<String> sample = (List<String>)SerializationHelper.deserialize(sampleName);
		
		List<String> finalSample = new ArrayList<String>();
		
		for (String doc : sample) {
			
			Pair<Integer, Integer> bounds = docSentencesMap.get(doc);
			
			for (long i = (long)bounds.first; i <= (long)bounds.second; i++) {
				
				finalSample.add(Long.toString(i));
				
			}
			
		}
		
		FileUtils.writeLines(new File(sampleName.replaceAll(".ser", ".sample")), finalSample);
		
	}

}
