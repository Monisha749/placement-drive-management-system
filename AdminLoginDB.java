import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class AdminLoginDB {

    static Connection con;

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db",
                "root","root");

        // ===== FRAME SETUP =====
        JFrame frame = new JFrame("Placement Drive Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);

        // ===== GET SCREEN SIZE =====
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // ===== BACKGROUND IMAGE =====
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image bgImg = bgIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(bgImg));
        background.setLayout(null);
        background.setBounds(0, 0, screenWidth, screenHeight);
        frame.setContentPane(background);

        // ===== TITLE =====
        JLabel title = new JLabel("Placement Drive Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, screenHeight/6, screenWidth, 50);
        background.add(title);

        // ===== LOGIN PANEL =====
        int panelWidth = 500;
        int panelHeight = 350;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = (screenHeight - panelHeight) / 2;

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(230, 240, 255));
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));
        loginPanel.setLayout(null);
        loginPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        background.add(loginPanel);

        // ===== USERNAME =====
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setBounds(80, 60, 150, 30);
        loginPanel.add(userLabel);

        JTextField tfUser = new JTextField();
        tfUser.setFont(new Font("Arial", Font.PLAIN, 16));
        tfUser.setBounds(200, 60, 200, 30);
        loginPanel.add(tfUser);

        // ===== PASSWORD =====
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passLabel.setBounds(80, 120, 150, 30);
        loginPanel.add(passLabel);

        JPasswordField tfPass = new JPasswordField();
        tfPass.setFont(new Font("Arial", Font.PLAIN, 16));
        tfPass.setBounds(200, 120, 200, 30);
        loginPanel.add(tfPass);

        // ===== LOGIN BUTTON =====
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(new Color(41, 84, 209));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(180, 200, 140, 40);
        loginPanel.add(btnLogin);

        // ===== LOGIN ACTION =====
        btnLogin.addActionListener(e -> {
            try {
                String sql = "SELECT * FROM admin WHERE username=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, tfUser.getText());
                ps.setString(2, new String(tfPass.getPassword()));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(frame, "Login Successful");

                    UITransition.switchFrame(frame, () -> AdminDashboard.main(null));
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Credentials");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ===== SHOW FRAME =====
        frame.setVisible(true);
    }
}