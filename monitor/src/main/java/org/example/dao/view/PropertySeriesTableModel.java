package org.example.dao.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PropertySeriesTableModel extends AbstractTableModel {
    private List<String[]> data = List.of();

    private static final String[] columnNames = new String[]{"Property Code", "Series"};
    private static final Class<?>[] columnClasses = new Class[]{String.class, String.class};


    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}
