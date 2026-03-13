import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CompanyDashboard extends JFrame {

    static final String DB_URL =
            "jdbc:mysql://localhost:3306/placement_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASS = "root";

    int companyId;
    JPanel contentPanel;

    public CompanyDashboard(int companyId){

        this.companyId=companyId;

        setTitle("Company Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel mainPanel=new BackgroundPanel("company.png");
        setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());

        JLabel title=new JLabel("Company Dashboard",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,42));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30,0,30,0));

        mainPanel.add(title,BorderLayout.NORTH);

        JPanel menuPanel=new JPanel(new GridLayout(7,1,25,25));
        menuPanel.setPreferredSize(new Dimension(300,0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(60,40,60,40));
        menuPanel.setBackground(new Color(30,30,30));

        JButton btnHome=createMenuButton("Dashboard");
        JButton btnCreateDrive=createMenuButton("Create Drive");
        JButton btnViewDrives=createMenuButton("View My Drives");
        JButton btnViewApplicants=createMenuButton("View Applicants");
        JButton btnNotifications=createMenuButton("Notifications");
        JButton btnFeedback=createMenuButton("Feedback");
        JButton btnLogout=createMenuButton("Logout");

        menuPanel.add(btnHome);
        menuPanel.add(btnCreateDrive);
        menuPanel.add(btnViewDrives);
        menuPanel.add(btnViewApplicants);
        menuPanel.add(btnNotifications);
        menuPanel.add(btnFeedback);
        menuPanel.add(btnLogout);

        mainPanel.add(menuPanel,BorderLayout.WEST);

        contentPanel=new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        mainPanel.add(contentPanel,BorderLayout.CENTER);

        btnHome.addActionListener(e->showPanel(dashboardPanel()));
        btnCreateDrive.addActionListener(e->showPanel(createDrivePanel()));
        btnViewDrives.addActionListener(e->showPanel(viewDrivesPanel()));
        btnViewApplicants.addActionListener(e->showPanel(viewApplicantsPanel()));
        btnNotifications.addActionListener(e->showCompanyNotificationsDialog());
        btnFeedback.addActionListener(e->openFeedbackDialog());
        btnLogout.addActionListener(e->logout());

        showPanel(dashboardPanel());

        setVisible(true);
    }

    JButton createMenuButton(String text){

        JButton btn=new JButton(text);

        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI",Font.BOLD,20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0,120,215));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    void showPanel(JPanel panel){

        contentPanel.removeAll();
        contentPanel.add(panel,BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    JPanel dashboardPanel(){

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Centered glass card with stats
        JPanel glassCard = new JPanel(new BorderLayout());
        glassCard.setBackground(new Color(255, 255, 255, 200));
        glassCard.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 3));
        glassCard.setPreferredSize(new Dimension(700, 500));

        // Title
        JLabel cardTitle = new JLabel("Dashboard Statistics");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        cardTitle.setForeground(new Color(0, 80, 160));
        cardTitle.setHorizontalAlignment(JLabel.CENTER);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        glassCard.add(cardTitle, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel drives = createStatLabel("");
        JLabel applicants = createStatLabel("");
        JLabel upcoming = createStatLabel("");

        try(Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)){

            PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(*) FROM drive WHERE company_id=?");
            ps1.setInt(1, companyId);
            ResultSet r1 = ps1.executeQuery();
            r1.next();
            drives.setText("📊 Total Drives Created : " + r1.getInt(1));

            PreparedStatement ps2 = con.prepareStatement("SELECT COUNT(*) FROM result WHERE company_id=?");
            ps2.setInt(1, companyId);
            ResultSet r2 = ps2.executeQuery();
            r2.next();
            applicants.setText("👥 Total Applicants : " + r2.getInt(1));

            PreparedStatement ps3 = con.prepareStatement("SELECT COUNT(*) FROM drive WHERE company_id=? AND drive_date>=CURDATE()");
            ps3.setInt(1, companyId);
            ResultSet r3 = ps3.executeQuery();
            r3.next();
            upcoming.setText("📅 Upcoming Drives : " + r3.getInt(1));

        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        statsPanel.add(drives);
        statsPanel.add(applicants);
        statsPanel.add(upcoming);

        glassCard.add(statsPanel, BorderLayout.CENTER);

        // Center the glass card in the main panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(glassCard);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

    JLabel createStatLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(new Color(0, 0, 0));
        label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        label.setOpaque(true);
        label.setBackground(new Color(240, 248, 255, 150));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 120, 215, 100), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        return label;
    }

    JPanel createDrivePanel(){

        JPanel panel=new JPanel(new GridLayout(6,2,20,20));
        panel.setBorder(BorderFactory.createEmptyBorder(80,350,80,350));
        panel.setBackground(new Color(255,255,255,120));

        JTextField tfRole=createField();
        JTextField tfDate=createField();
        JTextField tfVenue=createField();
        JTextField tfPackage=createField();
        JTextField tfEligibility=createField();

        JButton btnSubmit=new JButton("Create Drive");
        btnSubmit.setFont(new Font("Segoe UI",Font.BOLD,20));
        btnSubmit.setBackground(new Color(0,120,215));
        btnSubmit.setForeground(Color.WHITE);

        panel.add(createLabel("Job Role")); panel.add(tfRole);
        panel.add(createLabel("Drive Date")); panel.add(tfDate);
        panel.add(createLabel("Venue")); panel.add(tfVenue);
        panel.add(createLabel("Package")); panel.add(tfPackage);
        panel.add(createLabel("Eligibility")); panel.add(tfEligibility);
        panel.add(new JLabel()); panel.add(btnSubmit);

        btnSubmit.addActionListener(e->{

            try{

                LocalDate driveDate=LocalDate.parse(tfDate.getText());

                if(driveDate.isBefore(LocalDate.now())){
                    JOptionPane.showMessageDialog(this,"Date cannot be past");
                    return;
                }

                Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);

                String sql="INSERT INTO drive(company_id,drive_date,venue,role,`package`,eligibility,status) VALUES(?,?,?,?,?,?,?)";

                PreparedStatement ps=con.prepareStatement(sql);

                ps.setInt(1,companyId);
                ps.setDate(2,Date.valueOf(driveDate));
                ps.setString(3,tfVenue.getText());
                ps.setString(4,tfRole.getText());
                ps.setDouble(5,Double.parseDouble(tfPackage.getText()));
                ps.setString(6,tfEligibility.getText());
                ps.setString(7,"OPEN");              // default status

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Drive Created");

                showPanel(viewDrivesPanel());

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }

        });

        return panel;
    }

    JPanel viewDrivesPanel(){

        JPanel panel=new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(80,200,80,200));
        panel.setBackground(new Color(255,255,255,120));

        // search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        searchPanel.setOpaque(false);
        JTextField tfSearch = new JTextField(25);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] cols={"ID","Role","Date","Venue","Package","Status"};

        DefaultTableModel model=new DefaultTableModel(cols,0);

        JTable table=createGlassTable(model);

        // method to populate table, optionally with keyword
        BiConsumer<DefaultTableModel,String> loadDrives=(mdl,kw)->{
            mdl.setRowCount(0);
            try(Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
                String sql="SELECT drive_id,role,drive_date,venue,`package`,status FROM drive WHERE company_id=?";
                if(kw!=null && !kw.isBlank()){
                    sql += " AND (role LIKE ? OR venue LIKE ? OR CAST(`package` AS CHAR) LIKE ? )";
                }
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setInt(1,companyId);
                if(kw!=null && !kw.isBlank()){
                    String like="%"+kw+"%";
                    ps.setString(2,like);
                    ps.setString(3,like);
                    ps.setString(4,like);
                }

                ResultSet rs=ps.executeQuery();
                LocalDate today = LocalDate.now();
                while(rs.next()){
                    LocalDate d=rs.getDate("drive_date").toLocalDate();
                    String status = d.isBefore(today) ? "CLOSED" : "OPEN";
                    mdl.addRow(new Object[]{
                            rs.getInt("drive_id"),
                            rs.getString("role"),
                            d,
                            rs.getString("venue"),
                            rs.getDouble("package"),
                            status
                    });
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,e.getMessage());
            }
        };
        // initial load
        loadDrives.accept(model, null);

        // attach search and refresh actions
        btnSearch.addActionListener(e -> {
            String kw = tfSearch.getText();
            System.out.println("[DEBUG] drive search keyword='"+kw+"'");
            loadDrives.accept(model, kw);
            if(model.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"No drives match '"+kw+"'");
            }
        });
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            loadDrives.accept(model,null);
        });

        JButton deleteBtn=new JButton("Delete Drive");
        JButton statusBtn=new JButton("Toggle Open/Close");
        JButton exportBtn=new JButton("Export CSV");

        deleteBtn.addActionListener(e->{

            int row=table.getSelectedRow();

            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select drive first");
                return;
            }

            int id=(int)model.getValueAt(row,0);

            try{

                Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);

                PreparedStatement ps=con.prepareStatement("DELETE FROM drive WHERE drive_id=?");
                ps.setInt(1,id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Drive Deleted");

                showPanel(viewDrivesPanel());

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }

        });

        statusBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select drive first");
                return;
            }
            int id=(int)model.getValueAt(row,0);
            String current = (String)model.getValueAt(row,5);
            String newStatus = current.equals("OPEN")?"CLOSED":"OPEN";
            try{
                Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
                PreparedStatement ps=con.prepareStatement("UPDATE drive SET status=? WHERE drive_id=?");
                ps.setString(1,newStatus);
                ps.setInt(2,id);
                ps.executeUpdate();
                model.setValueAt(newStatus,row,5);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });

        JPanel bottom=new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        bottom.add(deleteBtn);
        bottom.add(statusBtn);
        bottom.add(exportBtn);
        panel.add(createGlassScroll(table),BorderLayout.CENTER);
        panel.add(bottom,BorderLayout.SOUTH);

        exportBtn.addActionListener(e ->
            CsvExportUtil.exportTableToCsv(this, table, "company_drives_export")
        );

        return panel;
    }

    JPanel viewApplicantsPanel(){

        JPanel panel=new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(80,200,80,200));
        panel.setBackground(new Color(255,255,255,120));

        // search bar (by name or roll)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        searchPanel.setOpaque(false);
        JTextField tfSearch = new JTextField(25);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] cols={"Student ID","Name","Roll No","Branch","Status"};

        DefaultTableModel model=new DefaultTableModel(cols,0);

        JTable table=createGlassTable(model);

        // loader with optional keyword
        BiConsumer<DefaultTableModel,String> loadApplicants = (mdl,kw) -> {
            mdl.setRowCount(0);
            try(Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
                String sql="SELECT s.student_id,s.name,s.roll_no,s.branch,r.result_status " +
                           "FROM result r JOIN student s ON r.student_id=s.student_id " +
                           "WHERE r.company_id=?";
                if(kw!=null && !kw.isBlank()){
                    sql += " AND (s.name LIKE ? OR s.roll_no LIKE ? )";
                }
                PreparedStatement ps=con.prepareStatement(sql);
                ps.setInt(1,companyId);
                if(kw!=null && !kw.isBlank()){
                    String like="%"+kw+"%";
                    ps.setString(2,like);
                    ps.setString(3,like);
                }
                ResultSet rs=ps.executeQuery();
                while(rs.next()){
                    mdl.addRow(new Object[]{
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("roll_no"),
                            rs.getString("branch"),
                            rs.getString("result_status")
                    });
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,e.getMessage());
            }
        };
        loadApplicants.accept(model, null);
        btnSearch.addActionListener(e -> {
            String kw = tfSearch.getText();
            System.out.println("[DEBUG] applicant search keyword='"+kw+"'");
            loadApplicants.accept(model, kw);
            if(model.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"No applicants match '"+kw+"'");
            }
        });
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            loadApplicants.accept(model, null);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        JButton shortlistBtn = new JButton("Shortlist");
        JButton selectBtn = new JButton("Select");
        JButton rejectBtn = new JButton("Reject");
        JButton exportBtn = new JButton("Export CSV");
        btnPanel.add(shortlistBtn);
        btnPanel.add(selectBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(exportBtn);
        panel.add(createGlassScroll(table),BorderLayout.CENTER);
        panel.add(btnPanel,BorderLayout.SOUTH);

        exportBtn.addActionListener(e ->
            CsvExportUtil.exportTableToCsv(this, table, "applicants_export")
        );

        // show student details on double‑click of applicant row
        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                // single click anywhere in row shows details
                int r = table.rowAtPoint(e.getPoint());
                if(r!=-1){
                    int sid = (int)model.getValueAt(r,0);
                    new StudentDetails(sid);
                }
            }
        });

        // actions for applicant status updates
        shortlistBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select applicant first");
                return;
            }
            int sid = (int)model.getValueAt(row,0);
            try(Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
                PreparedStatement ps = con.prepareStatement("UPDATE result SET result_status='SHORTLISTED' WHERE student_id=? AND company_id=?");
                ps.setInt(1,sid);
                ps.setInt(2,companyId);
                ps.executeUpdate();
                createAdminStatusNotification(con, sid, "SHORTLISTED");
                model.setValueAt("SHORTLISTED",row,4);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });
        selectBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select applicant first");
                return;
            }
            int sid = (int)model.getValueAt(row,0);
            try(Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
                PreparedStatement ps = con.prepareStatement("UPDATE result SET result_status='SELECTED' WHERE student_id=? AND company_id=?");
                ps.setInt(1,sid);
                ps.setInt(2,companyId);
                ps.executeUpdate();
                createAdminStatusNotification(con, sid, "SELECTED");
                model.setValueAt("SELECTED",row,4);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });
        rejectBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select applicant first");
                return;
            }
            int sid = (int)model.getValueAt(row,0);
            try(Connection con=DriverManager.getConnection(DB_URL,DB_USER,DB_PASS)){
                PreparedStatement ps = con.prepareStatement("UPDATE result SET result_status='REJECTED' WHERE student_id=? AND company_id=?");
                ps.setInt(1,sid);
                ps.setInt(2,companyId);
                ps.executeUpdate();
                createAdminStatusNotification(con, sid, "REJECTED");
                model.setValueAt("REJECTED",row,4);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });
        
        // make sure panel is returned before leaving method
        return panel;
    }

    JPanel viewNotificationsPanel(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(80,200,80,200));
        panel.setBackground(new Color(255,255,255,120));

        String[] cols = {"Notification ID", "Date", "From", "Type", "Message", "Read"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createGlassTable(model);

        Runnable loadNotifications = () -> {
            model.setRowCount(0);
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT notif_id, notif_date, sender_role, notif_type, message, is_read " +
                             "FROM notification WHERE receiver_role=? AND receiver_id=? ORDER BY notif_date DESC";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, "COMPANY");
                    ps.setInt(2, companyId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            model.addRow(new Object[]{
                                    rs.getInt("notif_id"),
                                    rs.getTimestamp("notif_date"),
                                    rs.getString("sender_role"),
                                    rs.getString("notif_type"),
                                    rs.getString("message"),
                                    rs.getInt("is_read") == 1 ? "Yes" : "No"
                            });
                        }
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        };

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton markReadBtn = new JButton("Mark Selected Read");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        markReadBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        top.add(refreshBtn);
        top.add(markReadBtn);

        refreshBtn.addActionListener(e -> loadNotifications.run());
        markReadBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select notification first");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            int notifId = (int) model.getValueAt(modelRow, 0);
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE notification SET is_read=1 WHERE notif_id=? AND receiver_role='COMPANY' AND receiver_id=?");
                ps.setInt(1, notifId);
                ps.setInt(2, companyId);
                ps.executeUpdate();
                model.setValueAt("Yes", modelRow, 5);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(createGlassScroll(table), BorderLayout.CENTER);

        loadNotifications.run();

        return panel;
    }

    private static class CompanyNotificationItem {
        int notifId;
        String message;
        String senderRole;
        String notifType;
        Timestamp notifDate;
        boolean isRead;

        CompanyNotificationItem(int notifId, String message, String senderRole, String notifType, Timestamp notifDate, boolean isRead) {
            this.notifId = notifId;
            this.message = message;
            this.senderRole = senderRole;
            this.notifType = notifType;
            this.notifDate = notifDate;
            this.isRead = isRead;
        }
    }

    private List<CompanyNotificationItem> getCompanyNotifications() {
        List<CompanyNotificationItem> notifications = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT notif_id, message, sender_role, notif_type, notif_date, is_read " +
                         "FROM notification " +
                         "WHERE UPPER(COALESCE(receiver_role, '')) = 'COMPANY' " +
                         "AND (receiver_id = ? OR receiver_id IS NULL) " +
                         "ORDER BY notif_date DESC";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, companyId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        notifications.add(new CompanyNotificationItem(
                                rs.getInt("notif_id"),
                                rs.getString("message"),
                                rs.getString("sender_role"),
                                rs.getString("notif_type"),
                                rs.getTimestamp("notif_date"),
                                rs.getInt("is_read") == 1
                        ));
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        return notifications;
    }

    private void markCompanyNotificationRead(int notifId) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "UPDATE notification SET is_read=1 " +
                    "WHERE notif_id=? " +
                    "AND UPPER(COALESCE(receiver_role, ''))='COMPANY' " +
                    "AND (receiver_id=? OR receiver_id IS NULL)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, notifId);
                ps.setInt(2, companyId);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showCompanyNotificationsDialog() {
        JDialog notificationDialog = new JDialog(this, "Notifications", true);
        notificationDialog.setSize(620, 420);
        notificationDialog.setLayout(new BorderLayout(10, 10));
        notificationDialog.setLocationRelativeTo(this);

        JLabel titleLabel = new JLabel("Your Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(41, 84, 209));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        List<CompanyNotificationItem> all = getCompanyNotifications();
        DefaultListModel<CompanyNotificationItem> unreadModel = new DefaultListModel<>();
        DefaultListModel<CompanyNotificationItem> readModel = new DefaultListModel<>();
        for (CompanyNotificationItem item : all) {
            if (item.isRead) {
                readModel.addElement(item);
            } else {
                unreadModel.addElement(item);
            }
        }

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));

        tabbedPane.addTab("Unread (" + unreadModel.getSize() + ")", createCompanyNotificationTabPanel(unreadModel, true, tabbedPane, unreadModel, readModel));
        tabbedPane.addTab("Read (" + readModel.getSize() + ")", createCompanyNotificationTabPanel(readModel, false, tabbedPane, unreadModel, readModel));

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(ev -> notificationDialog.dispose());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        footerPanel.add(closeButton);

        notificationDialog.add(titleLabel, BorderLayout.NORTH);
        notificationDialog.add(tabbedPane, BorderLayout.CENTER);
        notificationDialog.add(footerPanel, BorderLayout.SOUTH);

        notificationDialog.setVisible(true);
    }

    private void updateCompanyNotificationTabTitles(JTabbedPane tabbedPane,
                                                    DefaultListModel<CompanyNotificationItem> unreadModel,
                                                    DefaultListModel<CompanyNotificationItem> readModel) {
        tabbedPane.setTitleAt(0, "Unread (" + unreadModel.getSize() + ")");
        tabbedPane.setTitleAt(1, "Read (" + readModel.getSize() + ")");
    }

    private JPanel createCompanyNotificationTabPanel(DefaultListModel<CompanyNotificationItem> currentModel,
                                                     boolean isUnread,
                                                     JTabbedPane tabbedPane,
                                                     DefaultListModel<CompanyNotificationItem> unreadModel,
                                                     DefaultListModel<CompanyNotificationItem> readModel) {
        JPanel tabPanel = new JPanel(new BorderLayout(10, 10));
        tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JList<CompanyNotificationItem> notificationList = new JList<>(currentModel);
        notificationList.setFont(new Font("Arial", Font.PLAIN, 12));
        notificationList.setFixedCellHeight(58);
        notificationList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                CompanyNotificationItem item = (CompanyNotificationItem) value;
                JLabel label = new JLabel();
                String subtitle = (item.senderRole == null ? "" : item.senderRole) +
                        (item.notifType == null ? "" : " | " + item.notifType);
                label.setText("<html><b>" + subtitle + "</b><br/><font size='-1'>" + item.message + "</font></html>");
                label.setFont(new Font("Arial", Font.PLAIN, 11));
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

                if (isSelected) {
                    label.setBackground(new Color(41, 84, 209));
                    label.setForeground(Color.WHITE);
                    label.setOpaque(true);
                } else {
                    label.setBackground(isUnread ? new Color(230, 240, 255) : Color.WHITE);
                    label.setForeground(new Color(70, 70, 70));
                    label.setOpaque(true);
                }
                return label;
            }
        });

        if (isUnread) {
            notificationList.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 1 && evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        int index = notificationList.locationToIndex(evt.getPoint());
                        if (index >= 0) {
                            Rectangle cellBounds = notificationList.getCellBounds(index, index);
                            if (cellBounds == null || !cellBounds.contains(evt.getPoint())) {
                                return;
                            }
                            CompanyNotificationItem selected = currentModel.getElementAt(index);
                            showCompanyNotificationMessagePopup(selected);
                            markCompanyNotificationRead(selected.notifId);
                            selected.isRead = true;
                            currentModel.removeElementAt(index);
                            readModel.addElement(selected);
                            updateCompanyNotificationTabTitles(tabbedPane, unreadModel, readModel);
                            tabbedPane.setSelectedIndex(0);
                            notificationList.clearSelection();
                        }
                    }
                }
            });
        }

        JScrollPane listScroll = new JScrollPane(notificationList);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tabPanel.add(listScroll, BorderLayout.CENTER);
        return tabPanel;
    }

    private void showCompanyNotificationMessagePopup(CompanyNotificationItem item) {
        String sender = item.senderRole == null ? "-" : item.senderRole;
        String type = item.notifType == null ? "-" : item.notifType;
        String date = item.notifDate == null ? "-" : item.notifDate.toString();

        JTextArea msgArea = new JTextArea(item.message == null ? "" : item.message);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setFont(new Font("Arial", Font.PLAIN, 13));
        msgArea.setBackground(new Color(245, 248, 252));
        msgArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(msgArea);
        scroll.setPreferredSize(new Dimension(420, 160));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("From: " + sender + "   |   Type: " + type + "   |   Date: " + date), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Notification", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openFeedbackDialog() {
        JTextArea feedbackArea = new JTextArea(6, 28);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setPreferredSize(new Dimension(380, 140));

        int option = JOptionPane.showConfirmDialog(
                this,
                feedbackScroll,
                "Share Your Feedback",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String message = feedbackArea.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Feedback cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (message.length() > 300) {
            JOptionPane.showMessageDialog(this, "Feedback must be 300 characters or less.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (submitCompanyFeedback(message)) {
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit feedback.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean submitCompanyFeedback(String message) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String insertSql = "INSERT INTO feedback(given_by, user_id, message, date) VALUES(?,?,?,CURDATE())";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, "company");
                ps.setInt(2, companyId);
                ps.setString(3, message);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Error submitting company feedback: " + ex.getMessage());
            return false;
        }
    }

    JTable createGlassTable(DefaultTableModel model){

        JTable table=new JTable(model);

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI",Font.BOLD,16));
        table.setForeground(Color.BLACK);
        table.setOpaque(false);

        JTableHeader header=table.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,18));

        return table;
    }

    JScrollPane createGlassScroll(JTable table){

        JScrollPane scroll=new JScrollPane(table);

        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    JLabel createLabel(String text){

        JLabel label=new JLabel(text);
        label.setFont(new Font("Segoe UI",Font.BOLD,28));
        label.setForeground(Color.BLACK);

        return label;
    }

    private void createAdminStatusNotification(Connection con, int studentId, String status) throws SQLException {
        String companyName = "Company";
        Integer latestDriveId = null;
        String roleName = "Role";

        String companySql = "SELECT name FROM company WHERE company_id=?";
        try (PreparedStatement companyStmt = con.prepareStatement(companySql)) {
            companyStmt.setInt(1, companyId);
            try (ResultSet companyRs = companyStmt.executeQuery()) {
                if (companyRs.next()) {
                    companyName = companyRs.getString("name");
                }
            }
        }

        String driveSql = "SELECT drive_id, role FROM drive WHERE company_id=? ORDER BY drive_date DESC, drive_id DESC LIMIT 1";
        try (PreparedStatement driveStmt = con.prepareStatement(driveSql)) {
            driveStmt.setInt(1, companyId);
            try (ResultSet driveRs = driveStmt.executeQuery()) {
                if (driveRs.next()) {
                    latestDriveId = driveRs.getInt("drive_id");
                    String dbRole = driveRs.getString("role");
                    if (dbRole != null && !dbRole.isBlank()) {
                        roleName = dbRole;
                    }
                }
            }
        }

        String message;
        if ("SELECTED".equalsIgnoreCase(status)) {
            message = "congratulations for being selected for " + companyName + " company for " + roleName + " role";
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            message = "you are rejected for " + companyName + " company for " + roleName + " role";
        } else {
            message = "congratulation for being shortlisted for next round at " + companyName + " company for " + roleName + " role";
        }

        String insertSql = "INSERT INTO notification (sender_role, sender_id, receiver_role, receiver_id, message, notif_type, related_drive_id, notif_date, is_read) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 0)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
            insertStmt.setString(1, "ADMIN");
            insertStmt.setNull(2, Types.INTEGER);
            insertStmt.setString(3, "STUDENT");
            insertStmt.setInt(4, studentId);
            insertStmt.setString(5, message);
            insertStmt.setString(6, status.toUpperCase());
            if (latestDriveId == null) {
                insertStmt.setNull(7, Types.INTEGER);
            } else {
                insertStmt.setInt(7, latestDriveId);
            }
            insertStmt.executeUpdate();
        }
    }

    JTextField createField(){

        JTextField field=new JTextField();
        field.setFont(new Font("Segoe UI",Font.PLAIN,18));

        return field;
    }

    void logout(){
        dispose();
        JOptionPane.showMessageDialog(null,"Logged Out Successfully");
    }

    public static void main(String[] args){
        new CompanyDashboard(1);
    }
}