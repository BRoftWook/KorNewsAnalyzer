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

public class test {
	public static void main(String[] args) throws FileNotFoundException{

		JSONParser parser=new JSONParser();		
		String content = "";
		String datetime = "";
		String url = "";
		FileReader dicFileReader = new FileReader("C:/Users/태욱/Desktop/KoreanDic.csv");
		BufferedReader dicReader = new BufferedReader(dicFileReader);
		String dic = "";

		try {
			for(int dicidx = 0; dicidx<32023; dicidx++){
				//	System.out.println(dicReader.readLine());
				System.out.println(dicReader.readLine().split(",")[0]);
				dic+=dicReader.readLine().split(",")[0];
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}