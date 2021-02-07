package com.fahdisa.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Session {

    public static void main(String[] args) throws InterruptedException {
        String address = "tcp://localhost:5555";


//        ZContext context = new ZContext();
//        ZMQ.Socket server = context.createSocket(SocketType.REQ);
//        server.bind(address);
//        System.out.println(server);
//        boolean send = server.send("Hello".getBytes(ZMQ.CHARSET));
//        System.out.println(send);

//        ZMQ.Socket client = context.createSocket(SocketType.REP);
//        client.connect(address);


        Server server = new Server(address);
        boolean serverConnect = server.connect();
        System.out.println(serverConnect);


        Client client = new Client(address);
        boolean clientConnect = client.connect();
        System.out.println(clientConnect);


        for (int i = 0; i < 10; i++) {
            boolean isSent = server.sendMessage("Hello World");
            System.out.println("Is Message Sent : " + isSent);

            TimeUnit.SECONDS.sleep(1);
            String receiveMessage = client.receiveMessage();

            System.out.println("Received Message : " + receiveMessage);

        }
        server.disconnect();
        client.disconnect();


//        try (ZContext context = new ZContext()) {
//            ZMQ.Socket server = context.createSocket(SocketType.REQ);
//            server.bind(address);
//
//            ZMQ.Socket client = context.createSocket(SocketType.REP);
//            client.connect(address);
//
//            boolean send = server.send("Hello world".getBytes(ZMQ.CHARSET));
//            System.out.println("Message Sent : " + send);
//
//            byte[] recv = client.recv();
//            String message = new String(recv, ZMQ.CHARSET);
//            System.out.println("Message Received: " + message);
//            server.close();
//            client.close();
//            context.close();
//        }
    }

    public static class Server implements Connection {
        private ZMQ.Socket socket;

        private String address;
        private long keepAlive;

        public Server(String address) {
            this(address, 1000);
        }

        public Server(String address, long keepAlive) {
            this.address = address;
            this.keepAlive = keepAlive;
        }

        public synchronized boolean sendMessage(String message) {
            if (Objects.isNull(message) || message.isEmpty()) {
                return false;
            }
            byte[] rawMsg = message.getBytes(ZMQ.CHARSET);
            return this.socket.send(rawMsg);
        }

        public boolean connect() {
            ZContext context = new ZContext();
            this.socket = context.createSocket(SocketType.REQ);
//            this.socket.setTCPKeepAliveIdle(keepAlive);
            return this.socket.bind(address);
        }

        @Override
        public void disconnect() {
            if (Objects.nonNull(socket)) {
                this.socket.close();
            }
        }

    }

    public static class Client implements Connection {
        private ZContext context;
        private ZMQ.Socket socket;

        private String address;
        private long keepAlive;

        public Client(String address) {
            this(address, 1000);
        }

        public Client(String address, long keepAlive) {
            this.address = address;
            this.keepAlive = keepAlive;
        }


        @Override
        public boolean connect() {
            ZContext context = new ZContext();
            this.socket = context.createSocket(SocketType.REP);
//            this.socket.setTCPKeepAliveIdle(keepAlive);
            return this.socket.connect(address);
        }

        public synchronized String receiveMessage() {
            byte[] recv = this.socket.recv();
            if (Objects.isNull(recv)) {
                return null;
            }
            return new String(recv, ZMQ.CHARSET);
        }

        @Override
        public void disconnect() {
            if (Objects.nonNull(socket)) {
                this.socket.close();
            }
        }
    }
}
