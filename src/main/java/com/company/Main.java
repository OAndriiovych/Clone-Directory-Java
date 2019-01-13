package com.company;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;


public class Main {

    private static LinkedList<File> list=new LinkedList<File>(),list2=new LinkedList<File>();
    private static Scanner scanner =new Scanner(System.in);
    private static File wayFrom;

    public static void main(String[] args) {

        initFiles("From which directory?",list);
        initFiles("To which directory?",list2);

        // go through the list
        //if in first 10 we didn't find the same file, we will skip it
        //I do it for the minimum time
        if(50<list2.size()){
            cleanList(20);
            //full cleaning list
            cleanList(list2.size());
        }
        else cleanList(list2.size());

        System.out.println(list2.size()+" file(s) will be deleting");
        System.out.println(list.size()+" file(s) will be copy");
        System.out.println("true/false?");
        if (!scanner.nextBoolean())return;

        for (int i=0;i<list2.size();i++){
            System.out.println(list2.get(i)+" DELETE");
            list2.get(i).delete();
        }

        int numOfThreads = Runtime.getRuntime().availableProcessors();
        ForkJoinPool pool=new ForkJoinPool(numOfThreads);
        System.out.println( pool.invoke(new CopyByThread(numOfThreads,list,wayFrom)));



    }

    private  static void initFiles(String s,LinkedList <File>lst){
        System.out.println(s);
        wayFrom = new File(scanner.next());
        while (!wayFrom.exists()){
            System.out.println("didn't find directory \n try again");
            wayFrom = new File(scanner.next());
        }
        File[] files = wayFrom.listFiles();

        for (int i=0;i<files.length;i++){
            lst.add(files[i]);
        }
    }

    private static void cleanList(int a){
        boolean flag;
        StringBuilder name;
        for (int i = 0; i<list.size();) {
            flag=true;
            name = new StringBuilder(list.get(i).getName());
            for(int ii=0;ii<a;ii++){
                if(name.toString().equals(list2.get(ii).getName())){
                    if(list.get(i).length()<=list2.get(ii).length()){
                        System.out.println(list.get(i).getName()+" Found");
                        list.remove(i);
                        list2.remove(ii);
                        if (a>=list2.size()){
                            a=list2.size();
                        }
                        else a++;
                        flag=false;
                    }
                    break;
                }
            }
            if(flag){
                i++;
            }
        }
    }
}
