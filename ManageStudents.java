import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageStudents {

    JFrame frame;
    JTable table;
    DefaultTableModel model;
    Connection con;

    JTextField searchField;
    JComboBox<String> filterBox;

    public static void main(String[] args) {
        new ManageStudents();
    }

    public ManageStudents() {

        frame = new JFrame("Manage Students");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        // ===== BACKGROUND IMAGE =====
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image img = bgIcon.getImage();
        Image scaled = img.getScaledInstance(screen.width, screen.height, Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(scaled));
        background.setLayout(null);
        frame.setContentPane(background);

        // ===== TITLE =====
        JLabel title = new JLabel("MANAGE STUDENTS", SwingConstants.CENTER);
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

        String[] options = {"Name","Roll No","Branch","Skills","CGPA"};
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
        model.addColumn("Name");
        model.addColumn("Roll No");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Branch");
        model.addColumn("CGPA");
        model.addColumn("Skills");

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

        JButton addBtn = new JButton("Add Student");
        JButton deleteBtn = new JButton("Delete Student");
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
        deleteBtn.addActionListener(e -> {

    int row = table.getSelectedRow();

    if(row == -1){
        JOptionPane.showMessageDialog(frame,"Please select a student to delete!");
        return;
    }

    int id = (int) model.getValueAt(row,0);

    int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if(confirm == JOptionPane.YES_OPTION){

        try{

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM student WHERE student_id=?"
            );

            ps.setInt(1,id);
            ps.executeUpdate();

            model.removeRow(row);

            JOptionPane.showMessageDialog(frame,"Student deleted successfully!");

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

});

addBtn.addActionListener(e -> {

    JTextField nameField = new JTextField();
    JTextField rollField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField branchField = new JTextField();
    JTextField cgpaField = new JTextField();
    JTextField skillsField = new JTextField();

    Object[] fields = {
            "Name:", nameField,
            "Roll No:", rollField,
            "Email:", emailField,
            "Phone:", phoneField,
            "Branch:", branchField,
            "CGPA:", cgpaField,
            "Skills:", skillsField
    };

    int option = JOptionPane.showConfirmDialog(
            frame,
            fields,
            "Add Student",
            JOptionPane.OK_CANCEL_OPTION
    );

    if(option == JOptionPane.OK_OPTION){

        try{

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO student(name, roll_no, email, phone, branch, cgpa, skills, password) VALUES(?,?,?,?,?,?,?,?)"
            );

            ps.setString(1, nameField.getText());
            ps.setString(2, rollField.getText());
            ps.setString(3, emailField.getText());
            ps.setString(4, phoneField.getText());
            ps.setString(5, branchField.getText());
            ps.setDouble(6, Double.parseDouble(cgpaField.getText()));
            ps.setString(7, skillsField.getText());
                ps.setString(8, "admin");

            ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,"Student Added Successfully! Default password is: admin");

            loadStudents(null,null);   // refresh table

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

});
        searchBtn.addActionListener(e -> performSearch());

        exportBtn.addActionListener(e ->
            CsvExportUtil.exportTableToCsv(frame, table, "students_export")
        );

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudents(null,null);
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            AdminDashboard.main(null);
        });

        connectDB();
        loadStudents(null,null);

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

    // ===== LOAD STUDENTS =====
    void loadStudents(String column,String value) {

        model.setRowCount(0);

        try {

            String query = "SELECT * FROM student";
            PreparedStatement ps;

            if(column!=null && value!=null && !value.isEmpty()) {
                if("cgpa".equals(column)) {
                    String input = value.trim();
                    String operator = "=";
                    String numberText = input;

                    if(input.startsWith(">=") || input.startsWith("<=")) {
                        operator = input.substring(0,2);
                        numberText = input.substring(2).trim();
                    } else if(input.startsWith(">") || input.startsWith("<") || input.startsWith("=")) {
                        operator = input.substring(0,1);
                        numberText = input.substring(1).trim();
                    }

                    double cgpaValue = Double.parseDouble(numberText);
                    query += " WHERE cgpa " + operator + " ?";
                    ps = con.prepareStatement(query);
                    ps.setDouble(1,cgpaValue);
                } else {
                    query += " WHERE "+column+" LIKE ?";
                    ps = con.prepareStatement(query);
                    ps.setString(1,"%"+value+"%");
                }
            } else {
                ps = con.prepareStatement(query);
            }

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("roll_no"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("branch"),
                        rs.getDouble("cgpa"),
                        rs.getString("skills")
                });

            }

        } catch(Exception e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Invalid input. For CGPA use formats like >8, <7.5, >=8.2, <=9 or =8.0"
            );
        }

    }

    // ===== SEARCH =====
    void performSearch() {

        String column="";

        switch(filterBox.getSelectedIndex()) {

            case 0: column="name"; break;
            case 1: column="roll_no"; break;
            case 2: column="branch"; break;
            case 3: column="skills"; break;
            case 4: column="cgpa"; break;

        }

        loadStudents(column,searchField.getText());

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