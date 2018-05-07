package com.app.androidkt.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;

    private ToggleButton toggleButtonBulb, toggleButtonDoor;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pahoMqttClient = new PahoMqttClient();

        toggleButtonBulb = (ToggleButton) findViewById(R.id.toggleButtonBulb);
        toggleButtonDoor = (ToggleButton) findViewById(R.id.toggleButtonDoor);

        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        toggleButtonBulb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    String msg = "Bulb:ON";
                    publish(msg, Constants.PUBLISH_TOPIC);
                } else {
                    // The toggle is disabled
                    String msg = "Bulb:OFF";
                    publish(msg, Constants.PUBLISH_TOPIC);
                }
            }
        });

        toggleButtonDoor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String msg = "Door:OPEN";
                    publish(msg, Constants.PUBLISH_TOPIC);
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    String msg = "Door:CLOSE";
                    publish(msg, Constants.PUBLISH_TOPIC);
                }
            }
        });

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                goToSettings(v);
            }
        });

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);
    }

    private void goToSettings(View view)
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void subscribeTopic(String topic){
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.subscribe(client, topic, 1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void unsubscribeTopic(String topic){
        if (!topic.isEmpty()) {
            try {
                pahoMqttClient.unSubscribe(client, topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void publish(String msg, String topic){
        if (!msg.isEmpty()) {
            try {
                pahoMqttClient.publishMessage(client, msg, 1, topic);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
