import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class class4 {
    public static void main(String[] args) {
        // Create a JFrame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the frame to full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
        // Create a JPanel for the bottom section (Gray panel)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.GRAY);  // Set panel color to gray
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Use FlowLayout for buttons
        bottomPanel.setBounds(0, frame.getHeight() - 200, frame.getWidth(), 200); // Position and size

        // Create buttons for the bottom panel
        JButton button1 = new JButton("Room Type");
        JButton button2 = new JButton("Set Dimensions");
        JButton button3 = new JButton("Set Position");

        // Set icons for the buttons
        ImageIcon b1 = new ImageIcon("roomtype.png");
        ImageIcon b2 = new ImageIcon("dim.png");
        ImageIcon b3 = new ImageIcon("pos.png");
        button1.setIcon(b1);
        button2.setIcon(b2);
        button3.setIcon(b3);

        // Add buttons to the bottom panel
        bottomPanel.add(button1);
        bottomPanel.add(button2);
        bottomPanel.add(button3);

        // Create a JPopupMenu for the "Room Type" button
        JPopupMenu roomTypeMenu = new JPopupMenu();
        JMenuItem m1 = new JMenuItem("Bedroom");
        JMenuItem m2 = new JMenuItem("Bathroom");
        JMenuItem m3 = new JMenuItem("Kitchen");
        JMenuItem m4 = new JMenuItem("Drawing Room");
        JMenuItem m5 = new JMenuItem("Dining Room");

        // Add menu items to the popup menu
        roomTypeMenu.add(m1);
        roomTypeMenu.add(m2);
        roomTypeMenu.add(m3);
        roomTypeMenu.add(m4);
        roomTypeMenu.add(m5);

        // Show the popup menu when the "Room Type" button is clicked
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomTypeMenu.show(button1, button1.getWidth() / 2, button1.getHeight());
            }
        });

        // Create a JPopupMenu for the "Set Dimensions" button
        JPopupMenu dimensionsMenu = new JPopupMenu();

        // Create a panel with input fields for height and width
        JPanel dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new GridLayout(2, 2, 5, 5));
        dimensionPanel.add(new JLabel("Height:"));
        JTextField heightField = new JTextField(10);
        dimensionPanel.add(heightField);
        dimensionPanel.add(new JLabel("Width:"));
        JTextField widthField = new JTextField(10);
        dimensionPanel.add(widthField);

        // Add the dimensionPanel to the popup menu
        dimensionsMenu.add(dimensionPanel);

        // Show the dimensions menu when the "Set Dimensions" button is clicked
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dimensionsMenu.show(button2, button2.getWidth() / 2, button2.getHeight());
            }
        });

        // Create a JPopupMenu for the "Set Position" button
        JPopupMenu positionMenu = new JPopupMenu();

        // Add Absolute positioning option
        JMenuItem absolutePosition = new JMenuItem("Absolute");
        positionMenu.add(absolutePosition);

        // Create a submenu for Relative positioning with directional options
        JMenu relativeMenu = new JMenu("Relative");
        JMenuItem northOption = new JMenuItem("North");
        JMenuItem eastOption = new JMenuItem("East");
        JMenuItem southOption = new JMenuItem("South");
        JMenuItem westOption = new JMenuItem("West");

        // Add directional options to the relative submenu
        relativeMenu.add(northOption);
        relativeMenu.add(eastOption);
        relativeMenu.add(southOption);
        relativeMenu.add(westOption);

        // Add the relative submenu to the position menu
        positionMenu.add(relativeMenu);

        // Show the position menu when the "Set Position" button is clicked
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                positionMenu.show(button3, button3.getWidth() / 2, button3.getHeight());
            }
        });

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
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(buttonSave);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(buttonReset);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(buttonZoomIn);
        sidePanel.add(Box.createVerticalStrut(10));
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
