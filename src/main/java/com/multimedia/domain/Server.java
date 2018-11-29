package com.multimedia.domain;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.multimedia.util.NetworkConstants.K;
import static com.multimedia.util.NetworkConstants.M;

/*
 * created by divya at 9/29/2018
 */
public class Server extends Peer {
    public static Logger _logger = Logger.getLogger(String.valueOf(Server.class));

    // membershipMap has - Peer id --> Peer
    public static Map<String, Peer> membershipMap = new ConcurrentHashMap<String, Peer>();

    // fileMap has - file name --> List of node id having that file
    public static Map<String, Set<Integer>> fileMap = new ConcurrentHashMap<String, Set<Integer>>();

    public Server(InetAddress ip, String id, int port) {
        super(ip, id, port);
    }

    public Map<String, Peer> getMembershipMap() {
        return membershipMap;
    }

    public void setMembershipMap(Map<String, Peer> membershipMap) {
        this.membershipMap = membershipMap;
    }

    public Map<String, Set<Integer>> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, Set<Integer>> fileMap) {
        this.fileMap = fileMap;
    }

    @Override
    public String toString() {
        return "Server{" +
                ", membershipMap=" + membershipMap +
                ", fileMap=" + fileMap +
                '}';
    }
}

