package com.example.speakingandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;
import helpers.MqttHelper;

public class SpeakingAndroid extends AppCompatActivity {

    MqttHelper mqttHelper;
    Button bed, keyboard, wheelchair;
    String message = "", sentmessage;
    public static int active = 1;

    @Override

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        bed = (Button) findViewById(R.id.bed);
        keyboard = (Button) findViewById(R.id.keyboard);
        wheelchair = (Button) findViewById(R.id.wheelchair);
        bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBedPage();
            }
        });
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboardPage();
            }
        });
        wheelchair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openWheelchairPage();
            }
        });
        bed.setSelected(true);


    }

    private void openBedPage() {
        Intent intent = new Intent(this, BedPage.class);
        //disconnect();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openKeyboardPage() {
        Intent intent = new Intent(this, KeyboardPage.class);
        //disconnect();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openWheelchairPage() {
        Intent intent = new Intent(this, WheelchairPage.class);
        //disconnect();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void openDialog() {
        DialogMessage exampleDialog = new DialogMessage();
        Bundle bundle = new Bundle();
        bundle.putString("sentmessage", sentmessage);
        exampleDialog.setArguments(bundle);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.e("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    Log.e("Main", mqttMessage.toString());
                    message = mqttMessage.toString();
                    if (active == 1) {
                        if (message.equalsIgnoreCase("up") || message.equalsIgnoreCase("left") || message.equalsIgnoreCase("right")) {
                            if (bed.isSelected()) {
                                bed.setSelected(false);
                                keyboard.setSelected(false);
                                wheelchair.setSelected(true);
                                message = "";
                            } else if (keyboard.isSelected()) {
                                bed.setSelected(true);
                                keyboard.setSelected(false);
                                wheelchair.setSelected(false);
                                message = "";
                            } else if (wheelchair.isSelected()) {
                                bed.setSelected(false);
                                keyboard.setSelected(true);
                                wheelchair.setSelected(false);
                                message = "";
                            }
                        } else if (message.equalsIgnoreCase("blink")) {
                            if (bed.isSelected()) {
                                openBedPage();
                                message = "";
                            } else if (keyboard.isSelected()) {
                                openKeyboardPage();
                                message = "";
                            } else if (wheelchair.isSelected()) {
                                openWheelchairPage();
                                message = "";
                            }
                        } else if (topic.equalsIgnoreCase("thirdeye/question")) {
                            Log.e("Sentence:", message);
                            sentmessage = message;
                            active = 0;
                            openDialog();
                        }
                    }
                }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

    }
    private void disconnect() {
        try{

        IMqttToken disconToken = mqttHelper.mqttAndroidClient.disconnect();
        disconToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // we are now successfully disconnected
                Log.e("Disconnected", message);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken,
                                  Throwable exception) {
                // something went wrong, but probably we are disconnected anyway
            }
        });
    } catch (
    MqttException e) {
            Log.e("Error", e.toString());
    }
        finish();
}
    public void onStart() {
        super.onStart();
        startMqtt();
    }

    public void onStop() {
        super.onStop();
        disconnect();
    }

}