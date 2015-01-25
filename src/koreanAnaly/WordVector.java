package koreanAnaly;

import java.util.HashMap;


public class WordVector {
	private static int numVoca;
	private static HashMap<String,Integer> termFreqVector = new HashMap<String, Integer>();

	public WordVector(){
		numVoca = 0;
	}

	public void putWord(String word){
		//Only use Noun Words
		if(word.contains("Noun")){
			if(!termFreqVector.containsKey(word)){
				termFreqVector.put(word, 1);
				numVoca++;
			}

			else{
				termFreqVector.put(word, termFreqVector.get(word) + 1);
			}
		}
		else{

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
		int cnt = 0;
		for(String key : termFreqVector.keySet()){
			vocaSet[cnt] = key;
			cnt++;
		}
		return vocaSet;
	}
}
