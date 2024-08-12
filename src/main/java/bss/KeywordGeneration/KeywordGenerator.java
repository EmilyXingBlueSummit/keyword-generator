package bss.KeywordGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.RealVector;

import bss.Data.DataAccess;
import bss.Keywords.Keyword;
import bss.Keywords.KeywordDictionary;
import bss.Keywords.Categories;
import bss.Keywords.KeywordMatch;
import bss.Keywords.SearchTerm;

/**
 * Generates a sorted list of keywords based on relevance to a given input keyword or attribute set.
 * <p>
 * This class provides functionality to generate and sort keywords by their relevance to a given keyword,
 * utilizing various weights and similarity metrics.
 * </p>
 */
public class KeywordGenerator {

    /** Singleton instance of the KeywordGenerator class. */
    static KeywordGenerator instance = null;

    /** Weights assigned to different categories for keyword relevance. */
    public static HashMap<Categories, Double> categoryWeights;

    /** Weights assigned to different attributes for keyword relevance. */
    public static HashMap<String, Double> attributeWeights;

    /** Minimum number of orders required for a keyword to be considered. */
    public static int minOrders = 0;

    /** Minimum conversion rate (CVR) required for a keyword to be considered. */
    public static double minCVR = 0;

    /**
     * Private constructor to prevent instantiation.
     */
    private KeywordGenerator() {
        KeywordGenerator.categoryWeights = new HashMap<>();
        KeywordGenerator.attributeWeights = new HashMap<>();
    }

    /**
     * A test method for keyword generation (currently commented out).
     */
    public static void test() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("2 divider");
        // Keyword kw = new Keyword("classification folder", "8.5 x 14", "blue", "10", "paper", arr);
        // ArrayList<KeywordMatch> matchedList = KeywordGenerator.generate(kw);
        // KeywordListPanel.getInstance().updateList(matchedList);

