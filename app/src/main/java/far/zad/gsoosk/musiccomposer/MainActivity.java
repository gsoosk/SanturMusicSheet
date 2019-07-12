package far.zad.gsoosk.musiccomposer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import far.zad.gsoosk.musiccomposer.Notes.MetronomeActivity;
import far.zad.gsoosk.musiccomposer.Notes.NoteActivity;
import far.zad.gsoosk.musiccomposer.Stream.BluetoothActivity;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button music_btn = (Button) findViewById(R.id.music_btn);
        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NoteActivity.class);
                startActivity(intent);
            }
        });

        Button blue_btn = (Button) findViewById(R.id.blue_btn);
        blue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BluetoothActivity.class);
                startActivity(intent);

            }
        });

        Button metronome_btn = (Button) findViewById(R.id.metronome_btn);
        metronome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MetronomeActivity.class);
                startActivity(intent);
            }
        });

        Button about_btn = (Button) findViewById(R.id.about_btn);
        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}