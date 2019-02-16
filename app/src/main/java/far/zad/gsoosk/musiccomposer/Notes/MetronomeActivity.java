package far.zad.gsoosk.musiccomposer.Notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import far.zad.gsoosk.musiccomposer.MainActivity;
import far.zad.gsoosk.musiccomposer.R;

public class MetronomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronome);

        final EditText num = (EditText) findViewById(R.id.number);
        num.setText(Integer.toString(time));
        Button set_btn = (Button) findViewById(R.id.set_btn);
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = Integer.valueOf(num.getText().toString());
                miliTime = 60000 / time;
                Toast.makeText(getBaseContext(), "Seted to " + Integer.toString(time), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public static int time = 90;
    public static int miliTime = 666;
}
