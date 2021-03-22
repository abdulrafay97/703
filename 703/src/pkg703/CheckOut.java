/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg703;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static pkg703.Home.getDbConnection;

/**
 *
 * @author Ran
 */
public class CheckOut extends javax.swing.JFrame {

    /**
     * Creates new form CheckOut
     */
    
    float payable;
    float TotalBill;
    int id;
    String discount;
    float paidAmount;
    float balance;
    
    public CheckOut() {
        initComponents();
    }
    
    public CheckOut(String Total) throws ClassNotFoundException, SQLException {
        initComponents();
        billTxt.setText(Total);
        this.TotalBill = Float.parseFloat(Total);
        discountTxt.setText("0");
    }
    
    public void applyDiscount(){
        float discount = Float.parseFloat(discountTxt.getText());
        
        if(discount<0 || discount>100){
            discount=0;
            discountTxt.setText("0");
            JOptionPane.showMessageDialog(null, "Invalid Discount Value!", "Error",JOptionPane.ERROR_MESSAGE);
        }
        
        float total = TotalBill;
        float calculatedDiscount = (total*discount)/100;
        
        this.payable = total-calculatedDiscount;
        
        billTxt.setText(Float.toString(payable));
    }
    
    public void getTheID() throws ClassNotFoundException, SQLException{
        
        Connection con = getDbConnection();
        
        String sql = "SELECT ID FROM customers ORDER BY ID DESC LIMIT 1";
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
    
    public void proceed() throws SQLException, ClassNotFoundException{
        String customername = customerNameTxt.getText();
        String Discount = discountTxt.getText();
        float Bill = Float.parseFloat(billTxt.getText());
        float cashCollected = Float.parseFloat(cashCollectedTxt.getText());
        
        this.discount = Discount;
        this.paidAmount = cashCollected;
        this.balance = cashCollected - payable;
        
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
        
        String sql = "INSERT INTO customers(Name , Total_Bill , Discount , PayAble , Paid_Amount , Balance , Date ,"
                + "Day , Month , Year)"
                + " VALUES('"+customername+"' , '"+TotalBill+"' , '"+Discount+"' , '"+payable+"' , '"+cashCollected+"' ,"
                + " '"+(cashCollected - payable)+"' , '"+date+"' , '"+day+"' , '"+Month+"' , '"+Year+"')";
        PreparedStatement pstmt1 = con.prepareStatement(sql);
        pstmt1.executeUpdate();
        con.close();
        
        getTheID();
    }
    
    public void copyTempDataInTheSales() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO Sales(Cust_ID , Menu_ID , Item_Name , Qty , Price , Sub_Total)"
                + "SELECT Cust_ID , ID , Name , Qty , Price , SubTotal from temp";
        stmt.executeUpdate(sql);
        
        
        UpdateInventory();
    }
    
    public void setDiscount(){
        float discount = Float.parseFloat(discountTxt.getText());
        
        if(discount == 0){
            discountTxt.setText("0");
        }
        applyDiscount();
    }
    
    public void TruncateCart() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "TRUNCATE TABLE temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
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
                
                
                JOptionPane.showMessageDialog(null, "DATA Updated");
            }
        }
    }
    
    public void AddDate() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String date = null;
        String sql = "Select Date , Date_Status from date_set";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            date = rs.getString("Date");
            String Date_Status = rs.getString("Date_Status");
            
            if(Date_Status.equals("ON")){
                String sql1 = "Select count(ID) from customers where Date = '"+date+"' ";
                PreparedStatement pstmt1 = con.prepareStatement(sql1);
                ResultSet rs1 = pstmt1.executeQuery();
                
                if(rs1.next()){
                    String count = rs1.getString("count(ID)");
                    
                    if(count.equals("1")){
                        String sql2 = "Insert into summary_cash_in_hand(Date) values('"+date+"')";
                        PreparedStatement pstmt2 = con.prepareStatement(sql2);
                        pstmt2.executeUpdate();
                        con.close();
                    }
                }
            }
        }
    }
    
public class print implements Printable{
    
