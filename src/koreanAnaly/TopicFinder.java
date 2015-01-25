package koreanAnaly;

import java.io.FileReader;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class TopicFinder {
	private static int numOfArticle = 21;
	public static void main(String[] args){

		JSONParser parser=new JSONParser();
		String title = "";
		String content = "";
		
		//Object for making Vocabulary Set
		WordVector termFreqVector = new WordVector();
		
		//Make Vocabulary Set for entire articles		
		for(int i=0; i<numOfArticle; i++){

			try{
				Object obj = parser.parse(new FileReader("C:/Users/태욱/Desktop/150123/politics/politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				title = (String) jsonObject.get("title");
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];

			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println((i+1)+"번째 기사 제목 : "+title);
			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();

			processor = new TwitterKoreanProcessorJava.Builder()
			//		.disableStemmer()
			//	.disableNormalizer()
			.build();

			List<KoreanTokenizer.KoreanToken> parsed;

			parsed = processor.tokenize(content);

			Object[] parsedResult = parsed.toArray();

			for(int cnt=0;cnt<parsedResult.length; cnt++){
				String word = parsedResult[cnt].toString();
				termFreqVector.putWord(word);
			}
		}

		//Vocabulary Set for Today's News is Created!
		String[] vocaSet = termFreqVector.toStringArray();

		//TermFreqVectors for each article
		Integer[][] TermFreqOfArticle = new Integer[numOfArticle][vocaSet.length];


		for(int i=0; i<numOfArticle; i++){

			try{
				Object obj = parser.parse(new FileReader("C:/Users/태욱/Desktop/150123/politics/politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;

				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];


			}catch(Exception e){
				e.printStackTrace();
			}

			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();

			processor = new TwitterKoreanProcessorJava.Builder()
			//		.disableStemmer()
			//	.disableNormalizer()
			.build();

			List<KoreanTokenizer.KoreanToken> parsed;

			parsed = processor.tokenize(content);

			Object[] parsedResult = parsed.toArray();

		//Calculate Term Frequency Vectors for each Article
			Integer[] TermFreqVector = new Integer[vocaSet.length];

			for(int cnt=0; cnt<TermFreqVector.length; cnt++){
				TermFreqVector[cnt] = 0; //Initialize value
				
				//For each word in the Vocabulary Set, count the frequency of the word in the given article.
				for(int cnt2=0; cnt2<parsedResult.length; cnt2++){
					if(vocaSet[cnt].equals(parsedResult[cnt2].toString())){
						TermFreqVector[cnt]++;
					}
				}
			}
			for(int cnt=0; cnt<TermFreqVector.length; cnt++){
				TermFreqOfArticle[i][cnt] = TermFreqVector[cnt];
			}
		}

		//Calculate DF for each word
		double[] dfVector = new double[vocaSet.length];
		for(int cnt=0; cnt<dfVector.length; cnt++){
			dfVector[cnt] = 0;
		}

		for(int cnt=0; cnt<numOfArticle; cnt++){
			for(int cnt2=0; cnt2<vocaSet.length; cnt2++){

				if(TermFreqOfArticle[cnt][cnt2]!=0) dfVector[cnt2]++;

			}
		}

		//Calculate TF x IDF Vectors for each article
		double[][] tfIdfVector = new double[numOfArticle][vocaSet.length];
		for(int cnt=0; cnt<numOfArticle; cnt++){
			for(int cnt2=0; cnt2<vocaSet.length; cnt2++){
				tfIdfVector[cnt][cnt2] = TermFreqOfArticle[cnt][cnt2] * (1 / dfVector[cnt2]);
			}
		}

		//Calculate Similarity between Articles
		double sim = 0;
		for(int cnt=0; cnt<tfIdfVector.length; cnt++){

			for(int cnt2=0; cnt2<tfIdfVector.length; cnt2++){

				if(cnt != cnt2){
					for(int cnt3=0; cnt3<vocaSet.length; cnt3++){
						sim += tfIdfVector[cnt][cnt3] * tfIdfVector[cnt2][cnt3];
					}
					//sim = sim / vocaSet.length;
					if(sim > 15){
						System.out.println((cnt+1)+"번 기사와 "+(cnt2+1)+"번 기사는 유사합니다");
					}
					sim = 0;
				}
			}
		}
	}
}
