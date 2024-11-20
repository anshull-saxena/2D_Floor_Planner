import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

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
    private final int LEFT_PANEL_WIDTH = 100;
    private final int BOTTOM_PANEL_HEIGHT = 80; // Height of the bottom panel
    private Point hoverPoint = null;
    private JButton propertiesButton;
    private JButton modifyButton;
    private JButton roomTypeButton;
    private JButton addFurnButton;
    private JButton setDimensionsButton;
    private JButton setPositionButton;
    private JButton addRoomButton;
    private JButton addRelativeButton; // Add Relative button


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
                String[] options = {"Rotate", "Remove"};
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
                }
            }
        });
        
        addFurnButton  = new JButton("Add Furniture");
        addFurnButton.setVisible(false);
        addFurnButton.addActionListener(e -> {
            if (selectedRoom != null) {
                String[] options1 = {"bed","chair", "table", "sofa", "dining set"};
                int choice = JOptionPane.showOptionDialog(this, "Choose an option", "Add Furniture", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options1, options1[0]);
        
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
                }
                    
                    repaint();
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(getWidth(), BOTTOM_PANEL_HEIGHT));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.add(roomTypeButton);
        buttonPanel.add(setDimensionsButton);
        buttonPanel.add(setPositionButton);
        buttonPanel.add(addRoomButton);
        buttonPanel.add(propertiesButton);
        buttonPanel.add(modifyButton); 
        buttonPanel.add(addFurnButton);// Add Modify button

        add(buttonPanel, BorderLayout.SOUTH);

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
    
        // Add buttons to the panel
        buttonPanel.add(propertiesButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(addRelativeButton); 
        add(buttonPanel, BorderLayout.SOUTH);

    
        // Existing mousePressed logic...
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
                    modifyButton.setVisible(true);
                    addRelativeButton.setVisible(true); // Show Add Relative button
                    roomTypeButton.setVisible(false);
                    setDimensionsButton.setVisible(false);
                    setPositionButton.setVisible(false);
                    addRoomButton.setVisible(false);
                    addFurnButton.setVisible(true);

                } else {
                    selectedRoom = null;
                    propertiesButton.setVisible(false);
                    modifyButton.setVisible(false);
                    addRelativeButton.setVisible(false); // Hide Add Relative button
                    roomTypeButton.setVisible(true);
                    setDimensionsButton.setVisible(true);
                    setPositionButton.setVisible(true);
                    addRoomButton.setVisible(true);
                    addFurnButton.setVisible(false);
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
                } else {
                    selectedRoom = null;
                    propertiesButton.setVisible(false);
                    modifyButton.setVisible(false); // Hide Modify button
                    roomTypeButton.setVisible(true);
                    setDimensionsButton.setVisible(true);
                    setPositionButton.setVisible(true);
                    addRoomButton.setVisible(true);
                    addFurnButton.setVisible(false);
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

                    selectedRoom.setX(newX);
                    selectedRoom.setY(newY);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        for (Room room : rooms) {
            room.draw(g);
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
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK); // Border color
            g2d.setStroke(new BasicStroke(3)); // Border thickness
            g2d.drawRect(x, y,width, height);

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
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getRoomType() {
            return type;
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Room Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in fullscreen mode
        frame.add(new RoomPlan2());
        frame.setVisible(true);
    }

}
