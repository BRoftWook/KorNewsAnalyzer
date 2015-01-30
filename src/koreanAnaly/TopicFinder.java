package koreanAnaly;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

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
		TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
		JSONParser parser = new JSONParser();
		List<KoreanTokenizer.KoreanToken> parsed;
		List<KoreanTokenizer.KoreanToken> parsedTitle;
		String title = "";
		String content = "";
		for(int cnt=0; cnt<numOfArticle; cnt++){
			tfOfArticle[cnt] = new WordVector();
			tfidfOfArticle[cnt] = new WordVector();
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
		}

		//Vocabulary Set for Today's News is Created
		String[] vocaSet = setOfVoca.toStringArray();
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

			//Calculate Term Frequency Vectors for each Article			
			for(int cnt=0; cnt<parsedResult.length; cnt++){
				String word = parsedResult[cnt].toString();

				for(int cnt2 = 0; cnt2 < parsedTitleResult.length; cnt2++){

					String titleWord = parsedTitleResult[cnt2].toString();
					//Give Weight for the words in title
					if(word.equals(titleWord)){
						for(int weight = 0; weight< 10; weight++){
							tfOfArticle[i].putWord(word);
						}
					}
					else{
						tfOfArticle[i].putWord(word);
					}
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

		//Find similar articles
		for(int row=0; row<numOfArticle; row++){
			for(int col=0; col<numOfArticle; col++){
				double sim = WordVector.similarity(tfidfOfArticle[row],tfidfOfArticle[col]);
				if(row!=col && sim > 0.2){
					System.out.println((row+1)+"과 "+(col+1)+"은 유사합니다. 유사도 : "+ sim);
				}
			}
		}
	}
}