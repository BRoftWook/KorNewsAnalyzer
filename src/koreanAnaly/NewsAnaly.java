package koreanAnaly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class NewsAnaly {

	private static int numOfArticle;
	private static int numOfWord = 33371;
	private static double cutOff=0.7;
	private static double prLength=0.9;
	private static double prLastName=0.7;
	private static double prDic=0.99;
	private static double prSynt=0.7;
	private static String[] answerSets = new String[numOfArticle];
	
	NewsAnaly(double cutOff, double prLength, double prLastName, double prDic, double prSynt, String[] answers){
		this.cutOff = cutOff;
		this.prLength = prLength;
		this.prLastName = prLastName;
		this.prDic = prDic;
		this.prSynt = prSynt;
		this.answerSets = answers;
	}
	
	//Method for NER Tagging for given Date && Parameter Tuning
	public static void getNamesInArticle(int date) throws FileNotFoundException{
		double[] totalScore = new double[3];
		int sumCnt = 0;
		Integer dt = new Integer(date);
		if(dt.toString().length() != 6){
			System.out.println("Input must be like YYMMDD");
		}
		
		File dir = new File("C:/Users/태욱/Desktop/"+date+"/politics/");
		numOfArticle = dir.list().length;
		WordVector result = new WordVector();

		JSONParser parser=new JSONParser();
		String content = "";

		FileReader dicFileReader = new FileReader("C:/Users/태욱/Desktop/KoreanDic.csv");
		BufferedReader dicReader = new BufferedReader(dicFileReader);
		//국어사전 + 팀포퐁 정치용어집 + 지명(도시이름)
		String[] dic = new String[numOfWord];

		try {
			for(int dicidx = 0; dicidx<numOfWord; dicidx++){
				String word = dicReader.readLine().split(",")[0];
				dic[dicidx]=word;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<numOfArticle; i++){
			try{
				Object obj = parser.parse(new FileReader("C:/Users/태욱/Desktop/"+date+"/politics/"+"politics_"+(i+1)+".json"));
				JSONObject jsonObject = (JSONObject) obj;
				content = (String) jsonObject.get("content");
				content = content.split("기자 = ")[1];

			}catch(Exception e){
				e.printStackTrace();
			}

			System.out.print((i+1)+"번째 정치 기사에 나온 정치인 : ");

			TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
			List<KoreanTokenizer.KoreanToken> parsed;
			String[] sentences = content.split("/n");

			for(int j=0; j<sentences.length; j++){

				parsed = processor.tokenize(sentences[j]);

				Object[] parsedResult = parsed.toArray();

				// 명사 아닌 것들 제외
				String[] nouns = new String[parsedResult.length];
				for(int cnt=0; cnt<parsedResult.length; cnt++){

					if(parsedResult[cnt].toString().contains("Noun")){
						nouns[cnt] = parsedResult[cnt].toString();
						String noun = nouns[cnt].replaceAll("Noun", "").replaceAll("\\*","");

						double probLength = 0;
						double probLastName = 0;
						double probNotDicWord = 0;
						double probNotSyntNoun = 0;
						double probName = 0;

						//규칙 기반 접근 방법

						//세글자 단어 뒤에 직책이 따라온다면 그 단어는 이름
						if((noun.length() == 3) && ((cnt+3)<parsedResult.length) &&

								(noun.startsWith("김")||noun.startsWith("이")||noun.startsWith("박")
										||noun.startsWith("최")||noun.startsWith("정")||noun.startsWith("강")
										||noun.startsWith("조")||noun.startsWith("윤")||noun.startsWith("장")
										||noun.startsWith("임")||noun.startsWith("오")||noun.startsWith("한")
										||noun.startsWith("신")||noun.startsWith("서")||noun.startsWith("권")
										||noun.startsWith("황")||noun.startsWith("안")||noun.startsWith("송")
										||noun.startsWith("류")||noun.startsWith("홍")||noun.startsWith("전")
										||noun.startsWith("고")||noun.startsWith("문")||noun.startsWith("손")
										||noun.startsWith("양")||noun.startsWith("배")||noun.startsWith("백")
										||noun.startsWith("조")||noun.startsWith("허")||noun.startsWith("남")
										||noun.startsWith("심")||noun.startsWith("유")||noun.startsWith("노")
										||noun.startsWith("하")||noun.startsWith("전")||noun.startsWith("정")
										||noun.startsWith("곽")||noun.startsWith("성")||noun.startsWith("차")
										||noun.startsWith("유")||noun.startsWith("구")||noun.startsWith("우")
										||noun.startsWith("주")||noun.startsWith("임")||noun.startsWith("나")
										||noun.startsWith("신")||noun.startsWith("민")||noun.startsWith("진")
										||noun.startsWith("지")||noun.startsWith("엄")||noun.startsWith("원")
										||noun.startsWith("채")) &&

										(parsedResult[cnt+1].toString().contains("의원") || parsedResult[cnt+1].toString().contains("대표")
												||parsedResult[cnt+1].toString().contains("대통령")||parsedResult[cnt+1].toString().contains("의장")
												||parsedResult[cnt+1].toString().contains("총리")||parsedResult[cnt+1].toString().contains("수석")
												||parsedResult[cnt+1].toString().contains("특보")||parsedResult[cnt+1].toString().contains("후보")))
						{
							probName = 1;

							//이처럼 후보들간 ~  이름이 쓰였을때는 직책뒤에 조사가 옴. xxx 후보가~ 후보는~ 으로 조사가 붙지 접미사 ~들 같은게 붙지는 않음
							if(parsedResult[cnt+2].toString().contains("Suffix")){
								probName = 0;
							}

						}

						//같은 글자가 연속으로 오는 이름은 거의 없다
						else if(noun.length()>=3 &&
								(noun.toCharArray()[0] == noun.toCharArray()[1] || noun.toCharArray()[1] == noun.toCharArray()[2])){
							probName = 0;
						}

						//한국어에서는 이름 다음 바로 동사가 오는 경우는 없다
						else if(parsedResult[cnt+1] != null &&
								parsedResult[cnt+1].toString().contains("Verb")){
							probName = 0;
						}

						//이름 다음 곧바로 인용하지 않는다(조사나 다른 것이 먼저 옴)
						else if(parsedResult[cnt+1] != null &&
								(parsedResult[cnt+1].toString().contains("'")||parsedResult[cnt+1].toString().contains("\""))){
							probName = 0;
						}

						// 김태욱 (25)와 같은 형태로 나오면 이름으로 처리
						else if((noun.length() == 3) && ((cnt+4)<= parsedResult.length) &&
								parsedResult[cnt+1] != null && parsedResult[cnt+2] != null && parsedResult[cnt+3] != null &&
								parsedResult[cnt+1].toString().contains("(") && parsedResult[cnt+2].toString().contains("Number")
								&& parsedResult[cnt+3].toString().contains(")")){
							probName = 1;
						}

						//통계적 접근방법
						else{

							//글자 길이 체크
							if(noun.length()==3){
								probLength = prLength;
							}
							else if(noun.length()==2||noun.length()==4){
								probLength = 1 - prLength;
							}
							else{
								probLength = 0;
							}

							//!--우리나라 성씨(주요)--!
							if((noun.startsWith("김")||noun.startsWith("이")||noun.startsWith("박")
									||noun.startsWith("최")||noun.startsWith("정")||noun.startsWith("강")
									||noun.startsWith("조")||noun.startsWith("윤")||noun.startsWith("장")
									||noun.startsWith("임")||noun.startsWith("오")||noun.startsWith("한")
									||noun.startsWith("신")||noun.startsWith("서")||noun.startsWith("권")
									||noun.startsWith("황")||noun.startsWith("안")||noun.startsWith("송")
									||noun.startsWith("류")||noun.startsWith("홍")||noun.startsWith("전")
									||noun.startsWith("고")||noun.startsWith("문")||noun.startsWith("손")
									||noun.startsWith("양")||noun.startsWith("배")||noun.startsWith("백")
									||noun.startsWith("조")||noun.startsWith("허")||noun.startsWith("남")
									||noun.startsWith("심")||noun.startsWith("유")||noun.startsWith("노")
									||noun.startsWith("하")||noun.startsWith("전")||noun.startsWith("정")
									||noun.startsWith("곽")||noun.startsWith("성")||noun.startsWith("차")
									||noun.startsWith("유")||noun.startsWith("구")||noun.startsWith("우")
									||noun.startsWith("주")||noun.startsWith("임")||noun.startsWith("나")
									||noun.startsWith("신")||noun.startsWith("민")||noun.startsWith("진")
									||noun.startsWith("지")||noun.startsWith("엄")||noun.startsWith("원")
									||noun.startsWith("채"))){

								probLastName = prLastName;
							}
							else{
								probLastName = 1 - prLastName;
							}

							//사전에 나오는 단어는 이름이 아님
							probNotDicWord = prDic;

							for(int diccnt=0; diccnt<dic.length; diccnt++){
								if(noun.equals(dic[diccnt])) {
									probNotDicWord = 1 - prDic;
								}
							}

							//합성 명사들(ex 부산시, 신혼집)에 페널티를 주기 위해, 앞에서부터 2글자가 사전에 있는 단어인 경우 페널티 부여
							boolean isNotSyntNoun = true;
							for(int diccnt=0; diccnt<dic.length; diccnt++){
								if(noun.substring(0,noun.length()-1).equals(dic[diccnt])) {
									isNotSyntNoun = false;
								}
							}
							if(isNotSyntNoun == true){
								probNotSyntNoun = prSynt;
							}
							else{
								probNotSyntNoun = 1 - prSynt;
							}

							//확률 계산
							probName = Math.pow((probLength * probLastName * probNotDicWord * probNotSyntNoun), 1.0/4);
						}
						if(probName > cutOff){
							System.out.print(noun + " ");
							result.putName(noun);
						}
					}			
				}				
			}
			System.out.println();
				
			if(!answerSets[i].isEmpty()){
			double[] tempScore = performanceScore(result,answerSets[i]);
			totalScore[0] += tempScore[0];
			totalScore[1] += tempScore[1];
			totalScore[2] += tempScore[2];
			sumCnt++;
			}
			result = new WordVector();
		}
		totalScore[0] = totalScore[0]/sumCnt;
		totalScore[1] = totalScore[1]/sumCnt;
		totalScore[2] = totalScore[2]/sumCnt;
		System.out.println("P mean: "+totalScore[0]+", R mean: "+totalScore[1]+", F mean: "+totalScore[2]);
	}
	
	//Method Used In TopicFinder
	public static WordVector getNamesInArticle(String content) throws FileNotFoundException{

		WordVector result = new WordVector();

		FileReader dicFileReader = new FileReader("C:/Users/태욱/Desktop/KoreanDic.csv");
		BufferedReader dicReader = new BufferedReader(dicFileReader);

		//국어사전 + 팀포퐁 정치용어집 + 지명(도시이름)
		String[] dic = new String[numOfWord];
		try {
			for(int dicidx = 0; dicidx<numOfWord; dicidx++){
				String word = dicReader.readLine().split(",")[0];
				dic[dicidx]=word;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
		List<KoreanTokenizer.KoreanToken> parsed;

			parsed = processor.tokenize(content);

			Object[] parsedResult = parsed.toArray();

			// 명사 아닌 것들 제외
			String[] nouns = new String[parsedResult.length];
			
			for(int cnt=0; cnt<parsedResult.length; cnt++){

				if(parsedResult[cnt].toString().contains("Noun")){
					nouns[cnt] = parsedResult[cnt].toString();
					String noun = nouns[cnt].replaceAll("Noun", "").replaceAll("\\*","");

					double probLength = 0;
					double probLastName = 0;
					double probNotDicWord = 0;
					double probNotSyntNoun = 0;
					double probName = 0;

					//규칙 기반 접근 방법

					//세글자 단어 뒤에 직책이 따라온다면 그 단어는 이름
					if((noun.length() == 3) &&

							(noun.startsWith("김")||noun.startsWith("이")||noun.startsWith("박")
									||noun.startsWith("최")||noun.startsWith("정")||noun.startsWith("강")
									||noun.startsWith("조")||noun.startsWith("윤")||noun.startsWith("장")
									||noun.startsWith("임")||noun.startsWith("오")||noun.startsWith("한")
									||noun.startsWith("신")||noun.startsWith("서")||noun.startsWith("권")
									||noun.startsWith("황")||noun.startsWith("안")||noun.startsWith("송")
									||noun.startsWith("류")||noun.startsWith("홍")||noun.startsWith("전")
									||noun.startsWith("고")||noun.startsWith("문")||noun.startsWith("손")
									||noun.startsWith("양")||noun.startsWith("배")||noun.startsWith("백")
									||noun.startsWith("조")||noun.startsWith("허")||noun.startsWith("남")
									||noun.startsWith("심")||noun.startsWith("유")||noun.startsWith("노")
									||noun.startsWith("하")||noun.startsWith("전")||noun.startsWith("정")
									||noun.startsWith("곽")||noun.startsWith("성")||noun.startsWith("차")
									||noun.startsWith("유")||noun.startsWith("구")||noun.startsWith("우")
									||noun.startsWith("주")||noun.startsWith("임")||noun.startsWith("나")
									||noun.startsWith("신")||noun.startsWith("민")||noun.startsWith("진")
									||noun.startsWith("지")||noun.startsWith("엄")||noun.startsWith("원")
									||noun.startsWith("채")) &&

									(parsedResult[cnt+1].toString().contains("의원") || parsedResult[cnt+1].toString().contains("대표")
											||parsedResult[cnt+1].toString().contains("대통령")||parsedResult[cnt+1].toString().contains("의장")
											||parsedResult[cnt+1].toString().contains("총리")||parsedResult[cnt+1].toString().contains("수석")
											||parsedResult[cnt+1].toString().contains("특보")||parsedResult[cnt+1].toString().contains("후보")))
					{
						probName = 1;

						//이처럼 후보들간 ~  이름이 쓰였을때는 직책뒤에 조사가 옴. xxx 후보가~ 후보는~ 으로 조사가 붙지 접미사 ~들 같은게 붙지는 않음
						if((cnt+2)<parsedResult.length && parsedResult[cnt+2].toString().contains("Suffix")){
							probName = 0;
						}

					}

					//같은 글자가 연속으로 오는 이름은 거의 없다
					else if(noun.length()>=3 &&
							(noun.toCharArray()[0] == noun.toCharArray()[1] || noun.toCharArray()[1] == noun.toCharArray()[2])){
						probName = 0;
					}

					//한국어에서는 이름 다음 바로 동사가 오는 경우는 없다
					else if((cnt+2)<parsedResult.length &&
							parsedResult[cnt+1].toString().contains("Verb")){
						probName = 0;
					}

					//이름 다음 곧바로 인용하지 않는다(조사나 다른 것이 먼저 옴)
					else if((cnt+2)<parsedResult.length &&
							(parsedResult[cnt+1].toString().contains("'")||parsedResult[cnt+1].toString().contains("\""))){
						probName = 0;
					}

					// 김태욱 (25)와 같은 형태로 나오면 이름으로 처리
					else if((noun.length() == 3) && ((cnt+4)<= parsedResult.length) &&
							parsedResult[cnt+1] != null && parsedResult[cnt+2] != null && parsedResult[cnt+3] != null &&
							parsedResult[cnt+1].toString().contains("(") && parsedResult[cnt+2].toString().contains("Number")
							&& parsedResult[cnt+3].toString().contains(")")){
						probName = 1;
					}

					//통계적 접근방법
					else{

						//글자 길이 체크
						if(noun.length()==3){
							probLength = prLength;
						}
						else if(noun.length()==2||noun.length()==4){
							probLength = 1 - prLength;
						}
						else{
							probLength = 0;
						}

						//!--우리나라 성씨(주요)--!
						if((noun.startsWith("김")||noun.startsWith("이")||noun.startsWith("박")
								||noun.startsWith("최")||noun.startsWith("정")||noun.startsWith("강")
								||noun.startsWith("조")||noun.startsWith("윤")||noun.startsWith("장")
								||noun.startsWith("임")||noun.startsWith("오")||noun.startsWith("한")
								||noun.startsWith("신")||noun.startsWith("서")||noun.startsWith("권")
								||noun.startsWith("황")||noun.startsWith("안")||noun.startsWith("송")
								||noun.startsWith("류")||noun.startsWith("홍")||noun.startsWith("전")
								||noun.startsWith("고")||noun.startsWith("문")||noun.startsWith("손")
								||noun.startsWith("양")||noun.startsWith("배")||noun.startsWith("백")
								||noun.startsWith("조")||noun.startsWith("허")||noun.startsWith("남")
								||noun.startsWith("심")||noun.startsWith("유")||noun.startsWith("노")
								||noun.startsWith("하")||noun.startsWith("전")||noun.startsWith("정")
								||noun.startsWith("곽")||noun.startsWith("성")||noun.startsWith("차")
								||noun.startsWith("유")||noun.startsWith("구")||noun.startsWith("우")
								||noun.startsWith("주")||noun.startsWith("임")||noun.startsWith("나")
								||noun.startsWith("신")||noun.startsWith("민")||noun.startsWith("진")
								||noun.startsWith("지")||noun.startsWith("엄")||noun.startsWith("원")
								||noun.startsWith("채"))){

							probLastName = prLastName;
						}
						else{
							probLastName = 1 - prLastName;
						}

						//사전에 나오는 단어는 이름이 아님
						probNotDicWord = prDic;

						for(int diccnt=0; diccnt<dic.length; diccnt++){
							if(noun.equals(dic[diccnt])) {
								probNotDicWord = 1 - prDic;
							}
						}

						//합성 명사들(ex 부산시, 신혼집)에 페널티를 주기 위해, 앞에서부터 2글자가 사전에 있는 단어인 경우 페널티 부여
						boolean isNotSyntNoun = true;
						for(int diccnt=0; diccnt<dic.length; diccnt++){
							if(noun.substring(0,noun.length()-1).equals(dic[diccnt])) {
								isNotSyntNoun = false;
							}
						}
						if(isNotSyntNoun == true){
							probNotSyntNoun = prSynt;
						}
						else{
							probNotSyntNoun = 1 - prSynt;
						}

						//확률 계산
						probName = Math.pow((probLength * probLastName * probNotDicWord * probNotSyntNoun), 1.0/4);
					}
					if(probName > cutOff){
						result.putName(noun);
					}
				}			
			}				
		return result;
		
	}
	
	//Calculate Precision, Recall, F-measure for NER Tagging Performance
	public static double[] performanceScore(WordVector result, String trueAnswer){
		double[] score = new double[3];
		double precision = 0;
		double recall = 0;
		double fmeasure = 0;
		int truePositive = 0;
		String[] predNames = result.toStringArray();	
		String[] answers = trueAnswer.split(" ");
		
		for(String answer : answers){
			for(int cnt=0; cnt<predNames.length; cnt++){
				if(answer.contains(predNames[cnt])) truePositive++;
			}
		}
		recall = (double)truePositive / answers.length;
		precision = (double)truePositive / predNames.length;
		fmeasure = (2 * recall * precision) / (recall + precision);
		score[0] = precision;
		score[1] = recall;
		score[2] = fmeasure;
		System.out.println("P : "+score[0]+", R : "+score[1]+", F : "+score[2]);
		return score;
	}
}
