package viewer;

import org.sqlite.SQLiteException;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class ExecuteTask extends SwingWorker {

    final Connection conn;
    final String query;

    public ExecuteTask(Connection conn, String query) {
        this.conn = conn;
        this.query = query;
    }

    @Override
    protected TableResult doInBackground() throws Exception {
        TableResult tableResult = null;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            int count = 1;
            int i = 0;
            String[] columns = new String[columnCount];
            while (count <= columnCount) {
                columns[i++] = metaData.getColumnName(count++);
            }
            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (String column: columns) {
                    row.add(rs.getString(column));
                }
                rows.add(row);
            }
            String[][] data = new String[rows.size()][columns.length];
            int datai = 0;
            for (List<String> row: rows) {
                int dataj = 0;
                for (String column: row) {
                    data[datai][dataj++] = column;
                }
                datai++;
            }
            tableResult = new TableResult(columns, data);
        } catch (SQLiteException e) {
            throw new BadQueryException(e.getMessage());
        }
        return tableResult;
    }

}
