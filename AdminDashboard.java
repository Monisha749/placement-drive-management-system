import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Placement Drive Admin Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);

        // ===== BACKGROUND IMAGE =====
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image img = bgIcon.getImage();
        Image scaledImg = img.getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(scaledImg));
        background.setLayout(null);
        frame.setContentPane(background);

        // ===== TITLE =====
        JLabel title = new JLabel("PLACEMENT DRIVE ADMIN DASHBOARD",SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,36));
        title.setForeground(Color.WHITE);
        title.setBounds(0,80,screenWidth,50);
        background.add(title);

        // ===== GLASS PANEL =====
        JPanel glassPanel = new JPanel(new GridBagLayout());
        glassPanel.setBackground(new Color(255,255,255,200)); // Glass effect
        glassPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));

        int panelWidth = 800;
        int panelHeight = 500;
        int panelX = (screenWidth - panelWidth)/2;
        int panelY = (screenHeight - panelHeight)/2;

        glassPanel.setBounds(panelX,panelY,panelWidth,panelHeight);
        background.add(glassPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,40,15,40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== WELCOME =====
        JLabel welcome = new JLabel("Welcome, Admin!",SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI",Font.BOLD,24));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        glassPanel.add(welcome,gbc);

        Font buttonFont = new Font("Segoe UI",Font.BOLD,18);

        JButton manageStudents = new JButton("Manage Students");
        JButton manageCompanies = new JButton("Manage Companies");
        JButton viewDrives = new JButton("View Drives");
        JButton postNotification = new JButton("Post Notification");
        JButton viewApplications = new JButton("View Applications");
        JButton viewResults = new JButton("View Results");
        JButton viewFeedback = new JButton("View Feedback");
        JButton logout = new JButton("Logout");

        styleButton(manageStudents,buttonFont);
        styleButton(manageCompanies,buttonFont);
        styleButton(viewDrives,buttonFont);
        styleButton(postNotification,buttonFont);
        styleButton(viewApplications,buttonFont);
        styleButton(viewResults,buttonFont);
        styleButton(viewFeedback,buttonFont);
        styleButton(logout,buttonFont);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        glassPanel.add(manageStudents,gbc);
        gbc.gridx = 1;
        glassPanel.add(manageCompanies,gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        glassPanel.add(viewDrives,gbc);
        gbc.gridx = 1;
        glassPanel.add(postNotification,gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        glassPanel.add(viewApplications,gbc);
        gbc.gridx = 1;
        glassPanel.add(viewResults,gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        glassPanel.add(viewFeedback,gbc);
        gbc.gridx = 1;
        glassPanel.add(logout,gbc);

        // ===== ACTIONS =====

        manageStudents.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ManageStudents.main(null));
        });

        manageCompanies.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ManageCompanies.main(null));
        });

        viewDrives.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ViewDrives.main(null));
        });

        postNotification.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> PostNotification.main(null));
        });

        viewApplications.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ViewApplications.main(null));
        });

        viewResults.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ViewResults.main(null));
        });

        viewFeedback.addActionListener(e -> {
            UITransition.switchFrame(frame, () -> ViewFeedback.main(null));
        });

        logout.addActionListener(e -> {
            frame.dispose();
            JOptionPane.showMessageDialog(null,"Logged out successfully!");
        });

        frame.setVisible(true);
    }

    // ===== BUTTON STYLE =====
    private static void styleButton(JButton btn,Font font){

        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220,50));
        btn.setBackground(new Color(41, 84, 209));
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new MouseAdapter(){

            public void mouseEntered(MouseEvent e){
                btn.setBackground(new Color(63, 112, 235));
            }

            public void mouseExited(MouseEvent e){
                btn.setBackground(new Color(41, 84, 209));
            }

        });
    }
}