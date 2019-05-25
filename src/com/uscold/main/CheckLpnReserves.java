package com.uscold.main;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CheckLpnReserves extends javax.swing.JFrame  {
    /**
     * Creates new form CheckLpnReserves
     */
    public CheckLpnReserves() {
        initComponents();

        // call findTasks function
        findTasks();

    }


    // function to connect to mysql database
    public Connection getConnection() {
        Connection con = null;
        String url = "jdbc:as400://devdb2.uscold.com/migsrcmil";
        String user = "HQIRMORA";
        String password = "HQIRMORA";

        try {
            // Load the driver
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            System.out.println("**** Loaded the JDBC driver");

            // Create the connection using the IBM Data Server Driver for JDBC and SQLJ
            con = DriverManager.getConnection(url, user, password);
            System.out.println("**** Created a JDBC connection to the data source");

            // Commit changes manually
            con.setAutoCommit(false);
            System.out.println("**** Created a JDBC connection to the data source");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return con;
    }

    // function to return tasks arraylist with particular data
    public ArrayList<TaskDetails> taskArrayList(String ValToSearch) {
        ArrayList<TaskDetails> usersList = new ArrayList<TaskDetails>();

        Statement st;
        ResultSet rs;

        try {

            Connection con = getConnection();
            st = con.createStatement();
            String query = "SELECT "
                    + "  LHTB.PALLET_FACTOR          AS \"LotPf\", "
                    + "  WPTB.PALLET_FACTOR          AS \"ProdPf\", "
                    + "  OTB.DLVRY_TICKET_NUMBER     AS \"DtNumber\", "
                    + "  ECTB.CUST_NAME              AS \"CustomerName\", "
                    + "  UTB.LOGIN_USER_NAME         AS \"AssignedUser\", "
                    + "  ptdtb.PROJECTION_FLAG       AS \"PrjFlag\","
                    + "  THTB.TASK_NUMBER            AS \"TaskNumber\", "
                    + "  ptdtb.OB_LPN            	 AS \"OutboundLpn\", "
                    + "  EPTB.PROD_CODE              AS \"ExpectedProd\", "
                    + "  DCTBTS.DTL_CODE             AS \"TaskStatus\", "
                    + "  DCTBTT.DTL_CODE             AS \"TaskType\", "
                    + "  IPDTB.PULL_SEQ_DT           AS \"PullSeq\", "
                    + "  ptdtb.USCS_LOT_SYSID        AS \"USCS_LOT_SYSID\", "
                    + "  LHTB.USCS_LOT_NUMBER        AS \"LotNumber\", "
                    + "  ptdtb.ORD_DTL_SUBLOT_NUMBER AS \"ORD_DTL_SUBLOT_NUMBER\", "
                    + "  PTDTB.PICK_TASK_DTL_SYSID   AS \"PICK_TASK_DTL_SYSID\", "
                    + "  IPDTB.INB_LPN               AS \"RsvInbLpn\", "
                    + "  LTB.LOC_ID                  AS \"PtdPickFromLoc\", "
                    + "  IPDTB.ONHAND_QTY            AS \"IpdOhQty\", "
                    + "  PTDTB.QTY_TO_PICK           AS \"PpdQty2Pick\", "
                    + "  PTDTB.PICKED_FROM_CASES     AS \"PtdPickedFromCases\", "
                    + "  PTDTB.ORIG_PICKED_QTY       AS \"PtdOrgPickedQty\", "
                    + "  PTDTB.PICKED_QTY            AS \"PtdPickedQty\", "
                    + "  PTDTB.pick_scanned_ts       AS \"PickScannedTs\", "
                    + "  PTDTB.pick_completion_ts    AS \"PickCompletionTs\", "
                    + "  PTDTB.CREATE_TS             AS \"CreateTs\", "
                    + "  PTDTB.CREATE_USERID         AS \"CreateUserId\", "
                    + "  PTDTB.UPDATE_TS             AS \"UpdateTs\", "
                    + "  PTDTB.UPDATE_USERID         AS \"UpdateUserId\" "
                    + "FROM TASK_HEADER AS THTB "
                    + "  INNER JOIN PICK_TASK_DTL AS PTDTB ON PTDTB.TASK_NUMBER_SYSID = THTB.TASK_NUMBER_SYSID "
                    + "  INNER JOIN DTL_CODE AS DCTBTT ON DCTBTT.DTL_CODE_SYSID = THTB.TASK_TYPE_SYSID "
                    + "  LEFT JOIN INVTRY_PALLET_DTL AS IPDTB ON IPDTB.INVTRY_PALLET_DTL_SYSID = PTDTB.INVTRY_PALLET_DTL_SYSID "
                    + "  LEFT JOIN LOT_HEADER AS LHTB ON LHTB.USCS_LOT_NUMBER_SYSID = IPDTB.USCS_LOT_NUMBER_SYSID "
                    + "  INNER JOIN DTL_CODE AS DCTBTS ON DCTBTS.DTL_CODE_SYSID = THTB.TASK_STATUS_SYSID "
                    + "  LEFT JOIN \"ORDER\" AS OTB ON OTB.DLVRY_TICKET_SYSID = THTB.DLVRY_TICKET_SYSID "
                    + "  LEFT JOIN ORDER_DTL AS ODTB ON ODTB.ORDER_DTL_SYSID = PTDTB.ORDER_DTL_SYSID "
                    + "  LEFT JOIN ENT_PROD AS EPTB ON EPTB.PROD_SYSID = PTDTB.PROD_SYSID "
                    + "  LEFT JOIN WHSE_PROD AS WPTB ON WPTB.PROD_SYSID = IPDTB.PROD_SYSID AND WPTB.WHSE_SYSID = IPDTB.WHSE_SYSID "
                    + "  LEFT JOIN LOC AS LTB ON LTB.LOC_SYSID = IPDTB.LOC_SYSID "
                    + "  LEFT JOIN \"USER\" AS UTB ON UTB.USERID = THTB.USERID "
                    + "  LEFT JOIN APPT AS A ON A.APPT_SYSID = OTB.APPT_SYSID "
                    + "  LEFT JOIN ENT_CUST AS ECTB ON ECTB.CUST_SYSID = ODTB.CUST_SYSID "
                    + "WHERE A.appt_number = '" + ValToSearch + "' "
                    + "ORDER BY THTB.TASK_NUMBER ASC";

            System.out.println("This value was passed: " + ValToSearch);
            System.out.println("This value was passed: " + query);

            // Execute a query and generate a ResultSet instance
            rs = st.executeQuery(query);
            System.out.println("**** Created JDBC ResultSet object");

            // Connection must be on a unit-of-work boundary to allow close
            con.commit();
            System.out.println ( "**** Transaction committed" );



            TaskDetails taskDetails;

            while (rs.next()) {
                taskDetails = new TaskDetails(
                        rs.getString("LotPf"),
                        rs.getString("ProdPf"),
                        rs.getString("DtNumber"),
                        rs.getString("CustomerName"),
                        rs.getString("AssignedUser"),
                        rs.getString("PrjFlag"),
                        rs.getString("TaskNumber"),
                        rs.getString("OutboundLpn"),
                        rs.getString("ExpectedProd"),
                        rs.getString("TaskStatus"),
                        rs.getString("TaskType"),
                        rs.getString("PullSeq"),
                        rs.getString("USCS_LOT_SYSID"),
                        rs.getString("LotNumber"),
                        rs.getString("ORD_DTL_SUBLOT_NUMBER"),
                        rs.getString("PICK_TASK_DTL_SYSID"),
                        rs.getString("RsvInbLpn"),
                        rs.getString("PtdPickFromLoc"),
                        rs.getString("IpdOhQty"),
                        rs.getString("PpdQty2Pick"),
                        rs.getString("PtdPickedFromCases"),
                        rs.getString("PtdOrgPickedQty"),
                        rs.getString("PtdPickedQty"),
                        rs.getString("PickScannedTs"),
                        rs.getString("PickCompletionTs"),
                        rs.getString("CreateTs"),
                        rs.getString("CreateUserId"),
                        rs.getString("UpdateTs"),
                        rs.getString("UpdateUserId")
                );
                usersList.add(taskDetails);
            }

            // Close the connection
            con.close();
            System.out.println("**** Disconnected from data source");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return usersList;
    }

    // function to display data in jtable
    public void findTasks() {
        ArrayList<TaskDetails> taskDetails = taskArrayList(jText_Search.getText());
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"LotPf", "ProdPf", "DtNumber", "CustomerName", "AssignedUser", "PrjFlag", "TaskNumber", "OutboundLpn", "ExpectedProd", "TaskStatus", "TaskType", "PullSeq", "USCS_LOT_SYSID", "LotNumber", "ORD_DTL_SUBLOT_NUMBER", "PICK_TASK_DTL_SYSID", "RsvInbLpn", "PtdPickFromLoc", "IpdOhQty", "PpdQty2Pick", "PtdPickedFromCases", "PtdOrgPickedQty", "PtdPickedQty", "PickScannedTs", "PickCompletionTs", "CreateTs", "CreateUserId", "UpdateTs", "UpdateUserId"
        });
        Object[] row = new Object[29];

        for (int i = 0; i < taskDetails.size(); i++) {
            row[0] = taskDetails.get(i).LotPf();
            row[1] = taskDetails.get(i).ProdPf();
            row[2] = taskDetails.get(i).DtNumber();
            row[3] = taskDetails.get(i).CustomerName();
            row[4] = taskDetails.get(i).AssignedUser();
            row[5] = taskDetails.get(i).PrjFlag();
            row[6] = taskDetails.get(i).TaskNumber();
            row[7] = taskDetails.get(i).OutboundLpn();
            row[8] = taskDetails.get(i).ExpectedProd();
            row[9] = taskDetails.get(i).TaskStatus();
            row[10] = taskDetails.get(i).TaskType();
            row[11] = taskDetails.get(i).PullSeq();
            row[12] = taskDetails.get(i).USCS_LOT_SYSID();
            row[13] = taskDetails.get(i).LotNumber();
            row[14] = taskDetails.get(i).ORD_DTL_SUBLOT_NUMBER();
            row[15] = taskDetails.get(i).PICK_TASK_DTL_SYSID();
            row[16] = taskDetails.get(i).RsvInbLpn();
            row[17] = taskDetails.get(i).PtdPickFromLoc();
            row[18] = taskDetails.get(i).IpdOhQty();
            row[19] = taskDetails.get(i).PpdQty2Pick();
            row[20] = taskDetails.get(i).PtdPickedFromCases();
            row[21] = taskDetails.get(i).PtdOrgPickedQty();
            row[22] = taskDetails.get(i).PtdPickedQty();
            row[23] = taskDetails.get(i).PickScannedTs();
            row[24] = taskDetails.get(i).PickCompletionTs();
            row[25] = taskDetails.get(i).CreateTs();
            row[26] = taskDetails.get(i).CreateUserId();
            row[27] = taskDetails.get(i).UpdateTs();
            row[28] = taskDetails.get(i).UpdateUserId();
            model.addRow(row);
        }
        jTableLicenses.setModel(model);

    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton_Search = new javax.swing.JButton();
        jText_Search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableLicenses = new javax.swing.JTable();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_Search.setText("Search");
        jButton_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("This value was inputted: " + evt.toString());
                jButton_SearchActionPerformed(evt);
            }
        });

        jText_Search.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTableLicenses.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N

        jTableLicenses.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "LotPf", "ProdPf", "DtNumber", "CustomerName", "AssignedUser", "PrjFlag", "TaskNumber", "OutboundLpn", "ExpectedProd", "TaskStatus", "TaskType", "PullSeq", "USCS_LOT_SYSID", "LotNumber", "ORD_DTL_SUBLOT_NUMBER", "PICK_TASK_DTL_SYSID", "RsvInbLpn", "PtdPickFromLoc", "IpdOhQty", "PpdQty2Pick", "PtdPickedFromCases", "PtdOrgPickedQty", "PtdPickedQty", "PickScannedTs", "PickCompletionTs", "CreateTs", "CreateUserId", "UpdateTs", "UpdateUserId"
                }
        ));

        jScrollPane1.setViewportView(jTableLicenses);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap(22, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jText_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton_Search)
                                                .addGap(136, 136, 136))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(29, 29, 29))))
        );

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton_Search)
                                        .addComponent(jText_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();

    }// </editor-fold>

    private void jButton_SearchActionPerformed(java.awt.event.ActionEvent evt) {

        findTasks();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CheckLpnReserves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CheckLpnReserves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CheckLpnReserves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CheckLpnReserves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CheckLpnReserves().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton_Search;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableLicenses;
    private javax.swing.JTextField jText_Search;
    // End of variables declaration

}
