import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentDetails {

    // ===== DB CONNECTION =====
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db", // 👈 same DB
                "root",
                "root"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // helper for styling labels on the details screen
    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(new Color(40, 40, 40));
        return lbl;
    }

    private JLabel createValueLabel(JLabel value, Font font) {
        value.setFont(font);
        value.setForeground(new Color(60, 60, 60));
        return value;
    }

    public StudentDetails(int studentId) {

        JFrame frame = new JFrame("Student Details");
        frame.setSize(700, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // apply simple background and padding
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 245, 245));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.setContentPane(content);

        JLabel header = new JLabel("Student Details", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(new Color(41, 84, 209));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        content.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(9, 2, 15, 15));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel name = new JLabel();
        JLabel roll = new JLabel();
        JLabel email = new JLabel();
        JLabel phone = new JLabel();
        JLabel branch = new JLabel();
        JLabel cgpa = new JLabel();
        JLabel skills = new JLabel();
        JLabel resume = new JLabel();
        JLabel password = new JLabel();

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM student WHERE student_id=?"
            );
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                name.setText(rs.getString("name"));
                roll.setText(rs.getString("roll_no"));
                email.setText(rs.getString("email"));
                phone.setText(rs.getString("phone"));
                branch.setText(rs.getString("branch"));
                cgpa.setText(rs.getString("cgpa"));
                skills.setText(rs.getString("skills"));
                resume.setText(rs.getString("resume_path"));
                password.setText(rs.getString("password"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // helper to style labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 18);

        grid.add(createLabel("Name:", labelFont)); grid.add(createValueLabel(name, valueFont));
        grid.add(createLabel("Roll No:", labelFont)); grid.add(createValueLabel(roll, valueFont));
        grid.add(createLabel("Email:", labelFont)); grid.add(createValueLabel(email, valueFont));
        grid.add(createLabel("Phone:", labelFont)); grid.add(createValueLabel(phone, valueFont));
        grid.add(createLabel("Branch:", labelFont)); grid.add(createValueLabel(branch, valueFont));
        grid.add(createLabel("CGPA:", labelFont)); grid.add(createValueLabel(cgpa, valueFont));
        grid.add(createLabel("Skills:", labelFont)); grid.add(createValueLabel(skills, valueFont));
        grid.add(createLabel("Resume:", labelFont)); grid.add(createValueLabel(resume, valueFont));
        grid.add(createLabel("Password:", labelFont)); grid.add(createValueLabel(password, valueFont));

        content.add(grid, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}