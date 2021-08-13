package viewer;

public class TableResult {
    final Object[] columns;
    final Object[][] data;

    public TableResult(Object[] columns, Object[][] data) {
        this.columns = columns;
        this.data = data;
    }
}
