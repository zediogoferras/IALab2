import java.util.List;

public interface Ilayout {
    /**
     * @return the children of the receiver
     */
    List<Ilayout> children();

    /**
     * @return true if the receiver equals the argument 1; false otherwise
     */
    boolean isGoal(Ilayout l);

    /**
     * @return the cost from the receiver to the successor
     */
    double getG();

    /**
     * @return heuristic value of the designated layout
     */
    double heuristics(Ilayout l);
}
