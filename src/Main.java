import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        BestFirst s = new BestFirst();
        Iterator<BestFirst.State> it = s.solve(new StacksOfContainers(sc.nextLine(), false), new StacksOfContainers(sc.nextLine(), true));
        if(it == null){
            System.out.println("no solution found");
        }
        else{
            while(it.hasNext()){
                BestFirst.State i = it.next();
                //System.out.println(i+ "\n");
                if(!it.hasNext()){
                    System.out.println(i);
                    System.out.println((int)i.getG());
                }
            }
        }
        sc.close();
    }
}