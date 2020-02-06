package com.example.testtranslate.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.testtranslate.App;
import com.example.testtranslate.R;
import com.example.testtranslate.YandexTranslate;
import com.example.testtranslate.db.HistoryResponse;
import com.example.testtranslate.interfaces.IFragment;
import com.example.testtranslate.models.LanguageResponse;
import com.example.testtranslate.models.TranslateBody;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private TextView textViewResultTranslate;
    private EditText editTextTranslate;
    private Spinner spinnerLanguage;
    private YandexTranslate mYandexTranslate;
    private TranslateBody mTranslateBody;
    private Map<String, String> mLanguagesHashMap = new HashMap<>();
    private List<String> languages = new ArrayList<>();
    private String selectedLanguage;
    private String selectedLang;
    private String selectedResponseInHistory;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public MainFragment() {
    }

    public MainFragment(String text) {
        selectedResponseInHistory = text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null) {
            textViewResultTranslate.setText(savedInstanceState.getString("TEXT_VIEW_STATE_KEY"));
            editTextTranslate.setText(savedInstanceState.getString("EDIT_TEXT_STATE_KEY"));
            spinnerLanguage.setSelection(savedInstanceState.getInt("SPINNER_STATE_KEY"));
        }

        mTranslateBody = new TranslateBody(getString(R.string.key_yandex_api), "", "");

        Toolbar toolbar = view.findViewById(R.id.fragment_main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle(getString(R.string.translator));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                if(getActivity() instanceof IFragment){
                    ((IFragment) getActivity()).setFragment(new HistoryFragment(), HistoryFragment.class.getSimpleName());
                }
            }
            return false;
        });

        textViewResultTranslate = view.findViewById(R.id.textView);
        editTextTranslate = view.findViewById(R.id.editText);
        if(selectedResponseInHistory != null){
            editTextTranslate.setText(selectedResponseInHistory);
            selectedResponseInHistory = null;
        }
        spinnerLanguage = view.findViewById(R.id.spinner);

        if(getActivity() != null) {
            Bundle arguments = getActivity().getIntent().getExtras();
            if (arguments != null) {
                LanguageResponse mLanguageResponse = arguments.getParcelable(LanguageResponse.class.getSimpleName());
                if (mLanguageResponse == null) throw new AssertionError();
                languages.addAll(mLanguageResponse.getValueLangs());
                mLanguagesHashMap = mLanguageResponse.langs;
            }


            ArrayAdapter<String> mLanguageAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item,
                    languages);
            mLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLanguage.setAdapter(mLanguageAdapter);
        }


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
                Toast.makeText(getContext(), "Выберете язык", Toast.LENGTH_SHORT).show();
            }
        });

        mYandexTranslate = YandexTranslate.getInstance();

        mDisposable.add(RxTextView.textChanges(editTextTranslate)
                .map(CharSequence::toString)
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(text -> !text.equals(""))
                .distinct()
                .subscribe(response -> {
                            translate();
                            saveToDB(response);
                        }, throwable -> Log.e(TAG, "Unable to get translate", throwable)
                ));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        mYandexTranslate.clearDisposable();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        TextView textView = this.textViewResultTranslate;
        EditText editText = this.editTextTranslate;
        Spinner spinner = this.spinnerLanguage;

        outState.putString("TEXT_VIEW_STATE_KEY", textView.getText().toString());
        outState.putString("EDIT_TEXT_STATE_KEY", editText.getText().toString());
        outState.putInt("SPINNER_STATE_KEY", spinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }


    private String getKeyToValue(Map<String, String> map , String value){
        for(Map.Entry entry : map.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey().toString();
        }
        return null;
    }

    private void translate(){
        mTranslateBody.setText(editTextTranslate.getText().toString());
        mTranslateBody.setLang(selectedLang);
        if(selectedLang != null){
            mYandexTranslate.setTranslateBody(mTranslateBody);
            mYandexTranslate.getResponse();
            mYandexTranslate.setListener(translateResponse -> {
                if(translateResponse!=null) {
                    String resultResponse = translateResponse.text.toString().replace("[", "").replace("]", "");
                    textViewResultTranslate.setText(resultResponse);
                }
            });
        } else {
            Toast.makeText(getContext(), "Выберете язык", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDB(String response){
        mDisposable.add(App.getHistoryDAO().insert(new HistoryResponse(response))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, throwable -> Log.e(TAG, "Unable to save in DB", throwable)));
    }

}
