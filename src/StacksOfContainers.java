import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Class that will represent the different stacks of containers
 */
@SuppressWarnings({"unchecked"})
public class StacksOfContainers implements Ilayout, Cloneable{

    private ArrayList<Stack<Container>> stacksOfContainers;

    private Container lastMovedContainer;

    private String str; //stores the string so we only need to generate it once
    private int hc; //stores the hashcode so we only need to generate it once

    /**
     * Constructor to help with the way different data is given in the input
     * @param str linear string defining stacks of containers layout
     * @param goal is it goal or is it initial layout (are given in different strings) true if it's goal, false if not
     * @throws IllegalStateException if the string isn't formatted correctly
     */
    public StacksOfContainers(String str, boolean goal){
        if(str.isEmpty()){
            throw new IllegalStateException("Invalid arg in StacksOfContainers constructor");
        }
        this.stacksOfContainers = new ArrayList<>();
        this.lastMovedContainer = null;
        String[] aos = str.split(" ");
        if(!goal){
            for(String s : aos){
                Stack<Container> currentStack = new Stack<>();
                for(int i = 0; i < s.length()/2; i++) {
                    char id;
                    int cost;
                    try {
                        id = s.charAt(i * 2);
                        cost = Character.getNumericValue(s.charAt(i * 2 + 1));
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("Invalid arg in StacksOfContainers constructor");
                    }
                    Container currentContainer = new Container(id, cost);
                    currentStack.push(currentContainer);
                }
                this.stacksOfContainers.add(currentStack);
            }
        }
        else{
            this.stacksOfContainers = new ArrayList<>();
            this.lastMovedContainer = null;
            for(String s : aos){
                Stack<Container> currentStack = new Stack<>();
                for(int i = 0; i < s.length(); i++) {
                    char id;
                    try {
                        id = s.charAt(i);
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("Invalid arg in StacksOfContainers constructor");
                    }
                    Container currentContainer = new Container(id, 0);
                    currentStack.push(currentContainer);
                }
                this.stacksOfContainers.add(currentStack);
            }
        }
        this.str = null;
        this.hc = 0;
    }

    /**
     * StacksOfContainers constructor
     * @param soc new stacks of containers
     * @param c last moved container
     */
    public StacksOfContainers(ArrayList<Stack<Container>> soc, Container c){
        this.stacksOfContainers = soc;
        this.lastMovedContainer = c;
        this.str = null;
        this.hc = 0;
    }

    /**
     * clone method for StacksOfContainers class
     * @return clone (deep copy)
     */
    @Override
    public StacksOfContainers clone(){
        ArrayList<Stack<Container>> clonedAL= new ArrayList<>();
        for(Stack<Container> s : this.stacksOfContainers){
            Stack<Container> currentStack = (Stack<Container>) s.clone();
            clonedAL.add(currentStack);
        }
        return new StacksOfContainers(clonedAL, this.lastMovedContainer);
    }

    /**
     * Returns all the successors of the current layout of the stacks of containers
     * (basically all moves possible regarding 1 container moving)
     * @return children of current stacks of containers layout
     */
    @Override
    public List<Ilayout> children() {
        //we select a Container, get it in any other possible position in 1 iteration
        //and do it again to all containers that are available to move.

        List<Ilayout> children = new ArrayList<>();

        //use this loop because we will use 1 container of each stack of containers
        for(int i = 0; i < this.stacksOfContainers.size(); i++){
            //current stack chosen will be skipped inside the inner loop because we can't place
            //a container where it already is
            Container currentContainer = this.stacksOfContainers.get(i).peek();

            for(int j = 0; j < this.stacksOfContainers.size(); j++){
                //we aren't going to place a container where it already is
                if(j == i){
                    continue;
                }
                StacksOfContainers currentChild = this.clone();

                //save the last moved container into the child
                currentChild.lastMovedContainer = currentContainer;

                //pop the container that we are using as currentContainer
                currentChild.stacksOfContainers.get(i).pop();

                //push the container that has been popped from another stack into a new one
                currentChild.stacksOfContainers.get(j).push(currentContainer);

                //if the stack where the currentContainer has been popped from is now empty
                //we remove it
                if(currentChild.stacksOfContainers.get(i).isEmpty()){
                    currentChild.stacksOfContainers.remove(i);
                }
                children.add(currentChild);
            }
            //case where stack in i postion has more than 1 element, so we need to create one extra case
            //where the element that is going to be popped can be in a new stack at the end of the list
            if(this.stacksOfContainers.get(i).size() > 1){
                StacksOfContainers currentChild = this.clone();
                currentChild.stacksOfContainers.get(i).pop();
                currentChild.lastMovedContainer = currentContainer;
                Stack<Container> newStack = new Stack<>();
                newStack.push(currentContainer);
                currentChild.stacksOfContainers.add(newStack);
                children.add(currentChild);
            }
        }
        return children;
    }

