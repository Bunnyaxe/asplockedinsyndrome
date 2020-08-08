package com.example.speakingandroid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.Key;

import androidx.appcompat.app.AppCompatDialogFragment;
import helpers.MqttHelper;
import helpers.MqttHelper2;

public class DialogMessage extends AppCompatDialogFragment {

    String message, receivedmessage;
    MqttHelper mqttHelper;
    TextView caregiversmessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_caregivermessage, container, false);
        caregiversmessage = (TextView) view.findViewById(R.id.caregivermessage);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (getArguments() != null){
            receivedmessage = getArguments().getString("sentmessage");
            caregiversmessage.setText(receivedmessage);
        }

        return view;
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getActivity().getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.e("Dialog", mqttMessage.toString());
                message = mqttMessage.toString();
                if (topic.equalsIgnoreCase("thirdeye/question"))
                {
                    caregiversmessage.setText(message);
                }
                if (message.equalsIgnoreCase("blink")){

                    closeDialog();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

    }

    public void disconnect() {
        try{
            mqttHelper.mqttAndroidClient.close();
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

    public void onStart() {
        super.onStart();
        startMqtt();
    }
    public void onStop(){
        super.onStop();
        disconnect();
    }


    public void closeDialog(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                SpeakingAndroid.active = 1;
                BedPage.active = 1;
                WheelchairPage.active = 1;
                KeyboardPage.active = 1;
            }
        }, 250);


        getDialog().dismiss();
    }
}
