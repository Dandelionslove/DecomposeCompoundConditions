//import com.sun.org.apache.xpath.internal.operations.String;

import javax.swing.*;
import java.util.*;

public class DCC {

    public DCC() {

    }

    /**
     * Deal with the "&&", can also deal with the single condition such as: a>2
     *
     * @param s
     * @return
     */
    private List<String> DecomposeAnd(String s) {
        List<String> result = new ArrayList<String>();
        String[] Conditions = s.split("&&");
//        int conditionNum = Conditions.length+1;
//        System.out.println("路径组合情况共有"+conditionNum+"种");
        for (int i = 0; i < Conditions.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                if (i == 0) {
                    sb.append("F!(" + Conditions[j].trim() + ")");
                } else {
                    sb.append(j == 0 ? "F" + Conditions[j].trim() : "," + (j < i ? " " : " !(" ) + Conditions[j].trim() + (j<i?"":")"));
                }
            }
//            System.out.println(sb);
            result.add(sb.toString());
        }
        StringBuilder final_sb = new StringBuilder();
        for (int i = 0; i < Conditions.length; i++) {
            final_sb.append(i == 0 ? "T" + Conditions[i].trim() : ", " + Conditions[i].trim());// the last one is true
        }
//        System.out.println(final_sb);
        result.add(final_sb.toString());
        return result;
    }

    /**
     * deal with the "||"
     *
     * @param s
     * @return
     */
    private List<String> DecomposeOr(String s) {
        List<String> result = new ArrayList<String>();
        String[] Conditions = s.split("\\|\\|");
        for (int i = 0; i < Conditions.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                if (i == 0) {
                    sb.append("T" + Conditions[j].trim());
                } else {
                    sb.append(j == i ? ", " + Conditions[j].trim() : (j == 0 ? "T!" + Conditions[j].trim() : ", !" + Conditions[j].trim()));
                }
            }
            result.add(sb.toString());
        }
        StringBuilder final_sb = new StringBuilder();
        for (int i = 0; i < Conditions.length; i++) {
            final_sb.append(i == 0 ? "F!" + Conditions[i].trim() : ", !" + Conditions[i].trim()); // the last one is false
        }
        result.add(final_sb.toString());
        return result;
    }

    /**
     * 处理一个单独的条件表达式，如a>2, b!=3等，并返回其路径结果
     * @param s
     * @return
     */
    private List<String> DecomposeSingleCondition(String s) {
        List<String> decomposeResult = new ArrayList<>();
        decomposeResult.add("T"+s.trim());
        decomposeResult.add("F!("+s.trim()+")");
        return decomposeResult;
    }

    /**
     * deal with the composed condition of "&&" and "||" without ()
     *
     * @param s
     * @return
     */
    private List<String> DecomposeAndOr(String s){
        String[] subComposedConditions = s.split("\\|\\|");
        List<String> decomposeResult = new ArrayList<>();
        List<List<String>> decompose = new ArrayList<>();
        for (int i = 0; i < subComposedConditions.length; i++) {
            List<String> subDecomposeResult = DecomposeAnd(subComposedConditions[i]); //
            decompose.add(subDecomposeResult);
        }
        //先把decompose[0]的所有项加入decomposeResult
        for (int i = 0; i < decompose.get(0).size(); i++) {
            decomposeResult.add(decompose.get(0).get(i));
        }
        for (int i = 1; i < decompose.size(); i++) {
            int curResultLength = decomposeResult.size();
            for (int j = 0; j < curResultLength; j++) {
                String curCondition = decomposeResult.get(j);
                if (curCondition.charAt(0) == 'F') {
                    for (int k = 0; k < decompose.get(i).size(); k++) {
                        String nextCondition = decompose.get(i).get(k).substring(1);
                        boolean isNextConditionTrue = decompose.get(i).get(k).charAt(0) == 'F' ? false : true;
                        if (isNextConditionTrue) {
                            String newCondition = "T" + curCondition.substring(1) + ", " + nextCondition;
                            decomposeResult.add(newCondition);
                        } else {
                            String newCondition = curCondition + ", " + nextCondition;
                            decomposeResult.add(newCondition);
                        }
                    }
                }
            }
            int curIndex = 0;
            for (int j = 0; j < curResultLength; j++) {
                if (decomposeResult.get(curIndex).charAt(0) == 'F') {
                    decomposeResult.remove(curIndex);
                } else {
                    curIndex++;
                }
            }
        }
        return decomposeResult;
    }

    /**
     * 处理栈中的已经拆分的逻辑条件的路径组合
     *
     * @param L1
     * @param logicalSymbol -- &&或者||
     * @param L2
     * @return
     */
    private List<String> DealWithTwoDecomposedConditionsList(List<String> L1, String logicalSymbol, List<String> L2) {
        List<String> result = new ArrayList<>();
        if (logicalSymbol.equals("||")) {
            for (int i = 0; i < L1.size(); i++) {
                for (int j = 0; j < L2.size(); j++) {
                    String c1 = L1.get(i);
                    String c2 = L2.get(j);
                    String newc = new String();
                    if (c1.charAt(0) == 'T') {
                        newc = c1;
                        result.add(newc);
                        break;
                    } else {
                        if (c2.charAt(0) == 'T') {
                            newc = "T" + c1.substring(1) + ", " + c2.substring(1);
                        } else {
                            newc = c1 + ", " + c2.substring(1);
                        }
                        result.add(newc);
                    }
                }
            }
        } else {  // &&
            for (int i = 0; i < L1.size(); i++) {
                for (int j = 0; j < L2.size(); j++) {
                    String c1 = L1.get(i);
                    String c2 = L2.get(j);
                    String newc = new String();
//                    if(c1.charAt(0)=='F'||c2.charAt(0)=='F'){
//                        newc="F"+c1.substring(1)+", "+c2.substring(1);
//                    }
//                    else
//                    {
//                        newc = c1+", "+c2.substring(1);
//                    }
                    if (c1.charAt(0) == 'F') {
                        newc = c1;
                        result.add(newc);
                        break;
                    } else {
                        if (c2.charAt(0) == 'F') {
                            newc = "F" + c1.substring(1) + ", " + c2.substring(1);
                        } else {
                            newc = c1 + ", " + c2.substring(1);
                        }
                        result.add(newc);
                        continue;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 得到括号对的索引链表，相邻两个值代表的是一对括号的索引下标
     * 注：
     * 根据mode的值来决定找的括号对的情况
     *      mode=0：这里的括号中内容为条件，已排除了括号中为表达式的情况
     *      mode为其他数值：这里的括号对为所有的括号对
     * @param s
     * @return
     */
    private List<Integer> DealWithBrackets(String s, int mode) {
        List<Integer> bracketsIndex = new ArrayList<>();
        Map<Character, Character> bracket = new HashMap<>();
        bracket.put(')', '('); // The value is '(', and the key is ')'
        Stack bracketStack = new Stack();
        Stack indexStack = new Stack();
        for (int i = 0; i < s.length(); i++) {
            Character temp = s.charAt(i);
            // 判断是否是左括号 '(' -- the value
            if (bracket.containsValue(temp)) {
                bracketStack.push(temp);
                indexStack.push(i);
            } else if (bracket.containsKey(temp)) {
                if (bracketStack.isEmpty() || indexStack.isEmpty()) //括号不匹配
                {
                    System.out.println("您输入的复合条件语句有误！");
                    System.exit(-1);
                }
                if (bracketStack.peek() == bracket.get(temp)) {
                    bracketStack.pop();
                    int leftBracketIndex = (Integer) indexStack.pop();
                    if(mode==0)
                    // 判断该括号对中的表达式是否是条件语句，若是的话则把括号对的索引值加入结果集中
                    {if (IsLegalCondition(s.substring(leftBracketIndex + 1, i).trim())) {
                        bracketsIndex.add(leftBracketIndex);
                        bracketsIndex.add(i);
                    }}
                    else {
                        bracketsIndex.add(leftBracketIndex);
                        bracketsIndex.add(i);
                    }
                }
            }
        }

        return bracketsIndex;
    }

    /**
     * 主要用于判断一个括号对中的表达式是否是条件语句
     *
     * @param subS
     * @return
     */
    private boolean IsLegalCondition(String subS) {
//        System.out.println(subS);
        String[] symbols = {"&&", "||", ">", "<", ">=", "<=", "==", "!=", "!", "instanceof"};
        for (String symbol : symbols) {
            if (subS.contains(symbol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 用于判断复合条件中出现的!是否是条件非,即形如 !（a>2||c>3)
     * @param s
     * @param notSymbolIndex
     * @param conditionBracketsIndex
     * @return
     */
    private boolean IsConditionNotSymbolInComposedCondition(String s, int notSymbolIndex, List<Integer> conditionBracketsIndex){
        if(s.charAt(notSymbolIndex)!='!'){
            return false;
        }
        int i = notSymbolIndex;
        while(i<s.length()-1&&s.charAt(i++)==' ');
        if(s.charAt(i)=='('&&conditionBracketsIndex.contains(i)){
            return true;
        }
        return false;
    }

    /**
     * 用于判断拆分了的每条路径中的最开头的!是否表示整个条件的非
     * @return
     */
    private boolean IsConditionNotSymbolInPath(String s){
        s = s.trim();
        List<Integer> bracketsIndex = DealWithBrackets(s,1);
        if(s.length()>3&&s.charAt(0)=='!'&&s.charAt(1)=='('&&bracketsIndex.get(bracketsIndex.indexOf(1)+1)==s.length()-1){
            return true;
        }
        return false;
    }
    /**
     * 对条件非！的计算
     * @param decomposedConditions
     * @return
     */
    private List<String> ComputeNotDecomposedConditions(List<String> decomposedConditions){
        List<String> result = new ArrayList<>();
        for(int i=0;i<decomposedConditions.size();i++){
            Character curConditionState = decomposedConditions.get(i).charAt(0);
//            // 把每条路径先拆分
//            String[] conditionsInOnePath = decomposedConditions.get(i).substring(1).split(",");
//            String newPath = curConditionState=='F'?"T":"F";
//            //依次处理每条路径中的各个条件
//            for(int j=0;j<conditionsInOnePath.length;j++){
//                String curConditionsInOnePath = conditionsInOnePath[j].trim();
//                if(curConditionsInOnePath.charAt(0)=='!'&&IsConditionNotSymbolInPath(curConditionsInOnePath)){
//                    newPath = newPath + (j==0?"":", ")+curConditionsInOnePath.substring(2,curConditionsInOnePath.length()-1);
//                }
//                else{
//                    newPath = newPath + (j==0?"":", ")+"!("+curConditionsInOnePath+")";
//                }
//            }
            String newPath = (curConditionState=='F'?"T":"F")+decomposedConditions.get(i).substring(1);
            result.add(newPath);
        }
        return result;
    }

    /**
     * 分解复合条件：
     * 主要情况：
     * 1、遇到条件左括号
     * 2、遇到条件右括号
     * 3、遇到逻辑运算符 || 或者 &&
     * 4、遇到条件语句之前有！
     *      对于！：没有考虑!!(a>2)的这种情况因为!!(a>2)就等于(a>2)
     * 主要数据结构：
     * 记录出现条件左括号时条件栈长度的栈 —— conditionalBracketsStack
     * 条件栈 -- conditionListStack
     * 条件括号对索引值列表 -- bracketsIndex
     *
     * @param s
     * @return
     */
    public List<String> DecomposeCompoundConditions(String s) {
        if (DealWithBrackets(s,0).size() == 0) {  // s中无条件括号
            return DecomposeAndOr(s);
        }
        Stack conditionalBracketsIndexStack = new Stack();
        Stack conditionListStack = new Stack();
        List<Integer> bracketsIndex = DealWithBrackets(s,0);
        int conditionBeginIndex = -1;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '(': // 遇到左括号
                    //判断是否为条件左括号
                    if (bracketsIndex.contains(i)) {  // 是条件左括号
                        // 记录条件栈长度
                        conditionalBracketsIndexStack.push(conditionListStack.size());
                    }
                    break;
                case ')': // 遇到右括号
                    // 判断是否为条件右括号
                    if (bracketsIndex.contains(i)) {  // 是条件右括号
                        if(conditionBeginIndex!=-1){
                            String subS = s.substring(conditionBeginIndex,i);
                            conditionBeginIndex=-1;
                            List<String> L = DecomposeSingleCondition(subS);
                            conditionListStack.push(L);
                        }
                        // cBIS栈顶元素出栈
                        int cSsize = (int) conditionalBracketsIndexStack.pop() + 1;
                        while (conditionListStack.size() > cSsize) {
                            List<String> L2 = (List<String>) conditionListStack.pop();
                            String symbol = (String) conditionListStack.pop();
//                            if(symbol.equals("||")||symbol.equals("&&")){
                            List<String> L1 = (List<String>) conditionListStack.pop();
                            List<String> L3 = DealWithTwoDecomposedConditionsList(L1, symbol, L2);
                            conditionListStack.push(L3);
//                            }
//                            else if(symbol.equals("!")){
//                                List<String> result = ComputeNotDecomposedConditions(L2);
//                                conditionListStack.push(result);
//                            }else{
//                                System.out.println("error:未知逻辑符！");
//                                System.exit(-1);
//                            }
                        }
                        //右括号计算完了以后需要判断是否有条件非！计算的情况
                        if(conditionListStack.size()%2==0){
                            List<String> L=(List<String>)conditionListStack.pop();
                            String symbol = (String)conditionListStack.pop();
                            if(symbol!="!"){
                                System.out.println("error:未知逻辑符号！");
                                System.exit(-1);
                            }
                            conditionListStack.push(ComputeNotDecomposedConditions(L));
                        }
                    }
                    break;
                case '&':
                case '|':
                    if (i < s.length() - 1 && s.charAt(i + 1) == s.charAt(i)) // 遇到||或者&&，入栈
                    {
                        //标记之后的可能的条件计算表达式的起点索引值
                        if (conditionBeginIndex != -1) {
                            String subS = s.substring(conditionBeginIndex, i);
                            conditionBeginIndex = -1;
                            List<String> L2 = DecomposeSingleCondition(subS);  // 注：此处先不要放入条件栈，主要是为了比较括号栈中的值同条件栈的数量
                            if(!conditionalBracketsIndexStack.isEmpty()){
                                int peekCBSize=(int)conditionalBracketsIndexStack.peek()+1;
                                if (!conditionListStack.isEmpty()&&conditionListStack.size()>peekCBSize) {
                                    String peekSymbol = (String) conditionListStack.peek();
                                    if (peekSymbol.equals("&&")) {
                                        String symbol = (String) conditionListStack.pop();
                                        List<String> L1 = (List<String>) conditionListStack.pop();
                                        List<String> L3 = DealWithTwoDecomposedConditionsList(L1, symbol, L2);
                                        conditionListStack.push(L3);
                                    }
                                    else{
                                        conditionListStack.push(L2);
                                    }
                                }
                                else{
                                    conditionListStack.push(L2);
                                }
                            }
                            else{
                                conditionListStack.push(L2);
                            }
                        }
                        conditionListStack.push(s.charAt(i)=='|'?"||":"&&");
                        i++;
                    }
                    break;
                case '!':  // 遇到!
                    if(IsConditionNotSymbolInComposedCondition(s,i,bracketsIndex)) //如果是表示条件非的!，则直接入栈
                    {
                        conditionListStack.push("!");
                    }
                    break;
                case ' ':  //对空格的处理，直接跳过
                    break;
                default:
                    if (conditionBeginIndex == -1) {
                        conditionBeginIndex = i;
                    }
                    break;
            }
            if(i==s.length()-1){
                if(conditionBeginIndex!=-1){
                    String subS = s.substring(conditionBeginIndex);
                    List<String> L = DecomposeSingleCondition(subS);
                    conditionListStack.push(L);
                }
                while (conditionListStack.size()>1){
                    List<String> L2 = (List<String>)conditionListStack.pop();
                    String symbol = (String)conditionListStack.pop();
                    if(symbol.equals("||")||symbol.equals("&&"))
                    { List<String> L1 = (List<String>)conditionListStack.pop();
                    List<String> L3 = DealWithTwoDecomposedConditionsList(L1,symbol,L2);
                    conditionListStack.push(L3);}
                    else if(symbol.equals("!")){
                        List<String> result = ComputeNotDecomposedConditions(L2);
                        conditionListStack.push(result);
                    }
                    else{
                        System.out.println("error：未知逻辑符！");
                        System.exit(-1);
                    }
                }
            }
        }
        return (List<String>) conditionListStack.pop();
    }
}
