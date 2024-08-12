package bss.Keywords;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single dictionary entry with a primary term and associated synonyms.
 */
public class Entry {

    /** The primary term for this dictionary entry. */
    String str;

    /** List of synonyms for the primary term. */
    ArrayList<String> synonyms;

    /**
     * Constructs an Entry with the specified term.
     * 
     * @param str The primary term for the entry.
     */
    public Entry(String str) {
        this.str = str;
        this.synonyms = new ArrayList<>();
    }

    /**
     * Gets the primary term of this entry.
     * 
     * @return The primary term.
     */
    public String getValue() {
        return this.str;
    }
    
    /**
     * Gets the list of synonyms for this entry.
     * 
     * @return The list of synonyms.
     */
    public ArrayList<String> getSynonyms() {
        return this.synonyms;
    }

    /**
     * Compares this entry to another object for equality.
     * 
     * @param o The object to compare to.
     * @return True if the object is an Entry with the same primary term, or a String with the same value; otherwise, false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof Entry) {
            Entry e = (Entry) o;
            return this.str.equals(e.str);
        }

        if (o instanceof String) {
            return this.str.equals((String) o);
        }

        return false;
    }
}