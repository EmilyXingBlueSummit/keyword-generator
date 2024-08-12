package bss.KeywordGeneration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Provides methods for computing cosine similarity and related metrics.
 */
public class CosineSimilarity {

    /**
     * Computes the Inverse Document Frequency (IDF) for each unique keyword.
     * 
     * @param totalKeywords a list of lists, where each inner list contains tokens (keywords) from a document
     * @return a map where the keys are unique tokens and the values are their IDF scores
     */
    public static Map<String, Double> computeIDF(List<List<String>> totalKeywords) {
        Map<String, Double> idf = new HashMap<>();
        int kwCount = totalKeywords.size();
        for (List<String> tokens : totalKeywords) {
            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String token : uniqueTokens) {
                idf.put(token, idf.getOrDefault(token, 0.0) + 1.0);
            }
        }
        for (Map.Entry<String, Double> entry : idf.entrySet()) {
            idf.put(entry.getKey(), Math.log(kwCount / entry.getValue()));
        }
        return idf;
    }

    /**
     * Creates a TF-IDF vector from term frequency (TF) and IDF scores.
     * 
     * @param tf a map where the keys are terms and the values are their term frequencies
     * @param idf a map where the keys are terms and the values are their inverse document frequencies
     * @param vocabulary a set of all terms in the vocabulary
     * @return a RealVector representing the TF-IDF vector
     */
    public static RealVector createTFIDFVector(Map<String, Double> tf, Map<String, Double> idf, Set<String> vocabulary) {
        //Map<String, Double> tfidf = new HashMap<>();
        double[] tfidfValues = new double[vocabulary.size()];
        int i = 0;
        for (String term : vocabulary) {
            double tfidf = tf.getOrDefault(term, 0.0) * idf.getOrDefault(term, 0.0);
            if (tfidf != 0.0) {
                //System.out.println(term + "\t" + Double.toString(tfidf));
            }
            tfidfValues[i++] = tfidf;
        }
        //KeywordGenerator.tfidf = new HashMap<>();
        //for (String term : tf.keySet()) {
        //    KeywordGenerator.tfidf.put(term, tf.get(term) * idf.getOrDefault(term, 0.0));
        //}
        //return new ArrayRealVector(tfidf.values().stream().mapToDouble(Double::doubleValue).toArray());

        return new ArrayRealVector(tfidfValues);
    }

    /**
     * Computes the cosine similarity between two vectors.
     * <p>
     * The vectors are padded with zeros if they have different dimensions.
     * </p>
     * 
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the cosine similarity between the two vectors, or 0 if the similarity is NaN
     */
    public static double computeCosineSimilarity(RealVector v1, RealVector v2) {
        if (v1.getDimension() < v2.getDimension()) {
            while (v1.getDimension() < v2.getDimension()) {
                v1 = v1.append(0.0);
            }
        }
        else if (v1.getDimension() > v2.getDimension()) {
            while (v1.getDimension() > v2.getDimension()) {
                v2 = v2.append(0.0);
            }
        }
        double cosineSimilarity = v1.dotProduct(v2) / (v1.getNorm() * v2.getNorm());
        if (Double.isNaN(cosineSimilarity)) {
            return 0;
        }
        return cosineSimilarity;
    }
}
