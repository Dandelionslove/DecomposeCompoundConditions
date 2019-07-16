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
    public List<String> DecomposeAnd(String s) {
        List<String> result = new ArrayList<String>();
        String[] Conditions = s.split("&&");
//        int conditionNum = Conditions.length+1;
//        System.out.println("路径组合情况共有"+conditionNum+"种");
        for (int i = 0; i < Conditions.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                if (i == 0) {
                    sb.append("F!" + Conditions[j].trim());
                } else {
                    sb.append(j == 0 ? "F" + Conditions[j].trim() : "," + (j < i ? " " : " !") + Conditions[j].trim());
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
    public List<String> DecomposeOr(String s) {
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
     * deal with the composed condition of "&&" and "||" without ()
     *
     * @param s
     * @return
     */
    public List<String> DecomposeAndOr(String s) {
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
                            String newCondition = "T" + curCondition.substring(1) +", "+ nextCondition;
                            decomposeResult.add(newCondition);
                        } else {
                            String newCondition = curCondition +", " +nextCondition;
                            decomposeResult.add(newCondition);
                        }
                    }
                }
            }
            int curIndex = 0;
            for(int j=0;j<curResultLength;j++){
                if(decomposeResult.get(curIndex).charAt(0) == 'F'){
                    decomposeResult.remove(curIndex);
                }
                else{
                    curIndex++;
                }
            }
        }
        return decomposeResult;
    }

    /**
     * 处理含有括号的复合条件，考虑到括号套括号的嵌套情况
     * 注：参数s的最外层也可能包含括号，并且s最外层的空格已经处理掉了
     * 栈实现
     * @param s
     * @return
     */
    public List<String> DecomposeConditions(String s){
        if(DealWithBrackets(s).size() == 0){  // s中无嵌套括号
            return DecomposeAndOr(s);
        }
        else
        {
            Stack bracketsStack = new Stack();
            Stack conditionsListStack = new Stack();
            List<Integer> bracketsIndex = DealWithBrackets(s);
            int leftConditionIndex = 0;
            // 用于控制计算不含有括号的逻辑表达式的情况
            boolean isConditionWithoutBracketBegin = true;
//            boolean isLeftIndexValued = false;
            //用于控制重括号的情况
            boolean isBracketNeedCompute = true;
            // 对条件字符串中的字符逐个分析，主要情况有：
            // 1）遇到左括号 "（"
            // 2）遇到右括号 "）"
            // 3）遇到双目逻辑运算符（这里只考虑||和&&）
            // 4）遇到单目逻辑运算符（!）-- 该情况还未考虑
            // 5）其他

            for(int i=0;i<s.length();i++){
                //遇到左括号
                if(bracketsIndex.contains(i)&&s.charAt(i)=='('){
                    bracketsStack.push('(');  //左括号入栈
                    isConditionWithoutBracketBegin=false;
                }
                //遇到右括号，处理括号中的表达式，左括号出栈
                else if(bracketsIndex.contains(i)&&s.charAt(i) == ')'){
                    int leftBracketIndex = bracketsIndex.get(bracketsIndex.indexOf(i)-1);
                    int rightBracketIndex = i;
                    if((Character)bracketsStack.peek()!='('){
                        System.out.println("error: bracket not matchable");
                        System.exit(-1);
                    }
                    bracketsStack.pop();
//                    if(bracketsStack.isEmpty()){
//
//                    }
                    String subComposedCondition = s.substring(leftBracketIndex+1, rightBracketIndex);
                    List<String> subDecomposedConditions = DecomposeAndOr(subComposedCondition);
                    if(conditionsListStack.isEmpty()) {
                        //如果condition栈是空的，则直接入栈
                        conditionsListStack.push(subDecomposedConditions);
                    }
                    else{ //否则将栈中的逻辑运算符和condition list依次出栈，计算后再入栈
                        String logicalSymbol = (String)conditionsListStack.pop();
                        List<String> L1 = (List<String>)conditionsListStack.pop();
                        List<String> subResult = DealWithTwoDecomposedConditionsList(L1, logicalSymbol, subDecomposedConditions);
                        conditionsListStack.push(subResult);
                    }
                }
                //遇到逻辑运算符：&&或者||
                else if((s.charAt(i) == '&' && s.charAt(i+1) == '&')||(s.charAt(i)=='|'&&s.charAt(i+1)=='|')){
                    if(!isConditionWithoutBracketBegin){
                        isConditionWithoutBracketBegin=true;
                        leftConditionIndex = i+2;
                    }
                    int possibleConditionEndIndex=i; //记录可能的无括号复合条件结束位置
                    i++; // i前进一位
                    String logicalSymbol = s.charAt(i)=='&'?"&&":(s.charAt(i)=='|'?"||":"error");
                    //遇到一个逻辑运算符时需考虑：
                    //  之后是否遇到左括号：
                    //  若遇到左括号
                    //       若栈中只有一个list，
                    //          若是非表达式计算，则该逻辑运算符入栈，继续向前
                    //          若是表达式计算，则计算前面的表达式结果入栈，该逻辑运算符也入栈
                    //       若栈为空，则先把前面的条件路径计算并将结果入栈，该逻辑运算符也入栈，更新下一条件初始位置和判定布尔值；
                    //  若没有遇到左括号，
                    //       若栈为空，则该逻辑运算符入栈，继续向前
                    //       若栈中只有一个list，则该逻辑运算符入栈，继续向前
                    //       若栈中有两个元素（一个list和一个逻辑运算符），继续向前

                    while(s.charAt(++i) == ' '); //跳过空格
                    if(i>=s.length()){break;}
                    //逻辑运算符后遇到左括号
                    if(bracketsIndex.contains(i)&&s.charAt(i)=='('){
//                        isConditionWithoutBracketBegin = false;
                        if(conditionsListStack.isEmpty()){//栈为空
                            //把前面的条件路径计算并将结果入栈
                            int rightConditionIndex = possibleConditionEndIndex;
                            String subComposedCondition = s.substring(leftConditionIndex, rightConditionIndex);
                            List<String> subDecomposedConditions = DecomposeAndOr(subComposedCondition);
                            conditionsListStack.push(subDecomposedConditions);
                            conditionsListStack.push(logicalSymbol);
                        }
                        else if(conditionsListStack.size() == 1){
                            conditionsListStack.push(logicalSymbol);
                        }
                        else{
                            System.out.println("error");
                            System.exit(-1);
                        }
                    }
                    else{ //逻辑运算符后面没有遇到左括号
                        if(conditionsListStack.size() == 1){
                            conditionsListStack.push(logicalSymbol);
                        }
//                        isConditionWithoutBracketBegin=true;
                    }
                    i--; // 注意这里的计算
                }
                //  遇到一般的表达式字符,更新计算表达式初始状态
//                else {
//                    if(isConditionWithoutBracketBegin && !isLeftIndexValued) {
//                        leftConditionIndex = i;
////                        isConditionWithoutBracketBegin = false;
//                        isLeftIndexValued = true;
//                    }
//                }

                // 看是否到达了表达式的末尾
                // 到末尾的时候对栈中元素个数作出判断
                // 若是栈中元素个数为1，则就是计算结果，无需处理
                // 若是栈中元素个数为2，则需要计算该子表达式，将栈中元素依次出栈并计算，然后将计算结果入栈
                if(i==s.length()-1){//到达条件表达式末尾并且未遇到括号
                    if(conditionsListStack.size() == 2 && isConditionWithoutBracketBegin){
                        String subComposedCondition = s.substring(leftConditionIndex);
                        List<String> L2 = DecomposeAndOr(subComposedCondition);
                        String logicalSymbol = (String)conditionsListStack.pop();
                        List<String> L1 = (List<String>)conditionsListStack.pop();
                        List<String> subResult = DealWithTwoDecomposedConditionsList(L1, logicalSymbol, L2);
                        conditionsListStack.push(subResult);
                    }
                }
            }
            return (List<String>)conditionsListStack.pop();
        }

    }

    /**
     * 处理栈中的已经拆分的逻辑条件的路径组合
     * @param L1
     * @param logicalSymbol -- &&或者||
     * @param L2
     * @return
     */
    private List<String> DealWithTwoDecomposedConditionsList(List<String> L1, String logicalSymbol, List<String> L2){
        List<String> result = new ArrayList<>();
        if(logicalSymbol.equals("||")){
            for(int i=0;i<L1.size();i++){
                for(int j=0;j<L2.size();j++){
                    String c1 = L1.get(i);
                    String c2 = L2.get(j);
                    String newc = new String();
                    if(c1.charAt(0) == 'T'){
                        newc = c1;
                        result.add(newc);
                        break;
                    }else{
                        if(c2.charAt(0) == 'T'){
                            newc = "T"+c1.substring(1)+", "+c2.substring(1);
                        }
                        else{
                            newc = c1+", "+c2.substring(1);
                        }
                        result.add(newc);
                    }
                }
            }
        }
        else {  // &&
            for(int i=0;i<L1.size();i++){
                for(int j=0;j<L2.size();j++){
                    String c1=L1.get(i);
                    String c2=L2.get(j);
                    String newc=new String();
//                    if(c1.charAt(0)=='F'||c2.charAt(0)=='F'){
//                        newc="F"+c1.substring(1)+", "+c2.substring(1);
//                    }
//                    else
//                    {
//                        newc = c1+", "+c2.substring(1);
//                    }
                    if(c1.charAt(0) == 'F'){
                        newc = c1;
                        result.add(newc);
                        break;
                    }
                    else{
                        if(c2.charAt(0) == 'F'){
                            newc = "F"+c1.substring(1)+", "+c2.substring(1);
                        }
                        else{
                            newc = c1+", "+c2.substring(1);
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
     * 注：这里的括号中内容为条件，已排除了括号中为表达式的情况
     * @param s
     * @return
     */
    public List<Integer> DealWithBrackets(String s){
        List<Integer> bracketsIndex = new ArrayList<>();
        Map<Character, Character> bracket = new HashMap<>();
        bracket.put(')','('); // The value is '(', and the key is ')'
        Stack bracketStack = new Stack();
        Stack indexStack = new Stack();
        for(int i=0;i<s.length();i++){
            Character temp=s.charAt(i);
            // 判断是否是左括号 '(' -- the value
            if(bracket.containsValue(temp)){
                bracketStack.push(temp);
                indexStack.push(i);
            }
            else if(bracket.containsKey(temp)){
                if(bracketStack.isEmpty() || indexStack.isEmpty()) //括号不匹配
                {
                    System.out.println("您输入的复合条件语句有误！");
                    System.exit(-1);
                }
                if(bracketStack.peek() == bracket.get(temp)){
                    bracketStack.pop();
                    int leftBracketIndex = (Integer)indexStack.pop();
                    // 判断该括号对中的表达式是否是条件语句，若是的话则把括号对的索引值加入结果集中
                    if(IsLegalCondition(s.substring(leftBracketIndex+1, i).trim())) {
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
     * @param subS
     * @return
     */
    private boolean IsLegalCondition(String subS){
//        System.out.println(subS);
        String[] symbols = {"&&", "||", ">", "<", ">=", "<=", "==", "!=", "!", "instanceof"};
        for(String symbol:symbols){
            if(subS.contains(symbol)){
                return true;
            }
        }
        return false;
    }
}
