package org.example;

import org.example.dao.FooDao;
import org.example.dao.FooDaoImpl;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class App {
    public static void main(String[] args) throws InterruptedException {
        FooDao dao = new FooDaoImpl();

        while (true) {
            String nums = dao.findAllNums(10).stream().map(String::valueOf).collect(Collectors.joining(", "));
            System.out.print(nums + "\r");

            TimeUnit.MILLISECONDS.sleep(300);
        }
    }
}
