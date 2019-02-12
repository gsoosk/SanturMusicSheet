package far.zad.gsoosk.musiccomposer.Notes;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import far.zad.gsoosk.musiccomposer.R;

public class Note {
    public static float  NOTE_0_WIDTH_ON_HEIGHT(float x) {return x *  76 / 46;}
    public static float  NOTE_1_HEIGHT_ON_HEAD (float x) {return x * 105 / 31;}
    public static  float  NOTE_1_WIDTH_ON_HEIGHT(float x) {return x * 43 / 105;}
    public static float  NOTE_3_HEIGHT_ON_HEAD (float x) {return x * 105 / 29;}
    public static  float  NOTE_3_WIDTH_ON_HEIGHT(float x) {return x * 73 / 105;}
    public static float  NOTE_3_HEIGHT_ON_HEAD_DOWN (float x) {return x * 105 / 28;}
    public static  float  NOTE_3_WIDTH_ON_HEIGHT_DOWN(float x) {return x * 46 / 105;}

    private int noteNumber;
    private int kind;
    private float x;
    private float y;
    public Note(int _noteNumber, float _x, float _y, int _kind)
    {
        x = _x;
        y = _x;
        noteNumber = _noteNumber;
        kind = _kind;
    }
    public int getNoteNumber()
    {
        return noteNumber;
    }
    public int getKind() {return kind;}



}
