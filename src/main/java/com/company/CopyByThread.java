package com.company;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;


public class CopyByThread extends RecursiveTask <Integer> {

    private static LinkedList<File> list=new LinkedList<>();
    private static File wayFrom;
    private static int numOfThreads,toAtAll;
    public int from=0;
    public int to;

    public CopyByThread(int numOfThreads, LinkedList<File> list, File wayFrom) {
        this.numOfThreads = numOfThreads;
        this.list = list;
        this.wayFrom = wayFrom;
        to=toAtAll=list.size();
    }

    public CopyByThread(int from, int to) {
        this.from = from;
        this.to = to;
    }
    @Override
    protected Integer compute() {
        if((to-from)<=toAtAll/numOfThreads){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(to<toAtAll) {
                        to += 1;
                    }
                    for (int i=from;i<to;i++){
                        System.out.println(Thread.currentThread().getName()+list.get(i)+" COPY");
                        try {
                            copyFileUsingStream(list.get(i), new File(wayFrom+"\\"+list.get(i).getName()));
                        }
                        catch (IOException e){
                            System.out.println(e);
                        }
                    }
                }
            }).start();
            return 0;
        }
        else {
            long middle =  (to+from)/2;
            CopyByThread firstHalf = new CopyByThread(from,(int) middle);
            firstHalf.fork();
            CopyByThread secondHalf = new CopyByThread((int)middle+1,to);
            int secondValue = secondHalf.compute();
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
