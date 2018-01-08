package tableModels;

import javax.swing.table.AbstractTableModel;

/**
 * Trieda potrebna ako DataSource pre JTable v GUI
 */
public class OsobaTableModel extends AbstractTableModel {

    public OsobaTableModel() {
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
