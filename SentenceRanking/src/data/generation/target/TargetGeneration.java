package data.generation.target;

import java.util.List;

import weka.core.Instances;

public abstract class TargetGeneration {
 
	public abstract List<double[]> selectFeatures(Instances instances, String word2vec)
			throws Exception;

}
