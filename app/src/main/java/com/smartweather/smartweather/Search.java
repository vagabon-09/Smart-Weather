package com.smartweather.smartweather;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.Inflater;

public class Search extends AppCompatActivity {
EditText search_field;
ImageView micBtn;
private final int REQUEST_CODE_EXTRA_INPUT =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Removing status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Finding view through id
        getId();
        //Action on click keyboard search
        lisentEdit();
        //Add one click 
        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicMic();
            }
        });
    }

    private void clicMic() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,REQUEST_CODE_EXTRA_INPUT);
        try {
            startActivityForResult(intent,REQUEST_CODE_EXTRA_INPUT);
        }catch (Exception e){
            Log.d("Error Voice", "Mic Error:  "+e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXTRA_INPUT){
            if(resultCode == RESULT_OK && data!=null){
                ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                search_field.setText(Objects.requireNonNull(arrayList).get(0));

            }
        }
    }

    //Sending search value to MainActivity and listening search from keyboard
    private void lisentEdit() {
        search_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String search = search_field.getText().toString();
                if (search.isEmpty()){
                    search_field.setError("Please enter city name");
                }else{
                    search_field.setError(null);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("rj",search);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });
    }

    //Finding view through id
    private void getId() {
        search_field = findViewById(R.id.edit_search_field_id);
        micBtn = findViewById(R.id.mic_icon_btn);
    }
}