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
import java.net.http.HttpServer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton googleLoginButton;
    private FirebaseAuth firebaseAuth;
    private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public Login() {
        initializeFirebase();
        initializeUI();
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

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Room Planner Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);  // Increased height for Google button
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
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 10));

        // Login and Register buttons panel
        JPanel loginRegisterPanel = new JPanel();
        loginRegisterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 35));

        loginRegisterPanel.add(loginButton);
        loginRegisterPanel.add(registerButton);

        // Google login button
        googleLoginButton = new JButton("Sign in with Google");
        googleLoginButton.setPreferredSize(new Dimension(220, 35));
        googleLoginButton.setBackground(new Color(66, 133, 244));
        googleLoginButton.setForeground(Color.WHITE);
        googleLoginButton.setFocusPainted(false);

        buttonPanel.add(loginRegisterPanel);
        buttonPanel.add(googleLoginButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(buttonPanel, gbc);

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

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email and password cannot be empty",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters long",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void handleFirebaseLogin() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!validateInput(email, password)) {
            return;
        }

        try {
            // Create a custom token for the user
            String customToken = firebaseAuth.createCustomToken(email);
            
            // In a production environment, you would exchange this token for an ID token
            // For this example, we'll simulate successful authentication if we can create the token
            if (customToken != null) {
                JOptionPane.showMessageDialog(this, 
                    "Welcome back, " + email + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame();
                    frame.setTitle("Room Planner - " + email);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.add(new RoomPlan2());
                    frame.setVisible(true);
                });
            }
        } catch (FirebaseAuthException e) {
            String errorMessage = "Login failed: ";
            if (e.getErrorCode().equals("user-not-found")) {
                errorMessage += "No account found with this email";
            } else if (e.getErrorCode().equals("invalid-password")) {
                errorMessage += "Incorrect password";
            } else {
                errorMessage += e.getMessage();
            }
            JOptionPane.showMessageDialog(this,
                errorMessage,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFirebaseRegister() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!validateInput(email, password)) {
            return;
        }

        try {
            // Check if user already exists
            try {
                UserRecord existingUser = firebaseAuth.getUserByEmail(email);
                JOptionPane.showMessageDialog(this,
                    "An account with this email already exists",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            } catch (FirebaseAuthException e) {
                // User doesn't exist, proceed with registration
                if (!e.getErrorCode().equals("user-not-found")) {
                    throw e; // Rethrow if it's not a user-not-found error
                }
            }

            // Create user with email/password
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false)
                .setDisplayName(email.split("@")[0]); // Use part before @ as display name

            UserRecord userRecord = firebaseAuth.createUser(request);
            
            // Send email verification
            try {
                ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                    .setUrl("http://localhost:8080/verify")
                    .build();
                String emailLink = firebaseAuth.generateEmailVerificationLink(email, actionCodeSettings);
                
                // In a real application, you would send this link via email
                // For this example, we'll show it in a dialog
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please verify your email.\n" +
                    "Verification link (in production, this would be emailed):\n" + emailLink,
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (FirebaseAuthException e) {
                // If email verification fails, still allow registration but inform the user
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Email verification could not be sent.\n" +
                    "You can still login, but some features may be limited.",
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Clear the password field after successful registration
            passwordField.setText("");
            
        } catch (FirebaseAuthException e) {
            String errorMessage = "Registration failed: ";
            if (e.getErrorCode().equals("email-already-exists")) {
                errorMessage += "An account with this email already exists";
            } else if (e.getErrorCode().equals("invalid-password")) {
                errorMessage += "Password is too weak";
            } else {
                errorMessage += e.getMessage();
            }
            JOptionPane.showMessageDialog(this,
                errorMessage,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleGoogleLogin() {
        try {
            // Create a desktop browser window for Google Sign-In
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                // Generate OAuth URL
                String clientId = "YOUR_CLIENT_ID"; // Replace with your Google OAuth client ID
                String redirectUri = "http://localhost:8080/callback";
                String scope = "email profile";
                
                String authUrl = String.format(
                    "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s" +
                    "&response_type=code&scope=%s",
                    clientId,
                    redirectUri,
                    scope
                );
                
                // Open the default browser
                desktop.browse(new URI(authUrl));
                
                // Start a local server to handle the OAuth callback
                startOAuthCallbackServer();
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "Desktop browser not supported on this platform.",
                    "Google Sign-In Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during Google Sign-In: " + e.getMessage(),
                "Google Sign-In Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startOAuthCallbackServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/callback", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String code = null;
                if (query != null) {
                    for (String param : query.split("&")) {
                        String[] pair = param.split("=");
                        if (pair.length == 2 && pair[0].equals("code")) {
                            code = pair[1];
                            break;
                        }
                    }
                }

                if (code != null) {
                    // Exchange code for tokens (in production)
                    String response = "Authentication successful! You can close this window.";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    
                    // In production, you would:
                    // 1. Exchange the code for tokens
                    // 2. Verify the tokens
                    // 3. Create or update the user in Firebase
                    // 4. Sign in the user
                    
                    SwingUtilities.invokeLater(() -> {
                        this.dispose();
                        JFrame frame = new JFrame();
                        frame.setTitle("Room Planner");
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        frame.add(new RoomPlan2());
                        frame.setVisible(true);
                    });
                } else {
                    String response = "Authentication failed!";
                    exchange.sendResponseHeaders(400, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
                
                // Stop the server after handling the callback
                server.stop(0);
            });
            
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error starting OAuth callback server: " + e.getMessage(),
                "Google Sign-In Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
}