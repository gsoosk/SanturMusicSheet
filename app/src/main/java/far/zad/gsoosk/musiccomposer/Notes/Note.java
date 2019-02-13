package far.zad.gsoosk.musiccomposer.Notes;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;

import far.zad.gsoosk.musiccomposer.R;

public class Note {
    public static float  NOTE_0_WIDTH_ON_HEIGHT(float x) {return x *  76 / 46;}
    public static float  NOTE_1_HEIGHT_ON_HEAD (float x) {return x * 105 / 31;}
    public static  float  NOTE_1_WIDTH_ON_HEIGHT(float x) {return x * 43 / 105;}
    public static float  NOTE_3_HEIGHT_ON_HEAD (float x) {return x * 105 / 29;}
    public static  float  NOTE_3_WIDTH_ON_HEIGHT(float x) {return x * 73 / 105;}
    public static float  NOTE_3_HEIGHT_ON_HEAD_DOWN (float x) {return x * 105 / 28;}
    public static  float  NOTE_3_WIDTH_ON_HEIGHT_DOWN(float x) {return x * 46 / 105;}
    public static  float  SILENT_NOTE_WIDTH_ON_HEIGHT(float x) {return x * 32 / 105;}

    private int noteNumber;
    private int kind;
    private float x;
    private float y;
    private int kharak = 0;
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
    public void setKharak(int kharak)
    {
        this.kharak = kharak;
    }
    public int getKharak(){return kharak;}
    public void changeKharak()
    {
        if(kharak == 8)
            kharak = 1;
        else if(kharak == 1)
            kharak = 8;
        else if(kharak == 9)
            kharak = 2;
        else if(kharak == 2)
            kharak = 9;
    }



}
