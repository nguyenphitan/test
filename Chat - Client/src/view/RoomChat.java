/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.github.sarxos.webcam.Webcam;
import flag.ActionFlags;
import java.util.Observable;
import java.util.Observer;
import core.Client;
import entity.Message;
import entity.Request;
import entity.Response;
import entity.Room;
import entity.User;
import entity.UserRoom;
import entity.VideoRequest;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import flag.ResultFlags;
import java.awt.image.BufferedImage;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class RoomChat extends javax.swing.JFrame implements Observer {

//    static Socket socket;
//    static String ipAddress = "26.139.8.48";
//    static int port = 11000;
    private Client client;
    private Room room;
    private User user;
    private List<User> listUser = new ArrayList<>();

    public RoomChat(Client client, Room room, User user) {
        initComponents();
        this.client = client;
        this.client.addObserver(this);
        this.room = room;
        this.user = user;
    }

    @Override
    public void update(Observable o, Object arg) {
        Response response = (Response) arg;
        if (response.getResultType().equals(ResultFlags.ERROR)) {
            JOptionPane.showMessageDialog(null, response.getContent(), "Thất bại", JOptionPane.ERROR_MESSAGE);
        } else {
            switch (response.getActionType()) {
                case ActionFlags.SEND_MESSAGE: {
                    Message message = (Message) response.getEntity();
                    if (message.getRoomId() == room.getId()) {
                        if (message.getUser().getDisplayName().equals(user.getDisplayName())) {
                            txtConversation.append(message.getSendTime().toString() + "- You: " + message.getContent() + "\n");
                        } else {
                            txtConversation.append(message.toString() + "\n");
                        }
                    }
                    break;
                }
                
                case ActionFlags.VIDEO_CALL: {
                    Message message = (Message) response.getEntity();
                    if(message.getRoomId() == room.getId()) {
                        if(message.getUser().getDisplayName().equals(user.getDisplayName())) {
                            txtConversation.append(message.getSendTime().toString() + "- You: " + message.getContent() + "\n");
                            
                        } else {
                            JFrame frame = new JFrame("PC client");
                            frame.setSize(640,360);
                            frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
                            JLabel label = new JLabel();
                            label.setSize(640,360);
                            label.setVisible(true);
                            frame.add(label);
                            frame.setVisible(true);

                            try {
                                // video call
                                Webcam webcam = Webcam.getDefault();
                                webcam.open();

                                BufferedImage bm = webcam.getImage();
                                ImageIcon im = new ImageIcon(bm);

                                while(true) {
                   
                                    if(message.getImageIcon() != null) {
                                        System.out.println("Webcam client nhận phản hồi");
                                        label.setIcon(message.getImageIcon());
                                    }
                                    
                                    System.out.println("Webcam client gửi đi");
                                    bm = webcam.getImage();
                                    im = new ImageIcon(bm);
                                    
                                    // send video to server
                                    message.setImageIcon(im);
                                    message.setRoomId(room.getId());
                                    message.setSendTime(new Date());
                                    message.setUser(user);
                                    client.userController.sendMessage2(message);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    break;
                }
                
                case ActionFlags.UPDATE_USERS_ROOM: {
                    Room roomr = (Room) response.getEntity();
                    if (roomr.getId() == room.getId()) {
                        FillListUser(roomr);
                    }
                    break;
                }
                case ActionFlags.UPDATE_ROOM: {
                    Room roomr = (Room) response.getEntity();
                    if (roomr.getId() == room.getId()) {
                        this.room = roomr;
                        lbRoomName.setText("Room name: " + roomr.getDescription());
                    }
                    break;
                }
            }
        }
    }

    private void FillListUser(Room room) {
        DefaultTableModel tm = (DefaultTableModel) tbMembers.getModel();
        tm.setRowCount(0);
        listUser.clear();
        room.getListUserRoom().stream().map(userRoom -> userRoom.getUser()).forEachOrdered(userr -> {
            tm.addRow(userr.toObjects());
            listUser.add(userr);
        });
    }

    private void FillMessages(Room room) {
        room.getListMessage().forEach(message -> {
            if (message.getUser().getUsername().equals(user.getUsername())) {
                txtConversation.append(message.getSendTime() + "- You: " + message.getContent() + "\n");
            } else {
                txtConversation.append(message.toString() + "\n");
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbMembers = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        btnLeave = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtRoomName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lbRoomName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtConversation = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtChat = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        jLabel4.setText("Add Member");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btnAdd.setText("Add User");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        tbMembers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Active"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tbMembers);

        jLabel3.setText("Members");

        btnLeave.setText("Leave Room");
        btnLeave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveActionPerformed(evt);
            }
        });

        jButton1.setText("Rename");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLeave))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLeave, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRoomName))
                .addContainerGap())
        );

        lbRoomName.setText("Room Name: ");

        txtConversation.setEditable(false);
        txtConversation.setColumns(20);
        txtConversation.setRows(5);
        jScrollPane1.setViewportView(txtConversation);

        txtChat.setColumns(20);
        txtChat.setRows(5);
        jScrollPane2.setViewportView(txtChat);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jLabel1.setText("Chat");

        jButton2.setText("Video call");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lbRoomName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbRoomName)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
//        this.setTitle("Nickname: " + user.getDisplayName() + " Tên phòng: " + room.getDescription() + " Số người: " + room.getListUserRoom().size());
        lbRoomName.setText("Room name: " + room.getDescription());
        FillMessages(room);
        FillListUser(room);
    }//GEN-LAST:event_formWindowOpened

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        if (txtChat.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tin nhắn", "Chưa nhập tin nhắn", JOptionPane.WARNING_MESSAGE);
            txtChat.requestFocus();
            return;
        }
        Message message = new Message();
        message.setContent(txtChat.getText());
        message.setUser(user);
        message.setSendTime(new Date());
        message.setRoomId(room.getId());
        client.userController.sendMessage(message);
        txtChat.setText("");
    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
//        client.leaveRoom();
        client.userController.listRoomOpened.remove((Integer) room.getId());
        System.out.println(client.userController.listRoomOpened.size());
        client.deleteObserver(this);
//        client.addObserver(homeCenter);
//        homeCenter.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        ListUser listUserView = new ListUser(client, room, user);
        listUserView.setVisible(true);

    }//GEN-LAST:event_btnAddActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (txtRoomName.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tên phòng", "Chưa nhập tên phòng", JOptionPane.WARNING_MESSAGE);
            txtRoomName.requestFocus();
            return;
        }
        room.setDescription(txtRoomName.getText());
        txtRoomName.setText("");
        client.userController.updateRoom(room);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveActionPerformed
        // TODO add your handling code here:
        for (UserRoom userRoom : room.getListUserRoom()) {
            if (userRoom.getUser().getId() == user.getId()) {
                userRoom.setRoomId(room.getId());
                client.userController.leaveRoom(userRoom);
                client.userController.listRoomOpened.remove((Integer) room.getId());
                client.deleteObserver(this);
                this.setVisible(false);
                return;
            }
        }
    }//GEN-LAST:event_btnLeaveActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Message message = new Message();
        message.setContent("Video call");
        message.setUser(user);
        message.setSendTime(new Date());
        message.setRoomId(room.getId());
        client.userController.sendMessage2(message);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnLeave;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbRoomName;
    private javax.swing.JTable tbMembers;
    private javax.swing.JTextArea txtChat;
    private javax.swing.JTextArea txtConversation;
    private javax.swing.JTextField txtRoomName;
    // End of variables declaration//GEN-END:variables
}
