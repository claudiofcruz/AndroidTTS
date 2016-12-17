package br.com.fernandescruz.androidtts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TTSActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        View.OnClickListener {

    private TextToSpeech tts;
    private int REQUEST_TTS = 0;
    private int REQ_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        Button btnFalar = (Button)findViewById(R.id.btnFalar);
        Button btnEscutar = (Button)findViewById(R.id.btnEscutar);

        btnFalar.setOnClickListener(this);
        btnEscutar.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(
                TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, REQUEST_TTS);
    }

    @Override
    public void onInit(int status) {
        //Verificao se foi instalado com sucesso
        if (status == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.getDefault())==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.getDefault());
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro TTS", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        EditText etTexto = (EditText)findViewById(R.id.etTexto);
        String texto = etTexto.getText().toString();

        if (v.getId() == R.id.btnFalar){
            falar(texto);
        }else
        {
            escutar();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }else{
            TextView tvTexto = (TextView)findViewById(R.id.tvTexto);
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tvTexto.setText(result.get(0));
            }
        }
    }

    private void falar(String texto) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }


    private void escutar() {
        Intent intent = new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
