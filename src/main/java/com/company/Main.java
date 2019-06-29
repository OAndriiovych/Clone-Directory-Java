package com.company;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static int numOfThreads = Runtime.getRuntime().availableProcessors();
    private List<File> from = new LinkedList();
    private List<File> to = new LinkedList();
    private static Scanner scanner = new Scanner(System.in);
    private File wayFrom;

    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.cloneDirectory();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

    private void cloneDirectory() throws ExecutionException, InterruptedException {
        initFiles("From which directory?", from);
        initFiles("To which directory?", to);
        // go through the from
        //if in first 10 we didn't find the same file, we will skip it
        //I do it for the minimum time
        if (50 < to.size()) {
            cleanList(20);
            //full cleaning from
            cleanList(to.size());
        } else cleanList(to.size());

        System.out.println(to.size() + " file(s) will be deleting");
        System.out.println(from.size() + " file(s) will be copy");
        System.out.println("true/false?");
        if (!scanner.nextBoolean()) return;

        for (File file : to) {
            System.out.println(file + " DELETE");
            file.deleteOnExit();
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);
        List<Future<File>> futures = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(from.size());
        for (File saveFile : from) {
            futures.add(
                    CompletableFuture.supplyAsync(() -> {
                                try {
                                    copyFileUsingStream(saveFile, new File(wayFrom + "\\" + saveFile.getName()));
                                    latch.countDown();
                                    return null;
                                } catch (IOException e) {
                                    return saveFile;
                                }

                            }, threadPool
                    ));
        }
        threadPool.shutdown();

        latch.await();


        for (Future<File> errorFile : futures) {
            try {
                System.out.println("smth was wrong with " + errorFile.get().getName());
            } catch (NullPointerException ignored){
            }
        }


    }

    private void initFiles(String s, List<File> lst) {
        System.out.println(s);
        wayFrom = new File(scanner.next());
        while (!wayFrom.exists()) {
            System.out.println("didn't find directory \n try again");
            wayFrom = new File(scanner.next());
        }
        Collections.addAll(lst, Objects.requireNonNull(wayFrom.listFiles()));
    }


    private void cleanList(int a) {
        int maxSkip;
        for (Iterator<File> i = from.iterator(); i.hasNext(); ) {
            File fileFrom = i.next();
            maxSkip = a;
            for (Iterator<File> ii = to.iterator(); ii.hasNext(); maxSkip--) {
                if (maxSkip == 0) {
                    break;
                }
                File FileTo = ii.next();
                if (fileFrom.getName().equals(FileTo.getName())) {
                    if (fileFrom.length() <= FileTo.length()) {
                        System.out.println(fileFrom.getName() + " Found");
                        i.remove();
                        ii.remove();
                    }
                    break;
                }
            }
        }
    }

    private void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            System.out.println(source.getName() + " cloned");
        }
    }
}
