/**
 * Class that defines the Container
 */
@SuppressWarnings({"unchecked"})
public class Container implements Comparable<Container>{
    public char id;
    public int cost;

    /**
     * Container constructor
     * @param id identifies the Container
     * @param cost of Container
     */
    public Container(char id, int cost){
        this.id = id;
        this.cost = cost;
    }

    /**
     * toString method
     * @return container in string format
     */
    public String toString(){
        String str = "";
        str += this.id;
        return str;
    }

    /**
     * Equals method
     * @param o Container to be compared with this
     * @return true if they have the same id, false if not
     */
    @Override
    public boolean equals(Object o){
        Container that = (Container) o;
        return this.id == that.id;
    }

    /**
     * hashCode method
     * @return hashCode of the string format of the board
     */
    @Override
    public int hashCode(){
        return this.id;
    }

    /**
     * CompareTo method
     * @param o the object to be compared.
     * @return character of id comparison
     */
    @Override
    public int compareTo(Container o) {
        return Character.compare(this.id, o.id);
    }
}
