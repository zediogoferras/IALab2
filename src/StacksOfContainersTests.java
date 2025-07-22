import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class StacksOfContainersTests {

    @Test
    void testConstructor0(){
        String str = "A1B2C3 D1F4 G5 J2K4L9M0";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        assertEquals(soc.toString(), "[A, B, C]\r\n[D, F]\r\n[G]\r\n[J, K, L, M]\r\n");
    }

    @Test
    void testConstructor1(){
        String str = "X2Y2 A1B1C1D1E1F1 Z2";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        assertEquals(soc.toString(), "[A, B, C, D, E, F]\r\n[X, Y]\r\n[Z]\r\n");
    }

    @Test
    void testConstructor2(){
        String str = "X2Y2 A1B1C1D1E1F1 Z2";
        String strGoal = "XY ABCDEF Z";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        StacksOfContainers socGoal = new StacksOfContainers(strGoal, true);
        assertTrue(soc.isGoal(socGoal));
    }

    @Test
    void testClone() {
        String str = "A1B2C3 D1F4 G5 J2K4L9M0";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        StacksOfContainers socC = soc.clone();
        assertEquals(soc.getStacksOfContainers(), socC.getStacksOfContainers());
        //after debugging this test, we can verify that the array lists are equal but are not the same instance
        //which is what is pretended
        assertEquals(soc.getLastMovedContainer(), socC.getLastMovedContainer());
    }

    @Test
    void children() {
        String str = "A1B2 C3";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        /*
        Default config:
            [A, B]
            [C]

        Should generate:
            [A]
            [C, B]
            ;
            [A]
            [C]
            [B]
            ;
            [A, B, C]
        */
        ArrayList<Stack<Container>> alSoc = soc.getStacksOfContainers();
        Stack<Container> s0def = alSoc.get(0); //[A, B]
        Stack<Container> s1def = alSoc.get(1); //[C]
        Container a = s0def.getFirst();
        Container b = s0def.peek();
        Container c = s1def.peek();
        Stack<Container> sA = new Stack<>();
        sA.push(a);
        Stack<Container> sB = new Stack<>();
        sB.push(b);
        Stack<Container> sC = new Stack<>();
        sC.push(c);
        Stack<Container> sCB = (Stack<Container>) sC.clone();
        sCB.push(b);
        Stack<Container> sABC = (Stack<Container>) sA.clone();
        sABC.push(b);
        sABC.push(c);
        List<Ilayout> children = soc.children();
        ArrayList<Stack<Container>> child0 = new ArrayList<>();
        child0.add(sA);
        child0.add(sCB);
        ArrayList<Stack<Container>> child1 = new ArrayList<>();
        child1.add(sA);
        child1.add(sC);
        child1.add(sB);
        ArrayList<Stack<Container>> child2 = new ArrayList<>();
        child2.add(sABC);
        StacksOfContainers child0Soc = new StacksOfContainers(child0, b);
        StacksOfContainers child1Soc = new StacksOfContainers(child1, b);
        StacksOfContainers child2Soc = new StacksOfContainers(child2, c);

        assertEquals(children.get(0).toString(), child0Soc.toString());
        assertEquals(children.get(1).toString(), child1Soc.toString());
        assertEquals(children.get(2).toString(), child2Soc.toString());

    }

    @Test
    void isGoal() {
        String str = "G1H2I3 J1K4 L5 M2N4O9P0";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        String strGoal = "GHI L MNOP JK";
        StacksOfContainers socGoal = new StacksOfContainers(strGoal, true);
        assertTrue(soc.isGoal(socGoal));
    }

    @Test
    void getG() {
        String str = "G1H2I3 J1K4 L5 M2N4O9P0";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        Container c = new Container('a', 2);
        soc.setLastMovedContainer(c);
        assertEquals(soc.getG(), 2);
    }

    @Test
    void testToString() {
        String str = "G1H2I3 J1K4 L5 M2N4O9P0";
        StacksOfContainers soc = new StacksOfContainers(str, false);
        assertEquals(soc.toString(), "[G, H, I]\r\n[J, K]\r\n[L]\r\n[M, N, O, P]\r\n");
    }

    @Test
    void testHeuristics(){
        String goalStr = "ABC DEF";
        StacksOfContainers goal = new StacksOfContainers(goalStr, true);
        //case where all containers will cost 1*cost
        //(wrong bases)
        String str0 = "C1B2A2 F4E1D1";
        StacksOfContainers state0 = new StacksOfContainers(str0, false);
        assertEquals(state0.heuristics(goal), 11);
        //case where all misplaced containers will cost 1*cost
        //(right bases)
        String str1 = "A2F4E1 D1C1B2";
        StacksOfContainers state1 = new StacksOfContainers(str1, false);
        assertEquals(state1.heuristics(goal), 8);
        //case where some misplaced containers will cost 2*cost because they
        //are above a container that they should be above of but are misplaced
        //(wrong bases)
        String str2 = "B2D1F4 E1A2C1";
        StacksOfContainers state2 = new StacksOfContainers(str2, false);
        assertEquals(state2.heuristics(goal), 16);
        //case where some misplaced containers will cost 2*cost because
        //they are in the correct stack but are misplaced
        //(right bases)
        String str3 = "A2C1E1 D1F4B2";
        StacksOfContainers state3 = new StacksOfContainers(str3, false);
        assertEquals(state3.heuristics(goal), 13);
        //case where some misplaced containers will cost 2*cost because
        //they are above a container they should be above of but are misplaced
        //(right bases)
        String str4 = "A2E1F4 D1B2C1";
        StacksOfContainers state4 = new StacksOfContainers(str4, false);
        assertEquals(state4.heuristics(goal), 13);
        //case where all containers are on the ground
        //(right bases with extra bases)
        String str5 = "A2 B2 C1 D1 E1 F4";
        StacksOfContainers state5 = new StacksOfContainers(str5, false);
        assertEquals(state5.heuristics(goal), 8);
    }
}