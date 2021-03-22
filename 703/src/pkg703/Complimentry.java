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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static pkg703.Home.getDbConnection;

/**
 *
 * @author Ran
 */
public class Complimentry extends javax.swing.JFrame {

    /**
     * Creates new form Complimentry
     */
    
    String Bill;
    int id;
    
    public Complimentry() {
        initComponents();
    }
    
    public Complimentry(String sum) {
        initComponents();
        this.Bill = sum;
    }
    
    public void Add() throws SQLException, ClassNotFoundException{
        String customername = customerNameTxt.getText();
        
        Connection con = getDbConnection();
        
        String date = null;
        String day = null;
        String Month = null;
        String Year = null;
        
        String qry = "Select Date , Day , Month , Year from date_set limit 1";
        PreparedStatement pstmt = con.prepareStatement(qry);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            date = rs.getString("Date");
            day = rs.getString("Day");
            Month = rs.getString("Month");
            Year = rs.getString("Year");
        }
        
        String sql = "INSERT INTO Complimentry_Customer(Name , Bill , Date ,"
                + "Day , Month , Year)"
                + " VALUES('"+customername+"' , '"+Bill+"' , '"+date+"' , '"+day+"' , '"+Month+"' , '"+Year+"')";
        PreparedStatement pstmt1 = con.prepareStatement(sql);
        pstmt1.executeUpdate();
        con.close();
        
        getTheID();
    }
    
    public void getTheID() throws ClassNotFoundException, SQLException{
        
        Connection con = getDbConnection();
        
        String sql = "SELECT ID FROM complimentry_customer ORDER BY ID DESC LIMIT 1";
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
            String sql = "UPDATE temp set Cust_ID = '"+id+"' ";
            stmt.executeUpdate(sql);
            
            copyTempDataInTheSales();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public void copyTempDataInTheSales() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO Complimentry_Sales(ID , Menu_ID , Item_Name , Qty , Price , Sub_Total)"
                + "SELECT Cust_ID , ID , Name , Qty , Price , SubTotal from temp";
        stmt.executeUpdate(sql);
        
        UpdateInventory();
    }
    
    public void UpdateInventory() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select ID , Qty from temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()){
            String Menu_ID = rs.getString("ID");
            int qty = rs.getInt("Qty");
            
            String sql1 = "Select Inventory_ID , Qty from used_things where Menu_ID = '"+Menu_ID+"' ";
            PreparedStatement pstmt1 = con.prepareStatement(sql1);
            ResultSet rs1 = pstmt1.executeQuery();
            
            while(rs1.next()){
                String Inventory_ID = rs1.getString("Inventory_ID");
                float used_Qty = rs1.getFloat("Qty");
                
                String sql2 = "Select Qty , Unit from inventory where ID = '"+Inventory_ID+"' ";
                PreparedStatement pstmt2 = con.prepareStatement(sql2);
                ResultSet rs2 = pstmt2.executeQuery();
                
                rs2.next();
                
                float Existing_Qty = rs2.getFloat("Qty");
                String Unit = rs2.getString("Unit");
                
                switch(Unit){
                    case "Unit Piece":
                        String sql3 = "Update inventory set Qty = '"+(Existing_Qty - (used_Qty*qty))+"' where ID = '"+Inventory_ID+"' ";
                        PreparedStatement pstmt3 = con.prepareStatement(sql3);
                        int rs3 = pstmt3.executeUpdate();
                    break; 
                    
                    case "Kilogram":
                        Existing_Qty = Existing_Qty * 1000;
                        Existing_Qty = (Existing_Qty - (used_Qty*qty));
                        Existing_Qty = Existing_Qty / 1000;
                        
                        String sql4 = "Update inventory set Qty = '"+Existing_Qty+"' where ID = '"+Inventory_ID+"' ";
                        PreparedStatement pstmt4 = con.prepareStatement(sql4);
                        int rs4 = pstmt4.executeUpdate();
                        
                    break;
                    
                    case "Liter":
                        Existing_Qty = Existing_Qty * 1000;
                        Existing_Qty = (Existing_Qty - (used_Qty*qty));
                        Existing_Qty = Existing_Qty / 1000;
                        
                        String sql5 = "Update inventory set Qty = '"+Existing_Qty+"' where ID = '"+Inventory_ID+"' ";
                        PreparedStatement pstmt5 = con.prepareStatement(sql5);
                        int rs5 = pstmt5.executeUpdate();
                    break;    
                }    
                
            }
        }
    }
    
    public void TruncateCart() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "TRUNCATE TABLE temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
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
        jLabel12 = new javax.swing.JLabel();
        customerNameTxt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel12.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel12.setText("Customer Name:");

        jButton1.setText("Confirm");
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
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(customerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        this.dispose();
    }//GEN-LAST:event_formWindowLostFocus

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Add();
            TruncateCart();
            this.dispose();
            JOptionPane.showMessageDialog(null, "Bill Added To Complimentry Table.");
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Complimentry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        jButton1.setBackground(Color.green);
        jButton1.setForeground(Color.white);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        jButton1.setBackground(Color.WHITE);
        jButton1.setForeground(Color.black);
    }//GEN-LAST:event_jButton1MouseExited

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
            java.util.logging.Logger.getLogger(Complimentry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Complimentry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Complimentry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Complimentry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Complimentry().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField customerNameTxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
