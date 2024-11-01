import java.awt.Color;
import java.awt.Dimension; // Import Dimension for setting size

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class Panel extends javax.swing.JPanel {

    public Panel() {
        initComponents();
    }

    @SuppressWarnings("unchecked ")
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        // Sample data for the JList
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Menu", "Reset", "Zoom In", "Zoom Out", "Download", "Exit" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setFixedCellHeight(30);
        jList1.setFixedCellWidth(70);
        jScrollPane1.setViewportView(jList1);

        // Set up button
        jButton1.setText("New Canvas");

        // Set button size (width, height)
        jButton1.setPreferredSize(new Dimension(100, 40)); // Modify the dimensions as needed

        // Border for jPanels
        Border blackline = BorderFactory.createLineBorder(Color.black);
        jPanel1.setBorder(blackline);
        jPanel2.setBorder(blackline);

        // Layout for jPanel1
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 427, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 243, Short.MAX_VALUE)
        );

        // Layout for jPanel2
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 187, Short.MAX_VALUE)
        );

        // Create a panel to hold jButton1
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER)); 
        jPanel3.add(jButton1); 

        this.setLayout(new java.awt.BorderLayout());

        // Add jScrollPane1 to the left
        this.add(jScrollPane1, java.awt.BorderLayout.WEST); // jList on the left

        // Create a panel to hold jPanel1 and jPanel2
        javax.swing.JPanel centerPanel = new javax.swing.JPanel(); 
        centerPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.add(jPanel1, java.awt.BorderLayout.CENTER); // Center region for jPanel1

        centerPanel.add(jPanel2, java.awt.BorderLayout.EAST); // Right region for jPanel2

        this.add(centerPanel, java.awt.BorderLayout.CENTER); // Center region for the panel

        this.add(jPanel3, java.awt.BorderLayout.SOUTH); // Bottom region for the button
    }

    // Declare variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel3;
}
