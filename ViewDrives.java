import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.Pattern;

public class ViewDrives extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public ViewDrives() {

        setTitle("View Drives");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // background image similar to AdminDashboard
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image img = bgIcon.getImage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Image scaledImg = img.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(scaledImg));
        background.setLayout(null);
        setContentPane(background);

        connectDB();

        // glass panel for content
        JPanel glassPanel = new JPanel(new BorderLayout(20,20));
        glassPanel.setBackground(new Color(255,255,255,200));
        int panelWidth = screenSize.width - 200;
        int panelHeight = screenSize.height - 200;
        glassPanel.setBounds(100,100,panelWidth,panelHeight);
        glassPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
        background.add(glassPanel);

        JLabel title = new JLabel("Placement Drives",SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,36));
        title.setForeground(new Color(41,84,209));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        filterPanel.setOpaque(false);
        JTextField searchField = new JTextField(18);
        JComboBox<String> filterBox = new JComboBox<>(new String[]{"Drive ID", "Company", "Role", "Package", "Venue", "Drive Date"});
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");
        styleButton(searchBtn, new Font("Segoe UI", Font.BOLD, 14));
        styleButton(resetBtn, new Font("Segoe UI", Font.BOLD, 14));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(filterBox);
        filterPanel.add(searchBtn);
        filterPanel.add(resetBtn);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        glassPanel.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Drive ID","Company","Role","Package","Venue","Drive Date"},0
        );

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,16));
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));

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
        makeScrollPaneTransparent(scroll);
        glassPanel.add(scroll,BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        JButton exportBtn = new JButton("Export CSV");
        styleButton(backBtn,new Font("Segoe UI",Font.BOLD,18));
        styleButton(exportBtn,new Font("Segoe UI",Font.BOLD,18));
        backBtn.addActionListener(e->{
            dispose();
            AdminDashboard.main(null);
        });
        exportBtn.addActionListener(e ->
            CsvExportUtil.exportTableToCsv(this, table, "drives_export")
        );

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(exportBtn);
        bottom.add(backBtn);
        glassPanel.add(bottom,BorderLayout.SOUTH);

        loadDrives();

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

    // utility: make scroll pane transparent similar to StudentDashboard
    private void makeScrollPaneTransparent(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
    }

    private void styleButton(JButton btn,Font font){
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220,50));
        btn.setBackground(new Color(41,84,209));
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(63,112,235));
            }
            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(41,84,209));
            }
        });
    }

    void loadDrives(){

        try{

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT d.drive_id,c.name,d.role,d.package,d.venue,d.drive_date " +
                            "FROM drive d JOIN company c ON d.company_id=c.company_id"
            );

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("drive_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getDouble("package"),
                        rs.getString("venue"),
                        rs.getDate("drive_date")
                });
            }

        }catch(Exception e){e.printStackTrace();}
    }

    public static void main(String[] args) {
        new ViewDrives();
    }
}