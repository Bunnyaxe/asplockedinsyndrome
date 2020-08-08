package com.example.speakingandroid;

import androidx.appcompat.app.AppCompatActivity;
import helpers.MqttHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class WheelchairPage extends AppCompatActivity {

    MqttHelper mqttHelper;
    String message;
    Button buttonforward, buttonright, buttonleft, buttonstop;
    MqttAndroidClient client;
    String username = "nklaskcc";
    String password = "8gq5EEA-_VHP";
    String serverUri = "tcp://tailor.cloudmqtt.com:18084";
    String motortopic = "test/motor";
    String[] buttonArray = { "stop", "forward", "right", "left"};
    String check, motormessage, sentmessage;
    int x = 0, movement = 0;
    public static int active = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheelchair_page);

        buttonforward = (Button) findViewById(R.id.forward);
        buttonleft = (Button) findViewById(R.id.left);
        buttonright = (Button) findViewById(R.id.right);
        buttonstop = (Button) findViewById(R.id.stop);
        buttonstop.setSelected(true);
        check = buttonArray[x];


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

                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        }
        catch(MqttException e) {
            e.printStackTrace();
        }

    }
    private void startMqtt() {

        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.e("Wheelchair", mqttMessage.toString());
                message = mqttMessage.toString();
                if (active == 1) {
                    if (message.equalsIgnoreCase("up")) {
                        deselection();
                        if (x == 3) {
                            x = 0;
                            check = buttonArray[x];
                            checkSelection();
                        } else {
                            x = x + 1;
                            check = buttonArray[x];
                            checkSelection();
                        }
                    } else if (message.equalsIgnoreCase("left")) {
                        deselection();
                        if (x == 0 || x == 1) {
                            x = 3;
                            check = buttonArray[x];
                            checkSelection();
                        } else if (x == 2) {
                            x = 0;
                            check = buttonArray[x];
                            checkSelection();
                        } else if (x == 3) {
                            x = x - 1;
                            check = buttonArray[x];
                            checkSelection();
                        }
                    } else if (message.equalsIgnoreCase("right")) {
                        deselection();
                        if (x == 0 || x == 1) {
                            x = 2;
                            check = buttonArray[x];
                            checkSelection();
                        } else if (x == 3) {
                            x = 0;
                            check = buttonArray[x];
                            checkSelection();
                        } else if (x == 2) {
                            x = x + 1;
                            check = buttonArray[x];
                            checkSelection();
                        }
                    } else if (message.equalsIgnoreCase("blink")) {
                        if (check.equalsIgnoreCase("forward")) {
                            motormessage = "On";
                            try {
                                client.publish(motortopic, motormessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            movement = 1;
                            resetToStop();
                        } else if (check.equalsIgnoreCase("right")) {
                            motormessage = "Right";
                            try {
                                client.publish(motortopic, motormessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            movement = 1;
                            resetToStop();
                        } else if (check.equalsIgnoreCase("left")) {
                            motormessage = "Left";
                            try {
                                client.publish(motortopic, motormessage.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            movement = 1;
                            resetToStop();
                        } else if (check.equalsIgnoreCase("stop")) {
                            if (movement == 1) {
                                motormessage = "Off";
                                try {
                                    client.publish(motortopic, motormessage.getBytes(), 0, false);
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                                movement = 0;
                            } else {
                                motormessage = "Off";
                                try {
                                    client.publish(motortopic, motormessage.getBytes(), 0, false);
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                                openMainPage();
                            }
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
    public void onStart() {
        super.onStart();
        startMqtt();
    }

    public void onStop() {
        super.onStop();
        disconnect();
    }
    private void disconnect() {
        try{
            IMqttToken disconToken = mqttHelper.mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
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

    private void deselection() {
        buttonforward.setSelected(false);
        buttonleft.setSelected(false);
        buttonright.setSelected(false);
        buttonstop.setSelected(false);
        message = "";
    }
    private void checkSelection() {
        if (check.equalsIgnoreCase("stop"))
            buttonstop.setSelected(true);
        if (check.equalsIgnoreCase("forward"))
            buttonforward.setSelected(true);
        if (check.equalsIgnoreCase("left"))
            buttonleft.setSelected(true);
        if (check.equalsIgnoreCase("right"))
            buttonright.setSelected(true);
    }
    private void resetToStop() {
        check = "stop";
        x = 0;
        deselection();
        checkSelection();
    }
    private void openMainPage() {
        Intent intent = new Intent(this, SpeakingAndroid.class);
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

}
