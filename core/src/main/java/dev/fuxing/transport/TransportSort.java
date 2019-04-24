package dev.fuxing.transport;

/**
 * Sort by.
 * <p>
 * For string: alphabetical, lexicographically
 * For integer: arithmetic
 */
public enum TransportSort {

    /**
     * Non-natural sort.
     * From big to small.
     * f, a, 9, 5, 4, 0
     * <p>
     * Integer:
     * 100 &#x3E; 0: True
     * 0 &#x3E; 100: False
     * <p>
     * String:
     * "F" &#x3E; "B": True
     * "F" &#x3E; "BA": True
     * "FF" &#x3E; "FB": True
     * "FA" &#x3E; "FB": False
     * <p>
     * To ensure string are sorted properly.
     * - Make sure the field is the same size for all values.
     * - Don't use non sortable values e.g.
     */
    desc,

    /**
     * Natural sort.
     * From small to big.
     * 0, 1, 3, 9, a, f
     */
    asc;

    /**
     * Default sort direction.
     */
    static TransportSort DEFAULT = desc;

    /**
     * This is in compliant with dynamodb index direction.
     *
     * @return scan index direction. (natural)
     */
    public boolean isForward() {
        switch (this) {
            case desc:
                return false;

            case asc:
            default:
                return true;
        }
    }

    /**
     * @return whether the direction is less than
     */
    public boolean isLT() {
        switch (this) {
            case desc:
                return true;

            case asc:
            default:
                return false;
        }
    }

    /**
     * @return whether the direction is greater than
     */
    public boolean isGT() {
        return !isLT();
    }
}
