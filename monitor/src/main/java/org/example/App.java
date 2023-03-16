package org.example;

import org.example.dao.FooDao;
import org.example.dao.FooDaoImpl;
import org.example.dao.view.NumGroupTableModel;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class App {
    public static void main(String[] args) throws InterruptedException {

        NumGroupTableModel model = new NumGroupTableModel();
        JTable table = new JTable(model);
        JFrame frame = new JFrame("FileTableDemo");
        frame.getContentPane().add(new JScrollPane(table), "Center");
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FooDao dao = new FooDaoImpl();

        while (true) {
            model.setData(dao.findAllNums());
            model.fireTableDataChanged();

            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
