package ycy;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class DriverOfCheckingSensitiveWord {
    public static void main(String[] args) {

        //接收命令行给的三个路径参数
        String path1 = args[0];
        String path2 = args[1];
        String path3 = args[2];
//        Scanner scanner= new Scanner(System.in);
//        String path1 = scanner.nextLine();
//        String path2 = scanner.nextLine();
//        String path3 = scanner.nextLine();
//        String path1 = "D:\\javatest\\org.txt";
//        String path2 = "D:\\javatest\\words.txt";
//        String path3 = "D:\\javatest\\answer.txt";

        //调用读文件类读取文件里的内容
        FileReader fileReader=new FileReader();
        //调用读文件类提取待测文本的内容，并封装到列表中
        ArrayList<String> arrayList=fileReader.readCheckedFile(path1);
        //调用读文件类提取敏感词文本的内容，并封装到集合中
        Set<String> stringSet = fileReader.readSensitiveWordFile(path2);
        //用上述两个参数初始化敏感词检测类
        BasicSensitiveWordChecker checker= new BasicSensitiveWordChecker(arrayList,stringSet);

        //敏感词检测分两种检测原则，调用该方法，实参为1时采用最大匹配模式，为0采用最小匹配模式，此处采用最小匹配模式
        //得到的检测结果装入列表内
        ArrayList <String> result = checker.FindSensitiveWord(0);//选择最小匹配模式

        //通过检测结果初始化写文件类
        FileWriter fileWriter = new FileWriter(result);

        //根据路径将检测结果写入指定文件内
        fileWriter.writeResultFile(path3);

    }


}
