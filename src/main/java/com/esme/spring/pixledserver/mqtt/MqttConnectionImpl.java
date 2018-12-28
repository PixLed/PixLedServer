package com.esme.spring.pixledserver.mqtt;

import com.esme.spring.pixledserver.model.Status;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttConnectionImpl implements MqttConnection {

    @Autowired
    MqttConnectionHandler mqttConnectionHandler;

    private IMqttClient client;
    private static final String clientId = "PixLedServer";

    public MqttConnectionImpl() {
    }

    @Override
    public void connect() {
        System.out.println("Connect mqtt");
        try {
            client = new MqttClient("tcp://localhost:1883", clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            client.subscribe(connected_topic, 1, mqttConnectionHandler);
            client.subscribe(connected_topic, 1, mqttConnectionHandler);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void publishColor(long buildingId, long roomId, long lightId, String color) {
        String topic = "/buildings/" + buildingId + "/rooms/" + roomId + "/lights/" + lightId + "/color";
        byte[] payload = color.getBytes();
        System.out.println("Publish " + payload + " to " + topic);
        MqttMessage msg = new MqttMessage(payload);
        msg.setQos(1);
        msg.setRetained(true);
        try {
            client.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishSwitch(long buildingId, long roomId, long lightId, Status status) {
        String topic = "/buildings/" + buildingId + "/rooms/" + roomId + "/lights/" + lightId + "/switch";
        byte[] payload = status.name().getBytes();
        System.out.println("Publish " + status.name() + " to " + topic);
        MqttMessage msg = new MqttMessage(payload);
        msg.setQos(1);
        msg.setRetained(true);
        try {
            client.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
