package edu.illinois.cs.cogcomp.xlwikifier;

import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.nlp.SentenceSplitter;
import edu.illinois.cs.cogcomp.lbjava.nlp.WordSplitter;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.PlainToTokenParser;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.pos.POSConfigurator;
import edu.illinois.cs.cogcomp.pos.lbjava.POSTagger;
import edu.illinois.cs.cogcomp.tokenizers.CharacterTokenizer;
import edu.illinois.cs.cogcomp.tokenizers.MultiLingualTokenizer;
import edu.illinois.cs.cogcomp.tokenizers.Tokenizer;
import edu.illinois.cs.cogcomp.xlwikifier.datastructures.Language;
import edu.illinois.cs.cogcomp.xlwikifier.datastructures.ELMention;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * This class runs MultiLigualNER and CrossLingualWikifier on the Spansih and Chinese sample text,
 * and checks prediction results.
 * <p>
 * Created by ctsai12 on 10/25/16.
 */
public class TestAnnotatorsTest{
    public static void main(String[] args){
      TestAnnotatorsTest tat = new TestAnnotatorsTest();
      try{
        tat.testEnglish();
      } catch (AnnotatorException e){
        e.printStackTrace();
      }
    }

    final static String spanish_input = "Barack Hussein Obama II3 es el cuadragésimo cuarto y actual presidente de " +
            "los Estados Unidos de América. Fue senador por el estado de Illinois desde el 3 de enero de 2005 hasta su " +
            "renuncia el 16 de noviembre de 2008. Además, es el quinto legislador afroamericano en el Senado de los Estados Unidos, " +
            "tercero desde la era de reconstrucción. También fue el primer candidato afroamericano nominado a la presidencia " +
            "por el Partido Demócrata y es el primero en ejercer el cargo presidencial.";

    final static String chinese_input = "巴拉克·歐巴馬是美國民主黨籍政治家，也是第44任美國總統。" +
            "歐巴馬是第一位非裔美國總統。他1961年出生於美國夏威夷州檀香山。1991年，以優等生榮譽從哈佛法學院畢業。" +
            "1996年開始參選公職，在補選中，當選伊利諾州參議員。";
    
    final static String english_input = "At a 10 p.m. media conference at SEIU Healthcare, 2229 S. Halsted St., " + 
    "Lewis said there was no deal yet, but said the district had made a \"significantly better\" offer than it had previously." +
    "\"We're deliberating\" about whether to accept the offer, she said." + 
    "\"We are working on it,\" she added. She said families would \"have to stay up a little later\"" + 
    "to find out whether the 380,000 Chicago school kids would be going to school Tuesday." + 
    "CPS Chief Education Officer Janice Jackson said she was optimistic a strike could be averted."+
    "\"We do believe we are close and we are hopeful we will reach an agreement very soon,\" she said." + 
    "Emanuel and CPS CEO Forrest Claypool issued a written statement saying they were \"committed to " + 
    "remaining at the negotiating table as long as it takes to reach an agreement — to give teachers raises, " + 
    "to secure their pensions, to invest in our schools, and — most importantly — to ensure our children are in school where they belong. ..." + 
    "We believe there's nothing here that can't be solved.\"";
    final private static Map<Pair<Integer, Integer>, ELMention> spanish_answers = new HashMap<>();

    static {
        spanish_answers.put(new Pair(0, 20), new ELMention("Barack Hussein Obama", 0, 20, "PER", "barack_obama", "barack_obama"));
        spanish_answers.put(new Pair<>(78, 103), new ELMention("Estados Unidos de América", 78, 103, "GPE", "estados_unidos", "united_states"));
        spanish_answers.put(new Pair<>(249, 262), new ELMention("afroamericano", 249, 262, "LOC", "afroamericano", "african_diaspora_in_the_americas"));
        spanish_answers.put(new Pair<>(134, 142), new ELMention("Illinois", 134, 142, "GPE", "illinois", "illinois"));
        spanish_answers.put(new Pair<>(269, 275), new ELMention("Senado", 269, 275, "ORG", "senado", "senate"));
        spanish_answers.put(new Pair<>(283, 297), new ELMention("Estados Unidos", 283, 297, "GPE", "estados_unidos", "united_states"));
        spanish_answers.put(new Pair<>(371, 384), new ELMention("afroamericano", 371, 384, "LOC", "afroamericano", "african_diaspora_in_the_americas"));
        spanish_answers.put(new Pair<>(418, 435), new ELMention("Partido Demócrata", 418, 435, "ORG", "partido_demócrata_(estados_unidos)", "democratic_party_(united_states)"));
    }

