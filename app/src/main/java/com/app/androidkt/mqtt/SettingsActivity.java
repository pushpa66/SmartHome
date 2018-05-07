package com.app.androidkt.mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.UnsupportedEncodingException;

public class SettingsActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private PahoMqttClient pahoMqttClient;

    private EditText textMessage, subscribeTopic, unSubscribeTopic;
    private Button publishMessage, subscribe, unSubscribe, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pahoMqttClient = new PahoMqttClient();

        textMessage = (EditText) findViewById(R.id.textMessage);
        publishMessage = (Button) findViewById(R.id.publishMessage);

        subscribe = (Button) findViewById(R.id.subscribe);
        unSubscribe = (Button) findViewById(R.id.unSubscribe);

        subscribeTopic = (EditText) findViewById(R.id.subscribeTopic);
        subscribeTopic.setText(Constants.SUBSCRIBE_TOPIC);

        unSubscribeTopic = (EditText) findViewById(R.id.unSubscribeTopic);
        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textMessage.getText().toString().trim();
                if (!msg.isEmpty()) {
                    try {
                        pahoMqttClient.publishMessage(client, msg, 1, Constants.PUBLISH_TOPIC);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = subscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, topic, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        unSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = unSubscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {
                    try {
                        pahoMqttClient.unSubscribe(client, topic);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                goBack(v);
            }
        });

        Intent intent = new Intent(SettingsActivity.this, MqttMessageService.class);
        startService(intent);
    }

    private void goBack(View view)
    {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
