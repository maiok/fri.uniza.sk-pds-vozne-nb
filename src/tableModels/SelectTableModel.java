package tableModels;

import java.lang.reflect.Member;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import oracle.jdbc.rowset.OracleCachedRowSet;

/**
 * Trieda potrebna ako DataSource pre JTable v GUI
 */
public class SelectTableModel extends AbstractTableModel {

    ResultSet rs;
    String[] menaStlpcov;
    ArrayList<OracleCachedRowSet> zoznam;
    int numberOfColumns;
    OracleCachedRowSet ocrs;

    public SelectTableModel(ResultSet rs) {
        super();
        this.rs = rs;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
            menaStlpcov = new String[numberOfColumns];

            for (int i = 0; i < numberOfColumns; i++) {
                menaStlpcov[i] = rsmd.getColumnName(i + 1);
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        zoznam = new ArrayList<>();

        try {
            ocrs = new OracleCachedRowSet();
            ocrs.populate(this.rs);
            ocrs.beforeFirst();
            while (ocrs.next()) {
                zoznam.add(ocrs);
            }
            ocrs.beforeFirst();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public int getRowCount() {
        return zoznam.size();
    }

    @Override
    public int getColumnCount() {
        return menaStlpcov.length;
    }

    @Override
    public String getColumnName(int column) {
        return menaStlpcov[column];
    }

    @Override
    public Object getValueAt(int i, int j) {
        try {
            ocrs.absolute(i + 1);
            Object o = ocrs.getObject(j + 1);
            if (o == null) {
                return null;
            } else {
                return o.toString();
            }
        } catch (SQLException e) {
            System.out.print(e);
            return e.toString();
        }
    }

    protected Member getMemberAt(int row) {
        return (Member) zoznam.get(row);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
        try {
            this.ocrs.absolute(rowIndex + 1);
            this.ocrs.updateObject((colIndex + 1), value);
            this.ocrs.updateRow();
            fireTableCellUpdated(rowIndex, colIndex);

        } catch (SQLException ex) {
            Logger.getLogger(SelectTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
