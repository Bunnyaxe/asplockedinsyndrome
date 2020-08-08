package com.example.speakingandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import java.util.Locale;

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

import androidx.appcompat.app.AppCompatActivity;
import helpers.MqttHelper;

public class KeyboardPage extends AppCompatActivity implements OnClickListener, OnInitListener {

    MqttAndroidClient client;
    String username = "nklaskcc";
    String password = "8gq5EEA-_VHP";
    String serverUri = "tcp://tailor.cloudmqtt.com:18084";
    MqttHelper mqttHelper;
    String topics = "thirdeye/answer";
    String standardwords = "";
    String check = "";
    EditText finalSentence;
    Button keyA, keyB, keyC, keyD, keyE, keyF, keyG, keyH, keyI,
            keyJ, keyK, keyL, keyM, keyN, keyO, keyP, keyQ, keyR, keyT,
            keyY, keyV, keyX, keyW, keyZ, keyS, keyU, keySpace, keyEnter,
            keyBack, keyReturn, keyFood, keyWater, keyToilet;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    String message, current, sentence, newsentence, sentmessage;
    int x = 0, y = 0;
    public static int active = 1;
    String[][] keyArray = {
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l", "l"},
            {"z", "x", "c", "v", "b", "n", "m", "m", "m", "enter"},
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", "back"},
            {"food", "water", "toilet","toilet", "toilet", "toilet","toilet","toilet","toilet","goBack"},
    };

    @Override
    //create the Activity
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get a reference to the button element listed in the XML layout
        Button speakButton = (Button) findViewById(R.id.speak);
        //listen for clicks
        speakButton.setOnClickListener(this);

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

        keyA = (Button) findViewById(R.id.keyA);
        keyB = (Button) findViewById(R.id.keyB);
        keyC = (Button) findViewById(R.id.keyC);
        keyD = (Button) findViewById(R.id.keyD);
        keyE = (Button) findViewById(R.id.keyE);
        keyF = (Button) findViewById(R.id.keyF);
        keyG = (Button) findViewById(R.id.keyG);
        keyH = (Button) findViewById(R.id.keyH);
        keyI = (Button) findViewById(R.id.keyI);
        keyJ = (Button) findViewById(R.id.keyJ);
        keyK = (Button) findViewById(R.id.keyK);
        keyL = (Button) findViewById(R.id.keyL);
        keyO = (Button) findViewById(R.id.keyO);
        keyP = (Button) findViewById(R.id.keyP);
        keyR = (Button) findViewById(R.id.keyR);
        keyQ = (Button) findViewById(R.id.keyQ);
        keyW = (Button) findViewById(R.id.keyW);
        keyZ = (Button) findViewById(R.id.keyZ);
        keyX = (Button) findViewById(R.id.keyX);
        keyY = (Button) findViewById(R.id.keyY);
        keyS = (Button) findViewById(R.id.keyS);
        keyU = (Button) findViewById(R.id.keyU);
        keyV = (Button) findViewById(R.id.keyV);
        keyM = (Button) findViewById(R.id.keyM);
        keyN = (Button) findViewById(R.id.keyN);
        keyT = (Button) findViewById(R.id.keyT);
        keySpace = (Button) findViewById(R.id.keySpace);
        keyEnter = (Button) findViewById(R.id.speak);
        keyBack = (Button) findViewById(R.id.back);
        keyReturn = (Button) findViewById(R.id.goBack);
        keyFood = (Button) findViewById(R.id.food);
        keyWater = (Button) findViewById(R.id.water);
        keyToilet = (Button) findViewById(R.id.toilet);
        keyA.setClickable(false);
        keyB.setClickable(false);
        keyC.setClickable(false);
        keyD.setClickable(false);
        keyE.setClickable(false);
        keyF.setClickable(false);
        keyG.setClickable(false);
        keyH.setClickable(false);
        keyI.setClickable(false);
        keyJ.setClickable(false);
        keyK.setClickable(false);
        keyL.setClickable(false);
        keyM.setClickable(false);
        keyN.setClickable(false);
        keyO.setClickable(false);
        keyP.setClickable(false);
        keyQ.setClickable(false);
        keyR.setClickable(false);
        keyS.setClickable(false);
        keyT.setClickable(false);
        keyU.setClickable(false);
        keyV.setClickable(false);
        keyW.setClickable(false);
        keyX.setClickable(false);
        keyY.setClickable(false);
        keyZ.setClickable(false);
        keyEnter.setClickable(false);
        keySpace.setClickable(false);
        keyBack.setClickable(false);
        keyWater.setClickable(false);
        keyFood.setClickable(false);
        keyToilet.setClickable(false);

