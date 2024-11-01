import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Template {
    public static void main(String[] args) {
        // Create a new JFrame
        JFrame frame = new JFrame("2D Floor Planner");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Panel panel = new Panel();

        frame.add(panel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setVisible(true);
    }
}