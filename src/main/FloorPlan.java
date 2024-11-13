package new_draft;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class RoomPlan extends JPanel {
    private ArrayList<Room> rooms = new ArrayList<>();
    private int roomWidth = 100; // Default width
    private int roomHeight = 100; // Default height
    private int roomX = 50; // Default x position
    private int roomY = 50; // Default y position
    private Room selectedRoom = null; // To track the selected room
    private Point mouseOffset = null; // Offset for dragging
    private int originalX, originalY; // To store the original position of the dragged room
    private String selectedRoomType = "Bedroom"; // Default room type
    private Color selectedRoomColor = Color.GREEN; // Default color is green for Bedroom
    private final int GRID_SIZE = 20; // Size of the grid cells
    private final int HOVER_MARGIN = 5; // Margin within which to detect hover near grid points

    public RoomPlan() {
        setLayout(new BorderLayout()); // Use BorderLayout for overall panel layout

        // Button to set dimensions of the new room
        JButton setDimensionsButton = new JButton("Set Dimensions");
        setDimensionsButton.addActionListener(e -> {
            JTextField widthField = new JTextField(5);
            JTextField heightField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("Width:"));
            panel.add(widthField);
            panel.add(Box.createHorizontalStrut(15)); // Spacer
            panel.add(new JLabel("Height:"));
            panel.add(heightField);

            int result = JOptionPane.showConfirmDialog(null, panel,
                    "Enter Room Dimensions", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    roomWidth = Integer.parseInt(widthField.getText());
                    roomHeight = Integer.parseInt(heightField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for dimensions.");
                }
            }
        });

        // Button to set position of the new room
        JButton setPositionButton = new JButton("Set Position");
        setPositionButton.addActionListener(e -> {
            JTextField xField = new JTextField(5);
            JTextField yField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("X Position:"));
            panel.add(xField);
            panel.add(Box.createHorizontalStrut(15)); // Spacer
            panel.add(new JLabel("Y Position:"));
            panel.add(yField);

            int result = JOptionPane.showConfirmDialog(null, panel,
                    "Enter Room Position", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    roomX = Integer.parseInt(xField.getText());
                    roomY = Integer.parseInt(yField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for position.");
                }
            }
        });

        // Button to add the room
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> {
            Room newRoom = new Room(roomX, roomY, roomWidth, roomHeight, selectedRoomColor, selectedRoomType);

            // Check for overlap with existing rooms
            boolean overlaps = false;
            for (Room room : rooms) {
                if (newRoom.overlapsWith(room)) {
                    overlaps = true;
                    break;
                }
            }

            if (overlaps) {
                JOptionPane.showMessageDialog(null, "Room overlaps with an existing one. Please try a different position.");
            } else {
                rooms.add(newRoom);  // Add new room to the list
                repaint();           // Refresh to show new room
            }
        });

        // Button to choose room type
        JButton roomTypeButton = new JButton("Room Type");
        roomTypeButton.addActionListener(e -> {
            String[] roomTypes = {"Bedroom", "Kitchen", "Living Room", "Bathroom", "Drawing Room"};
            String selectedType = (String) JOptionPane.showInputDialog(null, "Choose Room Type:",
                    "Room Type", JOptionPane.QUESTION_MESSAGE, null, roomTypes, roomTypes[0]);

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

        // Layout buttons at the center of the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center alignment

        buttonPanel.add(roomTypeButton);  // Add Room Type button first (as per request)
        buttonPanel.add(setDimensionsButton); // Add the remaining buttons
        buttonPanel.add(setPositionButton);
        buttonPanel.add(addRoomButton);

        // Add the button panel at the bottom of the frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Mouse listeners to handle dragging rooms
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedRoom = null;
                for (Room room : rooms) {
                    if (room.contains(e.getX(), e.getY())) {
                        selectedRoom = room;
                        mouseOffset = new Point(e.getX() - room.getX(), e.getY() - room.getY());
                        originalX = room.getX(); // Store the original position
                        originalY = room.getY();
                        break;
                    }
                }
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
                        selectedRoom.setX(originalX);
                        selectedRoom.setY(originalY);
                        repaint();  // Refresh to show the room back at the original position
                        JOptionPane.showMessageDialog(null, "Room overlaps with another. Returning to original position.");
                    }
                }
                selectedRoom = null;  // Deselect the room after dragging
                mouseOffset = null;   // Clear the mouse offset
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null && mouseOffset != null) {
                    int newX = e.getX() - mouseOffset.x;
                    int newY = e.getY() - mouseOffset.y;

                    selectedRoom.setX(newX);
                    selectedRoom.setY(newY);

                    repaint();  // Refresh to show the room in its new position
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean onGridPoint = false;

                // Check if the mouse is near a grid intersection
                int closestX = (e.getX() / GRID_SIZE) * GRID_SIZE;
                int closestY = (e.getY() / GRID_SIZE) * GRID_SIZE;
                if (Math.abs(e.getX() - closestX) <= HOVER_MARGIN && Math.abs(e.getY() - closestY) <= HOVER_MARGIN) {
                    onGridPoint = true;
                    setToolTipText("Grid Coordinates: (" + closestX + ", " + closestY + ")");
                }

                // If not on a grid point, check if hovering over a room
                if (!onGridPoint) {
                    setToolTipText(null);
                    for (Room room : rooms) {
                        if (room.contains(e.getX(), e.getY())) {
                            setToolTipText("Type: " + room.getRoomType() +
                                           ", Dimensions: " + room.getWidth() + "x" + room.getHeight() +
                                           ", Position: (" + room.getX() + ", " + room.getY() + ")");
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        for (Room room : rooms) {
            g.setColor(room.getColor());
            g.fillRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
            g.setColor(Color.BLACK);
            g.drawRect(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i < getWidth(); i += GRID_SIZE) {
            g.drawLine(i, 0, i, getHeight());
        }

        for (int i = 0; i < getHeight(); i += GRID_SIZE) {
            g.drawLine(0, i, getWidth(), i);
        }
    }

    class Room {
        private int x, y, width, height;
        private Color color;
        private String roomType;

        public Room(int x, int y, int width, int height, Color color, String roomType) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.roomType = roomType;
        }

        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public Color getColor() { return color; }
        public String getRoomType() { return roomType; }

        public boolean contains(int px, int py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }

        public boolean overlapsWith(Room other) {
            return x < other.x + other.width && x + width > other.x &&
                   y < other.y + other.height && y + height > other.y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Floor Planner");
        RoomPlan planner = new RoomPlan();
        frame.add(planner);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
