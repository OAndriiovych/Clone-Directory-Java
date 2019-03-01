package com.company;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;


public class Main {

    private static int  numOfThreads = Runtime.getRuntime().availableProcessors();
    private List<File> list=new LinkedList(),list2=new LinkedList();
    private static Scanner scanner =new Scanner(System.in);
    private File wayFrom;

    public static void main(String[] args) {
        Main main = new Main();
        main.cloneDirectory();
        System.out.println("end");
    }
    private void cloneDirectory(){
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

        ForkJoinPool pool=new ForkJoinPool(numOfThreads);
        ArrayList<String> arrayList= new ArrayList();

        pool.invoke(new CopyByThread(numOfThreads, list, wayFrom,arrayList));
        while (!arrayList.isEmpty()){
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initFiles(String s,List <File>lst){
        System.out.println(s);
        wayFrom = new File(scanner.next());
        while (!wayFrom.exists()){
            System.out.println("didn't find directory \n try again");
            wayFrom = new File(scanner.next());
        }
        Collections.addAll(lst,wayFrom.listFiles());
    }

    private void cleanList(int a){
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
