

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentregisterDB {

    static Connection con;

    static void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
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

        // ✅ Database Connection
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db",
                "root", "root");

        System.out.println("Database Connected Successfully");

        // ✅ Frame Setup
        JFrame frame = new JFrame("Student Registration");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("student.jpeg", 1.08);
        frame.setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        // Centering constraints for the glass panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setLayout(null);
        glassPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        glassPanel.setPreferredSize(new Dimension(600, 500));

        Font font = new Font("Segoe UI", Font.BOLD, 18);

        // === Labels ===
        JLabel name = new JLabel("Name");
        name.setBounds(30,30,150,30);
        name.setFont(font);
        glassPanel.add(name);

        JLabel roll = new JLabel("Roll No");
        roll.setBounds(30,80,150,30);
        roll.setFont(font);
        glassPanel.add(roll);

        JLabel email = new JLabel("Email");
        email.setBounds(30,130,150,30);
        email.setFont(font);
        glassPanel.add(email);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(30,180,150,30);
        phone.setFont(font);
        glassPanel.add(phone);

        JLabel branch = new JLabel("Branch");
        branch.setBounds(30,230,150,30);
        branch.setFont(font);
        glassPanel.add(branch);

        JLabel cgpa = new JLabel("CGPA");
        cgpa.setBounds(30,280,150,30);
        cgpa.setFont(font);
        glassPanel.add(cgpa);

        JLabel skills = new JLabel("Skills");
        skills.setBounds(30,330,150,30);
        skills.setFont(font);
        glassPanel.add(skills);

        JLabel pass = new JLabel("Password");
        pass.setBounds(30,380,150,30);
        pass.setFont(font);
        glassPanel.add(pass);

        // === Input Fields ===
        JTextField tfName = new JTextField();
        tfName.setBounds(200,30,200,30);
        glassPanel.add(tfName);

        JTextField tfRoll = new JTextField();
        tfRoll.setBounds(200,80,200,30);
        glassPanel.add(tfRoll);

        JTextField tfEmail = new JTextField();
        tfEmail.setBounds(200,130,200,30);
        glassPanel.add(tfEmail);

        JTextField tfPhone = new JTextField();
        tfPhone.setBounds(200,180,200,30);
        glassPanel.add(tfPhone);

        String branches[] = {"CSE","IT","ECE","AIML","DS"};
        JComboBox<String> cbBranch = new JComboBox<>(branches);
        cbBranch.setBounds(200,230,200,30);
        glassPanel.add(cbBranch);

        JTextField tfCgpa = new JTextField();
        tfCgpa.setBounds(200,280,200,30);
        glassPanel.add(tfCgpa);

        JTextField tfSkills = new JTextField();
        tfSkills.setBounds(200,330,200,30);
        glassPanel.add(tfSkills);

        JPasswordField tfPass = new JPasswordField();
        tfPass.setBounds(200,380,200,30);
        glassPanel.add(tfPass);

        JButton btn = new JButton("Register");
        btn.setBounds(200,450,150,40);
        stylePrimaryButton(btn);
        glassPanel.add(btn);

        // attach glass panel to background with centering
        bgPanel.add(glassPanel, gbc);

        // === Button Action ===
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = tfName.getText().trim();
                    String roll = tfRoll.getText().trim();
                    String email = tfEmail.getText().trim();
                    String phone = tfPhone.getText().trim();
                    String branch = cbBranch.getSelectedItem().toString();
                    String cgpa = tfCgpa.getText().trim();
                    String skills = tfSkills.getText().trim();
                    String password = new String(tfPass.getPassword());

                    // Validation
                    if(name.isEmpty() || roll.isEmpty() || email.isEmpty() || phone.isEmpty()
                        || cgpa.isEmpty() || skills.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, 
                            "All fields are required!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // SQL Insert
                    String sql = "INSERT INTO student(name, roll_no, email, phone, branch, cgpa, skills, password) VALUES(?,?,?,?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, name);
                    ps.setString(2, roll);
                    ps.setString(3, email);
                    ps.setString(4, phone);
                    ps.setString(5, branch);
                    ps.setDouble(6, Double.parseDouble(cgpa));
                    ps.setString(7, skills);
                    ps.setString(8, password);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame, 
                        "Student Registered Successfully!",
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);

                    // After success redirect to login
                    frame.dispose();
                    StudentLoginDB.main(null);

                    // Clear Fields (optional since frame closed)
                    tfName.setText("");
                    tfRoll.setText("");
                    tfEmail.setText("");
                    tfPhone.setText("");
                    tfCgpa.setText("");
                    tfSkills.setText("");
                    tfPass.setText("");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.toString());
                }
            }
        });

        frame.setVisible(true);
    }
}
