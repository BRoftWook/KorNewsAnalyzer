package koreanAnaly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import org.paukov.combinatorics.*;

public class TopicFinder {

	private static String date  = "150128";
	private static String dirPath = "C:/Users/태욱/Desktop/"+date+"/politics/";
	private static File dir = new File(dirPath);
	private static int numOfArticle = dir.list().length;

	public static void main(String[] args){

		//Object for making Vocabulary Set
		WordVector setOfVoca = new WordVector();
		WordVector titleVoca = new WordVector();
		WordVector[] tfOfArticle = new WordVector[numOfArticle];
		WordVector[] tfidfOfArticle = new WordVector[numOfArticle];
		WordVector setOfPerson = new WordVector();
		WordVector[] personInArticle = new WordVector[numOfArticle];
		TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
		JSONParser parser = new JSONParser();
		List<KoreanTokenizer.KoreanToken> parsed;
		List<KoreanTokenizer.KoreanToken> parsedTitle;

		String title = "";
		String content = "";
		for(int cnt=0; cnt<numOfArticle; cnt++){
			tfOfArticle[cnt] = new WordVector();
			tfidfOfArticle[cnt] = new WordVector();
			personInArticle[cnt] = new WordVector();
		}
		double[] idfVector;

		//Make Vocabulary Set for entire articles
		for(int i=0; i<numOfArticle; i++){

			try{
				Object obj = parser.parse(new FileReader(dirPath+"politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				title = (String) jsonObject.get("title");
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];
				System.out.println((i+1)+"번째 기사 제목 : "+title);
			}catch(Exception e){
				e.printStackTrace();
			}

			parsed = processor.tokenize(title + " " + content);
			parsedTitle = processor.tokenize(title);
			Object[] parsedResult = parsed.toArray();
			Object[] parsedTitleResult = parsedTitle.toArray();

			for(int cnt=0;cnt<parsedResult.length; cnt++){
				setOfVoca.putWord(parsedResult[cnt].toString());
			}
			for(int cnt=0;cnt<parsedTitleResult.length; cnt++){
				titleVoca.putWord(parsedTitleResult[cnt].toString());
			}
			try {
				setOfPerson.append(NewsAnaly.getNamesInArticle(content));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		//Vocabulary Set && Person Set for Today's News are Created
		String[] vocaSet = setOfVoca.toStringArray();
		String[] personSet = setOfPerson.toStringArray();
		idfVector = new double[vocaSet.length];

		//TermFreqVectors for each article
		for(int i=0; i<numOfArticle; i++){

			tfOfArticle[i].setVocaSet(vocaSet);

			try{
				Object obj = parser.parse(new FileReader(dirPath+"politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				title = (String) jsonObject.get("title");
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];
			}catch(Exception e){
				e.printStackTrace();
			}

			parsed = processor.tokenize(title + " " + content);
			parsedTitle = processor.tokenize(title);

			Object[] parsedResult = parsed.toArray();
			Object[] parsedTitleResult = parsedTitle.toArray();

			//Make Person Vector for each Article
			try {
				personInArticle[i].setVocaSet(personSet);
				personInArticle[i] = personInArticle[i].append(NewsAnaly.getNamesInArticle(content));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			//Calculate Term Frequency Vectors for each Article			
			for(int cnt=0; cnt<parsedResult.length; cnt++){
				String word = parsedResult[cnt].toString();

				for(int cnt2 = 0; cnt2 < parsedTitleResult.length; cnt2++){
					tfOfArticle[i].putWord(word);
				}

			}

			//Calculate DF for words in Vocabulary Set
			double[] tfVector = tfOfArticle[i].getTermFreqVector();

			for(int cnt=0; cnt<vocaSet.length; cnt++){
				if(tfVector[cnt] != 0){
					idfVector[cnt]++;
				}
			}
		}

		//Calculate IDF Vector
		for(int cnt=0; cnt<idfVector.length; cnt++){
			idfVector[cnt] = 1.0/idfVector[cnt];
		}

		//Calculate TF x IDF Vectors for each article
		for(int cnt=0; cnt<numOfArticle; cnt++){
			tfidfOfArticle[cnt].setVocaSet(vocaSet);

			double[] tf = tfOfArticle[cnt].getTermFreqVector();
			for(int cnt2=0; cnt2< vocaSet.length; cnt2++){			
				tf[cnt2] = tf[cnt2]*idfVector[cnt2];
			}

			tfidfOfArticle[cnt].setTermFreqSet(tf);		
		}
		
		//Matrix for finding cluster
		int[][] similar = new int[numOfArticle][numOfArticle];
		
		//Find similar articles
		for(int row=0; row<numOfArticle; row++){
			for(int col=0; col<numOfArticle; col++){
				double sim1 = WordVector.similarity(tfidfOfArticle[row],tfidfOfArticle[col]);
				double sim2 = WordVector.similarity(personInArticle[row],personInArticle[col]);
				double sim = (2*sim1 + sim2) / 3;
				if(sim > 0.2){
					
					similar[row][col] = 1;
					if(row!=col) System.out.println((row+1)+"과 "+(col+1)+"은 유사합니다. 유사도 : "+ sim);
				}
				else{
					similar[row][col] = 0;
				}
			}
		}
		
		System.gc();
		
		//Find Cluster of Articles
		String[] articles = new String[numOfArticle];
		List<Cluster> clusters = new ArrayList<Cluster>();
		
		for(int cnt=0; cnt<articles.length; cnt++){
			articles[cnt] = (cnt+1)+"";
		}

		ICombinatoricsVector<String> initialVector = Factory.createVector(articles);
		
		for(int k=numOfArticle; k>0; k--){
			// Create a simple combination generator to generate k-combinations of the initial vector
			Generator<String> gen = Factory.createSimpleCombinationGenerator(initialVector, k);
			// Generate all possible combinations
			for (ICombinatoricsVector<String> combination : gen) {
				Cluster c = new Cluster();
				int clusterChecker = 0;
				
				for(int e=0; e<k; e++){
					Integer articleNum = Integer.parseInt(combination.getValue(e));
					c.put(articleNum);
				}
				
				Integer[] elements = c.getElements();
				for(int row = 0; row<elements.length; row++){
					for(int col = 0; col<elements.length; col++){
						clusterChecker += similar[elements[row]-1][elements[col]-1];  //Minus 1 because Article Number Starts From 1
					}
				}
				//Avoid Creating Sub Cluster of Bigger Cluster
				if(clusterChecker == k*k){
					Object[] existCluster = clusters.toArray();
					int redunClusterCheck = 0;
					for(int cnt=0; cnt<existCluster.length; cnt++){
						Cluster existOne = (Cluster) existCluster[cnt];
						if(c.isSubsetOf(existOne))	redunClusterCheck ++;
					}
					if(redunClusterCheck == 0){
						c.print();
						clusters.add(c);
					}
				}
			}
		}

	}
}