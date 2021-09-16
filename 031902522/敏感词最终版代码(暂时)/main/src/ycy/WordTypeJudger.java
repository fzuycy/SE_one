package ycy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordTypeJudger {
    public  static  boolean isChinese(String str)
    {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }
    public static  boolean isEnglish(char ch){
        return (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122);
    }
    public static  boolean isNumber(char ch){
        return ch >= '1' && ch <= '9';
    }

}