    /**
     * Verifies current layout has reached goal or not
     * It also makes the stacks that are already right to be inserted into verified
     * @param l goal layout
     * @return true if we have reached it, false if not
     */
    @Override
    public boolean isGoal(Ilayout l) {
        StacksOfContainers goal = (StacksOfContainers) l;
        return this.equals(goal);
    }

    /**
     * Getter of cost
     * @return cost of last operation done to the stacksOfContainers in order to get to the current layout
     */
    @Override
    public double getG() {
        return this.lastMovedContainer.cost;
    }

    /**
     * Finds the stack where Container c is
     * Places in the hashMap all the containers that are below it (excludes itself)
     * @param c Container
     * @return hashMap with all the containers that are below it in a certain stack
     */
    public HashSet<Container> findContainersBelow(Container c){
        HashSet<Container> result = new HashSet<>();
        for(Stack<Container> s0 : this.stacksOfContainers){
            if(s0.contains(c)){
                for(int i = s0.indexOf(c)-1; i >= 0; i--){
                    result.add(s0.get(i));
                }
                break;
            }
        }
        return result;
    }

    /**
     * heuristics() will use the h0 heuristic function
     * to calculate the heuristic value of the current layout of containers
     * @param l goal (objective)
     * @return heuristic value of current layout of containers
     */
    public double heuristics(Ilayout l){
        StacksOfContainers goal = (StacksOfContainers) l;
        return this.h0(goal);
    }

