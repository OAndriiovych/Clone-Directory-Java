package com.company;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.RecursiveTask;

public class CopyByThread extends RecursiveTask <Integer> {

    //int numOfThreads = Runtime.getRuntime().availableProcessors();
    private LinkedList<File> list=new LinkedList<File>();
    private int numOfThreads,
            from=0,
            to=list.size();
    private File wayFrom;

    public CopyByThread(int numOfThreads, LinkedList<File> list, File wayFrom) {
        this.numOfThreads = numOfThreads;
        this.list = list;
        this.wayFrom = wayFrom;
    }

    public CopyByThread(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    protected Integer compute() {
        if((to-from)<=from/numOfThreads){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i=from;i<to;i++){
                        try {
                            System.out.println(list.get(i)+" COPY");
                            copyFileUsingStream(list.get(i), new File(wayFrom+"\\"+list.get(i).getName()));
                        }
                        catch (IOException e){
                            System.out.println(e);
                        }
                    }
                }
            }).start();
            return 0;
        }else {
            int middle =(to+from)/2;
            CopyByThread firstHalf = new CopyByThread(from,middle);
            firstHalf.fork();
            CopyByThread secondHalf = new CopyByThread(middle+1,to);
            int secondValue= secondHalf.compute();
            return firstHalf.join()+secondValue;
        }
    }

    private void copyFileUsingStream(File source, File dest) throws IOException {
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
}
