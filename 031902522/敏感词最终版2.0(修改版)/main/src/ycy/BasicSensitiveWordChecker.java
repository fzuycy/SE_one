package ycy;

import java.util.*;
@SuppressWarnings("all")
public class BasicSensitiveWordChecker {
    private HashMap sensitiveWordMap=null;//敏感词检索树
    private HashMap PinyinMap = null;//拼音与汉字相映射的字典，如 pin-拼
    private HashMap EnglishLSMap = null;//英文字母大小写相映射的字典，如 a-A
    private  String txt=null;//待检测的一行文本
    private int maxPinyinLength = 0;//敏感词对应拼音的最大长度
    private ArrayList<String> CheckedList = null;//待检测的全部文本
    private String CatchedWord="";//当前检测出的敏感词部分
    private String OriginalWord="";//检测出的敏感词部分的原型
    private  ArrayList<String> CheckedResult = null;//检测出的全部敏感词结果文本
    public BasicSensitiveWordChecker(){

    }

    public BasicSensitiveWordChecker(ArrayList<String> tmp,Set<String> keyWordSet) {
        CheckedList = tmp;//初始化检测文本内容
        UpdateSensitiveWordMap(keyWordSet);//构建相关搜索树及字典
    }

    //在该方法中调用匹配过滤敏感词的方法
    public ArrayList<String> FindSensitiveWord(int matchType){
        CheckedResult = new ArrayList<>();//初始化结果集
        CheckedResult.add("0");//由于输出内容第一行为检测出的敏感词数量此时未知，因此先留一个位置
        //一行一行检测文本内容
        for(int i=0;i<CheckedList.size();i++)
        {
            txt = CheckedList.get(i);
            for(int j=0;j<txt.length();j++){
                //得到以当前位置为起点，下标应往前跳的步数
                int step=MatchSensitiveWordMap(i,j,matchType);
                if(step!=0){//步数非0，代表检测到敏感词
                    j+=step-1;
                }
            }
        }
        CheckedResult.set(0,"Total: "+CheckedResult.size()+"");
        return CheckedResult;
    }
    //利用DFA算法构建敏感词词库已经相关的一些映射
    private void UpdateSensitiveWordMap(Set<String> keyWordSet){
        sensitiveWordMap= new HashMap(keyWordSet.size());//初始化敏感词词库
        PinyinMap = new HashMap();//初始化拼音字典
        EnglishLSMap = new HashMap();//初始化英文字母大小写字典
        String word = null;//用于暂存单个敏感词
        Map nowMap = null;
        Map<String,String> newMap = null;
        //开始构建DFA敏感词搜索树
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext())
        {
            word = iterator.next();//单独抽取敏感词
            nowMap = sensitiveWordMap;
            for(int i=0;i<word.length();i++){

                char wordChar = word.charAt(i);//抽取单个敏感词的单字

                //把单字与其拼音对应起来构建字典
                Object isExit =PinyinMap.get(wordChar);
                if(!WordTypeJudger.isEnglish(wordChar) && isExit == null){//不是英文字符且不存在匹配项
                    String pinyin = PinyinExchanger.getPinyin(wordChar);//把该汉字转为拼音
                    PinyinMap.put(pinyin,wordChar);   //加入 拼音-汉字 字典中
                    //在构建字典过程中顺便记录敏感词的单字中最长的拼音
                    if(pinyin.length()>maxPinyinLength) maxPinyinLength =pinyin.length();
                }else if(WordTypeJudger.isEnglish(wordChar) && isExit == null){
                    //对于英文敏感词，建立各个英文字母及其相对的大（小）写的映射
                    String tmpStr1 = wordChar+"";
                    String tmpStr2 = tmpStr1.toLowerCase();//小写
                    String tmpStr3 = tmpStr1.toUpperCase();//大写
                    if(tmpStr1.equals(tmpStr2)){//原型是小写，映射应该是 大写-小写，大写为键，小写为值
                        EnglishLSMap.put(tmpStr3.charAt(0),tmpStr2.charAt(0));
                    }else{//原型是大写，映射应该是 小写-大写，小写为键，大写为值
                        EnglishLSMap.put(tmpStr2.charAt(0),tmpStr3.charAt(0));
                    }
                }

                Object wordMap = nowMap.get(wordChar);
                if(wordMap != null){//当前单字匹配
                    nowMap=(Map) wordMap;//进入map中的map
                }
                else{
                    newMap =new HashMap<String,String>();
                    newMap.put("isEnd","0");//暂时标记为非敏感词结尾单字
                    nowMap.put(wordChar,newMap);
                    nowMap=newMap;//同步nowMap和newMap，使nowMap保持与newMap在同一个深度
                }
                if(i==word.length()-1){//对于真正的单字，修改标记
                    nowMap.put("isEnd","1");
                }
            }
        }
    }

    //匹配检测敏感词
    private int MatchSensitiveWordMap(int line,int beginIndex,int matchType){//line为待检测文本当前行数，beginIndex为该行检索到的下标，matchType用于区分最大匹配还是最小匹配
        int matchLength = 0;//敏感词匹配到的当前长度
        int limit =0;//限制变量，用于限制在敏感词中插入的无意义字符数量
        boolean flag =false;//用于是否匹配到至少一个字符
        String copyCatchedWord = "";//当前检测到的敏感词副本（用于必要时的字符回溯）
        char wordChar = 0;//单字
        Map nowMap =sensitiveWordMap;
        Map copyMap = null;//复制一份nowMap的副本
        int key =0;//用于拼音检测循环的跳出循环条件
        //循环终止的条件：下标超过本行测试文本的长度、
        for(int i =beginIndex;i<txt.length()&&limit<=20&&nowMap!=null;i++){
            wordChar =txt.charAt(i);//读取单字
            //这里要过滤无关字符

            if(!"".equals(OriginalWord)){//当前检测出的敏感词原型为非空字符串（即此时已经扫描到敏感词）
                char ch = OriginalWord.charAt(OriginalWord.length()-1);//提取字符串尾部的单字
                if(WordTypeJudger.isChinese(ch+"")){//该原型非空字符串最后一个为中文字符（说明检测到中文敏感词）
                    if(WordTypeJudger.isChinese(wordChar+"")){//当前下标指向的字符为汉字，非无意义字符

                    }
                    else if(WordTypeJudger.isEnglish(wordChar)) {//当前字符是英文字母（有可能为拼音的字母或英文的字母）
                        int j=i;
                        int copyML=0;//类似matchedLength，该变量在接下去检测该字母以后的字符时记录步长
                        String tmpstr1="";//暂存纯小写的标准字符串
                        String tmpstr2="";//暂存原型(读到什么字符，不管大小写，直接存入)
                        key=0;//标记初始化
                        //下面开始检测以当前字母为起点，是否会有对应某个敏感词单字的拼音
                        for(;j<txt.length()&&(j<i+maxPinyinLength);j++){
                            copyML++;
                            //接下来执行将读到的字符转为小写存入字符串tmpstr1或不做变化直接存入字符串tmpstr2
                            char tmpch =txt.charAt(j);
                            String str1 =tmpch+"";
                            tmpstr1+=str1.toLowerCase();
                            tmpstr2+=str1;
                            Object tmpstr3 = PinyinMap.get(tmpstr1);
                            if(tmpstr3!=null){//是否存在对应拼音的敏感词单字

                                nowMap = (Map) nowMap.get(tmpstr3);//要特别注意nowMap的同步（按DFA搜索树的层次，各检测功能在捕获敏感词单字时必须同步nowMap，保证各模块的一致性）
                                if(nowMap == null) break;//若为空，表示该字虽然是拼音字，但与之前检测到的字符串并不匹配
                                matchLength+=copyML;//将步长补上
                                OriginalWord+=tmpstr3.toString();//将汉字原型添到字符串上
                                CatchedWord+=tmpstr2;//将拼音原文本填到字符串上
                                i=j;//同步外循环的下标
                                key=1;//设置跳出本轮条件
                                if("1".equals(nowMap.get("isEnd"))){
                                    flag=true;
                                }
                                copyCatchedWord=CatchedWord.toString();


                                break;
                            }
                        }
                        if(key==1) continue;//检测到拼音，直接进入下一轮检测
                        break;
                    }

                    else if(WordTypeJudger.isNumber(wordChar)){//当前字符是数字
                        break;
                    }

                    else{//检测到无意义字符
                        limit++;
                        matchLength++;
                        CatchedWord+=wordChar;//这里先直接加上去，之后有必要时可以用copyCatchedWord回溯
                        continue;//直接跳转至下一轮
                    }
                }
                else if (WordTypeJudger.isEnglish(ch)){//该原型非空字符串最后一个为英文字符（说明检测到英文敏感词）
                    if(!WordTypeJudger.isEnglish(wordChar)){//当前字符不是英文字符，要检查其是否为可过滤的字符
                        if(!WordTypeJudger.isChinese(wordChar+"")){//检测出不是中文字符，则为可过滤字符
                            limit++;
                            matchLength++;
                            CatchedWord+=wordChar;
                            continue;//进入下一轮
                        }
                        else break;//否则直接跳出循环，因此一个还未匹配完的英文敏感词，后面不可能再跟一个中文字符
                    }

                }

            }
            else if(WordTypeJudger.isEnglish(wordChar)){//累加的字符串为空，同时当前字符为英文字符，可能为拼音，可能为英文敏感词
                //同上的拼音检测
                int j=i;
                int copyML=0;
                String tmpstr1="";//暂存全小写
                String tmpstr2="";//暂存原型
                key=0;
                for(;j<txt.length()&&(j<i+maxPinyinLength);j++){
                    copyML++;
                    char tmpch =txt.charAt(j);
                    String str1 =tmpch+"";
                    tmpstr1+=str1.toLowerCase();
                    tmpstr2+=str1;
                    Object tmpstr3 = PinyinMap.get(tmpstr1);
                    if(tmpstr3!=null){//在这里要设置一个判断，看看是否已经到底了

                        nowMap = (Map) nowMap.get(tmpstr3);
                        if(nowMap == null) break;
                        matchLength+=copyML;
                        OriginalWord+=tmpstr3.toString();
                        CatchedWord+=tmpstr2;
                        i=j;
                        key=1;
                        if("1".equals(nowMap.get("isEnd"))){
                            flag=true;
                        }
                        copyCatchedWord=CatchedWord.toString();


                        break;
                    }
                }
                if(key==1) continue;
            }

            if(flag==true) break;//如果上述步骤中已经找打了nowMap的最后一步，则直接跳出循环
            if(nowMap==null) break;//这里有bug，由于选择了最短匹配原则，nowMap在这里可能为空，为空的话要在这里跳出

            copyMap=nowMap;//这边记得每轮nowMap变化前，copy都必须地复制一份（同步），后面回溯要用
            nowMap = (Map)nowMap.get(wordChar);
            if(nowMap != null){
                matchLength++;
                CatchedWord+=wordChar;
                OriginalWord+=wordChar;
                if("1".equals(nowMap.get("isEnd"))){
                    flag=true;
                    if(nowMap.size()==1){//走到尽头了，必须退出
                        break;
                    }
                    if(matchType==0)//为0表示采用最小前缀匹配
                        break;
                    copyCatchedWord=CatchedWord.toString();
                }
            }
            else if(WordTypeJudger.isChinese(wordChar+"")){//无法正常匹配的中文，有可能是谐音字
                String pinyin =PinyinExchanger.getPinyin(wordChar);//获取该单字的拼音
                Object OriTmp = PinyinMap.get(pinyin);//通过拼音查找是否有匹配的敏感词单字
                if(OriTmp !=null){//检测到谐音
                    String oriword =  OriTmp.toString();
                    copyMap =(Map)copyMap.get(OriTmp);
                    nowMap = copyMap;
                    if(nowMap == null) break;//虽然说是谐音，但并不匹配
                    matchLength++;
                    CatchedWord+=wordChar;
                    OriginalWord+=oriword;

                    if("1".equals(nowMap.get("isEnd"))){
                        flag=true;
                        if(nowMap.size()==1){//走到尽头了，必须退出
                            break;
                        }
                        if(matchType==0)//为0表示采用最小前缀匹配
                            break;
                        copyCatchedWord=CatchedWord.toString();
                    }

                }
                else break;//没检测到谐音直接结束循环
            }
            else if(WordTypeJudger.isEnglish(wordChar)){//无法正常匹配的英文，可能是大小写变化（拼音在前面最开始检测过了）
                Object tmp = EnglishLSMap.get(wordChar);
                if(tmp != null){//有存在大小写变化的情况
                    String LS = tmp.toString();
                    copyMap = (Map) copyMap.get(tmp);
                    nowMap = copyMap;
                    if(nowMap == null) break;
                    matchLength++;
                    CatchedWord+=wordChar;
                    OriginalWord+=LS;
                    if("1".equals(nowMap.get("isEnd"))){
                        flag=true;
                        if(nowMap.size()==1){//走到尽头了，必须退出
                            break;
                        }
                        if(matchType==0)//为0表示采用最小前缀匹配
                            break;
                        copyCatchedWord=CatchedWord.toString();
                    }
                }
                else  break;//不是英文字母大小写变化，直接退出循环
            }
            else break;
        }
        if(matchLength < 2||!flag){//不够长，或者根本没有检测到敏感词单字
            matchLength=0;
        }
        else{
            if(WordTypeJudger.isChinese(OriginalWord.charAt(0)+""))//中文敏感词
            {
                if((!WordTypeJudger.isChinese(CatchedWord.charAt(CatchedWord.length()-1)+""))&&!WordTypeJudger.isEnglish(CatchedWord.charAt(CatchedWord.length()-1))){
                    CatchedWord = copyCatchedWord;
                }
            }
            else{//英文敏感词
                if(WordTypeJudger.isNumber(CatchedWord.charAt(CatchedWord.length()-1))||((!WordTypeJudger.isChinese(CatchedWord.charAt(CatchedWord.length()-1)+""))&&!WordTypeJudger.isEnglish(CatchedWord.charAt(CatchedWord.length()-1)))){//是数字或者 不是中文也不是英文字符
                    CatchedWord = copyCatchedWord;
                }
            }
            line++;
            String tmp="Line"+line+": <"+OriginalWord+"> "+CatchedWord;
            CheckedResult.add(tmp);
        }
        CatchedWord="";
        OriginalWord="";
        return  matchLength;
    }

}