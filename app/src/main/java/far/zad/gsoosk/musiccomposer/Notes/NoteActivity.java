package far.zad.gsoosk.musiccomposer.Notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
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
    public static final int YEK_LA_CHANG_WIDTH = 160;
    public static final int ADD_WIDTH = 20;
    private ArrayList<Note> notes  = new ArrayList<Note>();
    private NoteButton selectedBtn = null;
    private SoundPool sp;
    private int[] mainSounds = new int[23];
    private int[] secondarySounds = new int[4];
    private int kookSound;
    private boolean isPlaying = false;
    private int time;
    private boolean exiting = false;



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
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);


        final LineView newView = new LineView(getBaseContext());
        newView.setNoteBase(selectedBtn.getIndex());
        linearLayout.addView(newView, new LinearLayout.LayoutParams(dps(YEK_LA_CHANG_WIDTH), LinearLayout.LayoutParams.MATCH_PARENT));

        setLineViewListeners(newView, true);



    }

    public void setLineViewListeners(final LineView newView, final boolean lastNote) {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
        newView.setLineViewDeleteListener(new LineView.LineViewDeleteListener() {
            @Override
            public void onViewDeleteCalled() {
                deletePairViewTwoHand(newView);
                linearLayout.removeView(newView);
            }
        });

        newView.setLineViewEditListener(new LineView.LineViewEditListener() {
            @Override
            public void onViewEditCalled() {
                deletePairViewTwoHand(newView);
            }
        });

        newView.setLineViewAddListener(new LineView.LineViewAddListener() {
            @Override
            public void onViewAddCalled() {
                if(newView.twoHandPair == LineView.PAIR.RIGHT && newView.note.withTwoHand) {
                    deletePairViewTwoHand(newView);
                    newView.setTwoHand(false);
                }
                int index = linearLayout.indexOfChild(newView) + 1;
                LineView view = new LineView(getBaseContext());
                view.setNoteBase(selectedBtn.getIndex());
                linearLayout.addView(view, index, new LinearLayout.LayoutParams(dps(YEK_LA_CHANG_WIDTH), LinearLayout.LayoutParams.MATCH_PARENT));
                setLineViewListeners(view, false);
            }
        });

        newView.setLineViewTwoHandListener(new LineView.LineViewTwoHandListener() {
            @Override
            public void onViewTwoHandCalled() {
                if(newView.note.withTwoHand) {
                    // Check if there was a second two hand after it
                    int indexOfRight = linearLayout.indexOfChild(newView) + 1;
                    LineView rightView = (LineView) linearLayout.getChildAt(indexOfRight);
                    if (rightView == null || rightView.note == null || rightView.note.withTwoHand)
                        newView.setTwoHand(false);
                    else {
                        newView.twoHandPair = LineView.PAIR.RIGHT;
                        rightView.twoHandPair = LineView.PAIR.LEFT;
                        rightView.setTwoHand(true);

                        if(rightView.note.getNoteNumber() > newView.note.getNoteNumber()) {
                            rightView.note.setHand(1);
                            newView.note.setHand(2);
                            rightView.inv();
                            newView.inv();
                        }
                        else {
                            rightView.note.setHand(2);
                            newView.note.setHand(1);
                            rightView.inv();
                            newView.inv();
                        }
                    }
                }
                else {
                    deletePairViewTwoHand(newView);
                }
            }
        });

        final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scroll);
        newView.setLineViewListener(new LineView.LineViewListener() {
            @Override
            public void onNoteAdded(Note note, boolean editing) {
                playNote(note);
                if(!editing && lastNote)
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
    public void deletePairViewTwoHand(LineView newView) {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
        LineView pairView = null;
        if(newView.twoHandPair == LineView.PAIR.RIGHT) {
            pairView = (LineView) linearLayout.getChildAt(
                    linearLayout.indexOfChild(newView) + 1
            );
        } else if(newView.twoHandPair == LineView.PAIR.LEFT) {
            pairView = (LineView) linearLayout.getChildAt(
                    linearLayout.indexOfChild(newView) - 1
            );
        }
        pairView.setTwoHand(false);
    }

    public void setNoteBarListener()
    {
        LinearLayout noteBar = (LinearLayout) findViewById(R.id.note_bar);
        initSelected((NoteButton) noteBar.getChildAt(INITIAL_BUTTON));

        for(int i = 0 ; i < 14 ; i++ )
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
        for(int i = 0 ; i < linearLayout.getChildCount(); i++)
        {
            LineView view = (LineView) linearLayout.getChildAt(i);
            if(view.editing || !view.isNoteSelected || view.note == null)
                view.setNoteBase(selectedBtn.getIndex());
        }

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
//                    notes.remove(notes.size() - 1);

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
        if(note.getKind() >= 6 && note.getKind() <=  11)
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
                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);
                final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scroll);
                ArrayList<Integer> repeatNotes = new ArrayList<>();

                boolean twoHand = false;
                for(int i = 0 ; i < linearLayout.getChildCount() ; i++)
                {
                    if(!isPlaying)
                        break;
                    Note note = (( LineView )linearLayout.getChildAt(i)).note;

                    if(note == null)
                        continue;

                    Log.d("NOTE NOM", Integer.toString(note.getNoteNumber()));

                    // repeat note
                    if(note.isRepeatStart())
                        continue;
                    if(note.isRepeatEnd() && note.repeated)
                        continue;
                    if(note.isRepeatEnd() && !note.repeated) {
                        int j = i;
                        boolean reverse = true;
                        while(reverse && j > 0) {
                            Note innerNote = (( LineView )linearLayout.getChildAt(j)).note;
                            if(innerNote.isRepeatStart()) {
                                innerNote.repeated = true;
                                reverse = false;
                                repeatNotes.add(j);
                                break;
                            }
                            j--;
                        }
                        repeatNotes.add(i);
                        i = j;
                        note.repeated = true;
                        continue;
                    }

                    playNote(note);
                    final LineView lineView = (( LineView )linearLayout.getChildAt(i));
                    lineView.play();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                           scrollView.smoothScrollTo(lineView.getLeft() - lineView.getWidth(), 0);
                        }
                    });




                    if(note.isWithTwoHand() && !twoHand) {
                        twoHand = true;
                        continue;
                    }
                    else if(!twoHand)
                        sendBTData(note);

                    boolean pause = false;
                    if(twoHand)
                    {
                        twoHand = false;
                        Note note2 = (( LineView )linearLayout.getChildAt(i-1)).note;
                        sendBTDataTwoHands(note2, note);
                        pause = true;
                    }
                    try{
                        Thread.sleep(note.getSleepTime(time));
                    }
                    catch (Exception ex)
                    {

                    }
                    (( LineView )linearLayout.getChildAt(i)).pause();
                    if(pause)
                    {
                        (( LineView )linearLayout.getChildAt(i-1)).pause();
                    }

                }
                for(int i = 0 ; i < repeatNotes.size() ; i++) {
                    Note note = (( LineView )linearLayout.getChildAt(repeatNotes.get(i))).note;
                    if(note == null)
                        continue;
                    note.repeated = false;

                }

                ImageButton playBtn = (ImageButton) findViewById(R.id.play_btn);
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_button));
                isPlaying = false;


            }
        }).start();
    }

    public void sendBTData(Note note)
    {
        if(BluetoothConnectionService.getMainConnection() != null && note != null && note.getKind() < 6)
        {
            BluetoothConnectionService bluetoothService = BluetoothConnectionService.getMainConnection();
            String msg = "S";
            String noteMsg = getNoteCode(note);
            if(note.getHand() == 1)
                msg += noteMsg + "0000";
            else
                msg += "00" + noteMsg + "00";
            bluetoothService.write(msg.getBytes(Charset.defaultCharset()));
        }
    }
    public void sendBTDataTwoHands(Note note1, Note note2)
    {
        if(BluetoothConnectionService.getMainConnection() != null && note1 != null && note2 != null && note1.getKind() < 6 )
        {
            BluetoothConnectionService bluetoothService = BluetoothConnectionService.getMainConnection();
            String msg = "S";
            if(note1.getHand() == 1)
                msg += getNoteCode(note1) + getNoteCode(note2) + "00";
            else
                msg += getNoteCode(note2) + getNoteCode(note1) + "00";

            bluetoothService.write(msg.getBytes(Charset.defaultCharset()));
        }
    }
    public String getNoteCode(Note note)
    {
        int noteNumber = note.getNoteNumber();
        int noteKharak = note.getKharak();
        int noteHand = note.getHand();
        if(noteNumber == 0 && noteHand == 1)
            return "95";
        else if(noteNumber == 0 && noteHand == 2)
            return "96";
        else if(noteNumber == 1 && noteHand == 1)
            return "85";
        else if(noteNumber == 1 && noteHand == 2)
            return "86";
        else if(noteNumber == 2 && noteHand == 1)
            return "75";
        else if(noteNumber == 2 && noteHand == 2)
            return "76";
        else if(noteNumber == 3 && noteHand == 1)
            return "65";
        else if(noteNumber == 3 && noteHand == 2)
            return "66";
        else if(noteNumber == 4 && noteHand == 1)
            return "55";
        else if(noteNumber == 4 && noteHand == 2)
            return "56";
        else if(noteNumber == 5 && noteHand == 1)
            return "45";
        else if(noteNumber == 5 && noteHand == 2)
            return "46";
        else if(noteNumber == 6 && noteHand == 1)
            return "35";
        else if(noteNumber == 6 && noteHand == 2)
            return "36";
        else if(noteNumber == 7 && noteHand == 1 && noteKharak == 2)
            return "25";
        else if(noteNumber == 7 && noteHand == 2 && noteKharak == 2)
            return "26";
        else if(noteNumber == 8 && noteHand == 1 && noteKharak == 1)
            return "15";
        else if(noteNumber == 8 && noteHand == 2 && noteKharak == 1)
            return "16";
        else if(noteNumber == 7 && noteHand == 1 && noteKharak == 9)
            return "93";
        else if(noteNumber == 7 && noteHand == 2 && noteKharak == 9)
            return "94";
        else if(noteNumber == 8 && noteHand == 1 && noteKharak == 8)
            return "83";
        else if(noteNumber == 8 && noteHand == 2 && noteKharak == 8)
            return "84";
        else if(noteNumber == 9 && noteHand == 1)
            return "73";
        else if(noteNumber == 9 && noteHand == 2)
            return "74";
        else if(noteNumber == 10 && noteHand == 1)
            return "63";
        else if(noteNumber == 10 && noteHand == 2)
            return "64";
        else if(noteNumber == 11 && noteHand == 1)
            return "53";
        else if(noteNumber == 11 && noteHand == 2)
            return "54";
        else if(noteNumber == 12 && noteHand == 1)
            return "43";
        else if(noteNumber == 12 && noteHand == 2)
            return "44";
        else if(noteNumber == 13 && noteHand == 1)
            return "33";
        else if(noteNumber == 13 && noteHand == 2)
            return "34";
        else if(noteNumber == 14 && noteHand == 1 && noteKharak == 2)
            return "23";
        else if(noteNumber == 14 && noteHand == 2 && noteKharak == 2)
            return "24";
        else if(noteNumber == 15 && noteHand == 1 && noteKharak == 1)
            return "13";
        else if(noteNumber == 15 && noteHand == 2 && noteKharak == 1)
            return "14";
        else if(noteNumber == 14 && noteHand == 1 && noteKharak == 9)
            return "91";
        else if(noteNumber == 14 && noteHand == 2 && noteKharak == 9)
            return "92";
        else if(noteNumber == 15 && noteHand == 1 && noteKharak == 8)
            return "81";
        else if(noteNumber == 15 && noteHand == 2 && noteKharak == 8)
            return "82";
        else if(noteNumber == 16 && noteHand == 1)
            return "71";
        else if(noteNumber == 16 && noteHand == 2)
            return "72";
        else if(noteNumber == 17 && noteHand == 1)
            return "61";
        else if(noteNumber == 17 && noteHand == 2)
            return "62";
        else if(noteNumber == 18 && noteHand == 1)
            return "51";
        else if(noteNumber == 18 && noteHand == 2)
            return "52";
        else if(noteNumber == 19 && noteHand == 1)
            return "41";
        else if(noteNumber == 19 && noteHand == 2)
            return "42";
        else if(noteNumber == 20 && noteHand == 1)
            return "31";
        else if(noteNumber == 20 && noteHand == 2)
            return "32";
        else if(noteNumber == 21 && noteHand == 1)
            return "21";
        else if(noteNumber == 21 && noteHand == 2)
            return "22";
        else if(noteNumber == 22 && noteHand == 1)
            return "11";
        else if(noteNumber == 22 && noteHand == 2)
            return "12";

        return "";


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
        else if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                saveFile(uri);
                if(exiting) {
                    finish();
                }

            }
        }
        if(resultCode == Activity.RESULT_OK)
        {
            String filename = queryName(getContentResolver(), resultData.getData());
            TextView textView = findViewById(R.id.file_name_text);
            textView.setText(filename);
        }

    }
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
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
                newNote.setHand(Integer.valueOf(notesData[3]));
                newNote.setWithTwoHand(Integer.valueOf(notesData[4]) == 1);


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
                    newView.setHand(notes.get(index).getHand(), notes.get(index).isWithTwoHand());
                    newView.postInvalidate();
                    newView.setFromOutSide(true);
                    setLineViewListeners(newView, false);
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.s);

        for(int i = 0 ; i < linearLayout.getChildCount() ; i++)
        {
            Note note = ((LineView)linearLayout.getChildAt(i)).note;
            if(note != null)
                output += Integer.toString(note.getKind()) + ":" + Integer.toString(note.getNoteNumber() )+ ":" +
                    Integer.toString(note.getKharak()) + ":" + Integer.toString(note.getHand())+ ":" + Integer.toString(note.isWithTwoHand() ? 1 : 0) +  "\n";
        }
        return output;
    }


    public void handleSaveBtn()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.save_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for
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
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.sure_save)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createFile();
                        exiting = true;
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}

