import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CompanyRegisterDB {

    static Connection con;

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root", "root");

        JFrame frame = new JFrame("Company Registration");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("company.png");
        background.setLayout(new GridBagLayout());

        // Panel (more opaque)
        JPanel glassPanel = new JPanel();
        glassPanel.setPreferredSize(new Dimension(520, 520));
        glassPanel.setLayout(null);
        glassPanel.setBackground(new Color(255,255,255,200));
        glassPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));

        Font font = new Font("Segoe UI", Font.BOLD, 18);

        JLabel lblTitle = new JLabel("Company Registration", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(110, 20, 300, 40);
        glassPanel.add(lblTitle);

        JLabel lblName = new JLabel("Company Name");
        lblName.setBounds(40, 90, 200, 30);
        lblName.setFont(font);
        lblName.setForeground(Color.BLACK);
        glassPanel.add(lblName);

        JTextField tfName = new JTextField();
        tfName.setBounds(260, 90, 200, 30);
        glassPanel.add(tfName);

        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setBounds(40, 140, 200, 30);
        lblPhone.setFont(font);
        lblPhone.setForeground(Color.BLACK);
        glassPanel.add(lblPhone);

        JTextField tfPhone = new JTextField();
        tfPhone.setBounds(260, 140, 200, 30);
        glassPanel.add(tfPhone);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(40, 190, 200, 30);
        lblPass.setFont(font);
        lblPass.setForeground(Color.BLACK);
        glassPanel.add(lblPass);

        JPasswordField tfPass = new JPasswordField();
        tfPass.setBounds(260, 190, 200, 30);
        glassPanel.add(tfPass);

        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setBounds(40, 240, 200, 30);
        lblConfirm.setFont(font);
        lblConfirm.setForeground(Color.BLACK);
        glassPanel.add(lblConfirm);

        JPasswordField tfConfirm = new JPasswordField();
        tfConfirm.setBounds(260, 240, 200, 30);
        glassPanel.add(tfConfirm);

        JLabel lblQuestion = new JLabel("Security Question");
        lblQuestion.setBounds(40, 290, 200, 30);
        lblQuestion.setFont(font);
        lblQuestion.setForeground(Color.BLACK);
        glassPanel.add(lblQuestion);

        String questions[] = {
                "What is your favorite city?",
                "What is your favorite food?",
                "What is your favorite programming language?"
        };

        JComboBox<String> cbQuestion = new JComboBox<>(questions);
        cbQuestion.setBounds(260, 290, 200, 30);
        glassPanel.add(cbQuestion);

        JLabel lblAnswer = new JLabel("Security Answer");
        lblAnswer.setBounds(40, 340, 200, 30);
        lblAnswer.setFont(font);
        lblAnswer.setForeground(Color.BLACK);
        glassPanel.add(lblAnswer);

        JTextField tfAnswer = new JTextField();
        tfAnswer.setBounds(260, 340, 200, 30);
        glassPanel.add(tfAnswer);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(190, 410, 160, 45);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 18));
        glassPanel.add(btnRegister);

        background.add(glassPanel);
        frame.setContentPane(background);

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String name = tfName.getText().trim();
                    String phone = tfPhone.getText().trim();
                    String pass = new String(tfPass.getPassword());
                    String confirm = new String(tfConfirm.getPassword());
                    String question = (String) cbQuestion.getSelectedItem();
                    String answer = tfAnswer.getText().trim();

                    if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty() || answer.isEmpty()) {

                        JOptionPane.showMessageDialog(frame,
                                "All fields are required!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!pass.equals(confirm)) {

                        JOptionPane.showMessageDialog(frame,
                                "Passwords do not match!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String checkSQL = "SELECT * FROM company WHERE name=?";
                    PreparedStatement checkPs = con.prepareStatement(checkSQL);
                    checkPs.setString(1, name);
                    ResultSet rs = checkPs.executeQuery();

                    if (rs.next()) {

                        JOptionPane.showMessageDialog(frame,
                                "Company already registered!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String sql = "INSERT INTO company (name,password,phone,security_question,security_answer) VALUES (?,?,?,?,?)";

                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, name);
                    ps.setString(2, pass);
                    ps.setString(3, phone);
                    ps.setString(4, question);
                    ps.setString(5, answer);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame,
                            "Registration Successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    CompanyLoginDB.main(null);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.toString());
                }
            }
        });

        frame.setVisible(true);
    }
}