        //System.out.println(matchedList.get(1).keyword);
        //matchedList.get(1).reorderTokens(kw.tokens);
        //System.out.println(matchedList.get(1).tokens);
    }

    /**
     * Sets the category weights used for keyword relevance calculation.
     * 
     * @param weights a map of category weights
     */
    public static void setCategoryWeights(HashMap<Categories, Double> weights) {
        KeywordGenerator.categoryWeights = weights;
    }

    /**
     * Adds or updates the weight for a specific category.
     * 
     * @param c the category
     * @param d the weight for the category
     */
    public static void addCategoryWeight(Categories c, double d) {
        KeywordGenerator.categoryWeights.put(c, d);
    }

    /**
     * Removes the weight for a specific category.
     * 
     * @param c the category to remove
     */
    public static void removeCategoryWeight(Categories c) {
        if (KeywordGenerator.categoryWeights.containsKey(c)) {
            KeywordGenerator.categoryWeights.remove(c);
        }
    }

    /**
     * Adjusts the weight for a specific category.
     * 
     * @param c the category
     * @param d the new weight for the category
     */
    public static void adjustCategoryWeight(Categories c, double d) {
        KeywordGenerator.categoryWeights.replace(c, d);
    }

    /**
     * Sets the attribute weights used for keyword relevance calculation.
     * 
     * @param weights a map of attribute weights
     */
    public static void setAttributeWeights(HashMap<String, Double> weights) {
        KeywordGenerator.attributeWeights = weights;
    }

    /**
     * Adjusts the weight for a specific attribute.
     * 
     * @param s the attribute
     * @param d the new weight for the attribute
     */
    public static void adjustAttributeWeight(String s, double d) {
        KeywordGenerator.attributeWeights.replace(s, d);
    }

    /**
     * Redistributes the tokens of a keyword based on category and attribute weights.
     * 
     * @param keyword the keyword to redistribute
     */
    private static void redistributeKeyword(Keyword keyword) {
        ArrayList<String> redistributedTokens = new ArrayList<>();
        for (Categories c : categoryWeights.keySet()) {
            for (int i = 0; i <= KeywordGenerator.categoryWeights.get(c) * 100; i++) {
                redistributedTokens.add(keyword.getCategorizedTokens().get(c).get(0));
            }
        }

        if (!attributeWeights.isEmpty()) {
            for (String token : keyword.getCategorizedTokens().get(Categories.ATTRIBUTES)) {
                if (KeywordGenerator.attributeWeights.containsKey(token)) {
                    for (int i = 0; i <= KeywordGenerator.attributeWeights.get(token) * 100; i++) {
                        redistributedTokens.add(token);
                    }
                }
                else {
                    redistributedTokens.add(token);
                }
            }
        }

        keyword.computeTF(redistributedTokens);
    }

    /**
     * Generates a sorted list of keyword matches based on the relevance to the given keyword.
     * 
     * @param keyword the input keyword to match against
     * @return a sorted list of {@link KeywordMatch} objects
     */
    public static ArrayList<KeywordMatch> generate(Keyword keyword) {
        ArrayList<SearchTerm> kwList = DataAccess.getSearchTermList("testData");
        redistributeKeyword(keyword);

        ArrayList<KeywordMatch> matchedList = new ArrayList<>();
        List<List<String>> totalTokens = new ArrayList<>();
        for (SearchTerm kw : kwList) {
            if (kw.getOrders() >= KeywordGenerator.minOrders && kw.getCVR() >= KeywordGenerator.minCVR) {
                //kw.tokenize();
                //kw.computeTF();
                totalTokens.add(kw.getTokens());
                // KeywordDictionary.getInstance().checkTokens(kw);
                KeywordMatch kwMatch = new KeywordMatch(kw, keyword);
                kwMatch.weightTF(KeywordGenerator.categoryWeights, KeywordGenerator.attributeWeights);
                matchedList.add(kwMatch);
                //break;
            }
        }
        kwList.clear();
        //DataAccess.writeToJson("keywordList", kwList);

        Map<String, Double> idf = CosineSimilarity.computeIDF(totalTokens);
        //System.out.println(idf.keySet());

        Set<String> vocabulary = new HashSet<>();
        for (List<String> tokens : totalTokens) {
            vocabulary.addAll(tokens);
        }
        vocabulary.addAll(keyword.getTokens());

        // HashMap<KeywordMatch, RealVector> vectors = new HashMap<>();
        RealVector keywordVector = CosineSimilarity.createTFIDFVector(keyword.getTF(), idf, vocabulary);
        for (KeywordMatch kw : matchedList) {
            //System.out.println(kw.getKeyword());
            RealVector vector = CosineSimilarity.createTFIDFVector(kw.getTF(), idf, vocabulary);
            // vectors.put(kw, vector);

            if (kw == matchedList.get(0)) {
                //System.out.println(KeywordGenerator.tfidf);
            }

            double cs = CosineSimilarity.computeCosineSimilarity(vector, keywordVector);
            kw.setScore(cs);
            //break;
        }

        // RealVector keywordVector = CosineSimilarity.createTFIDFVector(keyword.getTF(), idf, vocabulary);
        //System.out.println(KeywordGenerator.tfidf);
        
        // for (KeywordMatch kw : matchedList) {
        //     double cs = CosineSimilarity.computeCosineSimilarity(vectors.get(kw), keywordVector);
        //     kw.setScore(cs);
        // }

        //System.out.println(matchedList.get(0).keyword);
        //System.out.println(kwList.get(0).getTokens());
        //System.out.println(kwList.get(0).tf);
        //System.out.println(vectors.get(0));
        //System.out.println(matchedList.get(0).score);
        
        KeywordGenerator.sortByScore(matchedList);
        System.out.println("generation done");

        return matchedList;
    }

    /**
     * Sorts a list of keyword matches by their relevance score in descending order.
     * 
     * @param matchedList the list of {@link KeywordMatch} objects to sort
     */
    public static void sortByScore(ArrayList<KeywordMatch> matchedList) {
        Collections.sort(matchedList, new Comparator<KeywordMatch>() {
            @Override
            public int compare(KeywordMatch kw1, KeywordMatch kw2) {
                return (int) (10000 * (kw2.getScore() - kw1.getScore()));
            }
        });
    }

    /**
     * Returns the singleton instance of the KeywordGenerator class.
     * <p>
     * Initializes the instance if it does not already exist.
     * </p>
     * 
     * @return the singleton instance of KeywordGenerator
     */
    public static KeywordGenerator getInstance() {
        if (KeywordGenerator.instance == null) {
            KeywordGenerator.instance = new KeywordGenerator();
        }
        return KeywordGenerator.instance;
    }
}
