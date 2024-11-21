import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

public class RoomPlan2 extends JPanel {
    private ArrayList<Room> rooms = new ArrayList<>();
    private Integer roomWidth = null;
    private Integer roomHeight = null;
    private Integer roomX = null;
    private Integer roomY = null;
    private Room selectedRoom = null;
    private Point mouseOffset = null;
    private int originalX, originalY;
    private String selectedRoomType = "Bedroom";
    private Color selectedRoomColor = Color.GREEN;
    private final int GRID_SIZE = 20;
    private final int LEFT_PANEL_WIDTH = 150;
    private final int BOTTOM_PANEL_HEIGHT = 50; // Height of the bottom panel
    private Point hoverPoint = null;
    private JButton propertiesButton;
    private JButton modifyButton;
    private JButton roomTypeButton;
    private JButton addFurnButton;
    private JButton setDimensionsButton;
    private JButton setPositionButton;
    private JButton addRoomButton;
    private JButton addRelativeButton; // Add Relative button
    private JButton addWindowButton;
    private JButton zoomInButton;
    private List<Furniture> furnitureList = new ArrayList<>();
    private Furniture selectedFurniture = null;
    private Map<Furniture, Room> furnitureRoomMap = new HashMap<>();
    private Map<Furniture, Point> furnitureRelativePositions = new HashMap<>();
    private double zoomScale = 1.0;

    public RoomPlan2() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, getHeight())); // LEFT_PANEL_WIDTH = 200
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(new GridBagLayout()); // Center elements in the panel

