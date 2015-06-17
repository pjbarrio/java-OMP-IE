package sentence.similarity;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.JSONLoader;
import distance.measure.DistanceMeasure;
import distance.measure.impl.CosineSimilarity;
import feature.extractor.FeatureExtractor;
import feature.extractor.impl.POSFeatureExtractor;
import feature.extractor.impl.TokensFeatureExtractor;

public class OrderAdaptivelyBySimilarity {

	public static void main(String[] args) throws IOException {
		
		boolean limit = true;
		int my_size = 1000;
		
		System.setOut(new PrintStream(new File("data\\adaptive.csv")));
		
		int startpoints = 5;
		
		String[] relations = {"PersonParty"/*,"20131104-education-degree","20131104-date_of_birth","20131104-place_of_death",
				"20130403-place_of_birth","20130403-institution"*/};
		
		String[] tasks = {"Train"/*,"default","default","default","default","default"*/};
		
		ArrayList<FeatureExtractor<String>> fes = CompareSentences.loadFeatureExtractors();
		
		System.out.println("FeatureExtractor,Relation,Seed,ProcessedDocuments,Recall");
		
		for (int h = 0; h < relations.length; h++) {

			String relation = relations[h];
			String task = tasks[h];
			
			Map<String,List<String>> map = CompareSentences.readMap(relation, task);
			
			int maxSize = limit? Math.min(my_size, map.get("yes").size()) : Integer.MAX_VALUE; 
			
			DistanceMeasure m = new CosineSimilarity();

			int[] randoms = createRandoms(maxSize,startpoints);			
						
			for (FeatureExtractor<String> fe : fes) {

				fe.initialize(CompareSentences.getWordSetName(relation,task));
				
				Map<String, List<Map<Integer, Double>>> mVec = fe.createFeatureVectors(map, maxSize);
				
				//Obtain Starting Points
				
				List<Map<Integer,Double>> spoints = mVec.get("yes");

				//Populate matrix
				
				List<Map<Integer,Double>> useless = mVec.get("no");
				
				Map<Integer,Map<Integer,Double>> feats = new HashMap<Integer,Map<Integer,Double>>(spoints.size() + useless.size());
				
				boolean[] usef = new boolean[spoints.size() + useless.size()];
				
				int index = 0;
				
				for (int i = 0; i < spoints.size(); i++) {
					feats.put(index, spoints.get(i));
					usef[index] = true;
					index++;
				}
				
				for (int i = 0; i < useless.size(); i++) {
					feats.put(index, useless.get(i));
					usef[index] = false;
					index++;
				}
		
				double[][] dmatrix = new double[index][index];
				
				for (int i = 0; i < index-1; i++) {
					
					/*for (int j = 0; j <= index; j++) {
						dmatrix[i][j] = -1.0;
					}
					
					for (int j = index+1; j < index; j++) {
						
						dmatrix[i][j] = m.distance(feats.get(i), feats.get(j));
						
					}*/
					
					for (int j = 0; j < index; j++) {
						dmatrix[i][j] = m.distance(feats.get(i), feats.get(j));
					}
					
				}
				
				System.err.println("Relation Created...");
				
				for (int i = 0; i < randoms.length; i++) {
					
					int[] visitedIndexes = new int[spoints.size()];
					
					visitedIndexes[0] = randoms[i];
					
					int visited = 1;
					
					while(visited < spoints.size()){
						
						visitedIndexes[visited] = visitNext(dmatrix,visitedIndexes,usef);
						
						visited++;
						
					}
										
					System.err.println(calculateRPrecision(fe.getSimpleName() + "," + relation+","+randoms[i]+",", visitedIndexes,spoints.size(),usef));
					
				}

				
			}
						
		}
				
		
	}

	static int[] createRandoms(int maxIndex, int total) {
		
		int[] ret = new int[total];
		
		for (int i = 0; i < total; i++) {
			
			ret[i] = (int) Math.floor(Math.random()*maxIndex);
			
		}
		
		return ret;
	}

	private static int visitNext(double[][] dmatrix, int[] visitedIndexes, boolean[] usef) {
		
		//calculate distances to usef visitedIndexes
		
		double maxMeasure = 0.0;
		int index = 0;
		
		for (int i = 0; i < dmatrix.length; i++) {
			if (!contains(visitedIndexes,i)){
				double sum = 0.0;
				double count = 0.0;
				for (int j = 0; j < visitedIndexes.length; j++) {
					if (usef[visitedIndexes[j]]){
						sum+=dmatrix[i][visitedIndexes[j]];
						count++;
					}
				}
				if ((sum / count) > maxMeasure){
					maxMeasure = sum / count;
					index = i;
				}
			}
		}
		
		return index;
		
	}

	private static boolean contains(int[] vals, int val) {
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] == val)
				return true;
		}
		return false;
	}

	private static double calculateRPrecision(String prefix, int[] visitedIndexes, int recallUseful,
			boolean[] usef) {
		
		double total = 0.0;
		for (int i = 0; i < recallUseful; i++) {
			if (usef[visitedIndexes[i]])
				total++;
			System.out.println(prefix + i + "," + total/recallUseful);
		}
		
		return total/recallUseful;
	}
	
}
