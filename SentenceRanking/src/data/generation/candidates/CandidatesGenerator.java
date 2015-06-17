package data.generation.candidates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import utils.SerializationHelper;
import utils.wordmodel.DataGenerationParameterUtils;
import weka.core.Instances;
import weka.core.Utils;

import data.generation.InputFilesGenerator;
import data.generation.SentenceUsefulnessSplit;
import data.generation.feature.AttributeEvaluation;
import data.generation.feature.PrecisionBasedAttributeEvaluation;

public class CandidatesGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		InputFilesGenerator.resetLineSeparator();

		String data = args[0];
		String relation = DataGenerationParameterUtils.relations[Integer.valueOf(args[1])];
		String extractor = args[2];
		String word2Name = new File(args[3]).getName();
		String outprefix = args[4]; 
		String sampleMethod = args[5];

		//For FromSplit, size (args[6]) , fractionuseful (args[7]), and seed (args[8])

		//For DocumentSampler, size (args[6]), sampling_algorithm (args[7]) , split (args[8]), docsPerQuerySample (args[9]), and numQueries (args[10]) 

		Set<String> sample = new HashSet<String>(FileUtils.readLines(new File(getSampleFile(outprefix,sampleMethod,data,relation,extractor,word2Name,Arrays.copyOfRange(args, 6, args.length)))));

		BufferedReader candidates = new BufferedReader(new FileReader(InputFilesGenerator.getCandidatesFileName(outprefix, data, word2Name)));

		BufferedReader usefulness = new BufferedReader(new FileReader(SentenceUsefulnessSplit.getUsefulnessFile(outprefix, data, relation, extractor)));

		Map<Integer,String> invertedIndexMap = (Map<Integer,String>)SerializationHelper.deserialize(InputFilesGenerator.getInvertedIndexMap(outprefix, data, word2Name));

		String dataFile = getDataFile(outprefix,sampleMethod,data,relation,extractor,word2Name,Arrays.copyOfRange(args, 6, args.length));

		StringBuilder sb = new StringBuilder();

		sb.append("@DATA\n");

		String lcand,lusef;

		Map<Integer,Integer> featIndex = new HashMap<Integer,Integer>();
		Map<Integer,Integer> invertedFeatIndex = new HashMap<Integer,Integer>();

		while ((lcand = candidates.readLine())!=null){

			lusef = usefulness.readLine();

			String cand = lusef.split(" ")[0];

			if(!sample.contains(cand))
				continue;

			String[] feats = lcand.split(" ");

			int[] featsI = new int[feats.length - 1];

			for (int i = 1; i < feats.length; i++) {

				Integer val = Integer.valueOf(feats[i]);

				Integer index = featIndex.get(val);

				if (index == null){
					index = featIndex.size()+1;
					featIndex.put(val, index);
					invertedFeatIndex.put(index, val);
				}

				featsI[i-1] = index;

			}

			Arrays.sort(featsI);

			sb.append("{");

			int us = Integer.valueOf(lusef.split(" ")[1]);

			sb.append(0 + " " + us);

			for (int i = 0; i < featsI.length; i++) {

				sb.append("," + featsI[i] + " " + 1);

			}

			sb.append("}\n");

		}

		candidates.close();
		usefulness.close();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataFile)));

		bw.write("@RELATION " + relation + "-" + extractor);
		bw.newLine();

		bw.write("@ATTRIBUTE usefulness-class {0,1}");
		bw.newLine();

		for (int i = 1; i <= invertedFeatIndex.size(); i++) {

			bw.write("@ATTRIBUTE "+ Utils.quote(invertedIndexMap.get(invertedFeatIndex.get(i))) +" NUMERIC");
			bw.newLine();

		}

		bw.write(sb.toString());

		bw.close();

		//TODO: Should also save the full (i.e., no selection) indexes.

		/* I commented this out just to be able to run...		

		double threshold = 0.5;	

		BufferedReader reader = new BufferedReader(
				new FileReader(dataFile));

		Instances instances = new Instances(reader);

		instances.setClass(instances.attribute(0));

		AttributeEvaluation attEval = new PrecisionBasedAttributeEvaluation(instances,threshold);

		List<Integer> allIndexes = new ArrayList<Integer>();

		for (int i = 0; i < instances.numAttributes()-1; i++) { //-1 because of the class index

			if (attEval.keep(i)){
				allIndexes.add(i);
			}

		}

		SerializationHelper.serialize(outprefix + data + "." + relation + "." + extractor + "." + "FULL" + "." + "FULL" + "." + InputFilesGenerator.CANDIDATE + ".ser", allIndexes);

		 */

	}

	public static String getDataFile(String outprefix, String sampleMethod,
			String data, String relation, String extractor, String word2Name,
			String[] params) {

		new File(outprefix + "data/").mkdirs();
				
		return outprefix + "data/" + getSampleName(sampleMethod,data,relation,extractor,word2Name, params) + ".arff";

	}

	private static String getSampleFile(String outprefix, String sampleMethod, String data, String relation, String extractor, String word2Name, String[] params) {

		
		return outprefix + "sample/" + getSampleName(sampleMethod, data, relation, extractor, word2Name, params) + ".sample";

	}

	public static String getSampleName(String sampleMethod, String data, String relation, String extractor, String word2Name, String[] params) {

		if (sampleMethod.equals("FromSplit")){

			String ret = data + "." + sampleMethod + "." + relation + "." + extractor;

			for (int i = 0; i < params.length; i++) {

				ret = ret + "." + params[i];

			}

			return ret;
		}

		return null;
	}

}
