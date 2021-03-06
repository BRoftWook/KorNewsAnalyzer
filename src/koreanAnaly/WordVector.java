package koreanAnaly;

import java.util.HashMap;

public class WordVector {
	private int numVoca;
	private HashMap<String,Double> termFreqVector = new HashMap<String, Double>();

	public WordVector(){
		numVoca = 0;
	}

	public HashMap<String,Double> getVector(){
		return termFreqVector;
	}
	public void putWord(String word){
		//Only use Noun Words
		if(word.contains("Noun") && !word.contains("*") && word.length() > 5){
			if(!termFreqVector.containsKey(word)){
				termFreqVector.put(word, 1.0);
				numVoca++;
			}

			else{
				termFreqVector.put(word, termFreqVector.get(word) + 1.0);
			}
		}
	}

	public void putName(String word){
		if(!termFreqVector.containsKey(word)){
			termFreqVector.put(word, 1.0);
			numVoca++;
		}

		else{
			termFreqVector.put(word, termFreqVector.get(word) + 1.0);
		}
	}

	public int getNumOfVoca(){
		return numVoca;
	}

	public void flush(){
		termFreqVector.clear();
	}

	public void print(){
		for(String key : termFreqVector.keySet()){
			System.out.println(key + " : "+termFreqVector.get(key));
		}
	}

	public String[] toStringArray(){
		String[] vocaSet = new String[numVoca];

		for(int cnt = 0; cnt< termFreqVector.keySet().size(); cnt++){
			String key = termFreqVector.keySet().toArray()[cnt].toString();
			vocaSet[cnt] = key;
		}
		return vocaSet;
	}

	public double[] getTermFreqVector(){
		double[] termVector = new double[numVoca];

		for(int cnt = 0; cnt< numVoca; cnt++){
			double value = termFreqVector.get(termFreqVector.keySet().toArray()[cnt]);
			termVector[cnt] = value;
		}
		return termVector;
	}

	public void setVocaSet(String[] vocaSet){
		for(int cnt=0; cnt<vocaSet.length; cnt++){
			termFreqVector.put(vocaSet[cnt],0.0);
		}
		numVoca = vocaSet.length;
	}

	public void setTermFreqSet(double[] termFreqSet){
		for(int cnt=0; cnt<termFreqSet.length; cnt++){
			String word = (String) termFreqVector.keySet().toArray()[cnt];
			termFreqVector.put(word, termFreqSet[cnt]);
		}
	}

	private static double vectorSize(double[] tfidf){
		double size = 0;
		for(int cnt=0; cnt<tfidf.length; cnt++){
			size += tfidf[cnt]*tfidf[cnt];
		}
		size = Math.sqrt(size);
		return size;
	}

	public static double similarity(WordVector wv1, WordVector wv2){
		double similarity = 0;
		double[] tfidf1 = wv1.getTermFreqVector();
		double[] tfidf2 = wv2.getTermFreqVector();
		for(int cnt=0; cnt<wv1.getNumOfVoca(); cnt++){
			similarity += tfidf1[cnt] * tfidf2[cnt];
		}
		similarity = similarity / (vectorSize(tfidf1)*vectorSize(tfidf2));
		return similarity;
	}

	public static double similarity(double[] tfidf1, double[] tfidf2){
		double similarity = 0;
		for(int cnt=0; cnt<tfidf1.length; cnt++){
			similarity += tfidf1[cnt] * tfidf2[cnt];
		}
		similarity = similarity / (vectorSize(tfidf1)*vectorSize(tfidf2));
		return similarity;
	}
	
	public static double jacqSimilarity(WordVector personVector1, WordVector personVector2){
		
		double sim = 0;
		double numOfPerson1 = 0;
		double numOfPerson2 = 0;
		double interSection = 0;
		String[] setOfPerson = personVector1.toStringArray();
		double[] personFreq1 = personVector1.getTermFreqVector();
		double[] personFreq2 = personVector2.getTermFreqVector();
		for(int cnt=0; cnt<setOfPerson.length; cnt++){
			if(personFreq1[cnt] != 0) numOfPerson1++;
			if(personFreq2[cnt] != 0) numOfPerson2++;
			if(personFreq1[cnt] != 0 && personFreq2[cnt] != 0) interSection++;
		}
		sim = interSection / (numOfPerson1 + numOfPerson2);
		
		return sim;
	}

	public static String printVector(double[] pv){
		String result = "";
		for(int cnt=0; cnt<pv.length; cnt++){
			result += pv[cnt]+",";
		}
		return result;
	}

	public static String printVector(String[] pv){
		String result = "";
		for(int cnt=0; cnt<pv.length; cnt++){
			result += pv[cnt]+",";
		}
		return result;
	}

	public WordVector append(WordVector wv){
		String[] toBeAddedVoca = wv.toStringArray();
		double[] toBeAddedFreq = wv.getTermFreqVector();
		for(int cnt=0; cnt<toBeAddedVoca.length; cnt++){
			if(termFreqVector.get(toBeAddedVoca[cnt])!=null){
				termFreqVector.put(toBeAddedVoca[cnt], termFreqVector.get(toBeAddedVoca[cnt]) + toBeAddedFreq[cnt]);
			}
			else{
				termFreqVector.put(toBeAddedVoca[cnt], 1.0);
				numVoca++;
			}
		}
		return this;
	}
}
