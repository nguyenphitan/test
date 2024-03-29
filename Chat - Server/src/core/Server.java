/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.github.sarxos.webcam.Webcam;
import controller.ServerWorker;
import entity.Message;
import entity.Request;
import entity.Room;
import entity.User;
import entity.UserRoom;
import entity.VideoRequest;
import flag.ActionFlags;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class Server {

    private static final int port = 11000;
    private static final List<ServerWorker> serverWorkers = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running");
        try {
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class Handler extends Thread {

        private Socket socket;
        private ServerWorker serverWorker;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                serverWorker = new ServerWorker(socket);
                serverWorkers.add(serverWorker);        // add to list server when have a socket accept
                System.out.println(socket.getLocalAddress() + " da ket noi" + serverWorkers.size());

                while (true) {
                    System.out.println("User size" + serverWorkers.size());
                    checkRequest(serverWorker);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                serverWorker.logout();
                serverWorkers.remove(serverWorker);
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private static void checkRequest(ServerWorker uc) throws ClassNotFoundException, IOException {
            Request request = (Request) uc.objectInputStream.readObject();
            processRequest(uc, request);
        }

        private static void processRequest(ServerWorker serverWorker, Request request) throws IOException {
            String actionType = request.getActionType();
//            System.out.println(serverWorker.userc.getUsername() + " " + actionType + " ");
            switch (actionType) {
                case ActionFlags.LOGIN: {
                    User user = (User) request.getEntity();
                    serverWorker.checkLogin(user.getUsername(), user.getPassword(), actionType);
                    serverWorker.getListRoomByUser();
                    sendListUserHome();
                    break;
                }
                case ActionFlags.REGISTER: {
                    User objUser = (User) request.getEntity();
                    serverWorker.checkRegister(objUser, actionType);
                    break;
                }
                case ActionFlags.LOGOUT: {
                    serverWorker.logout();
                    serverWorker.objectInputStream = null;
                    serverWorker.objectOutputStream = null;
                    if (serverWorkers.remove(serverWorker)) {
                        sendListUserHome();
                        List<Room> listRoom = serverWorker.getListRoomByUser();
                        if (listRoom != null) {
                            listRoom.forEach(room -> {
                                sendListUserRoom(room);
                            });
                        }
                    }
                    break;
                }

                case ActionFlags.GET_ALL_USER: {
                    serverWorker.sendListUserHome();
                    break;
                }
                case ActionFlags.UPDATE_ROOM: {
                    Room room = (Room) request.getEntity();
                    Room roomUpdated = serverWorker.updateRoom(room);
                    if (roomUpdated != null) {
                        updateListRoomByUser(roomUpdated);
                    }
                    break;
                }

                case ActionFlags.ADD_USER_TO_ROOM: {
                    UserRoom userRoom = (UserRoom) request.getEntity();
                    Room room = serverWorker.addUserToRoom(userRoom);
                    if (room != null) {
                        updateListRoomByUser(room);
                    }
                    break;
                }

                case ActionFlags.CREATE_ROOM: {
                    Room room = (Room) request.getEntity();
                    serverWorker.createRoomByUser(room, actionType);
                    break;
                }

                case ActionFlags.LEAVE_ROOM: {
                    UserRoom userRoom = (UserRoom) request.getEntity();
                    Room room = serverWorker.LeaveRoom(userRoom, actionType);
                    if (room != null) {
                        sendListUserRoom(room);
                        updateListRoomByUser(room);
                    }
                    break;
                }
                case ActionFlags.CREATE_OR_JOIN_PRIVATE_ROOM: {
                    User user = (User) request.getEntity();
                    Room room = serverWorker.checkExistsPrivateRoom(user);
                    if (room != null) {
                        // open view chat
                        serverWorker.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                        serverWorker.getListRoomByUser();
                    }
                    break;
                }

                case ActionFlags.SEND_MESSAGE: {
                    Message message = (Message) request.getEntity();
                    
                    // Luu lich su tin nhan
                    message = serverWorker.createMessage(message);
                    // Neu luu lich su thanh cong
                    if (message != null) {
                        Room room = serverWorker.getRoom(message.getRoomId());
                        if (room != null) {
                            sendMessageToAllUserInRoom(room, message, ActionFlags.SEND_MESSAGE);
                        }
                    }
                    break;
                }
                
                case ActionFlags.VIDEO_CALL: {
                    Message message = (Message) request.getEntity();
                    
                    // Luu lich su tin nhan
                    message = serverWorker.createMessage(message);
                    // Neu luu lich su thanh cong
                    if (message != null) {
                        Room room = serverWorker.getRoom(message.getRoomId());
                        if (room != null) {
                            sendMessageToAllUserInRoom(room, message, ActionFlags.VIDEO_CALL);
                        }
                    }
                    
                    break;
                }
            }
        }

        private static void sendListUserHome() {
            for (int i = 0; i < serverWorkers.size(); i++) {
                ServerWorker uc = serverWorkers.get(i);
                uc.sendListUserHome();
                System.out.println("Gui danh sach user cho user " + i);
            }
        }

        private static void sendListUserRoom(Room room) {
            for (int i = 0; i < serverWorkers.size(); i++) {
                ServerWorker uc = serverWorkers.get(i);
                uc.sendListUserRoom(room);
            }
        }

        private static void updateListRoomByUser(Room room) {
            for (UserRoom userRoom : room.getListUserRoom()) {
                for (int i = 0; i < serverWorkers.size(); i++) {
                    ServerWorker uc = serverWorkers.get(i);
                    if (uc.userc.getId() == userRoom.getUser().getId()) {
                        uc.getListRoomByUser();
                    }
                }
            }
        }

        private static void sendMessageToAllUserInRoom(Room room, Message message, String action) {

            for (UserRoom userRoom : room.getListUserRoom()) {
                for (int i = 0; i < serverWorkers.size(); i++) {
                    ServerWorker uc = serverWorkers.get(i);
                    if (uc.userc.getId() == userRoom.getUser().getId()) {
                        if(action.equals(ActionFlags.SEND_MESSAGE)) {
                            uc.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                            uc.sendMessage(message, ActionFlags.SEND_MESSAGE);
                            uc.sendListUserHome();
                            uc.getListRoomByUser();
                        }
                        else if (action.equals(ActionFlags.VIDEO_CALL)) {
                            // video call
//                            Webcam webcam = Webcam.getDefault();
//                            webcam.open();
//                            BufferedImage bm = webcam.getImage();
//                            ImageIcon im = new ImageIcon(bm);
//                            System.out.println("Webcam client gửi đi");
//                            bm = webcam.getImage();
//                            im = new ImageIcon(bm);
//
//                            // send video to server
//                            message.setImageIcon(im);
//                            message.setRoomId(room.getId());
//                            message.setSendTime(new Date());
//                            message.setUser(user);
//                            client.userController.sendMessage2(message);
                            
                            
                            uc.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                            uc.sendMessage(message, ActionFlags.VIDEO_CALL);
                            uc.sendListUserHome();
                            uc.getListRoomByUser();
                        }
                        
                    }
                }
            }
        }
        
        private static void sendVideoToAllUserInRoom(Room room, VideoRequest videoRequest) {
            for (UserRoom userRoom : room.getListUserRoom()) {
                for (int i = 0; i < serverWorkers.size(); i++) {
                    ServerWorker uc = serverWorkers.get(i);
                    if (uc.userc.getId() == userRoom.getUser().getId()) {
                        uc.openRoomChat(room, ActionFlags.OPEN_ROOM_CHAT);
                        uc.videoCall(videoRequest, ActionFlags.VIDEO_CALL);
                        uc.sendListUserHome();
                        uc.getListRoomByUser();
                    }
                }
            }
        }
        
    }
}
