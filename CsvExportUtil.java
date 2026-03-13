import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static void exportTableToCsv(Component parent, JTable table, String defaultFileName) {
        if (table == null || table.getModel() == null) {
            JOptionPane.showMessageDialog(parent, "No table data available to export.");
            return;
        }

        TableModel model = table.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Table is empty. Nothing to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV File");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        chooser.setSelectedFile(new File(defaultFileName + ".csv"));

        int choice = chooser.showSaveDialog(parent);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                writer.write(escapeCsv(model.getColumnName(col)));
                if (col < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(escapeCsv(value == null ? "" : value.toString()));
                    if (col < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(parent, "CSV exported successfully to:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Failed to export CSV: " + ex.getMessage());
        }
    }

    private static String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}