import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CLR {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Company Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel background = new BackgroundPanel("company.png");
        background.setLayout(new GridBagLayout());

        JPanel glassPanel = new JPanel();
        glassPanel.setPreferredSize(new Dimension(450, 320));
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,180),2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // WELCOME TITLE
        JLabel title = new JLabel("WELCOME");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.black);
        title.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        glassPanel.add(title, gbc);

        // SPACE BETWEEN TITLE AND BUTTONS
        gbc.gridy = 1;
        glassPanel.add(Box.createVerticalStrut(40), gbc);

        // LOGIN BUTTON
        gbc.gridy = 2;

        JButton LoginBtn = new JButton("Login");
        LoginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LoginBtn.setFocusPainted(false);
        glassPanel.add(LoginBtn, gbc);

        // REGISTER BUTTON
        gbc.gridy = 3;

        JButton RegisterBtn = new JButton("Register");
        RegisterBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        RegisterBtn.setFocusPainted(false);
        glassPanel.add(RegisterBtn, gbc);

        // EXTRA SPACE BELOW BUTTONS (pushes title slightly up)
        gbc.gridy = 4;
        glassPanel.add(Box.createVerticalStrut(60), gbc);

        background.add(glassPanel);

        frame.setContentPane(background);

        LoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> {
                    try {
                        CompanyLoginDB.main(null);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });

        RegisterBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> {
                    try {
                        CompanyRegisterDB.main(null);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });

        frame.setVisible(true);
    }
}