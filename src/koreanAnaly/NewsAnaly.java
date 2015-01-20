package koreanAnaly;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class NewsAnaly {
	public static void main(String[] args) throws FileNotFoundException{

		JSONParser parser=new JSONParser();		
		String content = "";
		String datetime = "";
		String url = "";
		FileReader dicFileReader = new FileReader("C:/Users/태욱/Desktop/KoreanDic.csv");
		BufferedReader dicReader = new BufferedReader(dicFileReader);
		String[] dic = new String[32023];

		try {
			for(int dicidx = 0; dicidx<32023; dicidx++){
				String word = dicReader.readLine().split(",")[0];
				dic[dicidx]=word;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i=1; i<31; i++){
			try{
				Object obj = parser.parse(new FileReader("C:/Users/태욱/Desktop/150120/politics/politics_"+i+".json"));
				JSONObject jsonObject = (JSONObject) obj;

				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];
				//		TODO: Make it able to read image tag and src from json file.
				//			JSONArray ImageList = (JSONArray) jsonObject.get("images");
				//			while(ImageList != null){
				//				JSONObject Image = (JSONObject)ImageList.get(0);
				//				
				//			}

				datetime = (String) jsonObject.get("datetime");
				url = (String) jsonObject.get("url");

			}catch(Exception e){
				e.printStackTrace();
			}
			
			System.out.print(i+"번째 정치 기사에 나온 정치인 : ");
			
			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();

			processor = new TwitterKoreanProcessorJava.Builder()
			.disableStemmer()
			.disableNormalizer()
			.build();

			List<KoreanTokenizer.KoreanToken> parsed;
			String[] sentences = content.split("/n");

			for(int j=0; j<sentences.length; j++){
				//	System.out.println(sentences[j].toString());
				parsed = processor.tokenize(sentences[j]);
	//Parsed Result(No Normalization ,No Stemming)
				//	System.out.println(parsed.toString());

				Object[] parsedResult = parsed.toArray();

				// 명사 아닌 것들 제외
				String[] nouns = new String[parsedResult.length];
				for(int cnt=0; cnt<parsedResult.length; cnt++){

					if(parsedResult[cnt].toString().contains("Noun")){
						nouns[cnt] = parsedResult[cnt].toString();
						String noun = nouns[cnt].replaceAll("Noun", "").replaceAll("\\*","");
						if(3<=noun.length() && noun.length()<=3){
							//!--우리나라 성씨--!
							if((noun.startsWith("가")||noun.startsWith("감")||noun.startsWith("강")||noun.startsWith("견")||
									noun.startsWith("경")||noun.startsWith("계")||noun.startsWith("고")||noun.startsWith("공")||noun.startsWith("곽")||noun.startsWith("구")||noun.startsWith("국")||noun.startsWith("궁")||noun.startsWith("궉")||noun.startsWith("권")||noun.startsWith("금")||noun.startsWith("기")||noun.startsWith("길")||noun.startsWith("김")||noun.startsWith("나")||noun.startsWith("남")||noun.startsWith("남궁")||noun.startsWith("내")||noun.startsWith("노")||noun.startsWith("뇌")||noun.startsWith("단")||noun.startsWith("당")||noun.startsWith("도")||noun.startsWith("돈")||noun.startsWith("독고")||noun.startsWith("동")||noun.startsWith("동방")||noun.startsWith("두")||noun.startsWith("류")||noun.startsWith("마")||noun.startsWith("매")||noun.startsWith("맹")||
									noun.startsWith("명")||noun.startsWith("문")||noun.startsWith("모")||noun.startsWith("목")||noun.startsWith("묵")||noun.startsWith("민")||noun.startsWith("박")||noun.startsWith("반")||noun.startsWith("방")||noun.startsWith("배")||noun.startsWith("백")||noun.startsWith("변")||noun.startsWith("복")||noun.startsWith("봉")||noun.startsWith("부")||noun.startsWith("빈")||noun.startsWith("사")||noun.startsWith("사공")||noun.startsWith("삼")||noun.startsWith("서")||noun.startsWith("서문")||noun.startsWith("석")||noun.startsWith("선")||noun.startsWith("선우")||noun.startsWith("설")||noun.startsWith("성")||noun.startsWith("소")||noun.startsWith("손")||noun.startsWith("송")||noun.startsWith("승")||noun.startsWith("시")||noun.startsWith("신")||noun.startsWith("심")||noun.startsWith("아")||noun.startsWith("안")||noun.startsWith("양")||noun.startsWith("어")||noun.startsWith("여")||noun.startsWith("연")||noun.startsWith("염")||noun.startsWith("예")||noun.startsWith("오")||noun.startsWith("옥")||noun.startsWith("온")||noun.startsWith("옹")||noun.startsWith("왕")||noun.startsWith("용")||noun.startsWith("우")||
									noun.startsWith("원")||noun.startsWith("위")||noun.startsWith("유")||noun.startsWith("육")||noun.startsWith("윤")||noun.startsWith("은")||noun.startsWith("이")||noun.startsWith("인")||noun.startsWith("음")||noun.startsWith("장")||noun.startsWith("전")||noun.startsWith("정")||noun.startsWith("제")||noun.startsWith("제갈")||noun.startsWith("조")||noun.startsWith("종")||noun.startsWith("주")||noun.startsWith("준")||noun.startsWith("즙")||noun.startsWith("지")||noun.startsWith("진")||noun.startsWith("차")||noun.startsWith("창")||noun.startsWith("채")||noun.startsWith("척")||noun.startsWith("천")||noun.startsWith("초")||noun.startsWith("최")||noun.startsWith("추")||noun.startsWith("춘")||noun.startsWith("쾌")||noun.startsWith("탁")||noun.startsWith("탄")||noun.startsWith("태")||noun.startsWith("판")||noun.startsWith("팽")||noun.startsWith("편")||noun.startsWith("평")||noun.startsWith("표")||noun.startsWith("피")||noun.startsWith("하")||noun.startsWith("한")||noun.startsWith("함")||noun.startsWith("허")||noun.startsWith("현")||noun.startsWith("호")||noun.startsWith("홍")||noun.startsWith("환")||noun.startsWith("황")||noun.startsWith("황보")||noun.startsWith("후"))
							//!--우리나라 성씨 끝--!		
									
									&&
									
							//금액 표현 필터링		
									!noun.endsWith("만원") && !noun.endsWith("억원"))
							
							//사전에 나오는 단어는 이름이 아님
							{
								boolean isInDic = false;
								for(int diccnt=0; diccnt<dic.length; diccnt++){
									if(noun.equals(dic[diccnt])) {
										isInDic = true;
										break;
									}
								}
								if(isInDic == false){
									System.out.print(noun+" ");
								}
							}			
						}

					}
				}
			}
			System.out.println();
		}
	}
}
