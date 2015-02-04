package koreanAnaly;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NerTest {
	
	public static void main(String[] args) throws IOException{
		
		try {

			//	NewsAnaly.getNamesInArticle(150201); //Default Setting of Parameters

			int numOfArticle = 12;
			
			FileReader fr = new FileReader("C:/Users/태욱/Desktop/answerSet0201.txt");
			BufferedReader br = new BufferedReader(fr);
			
			double cutOff = 0.6;
			double prLength = 0.9;
			double prLastName = 0.9;
			double prDic = 0.99;
			double prSynt = 0.9;
			
			String[] answerSets0201 = new String[numOfArticle];
			for(int cnt=0; cnt<numOfArticle; cnt++){
				answerSets0201[cnt] = br.readLine();
			}
			
			NewsAnaly na = new NewsAnaly(cutOff, prLength, prLastName, prDic, prSynt,answerSets0201);
			na.getNamesInArticle(150201);
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
