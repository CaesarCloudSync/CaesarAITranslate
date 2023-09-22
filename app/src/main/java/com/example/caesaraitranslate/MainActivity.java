package com.example.caesaraitranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.os.Bundle;
import android.view.KeyEvent;
import android.Manifest;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }


    SpeechRecognizer speechRecognizer;
    TextToSpeech speech;
    int currentKeyCode;
    private void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
    public List<String> load_language_codes(Context context, String filename) {
        String json = null;
        try {
           InputStream is = context.getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            //Log.d("result",json);


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        JSONObject json_result = null;
        JSONArray language_codes = null;
        List<String> allcodes = new ArrayList<String>();

        try {
            json_result = new JSONObject(json);
            language_codes = json_result.getJSONArray("language_codes");
            for (int i=0; i<language_codes.length(); i++) {
                String code = language_codes.get(i).toString();

                allcodes.add(code);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return allcodes;

    }

    public Spinner getSpinner(int spinnerid ){
        Spinner  spinner = (Spinner) findViewById(spinnerid);
        return  spinner;

    }
    public void setSelectBar(Context context,Spinner spinner,List<String> language_codes){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item, language_codes);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#08073E");
        Log.d("myTag","Hi");
        setContentView(R.layout.activity_main);
        caesarRequest caesarRequest = new caesarRequest();
        String url = "https://caesarmobtranslate.fly.dev";
        String transcribeurl = "https://palondomus-caesaraitranscribetl.hf.space";
        caesarRequest.getRequest(this,url,"translateurl");
        caesarRequest.getRequest(this,transcribeurl,"transcribeurl");




    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }


        boolean val = SpeechRecognizer.isRecognitionAvailable(this);

        Log.d("Mic works",String.valueOf(val));

        List<String> language_codes =  load_language_codes(this,"language_codes_list.json");
        Log.d("testmn", language_codes.toString());
        Spinner spinner1 = getSpinner(R.id.spinner1);
        Spinner spinner2 = getSpinner(R.id.spinner2);
        Log.d("tagl", language_codes.toString());
        setSelectBar(this,spinner1,language_codes);
        List<String> language_codes1 = new ArrayList<String>(language_codes);
        String english_code = language_codes.get(1);
        language_codes1.remove(english_code);
        language_codes1.add(0,english_code);
        setSelectBar(this,spinner2,language_codes1);


        //String text = spinner1.getSelectedItem().toString();
        //Log.d("tagl",text);



        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
                //
               ImageView imageidview =  (ImageView) findViewById(R.id.mic);
               imageidview.setImageResource(R.drawable.green_mic);



            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {



            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ImageView imageidview =  (ImageView) findViewById(R.id.mic);
                imageidview.setImageResource(R.drawable.dark_grey);
                ArrayList<String> data = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                int keyCode = getKeyCode();
                int textid  = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? R.id.textView3 : keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ? R.id.textView2 : R.id.textView3;
                int textidother  = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? R.id.textView2 : keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ? R.id.textView3 : R.id.textView2;

                int spinnerid  = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? R.id.spinner2 : keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ? R.id.spinner1 : R.id.spinner2;
                Spinner spinner = getSpinner(spinnerid);
                String languagestring = spinner.getSelectedItem().toString();
                String language = languagestring.split("-")[1].strip();

                Log.d("currentkeycode", String.valueOf(keyCode));
                TextView mTextView = (TextView) findViewById(textid);
                TextView mTextViewOther = (TextView) findViewById(textidother);
                String url = "https://caesarmobtranslate.fly.dev/caesarmobiletranslate";
                String text = data.get(0);
                caesarRequest caesarRequestobj = new caesarRequest();
                speech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            Locale locale = new Locale(language);
                            Log.d("locale",locale.toString());

                            speech.setLanguage(locale);
                        }
                    }
                });

                caesarRequestobj.sendJsonPostRequest(MainActivity.this,url,text,language,mTextView,mTextViewOther,speech);
                //mTextView.setText(data.get(0));

                Log.d("Speech Recog",data.get(0));

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        Log.d("startObj",speechRecognizer.toString());

    }


    public SpeechRecognizer getSpeechRecognizer() {
        Log.d("Obj", this.speechRecognizer.toString());
        return this.speechRecognizer;
        //
    }
    public Intent getSpeechRecognizerIntent (String language){
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,language); // Locale.getDefault()

        return speechRecognizerIntent;
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted Microphone", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT);

            }
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            SpeechRecognizer speechRecognizer = getSpeechRecognizer();
            Log.d("hem","jaaka");
            speechRecognizer.stopListening();




            return true;
        }

        return super.onKeyDown(keyCode, event);

    }
    public void setKeyCode(int keyCode){
        currentKeyCode = keyCode;
    }
    public int getKeyCode(){
        return this.currentKeyCode;
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event){
        Log.d("code", String.valueOf(keyCode));
        int spinnerid  = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? R.id.spinner1 : keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ? R.id.spinner2 : R.id.spinner1;
        setKeyCode(keyCode);

        Spinner spinner = getSpinner(spinnerid);
        String languagestring = spinner.getSelectedItem().toString();
        String language = languagestring.split("-")[1].strip();




        if (language != ""){
            Log.d("myTag",String.format("Start Translating %s ...",language));
            SpeechRecognizer speechRecognizer = getSpeechRecognizer();

            Intent speechrecognizerIntent = getSpeechRecognizerIntent(language);
            speechRecognizer.startListening(speechrecognizerIntent);
            return true;
        }



        return super.onKeyLongPress(keyCode,event);
    }
}