// Create buttons with fixed dimensions
Dimension buttonSize = new Dimension(180, 40); // Width slightly smaller than the panel width
JButton downloadButton = new JButton("Download");
downloadButton.setPreferredSize(buttonSize);
downloadButton.addActionListener(e -> {
    if (rooms.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No rooms to save!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setSelectedFile(new java.io.File("floorplan.2ds"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        java.io.File file = fileChooser.getSelectedFile();
        String filePath = file.getPath();
        if (!filePath.endsWith(".2ds")) {
            filePath += ".2ds";
        }
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("2DS_FLOOR_PLAN\n");
            writer.write("VERSION 1.0\n");
            writer.write("ROOM_COUNT " + rooms.size() + "\n\n");
            
            // Write each room's data
            for (Room room : rooms) {
                writer.write("ROOM\n");
                writer.write("TYPE " + room.getRoomType() + "\n");
                writer.write("POSITION " + (room.getX() - LEFT_PANEL_WIDTH) + " " + room.getY() + "\n");
                writer.write("DIMENSIONS " + room.getWidth() + " " + room.getHeight() + "\n");
                writer.write("END_ROOM\n\n");
            }
            
            writer.write("END_FLOOR_PLAN");
            JOptionPane.showMessageDialog(this, "Floor plan saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving floor plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});

JButton openFileButton = new JButton("Open File");
openFileButton.setPreferredSize(buttonSize);
openFileButton.addActionListener(e -> {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".2ds");
        }
        public String getDescription() {
            return "2D Floor Plan Files (*.2ds)";
        }
    });
    
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Clear existing rooms
            rooms.clear();
            
            String line;
            // Read header
            line = reader.readLine();
            if (!"2DS_FLOOR_PLAN".equals(line)) {
                throw new IOException("Invalid file format");
            }
            
            reader.readLine(); // Skip version
            reader.readLine(); // Skip room count
            reader.readLine(); // Skip empty line
            
            // Read rooms
            while ((line = reader.readLine()) != null) {
                if ("ROOM".equals(line)) {
                    // Read room type
                    String type = reader.readLine().substring(5); // Skip "TYPE "
                    
                    // Read position
                    String[] position = reader.readLine().substring(9).split(" "); // Skip "POSITION "
                    int x = Integer.parseInt(position[0]) + LEFT_PANEL_WIDTH;
                    int y = Integer.parseInt(position[1]);
                    
                    // Read dimensions
                    String[] dimensions = reader.readLine().substring(11).split(" "); // Skip "DIMENSIONS "
                    int width = Integer.parseInt(dimensions[0]);
                    int height = Integer.parseInt(dimensions[1]);
                    
                    // Set color based on room type
                    Color color;
                    switch (type) {
                        case "Bedroom" -> color = Color.GREEN;
                        case "Kitchen" -> color = Color.RED;
                        case "Living Room" -> color = Color.ORANGE;
                        case "Bathroom" -> color = Color.BLUE;
                        case "Drawing Room" -> color = Color.YELLOW;
                        default -> color = Color.GRAY;
                    }
                    
                    // Create and add room
                    Room room = new Room(x, y, width, height, color, type);
                    rooms.add(room);
                    
                    reader.readLine(); // Skip "END_ROOM"
                    reader.readLine(); // Skip empty line
                }
            }
            
            repaint();
            JOptionPane.showMessageDialog(this, "Floor plan loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading floor plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            rooms.clear(); // Clear rooms if there was an error
            repaint();
        }
    }
});

JButton resetButton = new JButton("Reset");
resetButton.setPreferredSize(buttonSize);

//functionality to reset 
resetButton.addActionListener(e -> {
    rooms.clear();
    furnitureList.clear();
    selectedRoom = null;
    repaint();
    
});

GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(10, 0, 10, 0); // Spacing between buttons
gbc.gridx = 0; // Single column
gbc.anchor = GridBagConstraints.CENTER; // Center alignment

gbc.gridy = 0;
leftPanel.add(downloadButton, gbc);

gbc.gridy = 1;
leftPanel.add(openFileButton, gbc);

gbc.gridy = 2;
leftPanel.add(resetButton, gbc);


add(leftPanel, BorderLayout.WEST);


        roomTypeButton = new JButton("Room Type");
        roomTypeButton.addActionListener(e -> {
            String[] roomTypes = {"Bedroom", "Kitchen", "Living Room", "Bathroom", "Drawing Room"};
            String selectedType = (String) JOptionPane.showInputDialog(null, "Choose Room Type:", "Room Type", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);

            if (selectedType != null) {
                selectedRoomType = selectedType;
                switch (selectedType) {
                    case "Bedroom" -> selectedRoomColor = Color.GREEN;
                    case "Kitchen" -> selectedRoomColor = Color.RED;
                    case "Living Room" -> selectedRoomColor = Color.ORANGE;
                    case "Bathroom" -> selectedRoomColor = Color.BLUE;
                    case "Drawing Room" -> selectedRoomColor = Color.YELLOW;
                }
            }
        });

        JButton setDimensionsButton = new JButton();
        setDimensionsButton.setText("Set Dimension");
       setDimensionsButton.addActionListener(e -> {
            JTextField widthField = new JTextField(5);
            JTextField heightField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("Width:"));
            panel.add(widthField);
            panel.add(Box.createHorizontalStrut(15));
            panel.add(new JLabel("Height:"));
            panel.add(heightField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Room Dimensions", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    roomWidth = Integer.parseInt(widthField.getText());
                    roomHeight = Integer.parseInt(heightField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for dimensions.");
                }
            }
        });

        JButton setPositionButton = new JButton();
        setPositionButton.setText("Set Position");
       setPositionButton.addActionListener(e -> {
            JTextField xField = new JTextField(5);
            JTextField yField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("X Position:"));
            panel.add(xField);
            panel.add(Box.createHorizontalStrut(15));
            panel.add(new JLabel("Y Position:"));
            panel.add(yField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Room Position", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    roomX = Integer.parseInt(xField.getText()) + LEFT_PANEL_WIDTH;
                    roomY = Integer.parseInt(yField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for position.");
                }
            }
        });

        JButton addRoomButton = new JButton();
        addRoomButton.setText("Add Room");
        addRoomButton.addActionListener(e -> {
            // Check if room dimensions are incomplete
            if (roomWidth == null || roomHeight == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please provide room dimensions (width and height) before adding the room.",
                    "Incomplete Data",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                // Create a new room object
                Room newRoom = new Room(roomX, roomY, roomWidth, roomHeight, selectedRoomColor, selectedRoomType);
                roomWidth = null;
                roomHeight = null;
                roomX = null;
                roomY = null;
                
                // Check for overlaps
                boolean overlaps = false;
                for (Room room : rooms) {
                    if (newRoom.overlapsWith(room)) {
                        overlaps = true;
                        break;
                    }
                }
        
                if (overlaps) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Cannot add room. It overlaps with an existing room.",
                        "Overlap Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    // Add the new room to the list and repaint the canvas
                    rooms.add(newRoom);
                    repaint();
                }
            }
        });
        

        propertiesButton = new JButton("Properties");
        propertiesButton.setVisible(false); // Initially invisible
        propertiesButton.addActionListener(e -> {
            if (selectedRoom != null) {
                // Show properties of the selected room
                String properties = "Room Type: " + selectedRoom.getRoomType() +
                        "\nPosition: (" + (selectedRoom.getX() - LEFT_PANEL_WIDTH) + ", " + selectedRoom.getY() + ")" +
                        "\nDimensions: " + selectedRoom.getWidth() + "x" + selectedRoom.getHeight();
                JOptionPane.showMessageDialog(this, properties, "Room Properties", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No room is selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modifyButton = new JButton("Modify");
        modifyButton.setVisible(false); // Initially invisible
        modifyButton.addActionListener(e -> {
            if (selectedRoom != null) {
                String[] options = {"Rotate", "Remove","Edit"};
                int choice = JOptionPane.showOptionDialog(this, "Choose an option", "Modify Room", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
                if (choice == 0) {
                    // Rotate the room by 90 degrees clockwise
                    int newWidth = selectedRoom.getHeight();
                    int newHeight = selectedRoom.getWidth();
        
                    // Adjust position to maintain the top-left corner position after rotation
                    int newX = selectedRoom.getX() + (selectedRoom.getWidth() - newWidth) / 2;
                    int newY = selectedRoom.getY() + (selectedRoom.getHeight() - newHeight) / 2;
        
                    // Temporarily store old dimensions and position
                    int oldWidth = selectedRoom.getWidth();
                    int oldHeight = selectedRoom.getHeight();
                    int oldX = selectedRoom.getX();
                    int oldY = selectedRoom.getY();
        
                    // Update room's dimensions and position
                    selectedRoom.setWidth(newWidth);
                    selectedRoom.setHeight(newHeight);
                    selectedRoom.setX(newX);
                    selectedRoom.setY(newY);
        
                    // Check for overlaps after rotation
                    boolean overlaps = false;
                    for (Room room : rooms) {
                        if (room != selectedRoom && selectedRoom.overlapsWith(room)) {
                            overlaps = true;
                            break;
                        }
                    }
        
                    if (overlaps) {
                        JOptionPane.showMessageDialog(null, "Room overlaps with another. Rotation reverted.", 
                                                      "Overlap Error", JOptionPane.ERROR_MESSAGE);
                        // Revert to old dimensions and position if overlaps
                        selectedRoom.setWidth(oldWidth);
                        selectedRoom.setHeight(oldHeight);
                        selectedRoom.setX(oldX);
                        selectedRoom.setY(oldY);
                    }
        
                    repaint();
                } else if (choice == 1) {
                    // Remove the room
                    rooms.remove(selectedRoom);
                    selectedRoom = null;
                    repaint();
                } else if(choice == 2){
                    // Edit the room
                    JPanel editPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                    
                    // Room Type Selection
                    String[] roomTypes = {"Bedroom", "Kitchen", "Living Room", "Bathroom", "Drawing Room"};
                    JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
                    roomTypeCombo.setSelectedItem(selectedRoom.getRoomType());
                    editPanel.add(new JLabel("Room Type:"));
                    editPanel.add(roomTypeCombo);
                    
                    // Position Fields
                    JTextField xField = new JTextField(String.valueOf(selectedRoom.getX() - LEFT_PANEL_WIDTH));
                    JTextField yField = new JTextField(String.valueOf(selectedRoom.getY()));
                    JPanel positionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    positionPanel.add(new JLabel("X:"));
                    positionPanel.add(xField);
                    positionPanel.add(new JLabel("Y:"));
                    positionPanel.add(yField);
                    editPanel.add(new JLabel("Position:"));
                    editPanel.add(positionPanel);
                    
                    // Size Fields
                    JTextField widthField = new JTextField(String.valueOf(selectedRoom.getWidth()));
                    JTextField heightField = new JTextField(String.valueOf(selectedRoom.getHeight()));
                    JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    sizePanel.add(new JLabel("W:"));
                    sizePanel.add(widthField);
                    sizePanel.add(new JLabel("H:"));
                    sizePanel.add(heightField);
                    editPanel.add(new JLabel("Size:"));
                    editPanel.add(sizePanel);
                    
                    int result = JOptionPane.showConfirmDialog(this, editPanel, 
                        "Edit Room Properties", JOptionPane.OK_CANCEL_OPTION);
                        
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // Store original values in case we need to revert
                            int oldX = selectedRoom.getX();
                            int oldY = selectedRoom.getY();
                            int oldWidth = selectedRoom.getWidth();
                            int oldHeight = selectedRoom.getHeight();
                            String oldType = selectedRoom.getRoomType();
                            Color oldColor = selectedRoom.color;
                            
                            // Update position
                            int newX = Integer.parseInt(xField.getText()) + LEFT_PANEL_WIDTH;
                            int newY = Integer.parseInt(yField.getText());
                            selectedRoom.setX(newX);
                            selectedRoom.setY(newY);
                            
                            // Update size
                            int newWidth = Integer.parseInt(widthField.getText());
                            int newHeight = Integer.parseInt(heightField.getText());
                            selectedRoom.setWidth(newWidth);
                            selectedRoom.setHeight(newHeight);
                            
                            // Update room type and color
                            String newType = (String) roomTypeCombo.getSelectedItem();
                            Color newColor;
                            switch (newType) {
                                case "Bedroom" -> newColor = Color.GREEN;
                                case "Kitchen" -> newColor = Color.RED;
                                case "Living Room" -> newColor = Color.ORANGE;
                                case "Bathroom" -> newColor = Color.BLUE;
                                case "Drawing Room" -> newColor = Color.YELLOW;
                                default -> newColor = Color.GRAY;
                            }
                            selectedRoom.type = newType;
                            selectedRoom.color = newColor;
                            
                            // Check for overlaps with other rooms
                            boolean overlaps = false;
                            for (Room room : rooms) {
                                if (room != selectedRoom && selectedRoom.overlapsWith(room)) {
                                    overlaps = true;
                                    break;
                                }
                            }
                            
                            // If there's an overlap, revert all changes
                            if (overlaps) {
                                selectedRoom.setX(oldX);
                                selectedRoom.setY(oldY);
                                selectedRoom.setWidth(oldWidth);
                                selectedRoom.setHeight(oldHeight);
                                selectedRoom.type = oldType;
                                selectedRoom.color = oldColor;
                                
                                JOptionPane.showMessageDialog(this,
                                    "Cannot apply changes. The room would overlap with another room.",
                                    "Overlap Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                            
                            repaint();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this,
                                "Please enter valid numbers for position and size.",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        addFurnButton  = new JButton("Add Furniture");
        addFurnButton.setVisible(false);
        addFurnButton.addActionListener(e -> {
            if (selectedRoom != null) {
                String[] options = {"bed","chair", "table", "sofa", "dining set"};
                int choice = JOptionPane.showOptionDialog(this, "Choose Furniture", "Add Furniture", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice >= 0 && choice < options.length) {
                    int furnWidth = selectedRoom.getWidth() / 5;
                    int furnHeight = selectedRoom.getHeight() / 5;
                    int furnX = selectedRoom.getX() + (selectedRoom.getWidth() - furnWidth) / 2;
                    int furnY = selectedRoom.getY() + (selectedRoom.getHeight() - furnHeight) / 2;

                    Furniture newFurniture = new Furniture(furnX, furnY, furnWidth, furnHeight, options[choice]);
                    furnitureList.add(newFurniture);
                    updateFurnitureRoomMapping(newFurniture, selectedRoom);
                    repaint();
                }
            }
        });

        addRelativeButton = new JButton("Add Relative");
        addRelativeButton.setVisible(false); // Initially invisible
        addRelativeButton.addActionListener(e -> {
            if (selectedRoom != null) {
                // First get dimensions for the new room
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);
                
                JPanel dimensionPanel = new JPanel();
                dimensionPanel.add(new JLabel("Width:"));
                dimensionPanel.add(widthField);
                dimensionPanel.add(Box.createHorizontalStrut(15));
                dimensionPanel.add(new JLabel("Height:"));
                dimensionPanel.add(heightField);
                
                int dimensionResult = JOptionPane.showConfirmDialog(null, dimensionPanel, 
                    "Enter Room Dimensions", JOptionPane.OK_CANCEL_OPTION);
                
                if (dimensionResult == JOptionPane.OK_OPTION) {
                    try {
                        int newWidth = Integer.parseInt(widthField.getText());
                        int newHeight = Integer.parseInt(heightField.getText());
                        
                        // Then get the direction
                        String[] directions = {"North", "East", "South", "West"};
                        String selectedDirection = (String) JOptionPane.showInputDialog(this, 
                            "Select relative position to add a new room:", 
                            "Add Relative Room", 
                            JOptionPane.QUESTION_MESSAGE, 
                            null, 
                            directions, 
                            directions[0]);
            
                        if (selectedDirection != null) {
                            int newX = selectedRoom.getX();
                            int newY = selectedRoom.getY();
            
                            // Determine the new position based on the selected direction
                            switch (selectedDirection) {
                                case "North" -> newY = selectedRoom.getY() - newHeight;
                                case "East" -> newX = selectedRoom.getX() + selectedRoom.getWidth();
                                case "South" -> newY = selectedRoom.getY() + selectedRoom.getHeight();
                                case "West" -> newX = selectedRoom.getX() - newWidth;
                            }
                            
                            // Get room type
                            String[] roomTypes = {"Bedroom", "Kitchen", "Living Room", "Bathroom", "Drawing Room"};
                            String newRoomType = (String) JOptionPane.showInputDialog(null, 
                                "Choose Room Type:", "Room Type", 
                                JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);
                                
                            if (newRoomType != null) {
                                // Set color based on room type
                                Color newRoomColor;
                                switch (newRoomType) {
                                    case "Bedroom" -> newRoomColor = Color.GREEN;
                                    case "Kitchen" -> newRoomColor = Color.RED;
                                    case "Living Room" -> newRoomColor = Color.ORANGE;
                                    case "Bathroom" -> newRoomColor = Color.BLUE;
                                    case "Drawing Room" -> newRoomColor = Color.YELLOW;
                                    default -> newRoomColor = Color.GRAY;
                                }
                                
                                Room newRoom = new Room(newX, newY, newWidth, newHeight, newRoomColor, newRoomType);
                
                                // Check for overlaps
                                boolean overlaps = false;
                                for (Room room : rooms) {
                                    if (newRoom.overlapsWith(room)) {
                                        overlaps = true;
                                        break;
                                    }
                                }
                
                                if (overlaps) {
                                    JOptionPane.showMessageDialog(this, 
                                        "Cannot add room. It overlaps with an existing room.", 
                                        "Overlap Error", 
                                        JOptionPane.ERROR_MESSAGE);
                                } else {
                                    rooms.add(newRoom);
                                    repaint();
                                }
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, 
                            "Please enter valid numbers for dimensions.", 
                            "Invalid Input", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No room is selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        addWindowButton = new JButton("Add Window");
        addWindowButton.setVisible(false); // Initially invisible
        addWindowButton.addActionListener(e -> {
            if (selectedRoom != null) {
                String[] options = {"North Wall", "South Wall", "East Wall", "West Wall"};
                String wall = (String) JOptionPane.showInputDialog(
                    this,
                    "Select wall for window:",
                    "Add Window",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                
                if (wall != null) {
                    char wallChar = wall.charAt(0);
                    int position;
                    
                    // Calculate middle position based on wall
                    switch (wall) {
                        case "North Wall", "South Wall" -> {
                            position = selectedRoom.getWidth() / 2;
                        }
                        case "East Wall", "West Wall" -> {
                            position = selectedRoom.getHeight() / 2;
                        }
                        default -> {
                            return;
                        }
                    }
                    
                    selectedRoom.addWindow(position, wallChar);
                    repaint();
                }
            }
        });
    
        zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> {
            zoomScale *= 1.2; // Increase zoom by 20%
            repaint();
        });
    
        // Add buttons to the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(getWidth(), BOTTOM_PANEL_HEIGHT));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.add(roomTypeButton);
        buttonPanel.add(setDimensionsButton);
        buttonPanel.add(setPositionButton);
        buttonPanel.add(addRoomButton);
        buttonPanel.add(propertiesButton);
        buttonPanel.add(modifyButton); // Show Modify button
        buttonPanel.add(addFurnButton);// Add Modify button
        buttonPanel.add(addRelativeButton); 
        buttonPanel.add(addWindowButton);
        buttonPanel.add(zoomInButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Existing mousePressed logic...
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Furniture clickedFurniture = null;
                for (Furniture furn : furnitureList) {
                    if (furn.contains(e.getX(), e.getY())) {
                        clickedFurniture = furn;
                        selectedFurniture = furn;
                        break;
                    }
                }

                if (clickedFurniture != null) {
                    propertiesButton.setVisible(false);
                    modifyButton.setVisible(false);
                    addRelativeButton.setVisible(false);
                    roomTypeButton.setVisible(false);
                    setDimensionsButton.setVisible(false);
                    setPositionButton.setVisible(false);
                    addRoomButton.setVisible(false);
                    addFurnButton.setVisible(true);
                    addWindowButton.setVisible(false);
                    selectedRoom = null;
                } else {
                    Room clickedRoom = null;
                    for (Room room : rooms) {
                        if (room.contains(e.getX(), e.getY())) {
                            clickedRoom = room;
                            mouseOffset = new Point(e.getX() - room.getX(), e.getY() - room.getY());
                            originalX = room.getX();
                            originalY = room.getY();
                            break;
                        }
                    }

                    if (clickedRoom != null) {
                        selectedRoom = clickedRoom;
                        selectedFurniture = null;
                        propertiesButton.setVisible(true);
                        modifyButton.setVisible(true);
                        addRelativeButton.setVisible(true);
                        roomTypeButton.setVisible(false);
                        setDimensionsButton.setVisible(false);
                        setPositionButton.setVisible(false);
                        addRoomButton.setVisible(false);
                        addFurnButton.setVisible(true);
                        addWindowButton.setVisible(true);
                    } else {
                        selectedRoom = null;
                        selectedFurniture = null;
                        propertiesButton.setVisible(false);
                        modifyButton.setVisible(false);
                        addRelativeButton.setVisible(false);
                        roomTypeButton.setVisible(true);
                        setDimensionsButton.setVisible(true);
                        setPositionButton.setVisible(true);
                        addRoomButton.setVisible(true);
                        addFurnButton.setVisible(false);
                        addWindowButton.setVisible(false);
                    }
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Room clickedRoom = null;
                for (Room room : rooms) {
                    if (room.contains(e.getX(), e.getY())) {
                        clickedRoom = room;
                        mouseOffset = new Point(e.getX() - room.getX(), e.getY() - room.getY());
                        originalX = room.getX();
                        originalY = room.getY();
                        break;
                    }
                }

                if (clickedRoom != null) {
                    selectedRoom = clickedRoom;
                    propertiesButton.setVisible(true);
                    modifyButton.setVisible(true); // Show Modify button
                    roomTypeButton.setVisible(false);
                    setDimensionsButton.setVisible(false);
                    setPositionButton.setVisible(false);
                    addRoomButton.setVisible(false);
                    addFurnButton.setVisible(true);
                    addWindowButton.setVisible(true);
                } else {
                    selectedRoom = null;
                    propertiesButton.setVisible(false);
                    modifyButton.setVisible(false); // Hide Modify button
                    roomTypeButton.setVisible(true);
                    setDimensionsButton.setVisible(true);
                    setPositionButton.setVisible(true);
                    addRoomButton.setVisible(true);
                    addFurnButton.setVisible(false);
                    addWindowButton.setVisible(false);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedRoom != null) {
                    boolean overlaps = false;
                    for (Room room : rooms) {
                        if (room != selectedRoom && selectedRoom.overlapsWith(room)) {
                            overlaps = true;
                            break;
                        }
                    }

                    if (overlaps) {
                        JOptionPane.showMessageDialog(null, "Room overlaps with another. Click OK to return to original position.", 
                                                      "Overlap Error", JOptionPane.ERROR_MESSAGE);
                        selectedRoom.setX(originalX);
                        selectedRoom.setY(originalY);
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null && mouseOffset != null) {
                    int newX = e.getX() - mouseOffset.x;
                    int newY = e.getY() - mouseOffset.y;

                    // Apply boundary constraints
                    if (newX < LEFT_PANEL_WIDTH) {
                        newX = LEFT_PANEL_WIDTH;
                    }
                    if (newY < 0) {
                        newY = 0;
                    }
                    if (newX + selectedRoom.getWidth() > getWidth()) {
                        newX = getWidth() - selectedRoom.getWidth();
                    }
                    if (newY + selectedRoom.getHeight() > getHeight() - BOTTOM_PANEL_HEIGHT) {
                        newY = getHeight() - BOTTOM_PANEL_HEIGHT - selectedRoom.getHeight();
                    }

                    // Calculate the change in position with movement smoothing
                    int deltaX = (newX - selectedRoom.getX()) / 2; // Smooth movement by moving halfway
                    int deltaY = (newY - selectedRoom.getY()) / 2;

                    // Update room position
                    selectedRoom.setX(selectedRoom.getX() + deltaX);
                    selectedRoom.setY(selectedRoom.getY() + deltaY);

                    // Update furniture positions based on their relative positions
                    for (Furniture furn : furnitureList) {
                        if (furnitureRoomMap.get(furn) == selectedRoom) {
                            Point relativePos = furnitureRelativePositions.get(furn);
                            if (relativePos != null) {
                                furn.setX(selectedRoom.getX() + relativePos.x);
                                furn.setY(selectedRoom.getY() + relativePos.y);
                            }
                        }
                    }
                    repaint();
                }
                if (selectedFurniture != null) {
                    int newX = e.getX() - selectedFurniture.getWidth() / 2;
                    int newY = e.getY() - selectedFurniture.getHeight() / 2;
                    
                    Room containingRoom = null;
                    for (Room room : rooms) {
                        if (newX >= room.getX() && 
                            newX + selectedFurniture.getWidth() <= room.getX() + room.getWidth() &&
                            newY >= room.getY() && 
                            newY + selectedFurniture.getHeight() <= room.getY() + room.getHeight()) {
                            containingRoom = room;
                            break;
                        }
                    }

                    if (containingRoom != null) {
                        selectedFurniture.setX(newX);
                        selectedFurniture.setY(newY);
                        updateFurnitureRoomMapping(selectedFurniture, containingRoom);
                        repaint();
                    }
                }
            }
        });
    }

    private void updateFurnitureRoomMapping(Furniture furniture, Room room) {
        if (room != null) {
            furnitureRoomMap.put(furniture, room);
            // Store relative position within the room
            int relativeX = furniture.getX() - room.getX();
            int relativeY = furniture.getY() - room.getY();
            furnitureRelativePositions.put(furniture, new Point(relativeX, relativeY));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        for (Room room : rooms) {
            room.draw(g);
        }

        for (Furniture furn : furnitureList) {
            furn.draw(g);
        }

        if (hoverPoint != null) {
            g.setColor(Color.RED);
            g.fillRect(hoverPoint.x - 2, hoverPoint.y - 2, 4, 4);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int i = LEFT_PANEL_WIDTH; i < getWidth(); i += GRID_SIZE) {
            g.drawLine(i, 0, i, getHeight() - BOTTOM_PANEL_HEIGHT);
        }
        for (int i = 0; i < getHeight() - BOTTOM_PANEL_HEIGHT; i += GRID_SIZE) {
            g.drawLine(LEFT_PANEL_WIDTH, i, getWidth(), i);
        }
    }

    private class Room {
        private int x, y, width, height;
        private Color color;
        private String type;
        private List<Window> windows = new ArrayList<>();

        public Room(int x, int y, int width, int height, Color color, String type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.type = type;
        }

        public boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw room border first
            g2d.setColor(Color.BLACK);
            int borderWidth = 8; // Define border width to use consistently
            g2d.setStroke(new BasicStroke(borderWidth));
            g2d.drawRect(x, y, width, height);
            
            // Draw windows
            float[] dashPattern = {6, 6};
            BasicStroke dashedStroke = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, 
                                             BasicStroke.JOIN_BEVEL, 0, dashPattern, 0);
            g2d.setStroke(dashedStroke);
            
            for (Window window : windows) {
                int position = window.getPosition();
                
                // First fill with white
                g2d.setColor(Color.WHITE);
                switch (window.wall) {
                    case 'N' -> {
                        // Draw white background
                        g2d.fillRect(x + position - Window.WINDOW_LENGTH/2, y - borderWidth/2,
                                    Window.WINDOW_LENGTH, borderWidth);
                        // Draw dashed black line
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(x + position - Window.WINDOW_LENGTH/2, y,
                           x + position + Window.WINDOW_LENGTH/2, y);
                    }
                    case 'S' -> {
                        g2d.fillRect(x + position - Window.WINDOW_LENGTH/2, y + height - borderWidth/2,
                                    Window.WINDOW_LENGTH, borderWidth);
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(x + position - Window.WINDOW_LENGTH/2, y + height,
                           x + position + Window.WINDOW_LENGTH/2, y + height);
                    }
                    case 'E' -> {
                        g2d.fillRect(x + width - borderWidth/2, y + position - Window.WINDOW_LENGTH/2,
                                    borderWidth, Window.WINDOW_LENGTH);
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(x + width, y + position - Window.WINDOW_LENGTH/2,
                           x + width, y + position + Window.WINDOW_LENGTH/2);
                    }
                    case 'W' -> {
                        g2d.fillRect(x - borderWidth/2, y + position - Window.WINDOW_LENGTH/2,
                                    borderWidth, Window.WINDOW_LENGTH);
                        g2d.setColor(Color.BLACK);
                        g2d.drawLine(x, y + position - Window.WINDOW_LENGTH/2,
                           x, y + position + Window.WINDOW_LENGTH/2);
                    }
                }
            }
            
            // Reset stroke for doors
            g2d.setStroke(new BasicStroke(borderWidth));
            
            // Draw doors
            for (Room other : rooms) {
                if (other != this && isAdjacent(other)) {
                    drawDoor(g2d, other);
                }
            }
        }

        public boolean isAdjacent(Room other) {
            // Check if the rooms are adjacent either vertically or horizontally
            boolean isAdjacentVertically = (x < other.x + other.width && x + width > other.x) && 
                                           (y + height == other.y || y == other.y + other.height);
            boolean isAdjacentHorizontally = (y < other.y + other.height && y + height > other.y) && 
                                             (x + width == other.x || x == other.x + other.width);
            return isAdjacentVertically || isAdjacentHorizontally;
        }

        public void drawDoor(Graphics2D g2d, Room other) {
            // Check if the rooms are adjacent
            if (isAdjacent(other)) {
                int borderThickness = 10; // Match the border thickness
                
                // If the rooms are adjacent horizontally (side by side)
                if (x + width == other.x || x == other.x + other.width) {
                    // Calculate vertical overlap
                    int overlap = Math.min(y + height, other.y + other.height) - Math.max(y, other.y);
                    if (overlap >= 30) { // Minimum overlap required
                        // Calculate wall height (the overlap)
                        int wallHeight = overlap;
                        // Door should be at most 1/3 of wall height and no more than 25 pixels
                        int doorLength = Math.min(25, wallHeight / 3);
                        
                        int doorX = x + width == other.x ? x + width - borderThickness / 2 : x - borderThickness / 2;
                        int doorY = Math.max(y, other.y) + overlap / 2 - doorLength / 2;
                        
                        // Check for window overlap
                        boolean windowOverlap = false;
                        char wallToCheck = x + width == other.x ? 'E' : 'W';
                        
                        // Check windows in the current room
                        for (Window window : windows) {
                            if (window.wall == wallToCheck) {
                                int winPos = window.getPosition();
                                if (winPos >= doorY && winPos <= doorY + doorLength) {
                                    windowOverlap = true;
                                    break;
                                }
                            }
                        }
                        
                        // Check windows in the adjacent room
                        if (!windowOverlap) {
                            char otherWallToCheck = x + width == other.x ? 'W' : 'E';
                            for (Window window : other.windows) {
                                if (window.wall == otherWallToCheck) {
                                    int winPos = window.getPosition();
                                    if (winPos >= doorY && winPos <= doorY + doorLength) {
                                        windowOverlap = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        // Only draw the door if there's no window overlap
                        if (!windowOverlap) {
                            g2d.setColor(Color.white); // Door color
                            g2d.fillRect(doorX, doorY, borderThickness, doorLength); // Draw the door
                        }
                    }
                }
                // If the rooms are adjacent vertically (on top of each other)
                else if (y + height == other.y || y == other.y + other.height) {
                    // Calculate horizontal overlap
                    int overlap = Math.min(x + width, other.x + other.width) - Math.max(x, other.x);
                    if (overlap >= 30) { // Minimum overlap required
                        // Calculate wall width (the overlap)
                        int wallWidth = overlap;
                        // Door should be at most 1/3 of wall width and no more than 25 pixels
                        int doorLength = Math.min(25, wallWidth / 3);
                        
                        int doorX = Math.max(x, other.x) + overlap / 2 - doorLength / 2;
                        int doorY = y + height == other.y ? y + height - borderThickness / 2 : y - borderThickness / 2;
                        
                        // Check for window overlap
                        boolean windowOverlap = false;
                        char wallToCheck = y + height == other.y ? 'S' : 'N';
                        
                        // Check windows in the current room
                        for (Window window : windows) {
                            if (window.wall == wallToCheck) {
                                int winPos = window.getPosition();
                                if (winPos >= doorX && winPos <= doorX + doorLength) {
                                    windowOverlap = true;
                                    break;
                                }
                            }
                        }
                        
                        // Check windows in the adjacent room
                        if (!windowOverlap) {
                            char otherWallToCheck = y + height == other.y ? 'N' : 'S';
                            for (Window window : other.windows) {
                                if (window.wall == otherWallToCheck) {
                                    int winPos = window.getPosition();
                                    if (winPos >= doorX && winPos <= doorX + doorLength) {
                                        windowOverlap = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        // Only draw the door if there's no window overlap
                        if (!windowOverlap) {
                            g2d.setColor(Color.white); // Door color
                            g2d.fillRect(doorX, doorY, doorLength, borderThickness); // Draw the door
                        }
                    }
                }
            }
        }

        public boolean overlapsWith(Room other) {
            return x < other.x + other.width &&
                   x + width > other.x &&
                   y < other.y + other.height &&
                   y + height > other.y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setWidth(int width) {
            this.width = width;
            // Windows will automatically adjust due to proportion-based positioning
        }

        public void setHeight(int height) {
            this.height = height;
            // Windows will automatically adjust due to proportion-based positioning
        }

        public String getRoomType() {
            return type;
        }

        public class Window {
            private double proportion; // Position as proportion along the wall (0.0 to 1.0)
            private char wall;    // 'N' for North, 'S' for South, 'E' for East, 'W' for West
            private static final int WINDOW_LENGTH = 40;
            
            public Window(int position, char wall) {
                // Convert absolute position to proportion
                this.wall = wall;
                if (wall == 'N' || wall == 'S') {
                    this.proportion = (double)position / Room.this.width;
                } else {
                    this.proportion = (double)position / Room.this.height;
                }
            }

            public int getPosition() {
                // Convert proportion back to absolute position
                if (wall == 'N' || wall == 'S') {
                    return (int)(proportion * Room.this.width);
                } else {
                    return (int)(proportion * Room.this.height);
                }
            }
        }

        private boolean isWallShared(char wall) {
            for (Room other : rooms) {
                if (other != this && isAdjacent(other)) {
                    switch (wall) {
                        case 'N' -> {
                            if (y == other.y + other.height) return true;
                        }
                        case 'S' -> {
                            if (y + height == other.y) return true;
                        }
                        case 'E' -> {
                            if (x + width == other.x) return true;
                        }
                        case 'W' -> {
                            if (x == other.x + other.width) return true;
                        }
                    }
                }
            }
            return false;
        }

        public void addWindow(int position, char wall) {
            if (isWallShared(wall)) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot add window on a shared wall between rooms.", 
                    "Invalid Window Position", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if window overlaps with existing windows
            for (Window w : windows) {
                if (w.wall == wall) {
                    int existingPos = w.getPosition();
                    if (Math.abs(existingPos - position) < Window.WINDOW_LENGTH) {
                        return; // Window would overlap, don't add it
                    }
                }
            }
            
            // Check if position is valid (not too close to edges)
            int maxPosition = (wall == 'N' || wall == 'S') ? width : height;
            if (position < Window.WINDOW_LENGTH || position > maxPosition - Window.WINDOW_LENGTH) {
                return; // Too close to edge
            }
            
            windows.add(new Window(position, wall));
        }
    }

    private class Furniture {
        private int x, y, width, height;
        private String type;
        private int rotation;

        public Furniture(int x, int y, int width, int height, String type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = type;
            this.rotation = 0;
        }

        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.DARK_GRAY);
            
            AffineTransform oldTransform = g2d.getTransform();
            
            g2d.rotate(Math.toRadians(rotation), 
                       x + width / 2.0, 
                       y + height / 2.0);
            
            switch(type) {
                case "bed" -> g2d.setColor(new Color(139, 69, 19));
                case "chair" -> g2d.setColor(new Color(165, 42, 42));
                case "table" -> g2d.setColor(new Color(101, 67, 33));
                case "sofa" -> g2d.setColor(new Color(112, 128, 144));
                case "dining set" -> g2d.setColor(new Color(70, 130, 180));
            }
            
            g2d.fillRect(x, y, width, height);
            g2d.setTransform(oldTransform);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
        }

        public boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && 
                   mouseY >= y && mouseY <= y + height;
        }

        public void rotate() {
            rotation = (rotation + 90) % 360;
            int temp = width;
            width = height;
            height = temp;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public String getType() { return type; }
        public int getRotation() { return rotation; }

        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
    }
    
    private boolean isInsideRoom(Furniture furniture, Room room) {
        return furniture.getX() >= room.getX() && 
               furniture.getX() + furniture.getWidth() <= room.getX() + room.getWidth() &&
               furniture.getY() >= room.getY() && 
               furniture.getY() + furniture.getHeight() <= room.getY() + room.getHeight();
    }
    
    public static void main(String[] args) {
        try {
            // Set up FlatLaf theme
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }

}