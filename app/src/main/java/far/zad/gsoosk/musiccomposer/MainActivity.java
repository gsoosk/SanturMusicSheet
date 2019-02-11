package far.zad.gsoosk.musiccomposer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import far.zad.gsoosk.musiccomposer.Notes.Note;
import far.zad.gsoosk.musiccomposer.Notes.NoteButton;

public class MainActivity extends AppCompatActivity {

    public static final int INITIAL_BUTTON = 2;
    public static final int YEK_LA_CHANG_WIDTH = 100;
    private ArrayList<Note> notes  = new ArrayList<Note>();
    private NoteButton selectedBtn = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setNoteBarListener();
        addNoteLine();
        setInputViewNoteBase();


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
            public void onNoteAdded(Note note) {
                notes.add(note);
                addNoteLine();

                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                });
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

}
