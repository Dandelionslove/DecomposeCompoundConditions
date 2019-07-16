import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;

public class MainProcess {
    public static void main(String[] args) {
        Scanner sc = new Scanner(new BufferedInputStream(System.in));
        System.out.println("请输入复合条件：");
        String CompoundCondition = sc.nextLine();
        DCC dcc=new DCC();
        /**
         *  test &&
         */
//        List<String> decomposeAndResult = dcc.DecomposeAnd(CompoundConditions);
//        for(int i=0;i<decomposeAndResult.size();i++)
//        {
//            System.out.println(decomposeAndResult.get(i));
//        }
        /**
         *  test ||
         */
//        List<String> decomposeOrResult = dcc.DecomposeOr(CompoundConditions);
//        for(int i=0;i<decomposeOrResult.size();i++)
//        {
//            System.out.println(decomposeOrResult.get(i));
//        }
        /**
         *  test && ||
         */
//        System.out.println("Decompose $$ and ||:");
//        List<String> decomposeAndOrResult = dcc.DecomposeAndOr(CompoundConditions);
//        System.out.println("The number of the total paths is: "+decomposeAndOrResult.size());
//        for(int i=0;i<decomposeAndOrResult.size();i++){
//            System.out.println(decomposeAndOrResult.get(i));
//        }
        /**
         * test DealWithBrackets
         */
//        List<Integer> bracketsIndex = dcc.DealWithBrackets(CompoundConditions.trim());
//        for(int i=0;i<bracketsIndex.size();i+=2){
//            System.out.println("(:"+bracketsIndex.get(i)+", ):"+bracketsIndex.get(i+1));
//        }
        /**
         * test DecomposedConditions
         */
        List<String> result = dcc.DecomposeConditions(CompoundCondition);
        for(int i=0;i<result.size();i++){
            System.out.println(result.get(i));
        }
    }

}
