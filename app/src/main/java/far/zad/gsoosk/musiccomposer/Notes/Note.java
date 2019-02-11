package far.zad.gsoosk.musiccomposer.Notes;

public class Note {
    private int noteNumber;
    private float x;
    private float y;
    public Note(int _noteNumber, float _x, float _y)
    {
        x = _x;
        y = _x;
        noteNumber = _noteNumber;
    }
    public int getNoteNumber()
    {
        return noteNumber;
    }

}