    /**
     * h0 heuristic function
     * takes into account the cost of each container that will need to get moved at least
     * either once or twice for us to be closer to the objective
     * @param goal objective
     * @return heuristic h0 value
     */
    private int h0(StacksOfContainers goal){
        int result = 0;
        int goalN = goal.stacksOfContainers.size();
        HashSet<Container> containersBelow;
        HashSet<Container> goalContainersBelow;
        for(Stack<Container> s0 : this.stacksOfContainers){
            //if stack in this already exists in goal we skip this stack
            if(goal.stacksOfContainers.contains(s0)){
                continue;
            }
            Container c0 = s0.getFirst();
            int foundAt = -1;
            int i = 0;
            //search goal for a stack with a container on the ground equal to the
            //one we have on the ground on the current this stack
            while(i < goalN && foundAt == -1){
                if(goal.stacksOfContainers.get(i).getFirst().equals(c0)){
                    foundAt = i;
                    continue;
                }
                i++;
            }
            containersBelow = new HashSet<>();
            //if we didn't find a container on the ground on goal, then we need to
            //remove all of these containers from this stack
            //and move the bottom somewhere (we don't know where, we just know it shouldn't be on the ground)
            //so what we need to do is move all of them (at least)
            //(below we can see some cases where it's more than 1 time)
            if(foundAt == -1){
                for(int k = 0; k < s0.size(); k++){
                    Container currentC = s0.get(k);
                    //it's in the wrong place, so will need to move at least 1 time out
                    result += currentC.cost;
                    if(k >= 1){
                        //containers that are below current container in goal
                        goalContainersBelow = goal.findContainersBelow(currentC);
                        if(!goalContainersBelow.isEmpty()){
                            //if there is any container that should be below it, below it
                            //(but they are mispositioned)
                            //we'll need to move at least current container 2 times
                            //1 out and 1 on above of the other container
                            for(Container cC : goalContainersBelow){
                                if(containersBelow.contains(cC)){
                                    result += currentC.cost;
                                    break;
                                }
                            }
                        }
                    }
                    containersBelow.add(currentC);
                }
            }
            //if we found it, we will need to at least move all the
            //containers that are placed wrong on the stack out of it.
            //that is, find the deepest container inside the stack that is placed wrong
            //and start removing from there
            else{
                Stack<Container> currentGoalStack = goal.stacksOfContainers.get(foundAt);
                int currentGoalStackN = currentGoalStack.size();
                boolean foundDifference = false;
                for(int k = 1; k < s0.size(); k++){
                    Container currentC = s0.get(k);
                    if(!foundDifference){
                        //case where goal stack is smaller than this stack
                        if(k > currentGoalStackN-1){
                            foundDifference = true;
                        }
                        //if they are not equal then we found the deepest difference in the stack
                        else if(!currentGoalStack.get(k).equals(currentC)){
                            foundDifference = true;
                        }
                    }
                    if(foundDifference){
                        goalContainersBelow = goal.findContainersBelow(currentC);
                        //will always need to move ate least 1 time
                        result += currentC.cost;
                        //if the current container is contained in the same goal stack but is misplaced
                        //we will need to move it at least 2 times
                        //1 time out of the stack and another time in
                        if(currentGoalStack.contains(currentC)){
                            result += currentC.cost;
                        }
                        //if there is any container that should be below it, below it (but these are mispositioned),
                        //so will need to move at least 2 times
                        //1 out and one on top of the container
                        else{
                            for(Container cC : goalContainersBelow){
                                if(containersBelow.contains(cC)){
                                    result += currentC.cost;
                                    break;
                                }
                            }
                        }
                        containersBelow.add(currentC);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Getter of stacksOfContainers
     * @return stacksOfContainers
     * Note: This method is meant to be used for tests only
     */
    public ArrayList<Stack<Container>> getStacksOfContainers(){
        return this.stacksOfContainers;
    }

    /**
     * Getter of lastMovedContainer
     * @return lastMovedContainer
     * Note: This method is meant to be used for tests only
     */
    public Container getLastMovedContainer(){
        return this.lastMovedContainer;
    }

    /**
     * Setter of stacksOfContainers
     * Note: This method is meant to be used for tests only
     */
    public void setStacksOfContainers(ArrayList<Stack<Container>> soc){
        this.stacksOfContainers = soc;
    }

    /**
     * Setter of stacksOfContainers
     * Note: This method is meant to be used for tests only
     */
    public void setLastMovedContainer(Container c){
        this.lastMovedContainer = c;
    }

    /**
     * equals method
     * @param o another object to compare with this object
     * @return true if they are equal false if not
     */
    @Override
    public boolean equals(Object o){
        StacksOfContainers that = (StacksOfContainers) o;
        if(this.stacksOfContainers.size() != that.stacksOfContainers.size()){
            return false;
        }
        else if(this.hc != 0 && that.hc != 0){
            return this.hc == that.hc;
        }
        else{
            return this.toString().equals(that.toString());
        }
    }

    /**
     * hashCode method
     * @return hashCode of the string format of the stacks of containers
     */
    public int hashCode(){
        if(this.hc == 0){
            this.hc = this.toString().hashCode();
        }
        return this.hc;
    }

    /**
     * toString method
     * @return stacksOfContainers object in string format
     */
    public String toString(){
        if(this.str == null){
            this.stacksOfContainers.sort((s1,s2) -> s1.getFirst().compareTo(s2.getFirst()));
            StringBuilder str = new StringBuilder();
            for(Stack<Container> stacksOfContainer : stacksOfContainers) {
                str.append(stacksOfContainer.toString());
                str.append("\r\n");
            }
            this.str = str.toString();
        }
        return this.str;
    }
}
