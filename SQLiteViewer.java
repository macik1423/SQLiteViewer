package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteViewer extends JFrame {

    private JTextArea queryTextArea;
    private JButton executeButton;
    private JTextField fileName;
    private JButton openButton;
    private JComboBox<String> tables;
    private TableResult tableResult = new TableResult(new Object[]{}, new Object[][]{});
    private Connection conn;
    private DefaultTableModel defaultTableModel;


    public SQLiteViewer() throws InvocationTargetException, InterruptedException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);

        setResizable(true);
        setLocationRelativeTo(null);
        setTitle("SQLite Viewer");
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());

        SwingUtilities.invokeAndWait(() -> {
            firstRow(pane);
            secondRow(pane, new String[] {});
            thirdRow(pane);
            showResultTable(pane);
        });

        openButton.addActionListener(actionEvent -> {
            try {
                final java.util.List<String> tablesNames = new ArrayList<>();
                File tempFile = new File(fileName.getText());
                boolean fileExists = tempFile.exists();
                if (!fileExists) {
                    queryTextArea.setEnabled(false);
                    executeButton.setEnabled(false);
                    JDialog jDialog = new JDialog();
                    JOptionPane.showMessageDialog(jDialog, "Wrong file name!");
                } else {
                    conn = DriverManager.getConnection("jdbc:sqlite:" + fileName.getText());
                    DatabaseMetaData metaData = conn.getMetaData();

                    ResultSet rs = metaData.getTables(null, null, "%", null);
                    boolean tableExists = false;
                    while (rs.next()) {
                        tablesNames.add(rs.getString(3));
                        tableExists = true;
                    }
                    if (tableExists) {
                        queryTextArea.setEnabled(true);
                        executeButton.setEnabled(true);
                        String[] tablesNamesArray = tablesNames.toArray(new String[0]);
                        if (!tablesNames.isEmpty()) {
                            tables.removeAllItems();
                            for (String table: tablesNamesArray) {
                                tables.addItem(table);
                            }
                        }
                    } else {
                        queryTextArea.setEnabled(false);
                        executeButton.setEnabled(false);
                        JDialog jDialog = new JDialog();
                        JOptionPane.showMessageDialog(jDialog, "Wrong file name!");
                    }
                }
            } catch (SQLException throwables) {
                JDialog jDialog = new JDialog();
                JOptionPane.showMessageDialog(jDialog, "File doesn't exist!");
            }
        });
        tables.addActionListener(actionEvent -> {
            queryTextArea.setText("SELECT * FROM " + tables.getSelectedItem() + ";");
        });
        executeButton.addActionListener(actionEvent -> {
            defaultTableModel.setRowCount(0);
            ExecuteTask task = new ExecuteTask(conn, queryTextArea.getText());
            task.execute();
            try {
                tableResult = task.doInBackground();
                defaultTableModel.setColumnIdentifiers(tableResult.columns);
                for (Object[] row: tableResult.data) {
                    defaultTableModel.addRow(row);
                }
                task.get();
            } catch (BadQueryException e) {
                JDialog jDialog = new JDialog();
                JOptionPane.showMessageDialog(jDialog, "Error Query");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setVisible(true);
    }

    private void showResultTable(Container pane) {
        GridBagConstraints c = new GridBagConstraints();
        defaultTableModel = new DefaultTableModel();
        JTable jTable = new JTable(defaultTableModel);
        jTable.setName("Table");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        pane.add(jTable, c);
    }

    private void thirdRow(Container pane) {
        GridBagConstraints c = new GridBagConstraints();
        queryTextArea = new JTextArea();
        queryTextArea.setName("QueryTextArea");
        queryTextArea.setRows(5);
        queryTextArea.setColumns(5);
        queryTextArea.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        pane.add(queryTextArea, c);

        executeButton = new JButton();
        executeButton.setName("ExecuteQueryButton");
        executeButton.setText("Execute");
        executeButton.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        pane.add(executeButton, c);
    }

    private void secondRow(Container pane, String[] tablesNames) {
        GridBagConstraints c = new GridBagConstraints();
        tables = new JComboBox<>(tablesNames);
        tables.setName("TablesComboBox");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.gridwidth = 2;
        pane.add(tables, c);
    }

    private void firstRow(Container pane)  {
        GridBagConstraints c = new GridBagConstraints();
        fileName = new JTextField();
        fileName.setName("FileNameTextField");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(fileName, c);

        openButton = new JButton();
        openButton.setName("OpenFileButton");
        openButton.setText("Open");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(openButton, c);
    }
}
