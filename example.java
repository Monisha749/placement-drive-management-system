import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class example {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Placement Drive Management System");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background Image
        JLabel bg = new JLabel(new ImageIcon("bg.jpg"));
        bg.setLayout(new GridBagLayout());
        frame.setContentPane(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        // Title
        JLabel title = new JLabel("Placement Drive Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        gbc.gridy = 0;
        bg.add(title, gbc);

        // Glass Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,1,20,20));
        panel.setPreferredSize(new Dimension(300,250));

        panel.setBackground(new Color(0,0,0,120)); // transparent effect

        JButton adminBtn = createButton("Admin");
        JButton studentBtn = createButton("Student");
        JButton companyBtn = createButton("Company");

        panel.add(adminBtn);
        panel.add(studentBtn);
        panel.add(companyBtn);

        gbc.gridy = 1;
        bg.add(panel, gbc);

        frame.setVisible(true);
    }

    static JButton createButton(String text){

        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0,120,215));
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new MouseAdapter(){

            public void mouseEntered(MouseEvent e){
                btn.setBackground(new Color(30,144,255));
            }

            public void mouseExited(MouseEvent e){
                btn.setBackground(new Color(0,120,215));
            }

        });

        return btn;
    }
}