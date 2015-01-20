package koreanAnaly;

import java.util.List;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class JavaTwitterKoreanTextExample {
  public static void main(String[] args) {
    // Tokenize with normalization + stemmer
    TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();

    List<String> parsedStrings = processor.tokenizeToStrings("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsedStrings);
    // output: [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ]

    List<KoreanTokenizer.KoreanToken> parsed = processor
        .tokenize("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsed);
    // output: [한국어Noun, 를Josa, 처리Noun, 하다Verb, 예시Noun, 이다Adjective, ㅋㅋKoreanParticle]


    // Tokenize without stemmer
    processor = new TwitterKoreanProcessorJava.Builder()
            .disableStemmer()
            .build();

    parsedStrings = processor.tokenizeToStrings("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsedStrings);
    // output: [한국어, 를, 처리, 하는, 예시, 입, 니다, ㅋㅋ]

    parsed = processor.tokenize("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsed);
    // output: [한국어Noun, 를Josa, 처리Noun, 하는Verb, 예시Noun, 입Adjective, 니다Eomi, ㅋㅋKoreanParticle]


    // Tokenize with neither normalization nor stemmer
    processor = new TwitterKoreanProcessorJava.Builder()
        .disableNormalizer()
        .disableStemmer()
        .build();

    parsedStrings = processor.tokenizeToStrings("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsedStrings);
    // output: [한국어, 를, 처리, 하는, 예시, 입, 니, 닼, ㅋㅋㅋㅋㅋ]

    parsed = processor.tokenize("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ");
    System.out.println(parsed);
    // output: [한국어Noun, 를Josa, 처리Noun, 하는Verb, 예시Noun, 입Noun, 니Josa, 닼Noun*, ㅋㅋㅋㅋㅋKoreanParticle]

    List<CharSequence> phrases = processor
        .extractPhrases("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ 시발");
    System.out.println(phrases);
    // output: [한국어, 처리, 처리하는 예시, 예시, 시발]

    processor = new TwitterKoreanProcessorJava.Builder()
        .disableNormalizer()
        .disableStemmer()
        .enablePhraseExtractorSpamFilter()
        .build();

    phrases = processor.extractPhrases("한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ 시발");
    System.out.println(phrases);
    // output: [한국어, 처리, 처리하는 예시, 예시]
  }
}