    final private static Map<Pair<Integer, Integer>, ELMention> chinese_answers = new HashMap<>();

    static {
        chinese_answers.put(new Pair<>(0, 7), new ELMention("巴拉克·歐巴馬", 0, 7, "PER", "贝拉克·奥巴马", "barack_obama"));
        chinese_answers.put(new Pair<>(8, 13), new ELMention("美國民主黨", 8, 13, "ORG", "民主党_(美国)", "democratic_party_(united_states)"));
        chinese_answers.put(new Pair<>(24, 26), new ELMention("美國", 24, 26, "GPE", "美国", "united_states"));
        chinese_answers.put(new Pair<>(29, 32), new ELMention("歐巴馬", 29, 32, "PER", "贝拉克·奥巴马", "barack_obama"));
        chinese_answers.put(new Pair<>(36, 37), new ELMention("非", 36, 37, "LOC", "非洲", "africa"));
        chinese_answers.put(new Pair<>(38, 40), new ELMention("美國", 38, 40, "GPE", "美国", "united_states"));
        chinese_answers.put(new Pair<>(52, 61), new ELMention("美國夏威夷州檀香山", 52, 61, "GPE", "NIL", "NIL0001"));
        chinese_answers.put(new Pair<>(75, 80), new ELMention("哈佛法學院", 75, 80, "ORG", "哈佛法学院", "harvard_law_school"));
        chinese_answers.put(new Pair<>(102, 109), new ELMention("伊利諾州參議員", 102, 109, "ORG", "NIL", "NIL0002"));
    }

    @Test
    public void testEnglish() throws AnnotatorException{

        Language lang = Language.EN;

        Tokenizer tokenizer = MultiLingualTokenizer.getTokenizer("en");
        TextAnnotation ta = tokenizer.getTextAnnotation(english_input);

        String config = "config/xlwikifier-chase.config";
        ResourceManager rm = new POSConfigurator().getDefaultConfig();
        System.out.println(rm);
        POSTagger tagger = new POSTagger();
        System.out.println(POSConfigurator.MODEL_PATH);
        Parser parser = new PlainToTokenParser(new WordSplitter(new SentenceSplitter(new String[]{"This is one sentence", "Yet another sentence"})));
        System.out.println(tagger.getTagValues());
        for (Token word = (Token) parser.next(); word != null; word = (Token) parser.next()) {
          String tag = tagger.discreteValue(word);
          System.out.println(word + ":" + tag);
        }
 
        //POSAnnotator pos_annotator = new POSAnnotator();
        //POSTagger pos_tagger = new POSTagger();
        // pos_annotator.initialize(null);
        /*pos_annotator.getView(ta);
        System.out.println(ta.getAvailableViews()); 
        System.out.println(ta.getView("POS").getConstituents());
        for(Constituent c : ta.getView("POS").getConstituents()) 
            System.out.println("c- > "+ c + "  ---> " + c.getLabel() );*/ 
        return;
        /*
        MultiLingualNER ner_annotator = null;
        try {
            ner_annotator = new MultiLingualNER(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ner_annotator.addView(ta);
        */
        /*
        for (Constituent c : ta.getView(lang.getNERViewName()).getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            assertTrue("No entity mention \"" + c.getSurfaceForm() + "\"", spanish_answers.containsKey(key));

            String gold_type = spanish_answers.get(key).getType();
            assertTrue("Entity " + c.getSurfaceForm() + " has type " + c.getLabel() + " instead of " + gold_type
                    , c.getLabel().equals(gold_type));
        }
        */
        /*
        ChunkerAnnotator chunker  = new ChunkerAnnotator(true);
        chunker.initialize(new ChunkerConfigurator().getDefaultConfig());
        chunker.addView(ta);

        CrossLingualWikifier xlwikifier = null;
        try {
            xlwikifier = new CrossLingualWikifier(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        xlwikifier.enhanceKB("/home/cddunca2/cross-lingual-wikifier/enhancements.txt");
        xlwikifier.addView(ta);

        CoreferenceView corefview = (CoreferenceView) ta.getView(lang.getWikifierViewName());
        for (Constituent c : corefview.getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            //String gold_entitle = spanish_answers.get(key).getEnWikiTitle();

            System.out.println("Entity " + c.getSurfaceForm() + " has English title " + c.getLabel());
            //assertTrue("Entity " + c.getSurfaceForm() + " has English title " + c.getLabel() + " instead of " + gold_entitle,
            //       c.getLabel().equals(gold_entitle));
        }
          */
    }



