package viewer;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class ApplicationRunner {
    public static void main(String[] args) throws SQLException, InvocationTargetException, InterruptedException {
        new SQLiteViewer();
    }
}


