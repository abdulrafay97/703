/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg703;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import static pkg703.Home.getDbConnection;

/**
 *
 * @author Ran
 */
public class AddMenu extends javax.swing.JFrame {

    /**
     * Creates new form AddMenu
     */
    
    int id;
    
    public AddMenu() throws SQLException, ClassNotFoundException {
        initComponents();
        ShowData();
        searchtxt.setText("Search Item");
    }
    
    public void Add() throws SQLException, ClassNotFoundException{
        
        String itemName = itemNametxt.getText();
        String price = pricetxt.getText();
        
        Connection con = getDbConnection();
        
        String sql = "INSERT INTO menu(Item_Name , Price) VALUES('"+itemName+"' , '"+price+"')";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        con.close();
        
        getTheID();
        
        JOptionPane.showMessageDialog(null, "Record Inserted!");
    }
    
    public void getTheID() throws ClassNotFoundException, SQLException{
        
        Connection con = getDbConnection();
        
        String sql = "SELECT ID FROM Menu ORDER BY ID DESC LIMIT 1";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()){
            int id1 = rs.getInt(1);
            
            this.id = id1;
        }
        
        updateData(id);
    }
    
    public void updateData(int id) throws ClassNotFoundException, SQLException{
        Connection con = getDbConnection();
        
        try{
            Statement stmt = con.createStatement();
            String sql = "UPDATE used_things_temp set Menu_ID = '"+id+"' ";
            stmt.executeUpdate(sql);
            
            copyData();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public void copyData() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO Used_Things(Inventory_ID , Product_Name , Qty , Menu_ID)"
                + "SELECT Inventory_ID , Product_Name , Qty , Menu_ID from Used_Things_temp";
        stmt.executeUpdate(sql);
        
        TruncateCart();
    }
    
    public void ShowData() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select ID , Product_Name from Inventory";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        jTable1.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void ShowData1() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select Inventory_ID , Product_Name , Qty from used_things_temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        jTable2.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void search() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String stf = searchtxt.getText();
        
        String qry = "Select ID , Product_Name from Inventory where Product_Name LIKE '%"+stf+"%' ";
        PreparedStatement pstmt = con.prepareStatement(qry);
        ResultSet rs = pstmt.executeQuery();
        jTable1.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void Add_Used_Things_Temp(String id , String Product_name , String qty) throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "INSERT INTO used_things_temp(Inventory_ID , Product_Name , Qty) VALUES('"+id+"' , '"+Product_name+"' , '"+qty+"')";
        
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        con.close();
        
        JOptionPane.showMessageDialog(null, "Record Inserted!");
    }
    
    public void delete() throws SQLException, ClassNotFoundException{
        
        DefaultTableModel model = (DefaultTableModel)jTable2.getModel();
        int getSelectedRow = jTable2.getSelectedRow();
        
        String id = model.getValueAt(getSelectedRow, 0).toString();
        String name = model.getValueAt(getSelectedRow, 1).toString();
        
        Connection con = getDbConnection();
        
        String sql = "DELETE FROM used_things_temp where Inventory_ID = '"+id+"' AND Product_Name = '"+name+"' ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "Record Deleted!");
    }
    
    public void TruncateCart() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "TRUNCATE TABLE Used_Things_temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
    }
    
    public void check(){
        
        if(searchtxt.getText().isEmpty()){
            searchtxt.setText("Search Item");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        itemNametxt = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        pricetxt = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        searchtxt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 24)); // NOI18N
        jLabel2.setText("MENU");

        jLabel12.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel12.setText("Item Name:");

        jLabel15.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel15.setText("Price:");

        saveButton.setBackground(new java.awt.Color(255, 255, 255));
        saveButton.setText("SAVE");
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButtonMouseExited(evt);
            }
        });
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        searchtxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchtxtKeyReleased(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 24)); // NOI18N
        jLabel3.setText("Things To Used");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Delete");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(182, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel15))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(pricetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(itemNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(searchtxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(itemNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pricetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(37, 37, 37)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            
            String itemName = itemNametxt.getText();
            String price = pricetxt.getText();
            
            if(itemName.equals("") || price.equals("")){
                JOptionPane.showMessageDialog(null, "Enter Name and Price.");
            }
            else{
                Add();
                this.dispose();
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void searchtxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchtxtKeyReleased
        try {
            search();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchtxtKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        int getSelectedRow = jTable1.getSelectedRow();
        String unit = null;
        String Qty = null;
        String id = model.getValueAt(getSelectedRow, 0).toString();
        String name = model.getValueAt(getSelectedRow, 1).toString();
        
        Connection con = null;
        try {
            con = getDbConnection();
            
            String sql = "Select Unit from Inventory where ID = '"+id+"' AND Product_Name = '"+name+"' ";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            unit = rs.getNString("Unit");
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(null != unit)switch (unit) {
            case "Unit Piece":{
                Qty = JOptionPane.showInputDialog("Enter The Quantity: ");
                    break;
                }
            case "Kilogram":{
                Qty = JOptionPane.showInputDialog("Enter The Quantity In Grams: ");
                    break;
                }
            case "Liter":{
                Qty = JOptionPane.showInputDialog("Enter The Quantity In Mili-Liter: ");
                    break;
                }
            default:
                break;
        }
        try {
            Add_Used_Things_Temp(id , name , Qty);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        try {
            check();
            ShowData1();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowActivated

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
       
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            delete();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        jButton1.setBackground(Color.red);
        jButton1.setForeground(Color.white);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        jButton1.setBackground(Color.WHITE);
        jButton1.setForeground(Color.black);
    }//GEN-LAST:event_jButton1MouseExited

    private void saveButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseEntered
        saveButton.setBackground(Color.green);
        saveButton.setForeground(Color.white);
    }//GEN-LAST:event_saveButtonMouseEntered

    private void saveButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseExited
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(Color.black);
    }//GEN-LAST:event_saveButtonMouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new AddMenu().setVisible(true);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AddMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField itemNametxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField pricetxt;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField searchtxt;
    // End of variables declaration//GEN-END:variables
}
