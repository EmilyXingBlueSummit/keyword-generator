package bss.Keywords;

import java.util.ArrayList;

/**
 * Represents a list of {@link Entry} objects with additional utility methods.
 */
public class EntryList extends ArrayList<Entry> {

    /**
     * Retrieves the {@link Entry} object associated with the specified term.
     * 
     * @param str The primary term to search for.
     * @return The {@link Entry} object if found; otherwise, {@code null}.
     */
    public Entry get(String str) {
        if (contains(str)) {
            return get(indexOf(new Entry(str)));
        }
        else {
            return null;
        }
    }

    /**
     * Constructs an empty {@link EntryList}.
     */
    public EntryList() {
        super();
    }

    /**
     * Checks if the list contains an entry with the specified term.
     * 
     * @param str The primary term to check.
     * @return {@code true} if an entry with the specified term is in the list; otherwise, {@code false}.
     */
    public boolean contains(String str) {
        return contains(new Entry(str));
    }

    /**
     * Adds a new {@link Entry} with the specified term to the list.
     * 
     * @param str The primary term for the new entry.
     */
    public void add(String str) {
        add(new Entry(str));
    }

    /**
     * Removes the {@link Entry} with the specified term from the list.
     * 
     * @param str The primary term of the entry to remove.
     */
    public void remove(String str) {
        remove(new Entry(str));
    }

    /**
     * Converts the list of {@link Entry} objects to a list of primary terms.
     * 
     * @return An {@link ArrayList} of primary terms.
     */
    public ArrayList<String> toStr() {
        ArrayList<String> strList = new ArrayList<>();
        for (Entry entry : this) {
            strList.add(entry.str);
        }
        return strList;
    }
}
