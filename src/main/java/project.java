import java.awt.*;
import javax.swing.*;
public class project {
    
        public static void main(String[] args) {
        
        
        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setBounds(0,0,400,400);
        JPanel bpanel = new JPanel();
        bpanel.setVisible(true);
        bpanel.setOpaque(false);
        bpanel.setLayout(new FlowLayout());
        bpanel.setBounds(0,  20, 400, 20); // Position and size

        bpanel.setBackground(Color.green);
        frame.add(bpanel);
        frame.setBackground(Color.white);


    }}


