package com.example.aman.hospitalappointy;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class Voice extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Doctor_Details");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView voiceInput;
    private TextView speakButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    List valid = Arrays.asList("Paracetamol", "paracetamol", "Dolo", "Benadryl", "Aspirin", "aspirin", "bioflu", "Strepsils", "Cope", "Vicks", "Crocin");
    public String[] send = new String[10];
    int count = 0;
    static int call=0;
    //InputStream is=getResources().openRawResource(R.raw.data);
    //BufferedReader reader=new BufferedReader(
    //      new InputStreamReader(is, Charset.forName("UTF-8"))
    //);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceInput = (TextView) findViewById(R.id.voiceInput);
        speakButton = (TextView) findViewById(R.id.btnSpeak);

        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askSpeechInput();
            }
        });
    }

    // Showing google speech input dialog

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String input = result.get(0);
                    String[] words = input.split("\\s+");
                    for (int i = 0; i < words.length; i++) {
                        // You may want to check for a non-word character before blindly
                        // performing a replacement
                        // It may also be necessary to adjust the character class
                        words[i] = words[i].replaceAll("[^\\w]", "");
                    }
                    for (int i = 0; i < words.length; i++) {
                        if (valid.contains(words[i])) {
                            //voiceInput.setText(words[i]);
                            send[count++]=words[i];
                        }
                    }
                }
            }
        }
        //voiceInput.setText(send[0]+send[1]+send[2]);
        sendMail();
    }
    private void sendMail() {

        String mail = mAuth.getCurrentUser().getEmail();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < send.length; i++) {
            if (send[i] == null)
                break;
            sb.append(send[i] + "\n");
        }
        String str = sb.toString();
        Date d1 = new Date();
        String message = "Name:"+mAuth.getCurrentUser().getDisplayName()+"\n" + "Doctor Details:"+mDatabase.toString()+ "\n" + "Tablets Prescribed:" + "\n" + str;
        String subject = "prescription dated " + d1;

        //Send Mail

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);

        javaMailAPI.execute();


    }

}

