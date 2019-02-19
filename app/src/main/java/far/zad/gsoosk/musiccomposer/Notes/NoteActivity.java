package far.zad.gsoosk.musiccomposer.Notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import far.zad.gsoosk.musiccomposer.R;
import far.zad.gsoosk.musiccomposer.Stream.BluetoothActivity;
import far.zad.gsoosk.musiccomposer.Stream.BluetoothConnectionService;

public class NoteActivity  extends AppCompatActivity {

    public static final int INITIAL_BUTTON = 2;
    public static final int YEK_LA_CHANG_WIDTH = 140;
    private ArrayList<Note> notes  = new ArrayList<Note>();
    private NoteButton selectedBtn = null;
    private SoundPool sp;
    private int[] mainSounds = new int[23];
    private int[] secondarySounds = new int[4];
    private int kookSound;
    private boolean isPlaying = false;
    private int time;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);



        setNoteBarListener();
        addNoteLine();
        setInputViewNoteBase();
        handleUndoBtn();
        createSoundPool();
        handlePlayBtn();
        setBaseTime(MetronomeActivity.miliTime);
        handleSaveBtn();
        handleLoadBtn();




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.release();
    }
    public void setBaseTime(int x)
    {
        time = x ;
        ImageView blueImg =(ImageView) findViewById(R.id.blue_image);
        if(BluetoothConnectionService.getMainConnection() != null)
            blueImg.setVisibility(View.VISIBLE);
    }

    public int dps(int x)
    {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (x * scale + 0.5f);
        return pixels;
    }
    public void addNoteLine()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scroll);

        LineView newView = new LineView(getBaseContext());
        newView.setNoteBase(selectedBtn.getIndex());
        linearLayout.addView(newView, new LinearLayout.LayoutParams(dps(YEK_LA_CHANG_WIDTH), LinearLayout.LayoutParams.MATCH_PARENT));

        newView.setLineViewListener(new LineView.LineViewListener() {
            @Override
            public void onNoteAdded(Note note, boolean editing) {

                playNote(note);
                if(!editing)
                {
                    notes.add(note);
                    addNoteLine();
                    getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_RIGHT);
                        }
                    });
                }

            }
        });

    }

    public void setNoteBarListener()
    {
        LinearLayout noteBar = (LinearLayout) findViewById(R.id.note_bar);
        initSelected((NoteButton) noteBar.getChildAt(INITIAL_BUTTON));

        for(int i = 0 ; i < 10 ; i++ )
        {
            final NoteButton btn = (NoteButton) noteBar.getChildAt(i);
            final int base = i;

            btn.setIndex(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean sel = btn.getSelected();
                    if(!sel)
                    {
                        btn.setSelected(true);
                        selectedBtn.setSelected(false);
                        selectedBtn = btn;
                        setInputViewNoteBase();
                    }
                }
            });
        }


    }
    public void initSelected(NoteButton btn)
    {
        btn.setSelected(true);
        btn.setIndex(INITIAL_BUTTON);
        selectedBtn = btn;
    }
    public void setInputViewNoteBase()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
        LineView view = (LineView) linearLayout.getChildAt(linearLayout.getChildCount() - 1);
        view.setNoteBase(selectedBtn.getIndex());
    }
    public void handleUndoBtn()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.undo_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
                if(linearLayout.getChildCount() > 1)
                {
                    //Remove last note
                    linearLayout.removeViewAt(linearLayout.getChildCount() - 2);
                    notes.remove(notes.size() - 1);

                }
            }
        });
    }
    public void createSoundPool()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else
        {
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        mainSounds[0] = sp.load(getBaseContext(), R.raw.ll9, 1);
        mainSounds[1] = sp.load(getBaseContext(), R.raw.ll8, 1);
        mainSounds[2] = sp.load(getBaseContext(), R.raw.ll7, 1);
        mainSounds[3] = sp.load(getBaseContext(), R.raw.ll6, 1);
        mainSounds[4] = sp.load(getBaseContext(), R.raw.ll5, 1);
        mainSounds[5] = sp.load(getBaseContext(), R.raw.ll4, 1);
        mainSounds[6] = sp.load(getBaseContext(), R.raw.ll3, 1);
        mainSounds[7] = sp.load(getBaseContext(), R.raw.ll2, 1);
        mainSounds[8] = sp.load(getBaseContext(), R.raw.l8, 1);
        mainSounds[9] = sp.load(getBaseContext(), R.raw.l7, 1);
        mainSounds[10] = sp.load(getBaseContext(), R.raw.l6, 1);
        mainSounds[11] = sp.load(getBaseContext(), R.raw.l5, 1);
        mainSounds[12] = sp.load(getBaseContext(), R.raw.l4, 1);
        mainSounds[13] = sp.load(getBaseContext(), R.raw.l3, 1);
        mainSounds[14] = sp.load(getBaseContext(), R.raw.l2, 1);
        mainSounds[15] = sp.load(getBaseContext(), R.raw.r8, 1);
        mainSounds[16] = sp.load(getBaseContext(), R.raw.r7, 1);
        mainSounds[17] = sp.load(getBaseContext(), R.raw.r6, 1);
        mainSounds[18] = sp.load(getBaseContext(), R.raw.r5, 1);
        mainSounds[19] = sp.load(getBaseContext(), R.raw.r4, 1);
        mainSounds[20] = sp.load(getBaseContext(), R.raw.r3, 1);
        mainSounds[21] = sp.load(getBaseContext(), R.raw.r2, 1);
        mainSounds[22] = sp.load(getBaseContext(), R.raw.r1, 1);

        secondarySounds[0] = sp.load(getBaseContext(), R.raw.l9, 1);
        secondarySounds[1] = sp.load(getBaseContext(), R.raw.ll1, 1);
        secondarySounds[2] = sp.load(getBaseContext(), R.raw.r9, 1);
        secondarySounds[3] = sp.load(getBaseContext(), R.raw.l1, 1);


        kookSound = sp.load(getBaseContext(), R.raw.kook_kardan, 1);
        sp.play(kookSound, 1, 1, 0, 0, 1);

    }
    public void playNote(Note note)
    {
        if(note == null)
            return;
        if(note.getKind() >= 6 && note.getKind() <=  9)
            return ;
        if(note.getNoteNumber() == 7 && note.getKharak() == 9)
            sp.play(secondarySounds[0], 1, 1, 0, 0, 1);
        else if(note.getNoteNumber() == 8 && note.getKharak() == 1)
            sp.play(secondarySounds[1], 1, 1, 0, 0, 1);
        else if(note.getNoteNumber() == 14 && note.getKharak() == 9)
            sp.play(secondarySounds[2], 1, 1, 0, 0, 1);
        else if(note.getNoteNumber() == 15 && note.getKharak() == 1)
            sp.play(secondarySounds[3], 1, 1, 0, 0, 1);
        else
            sp.play(mainSounds[note.getNoteNumber()], 1, 1, 0, 0, 1);
    }
    public void handlePlayBtn()
    {
        final ImageButton playBtn = (ImageButton) findViewById(R.id.play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = !isPlaying;
                if(isPlaying)
                {
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_button));
                    play();
                }
                else
                {
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_button));
                }

            }
        });



    }
    public void play()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
                boolean twoHand = false;
                for(int i = 0 ; i < notes.size() ; i++)
                {
                    if(!isPlaying)
                        break;
                    Note note = (( LineView )linearLayout.getChildAt(i)).note;

                    if(note == null)
                        continue;

                    playNote(note);
                    (( LineView )linearLayout.getChildAt(i)).play();


                    sendBTData(note);

                    if(note.isWithTwoHand() && !twoHand) {
                        twoHand = true;
                        continue;
                    }

                    try{
                        Thread.sleep(notes.get(i).getSleepTime(time));
                    }
                    catch (Exception ex)
                    {

                    }
                    (( LineView )linearLayout.getChildAt(i)).pause();
                    if(twoHand)
                    {
                        twoHand = false;
                        (( LineView )linearLayout.getChildAt(i-1)).pause();
                    }

                }
                ImageButton playBtn = (ImageButton) findViewById(R.id.play_btn);
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_button));
                isPlaying = false;


            }
        }).start();
    }

    public void sendBTData(Note note)
    {
        if(BluetoothConnectionService.getMainConnection() != null && note != null)
        {
            BluetoothConnectionService bluetoothService = BluetoothConnectionService.getMainConnection();
//            String msg = note.getKind() + ":" + note.getNoteNumber() + ":" +  note.getKharak();

            String msg = "S110000";
            bluetoothService.write(msg.getBytes(Charset.defaultCharset()));
        }
    }
    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 43;

    private void createFile( ) {
        String fileName = "SanturData";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String mimeType = "text/plain";
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                loadFile(uri);
            }
        }
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                saveFile(uri);
            }
        }
    }
    public void loadFile(Uri uri)
    {
        try {
            ParcelFileDescriptor pfd = this.getContentResolver().
                    openFileDescriptor(uri, "r");

            FileInputStream fileInputStream =
                    new FileInputStream(pfd.getFileDescriptor());
            String data = "";
            int content;
            while ((content = fileInputStream.read()) != -1) {
                // convert to char and display it
                data += (char) content;
            }

            loadNotesViaData(data);
            fileInputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadNotesViaData(String data)
    {
        String[] lines = data.split(System.getProperty("line.separator"));
        if(lines[0].equals("SANTUR"))
        {
            notes = new ArrayList<>();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
            linearLayout.removeAllViews();

            for(int i = 1 ; i < lines.length ; i++)
            {
                String[] notesData = lines[i].split(":");


                Note newNote = new Note(Integer.valueOf(notesData[1]),Integer.valueOf(notesData[0]));
                newNote.setKharak(Integer.valueOf(notesData[2]));

                notes.add(newNote);


            }
            addNotesView();
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.not_support_file), Toast.LENGTH_LONG).show();
        }
    }
    public void addNotesView()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);

        for(int i = 0 ; i < notes.size(); i++)
        {
            final LineView newView = new LineView(getBaseContext());
            newView.setNoteBase(notes.get(i).getKind());

            linearLayout.addView(newView, new LinearLayout.LayoutParams(dps(YEK_LA_CHANG_WIDTH), LinearLayout.LayoutParams.MATCH_PARENT));

            final LineView lastView = (LineView)  linearLayout.getChildAt(linearLayout.getChildCount());
            final int index = i;
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    newView.addNewNote(notes.get(index).getNoteNumber());
                    newView.setKharak(notes.get(index).getKharak());
                    newView.postInvalidate();
                    newView.setFromOutSide(true);
                }
            });
//
        }

        addNoteLine();


    }

    public void saveFile(Uri uri)
    {
        try {
            ParcelFileDescriptor pfd = this.getContentResolver().
                    openFileDescriptor(uri, "w");

            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());

            fileOutputStream.write((getNotesString()).getBytes());

            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String getNotesString()
    {
        String output = "SANTUR\n";
        for(int i = 0 ; i < notes.size() ; i++)
        {
            output += Integer.toString(notes.get(i).getKind()) + ":" + Integer.toString(notes.get(i).getNoteNumber() )+ ":" +
                    Integer.toString(notes.get(i).getKharak()) + "\n";
        }
        return output;
    }


    public void handleSaveBtn()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.save_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile();
            }
        });
    }
    public void handleLoadBtn()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.load_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });
    }
}

