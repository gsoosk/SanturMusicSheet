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
    public static  float SILENT_NOTE_GERD_ON_HEIGHT(float x) {return x * 50 / 30;}
    public static float REPEAT_WIDTH_ON_HIGHT(float x) {return x * 50 / 26;}

    private int noteNumber;
    private int kind;
    private int hand;
    private static int globalHand = 1;
    public boolean withTwoHand;
    public boolean repeated = false;
    public boolean isRepeatEnd() {
        return kind == 13;
    }
    public boolean isRepeatStart() {
        return kind == 12;
    }



    public void changeHand()
    {
        hand = hand == 1 ? 2 : 1;
    }
    private int kharak = 0;
    public Note(int _noteNumber, int _kind)
    {

        noteNumber = _noteNumber;
        kind = _kind;
        hand = globalHand;

        if(globalHand == 1)
            globalHand = 2;
        else
            globalHand = 1;

        withTwoHand = false;
    }

    public void changeTwoHand()
    {
        withTwoHand = !withTwoHand;
    }

    public void setHand(int hand) {
        this.hand = hand;
    }

    public void setWithTwoHand(boolean withTwoHand) {
        this.withTwoHand = withTwoHand;
    }

    public boolean isWithTwoHand() {
        return withTwoHand;
    }

    public int getHand() {
        return hand;
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
    public int getSleepTime(int time)
    {
        int sleepTime = time;
        switch (kind)
        {
            case 0 :
                sleepTime = time * 4;
                break;
            case 1 :
                sleepTime = time * 2;
                break;
            case 2 :
                sleepTime = time ;
                break;
            case 3 :
                sleepTime = time / 2;
                break;
            case 4 :
                sleepTime = time / 4;
                break;
            case 5 :
                sleepTime = time / 8;
                break;
            case 6 :
                sleepTime = time * 4;
                break;
            case 7 :
                sleepTime = time * 2;
                break;
            case 8 :
                sleepTime = time ;
                break;
            case 9 :
                sleepTime = time / 2;
                break;
            case 10 :
                sleepTime = time / 4;
                break;
            case 11 :
                sleepTime = time / 8;
                break;
        }
        return sleepTime;
    }




}
