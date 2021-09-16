package ycy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
@SuppressWarnings("all")
public class FileReader {


    public ArrayList<String> readCheckedFile(String src){//读取待检测文件
        ArrayList<String> ContentOfTxt = new ArrayList<>();
        String path = src;
        File file = new File(path);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                ContentOfTxt.add(s);
            }

            br.close();
        }catch(IOException e){
            System.out.println("ycy找不到文件");
            e.printStackTrace();

        }
        return ContentOfTxt;
    }
    public Set<String> readSensitiveWordFile(String src){//读取敏感词文件
        Set<String> keyWordSet = new HashSet<>();
        String path = src;
        File file = new File(path);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件

            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                keyWordSet.add(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return keyWordSet;
    }
}
