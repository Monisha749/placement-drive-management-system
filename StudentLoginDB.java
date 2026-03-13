import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentLoginDB {

    static Connection con;

    static void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(41, 84, 209));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(63, 112, 235));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(41, 84, 209));
            }
        });
    }

    public static void main(String[] args) throws Exception {

        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db",
                "root", "root");

        JFrame frame = new JFrame("Student Login");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("student.jpeg", 1.08);
        frame.setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        // Use GridBagLayout to center the glass panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setLayout(null);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        glassPanel.setPreferredSize(new Dimension(500, 300)); // Set preferred size for centering

        JLabel roll = new JLabel("Roll Number");
        roll.setBounds(50,60,150,30);
        roll.setFont(new Font("Segoe UI", Font.BOLD, 18));
        roll.setForeground(Color.BLACK);
        glassPanel.add(roll);

        JTextField tfRoll = new JTextField();
        tfRoll.setBounds(200,60,200,30);
        tfRoll.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        glassPanel.add(tfRoll);

        JLabel pass = new JLabel("Password");
        pass.setBounds(50,120,150,30);
        pass.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pass.setForeground(Color.BLACK);
        glassPanel.add(pass);

        JPasswordField tfPass = new JPasswordField();
        tfPass.setBounds(200,120,200,30);
        tfPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        glassPanel.add(tfPass);

        JButton btn = new JButton("Login");
        btn.setBounds(200,180,120,40);
        stylePrimaryButton(btn);
        glassPanel.add(btn);

        bgPanel.add(glassPanel, gbc);

        btn.addActionListener(e -> {
            try {

                String sql = "SELECT * FROM student WHERE roll_no=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, tfRoll.getText());
                ps.setString(2, new String(tfPass.getPassword()));

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {

                    // retrieve student details from resultset
                    String name = rs.getString("name");
                    String rollNo = rs.getString("roll_no");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String skills = rs.getString("skills");

                    JOptionPane.showMessageDialog(frame,"Login Successful");

                    UITransition.switchFrame(frame, () ->
                            StudentDashboard.showDashboard(name, rollNo, email, phone, skills, null));

                } else {

                    JOptionPane.showMessageDialog(frame,"Invalid Credentials");

                }

            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, ex.toString());
            }
        });

        frame.setVisible(true);
    }
}