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
         * test DecomposedConditions
         */
        List<String> result = dcc.DecomposeCompoundConditions(CompoundCondition);
        System.out.println("路径数："+result.size());
        for(int i=0;i<result.size();i++){
            System.out.println((i+1)+": "+result.get(i));
        }
    }

}
