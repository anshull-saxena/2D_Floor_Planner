
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp extends JFrame {
    
    public MainApp() {
        // Set the title of the window
        setTitle("Swing Desktop App");

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set layout manager for the main frame
        setLayout(new BorderLayout());

        // Create components
        JLabel label = new JLabel("Welcome to the Desktop App");
        JButton button = new JButton("Click Me");
        JTextField textField = new JTextField(20);

        // Add an action listener to the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Button Clicked!");
            }
        });

        // Create a panel to hold components
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(textField);
        panel.add(button);

        // Add panel to the frame
        add(panel, BorderLayout.CENTER);

        // Set the size of the window
        setSize(400, 200);

        // Make the window visible
        setVisible(true);
    }

    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
    }
}
