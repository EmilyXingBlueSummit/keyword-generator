package bss.Keywords;

import java.util.ArrayList;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.StringUtils;

/**
 * Represents a search term that a user has entered, used for searching on Amazon.
 */
public class SearchTerm extends Keyword {

    /** The keyword text for the search term. */
    String keyword; // keyword_text

    /** The name of the campaign associated with the search term. */
    String campaignName; // campaign_name

    /** The match type for the keyword (e.g., exact, broad). */
    String keywordMatchType; // keyword_match_type
    
    /** The name of the ad group associated with the search term. */
    String adGroupName; // ad_group_name

    /** The number of clicks associated with the search term. */
    int clicks; // clicks__sum

    /** The total cost associated with the search term. */
    double cost; // cost__sum

    /** The total sales attributed to the search term. */
    double sales; // attributed_sales_14_day__sum

    /** The number of orders attributed to the search term. */
    int orders; // attributed_conversions_14_day__sum

    /** The number of impressions associated with the search term. */
    int impressions; // impressions__sum

    /** The conversion rate, calculated as orders divided by clicks. */
    double cvr; // orders / clicks

    /**
     * Constructs a SearchTerm with the given query, number of orders, and conversion rate.
     * 
     * @param query The user's search query.
     * @param orders The number of orders attributed to the search term.
     * @param cvr The conversion rate (orders divided by clicks).
     */
    public SearchTerm(String query, int orders, double cvr) {
        super(query);
        this.orders = orders;
        this.cvr = cvr;
    }

    /**
     * Constructs a SearchTerm with the given details.
     * 
     * @param keyword The keyword text for the search term.
     * @param campaignName The name of the campaign associated with the search term.
     * @param query The user's search query.
     * @param keywordMatchType The match type for the keyword.
     * @param adGroupName The name of the ad group associated with the search term.
     * @param clicks The number of clicks associated with the search term.
     * @param cost The total cost associated with the search term.
     * @param sales The total sales attributed to the search term.
     * @param orders The number of orders attributed to the search term.
     * @param impressions The number of impressions associated with the search term.
     */
    public SearchTerm(String keyword, String campaignName, String query, String keywordMatchType, String adGroupName, int clicks, double cost, double sales, int orders, int impressions) {
        super(query);
        this.keyword = keyword;
        this.campaignName = campaignName;
        this.keywordMatchType = keywordMatchType;
        this.adGroupName = adGroupName;
        this.clicks = clicks;
        this.cost = cost;
        this.sales = sales;
        this.orders = orders;
        this.impressions = impressions;
        this.cvr = (double) orders / clicks;
    }

    /**
     * Copy constructor to create a new SearchTerm from an existing one.
     * 
     * @param searchTerm The SearchTerm to copy.
     */
    public SearchTerm(SearchTerm searchTerm) {
        super(searchTerm);
        this.orders = searchTerm.orders;
        this.cvr = searchTerm.cvr;
    }

    /**
     * Constructs a SearchTerm with the given product details and attributes.
     * 
     * @param productType The type of the product.
     * @param dimensions The dimensions of the product.
     * @param color The color of the product.
     * @param quantity The quantity of the product.
     * @param material The material of the product.
     * @param attributes Additional attributes of the product.
     */
    public SearchTerm(String productType, String dimensions, String color, String quantity, String material, ArrayList<String> attributes) {
        super(productType, dimensions, color, quantity, material, attributes);
    }

    /**
     * Gets the number of orders attributed to the search term.
     * 
     * @return The number of orders.
     */    
    public int getOrders() {
        return this.orders;
    }

    /**
     * Gets the conversion rate, calculated as orders divided by clicks.
     * 
     * @return The conversion rate.
     */
    public double getCVR() {
        return this.cvr;
    }

    /**
     * Tokenizes the search query and processes the tokens.
     * <p>
     * This implementation tokenizes the query, removes certain tokens based on part-of-speech,
     * and handles numbers and special cases specific to search terms.
     * </p>
     */
    @Override
    public void tokenize() {
        this.tokens = new ArrayList<>();

        ArrayList<CoreLabel> annotatedTokens = Tokenizer.createAnnotatedTokens(this.query);
        
        for (CoreLabel at : annotatedTokens) {
            if (at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CD")) {
                Object num = at.get(CoreAnnotations.NumericValueAnnotation.class);
                if (num != null) {
                    if (num instanceof Integer) {
                        this.tokens.add(Integer.toString((int) num));
                    }
                    else if (num instanceof Long) {
                        this.tokens.add(Long.toString((Long) num));
                    }
                    else if (num instanceof Double) {
                        this.tokens.add(Double.toString((Double) num));
                    }
                    else {
                        this.tokens.add(at.value());
                        if (!at.lemma().equals(at.value())) {
                            this.tokens.add(at.lemma());
                        }
                    }
                }
                else {
                    this.tokens.add(at.value());
                        if (!at.lemma().equals(at.value())) {
                            this.tokens.add(at.lemma());
                        }
                }
            }
            else {
                this.tokens.add(at.value());
                    if (!at.lemma().equals(at.value())) {
                        this.tokens.add(at.lemma());
                    }
            }
            //if (token.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CD")) {
            //    System.out.println(token.lemma() + "\t" + token.get(CoreAnnotations.PartOfSpeechAnnotation.class) + "\t" + this.query);
            //}
        }
        //SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        //this.tokens.addAll((List<String>) Arrays.asList(tokenizer.tokenize(kw.toLowerCase())));
        //System.out.println(this.tokens);
        Categories.replaceSynonyms(this.tokens);
        
        //initializeCategorizedTokens();
        //System.out.println(this.tokens);

        for (CoreLabel at : annotatedTokens) {
            // CC and &     DT the no   IN with on fo w for at re in up     POS '   RP up   SYM + x ?   TO to
            if (at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("CC") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("DT") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("IN") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("POS") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("RP") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("SYM") || at.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals("TO")) {
                //System.out.println(at.lemma() + "\t" + at.get(CoreAnnotations.PartOfSpeechAnnotation.class) + "\t" + this.query);
                this.tokens.remove(at.lemma());
            }
            if (!at.lemma().equals(at.value())) {
                if (!this.tokens.contains(at.value())) {
                    this.tokens.remove(at.lemma());
                }
                this.tokens.remove(at.value());
            }
        }

        for (String token : new ArrayList<>(this.tokens)) {
            if (token.length() <= 1 && !StringUtils.isNumeric(token) && !token.equals("x")) {
                this.tokens.remove(token);
            }
            else if (token.equals(".")) {
                this.tokens.remove(token);
            }
        }

        this.categorizeTokens();
    }
}
