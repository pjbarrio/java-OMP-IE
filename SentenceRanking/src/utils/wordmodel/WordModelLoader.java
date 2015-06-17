package utils.wordmodel;

import java.io.IOException;
import java.util.Set;

public abstract class WordModelLoader<T> {

	public abstract T loadModel(String path, boolean binary, double fraction)
			throws IOException;

	public abstract T loadModel(String path, boolean binary, Set<String> wordSet,
			boolean normalized) throws IOException;

	public abstract Set<String> loadDictionary(String path, boolean binary) throws IOException;
	
}
