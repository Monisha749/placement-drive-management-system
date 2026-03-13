import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SLR {

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

    public static void main(String[] args) {

        JFrame frame = new JFrame("Student Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Full-screen mode
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel bgPanel = new BackgroundPanel("student.jpeg", 1.08);
        frame.setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        // Use GridBagLayout to center everything
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));

        JButton LoginBtn = new JButton("Login");
        JButton RegisterBtn = new JButton("Register");
        stylePrimaryButton(LoginBtn);
        stylePrimaryButton(RegisterBtn);

        buttonPanel.add(LoginBtn);
        buttonPanel.add(RegisterBtn);

        gbc.gridy = 1;
        glassPanel.add(buttonPanel, gbc);

        gbc.gridy = 0;
        bgPanel.add(glassPanel, gbc);

        // ===== ACTION LISTENERS =====
        LoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> {
                    try {
                        StudentLoginDB.main(null); // Open Student Login
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
                        StudentregisterDB.main(null); // Open Student Register
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });

        frame.setVisible(true);
    }
}