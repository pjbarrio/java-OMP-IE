package data.generation.target;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import utils.wordmodel.DataGenerationParameterUtils;
import weka.core.Instances;
import data.generation.InputFilesGenerator;
import data.generation.candidates.CandidatesGenerator;
import data.generation.samples.FromSplitSampleGenerator;
import data.generation.target.selection.UsefulOnlyFeatureSelection;

public class TargetGenerator {

	public static int USEFUL_INDEX = 0;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		InputFilesGenerator.resetLineSeparator();
		
		String data = args[0];
		String relation = DataGenerationParameterUtils.relations[Integer.valueOf(args[1])];
		String extractor = args[2];
		String word2vec = args[3];
		String word2Name = new File(word2vec).getName();
		String outprefix = args[4]; 
		String targetGenerationMethod = args[5];
		String sampleMethod = args[6];
		
		//For FromSplit, size (args[6]) , fractionuseful (args[7]), and seed (args[8])

		//For DocumentSampler, size (args[6]), sampling_algorithm (args[7]) , split (args[8]), docsPerQuerySample (args[9]), and numQueries (args[10]) 

		String dataFile = CandidatesGenerator.getDataFile(outprefix,sampleMethod,data,relation,extractor,word2Name,Arrays.copyOfRange(args, 7, args.length));

		BufferedReader reader = new BufferedReader(
				new FileReader(dataFile));

		Instances instances = new Instances(reader);
		instances.setClass(instances.attribute(USEFUL_INDEX));

		TargetGeneration t = getTargetGenerationMethod(targetGenerationMethod);

		String targetOutput = getTargetOutputName(outprefix, targetGenerationMethod, sampleMethod, data, relation, extractor, word2Name, Arrays.copyOfRange(args, 7, args.length));		

		BufferedWriter bw = new BufferedWriter(new FileWriter(targetOutput));
		
		List<double[]> list = t.selectFeatures(instances, word2vec);
		
		boolean first = true;
		
		for (double[] ds : list) {
			
			if (!first)
				bw.newLine();
			
			bw.write(InputFilesGenerator.prettyPrint(ds));
						
			first = false;
		}
		
		bw.close();
		
	}

	private static String getTargetOutputName(String outprefix, String targetGenerator, String sampleMethod, String data, String relation, String extractor, String word2Name, String[] params) {

		new File(outprefix + "target/").mkdirs();
				
		return outprefix + "target/" + targetGenerator + "." + word2Name + "." + CandidatesGenerator.getSampleName(sampleMethod, data,
				relation, extractor, word2Name, params) + ".target";

	}

	private static TargetGeneration getTargetGenerationMethod(
			String targetGenerationMethod) {
		if (UsefulOnlyFeatureSelection.class.getSimpleName().equals(targetGenerationMethod)){
			return new UsefulOnlyFeatureSelection();
		}
		return null;
	}

}
