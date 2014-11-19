/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.fm.configuration;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.excalibur.fm.solver.constraints.Operator;
import org.excalibur.fm.solver.constraints.Vars;

public class Table {

    public static void main(String[] args) 
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String[] columnTitles = {"Var", "Operator", "Value"};
        TableModel model = new EditableTableModel(columnTitles, new Object[2][3]);
        JTable table = new JTable(model);
        table.createDefaultColumnsFromModel();
        
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(createComboBoxVariables()));
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(createComboBoxOperators()));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
        
        frame.add(new JScrollPane(table));
        frame.setVisible(true);        
    }

    private static JComboBox createComboBoxVariables() 
    {
        Vars[] values = Vars.values();
        String[] vars = new String[values.length];
        
        for(int i = 0; i < vars.length; i++)
        {
            vars[i] = values[i].name();
        }                        
        return new JComboBox(vars);
    }

    private static JComboBox createComboBoxOperators() 
    {    
        Operator[] operators = Operator.values();
        
        String[] vars = new String[operators.length];
        
        for(int i = 0; i < vars.length; i++)
        {
            vars[i] = operators[i].getOp();
        }
                
        return new JComboBox(vars);        
        
    }
    
    static class EnumComboBoxModel<T extends Enum> extends DefaultComboBoxModel
    {
        private final T model;
                
        public EnumComboBoxModel(T model)
        {            
            this.model = model;
        }        
    }

    static class EditableTableModel extends AbstractTableModel {

        String[] columnTitles;
        Object[][] dataEntries;

        public EditableTableModel(String[] columnTitles, Object[][] dataEntries) {
            this.columnTitles = columnTitles;
            this.dataEntries = dataEntries;
        }

        @Override
        public int getRowCount() {
            return dataEntries.length;
        }

        @Override
        public int getColumnCount() {
            return columnTitles.length;
        }

        @Override
        public String getColumnName(int column) 
        {        
            return this.columnTitles[column];
        }                

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) 
        {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) 
        {
            return this.dataEntries[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
        {
            this.dataEntries[rowIndex][columnIndex] = aValue;
        }               
    }
}
