package bss.Keywords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import bss.Data.DataAccess;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

/**
 * Provides methods for tokenizing and processing keywords using Stanford CoreNLP.
 * This class handles the annotation, extraction, and transformation of tokens
 * for keyword analysis and matching.
 */
public class Tokenizer {

    /** The Stanford CoreNLP pipeline for natural language processing. */
    private static final StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Creates annotated tokens for the given keyword using Stanford CoreNLP.
     * 
     * @param keyword The keyword to annotate.
     * @return A list of {@link CoreLabel} tokens annotated by Stanford CoreNLP.
     */
    public static ArrayList<CoreLabel> createAnnotatedTokens(String keyword) {
        Annotation annotation = new Annotation(keyword);
        pipeline.annotate(annotation);

        ArrayList<CoreLabel> annotatedTokens = new ArrayList<>();
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                annotatedTokens.add(token);
            }
        }
        return annotatedTokens;
    }

    /**
     * Extracts tokens with their lemmas from a list of annotated tokens.
     * 
     * @param annotatedTokens A list of {@link CoreLabel} tokens.
     * @return A list of token strings with their lemmas.
     */
    public static ArrayList<String> extractTokensWithLemmas(ArrayList<CoreLabel> annotatedTokens) {
        ArrayList<String> tokens = new ArrayList<>();
        for (CoreLabel at : annotatedTokens) {
            if (at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CD")) {
                Object num = at.get(CoreAnnotations.NumericValueAnnotation.class);
                if (num != null) {
                    if (num instanceof Integer) {
                        tokens.add(Integer.toString((int) num));
                    }
                    else if (num instanceof Long) {
                        tokens.add(Long.toString((Long) num));
                    }
                    else if (num instanceof Double) {
                        tokens.add(Double.toString((Double) num));
                    }
                    else {
                        tokens.add(at.value());
                        if (!at.lemma().equals(at.value())) {
                            tokens.add(at.lemma());
                        }
                    }
                }
                else {
                    tokens.add(at.value());
                        if (!at.lemma().equals(at.value())) {
                            tokens.add(at.lemma());
                        }
                }
            }
            else {
                tokens.add(at.value());
                    if (!at.lemma().equals(at.value())) {
                        tokens.add(at.lemma());
                    }
            }
            //if (token.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CD")) {
            //    System.out.println(token.lemma() + "\t" + token.get(CoreAnnotations.PartOfSpeechAnnotation.class) + "\t" + keyword);
            //}
        }
        return tokens;
    }

    /**
     * Replaces the words in the given string with their lemmas.
     * 
     * @param str The input string to be replaced with lemmas.
     * @return The string with words replaced by their lemmas.
     */
    public static String replaceLemmas(String str) {
        ArrayList<CoreLabel> annotatedTokens = Tokenizer.createAnnotatedTokens(str);
        String newStr = "";
        for (CoreLabel token : annotatedTokens) {
            newStr += token.lemma() + " ";
        }

        if (newStr.replaceAll(" ", "").equals(str.replaceAll(" ", ""))) {
            return str;
        }
        return newStr.trim();
    }

    /**
     * Identifies possible matches for tokens based on predefined categories and entries.
     * 
     * @param tokens A list of token strings to match.
     * @return A map of possible matches categorized by category and token groupings.
     */
    public static HashMap<Categories, HashMap<ArrayList<String>, String>> identifyPossibleMatches(List<String> tokens) {
        HashMap<Categories, HashMap<ArrayList<String>, String>> possibleMatches = new HashMap<>(); // keys = categories, values = token groupings/corresponding dict entries

        // check for token groupings that can form a dict entry
        for (Categories c : Categories.getValues()) {
            if (c == Categories.PRODUCT_CATEGORY) { // skip product category
                continue;
            }

            possibleMatches.put(c, new HashMap<>()); // initialize list for each category

            for (Entry entry : c.getEntries()) {
                for (String s : entry.getSynonyms()) {
                    ArrayList<String> matchingTokens = new ArrayList<>(); // list of tokens matched within a dict entry
                    String s2 = s.replaceAll(" ", "");

                    // identify when s contains a token
                    for (String t : tokens) {
                        if (s.contains(t) || s2.contains(t)) {
                            matchingTokens.add(t);
                            if (s.contains(t)) {
                                s = s.replace(t, "");
                            }
                            s2 = s2.replace(t, "");
                        }
                    }

                    // if s contains no material that is not part of a token, tokens can be replaced with s
                    s = s.replaceAll(" ", "");
                    s = s.replaceAll("-", "");
                    s = s.replaceAll(",", "");
                    s = s.replaceAll("\"", "");
                    if (s.equals("") || s2.equals("")) {
                        possibleMatches.get(c).put(matchingTokens, entry.getValue()); // add possible match
                    }
                }
            }
        }
        return possibleMatches;
    }

    /**
     * Removes extraneous tokens from a list based on their part of speech and other criteria.
     * 
     * @param annotatedTokens A list of annotated tokens.
     * @param tokens A list of token strings to filter.
     * @return The filtered list of tokens.
     */
    public static List<String> removeExtraneousTokens(ArrayList<CoreLabel> annotatedTokens, List<String> tokens) {
        for (CoreLabel at : annotatedTokens) {
            // CC and &     DT the no   IN with on fo w for at re in up     POS '   RP up   SYM + x ?   TO to
            if (at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CC") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("DT") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("IN") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("POS") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("RP") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("SYM") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("TO")) {
                tokens.remove(at.lemma());
            }
            if (!at.lemma().equals(at.value())) {
                if (!tokens.contains(at.value())) {
                    tokens.remove(at.lemma());
                }
                tokens.remove(at.value());
            }
        }

        for (String token : new ArrayList<>(tokens)) {
            if (token.length() <= 1 && !StringUtils.isNumeric(token) && !token.equals("x")) {
                tokens.remove(token);
            }
        }
        return tokens;
    }

    /**
     * Retokenizes all keywords and writes the updated keywords to a JSON file.
     * 
     * @throws RuntimeException If there is an error accessing or writing data.
     */
    public static void retokenizeAllKeywords() {
        ArrayList<SearchTerm> kwList = DataAccess.getSearchTermList("testData");
        // for (LinkedHashMap<String, Keyword> productMap : DataAccess.getMasterList().values()) {
        //     for (Keyword kw : productMap.values()) {
        //         kwList.add(kw);
        //     }
        // }

        for (SearchTerm kw : kwList) {
            kw.tokenize();
            kw.computeTF();
        }

        DataAccess.writeToJson("testData", kwList);
    }
}
