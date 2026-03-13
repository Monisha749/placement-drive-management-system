import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CompanyForgotPassword {

    static Connection con;

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/placement_db",
                "root",
                "root"
        );

        JFrame frame = new JFrame("Forgot Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel background = new BackgroundPanel("company.png");
        background.setLayout(new GridBagLayout());

        JPanel glassPanel = new JPanel();
        glassPanel.setPreferredSize(new Dimension(450,350));
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.setBackground(new Color(255,255,255,200));
        glassPanel.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,180),2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("FORGOT PASSWORD");
        title.setFont(new Font("Segoe UI",Font.BOLD,30));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        glassPanel.add(title,gbc);

        gbc.gridwidth=1;
        gbc.gridy++;

        JLabel lblName = new JLabel("Company Name");
        lblName.setFont(new Font("Segoe UI",Font.BOLD,18));
        lblName.setForeground(Color.BLACK);

        gbc.gridx=0;
        glassPanel.add(lblName,gbc);

        JTextField tfName = new JTextField();
        tfName.setFont(new Font("Segoe UI",Font.PLAIN,18));

        gbc.gridx=1;
        glassPanel.add(tfName,gbc);

        gbc.gridy++;

        JLabel lblMethod = new JLabel("Recovery Method");
        lblMethod.setFont(new Font("Segoe UI",Font.BOLD,18));
        lblMethod.setForeground(Color.BLACK);

        gbc.gridx=0;
        glassPanel.add(lblMethod,gbc);

        gbc.gridx=1;

        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);

        JRadioButton rbQuestion = new JRadioButton("Security Question");
        JRadioButton rbPhone = new JRadioButton("Phone Number");

        rbQuestion.setOpaque(false);
        rbPhone.setOpaque(false);

        radioPanel.add(rbQuestion);
        radioPanel.add(rbPhone);

        glassPanel.add(radioPanel,gbc);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbQuestion);
        bg.add(rbPhone);

        gbc.gridy++;
        gbc.gridx=0;
        gbc.gridwidth=2;

        JButton btnContinue = new JButton("Continue");
        btnContinue.setFont(new Font("Segoe UI",Font.BOLD,18));
        glassPanel.add(btnContinue,gbc);

        background.add(glassPanel);

        frame.setContentPane(background);
        frame.setVisible(true);

        // CONTINUE BUTTON (YOUR ORIGINAL FUNCTIONALITY)
        btnContinue.addActionListener(e -> {

            try{

                String name = tfName.getText().trim();

                if(name.isEmpty()){
                    JOptionPane.showMessageDialog(frame,"Enter company name");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM company WHERE name=?"
                );

                ps.setString(1,name);
                ResultSet rs = ps.executeQuery();

                if(!rs.next()){
                    JOptionPane.showMessageDialog(frame,"Company not found");
                    return;
                }

                if(rbQuestion.isSelected()){

                    String question = rs.getString("security_question");
                    String dbAnswer = rs.getString("security_answer");

                    String userAnswer = JOptionPane.showInputDialog(frame,question);

                    if(userAnswer != null && userAnswer.equalsIgnoreCase(dbAnswer)){
                        resetPassword(frame,name);
                    }
                    else{
                        JOptionPane.showMessageDialog(frame,"Wrong Answer");
                    }

                }

                else if(rbPhone.isSelected()){

                    String dbPhone = rs.getString("phone");

                    String userPhone = JOptionPane.showInputDialog(
                            frame,
                            "Enter Registered Phone Number"
                    );

                    if(userPhone != null && userPhone.equals(dbPhone)){
                        resetPassword(frame,name);
                    }
                    else{
                        JOptionPane.showMessageDialog(frame,"Phone number incorrect");
                    }

                }

                else{
                    JOptionPane.showMessageDialog(frame,"Select recovery method");
                }

            }
            catch(Exception ex){
                ex.printStackTrace();
            }

        });

    }

    static void resetPassword(JFrame frame,String name){

        try{

            String newPass = JOptionPane.showInputDialog(frame,"Enter New Password");

            if(newPass == null || newPass.trim().isEmpty()){
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE company SET password=? WHERE name=?"
            );

            ps.setString(1,newPass);
            ps.setString(2,name);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame,"Password Reset Successful");

            frame.dispose();

            CompanyLoginDB.main(null);

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

}