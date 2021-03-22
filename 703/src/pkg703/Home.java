/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg703;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
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
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Ran
 */
public class Home extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    public Home() throws SQLException, ClassNotFoundException {
        initComponents();
        TruncateCart();
        ShowDataInTheMenuSelectTable();
        ShowDataInTheCart();
        searchtxt.setText("Search Item");
        complimentrybtn.setBackground(Color.yellow);
        checkoutbtn.setBackground(Color.GREEN);
        
    }
    
    public Home(String name) throws SQLException, ClassNotFoundException {
        initComponents();
        TruncateCart();
        ShowDataInTheMenuSelectTable();
        ShowDataInTheCart();
        nametxt.setText(name);
        searchtxt.setText("Search Item");
        complimentrybtn.setBackground(Color.yellow);
        checkoutbtn.setBackground(Color.GREEN);
        session_button.setText("Session Start");
        
    }
    
    public static Connection getDbConnection() throws SQLException, ClassNotFoundException{
        
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/cafe703", "root", "bHTGKn7@");
            
            return con;
    }
    
    public void ShowDataInTheMenuSelectTable() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select ID , Item_Name from menu";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        selectMenuTable.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void ShowDataInTheCart() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select ID , Name , Qty , Price , SubTotal from temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        jTable1.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void search() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String stf = searchtxt.getText();
        
        String qry = "Select ID , Item_Name From menu where Item_Name LIKE '%"+stf+"%' ";
        PreparedStatement pstmt = con.prepareStatement(qry);
        ResultSet rs = pstmt.executeQuery();
        selectMenuTable.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void AddDataInTheTemp(String id , String name , String Qty , float price) throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        int qty1 = Integer.parseInt(Qty);
        
        String sql = "INSERT INTO temp(ID , NAME , Qty , Price , SubTotal)"
                + "VALUES('"+id+"' , '"+name+"' , '"+Qty+"' , '"+price+"' , '"+qty1 * price+"')";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        con.close();
    }
    
    public void deleteRecord(String id) throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Delete from temp where ID = '"+id+"' ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
    }
    
    public void TruncateCart() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "TRUNCATE TABLE temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
    }
    
    public String GetSum() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select Sum(SubTotal) as Total from temp";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        rs.next();
        String sum = rs.getString("Total");
        
        Totaltxt.setText(sum);
        
        return sum;
    }
    
    public void getSales() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String date = null;
        String qry = "Select Date from date_set limit 1";
        PreparedStatement pstmt1 = con.prepareStatement(qry);
        ResultSet rs1 = pstmt1.executeQuery();
        
        if(rs1.next()){
            date = rs1.getString("Date");
        }
        
        
        String sql = "select sum(customers.PayAble) from customers where Date = '"+date+"' ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            String sum = rs.getString("sum(customers.PayAble)");
            
            //sales.setText(sum);
        }
        
    }
    
    public void showTodaysHotItem() throws SQLException, ClassNotFoundException{
        
        Connection con = getDbConnection();
        
        String date = null;
        String qry = "Select Date from date_set limit 1";
        PreparedStatement pstmt1 = con.prepareStatement(qry);
        ResultSet rs1 = pstmt1.executeQuery();
        
        if(rs1.next()){
            date = rs1.getString("Date");
        }
        
        String sql = "select sales.Item_Name , sum(sales.Qty) as Sold_Quantity from sales , customers\n" +
                    "where customers.Date = '"+date+"' AND sales.Cust_ID = customers.ID \n" +
                    "group by sales.Item_Name order by sum(sales.Qty) desc ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        top10.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    public void check(){
        
        if(searchtxt.getText().isEmpty()){
            searchtxt.setText("Search Item");
        }
    }
    
    public void countCutomer() throws SQLException, ClassNotFoundException{
        
        Connection con = getDbConnection();
        
        String date = null;
        String qry = "Select Date from date_set limit 1";
        PreparedStatement pstmt = con.prepareStatement(qry);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            date = rs.getString("Date");
        }
        
        String sql = "select count(ID) from customers where Date = '"+date+"' ";
        PreparedStatement pstmt1 = con.prepareStatement(sql);
        ResultSet rs1 = pstmt1.executeQuery();
        
        if(rs1.next()){
            String count = rs1.getString("count(ID)");
            
            COUNT.setText(count);
        }

    }
    
    public void session_set() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        String sql = "Select count(Date) , Date_Status from date_set";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            String count = rs.getString("count(Date)");
            String Date_Status = rs.getString("Date_Status");
            
            if(count.equals("1") && Date_Status.equals("ON")){
                session_button.setText("Session End");
            }else if(count.equals("1") && Date_Status.equals("OFF")){
                session_button.setText("Session Start");
            }
        }
    }
    
    public void Session_Check() throws SQLException, ClassNotFoundException{
        String session_state = session_button.getText();
        String Password;
        String code = "s2019266036";
        
        if(session_state.equals("Session Start")){
            Password = JOptionPane.showInputDialog("Enter Password To End Session: ");
            
            if(Password.equals(code) && !Password.equals("")){
                Session_Start();
            }
            else{
                JOptionPane.showMessageDialog(null,"Wrong Password.","Error!",JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(session_state.equals("Session End")){
            Password = JOptionPane.showInputDialog("Enter Password To End Session: ");
            
            if(Password.equals(code) && !Password.equals("")){
                Session_End();
            }
            else{
                JOptionPane.showMessageDialog(null,"Wrong Password.","Error!",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void Session_Start() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("hh:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy"); 
        LocalDateTime now = LocalDateTime.now();
        String status = "ON";
        
        DayOfWeek dayOfWeek = DayOfWeek.from(now);
        String date = dtf.format(now);
        String time = dtf1.format(now);
        String day = dayOfWeek.name();
        String month = getMonth();
        String year = dtf2.format(now);
        
        String sql = "Update date_Set Set Date = '"+date+"' , Day = '"+day+"' , Month = '"+month+"' , Year = '"+year+"' , Date_Status = '"+status+"' , Start_Time = '"+time+"' ";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        con.close();
            
        JOptionPane.showMessageDialog(null, "Session Started!");
    }
    
    public void Session_End() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf1.format(now);
        String status = "OFF";
        
        String sql = "Update date_Set Set Date_Status = '"+status+"' , End_Time = '"+time+"'";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
        
        copyData();
        
        JOptionPane.showMessageDialog(null, "Session Ended!");
    }
    
    public void setDate() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        String date = null;
        String sql = "Select Date from date_set";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            date = rs.getString("Date");
            
            datetxt.setText(date);
        }
        
    }
    
    public void copyData() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO Session_Track(Date , Start_Time , End_Time)"
                + "SELECT Date , Start_Time , End_Time from date_set";
        stmt.executeUpdate(sql);
    }
    
    public String getMonth(){
        String[] monthName = {"January", "February",
                    "March", "April", "May", "June", "July",
                    "August", "September", "October", "November",
                    "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

            return month;
    }
        
    public boolean check_Session(){
        String session_state = session_button.getText();
        
        if(session_state.equals("Session Start")){
            return false;
        }
        else if(session_state.equals("Session End")){
            return true;
        }
        
        return true;
    }
    
    public void truncateTable()throws ClassNotFoundException, SQLException{
        Connection con = getDbConnection();
        
        String sql = "TRUNCATE TABLE summary";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.executeUpdate();
    }
    
    public void copyDataExpense() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        
        Statement stmt = con.createStatement();
        String sql = "insert into summary(Date , Expense , Purchase , Sales , Complimentry)"
                + " select expense.Date , expense.Amount , "+0+" , "+0+" , "+0+" from expense";
        stmt.executeUpdate(sql);

    }
    
    public void copyDataPurchase() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        Statement stmt = con.createStatement();
        String sql = "insert into summary(Date , Purchase , Expense , Sales , Complimentry)"
                + " select purchaseditem.Date , purchaseditem.Amount , "+0+" , "+0+" , "+0+" from purchaseditem";
        stmt.executeUpdate(sql);

    }
    
    public void copyDataSales() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        Statement stmt = con.createStatement();
        String sql = "insert into summary(Date , Sales , Expense , Purchase , Complimentry)"
                + " select customers.Date , customers.PayAble , "+0+" , "+0+" , "+0+" from customers";
        stmt.executeUpdate(sql);

    }
    
    public void CopyDataComplimentry() throws SQLException, ClassNotFoundException{
        Connection con = getDbConnection();
        Statement stmt = con.createStatement();
        String sql = "insert into summary(Date , Sales , Expense , Purchase , Complimentry)"
                + " select Complimentry_Customer.Date , "+0+" , "+0+" , "+0+" , Complimentry_Customer.Bill from Complimentry_Customer";
        stmt.executeUpdate(sql);
    }
    
    public void Add_Cash_In_Hand() throws ClassNotFoundException, SQLException{
        Connection con = getDbConnection();
        
        String date = null;
        String sql = "Select Date , Date_Status from date_set";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        
        if(rs.next()){
            date = rs.getString("Date");
            String Date_Status = rs.getString("Date_Status");
            
            if(Date_Status.equals("ON")){
                String sql1 = "Select sum(Profit_Loss) from daily_summary";
                PreparedStatement pstmt1 = con.prepareStatement(sql1);
                ResultSet rs1 = pstmt1.executeQuery();
                
                if(rs1.next()){
                    String SUM = rs1.getString("sum(Profit_Loss)");
                    
                    String sql2 = "Update summary_cash_in_hand set Cash_In_Hand = '"+SUM+"' where Date = '"+date+"' ";
                    PreparedStatement pstmt2 = con.prepareStatement(sql2);
                    pstmt2.executeUpdate();
                    con.close();
                }
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        InventoryButton = new javax.swing.JButton();
        MenuButton = new javax.swing.JButton();
        SalesButton = new javax.swing.JButton();
        PurchaseButton = new javax.swing.JButton();
        ExpensesButton = new javax.swing.JButton();
        SummaryButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        SalesButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        searchtxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        nametxt = new javax.swing.JLabel();
        session_button = new javax.swing.JButton();
        datetxt = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectMenuTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        top10 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        COUNT = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Totaltxt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        checkoutbtn = new javax.swing.JButton();
        complimentrybtn = new javax.swing.JButton();

        jLabel5.setText("jLabel5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        InventoryButton.setBackground(new java.awt.Color(255, 255, 255));
        InventoryButton.setText("INVENTORY");
        InventoryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                InventoryButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                InventoryButtonMouseExited(evt);
            }
        });
        InventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InventoryButtonActionPerformed(evt);
            }
        });

        MenuButton.setBackground(new java.awt.Color(255, 255, 255));
        MenuButton.setText("MENU");
        MenuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MenuButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MenuButtonMouseExited(evt);
            }
        });
        MenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuButtonActionPerformed(evt);
            }
        });

        SalesButton.setBackground(new java.awt.Color(255, 255, 255));
        SalesButton.setText("SALES");
        SalesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SalesButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SalesButtonMouseExited(evt);
            }
        });
        SalesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalesButtonActionPerformed(evt);
            }
        });

        PurchaseButton.setBackground(new java.awt.Color(255, 255, 255));
        PurchaseButton.setText("PURCHASED");
        PurchaseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PurchaseButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PurchaseButtonMouseExited(evt);
            }
        });
        PurchaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PurchaseButtonActionPerformed(evt);
            }
        });

        ExpensesButton.setBackground(new java.awt.Color(255, 255, 255));
        ExpensesButton.setText("EXPENSES");
        ExpensesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ExpensesButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ExpensesButtonMouseExited(evt);
            }
        });
        ExpensesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExpensesButtonActionPerformed(evt);
            }
        });

        SummaryButton.setBackground(new java.awt.Color(255, 255, 255));
        SummaryButton.setText("SUMMARY");
        SummaryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SummaryButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SummaryButtonMouseExited(evt);
            }
        });
        SummaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SummaryButtonActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Graphics/logo.png"))); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe Script", 3, 30)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("STATE");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe Script", 3, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("703");

        SalesButton1.setBackground(new java.awt.Color(255, 255, 255));
        SalesButton1.setText("COMPLIMENTRY");
        SalesButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SalesButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SalesButton1MouseExited(evt);
            }
        });
        SalesButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalesButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SalesButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(MenuButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(InventoryButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SalesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PurchaseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ExpensesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SummaryButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(jLabel6))))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InventoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(MenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(SalesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(SalesButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(PurchaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(ExpensesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(SummaryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Graphics/search.png"))); // NOI18N

        searchtxt.setFont(new java.awt.Font("Tw Cen MT", 2, 14)); // NOI18N
        searchtxt.setBorder(null);
        searchtxt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchtxtMouseClicked(evt);
            }
        });
        searchtxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchtxtKeyReleased(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Graphics/power.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel1MouseExited(evt);
            }
        });

        nametxt.setBackground(new java.awt.Color(0, 0, 0));
        nametxt.setFont(new java.awt.Font("Segoe Script", 3, 14)); // NOI18N
        nametxt.setForeground(new java.awt.Color(51, 51, 51));
        nametxt.setText("703");

        session_button.setText("Session Start");
        session_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                session_buttonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                session_buttonMouseExited(evt);
            }
        });
        session_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                session_buttonActionPerformed(evt);
            }
        });

        datetxt.setBackground(new java.awt.Color(153, 153, 0));
        datetxt.setFont(new java.awt.Font("Segoe Script", 3, 14)); // NOI18N
        datetxt.setForeground(new java.awt.Color(51, 51, 51));
        datetxt.setText("703");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(searchtxt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(datetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(session_button, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(datetxt))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchtxt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(session_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));

        selectMenuTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        selectMenuTable.setColumnSelectionAllowed(true);
        selectMenuTable.getTableHeader().setReorderingAllowed(false);
        selectMenuTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectMenuTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(selectMenuTable);

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TODAY's TOP SELLING ITEMS.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 2, 18), new java.awt.Color(0, 204, 204))); // NOI18N

        top10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        top10.setColumnSelectionAllowed(true);
        top10.getTableHeader().setReorderingAllowed(false);
        top10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                top10MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(top10);

        jButton5.setText("History");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton5MouseExited(evt);
            }
        });
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton5))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel12.setBackground(new java.awt.Color(0, 0, 0));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TODAY's CUSTOMER COUNT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tw Cen MT", 3, 18), new java.awt.Color(153, 0, 0))); // NOI18N

        COUNT.setBackground(new java.awt.Color(255, 255, 255));
        COUNT.setFont(new java.awt.Font("Segoe Script", 2, 36)); // NOI18N
        COUNT.setForeground(new java.awt.Color(255, 255, 255));
        COUNT.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(COUNT, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(COUNT)
                .addContainerGap())
        );

        jButton3.setText("Session Track");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 103, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));

        jPanel5.setBackground(new java.awt.Color(102, 102, 102));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("CART");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(193, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );

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

        jButton2.setText("Clear Cart");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton2MouseExited(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        checkoutbtn.setText("Check Out");
        checkoutbtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                checkoutbtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                checkoutbtnMouseExited(evt);
            }
        });
        checkoutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkoutbtnActionPerformed(evt);
            }
        });

        complimentrybtn.setBackground(new java.awt.Color(255, 255, 255));
        complimentrybtn.setText("Complimentry");
        complimentrybtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                complimentrybtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                complimentrybtnMouseExited(evt);
            }
        });
        complimentrybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                complimentrybtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(complimentrybtn)
                        .addGap(18, 18, 18)
                        .addComponent(checkoutbtn))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 815, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Totaltxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Totaltxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkoutbtn)
                    .addComponent(complimentrybtn))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void InventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InventoryButtonActionPerformed
        try {
            new Inevntory().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_InventoryButtonActionPerformed

    private void MenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuButtonActionPerformed
        try {
            new Menu().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_MenuButtonActionPerformed

    private void SalesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalesButtonActionPerformed
        try {
            new Sales().setVisible(true);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SalesButtonActionPerformed

    private void PurchaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PurchaseButtonActionPerformed
        try {
            new Purchased().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_PurchaseButtonActionPerformed

    private void ExpensesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExpensesButtonActionPerformed
        try {
            new Expense().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ExpensesButtonActionPerformed

    private void SummaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SummaryButtonActionPerformed
        try {
            new Summary().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SummaryButtonActionPerformed

    private void selectMenuTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectMenuTableMouseClicked
        DefaultTableModel model = (DefaultTableModel)selectMenuTable.getModel();
        int getSelectedRow = selectMenuTable.getSelectedRow();
        
        String id = model.getValueAt(getSelectedRow, 0).toString();
        String name = model.getValueAt(getSelectedRow, 1).toString();
        
        Connection con = null;
        try {
            con = getDbConnection();
            
            String sql = "SELECT Price FROM menu WHERE Item_Name = '"+name+"' AND ID = '"+id+"' ";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            rs.next();
            
            float price = rs.getFloat("Price");
            
            String Qty = JOptionPane.showInputDialog("Enter The Quantity: ");
            
            if(!"".equals(Qty))
                AddDataInTheTemp(id , name , Qty , price);
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_selectMenuTableMouseClicked

    private void searchtxtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchtxtKeyReleased
        try {
            search();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchtxtKeyReleased

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        try {
            setDate();
            session_set();
            countCutomer();
            check();
            ShowDataInTheMenuSelectTable();
            ShowDataInTheCart();
            GetSum();
            showTodaysHotItem();
            truncateTable();
            copyDataExpense();
            copyDataPurchase();
            copyDataSales();
            CopyDataComplimentry();
            Add_Cash_In_Hand();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowActivated

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        int getSelectedRow = jTable1.getSelectedRow();
        
        
        if(getSelectedRow >= 0){
            String id = model.getValueAt(getSelectedRow, 0).toString();
            try {
                deleteRecord(id);
                JOptionPane.showMessageDialog(null, "Record Deleted!");
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Select a Row First.");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            TruncateCart();
            JOptionPane.showMessageDialog(null, "Cart Cleared");
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void checkoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkoutbtnActionPerformed
        try {
            if(check_Session()){
                if(jTable1.getRowCount()>0){
                    String sum = GetSum();
                    new CheckOut(sum).setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Cart Is Empty.");
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Start Session First.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_checkoutbtnActionPerformed

    private void top10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_top10MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_top10MouseClicked

    private void complimentrybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_complimentrybtnActionPerformed
        String sum;
        
            try {
                if(check_Session()){
                    if(jTable1.getRowCount()>0){
                        sum = GetSum();
                        new Complimentry(sum).setVisible(true);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Cart Is Empty.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Start Session First.");
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_complimentrybtnActionPerformed

    private void SalesButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalesButton1ActionPerformed
        try {
            new Complimentry_Sales().setVisible(true);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SalesButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            new DateForTheHistoryOfSoldItem().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void InventoryButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InventoryButtonMouseEntered
        InventoryButton.setBackground(Color.black);
        InventoryButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_InventoryButtonMouseEntered

    private void InventoryButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InventoryButtonMouseExited
        InventoryButton.setBackground(Color.WHITE);
        InventoryButton.setForeground(Color.black);
    }//GEN-LAST:event_InventoryButtonMouseExited

    private void MenuButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuButtonMouseEntered
        MenuButton.setBackground(Color.black);
        MenuButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_MenuButtonMouseEntered

    private void MenuButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuButtonMouseExited
        MenuButton.setBackground(Color.WHITE);
        MenuButton.setForeground(Color.black);
    }//GEN-LAST:event_MenuButtonMouseExited

    private void SalesButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SalesButtonMouseEntered
        SalesButton.setBackground(Color.black);
        SalesButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_SalesButtonMouseEntered

    private void SalesButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SalesButtonMouseExited
        SalesButton.setBackground(Color.WHITE);
        SalesButton.setForeground(Color.black);
    }//GEN-LAST:event_SalesButtonMouseExited

    private void SalesButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SalesButton1MouseEntered
        SalesButton1.setBackground(Color.black);
        SalesButton1.setForeground(Color.WHITE);
    }//GEN-LAST:event_SalesButton1MouseEntered

    private void SalesButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SalesButton1MouseExited
        SalesButton1.setBackground(Color.WHITE);
        SalesButton1.setForeground(Color.black);
    }//GEN-LAST:event_SalesButton1MouseExited

    private void PurchaseButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PurchaseButtonMouseEntered
        PurchaseButton.setBackground(Color.black);
        PurchaseButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_PurchaseButtonMouseEntered

    private void PurchaseButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PurchaseButtonMouseExited
        PurchaseButton.setBackground(Color.WHITE);
        PurchaseButton.setForeground(Color.black);
    }//GEN-LAST:event_PurchaseButtonMouseExited

    private void ExpensesButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExpensesButtonMouseEntered
        ExpensesButton.setBackground(Color.black);
        ExpensesButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_ExpensesButtonMouseEntered

    private void ExpensesButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ExpensesButtonMouseExited
        ExpensesButton.setBackground(Color.WHITE);
        ExpensesButton.setForeground(Color.black);
    }//GEN-LAST:event_ExpensesButtonMouseExited

    private void SummaryButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SummaryButtonMouseEntered
        SummaryButton.setBackground(Color.black);
        SummaryButton.setForeground(Color.WHITE);
    }//GEN-LAST:event_SummaryButtonMouseEntered

    private void SummaryButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SummaryButtonMouseExited
        SummaryButton.setBackground(Color.WHITE);
        SummaryButton.setForeground(Color.black);
    }//GEN-LAST:event_SummaryButtonMouseExited

    private void jButton5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseEntered
        jButton5.setBackground(Color.ORANGE);
        jButton5.setForeground(Color.blue);
    }//GEN-LAST:event_jButton5MouseEntered

    private void jButton5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseExited
        jButton5.setBackground(Color.WHITE);
        jButton5.setForeground(Color.black);
    }//GEN-LAST:event_jButton5MouseExited

    private void searchtxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchtxtMouseClicked
        searchtxt.setText("");
    }//GEN-LAST:event_searchtxtMouseClicked

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        jButton1.setBackground(Color.red);
        jButton1.setForeground(Color.white);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        jButton1.setBackground(Color.WHITE);
        jButton1.setForeground(Color.black);
    }//GEN-LAST:event_jButton1MouseExited

    private void jButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseEntered
        jButton2.setBackground(Color.red);
        jButton2.setForeground(Color.white);
    }//GEN-LAST:event_jButton2MouseEntered

    private void jButton2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseExited
        jButton2.setBackground(Color.WHITE);
        jButton2.setForeground(Color.black);
    }//GEN-LAST:event_jButton2MouseExited

    private void complimentrybtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_complimentrybtnMouseEntered
        complimentrybtn.setForeground(Color.gray);
    }//GEN-LAST:event_complimentrybtnMouseEntered

    private void complimentrybtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_complimentrybtnMouseExited
        complimentrybtn.setBackground(Color.yellow);
        complimentrybtn.setForeground(Color.black);
    }//GEN-LAST:event_complimentrybtnMouseExited

    private void checkoutbtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkoutbtnMouseEntered
        checkoutbtn.setForeground(Color.gray);
    }//GEN-LAST:event_checkoutbtnMouseEntered

    private void checkoutbtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkoutbtnMouseExited
        checkoutbtn.setBackground(Color.GREEN);
        checkoutbtn.setForeground(Color.black);
    }//GEN-LAST:event_checkoutbtnMouseExited

    private void jLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseEntered
       
    }//GEN-LAST:event_jLabel1MouseEntered

    private void jLabel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MouseExited

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        new Login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel1MouseClicked

    private void session_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_session_buttonActionPerformed
        try {
            Session_Check();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_session_buttonActionPerformed

    private void session_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_session_buttonMouseEntered
        String session_state = session_button.getText();
        
        if(session_state.equals("Session Start")){
            session_button.setBackground(Color.green);
            session_button.setForeground(Color.white);
        }
        else if(session_state.equals("Session End")){
            session_button.setBackground(Color.red);
            session_button.setForeground(Color.white);
        }
    }//GEN-LAST:event_session_buttonMouseEntered

    private void session_buttonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_session_buttonMouseExited
        session_button.setBackground(Color.WHITE);
        session_button.setForeground(Color.black);
    }//GEN-LAST:event_session_buttonMouseExited

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            new Session_Track().setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Home().setVisible(true);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel COUNT;
    private javax.swing.JButton ExpensesButton;
    private javax.swing.JButton InventoryButton;
    private javax.swing.JButton MenuButton;
    private javax.swing.JButton PurchaseButton;
    private javax.swing.JButton SalesButton;
    private javax.swing.JButton SalesButton1;
    private javax.swing.JButton SummaryButton;
    private javax.swing.JTextField Totaltxt;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton checkoutbtn;
    private javax.swing.JButton complimentrybtn;
    private javax.swing.JLabel datetxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel nametxt;
    private javax.swing.JTextField searchtxt;
    private javax.swing.JTable selectMenuTable;
    private javax.swing.JButton session_button;
    private javax.swing.JTable top10;
    // End of variables declaration//GEN-END:variables
}
