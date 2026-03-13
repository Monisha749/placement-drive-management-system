import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageCompanies {

    JFrame frame;
    JTable table;
    DefaultTableModel model;
    Connection con;

    JTextField searchField;
    JComboBox<String> filterBox;

    public static void main(String[] args) {
        new ManageCompanies();
    }

    public ManageCompanies() {

        frame = new JFrame("Manage Companies");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        // ===== BACKGROUND =====
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image img = bgIcon.getImage();
        Image scaled = img.getScaledInstance(screen.width, screen.height, Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(scaled));
        background.setLayout(null);
        frame.setContentPane(background);

        // ===== TITLE =====
        JLabel title = new JLabel("MANAGE COMPANIES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 40, screen.width, 50);
        background.add(title);

        // ===== GLASS PANEL =====
        JPanel glassPanel = new JPanel(new BorderLayout());
        glassPanel.setBackground(new Color(255,255,255,180));

        int width = 1100;
        int height = 550;

        glassPanel.setBounds(
                (screen.width-width)/2,
                (screen.height-height)/2,
                width,
                height
        );

        background.add(glassPanel);

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        filterPanel.setOpaque(false);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI",Font.PLAIN,14));

        String[] options = {"Company Name","Phone"};
        filterBox = new JComboBox<>(options);

        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Reset");

        styleButton(searchBtn);
        styleButton(resetBtn);

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(filterBox);
        filterPanel.add(searchBtn);
        filterPanel.add(resetBtn);

        glassPanel.add(filterPanel,BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Company Name");
        model.addColumn("Phone");

        table = new JTable(model);

        table.setOpaque(false);
        table.setBackground(new Color(0,0,0,0));
        table.setForeground(Color.BLACK);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI",Font.PLAIN,15));
        table.setGridColor(new Color(0,0,0,40));

        DefaultTableCellRenderer renderer =
                (DefaultTableCellRenderer)table.getDefaultRenderer(Object.class);
        renderer.setOpaque(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,16));
        header.setBackground(new Color(41,84,209));
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        glassPanel.add(scroll,BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Company");
        JButton deleteBtn = new JButton("Delete Company");
        JButton exportBtn = new JButton("Export CSV");
        JButton backBtn = new JButton("Back");

        styleButton(addBtn);
        styleButton(deleteBtn);
        styleButton(exportBtn);
        styleButton(backBtn);

        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(exportBtn);
        bottomPanel.add(backBtn);

        glassPanel.add(bottomPanel,BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====

        addBtn.addActionListener(e -> {

            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            JPasswordField passField = new JPasswordField();
            String[] questions = {
                    "What is your favorite city?",
                    "What is your favorite food?",
                    "What is your favorite programming language?"
            };
            JComboBox<String> questionBox = new JComboBox<>(questions);
            JTextField answerField = new JTextField();

            Object[] fields = {
                    "Company Name:", nameField,
                    "Phone:", phoneField,
                    "Password:", passField,
                    "Security Question:", questionBox,
                    "Security Answer:", answerField
            };

            int option = JOptionPane.showConfirmDialog(
                    frame,
                    fields,
                    "Add Company",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String question = (String) questionBox.getSelectedItem();
            String answer = answerField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || answer.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!");
                return;
            }

            try {
                String checkSql = "SELECT company_id FROM company WHERE name=?";
                PreparedStatement checkPs = con.prepareStatement(checkSql);
                checkPs.setString(1, name);
                ResultSet checkRs = checkPs.executeQuery();
                if (checkRs.next()) {
                    JOptionPane.showMessageDialog(frame, "Company already exists!");
                    return;
                }

                String insertSql = "INSERT INTO company(name,password,phone,security_question,security_answer) VALUES(?,?,?,?,?)";
                PreparedStatement insertPs = con.prepareStatement(insertSql);
                insertPs.setString(1, name);
                insertPs.setString(2, password);
                insertPs.setString(3, phone);
                insertPs.setString(4, question);
                insertPs.setString(5, answer);
                insertPs.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Company added successfully!");
                loadCompanies(null, null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to add company: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a company to delete!");
                return;
            }

            int companyId = (int) model.getValueAt(row, 0);
            String companyName = String.valueOf(model.getValueAt(row, 1));

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Delete company: " + companyName + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                String sql = "DELETE FROM company WHERE company_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, companyId);
                int affected = ps.executeUpdate();

                if (affected > 0) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(frame, "Company deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Company not found in database.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unable to delete company. It may be linked with existing drives/applications.");
            }
        });

        searchBtn.addActionListener(e -> performSearch());

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadCompanies(null,null);
        });

        exportBtn.addActionListener(e ->
                CsvExportUtil.exportTableToCsv(frame, table, "companies_export")
        );

        backBtn.addActionListener(e -> {
            frame.dispose();
            AdminDashboard.main(null);
        });

        connectDB();
        loadCompanies(null,null);

        frame.setVisible(true);
    }

    // ===== DATABASE CONNECTION =====
    void connectDB() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/placement_db",
                    "root",
                    "root"
            );

        } catch(Exception e) {
            JOptionPane.showMessageDialog(null,"Database Connection Failed!");
        }

    }

    // ===== LOAD COMPANIES =====
    void loadCompanies(String column,String value) {

        model.setRowCount(0);

        try {

            String query = "SELECT company_id, name, phone FROM company";

            if(column!=null && value!=null && !value.isEmpty()) {
                query += " WHERE "+column+" LIKE ?";
            }

            PreparedStatement ps = con.prepareStatement(query);

            if(query.contains("?")) {
                ps.setString(1,"%"+value+"%");
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("company_id"),
                    rs.getString("name"),
                    rs.getString("phone")
                });

            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // ===== SEARCH =====
    void performSearch() {

        String column="";

        switch(filterBox.getSelectedIndex()) {

            case 0: column="name"; break;
            case 1: column="phone"; break;

        }

        loadCompanies(column,searchField.getText());

    }

    // ===== BUTTON STYLE =====
    void styleButton(JButton btn) {

        btn.setFont(new Font("Segoe UI",Font.BOLD,15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(41,84,209));
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(63,112,235));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(41,84,209));
            }

        });

    }

}