    @Test
    public void testSpanishResults() {

        Language lang = Language.ES;

        Tokenizer tokenizer = MultiLingualTokenizer.getTokenizer("es");
        TextAnnotation ta = tokenizer.getTextAnnotation(spanish_input);

        String config = "config/xlwikifier-demo.config";

        MultiLingualNER ner_annotator = null;
        try {
            ner_annotator = new MultiLingualNER(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ner_annotator.addView(ta);

        for (Constituent c : ta.getView(lang.getNERViewName()).getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            assertTrue("No entity mention \"" + c.getSurfaceForm() + "\"", spanish_answers.containsKey(key));

            String gold_type = spanish_answers.get(key).getType();
            assertTrue("Entity " + c.getSurfaceForm() + " has type " + c.getLabel() + " instead of " + gold_type
                    , c.getLabel().equals(gold_type));
        }

        CrossLingualWikifier xlwikifier = null;
        try {
            xlwikifier = new CrossLingualWikifier(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        xlwikifier.addView(ta);

        CoreferenceView corefview = (CoreferenceView) ta.getView(lang.getWikifierViewName());
        for (Constituent c : corefview.getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            String gold_entitle = spanish_answers.get(key).getEnWikiTitle();

            System.out.println("Entity " + c.getSurfaceForm() + " has English title " + c.getLabel() + " instead of " + gold_entitle);
            assertTrue("Entity " + c.getSurfaceForm() + " has English title " + c.getLabel() + " instead of " + gold_entitle,
                    c.getLabel().equals(gold_entitle));
        }

    }

    @Test
    public void testChineseResults() {

        Language lang = Language.ZH;
        Tokenizer tokenizer = new CharacterTokenizer();
        TextAnnotation ta = tokenizer.getTextAnnotation(chinese_input);

        String config = "config/xlwikifier-demo.config";

        MultiLingualNER annotator = null;
        try {
            annotator = new MultiLingualNER(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        annotator.addView(ta);

        for (Constituent c : ta.getView(lang.getNERViewName()).getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            assertTrue("No entity mention \"" + c.getSurfaceForm() + "\"", chinese_answers.containsKey(key));

            String gold_type = chinese_answers.get(key).getType();
            assertTrue("Entity " + c.getSurfaceForm() + " has type " + c.getLabel() + " instead of " + gold_type
                    , c.getLabel().equals(gold_type));
        }

        CrossLingualWikifier xlwikifier = null;
        try {
            xlwikifier = new CrossLingualWikifier(lang, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        xlwikifier.addView(ta);

        CoreferenceView corefview = (CoreferenceView) ta.getView(lang.getWikifierViewName());
        for (Constituent c : corefview.getConstituents()) {
            Pair<Integer, Integer> key = new Pair<>(c.getStartCharOffset(), c.getEndCharOffset());
            String gold_entitle = chinese_answers.get(key).getEnWikiTitle();

            assertTrue("Entity " + c.getSurfaceForm() + " has English title " + c.getLabel() + " instead of " + gold_entitle,
                    c.getLabel().equals(gold_entitle));
        }
    }

}
