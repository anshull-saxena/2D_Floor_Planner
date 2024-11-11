import java.awt.*;
import javax.swing.*;

public class class1 {
    public static void main(String[] args) {
        // Create a JFrame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the frame to full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);  // Optional: to remove window border and title bar
        frame.setVisible(true);

        // Create a JPanel for the bottom section (Green panel)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.gray);  // Set panel color to green
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Use FlowLayout for buttons
        bottomPanel.setBounds(0, frame.getHeight() - 200, frame.getWidth(), 200); // Position and size

        // Create buttons for the bottom panel
        JButton button1 = new JButton("Room Type");
        JButton button2 = new JButton("Set Dimensions");
        JButton button3 = new JButton("Set Position");
        ImageIcon b1=new ImageIcon("roomtype.png");
        ImageIcon b2=new ImageIcon("dim.png");
        ImageIcon b3=new ImageIcon("pos.png");
        button1.setIcon(b1);
        button2.setIcon(b2);
        button3.setIcon(b3);

        // Add buttons to the bottom panel
        bottomPanel.add(button1);
        bottomPanel.add(button2);
        bottomPanel.add(button3);

        // Create a JPanel for the left side panel (Blue panel)
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(Color.BLUE);  // Set side panel color to blue
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));  // BoxLayout for vertical buttons
        sidePanel.setBounds(0, 0, 200, frame.getHeight()); // Position and size for side panel

        // Create buttons for the side panel
        JButton buttonMenu = new JButton("Menu");
        JButton buttonDownloadCanvas = new JButton("Download Canvas");
        JButton buttonSave = new JButton("Save");
        JButton buttonReset = new JButton("Reset");
        JButton buttonZoomIn = new JButton("Zoom In");
        JButton buttonZoomOut = new JButton("Zoom Out");

        // Add buttons to the side panel with some vertical spacing
        sidePanel.add(buttonMenu);
        sidePanel.add(Box.createVerticalStrut(10));  // Add spacing between buttons
        sidePanel.add(buttonDownloadCanvas);
        sidePanel.add(Box.createVerticalStrut(10));  // Add spacing between buttons
        sidePanel.add(buttonSave);
        sidePanel.add(Box.createVerticalStrut(10));  // Add spacing between buttons
        sidePanel.add(buttonReset);
        sidePanel.add(Box.createVerticalStrut(10));  // Add spacing between buttons
        sidePanel.add(buttonZoomIn);
        sidePanel.add(Box.createVerticalStrut(10));  // Add spacing between buttons
        sidePanel.add(buttonZoomOut);

        // Add the panels to the frame
        frame.add(bottomPanel);
        frame.add(sidePanel);

        // Set the frame background color to white
        frame.getContentPane().setBackground(Color.WHITE);

        // Use null layout for the frame to control the positioning of components manually
        frame.setLayout(null);

        // Revalidate and repaint the frame to update the layout
        frame.revalidate();
        frame.repaint();
    }
}
