package distance.measure;

import java.util.Map;

public abstract class DistanceMeasure {

	public abstract double distance(Map<Integer,Double> featV1, Map<Integer,Double> featV2);

}
