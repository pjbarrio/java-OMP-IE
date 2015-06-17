package evaluation;

import it.unimi.dsi.parser.Entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.NormalizedTuple;
import model.Tuple;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import data.generation.SentenceUsefulnessSplit;

import utils.SerializationHelper;
import utils.wordmodel.DataGenerationParameterUtils;

public class TuplesNovelty {

	private static final String CAND_SENTENCE_STRING = "candidate";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO The idea is that for each position, we can say if att_1, att_2, att_n is new (0,1)
		// Then in R, we can do lagged sums and stuff.

		String data = args[0]; //e.g., Train
		String word2vec = args[1]; //e.g., "C:/Users/Pablo/Downloads/GoogleNews-vectors-negative300.bin"
		String relation = DataGenerationParameterUtils.relations[Integer.valueOf(args[2])];// e.g., 0 for PersonParty;
		String extractor = args[3]; //e.g., "default";
		String outprefix = args[4]; //e.g., data/omp/test1/
		String file = getOutputFileName(outprefix,args[5]); //e.g., "toyresult.txt";
		String outputFile = getEvaluationOutputFileName(outprefix,args[5]);
		//TODO: Add other parameters?

		CSVReader reader = new CSVReader(new FileReader(file));

		//get candidate position

		String [] nextLine = reader.readNext();

		int i=0;

		while(!nextLine[i].trim().equals(CAND_SENTENCE_STRING)) i++;

		Map<Long,List<Tuple>> tuples = (Map<Long,List<Tuple>>)SerializationHelper.deserialize(outprefix + data + "." + relation + "." + extractor + SentenceUsefulnessSplit.TUPLES + ".ser");
		List<String> attributes = (List<String>)SerializationHelper.deserialize(outprefix + DataGenerationParameterUtils.getAttributesFileName(relation, extractor, data)); 
		
		Map<String,Set<String>> tuples_atts = new HashMap<String, Set<String>>();

		for (String att : attributes) {
			tuples_atts.put(att, new HashSet<String>());
			tuples_atts.put("norm_" + att, new HashSet<String>());
		}

		CSVWriter writer = new CSVWriter(new FileWriter(outputFile));
		
		List<String> entries = new ArrayList<String>();
		
		for (int j = 0; j < nextLine.length; j++) {
			if (j != i){
				entries.add(nextLine[j].trim());
			}
		}

		entries.add("relation");
		entries.add("extractor");
		entries.add("model");
		entries.add("data");
		entries.add("candidate");
		
		for (String att : tuples_atts.keySet()) {
			entries.add(att);
		}

		entries.add("is.novel");
		entries.add("novel");
		
		writer.writeNext(entries.toArray(new String[entries.size()]));
		
		
		List<String> prefix = new ArrayList<String>();
		
		prefix.add(relation);
		prefix.add(extractor);
		prefix.add(word2vec);
		prefix.add(data);
		
		String line[],sentence;

		List<NormalizedTuple> all_unique_tuples = new ArrayList<NormalizedTuple>();
		
		while ((line = reader.readNext()) != null) {

			int isNovel = 0;
			
			sentence = line[i].trim();

			entries.clear();

			for (int j = 0; j < line.length; j++) {
				if (j != i){
					entries.add(line[j].trim());
				}
			}

			entries.addAll(prefix);

			entries.add(sentence);
			
			List<Tuple> tups = tuples.get(Long.valueOf(sentence));

			if (tups != null){

				for (Tuple tuple : tups) {

					NormalizedTuple n_tuple = (NormalizedTuple) tuple;

					boolean added = false;
					
					for (String field : tuple.getFieldNames()) {
						tuples_atts.get(field).add(tuple.getFieldValue(field));
						added = tuples_atts.get("norm_" + field).add(n_tuple.getNormalizedFieldValue(field));
					}

					if (added || isNovel(all_unique_tuples,n_tuple)){
						all_unique_tuples.add(n_tuple);
						isNovel = 1;
					}
					
				}

			}

			for (Entry<String,Set<String>> att_entry : tuples_atts.entrySet()) {
				entries.add(Integer.toString(att_entry.getValue().size()));
			}

			entries.add(Integer.toString(isNovel));
			entries.add(Integer.toString(all_unique_tuples.size()));
			
			writer.writeNext(entries.toArray(new String[entries.size()]));
			
		}

		reader.close();
		writer.close();		

	}

	private static boolean isNovel(List<NormalizedTuple> all_tuples,
			NormalizedTuple n_tuple) {
		
		boolean novel = true;
		
		for (int i = 0; i < all_tuples.size(); i++) {
			
			if (isSameTuple(all_tuples.get(i),n_tuple)){
				return false;
			}
			
		}
		
		return novel;
	}

	private static boolean isSameTuple(NormalizedTuple existing_tuple,
			NormalizedTuple new_tuple) {
		
		if (existing_tuple.getFieldNames().length < new_tuple.getFieldNames().length)
			return false;
		
		for (String field : new_tuple.getFieldNames()) {

			String existing_value = existing_tuple.getFieldValue(field);
			
			if (existing_value == null){

				return false;

			}else{ //The field exists

				//Need to check if it refers to the same (normalized) entity
				String new_value = new_tuple.getNormalizedFieldValue(field);
				
				if (NormalizedTuple.NON_NORMALIZABLE.equals(new_value)){
					
					new_value = new_tuple.getFieldValue(field);
					
				} else{
					
					existing_value = existing_tuple.getNormalizedFieldValue(field);
					
				}
				
				//what about existing_value being non_normalizable? Next condition will say FALSE.
				
				if (!existing_value.equals(new_value)) //it's all in lowercase
					return false;

			}

		}
		
		return true;
		
	}

	public static String getEvaluationOutputFileName(String outprefix,
			String fileName) {
		
		new File(outprefix + "evaluation/").mkdirs();
				
		return outprefix + "evaluation/" + fileName + ".csv";
	}

	public static String getOutputFileName(String outprefix, String fileName) {
		return outprefix + "output/" + fileName;
	}

}