    public print(){
        PrinterJob printjob = PrinterJob.getPrinterJob();
        printjob.setPrintable(this,getPageFormat(printjob));
        if(printjob.printDialog()){
            try{
                printjob.print();
            }catch (Exception ex){
                System.out.println("ERROR : "+ex);
            }
        }
    }
 
   public PageFormat getPageFormat(PrinterJob pj){
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();    

        double middleHeight = 100.0;  
        double headerHeight = 5.0;                  
        double footerHeight = 5.0;                  
        double width = convert_CM_To_PPI(8);
        double height = convert_CM_To_PPI(headerHeight+middleHeight+footerHeight); 
        paper.setSize(width, height);
        paper.setImageableArea(                    
            0,
            10,
            width,            
            height - convert_CM_To_PPI(1)
        );
            
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);    

        return pf;
    }
    
    protected double convert_CM_To_PPI(double cm) {            
	return toPPI(cm * 0.393600787);            
    }
 
    protected double toPPI(double inch) {            
	return inch * 72d;            
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int page){
        
        String username = System.getProperty("user.name");
        ImageIcon icon1 = new ImageIcon("C:\\Users\\"+username+"\\Desktop\\logo.png"); 
        
        String customername = customerNameTxt.getText();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        
        if(page==0){
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate((int) pageFormat.getImageableX(),(int) pageFormat.getImageableY()); 
            
              
        try{
            int y=20;
            int yShift = 10;
            int headerRectHeight=15;
            int headerRectHeighta=40;
            g2d.setFont(new Font("Monospaced",Font.ITALIC,9));
      
                g2d.drawImage(icon1.getImage(), 50 , 20 , 90 , 30 , rootPane);y+=yShift+30;
                g2d.drawString("             STATE 703                ",-1,y);y+=yShift;
                g2d.drawString("         The House of Taste            ",-1,y);y+=yShift;
                g2d.drawString("      Address: 2-P1 Valencia Town     ",1,y);y+=yShift;
                g2d.drawString("         Tel: +92 301 4365434        ",1,y);y+=yShift;
                g2d.drawString("************************************",1,y);y+=yShift;
                g2d.drawString("              INVOICE                 ",1,y);y+=yShift;
                g2d.drawString("************************************",1,y);y+=yShift;
                g2d.drawString("Cust No:   "+id+" ",1,y);y+=yShift;
                g2d.drawString("Cust Name: "+customername+" ",1,y);y+=yShift;
                g2d.drawString("Date/Time: "+date+"          ",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=headerRectHeight;
                g2d.drawString("Item            Qty Price  Total  ",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=headerRectHeight;
                               Connection con = getDbConnection();
        
                               String sql = "Select Name , Qty , Price , SubTotal from temp";
                               PreparedStatement pstmt = con.prepareStatement(sql);
                               ResultSet rs = pstmt.executeQuery();     
                               
                               while(rs.next()){
                                    String Name = rs.getString("Name");
                                    int qty = rs.getInt("Qty");
                                    String price = rs.getString("Price");
                                    String sub = rs.getString("SubTotal");
                                    
                                    g2d.drawString(Name,1,y);y+=yShift;
                                    g2d.drawString("                "+qty+"   "+price+"   "+sub,1,y);y+=yShift;
                               }

                g2d.drawString("-------------------------------------",1,y);y+=yShift;
                g2d.drawString("Sub Total:              Rs. "+TotalBill+" ",1,y);y+=yShift;
                g2d.drawString("Discount:                  "+discount+"% ",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=yShift;
                g2d.drawString("PayAble:                Rs. "+payable+"",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=yShift;
                g2d.drawString("Amount Paid:            Rs. "+paidAmount+"",1,y);y+=yShift;
                g2d.drawString("Balance:                Rs. "+balance+"",1,y);y+=yShift;
                g2d.drawString("*************************************",1,y);y+=yShift;
                g2d.drawString("  THANKYOU FOR DINING WITH US.   ",1,y);y+=yShift;
                g2d.drawString("       PLEASE COME AGAIN!   ",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=yShift;
                g2d.drawString("|        Developed By ElCoders.     |",1,y);y+=yShift;
                g2d.drawString("|       el.coders.dev@gmail.com     |",1,y);y+=yShift;
                g2d.drawString("-------------------------------------",1,y);y+=yShift; 
            
            
    return (PAGE_EXISTS);
    } catch (Exception ex)
    {
        System.out.println("ERROR : "+ex);
    }
    return(PAGE_EXISTS);
}
    else
    return(NO_SUCH_PAGE);
    

}


        public print(Graphics grphcs, PageFormat pf, int i) throws PrinterException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        customerNameTxt = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        discountTxt = new javax.swing.JTextField();
        proceedButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        billTxt = new javax.swing.JTextField();
        billTxt.setEditable(false);
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cashCollectedTxt = new javax.swing.JTextField();
        billTxt.setEditable(false);
        jLabel4 = new javax.swing.JLabel();
        applyDiscountBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 24)); // NOI18N
        jLabel2.setText("Check Out");

        jLabel12.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel12.setText("Customer Name:");

        jLabel15.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel15.setText("Discount:");

        discountTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountTxtActionPerformed(evt);
            }
        });

        proceedButton.setBackground(new java.awt.Color(255, 255, 255));
        proceedButton.setText("Print");
        proceedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                proceedButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                proceedButtonMouseExited(evt);
            }
        });
        proceedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("%");

        jLabel16.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel16.setText("Bill:");

        billTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                billTxtActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Rs.");

        jLabel17.setFont(new java.awt.Font("Tempus Sans ITC", 1, 14)); // NOI18N
        jLabel17.setText("Cash Collected:");

        cashCollectedTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashCollectedTxtActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Rs.");

        applyDiscountBtn.setBackground(new java.awt.Color(0, 39, 0));
        applyDiscountBtn.setText("Apply DIscount");
        applyDiscountBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyDiscountBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(customerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(discountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(applyDiscountBtn))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(billTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cashCollectedTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(32, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel2)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discountTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel1)
                    .addComponent(applyDiscountBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(billTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel17)
                    .addComponent(cashCollectedTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(proceedButton)
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 272, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void discountTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountTxtActionPerformed
        
    }//GEN-LAST:event_discountTxtActionPerformed

    private void proceedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proceedButtonActionPerformed
        String cash = cashCollectedTxt.getText();
        String bill = billTxt.getText();
        
        float cash1 = Float.parseFloat(cash);
        float bill1 = Float.parseFloat(bill);
        
        if(cash1 >= bill1){
            try {
                setDiscount();
                proceed();
                print obj = new print();
                AddDate();
                TruncateCart();
                this.dispose();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(CheckOut.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Cash Collected is Less Than the Bill.","Error!",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_proceedButtonActionPerformed

    private void cashCollectedTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashCollectedTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cashCollectedTxtActionPerformed

    private void applyDiscountBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyDiscountBtnActionPerformed
        applyDiscount();
    }//GEN-LAST:event_applyDiscountBtnActionPerformed

    private void billTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_billTxtActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
        this.dispose();
    }//GEN-LAST:event_formWindowLostFocus

    private void proceedButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proceedButtonMouseEntered
        proceedButton.setBackground(Color.green);
        proceedButton.setForeground(Color.white);
    }//GEN-LAST:event_proceedButtonMouseEntered

    private void proceedButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proceedButtonMouseExited
        proceedButton.setBackground(Color.WHITE);
        proceedButton.setForeground(Color.black);
    }//GEN-LAST:event_proceedButtonMouseExited

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
            java.util.logging.Logger.getLogger(CheckOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CheckOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CheckOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CheckOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new CheckOut().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyDiscountBtn;
    private javax.swing.JTextField billTxt;
    private javax.swing.JTextField cashCollectedTxt;
    private javax.swing.JTextField customerNameTxt;
    private javax.swing.JTextField discountTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton proceedButton;
    // End of variables declaration//GEN-END:variables
}
