package com.world.jd.disa;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

import com.airbnb.lottie.LottieAnimationView;
import com.vistrav.ask.Ask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 007;
    private SpeechRecognizer speechRecognizer;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextToSpeech textToSpeech;
    private TextView textView, textView2, header,HintTxt;
    private Intent intent;
    private CameraManager cameraManager;
    private String cameraID;
    private WifiManager wifiManager;
    WebView browse;
    LottieAnimationView animationView,peopleView;
    String Header;
    Button button;
    Cursor c;
    ImageView Border1,Border2;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ask.on(this).id(PERMISSION_REQUEST_CODE)
                .forPermissions(Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                        Manifest.permission.INTERNET,
                        READ_CONTACTS,CAMERA_SERVICE,ACCESS_WIFI_STATE,LOCATION_SERVICE,BLUETOOTH,ACCESS_COARSE_LOCATION,SEND_SMS,READ_SMS,RECEIVE_SMS)
                .withRationales("Need All Permisssion")
                .go();


        Header = "Say Something...";
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        final BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            cameraID = cameraManager.getCameraIdList()[0]; // 0 is for back camera and 1 is for front camera
        } catch (Exception e) {
            e.printStackTrace();
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //textToSpeech.speak(Header, TextToSpeech.QUEUE_FLUSH, null, null);

        animationView = findViewById(R.id.animationView);
        peopleView = findViewById(R.id.peoView);
        button = findViewById(R.id.button);


        browse = findViewById(R.id.webview);
        HintTxt=findViewById(R.id.hintTxt);
        Border1=findViewById(R.id.border1);
        Border2=findViewById(R.id.border2);
        browse.requestFocus();
        browse.getSettings().setLightTouchEnabled(true);
        browse.getSettings().setGeolocationEnabled(true);
        browse.getSettings().setJavaScriptEnabled(true);
        browse.getSettings().setDomStorageEnabled(true);
        browse.setWebChromeClient(new GeoWebChromeClient());

        header = findViewById(R.id.header);
        header.setText(Header);


        textView2 = findViewById(R.id.asstext);
        textView2.setVisibility(View.INVISIBLE);


        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        textView = findViewById(R.id.usertext);
        textView.setVisibility(View.INVISIBLE);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");
                HintTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string = "";
                textView.setText("");
                if (matches != null) {
                    string = matches.get(0);
                    textView.setText(string);
                    browse.setVisibility(View.INVISIBLE);
                    Border1.setVisibility(View.INVISIBLE);
                    HintTxt.setVisibility(View.INVISIBLE);
                    peopleView.setVisibility(View.INVISIBLE);
                    Border2.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.INVISIBLE);

                    if (string.equals("hello") || string.equals("hi") || string.contains("Neo")) {
                        createMethod();
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");

                    }
                    else if (string.equals("do you know me")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        knowMe();
                    }
                    else if (string.equals("who is Sanjay") || string.equals("do you know Sanjay")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Sanjay Sharma is the person who tested me from the begining", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Sanjay Sharma is the person who tested me from the begining");

                    }
                    else if (string.equals("who is pradyut") || string.equals("do you know pradyut")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("JD Pradyut is the person who developed me from scratch", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("JD Pradyut is the person who developed me from scratch");

                    }
                    else if (string.equals("who is Sayoni") || string.equals("do you know Sayoni")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Sayoni Pandit is the person who gave me this beautiful name,Neo", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Sayoni Pandit is the person who gave me this beautiful name,Neo.");

                    }
                    else if (string.equals("set a timer") || string.equals("set timer")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("What is the length in seconds ?", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("What is the length in seconds ?");
                        button.setVisibility(View.INVISIBLE);
                        animationView.setVisibility(View.VISIBLE);
                        Intent setLen = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        setLen.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                        SpeechRecognizer lenRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                        lenRecognizer.setRecognitionListener(new RecognitionListener() {
                            @Override
                            public void onReadyForSpeech(Bundle params) {
                                HintTxt.setVisibility(View.VISIBLE);
                                HintTxt.setText("Preparing to Listen...");

                            }

                            @Override
                            public void onBeginningOfSpeech() {
                                HintTxt.setText("Listening...");

                            }

                            @Override
                            public void onRmsChanged(float rmsdB) {

                            }

                            @Override
                            public void onBufferReceived(byte[] buffer) {

                            }

                            @Override
                            public void onEndOfSpeech() {
                                HintTxt.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onError(int error) {
                                button.setVisibility(View.VISIBLE);
                                animationView.setVisibility(View.INVISIBLE);
                                HintTxt.setVisibility(View.INVISIBLE);
                                header.setText("Try Again...");
                                textView2.setVisibility(View.INVISIBLE);
                                textView.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onResults(Bundle results) {
                                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                String getLen = "";
                                textView.setText("");
                                if (matches != null) {
                                    getLen = matches.get(0);
                                    textView.setText(getLen);
                                    textView.setVisibility(View.VISIBLE);
                                    header.setText("");
                                    int setLen = Integer.parseInt(getLen);
                                    Intent setTim = new Intent(AlarmClock.ACTION_SET_TIMER).putExtra(AlarmClock.EXTRA_LENGTH,setLen).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(setTim);


                                    textToSpeech.speak("Setting Timer...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Timer...");
                                    button.setVisibility(View.VISIBLE);
                                    animationView.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    textToSpeech.speak("Say it Properly...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Say it Properly...");
                                    button.setVisibility(View.VISIBLE);
                                    animationView.setVisibility(View.INVISIBLE);

                                }


                            }

                            @Override
                            public void onPartialResults(Bundle partialResults) {

                            }

                            @Override
                            public void onEvent(int eventType, Bundle params) {

                            }
                        });
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lenRecognizer.startListening(setLen);



                    }
                    else if (string.equals("who are you")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        about();
                    }
                    else if (string.equals("take a picture") || string.equals("open camera")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Opening Camera", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Opening Camera");
                        Intent openCam = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                        startActivityForResult(openCam,REQUEST_IMAGE_CAPTURE);

                    }
                    else if (string.equals("take a selfie")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Opening Camera", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Opening Camera");
                        Intent openCam = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).putExtra("android.intent.extras.CAMERA_FACING",1);
                        startActivityForResult(openCam,REQUEST_IMAGE_CAPTURE);

                    }
                    else if (string.equals("record a video")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Opening Camera", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Opening Camera");
                        Intent openCam = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                        startActivityForResult(openCam,REQUEST_IMAGE_CAPTURE);

                    }
                    else if(string.equals("send a WhatsApp message") || string.equals("send a message on WhatsApp")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textView2.setText("Say name");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Say name", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.INVISIBLE);
                        animationView.setVisibility(View.VISIBLE);
                        whtpNum();

                    }
                    else if (string.equals("set an alarm") || string.equals("set a alarm") || string.contains("set alarm")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        if (string.contains("at")){
                            String setTime = string.replace("set alarm at","").trim();
                            if (setTime.contains("a.m.")){
                                String realTime = setTime.replace("a.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 1;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 2;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 3;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 4;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 5;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 6;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 7;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 8;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 9;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 10;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 11;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 0;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 4;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 5;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 6;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 0;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else if (setTime.contains("p.m.")){
                                String realTime = setTime.replace("p.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 13;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }

                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 14;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 15;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 16;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 17;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 18;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 19;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 20;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 21;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 22;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 23;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 12;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 16;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 17;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 18;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 12;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else{
                                textToSpeech.speak("Say it properly", TextToSpeech.QUEUE_FLUSH, null, null);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("Say it properly");

                            }

                        }
                        else if (string.contains("to")){
                            String setTime = string.replace("set alarm to","").trim();
                            if (setTime.contains("a.m.")){
                                String realTime = setTime.replace("a.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 1;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 2;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 3;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 4;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 5;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 6;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 7;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 8;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 9;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 10;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 11;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 0;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 4;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 5;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 6;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 0;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else if (setTime.contains("p.m.")){
                                String realTime = setTime.replace("p.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 13;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }

                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 14;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 15;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 16;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 17;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 18;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 19;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 20;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 21;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 22;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 23;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 12;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 16;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 17;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 18;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 12;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else{
                                textToSpeech.speak("Say it properly", TextToSpeech.QUEUE_FLUSH, null, null);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("Say it properly");

                            }

                        }
                        else if (string.contains("for")){
                            String setTime = string.replace("set alarm for","").trim();
                            if (setTime.contains("a.m.")){
                                String realTime = setTime.replace("a.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 1;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 2;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 3;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 4;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 5;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 6;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 7;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 8;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 9;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 10;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 11;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 0;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 4;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 5;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 6;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 0;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else if (setTime.contains("p.m.")){
                                String realTime = setTime.replace("p.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 13;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }

                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 14;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 15;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 16;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 17;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 18;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 19;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 20;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 21;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 22;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 23;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 12;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 16;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 17;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 18;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 12;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else{
                                textToSpeech.speak("Say it properly", TextToSpeech.QUEUE_FLUSH, null, null);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("Say it properly");

                            }

                        }
                        else if (string.contains("on")){
                            String setTime = string.replace("set alarm on","").trim();
                            if (setTime.contains("a.m.")){
                                String realTime = setTime.replace("a.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 1;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 2;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 3;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 4;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 5;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 6;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 7;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 8;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 9;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 10;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 11;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 0;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 4;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 5;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 6;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 0;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else if (setTime.contains("p.m.")){
                                String realTime = setTime.replace("p.m.","").trim();
                                if (realTime.contains("1:")){
                                    String revHr = realTime.replace("1:","").trim();
                                    int hour = 13;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }

                                }
                                else if(realTime.contains("2:")){
                                    String revHr = realTime.replace("2:","").trim();
                                    int hour = 14;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("3:")){
                                    String revHr = realTime.replace("3:","").trim();
                                    int hour = 15;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("4:")){
                                    String revHr = realTime.replace("4:","").trim();
                                    int hour = 16;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("5:")){
                                    String revHr = realTime.replace("5:","").trim();
                                    int hour = 17;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("6:")){
                                    String revHr = realTime.replace("6:","").trim();
                                    int hour = 18;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("7:")){
                                    String revHr = realTime.replace("7:","").trim();
                                    int hour = 19;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("8:")){
                                    String revHr = realTime.replace("8:","").trim();
                                    int hour = 20;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("9:")){
                                    String revHr = realTime.replace("9:","").trim();
                                    int hour = 21;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("10:")){
                                    String revHr = realTime.replace("10:","").trim();
                                    int hour = 22;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("11:")){
                                    String revHr = realTime.replace("11:","").trim();
                                    int hour = 23;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if(realTime.contains("12:")){
                                    String revHr = realTime.replace("12:","").trim();
                                    int hour = 12;
                                    if (revHr.matches("")){
                                        int mint = 0;
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                    else {
                                        int mint = Integer.parseInt(revHr);
                                        Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                        startActivity(alarmSet);
                                        textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                        textView2.setVisibility(View.VISIBLE);
                                        textView2.setText("Setting Alarm...");

                                    }
                                }
                                else if (realTime.contains("4")){
                                    String revHr = realTime.replace("4","").trim();
                                    int hour = 16;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("5")){
                                    String revHr = realTime.replace("5","").trim();
                                    int hour = 17;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("6")){
                                    String revHr = realTime.replace("6","").trim();
                                    int hour = 18;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                                else if (realTime.contains("12")){
                                    String revHr = realTime.replace("12","").trim();
                                    int hour = 12;
                                    int mint = 0;
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");

                                }
                            }
                            else{
                                textToSpeech.speak("Say it properly", TextToSpeech.QUEUE_FLUSH, null, null);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("Say it properly");

                            }

                        }
                        else {
                            textView2.setText("Say hour");
                            textView2.setVisibility(View.VISIBLE);
                            textToSpeech.speak("Say hour", TextToSpeech.QUEUE_FLUSH, null, null);
                            button.setVisibility(View.INVISIBLE);
                            animationView.setVisibility(View.VISIBLE);
                            sAlarm();

                        }


                    }
                    else if (string.contains("near")){
                        browse.setVisibility(View.VISIBLE);
                        Border2.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Searching...", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Searching...");
                        URL url = null;
                        try {
                            url = new URL("https://www.google.com/maps/search/?api=1&query=" + string);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        browse.loadUrl(String.valueOf(url));

                    }
                    else if (string.contains("on YouTube")){

                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("Opening Youtube...", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("Opening Youtube...");
                        String string2 = string.replace("on YouTube", "");
                        string2.trim();
                        if (string2.contains("search")){
                            String yourl = string2.replace("search", "");
                            URL url = null;
                            try {
                                url = new URL("https://www.youtube.com/results?search_query=" + yourl);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            browse.loadUrl(String.valueOf(url));
                        }
                        else {
                            String yourl = string2.replace("search", "");
                            URL url = null;
                            try {
                                url = new URL("https://www.youtube.com/results?search_query=" + yourl);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            browse.loadUrl(String.valueOf(url));

                        }

                    }
                    else if (string.contains("Wi-Fi")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        if (string.contains("turn on")){
                            wifiManager.setWifiEnabled(true);
                            textToSpeech.speak("Turned ON", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Turned ON");

                        }
                        else if (string.contains("turn off")){
                            wifiManager.setWifiEnabled(false);
                            textToSpeech.speak("Turned OFF", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Turned OFF");

                        }
                        else {
                            textToSpeech.speak("Say it Properly", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Say it Properly");

                        }

                    }
                    else if (string.contains("Bluetooth")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        if (string.contains("turn on")){
                            if(bAdapter == null)
                            {
                                textToSpeech.speak("Bluetooth Not Supported", TextToSpeech.QUEUE_FLUSH, null, null);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText("Bluetooth Not Supported");
                            }
                            else {
                                if (!bAdapter.isEnabled()) {
                                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
                                    textToSpeech.speak("Turned ON", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Turned ON");
                                }
                            }


                        }
                        else if (string.contains("turn off")){
                            bAdapter.disable();
                            textToSpeech.speak("Turned OFF", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Turned OFF");

                        }
                        else {
                            textToSpeech.speak("Say it Properly", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Say it Properly");

                        }

                    }
                    else if (string.contains("torch") || string.contains("flash") || string.contains("flashlight")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        if (string.contains("turn on")){
                            try {
                                cameraManager.setTorchMode(cameraID, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            textToSpeech.speak("Turned ON", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Turned ON");

                        }
                        else if (string.contains("turn off")){
                            try {
                                cameraManager.setTorchMode(cameraID, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            textToSpeech.speak("Turned OFF", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Turned OFF");

                        }
                        else {
                            textToSpeech.speak("Say it Properly", TextToSpeech.QUEUE_FLUSH, null, null);
                            textView2.setVisibility(View.VISIBLE);
                            textView2.setText("Say it Properly");

                        }

                    }
                    else if (string.contains("call")) {
                        if (string.equals("call a number")){
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            textView2.setText("Say name");
                            textView2.setVisibility(View.VISIBLE);
                            textToSpeech.speak("Say name", TextToSpeech.QUEUE_FLUSH, null, null);
                            button.setVisibility(View.INVISIBLE);
                            animationView.setVisibility(View.VISIBLE);
                            callNum();
                        }
                        else {
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            String conString = string.replace("call", "");
                            conString = conString.trim();


                            String conName = conString.substring(0,1).toUpperCase()+conString.substring(1);
                            textView.setText("call "+conName);


                            if (conName.length() > 0) {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                if (c.getCount() > 0) {
                                    c.moveToFirst();
                                }
                            } else {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                c.move(position);
                                c.moveToNext();
                            }
                            if (c.getCount() == 0) {
                                textView2.setText("Not found");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);

                                return;
                            }
                            callContact(c);

                        }

                    }
                    else if (string.contains("SMS") || string.contains("sms")) {
                        if (string.equals("send a SMS") || string.equals("send a sms") ){
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            textView2.setText("Say name");
                            textView2.setVisibility(View.VISIBLE);
                            textToSpeech.speak("Say name", TextToSpeech.QUEUE_FLUSH, null, null);
                            button.setVisibility(View.INVISIBLE);
                            animationView.setVisibility(View.VISIBLE);
                            smsNum();
                        }
                        else if (string.contains("send a sms to")){
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            String conString = string.replace("send a sms to", "");
                            conString = conString.trim();
                            String conName = conString.substring(0,1).toUpperCase()+conString.substring(1);


                            if (conName.length() > 0) {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                if (c.getCount() > 0) {
                                    c.moveToFirst();
                                }
                            } else {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                c.move(position);
                                c.moveToNext();
                            }
                            if (c.getCount() == 0) {
                                textView2.setText("Not found");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);

                                return;
                            }
                            smsContact(c);

                        }
                        else if (string.contains("send a SMS to")){
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            String conString = string.replace("send a SMS to", "");
                            conString = conString.trim();
                            String conName = conString.substring(0,1).toUpperCase()+conString.substring(1);


                            if (conName.length() > 0) {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                if (c.getCount() > 0) {
                                    c.moveToFirst();
                                }
                            } else {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                c.move(position);
                                c.moveToNext();
                            }
                            if (c.getCount() == 0) {
                                textView2.setText("Not found");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);

                                return;
                            }
                            smsContact(c);

                        }
                        else if (string.contains("send SMS to")){
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            String conString = string.replace("send SMS to", "");
                            conString = conString.trim();
                            String conName = conString.substring(0,1).toUpperCase()+conString.substring(1);


                            if (conName.length() > 0) {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                if (c.getCount() > 0) {
                                    c.moveToFirst();
                                }
                            } else {
                                Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                                CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                                c = loader.loadInBackground();
                                c.move(position);
                                c.moveToNext();
                            }
                            if (c.getCount() == 0) {
                                textView2.setText("Not found");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);

                                return;
                            }
                            smsContact(c);

                        }
                        else {
                            textView.setVisibility(View.VISIBLE);
                            header.setText("");
                            textView2.setText("Say it Properly ");
                            textView2.setVisibility(View.VISIBLE);
                            textToSpeech.speak("Say it properly", TextToSpeech.QUEUE_FLUSH, null, null);

                        }
                    }
                    else if (string.contains("open")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        if (string.contains("YouTube")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("settings")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.settings");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("phone") || string.contains("contact")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("message")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.mms");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("Facebook")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("WhatsApp")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("Play Store")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("Chrome")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("Instagram")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                        else if(string.contains("Gmail")){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                                textView2.setText("Opening...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening...", TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                textView2.setText("Ooops ! Not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Ooops ! Not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }

                        else{
                            textView2.setText("Ooops ! Cant Open...");
                            textView2.setVisibility(View.VISIBLE);
                            textToSpeech.speak("Ooops ! Cant Open...", TextToSpeech.QUEUE_FLUSH, null, null);

                        }

                    }
                    else if (string.equals("what's the time now") || string.equals("what's the date today") || string.equals("what is the time now") || string.equals("what is the date today")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        String curTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                        textToSpeech.speak(curTime, TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText(curTime);

                    }
                    else if (string.contains("play")){
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        String contName = string.replace("play","").trim();
                        if (contName.contains("any song") || contName.contains("a song")|| contName.contains("song")){


                            String ctNm = "Blue Eyes";
                            Intent musicPlay = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,"vnd.android.cursor.item/*");
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_TITLE,ctNm);
                            musicPlay.putExtra("android.intent.extra.genre",ctNm);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_ARTIST,ctNm);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_ALBUM,ctNm);
                            musicPlay.putExtra(SearchManager.QUERY,ctNm);
                            musicPlay.setPackage("com.google.android.music");
                            if (musicPlay.resolveActivity(getPackageManager()) != null){
                                startActivity(musicPlay);
                                textView2.setText("Opening Music App...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening Music App...", TextToSpeech.QUEUE_FLUSH, null, null);

                            }
                            else {
                                textView2.setText("Play Music not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Play Music not installed...", TextToSpeech.QUEUE_FLUSH, null, null);

                            }

                        }
                        else {
                            Intent musicPlay = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,"vnd.android.cursor.item/*");
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_TITLE,contName);
                            musicPlay.putExtra("android.intent.extra.genre",contName);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_ARTIST,contName);
                            musicPlay.putExtra(MediaStore.EXTRA_MEDIA_ALBUM,contName);
                            musicPlay.putExtra(SearchManager.QUERY,contName);
                            musicPlay.setPackage("com.google.android.music");
                            if (musicPlay.resolveActivity(getPackageManager()) != null){
                                startActivity(musicPlay);
                                textView2.setText("Opening Music App...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Opening Music App...", TextToSpeech.QUEUE_FLUSH, null, null);

                            }
                            else {
                                textView2.setText("Play Music not installed...");
                                textView2.setVisibility(View.VISIBLE);
                                textToSpeech.speak("Play Music not installed...", TextToSpeech.QUEUE_FLUSH, null, null);

                            }

                        }

                    }
                    else if (string.contains("turn on") || string.equals("turn off")) {
                        textView.setVisibility(View.VISIBLE);
                        header.setText("");
                        textToSpeech.speak("I can't do this..", TextToSpeech.QUEUE_FLUSH, null, null);
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("I can't do this..");

                    }
                    else {
                        string = matches.get(0);
                        textView.setText(string);
                        Dontknow();
                        browse.setVisibility(View.VISIBLE);
                        Border1.setVisibility(View.VISIBLE);
                        Border2.setVisibility(View.VISIBLE);
                        URL url = null;
                        try {
                            url = new URL("https://www.google.com/search?q=" + string);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        browse.loadUrl(String.valueOf(url));



                        /*Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent); */
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
    }




    public void startButton(View view) {

        button.setVisibility(View.INVISIBLE);
        animationView.setVisibility(View.VISIBLE);
        HintTxt.setVisibility(View.INVISIBLE);
        peopleView.setVisibility(View.INVISIBLE);


        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechRecognizer.startListening(intent);

        header.setText("Listening...");
        textView2.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }
    public void repeatCmd(View view) {

        button.setVisibility(View.INVISIBLE);
        animationView.setVisibility(View.VISIBLE);
        HintTxt.setVisibility(View.INVISIBLE);
        peopleView.setVisibility(View.INVISIBLE);


        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        speechRecognizer.startListening(intent);

        header.setText("Say it Again...");
        textView2.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }
    public void callContact(Cursor c) {
        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        String number = "";
        if (phoneCursor.getCount() > 0) {
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }
        phoneCursor.close();

        textView2.setText("Calling...");
        textToSpeech.speak("Calling...", TextToSpeech.QUEUE_FLUSH, null, null);
        textView2.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        animationView.setVisibility(View.INVISIBLE);
        Intent callInt = new Intent(Intent.ACTION_CALL);
        callInt.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callInt);
    }
    public void whtpContact(Cursor c) {
        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        String number = "";
        if (phoneCursor.getCount() > 0) {
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }
        phoneCursor.close();
        Intent whtpIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        whtpIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer whtpRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        final String finalNumber = number;
        whtpRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String smsMsg = "";
                textView.setText("");
                if (matches != null) {
                    smsMsg = matches.get(0);
                    textView.setText(smsMsg);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");
                    PackageManager pm = MainActivity.this.getPackageManager();
                    try {
                        Intent waIntent = new Intent(Intent.ACTION_VIEW);
                        waIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+finalNumber+"&text="+smsMsg));
                        PackageInfo info = pm.getPackageInfo("com.whatsapp",PackageManager.GET_META_DATA);
                        waIntent.setPackage("com.whatsapp");
                        startActivity(waIntent);
                        textView2.setText("Sharing Message...");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Sharing Message...", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.INVISIBLE);

                    } catch (PackageManager.NameNotFoundException e) {
                        textView2.setText("Whatsapp not installed...");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Whatsapp not installed...", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        whtpRecognizer.startListening(whtpIntent);

    }
    public void smsContact(Cursor c){
        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        String number = "";
        if (phoneCursor.getCount() > 0) {
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }
        phoneCursor.close();

        Intent smsIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        smsIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer smsRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        final String finalNumber = number;
        smsRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String smsMsg = "";
                textView.setText("");
                if (matches != null) {
                    smsMsg = matches.get(0);
                    textView.setText(smsMsg);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);


                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(finalNumber,null, smsMsg, null, null);

                    textView2.setText("SMS Sent...");
                    textView2.setVisibility(View.VISIBLE);
                    textToSpeech.speak("SMS Sent...", TextToSpeech.QUEUE_FLUSH, null, null);
                    button.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.INVISIBLE);

                }
                else {
                    textView2.setText("Error...");
                    textView2.setVisibility(View.VISIBLE);
                    textToSpeech.speak("Error...", TextToSpeech.QUEUE_FLUSH, null, null);
                    button.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.INVISIBLE);

                }



            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        smsRecognizer.startListening(smsIntent);

    }


    //Comands
    private void whtpNum(){
        Intent nameWhtp = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        nameWhtp.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer nameWRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        nameWRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String whtpName = "";
                textView.setText("");
                if (matches != null) {
                    whtpName = matches.get(0);
                    String conName = whtpName.substring(0,1).toUpperCase()+whtpName.substring(1);
                    textView.setText(conName);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");


                    if (conName.length() > 0) {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                        }
                    } else {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        c.move(position);
                        c.moveToNext();
                    }
                    if (c.getCount() == 0) {
                        textView2.setText("Not found");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.INVISIBLE);

                        return;
                    }
                    textView2.setText("Say the message...");
                    textToSpeech.speak("Say the message...", TextToSpeech.QUEUE_FLUSH, null, null);
                    textView2.setVisibility(View.VISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                    whtpContact(c);
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nameWRecognizer.startListening(nameWhtp);



    }
    private void sAlarm() {
        Intent setHour = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        setHour.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer hourRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        hourRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String getHour = "";
                textView.setText("");
                if (matches != null) {
                    getHour = matches.get(0);
                    textView.setText(getHour);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");
                    textView2.setText("Say minutes");
                    textToSpeech.speak("Say minutes", TextToSpeech.QUEUE_FLUSH, null, null);
                    button.setVisibility(View.INVISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);

                    Intent setMin = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    setMin.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                    SpeechRecognizer minRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    final String finalGetHour = getHour;
                    minRecognizer.setRecognitionListener(new RecognitionListener() {
                        @Override
                        public void onReadyForSpeech(Bundle params) {
                            HintTxt.setVisibility(View.VISIBLE);
                            HintTxt.setText("Preparing to Listen...");

                        }

                        @Override
                        public void onBeginningOfSpeech() {
                            HintTxt.setText("Listening...");

                        }

                        @Override
                        public void onRmsChanged(float rmsdB) {

                        }

                        @Override
                        public void onBufferReceived(byte[] buffer) {

                        }

                        @Override
                        public void onEndOfSpeech() {
                            HintTxt.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onError(int error) {
                            button.setVisibility(View.VISIBLE);
                            animationView.setVisibility(View.INVISIBLE);
                            HintTxt.setVisibility(View.INVISIBLE);
                            header.setText("Try Again...");
                            textView2.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onResults(Bundle results) {
                            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                String getMin = "";
                                textView.setText("");
                                if (matches != null) {

                                    getMin = matches.get(0);
                                    textView.setText(getMin);
                                    textView.setVisibility(View.VISIBLE);
                                    header.setText("");
                                    int hour = Integer.parseInt(finalGetHour);
                                    int mint = Integer.parseInt(getMin);
                                    Intent alarmSet = new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR,hour).putExtra(AlarmClock.EXTRA_MINUTES,mint).putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                                    startActivity(alarmSet);
                                    textToSpeech.speak("Setting Alarm...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Setting Alarm...");
                                    button.setVisibility(View.VISIBLE);
                                    animationView.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    textToSpeech.speak("Something happens wrong...", TextToSpeech.QUEUE_FLUSH, null, null);
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText("Something happens wrong...");
                                    button.setVisibility(View.VISIBLE);
                                    animationView.setVisibility(View.INVISIBLE);

                                }
                        }

                        @Override
                        public void onPartialResults(Bundle partialResults) {

                        }

                        @Override
                        public void onEvent(int eventType, Bundle params) {

                        }
                    });
                    try {
                        Thread.sleep(1300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    minRecognizer.startListening(setMin);


                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hourRecognizer.startListening(setHour);

    }
    private void smsNum(){
        Intent nameIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        nameIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer nameRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        nameRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String smsName = "";
                textView.setText("");
                if (matches != null) {
                    smsName = matches.get(0);
                    String conName = smsName.substring(0,1).toUpperCase()+smsName.substring(1);
                    textView.setText(conName);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");


                    if (conName.length() > 0) {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                        }
                    } else {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        c.move(position);
                        c.moveToNext();
                    }
                    if (c.getCount() == 0) {
                        textView2.setText("Not found");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.INVISIBLE);

                        return;
                    }
                    textView2.setText("Say the message...");
                    textToSpeech.speak("Say the message...", TextToSpeech.QUEUE_FLUSH, null, null);
                    textView2.setVisibility(View.VISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                    smsContact(c);
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nameRecognizer.startListening(nameIntent);


    }
    private void callNum(){
        Intent callIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        callIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SpeechRecognizer calleeRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        calleeRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                HintTxt.setVisibility(View.VISIBLE);
                HintTxt.setText("Preparing to Listen...");

            }

            @Override
            public void onBeginningOfSpeech() {
                HintTxt.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                HintTxt.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int error) {
                button.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.INVISIBLE);
                HintTxt.setVisibility(View.INVISIBLE);
                header.setText("Try Again...");
                textView2.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String callName = "";
                textView.setText("");
                if (matches != null) {
                    callName = matches.get(0);
                    String conName = callName.substring(0,1).toUpperCase()+callName.substring(1);
                    textView.setText(conName);
                    textView.setVisibility(View.VISIBLE);
                    header.setText("");


                    if (conName.length() > 0) {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, ContactsContract.Contacts.DISPLAY_NAME + "='" + conName + "'", null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        if (c.getCount() > 0) {
                            c.moveToFirst();
                        }
                    } else {
                        Uri contacts = ContactsContract.Contacts.CONTENT_URI;
                        CursorLoader loader = new CursorLoader(getApplicationContext(), contacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " asc");
                        c = loader.loadInBackground();
                        c.move(position);
                        c.moveToNext();
                    }
                    if (c.getCount() == 0) {
                        textView2.setText("Not found");
                        textView2.setVisibility(View.VISIBLE);
                        textToSpeech.speak("Not found", TextToSpeech.QUEUE_FLUSH, null, null);
                        button.setVisibility(View.VISIBLE);
                        animationView.setVisibility(View.INVISIBLE);

                        return;
                    }
                    callContact(c);


                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        calleeRecognizer.startListening(callIntent);

    }
    private void about(){
        String abouttxt = "Hi..I am Neo,your voice assistent,How can I help you?";
        textToSpeech.speak(abouttxt, TextToSpeech.QUEUE_FLUSH, null, null);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText(abouttxt);

    }
    private void createMethod(){
        textToSpeech.speak("Hi,I am Neo...How can I help you?", TextToSpeech.QUEUE_FLUSH, null, null);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText("Hi,I am Neo...How can I help you?");

    }
    private void Dontknow(){
        header.setText("");

        textView.setVisibility(View.VISIBLE);

        textToSpeech.speak("Searching Google...", TextToSpeech.QUEUE_FLUSH, null, null);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText("Searching Google...");
    }
    private void knowMe(){
        textToSpeech.speak("Yes,You are my Boss", TextToSpeech.QUEUE_FLUSH, null, null);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText("Yes,You are my Boss");

    }

    public void viewSettings(View view) {
        Intent sendSet = new Intent(getApplicationContext(),Settings.class);
        startActivity(sendSet);
    }

    public void viewBoxCt(View view) {
        Intent sendBox = new Intent(getApplicationContext(),BoxContent.class);
        startActivity(sendBox);
    }
}