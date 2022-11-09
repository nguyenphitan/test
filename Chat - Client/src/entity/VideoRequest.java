/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 *
 * @author ADMIN
 */
public class VideoRequest implements Serializable {
    private int roomId;
    private Date sendTime;
    private User user;
    private ImageIcon imageIcon;

    public VideoRequest() {
    }

    public VideoRequest(int roomId, Date sendTime, User user, ImageIcon imageIcon) {
        this.roomId = roomId;
        this.sendTime = sendTime;
        this.user = user;
        this.imageIcon = imageIcon;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    
}
