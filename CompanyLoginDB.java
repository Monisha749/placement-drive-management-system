import javax.swing.*;
import java.awt.*;

import java.sql.*;

public class CompanyLoginDB {

    static Connection con;

    public static void main(String[] args) throws Exception {

        // Database Connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "root", "root");

        System.out.println("Database Connected Successfully!");

        SwingUtilities.invokeLater(() -> {
            new CompanyLoginDB().createUI();
        });
    }

    public void createUI() {

        JFrame frame = new JFrame("Company Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel background = new BackgroundPanel("company.png");
        background.setLayout(new GridBagLayout());

        JPanel glassPanel = new JPanel();
        glassPanel.setPreferredSize(new Dimension(450, 340));
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.setBackground(new Color(255,255,255,200));
        glassPanel.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,180),2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("COMPANY LOGIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        glassPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;

        JLabel userLabel = new JLabel("Company Name");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.BLACK);
        glassPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField username = new JTextField(15);
        username.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        glassPanel.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passLabel.setForeground(Color.BLACK);
        glassPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField password = new JPasswordField(15);
        password.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        glassPanel.add(password, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        JButton login = new JButton("Login");
        login.setFont(new Font("Segoe UI", Font.BOLD, 18));
        login.setFocusPainted(false);
        glassPanel.add(login, gbc);

        gbc.gridy++;

        JButton forgot = new JButton("Forgot Password?");
        forgot.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setForeground(Color.BLUE);
        glassPanel.add(forgot, gbc);

        background.add(glassPanel);

        frame.setContentPane(background);
        frame.setVisible(true);

        // LOGIN BUTTON FUNCTIONALITY
        login.addActionListener(e -> {

            try {

                String name = username.getText().trim();
                String pass = new String(password.getPassword()).trim();

                if(name.isEmpty() || pass.isEmpty()){

                    JOptionPane.showMessageDialog(frame,
                            "Please fill all fields!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "SELECT company_id,name FROM company WHERE name=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1,name);
                ps.setString(2,pass);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){

                    int companyId = rs.getInt("company_id");

                    JOptionPane.showMessageDialog(frame,
                            "Login Successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                        UITransition.switchFrame(frame, () -> new CompanyDashboard(companyId));

                }
                else{

                    JOptionPane.showMessageDialog(frame,
                            "Invalid Credentials!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
            catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,ex.toString());
            }

        });

        // FORGOT PASSWORD BUTTON
        forgot.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> {
                try {
                    CompanyForgotPassword.main(null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

        });

    }
}