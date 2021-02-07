package com.fahdisa.zmq;

public class PubSubSession implements Publisher, Subscriber {

    public PubSubSession() {

    }

    @Override
    public boolean send(String message) {
        return false;
    }

    @Override
    public boolean subscribe(Subscription subscription) {
        return false;
    }
}
