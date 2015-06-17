package data.generation.target.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deeplearning4j.models.word2vec.Word2Vec;

import data.generation.target.TargetGeneration;
import data.generation.target.TargetGenerator;

import utils.wordmodel.DataGenerationParameterUtils;
import utils.wordmodel.MyWord2VecLoader;
import weka.core.Instances;

public abstract class FeatureSelection extends TargetGeneration{

	@Override
	public List<double[]> selectFeatures(Instances instances, String word2vec) throws Exception{
		
		instances = _selectFeatures(instances);
		
		Set<String> feats = new HashSet<String>();
		
		for (int i = 0; i < instances.numAttributes(); i++) {
			
			if (i != TargetGenerator.USEFUL_INDEX){
				
				feats.add(instances.attribute(i).name());
				
			}
			
		}
		
		List<double[]> ret = new ArrayList<double[]>();
		
		MyWord2VecLoader w2vl = new MyWord2VecLoader();
		
		boolean binary = DataGenerationParameterUtils.isBinary(word2vec);
		
		Word2Vec model = w2vl.loadModel(word2vec, binary,feats,false);
		
		for (String feat : feats) {
			
			ret.add(model.getWordVector(feat));
			
		}
		
		return ret;
		
	}
 	
	protected abstract Instances _selectFeatures(Instances instances) throws Exception;

	

}
