package com.example.speakingandroid;

import androidx.appcompat.app.AppCompatActivity;
import helpers.MqttHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class BedPage extends AppCompatActivity {

    MqttHelper mqttHelper;
    Button buttonUp, buttonDown, buttonReset, buttonBack;
    MqttAndroidClient client;
    String username = "nklaskcc";
    String password = "8gq5EEA-_VHP";
    String serverUri = "tcp://tailor.cloudmqtt.com:18084";
    String topic = "lockedin/bed";
    int x = 0, z = 0;
    String[] buttonArray = { "up", "reset", "down", "back" };
    String tempmessage = "", currentkey = "", sentmessage;
    public static int active = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_page);

        buttonUp = (Button) findViewById(R.id.up);
        buttonDown = (Button) findViewById(R.id.down);
        buttonReset = (Button) findViewById(R.id.original);
        buttonBack = (Button) findViewById(R.id.goBack2);

        buttonUp.setSelected(true);
        currentkey = buttonArray[z];

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), serverUri, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(BedPage.this, "Connected", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(BedPage.this, "Not Connected", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch(MqttException e) {
            e.printStackTrace();
        }

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inclineBed();
            }
        });
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reclineBed();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBed();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainPage();
            }
        });

    }

    private void resetBed() {
        x = 0;
        String message = String.valueOf(x);
        try {
            client.publish(topic,message.getBytes(), 0 ,false);
        } catch(MqttException e){
            e.printStackTrace();
        }
    }

    private void reclineBed() {
        if (x != 0)
        {
            x = x - 25;
            String message = String.valueOf(x);
            try {
                client.publish(topic,message.getBytes(), 0 ,false);
            } catch(MqttException e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(BedPage.this,"Minimum Angle Reached",Toast.LENGTH_LONG).show();
        }
    }

    private void inclineBed() {
        if (x != 75)
        {
            x = x + 25;
            String message = String.valueOf(x);
            try {
                client.publish(topic,message.getBytes(), 0 ,false);
            } catch(MqttException e){
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(BedPage.this,"Maximum Angle Reached",Toast.LENGTH_LONG).show();
        }
    }

    private void openDialog() {
        DialogMessage exampleDialog = new DialogMessage();
        Bundle bundle = new Bundle();
        bundle.putString("sentmessage", sentmessage);
        exampleDialog.setArguments(bundle);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void deSelection(){
        buttonBack.setSelected(false);
        buttonReset.setSelected(false);
        buttonUp.setSelected(false);
        buttonDown.setSelected(false);
        tempmessage = "";
    }

    private void checkSelection(){
        if (currentkey.equalsIgnoreCase("up"))
            buttonUp.setSelected(true);
        if (currentkey.equalsIgnoreCase("down"))
            buttonDown.setSelected(true);
        if (currentkey.equalsIgnoreCase("reset"))
            buttonReset.setSelected(true);
        if (currentkey.equalsIgnoreCase("back"))
            buttonBack.setSelected(true);
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
                Log.e("Bed", mqttMessage.toString());
                tempmessage = mqttMessage.toString();
                if (active == 1) {
                    if (tempmessage.equalsIgnoreCase("left") || tempmessage.equalsIgnoreCase("right") || tempmessage.equalsIgnoreCase("up")) {
                        deSelection();
                        if (z == 0) {
                            z = z + 3;
                        } else {
                            z = z - 1;
                        }
                        currentkey = buttonArray[z];
                        checkSelection();
                    } else if (tempmessage.equalsIgnoreCase("blink")) {
                        tempmessage = "";
                        if (currentkey.equalsIgnoreCase("up"))
                            inclineBed();
                        else if (currentkey.equalsIgnoreCase("down"))
                            reclineBed();
                        else if (currentkey.equalsIgnoreCase("reset"))
                            resetBed();
                        else if (currentkey.equalsIgnoreCase("back"))
                            openMainPage();
                    } else if (topic.equalsIgnoreCase("thirdeye/question")) {
                        Log.e("Sentence:", tempmessage);
                        sentmessage = tempmessage;
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
                    Log.e("Disconnected", tempmessage);
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
    }
    private void openMainPage() {
        Intent intent = new Intent(this, SpeakingAndroid.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //disconnect();
        startActivity(intent);
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
