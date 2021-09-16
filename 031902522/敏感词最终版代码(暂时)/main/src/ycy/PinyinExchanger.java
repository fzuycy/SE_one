package ycy;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinExchanger {
    public static  String getPinyin(char hanzi){//紧急修改过，可能有bug
        String result = null;

        StringBuilder sb = new StringBuilder();
        // 逐个汉字进行转换， 每个汉字返回值为一个String数组（因为有多音字）
        String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(hanzi);
        if(null != stringArray) {
            // 把第几声这个数字给去掉
            sb.append(stringArray[0].replaceAll("\\d", ""));
        }
        if(sb.length() > 0) {
            result = sb.toString();
        }
        return result;
    }

}
