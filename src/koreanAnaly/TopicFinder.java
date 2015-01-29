package koreanAnaly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class TopicFinder {

	private static String date  = "150126";
	private static String dirPath = "C:/Users/태욱/Desktop/"+date+"/politics/";
	private static File dir = new File(dirPath);
	private static int numOfArticle = dir.list().length;


	public static void main(String[] args){

		//Object for making Vocabulary Set
		WordVector setOfVoca = new WordVector();
		WordVector[] tfOfArticle = new WordVector[numOfArticle];
		WordVector[] tfidfOfArticle = new WordVector[numOfArticle];
		for(int cnt=0; cnt<numOfArticle; cnt++){
			tfOfArticle[cnt] = new WordVector();
			tfidfOfArticle[cnt] = new WordVector();
		}
		double[][] tfidf;
		double[] idfVector;

		//Make Vocabulary Set for entire articles
		for(int i=0; i<numOfArticle; i++){

			JSONParser parser = new JSONParser();
			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
			List<KoreanTokenizer.KoreanToken> parsed;
			String title = "";
			String content = "";

			try{

				Object obj = parser.parse(new FileReader(dirPath+"politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				title = (String) jsonObject.get("title");
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];
				//				System.out.println((i+1)+"번째 기사 제목 : "+title);
			}catch(Exception e){
				e.printStackTrace();
			}

			parsed = processor.tokenize(content);
			Object[] parsedResult = parsed.toArray();

			for(int cnt=0;cnt<parsedResult.length; cnt++){
				setOfVoca.putWord(parsedResult[cnt].toString());
			}
		}

		//Vocabulary Set for Today's News is Created!
		String[] vocaSet = setOfVoca.toStringArray();
		idfVector = new double[vocaSet.length];
		tfidf = new double[numOfArticle][vocaSet.length];

		//TermFreqVectors for each article
		for(int i=0; i<numOfArticle; i++){

			JSONParser parser = new JSONParser();
			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
			List<KoreanTokenizer.KoreanToken> parsed;
			String content = "";

			tfOfArticle[i].setVocaSet(vocaSet);

			try{
				Object obj = parser.parse(new FileReader(dirPath+"politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];
			}catch(Exception e){
				e.printStackTrace();
			}

			parsed = processor.tokenize(content);
			Object[] parsedResult = parsed.toArray();

			//Calculate Term Frequency Vectors for each Article			
			for(int cnt=0; cnt<parsedResult.length; cnt++){
				String word = parsedResult[cnt].toString();
				tfOfArticle[i].putWord(word);
			}

			//Calculate DF for words in Vocabulary Set
			double[] tfVector = tfOfArticle[i].getTermFreqVector();

			for(int cnt=0; cnt<vocaSet.length; cnt++){
				if(tfVector[cnt] != 0){
					idfVector[cnt]++;
				}
				double temp = 0;
				temp = tfVector[cnt];
				tfidf[i][cnt] = temp;
			}
		}

		//Calculate IDF Vector
		for(int cnt=0; cnt<idfVector.length; cnt++){
			idfVector[cnt] = 1.0/idfVector[cnt];
		}

		//Calculate TF x IDF Vectors for each article
		for(int cnt=0; cnt<numOfArticle; cnt++){
			for(int cnt2=0; cnt2< vocaSet.length; cnt2++){
				double temp = 0;
				temp = tfidf[cnt][cnt2]*idfVector[cnt2];
				tfidf[cnt][cnt2] = temp;
			}
		}
		
		for(int row=0; row<numOfArticle; row++){
			for(int col=0; col<numOfArticle; col++){
				double sim = WordVector.similarity(tfidf[row],tfidf[col]);
				System.out.print(sim + " ");
			}
			System.out.println();
		}
		
	}
}