import java.awt.*;
import javax.swing.*;

public class cl extends JFrame {

    public cl() {
        setTitle("2D Floor Planner with Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen

        // Set layout to BorderLayout
        setLayout(new BorderLayout());

        // Create a control panel on the left
        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(200, getHeight())); // Set width for control panel
        controlPanel.setBackground(Color.LIGHT_GRAY);

        // Create a canvas panel with grid
        CanvasPanel canvasPanel = new CanvasPanel();

        // Add the control panel to the left and canvas panel to the center
        add(controlPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(cl::new);
    }
}

// Canvas panel with grid
class CanvasPanel extends JPanel {

    public CanvasPanel() {
        setBackground(Color.WHITE); // Background color of the canvas
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set grid color and size
        g.setColor(Color.LIGHT_GRAY);
        int gridSize = 20; // Size of each grid cell

        // Draw vertical lines
        for (int x = 0; x < getWidth(); x += gridSize) {
            g.drawLine(x, 0, x, getHeight());
        }

        // Draw horizontal lines
        for (int y = 0; y < getHeight(); y += gridSize) {
            g.drawLine(0, y, getWidth(), y);
        }
    }
}