        deSelection();
        keyQ.setSelected(true);
        keyReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openMainPage();
            }
        });

        finalSentence = (EditText) findViewById(R.id.finalSentence);
        finalSentence.setEnabled(false);

        current = keyArray[y][x];



        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    public void onStart() {
        super.onStart();
        startMqtt();
    }

    public void onStop() {
        super.onStop();
        disconnect();
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
                Log.e("Keyboard", mqttMessage.toString());
                message = mqttMessage.toString();
                if (active == 1) {
                    if (message.equalsIgnoreCase("up")) {

                        if (y == 0) {
                            y = y + 4;
                            if (x == 9) {

                            } else {
                                x = x + 1;
                            }
                        } else {
                            y = y - 1;
                        }
                        current = keyArray[y][x];
                        check = current;
                        Log.e("Current Key Selected", current);
                        deSelection();
                        checkSelected();
                    } else if (message.equalsIgnoreCase("left")) {
                        if (x == 0) {
                            x = x + 9;
                        } else {
                            if (x == 8 && y == 2) {
                                x = x - 3;
                            } else if (x == 9 && y == 1) {
                                x = x - 2;
                            } else if (x == 9 && y == 3) {
                                x = x - 8;
                            } else if (x <= 9 && x >= 2 && y == 4) {
                                x = 1;
                            } else {
                                x = x - 1;
                            }
                        }
                        current = keyArray[y][x];
                        check = current;
                        Log.e("Current Key Selected", current);
                        deSelection();
                        checkSelected();
                    } else if (message.equalsIgnoreCase("right")) {
                        if (x == 9) {
                            x = x - 9;
                        } else {
                            if (x == 6 && y == 2) {
                                x = x + 3;
                            } else if (x == 8 && y == 1) {
                                x = x - 8;
                            } else if (x == 0 && y == 3) {
                                x = x + 8;
                            } else if (x >= 2 && x <= 8 && y == 4) {
                                x = 9;
                            } else {
                                x = x + 1;
                            }
                        }
                        current = keyArray[y][x];
                        check = current;
                        Log.e("Current Key Selected", current);
                        deSelection();
                        checkSelected();
                    } else if (message.equalsIgnoreCase("blink")) {

                        if (current.equalsIgnoreCase("enter")) {
                            String words = finalSentence.getText().toString();
                            speakWords(words);
                            try {
                                client.publish(topics, words.getBytes(), 0, false);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            finalSentence.setText("");
                            current = "";
                            message = "";
                        }
                        if (current.equalsIgnoreCase("back") || check.equalsIgnoreCase("back")) {
                            sentence = finalSentence.getText().toString();
                            newsentence = sentence.substring(0, sentence.length() - 1);
                            finalSentence.setText(newsentence);
                            message = "";
                            current = "";
                        }

                        if (current.equalsIgnoreCase("goBack")) {
                            message = "";
                            current = "";
                            openMainPage();
                        }
                        if (current.equalsIgnoreCase("food") || check.equalsIgnoreCase("food")) {
                            standardwords = "I want food";
                            speakWords(standardwords);
                            publishMqtt();
                            message = "";
                            current = "";
                        }
                        if (current.equalsIgnoreCase("water") || check.equalsIgnoreCase("water")) {
                            standardwords = "I want water";
                            speakWords(standardwords);
                            publishMqtt();
                            message = "";
                            current = "";
                        }
                        if (current.equalsIgnoreCase("toilet") || check.equalsIgnoreCase("toilet")) {
                            standardwords = "I want to go to the toilet";
                            speakWords(standardwords);
                            publishMqtt();
                            message = "";
                            current = "";
                        } else {
                            if (finalSentence.getText().toString().isEmpty()) {
                                finalSentence.setText(current);
                                message = "";
                                Log.e("Current Key Entered", current);
                            } else {
                                sentence = finalSentence.getText().toString();
                                newsentence = sentence + current;
                                finalSentence.setText(newsentence);
                                message = "";
                                Log.e("Spoken words:", finalSentence.getText().toString());
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

    private void openDialog() {

        DialogMessage exampleDialog = new DialogMessage();
        Bundle bundle = new Bundle();
        bundle.putString("sentmessage", sentmessage);
        exampleDialog.setArguments(bundle);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void openMainPage() {
        Intent intent = new Intent(this, SpeakingAndroid.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //disconnect();
        startActivity(intent);
    }

    public void deSelection() {
        keyA.setSelected(false);
        keyB.setSelected(false);
        keyC.setSelected(false);
        keyD.setSelected(false);
        keyE.setSelected(false);
        keyF.setSelected(false);
        keyG.setSelected(false);
        keyH.setSelected(false);
        keyI.setSelected(false);
        keyJ.setSelected(false);
        keyK.setSelected(false);
        keyL.setSelected(false);
        keyM.setSelected(false);
        keyN.setSelected(false);
        keyO.setSelected(false);
        keyP.setSelected(false);
        keyQ.setSelected(false);
        keyR.setSelected(false);
        keyS.setSelected(false);
        keyT.setSelected(false);
        keyU.setSelected(false);
        keyV.setSelected(false);
        keyW.setSelected(false);
        keyX.setSelected(false);
        keyY.setSelected(false);
        keyZ.setSelected(false);
        keyEnter.setSelected(false);
        keySpace.setSelected(false);
        keyBack.setSelected(false);
        keyReturn.setSelected(false);
        keyFood.setSelected(false);
        keyWater.setSelected(false);
        keyToilet.setSelected(false);
        message = "";
    }

    public void checkSelected() {
        if (current.equalsIgnoreCase("q"))
            keyQ.setSelected(true);
        if (current.equalsIgnoreCase("w"))
            keyW.setSelected(true);
        if (current.equalsIgnoreCase("e"))
            keyE.setSelected(true);
        if (current.equalsIgnoreCase("r"))
            keyR.setSelected(true);
        if (current.equalsIgnoreCase("t"))
            keyT.setSelected(true);
        if (current.equalsIgnoreCase("y"))
            keyY.setSelected(true);
        if (current.equalsIgnoreCase("u"))
            keyU.setSelected(true);
        if (current.equalsIgnoreCase("i"))
            keyI.setSelected(true);
        if (current.equalsIgnoreCase("o"))
            keyO.setSelected(true);
        if (current.equalsIgnoreCase("p"))
            keyP.setSelected(true);
        if (current.equalsIgnoreCase("a"))
            keyA.setSelected(true);
        if (current.equalsIgnoreCase("s"))
            keyS.setSelected(true);
        if (current.equalsIgnoreCase("d"))
            keyD.setSelected(true);
        if (current.equalsIgnoreCase("f"))
            keyF.setSelected(true);
        if (current.equalsIgnoreCase("g"))
            keyG.setSelected(true);
        if (current.equalsIgnoreCase("h"))
            keyH.setSelected(true);
        if (current.equalsIgnoreCase("j"))
            keyJ.setSelected(true);
        if (current.equalsIgnoreCase("k"))
            keyK.setSelected(true);
        if (current.equalsIgnoreCase("l"))
            keyL.setSelected(true);
        if (current.equalsIgnoreCase("z"))
            keyZ.setSelected(true);
        if (current.equalsIgnoreCase("x"))
            keyX.setSelected(true);
        if (current.equalsIgnoreCase("c"))
            keyC.setSelected(true);
        if (current.equalsIgnoreCase("v"))
            keyV.setSelected(true);
        if (current.equalsIgnoreCase("b"))
            keyB.setSelected(true);
        if (current.equalsIgnoreCase("n"))
            keyN.setSelected(true);
        if (current.equalsIgnoreCase("m"))
            keyM.setSelected(true);
        if (current.equalsIgnoreCase(" "))
            keySpace.setSelected(true);
        if (current.equalsIgnoreCase("enter"))
            keyEnter.setSelected(true);
        if (current.equalsIgnoreCase("back"))
            keyBack.setSelected(true);
        if (current.equalsIgnoreCase("goBack"))
            keyReturn.setSelected(true);
        if (current.equalsIgnoreCase("food"))
            keyFood.setSelected(true);
        if (current.equalsIgnoreCase("water"))
            keyWater.setSelected(true);
        if (current.equalsIgnoreCase("toilet"))
            keyToilet.setSelected(true);
    }

    //respond to button clicks
    public void onClick(View v) {

        //get the text entered
        String words = finalSentence.getText().toString();
        speakWords(words);
    }

    //speak the user text
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void publishMqtt(){
        try {
            client.publish(topics, standardwords.getBytes(), 0 ,false);
        } catch(MqttException e){
            e.printStackTrace();
        }
    }

    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
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
    }
}