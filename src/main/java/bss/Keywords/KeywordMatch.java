package bss.Keywords;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a keyword that is matched against another keyword, with additional
 * properties for orders, CVR (conversion rate), and score. Used to evaluate and
 * adjust the weight of keywords based on how closely they match a search term.
 */
public class KeywordMatch extends Keyword {

    /** The number of orders associated with this keyword match. */
    int orders;

    /** The conversion rate (CVR) of this keyword match. */
    double cvr;

    /** The score representing the match strength between the keyword and the search term. */
    double score;

    /** List of terms that match this keyword. */
    ArrayList<String> matchingTerms;

    /** The original keyword that is being matched. */
    Keyword toMatch;

    /**
     * Constructs a {@link KeywordMatch} with the specified keyword.
     * 
     * @param s The keyword for this match.
     */
    public KeywordMatch(String s) {
        super(s);
    }

    /**
     * Constructs a {@link KeywordMatch} based on a {@link SearchTerm} and the keyword to match.
     * 
     * @param kw The {@link SearchTerm} used to initialize this match.
     * @param toMatch The {@link Keyword} to match against.
     */
    public KeywordMatch(SearchTerm kw, Keyword toMatch) {
        super(kw);
        this.score = 0;
        this.orders = kw.orders;
        this.cvr = kw.cvr;
        this.matchingTerms = new ArrayList<>();
        this.toMatch = toMatch;
    }
    
    /**
     * Constructs a {@link KeywordMatch} based on an existing {@link Keyword} and a score.
     * 
     * @param kw The {@link Keyword} used to initialize this match.
     * @param score The score representing the match strength.
     */
    public KeywordMatch(Keyword kw, double score) {
        super(kw);
        this.score = score;
    }

    /**
     * Gets the number of orders associated with this keyword match.
     * 
     * @return The number of orders.
     */
    public int getOrders() {
        return this.orders;
    }

    /**
     * Gets the conversion rate (CVR) for this keyword match.
     * 
     * @return The CVR.
     */
    public double getCVR() {
        return this.cvr;
    }

    /**
     * Gets the score representing the match strength between the keyword and the search term.
     * 
     * @return The score.
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Sets the score representing the match strength between the keyword and the search term.
     * 
     * @param score The score to set.
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Adjusts the term frequency (TF) weights based on category and attribute weights.
     * 
     * @param categoryWeights A map of category weights.
     * @param attributeWeights A map of attribute weights.
     */
    public void weightTF(HashMap<Categories, Double> categoryWeights, HashMap<String, Double> attributeWeights) {
        //System.out.println(this.keyword);
        //System.out.println(this.tf);
        for (Categories c : this.categorizedTokens.keySet()) {
            if (categoryWeights.containsKey(c)) {
                for (String token : this.categorizedTokens.get(c)) {
                    if (!c.containsSynonym(toMatch.getCategorizedTokens().get(c), token)) {
                        this.tf.replace(token, this.tf.get(token) - categoryWeights.get(c));
                        //System.out.println(this.tf.get(token));
                    }
                }
                if (this.categorizedTokens.get(c).isEmpty()) {
                    for (String token : toMatch.getCategorizedTokens().get(c)) {
                        if (!this.tf.containsKey(token)) {
                            //this.tf.put(token, -categoryWeights.get(c) / 5);
                        }
                    }
                }
            }
            else if (c == Categories.ATTRIBUTES && !attributeWeights.isEmpty()) {
                double biggestWeight = 0;
                for (String attributeStr : attributeWeights.keySet()) {
                    if (attributeWeights.get(attributeStr) > biggestWeight) {
                        biggestWeight = attributeWeights.get(attributeStr);
                    }
                }
                for (String token : this.categorizedTokens.get(c)) {
                    if (attributeWeights.containsKey(token)) {

                    }
                    else {
                        this.tf.replace(token, this.tf.get(token) - biggestWeight);
                    }
                }
            }
            else if (c == Categories.OTHER) {
                for (String token : this.categorizedTokens.get(c)) {
                    this.tf.replace(token, this.tf.get(token) - 0.3);
                }
            }
        }
        //System.out.println(this.tf);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws UnsupportedOperationException Always thrown, as this method is not implemented.
     */
    @Override
    public void tokenize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tokenize'");
    }
}
