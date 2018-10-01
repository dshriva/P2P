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
    private static Map<String, Peer> membershipMap = new ConcurrentHashMap<String, Peer>();

    // fileMap has - file name --> List of node id having that file
    private Map<String, Set<String>> fileMap = new ConcurrentHashMap<String, Set<String>>();

    public Server(InetAddress ip, String id, int port) {
        super(ip, id, port);
    }

    public Map<String, Peer> getMembershipMap() {
        return membershipMap;
    }

    public void setMembershipMap(Map<String, Peer> membershipMap) {
        this.membershipMap = membershipMap;
    }

    public Map<String, Set<String>> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, Set<String>> fileMap) {
        this.fileMap = fileMap;
    }

    @Override
    public String toString() {
        return "Server{" +
                ", membershipMap=" + membershipMap +
                ", fileMap=" + fileMap +
                '}';
    }

    public void receiveInfoFromPeer(DatagramSocket serverSocket) {
        while (true) {
            byte[] b = new byte[1024];
            DatagramPacket incomingData = new DatagramPacket(b, b.length);
            try {
                serverSocket.receive(incomingData);
                ByteArrayInputStream byteIn = new ByteArrayInputStream(incomingData.getData());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                try {
                    Peer peer = (Peer) in.readObject();

                    peer.setLastSeen(System.currentTimeMillis());
                    peer.setActive(true);
                    if (!membershipMap.containsKey(peer.getId())) {
                        membershipMap.put(peer.getId(), peer);
                    } else {
                        membershipMap.get(peer.getId()).setLastSeen(System.currentTimeMillis());
                        membershipMap.get(peer.getId()).setActive(true);
                    }

                    //Printing map
                   /* System.out.println("------------------------------------------------");
                    for (Map.Entry<String, Peer> entrySet : membershipMap.entrySet()) {
                        _logger.debug(entrySet.getKey() + " --> " + entrySet.getValue());
                        System.out.println(entrySet.getKey() + " --> " + entrySet.getValue());
                    }
                    System.out.println("------------------------------------------------");
                   */

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //System.out.println("Data received from peer" + info);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void scheduleFailureDetection() {
        Timer periodicTimer = new Timer();
        periodicTimer.schedule(failureDetection(), 0, M *K);
    }

    public void startReceiverThread(final Server server, DatagramSocket serverSocket) {
        final DatagramSocket finalServerSocket = serverSocket;
        Runnable runnable = new Runnable() {
            public void run() {
                server.receiveInfoFromPeer(finalServerSocket);
            }
        };
        Thread t1 = new Thread(runnable);
        t1.start();
    }

    static TimerTask failureDetection() {
        TimerTask failureDetection = new TimerTask() {
            @Override
            public void run() {

                //System.out.println("Detecting Failures");
                _logger.trace("Detecting Failures");
                for (Map.Entry<String, Peer> entrySet : membershipMap.entrySet()) {
                    long currentTime = System.currentTimeMillis();
                   // String currPeerId = entrySet.getKey();
                    if ((currentTime - (M *K)) > entrySet.getValue().getLastSeen()) {
                        if (entrySet.getValue().isActive()) {
                            System.out.println("Marking Peer " + entrySet.getValue() + " as dead");
                            _logger.info("Marking Peer " + entrySet.getValue() + " as dead");
                            //entrySet.getValue().setActive(false);
                            membershipMap.remove(entrySet.getKey());
                        }
                    }
                    //System.out.println("Post failure detection :" + entrySet.getKey() + " --> " + entrySet.getValue());
                }
            }
        };
        return failureDetection;
    }


}

