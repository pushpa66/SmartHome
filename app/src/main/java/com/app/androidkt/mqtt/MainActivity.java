package com.app.androidkt.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;
    private BroadcastReceiver br;

    private ToggleButton toggleButtonBulb1, toggleButtonBulb2, toggleButtonFan;
    private TextView temperature, humidity, motion;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pahoMqttClient = new PahoMqttClient();

        toggleButtonBulb1 = (ToggleButton) findViewById(R.id.toggleButtonBulb1);
        toggleButtonBulb2 = (ToggleButton) findViewById(R.id.toggleButtonBulb2);
        toggleButtonFan = (ToggleButton) findViewById(R.id.toggleButtonFan);

        temperature = (TextView) findViewById(R.id.textViewTval);
        humidity = (TextView) findViewById(R.id.textViewHval);
        motion = (TextView) findViewById(R.id.textViewMval);

        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        toggleButtonBulb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    String msg = "Bulb1:ON";
                    publish(msg, Constants.PUBLISH_TOPIC);
                } else {
                    // The toggle is disabled
                    String msg = "Bulb1:OFF";
                    publish(msg, Constants.PUBLISH_TOPIC);
                }
            }
        });

        toggleButtonBulb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    String msg = "Bulb2:ON";
                    publish(msg, Constants.PUBLISH_TOPIC);
                } else {
                    // The toggle is disabled
                    String msg = "Bulb2:OFF";
                    publish(msg, Constants.PUBLISH_TOPIC);
                }
            }
        });

        toggleButtonFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String msg = "Fan:ON";
                    publish(msg, Constants.PUBLISH_TOPIC);
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    String msg = "Fan:OFF";
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

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    ((TextView) findViewById(R.id.textViewTval))
                            .setText(bundle.getString("T"));
                    ((TextView) findViewById(R.id.textViewHval))
                            .setText(bundle.getString("H"));
                    ((TextView) findViewById(R.id.textViewMval))
                            .setText(bundle.getString("M"));
                }
            }
        };

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(br, new IntentFilter("1"));
    }

//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//        unregisterReceiver(br);
//    }

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
            } catch (MqttException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
