package far.zad.gsoosk.musiccomposer.Notes;

import android.graphics.Canvas;

public class Note {
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
