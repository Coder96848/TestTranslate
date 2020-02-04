package com.example.testtranslate.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testtranslate.YandexLangs;
import com.example.testtranslate.models.LanguageResponse;
import com.example.testtranslate.models.TranslateBody;
import com.example.testtranslate.R;
import com.example.testtranslate.YandexTranslate;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = YandexLangs.class.getSimpleName();
    private TextView textViewForResultTranslate;
    private EditText editTextForTranslate;
    private Spinner spinnerLanguage;
    private YandexTranslate mYandexTranslate;
    private TranslateBody mTranslateBody;
    private Map<String, String> mLanguagesHashMap;
    private List<String> languages;
    private String selectedLanguage;
    private String selectedLang;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    private void Init(){
        languages = new ArrayList<>();
        mLanguagesHashMap = new HashMap<>();
        mTranslateBody = new TranslateBody(getString(R.string.key_yandex_api), "", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle(getString(R.string.translator));
        /*toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {

            }
            return false;
        });*/

        textViewForResultTranslate = findViewById(R.id.textView);
        editTextForTranslate = findViewById(R.id.editText);
        spinnerLanguage = findViewById(R.id.spinner);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            LanguageResponse mLanguageResponse = arguments.getParcelable(LanguageResponse.class.getSimpleName());
            if (mLanguageResponse == null) throw new AssertionError();
            languages.addAll(mLanguageResponse.getValueLangs());
            mLanguagesHashMap = mLanguageResponse.langs;
        }

        ArrayAdapter<String> mLanguageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                languages);
        mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(mLanguageAdapter);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = spinnerLanguage.getSelectedItem().toString();
                if(mLanguagesHashMap.containsValue(selectedLanguage)){
                    selectedLang = getKeyToValue(mLanguagesHashMap, selectedLanguage);
                    translate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mYandexTranslate = YandexTranslate.getInstance();

        mDisposable.add(RxTextView.textChanges(editTextForTranslate)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(text -> translate(),
                        throwable -> Log.e(TAG, "Unable to get translate", throwable)
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        mYandexTranslate.clearDisposable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
         TextView textView = this.textViewForResultTranslate;
         EditText editText = this.editTextForTranslate;
         Spinner spinner = this.spinnerLanguage;

         outState.putString("TEXT_VIEW_STATE_KEY", textView.getText().toString());
         outState.putString("EDIT_TEXT_STATE_KEY", editText.getText().toString());
         outState.putInt("SPINNER_STATE_KEY", spinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.textViewForResultTranslate.setText(savedInstanceState.getString("TEXT_VIEW_STATE_KEY"));
        this.editTextForTranslate.setText(savedInstanceState.getString("EDIT_TEXT_STATE_KEY"));
        this.spinnerLanguage.setSelection(savedInstanceState.getInt("SPINNER_STATE_KEY"));
    }

    private String getKeyToValue(Map<String, String> map , String value){
        for(Map.Entry entry : map.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey().toString();
        }
        return null;
    }

    private void translate(){
        mTranslateBody.setText(editTextForTranslate.getText().toString());
        mTranslateBody.setLang(selectedLang);
        if(selectedLang != null){
            mYandexTranslate.setTranslateBody(mTranslateBody);
            mYandexTranslate.getResponse();
            mYandexTranslate.setListener(translateResponse -> {
                if(translateResponse!=null) {
                    String resultResponse = translateResponse.text.toString().replace("[", "").replace("]", "");
                    textViewForResultTranslate.setText(resultResponse);
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Выберете язык", Toast.LENGTH_SHORT).show();
        }
    }
}
