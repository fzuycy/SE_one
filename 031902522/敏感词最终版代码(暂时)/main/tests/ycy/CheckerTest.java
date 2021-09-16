package ycy;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CheckerTest {
    @Test
    public void test1(){
        FileReader fileReader =new FileReader();
        ArrayList<String> ans = fileReader.readCheckedFile("D:\\javatest\\answer.txt");

        BasicSensitiveWordChecker basicSensitiveWordChecker = new BasicSensitiveWordChecker(fileReader.readCheckedFile("D:\\javatest\\a.txt"),fileReader.readSensitiveWordFile("D:\\javatest\\b.txt"));
        ArrayList<String> testResult = basicSensitiveWordChecker.FindSensitiveWord(0);
        Assert.assertEquals(ans,testResult);
    }
    @Test
    public void test2(){
        FileReader fileReader =new FileReader();
        ArrayList<String> ans = fileReader.readCheckedFile("D:\\javatest\\d.txt");
    }
    @Test
    public void test3(){
        FileWriter fileWriter = new FileWriter(new ArrayList<String>());
        fileWriter.writeResultFile("F:\\javatest\\d.txt");

    }
}
