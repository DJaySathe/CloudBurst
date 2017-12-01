package com.dssathe.cloudburst.model;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "reservation")
public class Reservation {
    private Long id;
    private String user_id;
    private String image_name;
    private String vm_id;
    private int source;
    private String public_ip;
    private String username;
    private String password;
    private String start_time;
    private String end_time;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getVm_id() {
        return vm_id;
    }

    public void setVm_id(String vm_id) {
        this.vm_id = vm_id;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getPublic_ip() {
        return public_ip;
    }

    public void setPublic_ip(String public_ip) {
        this.public_ip = public_ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", user_id='" + user_id + '\'' +
                ", image_name='" + image_name + '\'' +
                ", vm_id='" + vm_id + '\'' +
                ", source=" + source +
                ", public_ip='" + public_ip + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                '}';
    }
}
