package com.multimedia.domain;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/*
 * created by divya at 9/29/2018
 */
public class Peer implements Serializable {
    private InetAddress ip;
    private int port;
    private String id;
    private boolean active;
    private long lastSeen;
    private Set<String> fileSet = new HashSet<String>();


    public Peer(InetAddress ip, String id, int port) {
        this.ip = ip;
        this.id = id;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<String> getFileSet() {
        return fileSet;
    }

    public void setFileSet(Set<String> fileSet) {
        this.fileSet = fileSet;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", id='" + id + '\'' +
                ", active=" + active +
                ", lastSeen=" + lastSeen +
                ", fileSet=" + fileSet +
                '}';
    }


}
