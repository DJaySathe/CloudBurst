package com.dssathe.cloudburst.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vm_info")
public class VmInfo {
	private Long id;
    private String vm_id;
    private String public_ip;
    private int availability;
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVmId() {
        return vm_id;
    }

    public void setVmId(String vmId) {
        this.vm_id = vmId;
    }

    public String getPublicIp() {
        return public_ip;
    }

    public void setPublicIp(String ip) {
        this.public_ip = ip;
    }
    
    public Long getAvailability() {
        return id;
    }

    public void setAvailability(int id) {
        this.availability = id;
    }
}
