package ycy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileWriter {
    ArrayList<String> ResultSet = null;
    public FileWriter(){

    }
    public FileWriter(ArrayList<String> ansSet){
        ResultSet = ansSet;
    }
    public void writeResultFile(String src){
        File file = new File(src);//若该文件存在的话，会被重新覆盖
        try {
            FileOutputStream fop = new FileOutputStream(file);
            // 构建FileOutputStream对象,文件不存在会自动新建

            OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
            // 构建OutputStreamWriter对象,参数可以指定编码,默认为操作系统默认编码,windows上是gbk

            for(int i=0;i<ResultSet.size();i++){
                writer.append(ResultSet.get(i));
                // 写入到缓冲区
                if(i!=ResultSet.size()-1)writer.append("\r\n");
                // 换行
            }
            writer.close();
            // 关闭写入流,同时会把缓冲区内容写入文件,所以上面的注释掉
            fop.close();
            // 关闭输出流,释放系统资源
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
