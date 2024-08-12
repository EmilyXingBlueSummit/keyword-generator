package bss.Keywords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a keyword with associated metadata.
 */
public abstract class Keyword {

    /** The query or description of the keyword. */
    public String query;

    /** List of tokens extracted from the keyword. */
    List<String> tokens = new ArrayList<>();

    /** Categorized tokens based on predefined categories. */
    LinkedHashMap<Categories, List<String>> categorizedTokens = new LinkedHashMap<>();

    /** Term frequencies of tokens in the keyword. */
    Map<String, Double> tf = new HashMap<>();

    /**
     * Constructs a Keyword with the given query and processes it.
     * 
     * @param query the keyword query
     */
    public Keyword(String query) {
        this.query = query.toLowerCase();
        this.tokenize();
        this.computeTF();
    }

    /**
     * Copy constructor to create a new Keyword from an existing one.
     * 
     * @param kw The Keyword to copy.
     */
    public Keyword(Keyword kw) {
        this.query = kw.query;
        this.tokens = kw.tokens;
        this.categorizedTokens = kw.categorizedTokens;
        this.tf = new HashMap<>();
        this.tf.putAll(kw.tf);
    }

    /**
     * Constructs a Keyword with product-specific details.
     * 
     * @param productType The product type.
     * @param dimensions The dimensions of the product.
     * @param color The color of the product.
     * @param quantity The quantity of the product.
     * @param material The material of the product.
     * @param attributes Additional attributes of the product.
     */
    public Keyword(String productType, String dimensions, String color, String quantity, String material, ArrayList<String> attributes) {
        this.query = "";
        this.tokens = new ArrayList<>();
        initializeCategorizedTokens();

        addCategory(productType, Categories.PRODUCT_TYPE);
        addCategory(dimensions, Categories.DIMENSIONS);
        addCategory(color, Categories.COLOR);
        addCategory(quantity, Categories.QUANTITY);
        addCategory(material, Categories.MATERIAL);

        for (String s : attributes) {
            addCategory(s, Categories.ATTRIBUTES);
        }

        this.computeTF();
    }

    /**
     * Adds a token to a specific category.
     * 
     * @param s The token to add.
     * @param c The category to which the token belongs.
     */
    private void addCategory(String s, Categories c) {
        if (!s.equals("")) {
            this.query += " " + s;
            this.tokens.add(s);
            this.categorizedTokens.get(c).add(s);
        }
    }

    /**
     * Gets the keyword's query or description.
     * 
     * @return The keyword's query.
     */
    public String getKeyword() {
        return this.query;
    }

    /**
     * Gets the list of tokens extracted from the keyword.
     * 
     * @return The list of tokens.
     */
    public List<String> getTokens() {
        return this.tokens;
    }

    /**
     * Gets the categorized tokens.
     * 
     * @return The map of categorized tokens.
     */
    public LinkedHashMap<Categories, List<String>> getCategorizedTokens() {
        return this.categorizedTokens;
    }

    /**
     * Gets the term frequencies of the tokens.
     * 
     * @return The map of term frequencies.
     */
    public Map<String, Double> getTF() {
        return this.tf;
    }

    /**
     * Tokenizes the keyword's query or description.
     */
    public abstract void tokenize();

    /**
     * Categorizes the tokens based on predefined categories.
     */
    public void categorizeTokens() {
        initializeCategorizedTokens();
        for (String token : this.tokens) {
            this.categorizedTokens.get(Categories.checkTokenCategory(token)).add(token);
        }

        for (String token : this.categorizedTokens.get(Categories.PRODUCT_TYPE)) {
            this.categorizedTokens.get(Categories.PRODUCT_CATEGORY).add(Categories.PRODUCT_TYPE.getProductCategory(token));
        }
    }

    /**
     * Initializes the map of categorized tokens with empty lists.
     */
    protected void initializeCategorizedTokens() {
        this.categorizedTokens = new LinkedHashMap<>();
        this.categorizedTokens.put(Categories.PRODUCT_CATEGORY, new ArrayList<>());
        this.categorizedTokens.put(Categories.PRODUCT_TYPE, new ArrayList<>());
        this.categorizedTokens.put(Categories.DIMENSIONS, new ArrayList<>());
        this.categorizedTokens.put(Categories.COLOR, new ArrayList<>());
        this.categorizedTokens.put(Categories.QUANTITY, new ArrayList<>());
        this.categorizedTokens.put(Categories.MATERIAL, new ArrayList<>());
        this.categorizedTokens.put(Categories.ATTRIBUTES, new ArrayList<>());
        this.categorizedTokens.put(Categories.OTHER, new ArrayList<>());
    }

    /**
     * Computes term frequencies for the tokens.
     */
    public void computeTF() {
        this.tf = new HashMap<>();
        int tokenCount = this.tokens.size();
        for (String token : this.tokens) {
            this.tf.put(token, this.tf.getOrDefault(token, 0.0) + 1.0);
        }
        for (Map.Entry<String, Double> entry :  this.tf.entrySet()) {
            this.tf.put(entry.getKey(), entry.getValue() / tokenCount);
        }
    }

    /**
     * Computes term frequencies based on redistributed tokens.
     * 
     * @param redistributedTokens The list of redistributed tokens.
     */
    public void computeTF(ArrayList<String> redistributedTokens) {
        this.tf = new HashMap<>();
        int tokenCount = redistributedTokens.size();
        for (String token : redistributedTokens) {
            this.tf.put(token, this.tf.getOrDefault(token, 0.0) + 1.0);
        }
        for (Map.Entry<String, Double> entry :  this.tf.entrySet()) {
            this.tf.put(entry.getKey(), entry.getValue() / tokenCount);
        }
    }

}