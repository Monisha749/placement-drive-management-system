import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.Pattern;

public class ViewResults extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public ViewResults(){

        setTitle("Placement Results");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image scaledImg = bgIcon.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(scaledImg));
        background.setLayout(null);
        setContentPane(background);

        connectDB();

        JPanel glassPanel = new JPanel(new BorderLayout(20, 20));
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        int pw = screenSize.width - 200;
        int ph = screenSize.height - 200;
        glassPanel.setBounds(100, 100, pw, ph);
        background.add(glassPanel);

        JLabel title = new JLabel("Placement Results", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(new Color(41, 84, 209));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        filterPanel.setOpaque(false);
        JTextField searchField = new JTextField(18);
        JComboBox<String> filterBox = new JComboBox<>(new String[]{"Result ID", "Student", "Company", "Role", "Status"});
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setBackground(new Color(41, 84, 209));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { searchBtn.setBackground(new Color(63, 112, 235)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { searchBtn.setBackground(new Color(41, 84, 209)); }
        });

        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setBackground(new Color(41, 84, 209));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { resetBtn.setBackground(new Color(63, 112, 235)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { resetBtn.setBackground(new Color(41, 84, 209)); }
        });

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(filterBox);
        filterPanel.add(searchBtn);
        filterPanel.add(resetBtn);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        glassPanel.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"Result ID","Student","Company","Role","Status"},0
        );

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(41, 84, 209));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchBtn.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
                return;
            }
            int col = filterBox.getSelectedIndex();
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), col));
        });

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        JScrollPane scroll = new JScrollPane(table);
        glassPanel.add(scroll, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        JButton exportBtn = new JButton("Export CSV");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setBackground(new Color(41, 84, 209));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { backBtn.setBackground(new Color(63, 112, 235)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { backBtn.setBackground(new Color(41, 84, 209)); }
        });
        backBtn.addActionListener(e->{
            dispose();
            AdminDashboard.main(null);
        });

        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exportBtn.setBackground(new Color(41, 84, 209));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { exportBtn.setBackground(new Color(63, 112, 235)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { exportBtn.setBackground(new Color(41, 84, 209)); }
        });
        exportBtn.addActionListener(e ->
                CsvExportUtil.exportTableToCsv(this, table, "results_export")
        );

        JPanel bottom = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));
        bottom.setOpaque(false);
        bottom.add(exportBtn);
        bottom.add(backBtn);
        glassPanel.add(bottom, BorderLayout.SOUTH);

        loadResults();

        setVisible(true);
    }

    void connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/placement_db",
                    "root","root");
        }catch(Exception e){e.printStackTrace();}
    }

    void loadResults(){

        try{

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT r.result_id,s.name,c.name, " +
                        "COALESCE((SELECT d.role FROM drive d " +
                        "WHERE d.company_id = r.company_id " +
                        "ORDER BY d.drive_date DESC, d.drive_id DESC LIMIT 1), 'N/A') AS role_name, " +
                        "r.result_status " +
                            "FROM result r " +
                            "JOIN student s ON r.student_id=s.student_id " +
                            "JOIN company c ON r.company_id=c.company_id"
            );

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }

        }catch(Exception e){e.printStackTrace();}
    }

    public static void main(String[] args) {
        new ViewResults();
    }
}