import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PostNotification extends JFrame {

    Connection con;

    public PostNotification(){

        setTitle("Post Notification");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // background image similar to AdminDashboard
        ImageIcon bgIcon = new ImageIcon("admin.png");
        Image img = bgIcon.getImage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Image scaledImg = img.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(scaledImg));
        background.setLayout(null);
        setContentPane(background);

        connectDB();

        // glass panel for content
        JPanel glassPanel = new JPanel(new BorderLayout(20,20));
        glassPanel.setBackground(new Color(255,255,255,200));
        glassPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));
        int panelWidth = 700;
        int panelHeight = 500;
        int panelX = (screenSize.width - panelWidth)/2;
        int panelY = (screenSize.height - panelHeight)/2;
        glassPanel.setBounds(panelX,panelY,panelWidth,panelHeight);
        background.add(glassPanel);

        JLabel title = new JLabel("Post Notification",SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,36));
        title.setForeground(new Color(41,84,209));
        glassPanel.add(title, BorderLayout.NORTH);

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(4,2,15,15));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        JTextArea messageArea = new JTextArea();
        messageArea.setFont(new Font("Segoe UI",Font.PLAIN,13));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        makeScrollPaneTransparent(messageScroll);

        JLabel roleLabel = new JLabel("Target Role:");
        roleLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        JComboBox<String> roleBox = new JComboBox<>(
                new String[]{"Student","Company","All"}
        );
        roleBox.setFont(new Font("Segoe UI",Font.PLAIN,13));

        JLabel emailLabel = new JLabel("Target Email (optional):");
        emailLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI",Font.PLAIN,13));

        formPanel.add(messageLabel);
        formPanel.add(messageScroll);
        formPanel.add(roleLabel);
        formPanel.add(roleBox);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        JButton postBtn = new JButton("Post");
        JButton backBtn = new JButton("Back");
        styleButton(postBtn,new Font("Segoe UI",Font.BOLD,18));
        styleButton(backBtn,new Font("Segoe UI",Font.BOLD,18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(postBtn);
        buttonPanel.add(backBtn);
        formPanel.add(buttonPanel);

        glassPanel.add(formPanel, BorderLayout.CENTER);

        postBtn.addActionListener(e->{

            try{

                String message = messageArea.getText() == null ? "" : messageArea.getText().trim();
                String role = roleBox.getSelectedItem().toString();
                String email = emailField.getText() == null ? "" : emailField.getText().trim();

                if(message.isEmpty()){
                    JOptionPane.showMessageDialog(this,"Please enter a message.");
                    return;
                }

                if("Student".equalsIgnoreCase(role)){
                    if(email.isEmpty()){
                        JOptionPane.showMessageDialog(this,"Please enter student email for Student target role.");
                        return;
                    }

                    Integer studentId = findStudentIdByEmail(email);
                    if(studentId == null){
                        JOptionPane.showMessageDialog(this,"Student not found for email: " + email);
                        return;
                    }

                    insertNotification("ADMIN", null, "STUDENT", studentId, message, "ADMIN_NOTICE", null);
                }else if("All".equalsIgnoreCase(role)){
                    PreparedStatement psStudents = con.prepareStatement("SELECT student_id FROM student");
                    ResultSet rsStudents = psStudents.executeQuery();
                    int studentSent = 0;
                    while(rsStudents.next()){
                        int studentId = rsStudents.getInt("student_id");
                        insertNotification("ADMIN", null, "STUDENT", studentId, message, "ADMIN_NOTICE", null);
                        studentSent++;
                    }

                    PreparedStatement psCompanies = con.prepareStatement("SELECT company_id FROM company");
                    ResultSet rsCompanies = psCompanies.executeQuery();
                    int companySent = 0;
                    while(rsCompanies.next()){
                        int targetCompanyId = rsCompanies.getInt("company_id");
                        insertNotification("ADMIN", null, "COMPANY", targetCompanyId, message, "ADMIN_NOTICE", null);
                        companySent++;
                    }

                    if(studentSent == 0 && companySent == 0){
                        JOptionPane.showMessageDialog(this,"No students or companies found.");
                        return;
                    }
                }else if("Company".equalsIgnoreCase(role)){
                    if(!email.isEmpty()){
                        Integer companyId = findCompanyIdByEmail(email);
                        if(companyId == null){
                            JOptionPane.showMessageDialog(this,"Company not found for email: " + email);
                            return;
                        }
                        insertNotification("ADMIN", null, "COMPANY", companyId, message, "ADMIN_NOTICE", null);
                    }else{
                        PreparedStatement psCompanies = con.prepareStatement("SELECT company_id FROM company");
                        ResultSet rsCompanies = psCompanies.executeQuery();
                        int sent = 0;
                        while(rsCompanies.next()){
                            int targetCompanyId = rsCompanies.getInt("company_id");
                            insertNotification("ADMIN", null, "COMPANY", targetCompanyId, message, "ADMIN_NOTICE", null);
                            sent++;
                        }
                        if(sent == 0){
                            JOptionPane.showMessageDialog(this,"No companies found.");
                            return;
                        }
                    }
                }

                JOptionPane.showMessageDialog(this,"Notification Posted!");
                messageArea.setText("");
                emailField.setText("");

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        backBtn.addActionListener(e->{
            dispose();
            AdminDashboard.main(null);
        });

        setVisible(true);
    }

    void connectDB(){

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/placement_db",
                    "root","root"
            );
        }catch(Exception e){e.printStackTrace();}
    }

    private Integer findStudentIdByEmail(String email) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT student_id FROM student WHERE email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt("student_id");
        }
        return null;
    }

    private Integer findCompanyIdByEmail(String email) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT company_id FROM company WHERE email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt("company_id");
        }
        return null;
    }

    private void insertNotification(String senderRole, Integer senderId, String receiverRole, Integer receiverId,
                                    String message, String notifType, Integer relatedDriveId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO notification(sender_role,sender_id,receiver_role,receiver_id,message,notif_type,related_drive_id,notif_date,is_read) VALUES(?,?,?,?,?,?,?,NOW(),0)"
        );
        ps.setString(1, senderRole);
        if(senderId == null){
            ps.setNull(2, Types.INTEGER);
        }else{
            ps.setInt(2, senderId);
        }
        ps.setString(3, receiverRole);
        if(receiverId == null){
            ps.setNull(4, Types.INTEGER);
        }else{
            ps.setInt(4, receiverId);
        }
        ps.setString(5, message);
        ps.setString(6, notifType);
        if(relatedDriveId == null){
            ps.setNull(7, Types.INTEGER);
        }else{
            ps.setInt(7, relatedDriveId);
        }
        ps.executeUpdate();
    }

    // utility: make scroll pane transparent 
    private void makeScrollPaneTransparent(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
    }

    private void styleButton(JButton btn,Font font){
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160,50));
        btn.setBackground(new Color(41,84,209));
        btn.setForeground(Color.WHITE);

        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(63,112,235));
            }
            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(41,84,209));
            }
        });
    }

    public static void main(String[] args) {
        new PostNotification();
    }
}