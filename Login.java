import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton googleLoginButton;
    private FirebaseAuth firebaseAuth;
    private GoogleAuthConfig googleAuthConfig;
    private JPanel loginPanel;

    public Login() {
        initializeFirebase();
        initializeGoogleAuth();
        initializeUI();
        
        // Set application icon for window and dock/taskbar
        try {
            ImageIcon originalIcon = new ImageIcon("logo/logo.png");
            Image scaledImage = getScaledImage(originalIcon.getImage());
            ImageIcon icon = new ImageIcon(scaledImage);
            
            setIconImage(scaledImage);
            // Set dock icon if supported
            if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
                Taskbar.getTaskbar().setIconImage(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("config/key.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseAuth = FirebaseAuth.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error initializing Firebase: " + e.getMessage(),
                    "Firebase Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGoogleAuth() {
        googleAuthConfig = new GoogleAuthConfig();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            // Add Google Login Button with proper styling
            googleLoginButton = new JButton("Sign in with Google");
            googleLoginButton.setPreferredSize(new Dimension(200, 35));
            googleLoginButton.setBackground(new Color(66, 133, 244));
            googleLoginButton.setForeground(Color.WHITE);
            googleLoginButton.setFocusPainted(false);
            googleLoginButton.setBorderPainted(false);
            googleLoginButton.setFont(new Font("Arial", Font.BOLD, 14));

            JPanel googleButtonPanel = new JPanel();
            googleButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            googleButtonPanel.add(googleLoginButton);

            loginPanel = new JPanel();
            loginPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 5, 5, 5);
            loginPanel.add(googleButtonPanel, gbc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Room Planner Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with some padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Room Planner Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username/Email
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Email:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(buttonPanel, gbc);

        // Add Google Login Button
        googleLoginButton = new JButton("Sign in with Google");
        googleLoginButton.setPreferredSize(new Dimension(200, 35));
        googleLoginButton.setBackground(new Color(66, 133, 244));
        googleLoginButton.setForeground(Color.WHITE);
        googleLoginButton.setFocusPainted(false);
        googleLoginButton.setBorderPainted(false);
        googleLoginButton.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel googleButtonPanel = new JPanel();
        googleButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        googleButtonPanel.add(googleLoginButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        loginPanel.add(googleButtonPanel, gbc);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(e -> handleFirebaseLogin());
        registerButton.addActionListener(e -> handleFirebaseRegister());
        googleLoginButton.addActionListener(e -> handleGoogleLogin());

        // Add key listener for Enter key
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleFirebaseLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }

    private Image getScaledImage(Image srcImg) {
        // Scale to 128x128 pixels for better visibility in dock
        return srcImg.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
    }

    private void handleFirebaseLogin() {
        String email = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Check for admin credentials
        if (email.equals("admin") && password.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame();
                frame.setTitle("Room Planner - Admin");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                
                // Set application icon for window and dock/taskbar
                try {
                    ImageIcon originalIcon = new ImageIcon("logo/logo.png");
                    Image scaledImage = getScaledImage(originalIcon.getImage());
                    ImageIcon icon = new ImageIcon(scaledImage);
                    
                    frame.setIconImage(scaledImage);
                    // Set dock icon if supported
                    if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        Taskbar.getTaskbar().setIconImage(scaledImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                frame.add(new RoomPlan2());
                frame.setVisible(true);
            });
            return;
        }

        // Regular user authentication with Firebase
        try {
            UserRecord userRecord = firebaseAuth.getUserByEmail(email);
            // In a real application, you would verify the password with Firebase
            // For this example, we'll simulate successful authentication
            JOptionPane.showMessageDialog(this, "Login successful!");
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame();
                frame.setTitle("Room Planner");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                
                // Set application icon for window and dock/taskbar
                try {
                    ImageIcon originalIcon = new ImageIcon("logo/logo.png");
                    Image scaledImage = getScaledImage(originalIcon.getImage());
                    ImageIcon icon = new ImageIcon(scaledImage);
                    
                    frame.setIconImage(scaledImage);
                    // Set dock icon if supported
                    if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        Taskbar.getTaskbar().setIconImage(scaledImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                frame.add(new RoomPlan2());
                frame.setVisible(true);
            });
        } catch (FirebaseAuthException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email or password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleFirebaseRegister() {
        String email = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false);

            UserRecord userRecord = firebaseAuth.createUser(request);
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FirebaseAuthException e) {
            JOptionPane.showMessageDialog(this,
                    "Registration failed: " + e.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleGoogleLogin() {
        try {
            String userId = "user-" + System.currentTimeMillis(); // Generate a unique user ID
            String authUrl = googleAuthConfig.getAuthorizationUrl(userId);

            // Open the default browser with the authorization URL
            Desktop.getDesktop().browse(new URI(authUrl));

            // Get the credentials after authorization
            new Thread(() -> {
                try {
                    var credential = googleAuthConfig.getCredentials(userId);
                    if (credential != null && credential.getAccessToken() != null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Google login successful!");
                            this.dispose();

                            SwingUtilities.invokeLater(() -> {
                                JFrame frame = new JFrame();
                                frame.setTitle("Room Planner");
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                                
                                // Set application icon for window and dock/taskbar
                                try {
                                    ImageIcon originalIcon = new ImageIcon("logo/logo.png");
                                    Image scaledImage = getScaledImage(originalIcon.getImage());
                                    ImageIcon icon = new ImageIcon(scaledImage);
                                    
                                    frame.setIconImage(scaledImage);
                                    // Set dock icon if supported
                                    if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
                                        Taskbar.getTaskbar().setIconImage(scaledImage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                
                                frame.add(new RoomPlan2());
                                frame.setVisible(true);
                            });
                        });
                    }
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Google login failed: " + ex.getMessage(),
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize Google login: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}
