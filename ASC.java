import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class ASC {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Placement Drive Management System");
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

        JPanel glassPanel = new JPanel(new GridBagLayout());
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints glassGbc = new GridBagConstraints();
        glassGbc.insets = new Insets(20, 0, 20, 0);
        glassGbc.gridx = 0;
        glassGbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("Placement Drive Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        glassGbc.gridy = 0;
        glassPanel.add(title, glassGbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));

        JButton adminBtn = new JButton("Admin");
        JButton studentBtn = new JButton("Student");
        JButton companyBtn = new JButton("Company");
        
        adminBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> {
                    try {
                        AdminLoginDB.main(null); // open admin login
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });

        studentBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> SLR.main(null));
            }
        });

        companyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UITransition.switchFrame(frame, () -> CLR.main(null));
            }
        });

        buttonPanel.add(adminBtn);
        buttonPanel.add(studentBtn);
        buttonPanel.add(companyBtn);

        glassGbc.gridy = 1;
        glassPanel.add(buttonPanel, glassGbc);

        gbc.gridy = 0;
        bgPanel.add(glassPanel, gbc);

        frame.setVisible(true);
    }
}