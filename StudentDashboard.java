import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StudentDashboard {
	private static String currentStudentName;
	private static String currentStudentRoll;
	private static String currentStudentEmail;
	private static String currentStudentPhone;
	private static String currentStudentSkills;
	private static JFrame currentLoginFrame;
	private static final String DASHBOARD_BACKGROUND_IMAGE = "student.jpeg";
	static final String DB_URL = "jdbc:mysql://localhost:3306/placement_db";
	static final String DB_USER = "root";
	static final String DB_PASS = "root";

	private static JPanel createBackgroundPanel(String imagePath) {
		final Image backgroundImage = new ImageIcon(imagePath).getImage();
		return new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				if (backgroundImage != null && backgroundImage.getWidth(null) > 0) {
					g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
				} else {
					g2d.setPaint(new GradientPaint(0, 0, new Color(230, 240, 250), getWidth(), getHeight(), new Color(205, 225, 245)));
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}

				// Keep readability while allowing the background image to appear more strongly.
				g2d.setColor(new Color(255, 255, 255, 0));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
	}

	private static void makeScrollPaneTransparent(JScrollPane scrollPane) {
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
	}

	private static void styleDashboardButton(JButton button, Color baseColor, Color hoverColor, int fontSize) {
		button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
		button.setForeground(Color.WHITE);
		button.setBackground(baseColor);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 1),
				BorderFactory.createEmptyBorder(8, 14, 8, 14)));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setContentAreaFilled(true);

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (button.isEnabled()) {
					button.setBackground(hoverColor);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if (button.isEnabled()) {
					button.setBackground(baseColor);
				}
			}
		});
	}

	private static void styleDashboardMenuItem(JMenuItem item) {
		item.setFont(new Font("Segoe UI", Font.BOLD, 14));
		item.setBackground(new Color(232, 242, 252));
		item.setForeground(new Color(22, 57, 97));
		item.setOpaque(true);
		item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
	}

	private static JPanel createCardGridWrapper(JPanel cardGrid) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.add(cardGrid, BorderLayout.NORTH);
		return wrapper;
	}

	private static void updateCardGridSize(JPanel cardGrid, int cardCount) {
		int columns = 4;
		int minVisibleCards = 8;
		int effectiveCards = Math.max(cardCount, minVisibleCards);
		int rows = (int) Math.ceil(effectiveCards / (double) columns);

		int cardWidth = 260;
		int cardHeight = 88;
		int hGap = 10;
		int vGap = 10;

		int prefWidth = (columns * cardWidth) + ((columns - 1) * hGap);
		int prefHeight = (rows * cardHeight) + ((rows - 1) * vGap);

		cardGrid.setPreferredSize(new Dimension(prefWidth, prefHeight));
	}



	public static Connection connectToDatabase() {
		try {
			return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Database connection failed: " + ex.getMessage());
			return null;
		}
	}

	public static JPanel createDriveCard(String company, String position, String type, String location,
			String skills, String salary, String posted, String daysLeft) {
		return createDriveCard(0, 0, company, position, type, location, skills, salary, posted, daysLeft, null, null, false, null);
	}

	public static JPanel createDriveCard(int driveId, int companyId, String company, String position, String type, String location,
			String skills, String salary, String posted, String daysLeft, String studentEmail, JFrame parentFrame, boolean allowApply, Runnable onApplySuccess) {
		JPanel card = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(255, 255, 255, 200));
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
				g2.setColor(new Color(255, 255, 255, 220));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
				g2.dispose();
			}
		};
		card.setOpaque(false);
		card.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		card.setPreferredSize(new Dimension(260, 88));
		card.setMaximumSize(new Dimension(260, 88));

		JPanel topPanel = new JPanel(new BorderLayout(6, 0));
		topPanel.setOpaque(false);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 8));

		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(new Color(41, 84, 209));
		logoPanel.setPreferredSize(new Dimension(30, 30));
		logoPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		JLabel logoLabel = new JLabel(company.substring(0, 1), SwingConstants.CENTER);
		logoLabel.setFont(new Font("Arial", Font.BOLD, 14));
		logoLabel.setForeground(Color.WHITE);
		logoPanel.add(logoLabel);

		JPanel companyInfoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
		companyInfoPanel.setOpaque(false);

		JLabel companyLabel = new JLabel(company);
		companyLabel.setFont(new Font("Arial", Font.BOLD, 12));
		companyLabel.setForeground(new Color(18, 35, 62));

		JLabel positionLabel = new JLabel(position);
		positionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
		positionLabel.setForeground(new Color(33, 63, 98));

		companyInfoPanel.add(companyLabel);
		companyInfoPanel.add(positionLabel);

		topPanel.add(logoPanel, BorderLayout.WEST);
		topPanel.add(companyInfoPanel, BorderLayout.CENTER);

		JPanel badgePanel = new JPanel(new BorderLayout());
		badgePanel.setOpaque(false);
		badgePanel.setPreferredSize(new Dimension(148, 44));

		JPanel salaryBadge = new JPanel(new BorderLayout());
		salaryBadge.setBackground(new Color(41, 84, 209));
		salaryBadge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
				BorderFactory.createEmptyBorder(6, 10, 6, 10)));
		JLabel salaryLabel = new JLabel(position, SwingConstants.CENTER);
		salaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
		salaryLabel.setForeground(Color.WHITE);
		salaryBadge.add(salaryLabel, BorderLayout.CENTER);

		badgePanel.add(salaryBadge);
		topPanel.add(badgePanel, BorderLayout.EAST);

		JPanel middlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
		middlePanel.setOpaque(false);
		middlePanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 2, 8));

		JLabel locationLabel = new JLabel("Venue: " + location, SwingConstants.LEFT);
		locationLabel.setFont(new Font("Arial", Font.BOLD, 13));
		locationLabel.setForeground(new Color(16, 42, 74));
		locationLabel.setOpaque(true);
		locationLabel.setBackground(new Color(240, 248, 255, 170));
		locationLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

		JLabel skillLabel = new JLabel("Eligibility: " + skills, SwingConstants.LEFT);
		skillLabel.setFont(new Font("Arial", Font.BOLD, 12));
		skillLabel.setForeground(new Color(18, 52, 92));
		skillLabel.setOpaque(true);
		skillLabel.setBackground(new Color(232, 242, 255, 185));
		skillLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

		middlePanel.add(locationLabel);
		middlePanel.add(skillLabel);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(1, 8, 3, 8));

		String statusText = (daysLeft != null && !daysLeft.isBlank()) ? daysLeft : "OPEN";
		JLabel dateLabel = new JLabel("Status: " + statusText.toUpperCase());
		dateLabel.setFont(new Font("Arial", Font.BOLD, 10));
		dateLabel.setForeground(new Color(17, 49, 84));

		JPanel actionPanel = new JPanel(new BorderLayout(8, 0));
		actionPanel.setOpaque(false);

		JLabel liveStatusLabel = new JLabel(" ");
		liveStatusLabel.setFont(new Font("Arial", Font.BOLD, 9));
		liveStatusLabel.setForeground(new Color(16, 105, 69));

		JButton applyButton = new JButton("Apply");
		styleDashboardButton(applyButton, new Color(39, 106, 133, 225), new Color(56, 128, 158, 235), 13);
		applyButton.setPreferredSize(new Dimension(76, 28));
		applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// Check if student has already applied to this company
		boolean hasApplied = false;
		if (allowApply && studentEmail != null && companyId > 0) {
			hasApplied = checkIfAlreadyApplied(studentEmail, companyId);
		}

		if (!allowApply || hasApplied) {
			applyButton.setText("Applied");
			applyButton.setEnabled(false);
			applyButton.setBackground(new Color(94, 112, 126, 220));
			applyButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		if (allowApply && !hasApplied && driveId > 0 && companyId > 0 && studentEmail != null && parentFrame != null) {
			applyButton.addActionListener(ev -> {
				int confirm = JOptionPane.showConfirmDialog(
					parentFrame,
					"Apply for " + position + " at " + company + "?",
					"Confirm Application",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
				);
				if (confirm != JOptionPane.YES_OPTION) {
					liveStatusLabel.setText("Application cancelled");
					liveStatusLabel.setForeground(new Color(108, 117, 125));
					return;
				}

				boolean success = applyForDrive(driveId, companyId, company, position, studentEmail, parentFrame);
				if (success) {
					applyButton.setText("Applied");
					applyButton.setEnabled(false);
					applyButton.setBackground(new Color(94, 112, 126, 220));
					applyButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					liveStatusLabel.setText("Application submitted");
					liveStatusLabel.setForeground(new Color(25, 135, 84));
					if (onApplySuccess != null) {
						onApplySuccess.run();
					}
				} else {
					liveStatusLabel.setText("Application not submitted");
					liveStatusLabel.setForeground(new Color(220, 53, 69));
				}
				card.revalidate();
				card.repaint();
			});
		} else if (allowApply && !hasApplied) {
			applyButton.addActionListener(ev -> {
				liveStatusLabel.setText("Apply functionality not available");
				liveStatusLabel.setForeground(new Color(220, 53, 69));
			});
		}

		JPanel statusPanel = new JPanel(new GridLayout(2, 1, 0, 1));
		statusPanel.setOpaque(false);
		statusPanel.add(dateLabel);
		statusPanel.add(liveStatusLabel);

		actionPanel.add(statusPanel, BorderLayout.CENTER);
		actionPanel.add(applyButton, BorderLayout.EAST);

		bottomPanel.add(actionPanel, BorderLayout.CENTER);
		card.add(topPanel, BorderLayout.NORTH);
		card.add(middlePanel, BorderLayout.CENTER);
		card.add(bottomPanel, BorderLayout.SOUTH);

		return card;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(StudentDashboard::showLoginScreen);
	}

	private static void showLoginScreen() {
		JFrame loginFrame = new JFrame("Student Login");
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		JPanel backgroundPanel = createBackgroundPanel(DASHBOARD_BACKGROUND_IMAGE);
		backgroundPanel.setLayout(new GridBagLayout());
		loginFrame.setContentPane(backgroundPanel);

		JPanel loginPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(255, 255, 255, 175));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
			}
		};
		loginPanel.setOpaque(false);
		loginPanel.setBorder(BorderFactory.createEmptyBorder(42, 50, 42, 50));
		loginPanel.setPreferredSize(new Dimension(700, 330));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(14, 12, 14, 12);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel rollLabel = new JLabel("Roll Number");
		rollLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
		rollLabel.setForeground(new Color(30, 30, 30));

		JTextField rollField = new JTextField();
		rollField.setPreferredSize(new Dimension(270, 42));
		rollField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		rollField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(165, 165, 165), 1),
				BorderFactory.createEmptyBorder(6, 10, 6, 10)
		));

		JLabel passLabel = new JLabel("Password");
		passLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
		passLabel.setForeground(new Color(30, 30, 30));

		JPasswordField passField = new JPasswordField();
		passField.setPreferredSize(new Dimension(270, 42));
		passField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		passField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(165, 165, 165), 1),
				BorderFactory.createEmptyBorder(6, 10, 6, 10)
		));

		JButton loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(170, 52));
		loginButton.setBackground(new Color(36, 90, 210));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
		loginButton.setBorderPainted(false);
		loginButton.setFocusPainted(false);
		loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		gbc.gridx = 0;
		gbc.gridy = 0;
		loginPanel.add(rollLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		loginPanel.add(rollField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		loginPanel.add(passLabel, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		loginPanel.add(passField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(18, 12, 6, 12);
		loginPanel.add(loginButton, gbc);

		loginButton.addActionListener(e -> {
			String rollNo = rollField.getText().trim();
			String password = new String(passField.getPassword());

			if (rollNo.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(loginFrame, "Please enter Roll Number and Password");
				return;
			}

			String[] studentData = authenticateStudent(rollNo, password);
			if (studentData == null) {
				JOptionPane.showMessageDialog(loginFrame, "Invalid Credentials");
				return;
			}

			loginFrame.setVisible(false);
			showDashboard(studentData[0], studentData[1], studentData[2], studentData[3], studentData[4], loginFrame);
		});

		backgroundPanel.add(loginPanel);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setVisible(true);
	}

	private static String[] authenticateStudent(String rollNo, String password) {
		try (Connection conn = connectToDatabase()) {
			if (conn == null) {
				return null;
			}

			String sql = "SELECT name, roll_no, email, phone, skills FROM student WHERE TRIM(LOWER(roll_no)) = TRIM(LOWER(?)) AND password = ? LIMIT 1";
			java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, rollNo);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String name = rs.getString("name");
				String roll = rs.getString("roll_no");
				String email = rs.getString("email");
				String phone = rs.getString("phone");
				String skills = rs.getString("skills");

				if (phone == null || phone.trim().isEmpty()) {
					phone = "N/A";
				}
				if (skills == null || skills.trim().isEmpty()) {
					skills = "Not Added";
				}

				return new String[]{name, roll, email, phone, skills};
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Unable to login: " + ex.getMessage());
		}
		return null;
	}
	
	// Helper method to check if student has already applied to a company
	private static boolean checkIfAlreadyApplied(String studentEmail, int companyId) {
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				// First get student_id
				String getIdSql = "SELECT student_id FROM student WHERE email = ?";
				java.sql.PreparedStatement getIdStmt = conn.prepareStatement(getIdSql);
				getIdStmt.setString(1, studentEmail);
				ResultSet idRs = getIdStmt.executeQuery();
				
				if (idRs.next()) {
					int studentId = idRs.getInt("student_id");
					
					// Check in application table
					String checkSql = "SELECT COUNT(*) as count FROM application WHERE student_id = ? AND company_id = ?";
					java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
					checkStmt.setInt(1, studentId);
					checkStmt.setInt(2, companyId);
					ResultSet rs = checkStmt.executeQuery();
					
					if (rs.next()) {
						return rs.getInt("count") > 0;
					}
				}
			}
		} catch (SQLException ex) {
			System.err.println("Error checking application status: " + ex.getMessage());
		}
		return false;
	}
	
	// Method to fetch upcoming drives from database
	private static List<Object[]> getUpcomingDrives() {
		List<Object[]> drives = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				// Show active drives only; closed drives are excluded from Upcoming.
				String sql = "SELECT d.drive_id, d.company_id, c.name AS company_name, d.role, d.package, d.eligibility, d.venue, d.drive_date, d.last_date, d.status FROM drive d " +
							 "JOIN company c ON d.company_id = c.company_id " +
							 "WHERE COALESCE(TRIM(LOWER(d.status)), 'open') <> 'closed' " +
							 "AND (d.last_date IS NULL OR d.last_date >= CURDATE()) " +
							 "ORDER BY d.drive_date DESC";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					int driveId = rs.getInt("drive_id");
					int companyId = rs.getInt("company_id");
					String company = rs.getString("company_name");
					String position = rs.getString("role");
					String salary = rs.getString("package");
					String eligibility = rs.getString("eligibility");
					String venue = rs.getString("venue");
					String lastDate = formatDriveDate(rs.getDate("last_date"));
					String status = rs.getString("status") != null ? rs.getString("status") : "OPEN";
					
					drives.add(new Object[]{driveId, companyId, company, position, salary, eligibility, venue, lastDate, status});
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching upcoming drives: " + ex.getMessage());
		}
		return drives;
	}

	private static String formatDriveDate(java.sql.Date driveDate) {
		return driveDate != null ? driveDate.toString() : "N/A";
	}

	private static String formatSkillsForDisplay(String skillsFromDb) {
		if (skillsFromDb == null || skillsFromDb.trim().isEmpty()) {
			return "";
		}

		String[] parts = skillsFromDb.split("\\s*,\\s*");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			String s = part.trim();
			if (!s.isEmpty()) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}

	private static String normalizeSkillsForStorage(String skillsFromEditor) {
		if (skillsFromEditor == null || skillsFromEditor.trim().isEmpty()) {
			return "";
		}

		String normalized = skillsFromEditor.replace("\r\n", "\n").replace("\r", "\n");
		normalized = normalized.replace("\n", ",");
		String[] parts = normalized.split("\\s*,\\s*");

		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			String s = part.trim();
			if (!s.isEmpty()) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}
	
	// Method to fetch applied drives for a student
	private static List<Object[]> getAppliedDrives(String studentEmail) {
		List<Object[]> applied = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "SELECT r.result_id, c.name AS company_name, COALESCE(d.role, 'Position') AS role_name, r.result_status, d.last_date, d.venue, d.eligibility FROM result r " +
							 "JOIN student s ON r.student_id = s.student_id " +
							 "JOIN company c ON r.company_id = c.company_id " +
							 "LEFT JOIN drive d ON r.company_id = d.company_id " +
							 "WHERE s.email = ? " +
							 "AND TRIM(LOWER(COALESCE(r.result_status, ''))) = 'applied' " +
							 "ORDER BY r.result_id DESC " +
							 "LIMIT 20";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, studentEmail);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					String company = rs.getString("company_name");
					String position = rs.getString("role_name");
					String status = rs.getString("result_status");
					String lastDate = formatDriveDate(rs.getDate("last_date"));
					String venue = rs.getString("venue") != null ? rs.getString("venue") : "-";
					String eligibility = rs.getString("eligibility") != null ? rs.getString("eligibility") : "-";
					
					applied.add(new Object[]{company, position, "Full Time", status, lastDate, venue, eligibility});
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching applied drives: " + ex.getMessage());
		}
		return applied;
	}
	
	// Method to fetch shortlisted for student
	private static List<Object[]> getShortlistedDrives(String studentEmail) {
		List<Object[]> shortlisted = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "SELECT r.result_id, c.name AS company_name, COALESCE(d.role, 'Position') AS role_name, d.last_date, d.venue, d.eligibility FROM result r " +
							 "JOIN student s ON r.student_id = s.student_id " +
							 "JOIN company c ON r.company_id = c.company_id " +
							 "LEFT JOIN drive d ON r.company_id = d.company_id " +
							 "WHERE s.email = ? AND TRIM(LOWER(COALESCE(r.result_status, ''))) = 'shortlisted' " +
							 "ORDER BY r.result_id DESC " +
							 "LIMIT 20";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, studentEmail);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					String company = rs.getString("company_name");
					String position = rs.getString("role_name");
					String lastDate = formatDriveDate(rs.getDate("last_date"));
					String venue = rs.getString("venue") != null ? rs.getString("venue") : "-";
					String eligibility = rs.getString("eligibility") != null ? rs.getString("eligibility") : "-";
					
					shortlisted.add(new Object[]{company, position, "Full Time", "Technical Round", "2026-03-05", "Online", lastDate, venue, eligibility});
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching shortlisted drives: " + ex.getMessage());
		}
		return shortlisted;
	}
	
	// Method to fetch offers for student
	private static List<Object[]> getOffers(String studentEmail) {
		List<Object[]> offers = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "SELECT r.result_id, c.name AS company_name, COALESCE(d.role, 'Position') AS role_name, d.package AS package_value, r.result_status, d.last_date, d.venue, d.eligibility FROM result r " +
							 "JOIN student s ON r.student_id = s.student_id " +
							 "JOIN company c ON r.company_id = c.company_id " +
							 "LEFT JOIN drive d ON r.company_id = d.company_id " +
							 "WHERE s.email = ? AND TRIM(LOWER(COALESCE(r.result_status, ''))) IN ('selected', 'offer', 'offer accepted', 'offer received') " +
							 "ORDER BY r.result_id DESC " +
							 "LIMIT 20";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, studentEmail);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					String company = rs.getString("company_name");
					String position = rs.getString("role_name");
					String salary = rs.getString("package_value") != null ? rs.getString("package_value") : "Package";
					String offerStatus = rs.getString("result_status");
					String lastDate = formatDriveDate(rs.getDate("last_date"));
					String venue = rs.getString("venue") != null ? rs.getString("venue") : "-";
					String eligibility = rs.getString("eligibility") != null ? rs.getString("eligibility") : "-";
					
					offers.add(new Object[]{company, position, "Full Time", salary, "2026-07-01", offerStatus, lastDate, venue, eligibility});
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching offers: " + ex.getMessage());
		}
		return offers;
	}

	// Method to fetch rejected drives for student
	private static List<Object[]> getRejectedDrives(String studentEmail) {
		List<Object[]> rejected = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "SELECT r.result_id, c.name AS company_name, COALESCE(d.role, 'Position') AS role_name, r.result_status, d.last_date, d.venue, d.eligibility FROM result r " +
						 "JOIN student s ON r.student_id = s.student_id " +
						 "JOIN company c ON r.company_id = c.company_id " +
						 "LEFT JOIN drive d ON r.company_id = d.company_id " +
						 "WHERE s.email = ? AND TRIM(LOWER(COALESCE(r.result_status, ''))) = 'rejected' " +
						 "ORDER BY r.result_id DESC " +
						 "LIMIT 20";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, studentEmail);
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					String company = rs.getString("company_name");
					String position = rs.getString("role_name");
					String status = rs.getString("result_status");
					String lastDate = formatDriveDate(rs.getDate("last_date"));
					String venue = rs.getString("venue") != null ? rs.getString("venue") : "-";
					String eligibility = rs.getString("eligibility") != null ? rs.getString("eligibility") : "-";

					rejected.add(new Object[]{company, position, "Full Time", status, lastDate, venue, eligibility});
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching rejected drives: " + ex.getMessage());
		}
		return rejected;
	}

	// Method to show apply dialog with file upload
	public static void showApplyDialog(int driveId, int companyId, String companyName, String position, String studentEmail, JFrame parentFrame) {
		JDialog applyDialog = new JDialog(parentFrame, "Apply for " + companyName, true);
		applyDialog.setSize(500, 400);
		applyDialog.setLayout(new BorderLayout(10, 10));
		applyDialog.setLocationRelativeTo(parentFrame);
		
		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(41, 84, 209));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel titleLabel = new JLabel("Apply for " + position + " at " + companyName);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setForeground(Color.WHITE);
		headerPanel.add(titleLabel);
		
		// Content Panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		contentPanel.setBackground(Color.WHITE);
		
		// Student Email Label
		JLabel emailLabel = new JLabel("Student Email: " + studentEmail);
		emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		contentPanel.add(emailLabel);
		contentPanel.add(Box.createVerticalStrut(20));
		
		// File Upload Section
		JLabel fileLabel = new JLabel("Upload Resume/CV (PDF, DOC, DOCX):");
		fileLabel.setFont(new Font("Arial", Font.BOLD, 14));
		fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		filePanel.setBackground(Color.WHITE);
		filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JTextField filePathField = new JTextField(25);
		filePathField.setEditable(false);
		
		JButton browseButton = new JButton("Browse");
		browseButton.setBackground(new Color(108, 117, 125));
		browseButton.setForeground(Color.WHITE);
		browseButton.setFocusPainted(false);
		browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		final File[] selectedFile = {null};
		
		browseButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Resume Files (PDF, DOC, DOCX)", "pdf", "doc", "docx"));
			int result = fileChooser.showOpenDialog(applyDialog);
			
			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFile[0] = fileChooser.getSelectedFile();
				filePathField.setText(selectedFile[0].getName());
			}
		});
		
		filePanel.add(filePathField);
		filePanel.add(browseButton);
		
		contentPanel.add(fileLabel);
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(filePanel);
		contentPanel.add(Box.createVerticalStrut(30));
		
		// Button Panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton submitButton = new JButton("Submit Application");
		submitButton.setFont(new Font("Arial", Font.BOLD, 14));
		submitButton.setBackground(new Color(40, 167, 69));
		submitButton.setForeground(Color.WHITE);
		submitButton.setBorderPainted(false);
		submitButton.setFocusPainted(false);
		submitButton.setPreferredSize(new Dimension(180, 40));
		submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
		cancelButton.setBackground(new Color(220, 53, 69));
		cancelButton.setForeground(Color.WHITE);
		cancelButton.setBorderPainted(false);
		cancelButton.setFocusPainted(false);
		cancelButton.setPreferredSize(new Dimension(120, 40));
		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		submitButton.addActionListener(e -> {
			if (selectedFile[0] == null) {
				JOptionPane.showMessageDialog(applyDialog, "Please select a resume file to upload!", "No File Selected", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			// Submit application to database
			boolean success = submitApplication(companyId, studentEmail, selectedFile[0]);
			
			if (success) {
				JOptionPane.showMessageDialog(applyDialog, "Application submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
				applyDialog.dispose();
				reloadDashboardAfterApply(parentFrame);
			} else {
				JOptionPane.showMessageDialog(applyDialog, "Failed to submit application. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		cancelButton.addActionListener(e -> applyDialog.dispose());
		
		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);
		
		contentPanel.add(buttonPanel);
		
		applyDialog.add(headerPanel, BorderLayout.NORTH);
		applyDialog.add(contentPanel, BorderLayout.CENTER);
		
		applyDialog.setVisible(true);
	}

	// Direct apply flow without opening resume upload popup
	public static boolean applyForDrive(int driveId, int companyId, String companyName, String position, String studentEmail, JFrame parentFrame) {
		return submitApplicationWithoutResume(companyId, studentEmail, companyName, position);
	}
	
	// Method to submit application to database
	private static boolean submitApplication(int companyId, String studentEmail, File resumeFile) {
		Connection conn = null;
		try {
			conn = connectToDatabase();
			if (conn == null) {
				JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			// Create applications directory if it doesn't exist
			File applicationsDir = new File("applications");
			if (!applicationsDir.exists()) {
				applicationsDir.mkdirs();
			}
			
			// Copy file to applications directory
			String fileName = System.currentTimeMillis() + "_" + resumeFile.getName();
			File destFile = new File(applicationsDir, fileName);
			Files.copy(resumeFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
			// Get student_id from email
			String getStudentIdSql = "SELECT student_id FROM student WHERE email = ?";
			java.sql.PreparedStatement getStudentStmt = conn.prepareStatement(getStudentIdSql);
			getStudentStmt.setString(1, studentEmail);
			ResultSet studentRs = getStudentStmt.executeQuery();
			
			if (!studentRs.next()) {
				JOptionPane.showMessageDialog(null, "Student not found: " + studentEmail, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			int studentId = studentRs.getInt("student_id");
			System.out.println("DEBUG: Student ID = " + studentId + ", Company ID = " + companyId);
			
			// Check if already applied using application table
			String checkSql = "SELECT status FROM application WHERE student_id = ? AND company_id = ? ORDER BY app_id DESC LIMIT 1";
			java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setInt(1, studentId);
			checkStmt.setInt(2, companyId);
			ResultSet checkRs = checkStmt.executeQuery();
			
			if (checkRs.next()) {
				String existingStatus = checkRs.getString("status");
				JOptionPane.showMessageDialog(null,
					"You already have an application for this company.\nCurrent Status: " + existingStatus,
					"Already Applied",
					JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			// Insert application into application table
			String insertSql = "INSERT INTO application (student_id, company_id, apply_date, status) VALUES (?, ?, CURDATE(), 'Applied')";
			java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql);
			insertStmt.setInt(1, studentId);
			insertStmt.setInt(2, companyId);
			
			System.out.println("DEBUG: Executing INSERT with student_id=" + studentId + ", company_id=" + companyId);
			int rowsAffected = insertStmt.executeUpdate();
			System.out.println("DEBUG: Rows affected = " + rowsAffected);
			
			if (rowsAffected > 0) {
				// Also insert into result table for tracking
				String insertResultSql = "INSERT INTO result (student_id, company_id, result_status) VALUES (?, ?, 'Applied')";
				java.sql.PreparedStatement resultStmt = conn.prepareStatement(insertResultSql);
				resultStmt.setInt(1, studentId);
				resultStmt.setInt(2, companyId);
				resultStmt.executeUpdate();
			}
			
			return rowsAffected > 0;
			
		} catch (SQLException ex) {
			String errorMsg = "SQL Error: " + ex.getMessage() + "\nSQL State: " + ex.getSQLState() + "\nError Code: " + ex.getErrorCode();
			JOptionPane.showMessageDialog(null, errorMsg, "Database Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return false;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "File error: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Submit application record without resume upload.
	private static boolean submitApplicationWithoutResume(int companyId, String studentEmail, String companyName, String position) {
		Connection conn = null;
		try {
			conn = connectToDatabase();
			if (conn == null) {
				System.err.println("Database connection failed!");
				return false;
			}

			String getStudentIdSql = "SELECT student_id FROM student WHERE email = ?";
			java.sql.PreparedStatement getStudentStmt = conn.prepareStatement(getStudentIdSql);
			getStudentStmt.setString(1, studentEmail);
			ResultSet studentRs = getStudentStmt.executeQuery();

			if (!studentRs.next()) {
				System.err.println("Student not found: " + studentEmail);
				return false;
			}

			int studentId = studentRs.getInt("student_id");

			// Check if already applied using application table
			String checkSql = "SELECT status FROM application WHERE student_id = ? AND company_id = ? ORDER BY app_id DESC LIMIT 1";
			java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setInt(1, studentId);
			checkStmt.setInt(2, companyId);
			ResultSet checkRs = checkStmt.executeQuery();

			if (checkRs.next()) {
				String existingStatus = checkRs.getString("status");
				System.out.println("Already applied to this company. Status: " + existingStatus);
				return false;
			}

			// Insert into application table with current date
			String insertSql = "INSERT INTO application (student_id, company_id, apply_date, status) VALUES (?, ?, CURDATE(), 'Applied')";
			java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql);
			insertStmt.setInt(1, studentId);
			insertStmt.setInt(2, companyId);

			int rowsAffected = insertStmt.executeUpdate();
			
			if (rowsAffected > 0) {
				// Also insert into result table for tracking
				String insertResultSql = "INSERT INTO result (student_id, company_id, result_status) VALUES (?, ?, 'Applied')";
				java.sql.PreparedStatement resultStmt = conn.prepareStatement(insertResultSql);
				resultStmt.setInt(1, studentId);
				resultStmt.setInt(2, companyId);
				resultStmt.executeUpdate();
				
				// Create notification for applied status
				createNotification(studentEmail, companyName, position, "Applied");
				return true;
			}
			return false;

		} catch (SQLException ex) {
			System.err.println("SQL Error: " + ex.getMessage() + " | SQL State: " + ex.getSQLState() + " | Error Code: " + ex.getErrorCode());
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean submitStudentFeedback(String studentEmail, String message) {
		if (studentEmail == null || studentEmail.isBlank() || message == null || message.isBlank()) {
			return false;
		}

		try (Connection conn = connectToDatabase()) {
			if (conn == null) {
				return false;
			}

			String getStudentIdSql = "SELECT student_id FROM student WHERE email = ?";
			java.sql.PreparedStatement getStudentStmt = conn.prepareStatement(getStudentIdSql);
			getStudentStmt.setString(1, studentEmail);
			ResultSet studentRs = getStudentStmt.executeQuery();

			if (!studentRs.next()) {
				return false;
			}

			int studentId = studentRs.getInt("student_id");

			String insertSql = "INSERT INTO feedback(given_by, user_id, message, date) VALUES(?,?,?,CURDATE())";
			java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql);
			insertStmt.setString(1, "student");
			insertStmt.setInt(2, studentId);
			insertStmt.setString(3, message);

			return insertStmt.executeUpdate() > 0;
		} catch (SQLException ex) {
			System.err.println("Error submitting feedback: " + ex.getMessage());
			return false;
		}
	}

	private static class NotificationItem {
		public String message;
		public boolean isRead;
		public long resultId;
		public String companyName;
		public String rolePosition;
		public String status;

		public NotificationItem(String message, boolean isRead, long resultId, String companyName, String rolePosition, String status) {
			this.message = message;
			this.isRead = isRead;
			this.resultId = resultId;
			this.companyName = companyName;
			this.rolePosition = rolePosition;
			this.status = status;
		}
	
		@Override
		public String toString() {
			return companyName + " - " + rolePosition;
		}
	}

	private static List<NotificationItem> getStudentNotificationsWithStatus(String studentEmail) {
		List<NotificationItem> notifications = new ArrayList<>();
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "SELECT n.notif_id, n.message, n.notif_type, n.sender_role, n.notif_date, n.is_read, " +
						 "c.name AS company_name, d.role AS drive_role " +
						 "FROM notification n " +
						 "LEFT JOIN drive d ON n.related_drive_id = d.drive_id " +
						 "LEFT JOIN company c ON d.company_id = c.company_id " +
						 "JOIN student s ON s.student_id = n.receiver_id " +
						 "WHERE s.email = ? AND UPPER(COALESCE(n.receiver_role, '')) = 'STUDENT' " +
						 "ORDER BY n.notif_date DESC LIMIT 50";
				
				try {
					java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setString(1, studentEmail);
					
					System.out.println("[DEBUG] Fetching notifications for: " + studentEmail);
					System.out.println("[DEBUG] SQL: " + sql);
					
					ResultSet rs = stmt.executeQuery();
					int count = 0;

					while (rs.next()) {
						count++;
						long notifId = rs.getLong("notif_id");
						String dbMessage = rs.getString("message");
						String message = dbMessage;
						String notifType = rs.getString("notif_type");
						String senderRole = rs.getString("sender_role");
						String dbCompany = rs.getString("company_name");
						String dbRole = rs.getString("drive_role");
						int isRead = rs.getInt("is_read");

						System.out.println("[DEBUG] Found notification #" + count + ": " + message + " (read=" + isRead + ")");

						// Extract company and role from the notification message when available.
						String company = "Company";
						String status = "Update";
						String targetRole = notifType != null ? notifType : "Update";

						if (dbCompany != null && !dbCompany.isBlank()) {
							company = dbCompany;
						}
						if (dbRole != null && !dbRole.isBlank()) {
							targetRole = dbRole;
						}
						
						if ((dbCompany == null || dbCompany.isBlank() || dbRole == null || dbRole.isBlank())
								&& message != null && message.toLowerCase().contains(" company for ")) {
							String lower = message.toLowerCase();
							int companyForIndex = lower.indexOf(" company for ");
							if (companyForIndex > 0) {
								int companyStart = lower.lastIndexOf(" at ", companyForIndex);
								if (companyStart >= 0) {
									companyStart += 4;
								} else {
									companyStart = lower.lastIndexOf(" for ", companyForIndex);
									companyStart = companyStart >= 0 ? companyStart + 5 : 0;
								}
								company = message.substring(companyStart, companyForIndex).trim();

								int roleStart = companyForIndex + " company for ".length();
								int roleEnd = lower.lastIndexOf(" role");
								if (roleEnd > roleStart) {
									targetRole = message.substring(roleStart, roleEnd).trim();
								}
							}
						} else if ((dbCompany == null || dbCompany.isBlank()) && message != null && message.contains(" at ")) {
							int atIndex = message.indexOf(" at ");
							company = message.substring(atIndex + 4).split("\\.")[0];
						} else if ((dbCompany == null || dbCompany.isBlank()) && senderRole != null && !senderRole.isBlank()) {
							company = senderRole;
						}
						
						if (notifType != null && notifType.equalsIgnoreCase("SELECTED")) {
							status = "Offer";
						} else if (notifType != null && notifType.equalsIgnoreCase("SHORTLISTED")) {
							status = "Shortlisted";
						} else if (notifType != null && notifType.equalsIgnoreCase("REJECTED")) {
							status = "Rejected";
						} else if (message != null && message.toLowerCase().contains("selected")) {
							status = "Offer";
						} else if (message != null && message.toLowerCase().contains("shortlisted")) {
							status = "Shortlisted";
						} else if (message != null && message.toLowerCase().contains("rejected")) {
							status = "Rejected";
						} else if (message.contains("Offer")) {
							status = "Offer";
						} else if (message.contains("Application submitted")) {
							status = "Applied";
						}

						if (notifType != null && notifType.equalsIgnoreCase("SHORTLISTED")) {
							message = "congratulation for being shortlisted for next round at " + company + " company for " + targetRole + " role";
						} else if (notifType != null && notifType.equalsIgnoreCase("SELECTED")) {
							message = "congratulations for being selected for " + company + " company for " + targetRole + " role";
						} else if (notifType != null && notifType.equalsIgnoreCase("REJECTED")) {
							message = "you are rejected for " + company + " company for " + targetRole + " role";
						} else if (message == null || message.isBlank()) {
							message = dbMessage;
						}

						notifications.add(new NotificationItem(message, isRead == 1, notifId, company, targetRole, status));
					}
					
					System.out.println("[DEBUG] Total notifications found: " + count);
				} catch (SQLException e) {
					System.err.println("[ERROR] Query failed: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				System.out.println("[DEBUG] Database connection failed!");
			}
		} catch (SQLException ex) {
			System.err.println("[ERROR] Connection error: " + ex.getMessage());
			ex.printStackTrace();
		}

		return notifications;
	}

	private static void markNotificationAsRead(long notifId) {
		if (notifId <= 0) return;
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "UPDATE notification SET is_read = 1 WHERE notif_id = ?";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, notifId);
				int rows = stmt.executeUpdate();
				System.out.println("[DEBUG] Marked notification as read. Rows affected: " + rows);
			}
		} catch (SQLException ex) {
			System.err.println("Error marking notification as read: " + ex.getMessage());
		}
	}
	
	private static void markNotificationAsUnread(long notifId) {
		if (notifId <= 0) return;
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String sql = "UPDATE notification SET is_read = 0 WHERE notif_id = ?";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, notifId);
				int rows = stmt.executeUpdate();
				System.out.println("[DEBUG] Marked notification as unread. Rows affected: " + rows);
			}
		} catch (SQLException ex) {
			System.err.println("Error marking notification as unread: " + ex.getMessage());
		}
	}

	// Method to show notification details in a popup dialog
	private static void showNotificationDetailsDialog(NotificationItem notification, Runnable onCloseCallback) {
		JDialog detailsDialog = new JDialog((JFrame) null, "Notification Details", true);
		detailsDialog.setSize(500, 350);
		detailsDialog.setLayout(new BorderLayout(10, 10));
		detailsDialog.setLocationRelativeTo(null);

		// Header Panel with company name and role
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(41, 84, 209));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		JLabel companyLabel = new JLabel(notification.companyName);
		companyLabel.setFont(new Font("Arial", Font.BOLD, 20));
		companyLabel.setForeground(Color.WHITE);
		companyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel roleLabel = new JLabel(notification.rolePosition);
		roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		roleLabel.setForeground(new Color(230, 240, 255));
		roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		headerPanel.add(companyLabel);
		headerPanel.add(Box.createVerticalStrut(5));
		headerPanel.add(roleLabel);

		// Content Panel with message
		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPanel.setBackground(Color.WHITE);

		JLabel messageLabel = new JLabel("<html><div style='width: 420px;'>" + notification.message + "</div></html>");
		messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		messageLabel.setVerticalAlignment(SwingConstants.TOP);

		// Status badge
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		statusPanel.setBackground(Color.WHITE);
		
		JLabel statusBadge = new JLabel(notification.status);
		statusBadge.setFont(new Font("Arial", Font.BOLD, 12));
		statusBadge.setForeground(Color.WHITE);
		statusBadge.setOpaque(true);
		statusBadge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		
		// Set color based on status
		if (notification.status.equals("Offer")) {
			statusBadge.setBackground(new Color(76, 175, 80));
		} else if (notification.status.equals("Shortlisted")) {
			statusBadge.setBackground(new Color(255, 193, 7));
		} else if (notification.status.equals("Rejected")) {
			statusBadge.setBackground(new Color(220, 53, 69));
		} else if (notification.status.equals("Applied")) {
			statusBadge.setBackground(new Color(0, 123, 255));
		} else {
			statusBadge.setBackground(new Color(108, 117, 125));
		}
		
		statusPanel.add(statusBadge);

		contentPanel.add(messageLabel, BorderLayout.CENTER);
		contentPanel.add(statusPanel, BorderLayout.SOUTH);

		// Footer Panel with close button
		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("Arial", Font.BOLD, 13));
		closeButton.setBackground(new Color(108, 117, 125));
		closeButton.setForeground(Color.WHITE);
		closeButton.setFocusPainted(false);
		closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeButton.setPreferredSize(new Dimension(120, 35));
		
		closeButton.addActionListener(ev -> detailsDialog.dispose());

		footerPanel.add(closeButton);

		// Add window listener to run callback when dialog is closed
		detailsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent windowEvent) {
				if (onCloseCallback != null) {
					onCloseCallback.run();
				}
			}
		});

		detailsDialog.add(headerPanel, BorderLayout.NORTH);
		detailsDialog.add(contentPanel, BorderLayout.CENTER);
		detailsDialog.add(footerPanel, BorderLayout.SOUTH);

		detailsDialog.setVisible(true);
	}

	private static void createNotification(String studentEmail, String companyName, String rolePosition, String status) {
		try (Connection conn = connectToDatabase()) {
			if (conn != null) {
				String getStudentIdSql = "SELECT student_id FROM student WHERE email = ?";
				java.sql.PreparedStatement getStudentStmt = conn.prepareStatement(getStudentIdSql);
				getStudentStmt.setString(1, studentEmail);
				ResultSet studentRs = getStudentStmt.executeQuery();
				if (!studentRs.next()) {
					System.out.println("[DEBUG] Cannot create notification - student not found for email: " + studentEmail);
					return;
				}
				int studentId = studentRs.getInt("student_id");

				String message;
				if ("Shortlisted".equalsIgnoreCase(status)) {
					message = "✓ You are shortlisted for " + rolePosition + " at " + companyName + ".";
				} else if ("Offer".equalsIgnoreCase(status)) {
					message = "🎉 Offer received for " + rolePosition + " at " + companyName + ".";
				} else if ("Applied".equalsIgnoreCase(status)) {
					message = "📝 Application submitted for " + rolePosition + " at " + companyName + ".";
				} else {
					message = status + " - " + rolePosition + " at " + companyName + ".";
				}
				
				System.out.println("[DEBUG] Creating notification");
				System.out.println("[DEBUG] Student Email: " + studentEmail);
				System.out.println("[DEBUG] Message: " + message);
				System.out.println("[DEBUG] Role: " + rolePosition);
				
				String sql = "INSERT INTO notification (sender_role, sender_id, receiver_role, receiver_id, message, notif_type, related_drive_id, notif_date, is_read) " +
						 "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 0)";
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, "STUDENT");
				stmt.setInt(2, studentId);
				stmt.setString(3, "STUDENT");
				stmt.setInt(4, studentId);
				stmt.setString(5, message);
				stmt.setString(6, status == null ? "UPDATE" : status.toUpperCase());
				stmt.setNull(7, java.sql.Types.INTEGER);
				int rows = stmt.executeUpdate();
				System.out.println("[DEBUG] Notification created. Rows affected: " + rows);
			} else {
				System.out.println("[DEBUG] Cannot create notification - no database connection");
			}
		} catch (SQLException ex) {
			System.err.println("[ERROR] Error creating notification: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private static void showNotificationsDialog(JFrame parentFrame, String studentEmail) {
		JDialog notificationDialog = new JDialog(parentFrame, "Notifications", true);
		notificationDialog.setSize(600, 400);
		notificationDialog.setLayout(new BorderLayout(10, 10));
		notificationDialog.setLocationRelativeTo(parentFrame);

		// Header Panel
		JLabel titleLabel = new JLabel("📬 Your Notifications");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		titleLabel.setForeground(new Color(41, 84, 209));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

		// Get all notifications with status
		List<NotificationItem> allNotifications = getStudentNotificationsWithStatus(studentEmail);

		// Separate unread and read notifications
		List<NotificationItem> unreadNotifications = new ArrayList<>();
		List<NotificationItem> readNotifications = new ArrayList<>();

		for (NotificationItem notif : allNotifications) {
			if (notif.isRead) {
				readNotifications.add(notif);
			} else {
				unreadNotifications.add(notif);
			}
		}

		DefaultListModel<NotificationItem> unreadModel = new DefaultListModel<>();
		for (NotificationItem item : unreadNotifications) {
			unreadModel.addElement(item);
		}
		DefaultListModel<NotificationItem> readModel = new DefaultListModel<>();
		for (NotificationItem item : readNotifications) {
			readModel.addElement(item);
		}

		// Create Tab Panel
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));

		JPanel unreadTabPanel = createNotificationTabPanel(unreadModel, true, tabbedPane, unreadModel, readModel);
		JPanel readTabPanel = createNotificationTabPanel(readModel, false, tabbedPane, unreadModel, readModel);
		tabbedPane.addTab("Unread (" + unreadModel.getSize() + ")", unreadTabPanel);
		tabbedPane.addTab("Read (" + readModel.getSize() + ")", readTabPanel);

		// Footer Panel
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

	private static void updateNotificationTabTitles(JTabbedPane tabbedPane,
			DefaultListModel<NotificationItem> unreadModel,
			DefaultListModel<NotificationItem> readModel) {
		tabbedPane.setTitleAt(0, "Unread (" + unreadModel.getSize() + ")");
		tabbedPane.setTitleAt(1, "Read (" + readModel.getSize() + ")");
	}

	private static JPanel createNotificationTabPanel(DefaultListModel<NotificationItem> currentModel,
			boolean isUnread,
			JTabbedPane tabbedPane,
			DefaultListModel<NotificationItem> unreadModel,
			DefaultListModel<NotificationItem> readModel) {
		JPanel tabPanel = new JPanel(new BorderLayout(10, 10));
		tabPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JList<NotificationItem> notificationList = new JList<>(currentModel);
		notificationList.setFont(new Font("Arial", Font.PLAIN, 12));
		notificationList.setFixedCellHeight(60);
		final int[] popupIndexRef = {-1};
		JPopupMenu contextMenu = new JPopupMenu();
		notificationList.addMouseListener(new java.awt.event.MouseAdapter() {
			private void selectRowForPopup(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					int index = notificationList.locationToIndex(e.getPoint());
					if (index >= 0) {
						notificationList.setSelectedIndex(index);
						popupIndexRef[0] = index;
						contextMenu.show(notificationList, e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				selectRowForPopup(e);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				selectRowForPopup(e);
			}
		});

		notificationList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				NotificationItem item = (NotificationItem) value;
				JLabel label = new JLabel();
				label.setText("<html><b>" + item.companyName + "</b><br/><font size='-1'>" + item.rolePosition + "</font><br/><font size='-1'>" + item.message + "</font></html>");
				label.setFont(new Font("Arial", Font.PLAIN, 11));
				label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
				
				if (isSelected) {
					label.setBackground(new Color(41, 84, 209));
					label.setForeground(Color.WHITE);
					label.setOpaque(true);
				} else {
					if (isUnread) {
						label.setBackground(new Color(230, 240, 255));
					} else {
						label.setBackground(Color.WHITE);
						label.setForeground(new Color(80, 80, 80));
					}
					label.setOpaque(true);
				}
				return label;
			}
		});

		// Right-click context menu
		JMenuItem markItem = new JMenuItem(isUnread ? "Mark as Read" : "Mark as Unread");
		markItem.addActionListener(ev -> {
			int selectedIndex = notificationList.getSelectedIndex();
			if (popupIndexRef[0] >= 0 && popupIndexRef[0] < currentModel.getSize()) {
				selectedIndex = popupIndexRef[0];
			}
			if (selectedIndex >= 0) {
				NotificationItem selected = currentModel.getElementAt(selectedIndex);
				
				if (isUnread) {
					markNotificationAsRead(selected.resultId);
					selected.isRead = true;
					currentModel.removeElementAt(selectedIndex);
					readModel.addElement(selected);
					tabbedPane.setSelectedIndex(1);
				} else {
					markNotificationAsUnread(selected.resultId);
					selected.isRead = false;
					currentModel.removeElementAt(selectedIndex);
					unreadModel.addElement(selected);
					tabbedPane.setSelectedIndex(0);
				}

				updateNotificationTabTitles(tabbedPane, unreadModel, readModel);
				notificationList.revalidate();
				notificationList.repaint();
				tabbedPane.revalidate();
				tabbedPane.repaint();
				popupIndexRef[0] = -1;
			} else {
				popupIndexRef[0] = -1;
			}
		});
		contextMenu.add(markItem);


		// Clicking an unread item marks it as read immediately and moves it live.
		notificationList.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 1 && evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
					int index = notificationList.locationToIndex(evt.getPoint());
					if (index >= 0) {
						if (isUnread) {
							NotificationItem selected = currentModel.getElementAt(index);
							markNotificationAsRead(selected.resultId);
							selected.isRead = true;
							currentModel.removeElementAt(index);
							readModel.addElement(selected);
							updateNotificationTabTitles(tabbedPane, unreadModel, readModel);
							tabbedPane.setSelectedIndex(1);
							tabbedPane.revalidate();
							tabbedPane.repaint();
						}
					}
				}
			}
		});

		JScrollPane listScroll = new JScrollPane(notificationList);
		listScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		tabPanel.add(listScroll, BorderLayout.CENTER);
		return tabPanel;
	}

	private static void reloadDashboardAfterApply(JFrame currentDashboardFrame) {
		if (currentDashboardFrame != null) {
			currentDashboardFrame.dispose();
		}
		if (currentStudentName != null && currentStudentRoll != null && currentStudentEmail != null
				&& currentStudentPhone != null && currentStudentSkills != null) {
			showDashboard(currentStudentName, currentStudentRoll, currentStudentEmail, currentStudentPhone, currentStudentSkills, currentLoginFrame);
		}
	}
	
	// Method to display the student dashboard
	public static void showDashboard(String studentName, String studentRoll, String email, String studentPhone, String studentSkills, JFrame loginFrame) {
		currentStudentName = studentName;
		currentStudentRoll = studentRoll;
		currentStudentEmail = email;
		currentStudentPhone = studentPhone;
		currentStudentSkills = studentSkills;
		currentLoginFrame = loginFrame;

		// Create Dashboard Frame
		JFrame dashboardFrame = new JFrame("Student Dashboard - " + studentName);
		dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dashboardFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open dashboard in full screen
		dashboardFrame.setContentPane(createBackgroundPanel(DASHBOARD_BACKGROUND_IMAGE));
		dashboardFrame.getContentPane().setLayout(new BorderLayout());
		
		// Top Panel with Menu and Profile
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(new Color(38, 88, 160));
		topPanel.setPreferredSize(new Dimension(1000, 60));
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		
		// Hamburger Menu Button (Top Left)
		JButton menuButton = new JButton("☰");
		styleDashboardButton(menuButton, new Color(30, 86, 150, 220), new Color(58, 122, 186, 230), 22);
		
		// Profile Button (Top Right)
		JButton profileButton = new JButton("👤 Profile");
		styleDashboardButton(profileButton, new Color(37, 113, 143, 220), new Color(52, 140, 176, 230), 14);

		JButton notificationButton = new JButton("🔔 Notifications");
		styleDashboardButton(notificationButton, new Color(66, 97, 130, 220), new Color(84, 119, 154, 230), 14);

		JPanel rightActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		rightActionsPanel.setOpaque(false);
		rightActionsPanel.add(notificationButton);
		rightActionsPanel.add(profileButton);
		
		topPanel.add(menuButton, BorderLayout.WEST);
		topPanel.add(rightActionsPanel, BorderLayout.EAST);
		
		// Main Content Panel (CardLayout for different views)
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new CardLayout());
		mainContentPanel.setOpaque(false);
		
		// Upcoming Drives Panel
		JPanel upcomingDrivesPanel = new JPanel(new BorderLayout(10, 10));
		upcomingDrivesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		upcomingDrivesPanel.setOpaque(false);
		
		// Header panel with welcome message and title
		JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		headerPanel.setOpaque(false);
		
		JLabel welcomeLabel = new JLabel("Welcome, " + studentName);
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
		welcomeLabel.setForeground(new Color(41, 84, 209));
		
		JLabel upcomingTitle = new JLabel("Upcoming Drives");
		upcomingTitle.setFont(new Font("Arial", Font.BOLD, 16));
		upcomingTitle.setForeground(new Color(100, 100, 100));
		
		headerPanel.add(welcomeLabel);
		headerPanel.add(upcomingTitle);
		
		// Create a scrollable card panel for upcoming drives
		JPanel cardsContainer = new JPanel();
		cardsContainer.setLayout(new GridLayout(0, 4, 10, 10));
		cardsContainer.setOpaque(false);
		cardsContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel cardsWrapper = createCardGridWrapper(cardsContainer);
		
		// Cards are populated by shared refresh logic for search support.
		
		JScrollPane upcomingScroll = new JScrollPane(cardsWrapper);
		upcomingScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		upcomingScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		upcomingScroll.getVerticalScrollBar().setUnitIncrement(5);
		makeScrollPaneTransparent(upcomingScroll);
		
		upcomingDrivesPanel.add(headerPanel, BorderLayout.NORTH);
		upcomingDrivesPanel.add(upcomingScroll, BorderLayout.CENTER);
		
		// Applied Drives Panel
		JPanel appliedDrivesPanel = new JPanel(new BorderLayout(10, 10));
		appliedDrivesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		appliedDrivesPanel.setOpaque(false);
		
		JLabel appliedTitle = new JLabel("Applied Drives");
		appliedTitle.setFont(new Font("Arial", Font.BOLD, 24));
		appliedTitle.setForeground(new Color(40, 167, 69));
		
		// Create a scrollable card panel for applied drives
		JPanel appliedCardsContainer = new JPanel();
		appliedCardsContainer.setLayout(new GridLayout(0, 4, 10, 10));
		appliedCardsContainer.setOpaque(false);
		appliedCardsContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel appliedCardsWrapper = createCardGridWrapper(appliedCardsContainer);
		
		// Cards are populated by shared refresh logic for search support.
		
		JScrollPane appliedScroll = new JScrollPane(appliedCardsWrapper);
		appliedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		appliedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		appliedScroll.getVerticalScrollBar().setUnitIncrement(5);
		makeScrollPaneTransparent(appliedScroll);
		
		appliedDrivesPanel.add(appliedTitle, BorderLayout.NORTH);
		appliedDrivesPanel.add(appliedScroll, BorderLayout.CENTER);
		
		// Shortlisted Panel
		JPanel shortlistedPanel = new JPanel(new BorderLayout(10, 10));
		shortlistedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		shortlistedPanel.setOpaque(false);
		
		JLabel shortlistedTitle = new JLabel("Shortlisted");
		shortlistedTitle.setFont(new Font("Arial", Font.BOLD, 24));
		shortlistedTitle.setForeground(new Color(255, 193, 7));
		
		// Create card panel for shortlisted
		JPanel shortlistedCardsContainer = new JPanel();
		shortlistedCardsContainer.setLayout(new GridLayout(0, 4, 10, 10));
		shortlistedCardsContainer.setOpaque(false);
		shortlistedCardsContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel shortlistedCardsWrapper = createCardGridWrapper(shortlistedCardsContainer);
		
		// Cards are populated by shared refresh logic for search support.
		
		JScrollPane shortlistedScroll = new JScrollPane(shortlistedCardsWrapper);
		shortlistedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		shortlistedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		shortlistedScroll.getVerticalScrollBar().setUnitIncrement(5);
		makeScrollPaneTransparent(shortlistedScroll);
		
		shortlistedPanel.add(shortlistedTitle, BorderLayout.NORTH);
		shortlistedPanel.add(shortlistedScroll, BorderLayout.CENTER);
		
		// Offer Received Panel
		JPanel offerReceivedPanel = new JPanel(new BorderLayout(10, 10));
		offerReceivedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		offerReceivedPanel.setOpaque(false);
		
		JLabel offerTitle = new JLabel("Offers Received");
		offerTitle.setFont(new Font("Arial", Font.BOLD, 24));
		offerTitle.setForeground(new Color(220, 53, 69));
		
		// Create card panel for offers
		JPanel offerCardsContainer = new JPanel();
		offerCardsContainer.setLayout(new GridLayout(0, 4, 10, 10));
		offerCardsContainer.setOpaque(false);
		offerCardsContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel offerCardsWrapper = createCardGridWrapper(offerCardsContainer);
		
		// Cards are populated by shared refresh logic for search support.
		
		JScrollPane offerScroll = new JScrollPane(offerCardsWrapper);
		offerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		offerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		offerScroll.getVerticalScrollBar().setUnitIncrement(5);
		makeScrollPaneTransparent(offerScroll);
		
		offerReceivedPanel.add(offerTitle, BorderLayout.NORTH);
		offerReceivedPanel.add(offerScroll, BorderLayout.CENTER);

		// Rejected Drives Panel
		JPanel rejectedPanel = new JPanel(new BorderLayout(10, 10));
		rejectedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		rejectedPanel.setOpaque(false);

		JLabel rejectedTitle = new JLabel("Rejected Drives");
		rejectedTitle.setFont(new Font("Arial", Font.BOLD, 24));
		rejectedTitle.setForeground(new Color(200, 50, 60));

		JPanel rejectedCardsContainer = new JPanel();
		rejectedCardsContainer.setLayout(new GridLayout(0, 4, 10, 10));
		rejectedCardsContainer.setOpaque(false);
		rejectedCardsContainer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel rejectedCardsWrapper = createCardGridWrapper(rejectedCardsContainer);

		JScrollPane rejectedScroll = new JScrollPane(rejectedCardsWrapper);
		rejectedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		rejectedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rejectedScroll.getVerticalScrollBar().setUnitIncrement(5);
		makeScrollPaneTransparent(rejectedScroll);

		rejectedPanel.add(rejectedTitle, BorderLayout.NORTH);
		rejectedPanel.add(rejectedScroll, BorderLayout.CENTER);
		
		// Add all panels to main content with CardLayout
		mainContentPanel.add(upcomingDrivesPanel, "Upcoming Drives");
		mainContentPanel.add(appliedDrivesPanel, "Applied Drives");
		mainContentPanel.add(shortlistedPanel, "Shortlisted");
		mainContentPanel.add(offerReceivedPanel, "Offer Received");
		mainContentPanel.add(rejectedPanel, "Rejected Drives");

		CardLayout cardLayout = (CardLayout) mainContentPanel.getLayout();
		final Runnable[] refreshDriveCardsRef = new Runnable[1];
		Runnable onApplySuccess = () -> {
			if (refreshDriveCardsRef[0] != null) {
				refreshDriveCardsRef[0].run();
				cardLayout.show(mainContentPanel, "Applied Drives");
			}
		};

		Runnable refreshDriveCards = () -> {
			List<Object[]> upcomingData = getUpcomingDrives();
			List<Object[]> appliedData = getAppliedDrives(email);
			List<Object[]> shortlistedData = getShortlistedDrives(email);
			List<Object[]> offerData = getOffers(email);
			List<Object[]> rejectedData = getRejectedDrives(email);

			cardsContainer.removeAll();
			int upcomingCount = 0;
			for (Object[] row : upcomingData) {
				int driveId = (int) row[0];
				int companyId = (int) row[1];
				String company = (String) row[2];
				String position = (String) row[3];
				String salary = (String) row[4];
				String type = salary.contains("LPA") ? "Full Time" : "Internship";
				String venue = (String) row[6];
				String eligibility = (String) row[5];
				String lastDate = (String) row[7];
				String status = (String) row[8];

				JPanel card = createDriveCard(driveId, companyId, company, position, type, venue, eligibility, salary, lastDate, status, email, dashboardFrame, true, onApplySuccess);
				cardsContainer.add(card);
				upcomingCount++;
			}
			if (upcomingCount == 0) {
				JLabel empty = new JLabel("No upcoming drives");
				empty.setForeground(new Color(45, 75, 95));
				empty.setFont(new Font("Arial", Font.BOLD, 14));
				cardsContainer.add(empty);
			}
			updateCardGridSize(cardsContainer, upcomingCount);

			appliedCardsContainer.removeAll();
			int appliedCount = 0;
			for (Object[] row : appliedData) {
				String company = (String) row[0];
				String position = (String) row[1];
				String type = (String) row[2];
				String salary = "Applied";
				String venue = "-";
				String eligibility = "Status: " + (row.length > 3 ? row[3] : "Applied");
				String lastDate = (String) row[4];
				if (row.length > 5 && row[5] != null) {
					venue = (String) row[5];
				}
				if (row.length > 6 && row[6] != null) {
					eligibility = (String) row[6];
				}

				JPanel card = createDriveCard(company, position, type, venue, eligibility, salary, lastDate, "Pending");
				appliedCardsContainer.add(card);
				appliedCount++;
			}
			if (appliedCount == 0) {
				JLabel empty = new JLabel("No applied drives");
				empty.setForeground(new Color(45, 75, 95));
				empty.setFont(new Font("Arial", Font.BOLD, 14));
				appliedCardsContainer.add(empty);
			}
			updateCardGridSize(appliedCardsContainer, appliedCount);

			shortlistedCardsContainer.removeAll();
			int shortlistedCount = 0;
			for (Object[] row : shortlistedData) {
				String company = (String) row[0];
				String position = (String) row[1];
				String type = (String) row[2];
				String venue = "Interview: " + (String) row[4];
				String eligibility = "Date: " + (String) row[3];
				String salary = type.equals("Internship") ? "₹12K+/Month" : "Competitive";
				String lastDate = (String) row[6];
				if (row.length > 7 && row[7] != null) {
					venue = (String) row[7];
				}
				if (row.length > 8 && row[8] != null) {
					eligibility = (String) row[8];
				}

				JPanel card = createDriveCard(company, position, type, venue, eligibility, salary, lastDate, "Shortlisted");
				shortlistedCardsContainer.add(card);
				shortlistedCount++;
			}
			if (shortlistedCount == 0) {
				JLabel empty = new JLabel("No shortlisted drives");
				empty.setForeground(new Color(45, 75, 95));
				empty.setFont(new Font("Arial", Font.BOLD, 14));
				shortlistedCardsContainer.add(empty);
			}
			updateCardGridSize(shortlistedCardsContainer, shortlistedCount);

			offerCardsContainer.removeAll();
			int offerCount = 0;
			for (Object[] row : offerData) {
				String company = (String) row[0];
				String position = (String) row[1];
				String type = (String) row[2];
				String salary = (String) row[3];
				String venue = "Joining: " + (String) row[4];
				String eligibility = "Status: " + (String) row[5];
				String lastDate = (String) row[6];
				if (row.length > 7 && row[7] != null) {
					venue = (String) row[7];
				}
				if (row.length > 8 && row[8] != null) {
					eligibility = (String) row[8];
				}

				JPanel card = createDriveCard(company, position, type, venue, eligibility, salary, lastDate, "Offer");
				offerCardsContainer.add(card);
				offerCount++;
			}
			if (offerCount == 0) {
				JLabel empty = new JLabel("No offers");
				empty.setForeground(new Color(45, 75, 95));
				empty.setFont(new Font("Arial", Font.BOLD, 14));
				offerCardsContainer.add(empty);
			}
			updateCardGridSize(offerCardsContainer, offerCount);

			rejectedCardsContainer.removeAll();
			int rejectedCount = 0;
			for (Object[] row : rejectedData) {
				String company = (String) row[0];
				String position = (String) row[1];
				String type = (String) row[2];
				String salary = "Rejected";
				String venue = "-";
				String eligibility = "Status: " + (row.length > 3 ? row[3] : "Rejected");
				String lastDate = (String) row[4];
				if (row.length > 5 && row[5] != null) {
					venue = (String) row[5];
				}
				if (row.length > 6 && row[6] != null) {
					eligibility = (String) row[6];
				}

				JPanel card = createDriveCard(company, position, type, venue, eligibility, salary, lastDate, "Rejected");
				rejectedCardsContainer.add(card);
				rejectedCount++;
			}
			if (rejectedCount == 0) {
				JLabel empty = new JLabel("No rejected drives");
				empty.setForeground(new Color(45, 75, 95));
				empty.setFont(new Font("Arial", Font.BOLD, 14));
				rejectedCardsContainer.add(empty);
			}
			updateCardGridSize(rejectedCardsContainer, rejectedCount);

			cardsContainer.revalidate();
			cardsContainer.repaint();
			appliedCardsContainer.revalidate();
			appliedCardsContainer.repaint();
			shortlistedCardsContainer.revalidate();
			shortlistedCardsContainer.repaint();
			offerCardsContainer.revalidate();
			offerCardsContainer.repaint();
			rejectedCardsContainer.revalidate();
			rejectedCardsContainer.repaint();
		};
		refreshDriveCardsRef[0] = refreshDriveCards;

		refreshDriveCards.run();
		
		// Menu Button Action - Show Popup Menu
		menuButton.addActionListener(menuEv -> {
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.setBorder(BorderFactory.createLineBorder(new Color(41, 84, 209), 2));
			popupMenu.setBackground(new Color(232, 242, 252));
			
			JMenuItem upcomingItem = new JMenuItem("Upcoming Drives");
			styleDashboardMenuItem(upcomingItem);
			upcomingItem.addActionListener(menuE -> cardLayout.show(mainContentPanel, "Upcoming Drives"));
			
			JMenuItem appliedItem = new JMenuItem("Applied Drives");
			styleDashboardMenuItem(appliedItem);
			appliedItem.addActionListener(menuE -> cardLayout.show(mainContentPanel, "Applied Drives"));
			
			JMenuItem shortlistedItem = new JMenuItem("Shortlisted");
			styleDashboardMenuItem(shortlistedItem);
			shortlistedItem.addActionListener(menuE -> cardLayout.show(mainContentPanel, "Shortlisted"));
			
			JMenuItem offerItem = new JMenuItem("Offer Received");
			styleDashboardMenuItem(offerItem);
			offerItem.addActionListener(menuE -> cardLayout.show(mainContentPanel, "Offer Received"));

			JMenuItem rejectedItem = new JMenuItem("Rejected Drives");
			styleDashboardMenuItem(rejectedItem);
			rejectedItem.addActionListener(menuE -> cardLayout.show(mainContentPanel, "Rejected Drives"));
			
			popupMenu.add(upcomingItem);
			popupMenu.addSeparator();
			popupMenu.add(appliedItem);
			popupMenu.addSeparator();
			popupMenu.add(shortlistedItem);
			popupMenu.addSeparator();
			popupMenu.add(rejectedItem);
			popupMenu.addSeparator();
			popupMenu.add(offerItem);
			
			popupMenu.show(menuButton, 0, menuButton.getHeight());
		});
		
		// Profile Button Action
		profileButton.addActionListener(profEv -> {
			showProfileDialog(dashboardFrame, loginFrame, studentName, studentRoll, email, studentPhone, studentSkills);
		});

		notificationButton.addActionListener(notifyEv -> {
			showNotificationsDialog(dashboardFrame, email);
		});
		
		// Add components to dashboard
		JPanel centerArea = new JPanel(new BorderLayout());
		centerArea.setOpaque(false);
		centerArea.add(mainContentPanel, BorderLayout.CENTER);

		dashboardFrame.add(topPanel, BorderLayout.NORTH);
		dashboardFrame.add(centerArea, BorderLayout.CENTER);
		
		dashboardFrame.setLocationRelativeTo(null);
		dashboardFrame.setVisible(true);
	}
	
	// Profile Dialog method
	private static void showProfileDialog(JFrame dashboardFrame, JFrame loginFrame, String studentName, String studentRoll, String email, String studentPhone, String studentSkills) {
		JFrame profileFrame = new JFrame("Profile Information");
		profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		profileFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open profile window in full screen
		profileFrame.setContentPane(createBackgroundPanel(DASHBOARD_BACKGROUND_IMAGE));
		profileFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Top Panel with Edit Profile button
		JPanel topProfilePanel = new JPanel(new BorderLayout());
		topProfilePanel.setOpaque(true);
		topProfilePanel.setBackground(new Color(255, 255, 255, 200));
		topProfilePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
		
		JButton editProfileButton = new JButton("Edit Profile");
		styleDashboardButton(editProfileButton, new Color(37, 113, 143, 220), new Color(52, 140, 176, 230), 14);
		
		topProfilePanel.add(editProfileButton, BorderLayout.EAST);
		
		// Information Panel (top section)
		JPanel profilePanel = new JPanel(new GridLayout(4, 2, 12, 14));
		profilePanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));
		profilePanel.setOpaque(false);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JTextField profileNameField = new JTextField(studentName);
		profileNameField.setFont(new Font("Arial", Font.PLAIN, 20));
		profileNameField.setEditable(false);
		profileNameField.setOpaque(true);
		profileNameField.setBackground(Color.WHITE);
		profileNameField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(184, 198, 212)),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		
		JLabel rollLabel = new JLabel("Roll No:");
		rollLabel.setFont(new Font("Arial", Font.BOLD, 20));
		rollLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rollLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JTextField profileRollField = new JTextField(studentRoll);
		profileRollField.setFont(new Font("Arial", Font.PLAIN, 20));
		profileRollField.setEditable(false);
		profileRollField.setOpaque(true);
		profileRollField.setBackground(new Color(243, 247, 251));
		profileRollField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(205, 216, 227)),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		
		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setFont(new Font("Arial", Font.BOLD, 20));
		emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
		emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JTextField profileEmailField = new JTextField(email);
		profileEmailField.setFont(new Font("Arial", Font.PLAIN, 20));
		profileEmailField.setEditable(false);
		profileEmailField.setOpaque(true);
		profileEmailField.setBackground(new Color(243, 247, 251));
		profileEmailField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(205, 216, 227)),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		
		JLabel phoneLabel = new JLabel("Phone:");
		phoneLabel.setFont(new Font("Arial", Font.BOLD, 20));
		phoneLabel.setHorizontalAlignment(SwingConstants.CENTER);
		phoneLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JTextField profilePhoneField = new JTextField(studentPhone);
		profilePhoneField.setFont(new Font("Arial", Font.PLAIN, 20));
		profilePhoneField.setEditable(false);
		profilePhoneField.setOpaque(true);
		profilePhoneField.setBackground(Color.WHITE);
		profilePhoneField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(184, 198, 212)),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		
		profilePanel.add(nameLabel);
		profilePanel.add(profileNameField);
		profilePanel.add(rollLabel);
		profilePanel.add(profileRollField);
		profilePanel.add(emailLabel);
		profilePanel.add(profileEmailField);
		profilePanel.add(phoneLabel);
		profilePanel.add(profilePhoneField);
		
		// Bottom Section Panel (Left: Buttons, Right: Skills)
		JPanel bottomSectionPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		bottomSectionPanel.setOpaque(false);
		bottomSectionPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 40));
		
		// Left Panel with action buttons
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		
		// How to Use Manual Button
		JButton manualButton = new JButton("How to Use");
		manualButton.setPreferredSize(new Dimension(250, 46));
		manualButton.setMaximumSize(new Dimension(260, 46));
		styleDashboardButton(manualButton, new Color(33, 100, 172, 220), new Color(53, 124, 198, 230), 14);
		manualButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		manualButton.addActionListener(manualEv -> {
			JOptionPane.showMessageDialog(profileFrame,
				"How to Use:\n\n" +
				"1. Use the menu (☰) to navigate between sections\n" +
				"2. Click on 'Upcoming Drives' to view available jobs\n" +
				"3. Click on 'Applied Drives' to see your applications\n" +
				"4. Click on 'Shortlisted' to view shortlisted positions\n" +
				"5. Click on 'Rejected Drives' to view rejected applications\n" +
				"6. Click on 'Offer Received' to see your job offers\n" +
				"7. Click 'Profile' anytime to view your information",
				"User Manual",
				JOptionPane.INFORMATION_MESSAGE);
		});
		
		// Delete Account Button
		JButton deleteButton = new JButton("Delete Account");
		deleteButton.setPreferredSize(new Dimension(250, 46));
		deleteButton.setMaximumSize(new Dimension(260, 46));
		styleDashboardButton(deleteButton, new Color(41, 110, 186, 220), new Color(62, 136, 210, 230), 14);
		deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Feedback Button
		JButton feedbackButton = new JButton("Feedback");
		feedbackButton.setPreferredSize(new Dimension(250, 46));
		feedbackButton.setMaximumSize(new Dimension(260, 46));
		styleDashboardButton(feedbackButton, new Color(33, 100, 172, 220), new Color(53, 124, 198, 230), 14);
		feedbackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		feedbackButton.addActionListener(feedbackEv -> {
			JTextArea feedbackArea = new JTextArea(6, 28);
			feedbackArea.setLineWrap(true);
			feedbackArea.setWrapStyleWord(true);
			feedbackArea.setFont(new Font("Arial", Font.PLAIN, 14));

			JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
			feedbackScroll.setPreferredSize(new Dimension(380, 140));

			int option = JOptionPane.showConfirmDialog(
					profileFrame,
					feedbackScroll,
					"Share Your Feedback",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE
			);

			if (option == JOptionPane.OK_OPTION) {
				String message = feedbackArea.getText().trim();
				if (message.isEmpty()) {
					JOptionPane.showMessageDialog(profileFrame, "Feedback cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (message.length() > 300) {
					JOptionPane.showMessageDialog(profileFrame, "Feedback must be 300 characters or less.", "Validation", JOptionPane.WARNING_MESSAGE);
					return;
				}

				boolean submitted = submitStudentFeedback(profileEmailField.getText().trim(), message);
				if (submitted) {
					JOptionPane.showMessageDialog(profileFrame, "Feedback submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(profileFrame, "Failed to submit feedback.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteButton.addActionListener(deleteEv -> {
			int confirm = JOptionPane.showConfirmDialog(profileFrame,
				"Are you sure you want to delete your account?\nThis action cannot be undone!",
				"Confirm Delete",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION) {
				try (Connection deleteConn = connectToDatabase()) {
					if (deleteConn != null) {
						// Start transaction
						deleteConn.setAutoCommit(false);
						
						try {
							String currentEmail = profileEmailField.getText().trim();
							// First, get student_id from email
							String getIdSql = "SELECT student_id FROM student WHERE email = ?";
							java.sql.PreparedStatement getIdStmt = deleteConn.prepareStatement(getIdSql);
							getIdStmt.setString(1, currentEmail);
							ResultSet rs = getIdStmt.executeQuery();
							
							if (rs.next()) {
								int studentId = rs.getInt("student_id");
								
								// Step 1: Delete all notifications for this student
								String deleteNotifSql = "DELETE FROM notification WHERE receiver_role = 'STUDENT' AND receiver_id = ?";
								java.sql.PreparedStatement deleteNotifStmt = deleteConn.prepareStatement(deleteNotifSql);
								deleteNotifStmt.setInt(1, studentId);
								deleteNotifStmt.executeUpdate();
								
								// Step 2: Delete from result table (using student_id)
								String deleteResultSql = "DELETE FROM result WHERE student_id = ?";
								java.sql.PreparedStatement deleteResultStmt = deleteConn.prepareStatement(deleteResultSql);
								deleteResultStmt.setInt(1, studentId);
								deleteResultStmt.executeUpdate();
								
								// Step 3: Finally delete from student table
								String deleteStudentSql = "DELETE FROM student WHERE email = ?";
								java.sql.PreparedStatement deleteStudentStmt = deleteConn.prepareStatement(deleteStudentSql);
								deleteStudentStmt.setString(1, currentEmail);
								int rows = deleteStudentStmt.executeUpdate();
								
								// Commit transaction
								deleteConn.commit();
								
								if (rows > 0) {
									JOptionPane.showMessageDialog(profileFrame,
										"Account deleted successfully!",
										"Success",
										JOptionPane.INFORMATION_MESSAGE);
									profileFrame.dispose();
									dashboardFrame.dispose();
									if (loginFrame != null) {
										loginFrame.setVisible(true);
									}
								} else {
									deleteConn.rollback();
									JOptionPane.showMessageDialog(profileFrame,
										"Failed to delete account!",
										"Error",
										JOptionPane.ERROR_MESSAGE);
								}
							} else {
								deleteConn.rollback();
								JOptionPane.showMessageDialog(profileFrame,
									"Student not found!",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							}
						} catch (Exception ex) {
							// Rollback on error
							deleteConn.rollback();
							throw ex;
						} finally {
							deleteConn.setAutoCommit(true);
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(profileFrame,
						"Error: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Logout Button
		JButton logoutButton = new JButton("Logout");
		logoutButton.setPreferredSize(new Dimension(250, 46));
		logoutButton.setMaximumSize(new Dimension(260, 46));
		styleDashboardButton(logoutButton, new Color(24, 90, 162, 220), new Color(44, 114, 188, 230), 14);
		logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		logoutButton.addActionListener(logoutEv -> {
			int confirm = JOptionPane.showConfirmDialog(profileFrame,
				"Are you sure you want to logout?",
				"Confirm Logout",
				JOptionPane.YES_NO_OPTION);
			
			if (confirm == JOptionPane.YES_OPTION) {
				profileFrame.dispose();
				dashboardFrame.dispose();
				if (loginFrame != null) {
					loginFrame.setVisible(true);
				}
			}
		});
		
		bottomPanel.add(Box.createVerticalStrut(8));
		bottomPanel.add(manualButton);
		bottomPanel.add(Box.createVerticalStrut(18));
		bottomPanel.add(feedbackButton);
		bottomPanel.add(Box.createVerticalStrut(18));
		bottomPanel.add(deleteButton);
		bottomPanel.add(Box.createVerticalStrut(18));
		bottomPanel.add(logoutButton);

		JPanel leftColumnPanel = new JPanel(new BorderLayout());
		leftColumnPanel.setOpaque(false);
		leftColumnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		leftColumnPanel.add(bottomPanel, BorderLayout.NORTH);
		
		// Right Panel with Skills
		JPanel skillsPanel = new JPanel(new BorderLayout(5, 10));
		skillsPanel.setOpaque(false);
		skillsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		JLabel skillsHeading = new JLabel("SKILLS");
		skillsHeading.setFont(new Font("Arial", Font.BOLD, 22));
		skillsHeading.setForeground(new Color(41, 84, 209));
		
		String initialSkills = formatSkillsForDisplay(studentSkills);
		JTextArea skillsText = new JTextArea(initialSkills);
		skillsText.setFont(new Font("Arial", Font.PLAIN, 18));
		skillsText.setLineWrap(true);
		skillsText.setWrapStyleWord(true);
		skillsText.setEditable(false);
		skillsText.setOpaque(true);
		skillsText.setForeground(new Color(24, 36, 54));
		skillsText.setBackground(Color.WHITE);
		skillsText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(184, 198, 212)),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		// Edit Profile Button Action
		editProfileButton.addActionListener(editEv -> {
			if (editProfileButton.getText().equals("Edit Profile")) {
				// Enable editing
				profileNameField.setEditable(true);
				profileEmailField.setEditable(true);
				profilePhoneField.setEditable(true);
				skillsText.setEditable(true);
				profileNameField.setBackground(new Color(236, 246, 255));
				profileEmailField.setBackground(new Color(236, 246, 255));
				profilePhoneField.setBackground(new Color(236, 246, 255));
				skillsText.setBackground(new Color(236, 246, 255));
				editProfileButton.setText("Save");
				editProfileButton.setBackground(new Color(46, 139, 87));
			} else {
				// Save changes
				String newName = profileNameField.getText().trim();
				String newEmail = profileEmailField.getText().trim();
				String newPhone = profilePhoneField.getText().trim();
				String newSkills = skillsText.getText().trim();
				
				if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || newSkills.isEmpty()) {
					JOptionPane.showMessageDialog(profileFrame,
						"Name, Email, Phone and Skills cannot be empty!",
						"Error",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				String normalizedSkillsForDb = normalizeSkillsForStorage(newSkills);
				
				try (Connection updateConn = connectToDatabase()) {
					if (updateConn != null) {
						String updateSql = "UPDATE student SET name = ?, email = ?, phone = ?, skills = ? WHERE roll_no = ?";
						java.sql.PreparedStatement updateStmt = updateConn.prepareStatement(updateSql);
						updateStmt.setString(1, newName);
						updateStmt.setString(2, newEmail);
						updateStmt.setString(3, newPhone);
						updateStmt.setString(4, normalizedSkillsForDb);
						updateStmt.setString(5, studentRoll);
						int rows = updateStmt.executeUpdate();
						
						if (rows > 0) {
							JOptionPane.showMessageDialog(profileFrame,
								"Profile updated successfully!",
								"Success",
								JOptionPane.INFORMATION_MESSAGE);

							currentStudentName = newName;
							currentStudentEmail = newEmail;
							currentStudentPhone = newPhone;
							currentStudentSkills = normalizedSkillsForDb;
							skillsText.setText(formatSkillsForDisplay(normalizedSkillsForDb));
							
							// Disable editing
							profileNameField.setEditable(false);
							profileEmailField.setEditable(false);
							profilePhoneField.setEditable(false);
							skillsText.setEditable(false);
							profileNameField.setBackground(Color.WHITE);
							profileEmailField.setBackground(new Color(243, 247, 251));
							profilePhoneField.setBackground(Color.WHITE);
							skillsText.setBackground(Color.WHITE);
							editProfileButton.setText("Edit Profile");
							editProfileButton.setBackground(new Color(32, 122, 160));
						} else {
							JOptionPane.showMessageDialog(profileFrame,
								"Failed to update profile!",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(profileFrame,
						"Error: " + ex.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		skillsPanel.add(skillsHeading, BorderLayout.NORTH);
		skillsPanel.add(skillsText, BorderLayout.CENTER);

		// Add both panels to bottom section
		bottomSectionPanel.add(leftColumnPanel);
		bottomSectionPanel.add(skillsPanel);

		JPanel profileContentPanel = new JPanel(new BorderLayout());
		profileContentPanel.setOpaque(true);
		profileContentPanel.setBackground(new Color(255, 255, 255, 200));
		profileContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 20, 40));

		JPanel topDetailsWrapper = new JPanel(new BorderLayout());
		topDetailsWrapper.setOpaque(false);
		topDetailsWrapper.add(profilePanel, BorderLayout.NORTH);
		topDetailsWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(20, 36, 58)));

		JPanel bottomWrapper = new JPanel(new BorderLayout());
		bottomWrapper.setOpaque(false);
		bottomWrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		bottomWrapper.add(bottomSectionPanel, BorderLayout.CENTER);

		profileContentPanel.add(topDetailsWrapper, BorderLayout.NORTH);
		profileContentPanel.add(bottomWrapper, BorderLayout.CENTER);

		profileFrame.add(topProfilePanel, BorderLayout.NORTH);
		profileFrame.add(profileContentPanel, BorderLayout.CENTER);
		profileFrame.setLocationRelativeTo(dashboardFrame);
		profileFrame.setVisible(true);
	}
}
		
