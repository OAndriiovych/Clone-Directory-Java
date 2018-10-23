package com.company;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static LinkedList<File> list=new LinkedList<File>(),list2=new LinkedList<File>();
    private static Scanner scanner =new Scanner(System.in);
    private static File wayFrom;

    public static void main(String[] args) {

        initFiles("з якої папки?",list);
        initFiles("до якої папки?",list2);

        // проходимся по списках
        //якщо в першый 10 непопадаэться одинаковий файл пролистуэм далі
        //зроблено для зменшення часу пошуку по великому спику
        if(21<list2.size()){
            cleanList(10);
            //повна перевірка уже малого списка
            cleanList(list2.size());
        }
        else cleanList(list2.size());

        for (int i=0;i<list2.size();i++){list2.get(i).delete();}
        for (int i=0;i<list.size();i++){
            try {
                copyFileUsingStream(list.get(i), new File(wayFrom+"\\"+list.get(i).getName()));
            }
            catch (IOException e){
                System.out.println(e);
            }
        }
        System.out.println("end");
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    private  static void initFiles(String s,LinkedList <File>lst){
        System.out.println(s);
        wayFrom = new File(scanner.next());
        while (!wayFrom.exists()){
            System.out.println("нема папки");
            wayFrom = new File(scanner.next());
        }
        File[] files = wayFrom.listFiles();

        for (int i=0;i<files.length;i++){
            lst.add(files[i]);
        }

    }
    private static void cleanList(int a){
        boolean flag;
        String name;
        for (int i = 0; i<list.size();) {
            flag=true;
            name = list.get(i).getName();
            for(int ii=0;ii<a;ii++){
                if(name.equals(list2.get(ii).getName())){
                    if(list.get(i).length()==list2.get(ii).length()){
                        list.remove(list.get(i));
                        list2.remove(list2.get(ii));
                        flag=false;
                    }
                    break;
                }
            }
            if(flag)i++;
        }
    }

}
