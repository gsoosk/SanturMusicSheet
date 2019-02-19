package far.zad.gsoosk.musiccomposer.Notes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;

import far.zad.gsoosk.musiccomposer.Notes.Note;
import far.zad.gsoosk.musiccomposer.R;

import static far.zad.gsoosk.musiccomposer.R.*;

public class LineView extends View {
    private Paint paint = new Paint();
    public static final int LINE_WIDTH = 8;
    private float noteHeight;
    private Bitmap bitmap ;
    private Canvas can;
    public Note note = null;
    private Rect previewRect = null;
    private boolean isNoteSelected = false;
    private int noteBase;
    private boolean playing = false;
    private boolean fromOutSide = false;
    private float baseHeight;
    private float width ;
    private boolean editing = false;

    public void setFromOutSide(boolean fromOutSide)
    {
        this.fromOutSide = fromOutSide;
    }

    public void play()
    {
        playing = true;
        invalidate();
    }
    public void pause()
    {
        playing = false;
        invalidate();
    }

    public Note getNote() {
        return note;
    }

    public void setNoteBase(int noteBase) {
        this.noteBase = noteBase;
    }

    //Note Listener
    public interface LineViewListener
    {
        public void onNoteAdded(Note note, boolean editing);
    }
    private LineViewListener listener;
    public void setLineViewListener(LineViewListener l)
    {
        listener = l;
    }

    private void init() {
        paint.setColor(Color.BLACK);
        listener = null;
    }

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        baseHeight = canvas.getHeight() / 6;
        width = canvas.getWidth();

        float height = ( canvas.getHeight() - baseHeight ) / 24;
        noteHeight = height;

        drawLines(canvas);

        if(fromOutSide)
            drawNote(note);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        drawKharakNumber(canvas);

        drawHand(canvas);

        drawEditBtn(canvas);

        drawTwoHandBox(canvas);

        paint.setColor(getResources().getColor(color.note_prev));
        if(previewRect != null)
            canvas.drawRect(previewRect, paint);
        previewRect = null;

        if(playing) {
            paint.setColor(getResources().getColor(color.playing));
            canvas.drawRect(0, 0, canvas.getWidth(), ( canvas.getHeight()), paint);
        }

    }
    public void drawHand(Canvas canvas)
    {
        if(note != null)
        {
            paint.setColor(getResources().getColor(color.secondary_line_color));
            canvas.drawLine(canvas.getWidth(), baseHeight / 2, canvas.getWidth(), (int) baseHeight, paint);
            Drawable handPic = note.getHand() == 1 ? getResources().getDrawable(drawable.ic_hand_rast) :
                    getResources().getDrawable(drawable.ic_hand_chap);
            handPic.setBounds(canvas.getWidth() / 2 + (canvas.getWidth()/2) / 3 , (int) (baseHeight / 2 + baseHeight/2 * 10 / 100),
                    canvas.getWidth() / 2 + canvas.getWidth()/2 * 2 / 3, (int) (baseHeight / 2 + baseHeight/2 * 90 / 100));
            handPic.draw(canvas);
        }

    }
    public void drawTwoHandBox(Canvas canvas)
    {
        if(note != null && note.isWithTwoHand())
        {
            paint.setColor(getResources().getColor(color.two_hands));
            canvas.drawRect(0, 0, canvas.getWidth(), baseHeight/2 * 90/100, paint);
        }
    }

    public void drawEditBtn(Canvas canvas)
    {
        if(note != null)
        {
            canvas.drawLine(0, baseHeight / 2, 0, (int) baseHeight, paint);
            Drawable handPic = getResources().getDrawable(drawable.ic_note_interface_symbol) ;
            handPic.setBounds( (canvas.getWidth()/2) / 3 ,  (int) (baseHeight / 2 + baseHeight/2 * 10 / 100),
                    canvas.getWidth()/2 * 2 / 3,  (int) (baseHeight / 2 + baseHeight/2 * 90 / 100));
            handPic.draw(canvas);
        }
    }

    public void drawKharakNumber(Canvas can)
    {
        if(note != null) {
            if (note.getNoteNumber() == 7 || note.getNoteNumber() == 8 || note.getNoteNumber() == 14 || note.getNoteNumber() == 15) {
                paint.setTextSize(60);
                paint.setColor(getResources().getColor(color.main_line_color));
                can.drawText(Integer.toString(note.getKharak()), can.getWidth() / 2, baseHeight + noteHeight * 3 / 2, paint);
            }
        }

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w;
        int height = h;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        can = new Canvas(bitmap);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(isNoteSelected)
        {
            if(y < baseHeight / 2)
            {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    note.changeTwoHand();
                    invalidate();
                    return true;
                }
            }
            else if(y < baseHeight)
            {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    if(x > width/2)
                    {
                        note.changeHand();
                        invalidate();
                        return true;
                    }
                    else
                    {
                        note = null;
                        isNoteSelected = false;
                        can.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        editing = true;
                        invalidate();
                        return true;
                    }

                }
            }

        }
        y = y - baseHeight;

        if(!isNoteSelected) {
            if(y > 0) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        touchUp(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        previewRect = null;
                        invalidate();
                        break;
                }
            }
        } else if(note.getNoteNumber() == 7 || note.getNoteNumber() == 8 || note.getNoteNumber() == 14 || note.getNoteNumber() == 15)
        {
            if(event.getAction() == MotionEvent.ACTION_UP)
            {
                changeKharak(x, y);
                invalidate();
            }
        }
        return true;
    }
    public void changeKharak(float x, float y)
    {
        int noteNumber = (int) ((y - noteHeight/2) / noteHeight);
        if(noteNumber == 0){
            note.changeKharak();
        }

    }
    public void setKharak(int kharak)
    {
        note.setKharak(kharak);
        invalidate();
    }
    public void touchUp(float x, float y)
    {
        int noteNumber;
        if(noteBase >= 6 && noteBase <=  9)
            noteNumber = 0;
        else
            noteNumber = (int) ((y - noteHeight/2) / noteHeight);

        if(noteNumber >= 23)
            noteNumber = 22;
        if(noteNumber <= 0)
            noteNumber = 0;
        addNewNote(noteNumber);
        previewRect = null;

    }
    public void addNewNote(int noteNumber)
    {
        note = new Note(noteNumber, noteBase);
        if(noteNumber == 15 || noteNumber == 8)
            note.setKharak(8);
        else if(noteNumber == 7 || noteNumber == 14)
            note.setKharak(2);

        drawNote(note);

        isNoteSelected = true;
        if(listener != null)
            listener.onNoteAdded(note, editing);

        Log.d("new Note : ", Integer.toString(noteNumber) + " " + Integer.toString(noteBase));

    }
    public void drawNote(Note note)
    {
        if(note.getKind() == 0)
            drawNote_0(note);
        else if(note.getKind() == 1 || note.getKind() == 2)
            drawNote_1_2(note);
        else if(note.getKind() >= 3 &&  note.getKind() <= 5 )
            drawNote_3to5(note);
        else if(note.getKind() >= 6 && note.getKind() <=  9)
            drawNote_6to9(note);
    }


    public void drawNote_0(Note note)
    {
        Drawable notePic = getResources().getDrawable(drawable.note_0);
        float y = baseHeight + (note.getNoteNumber() * noteHeight);
        float note0Width = Note.NOTE_0_WIDTH_ON_HEIGHT(noteHeight );
        Log.d("Height", Float.toString(noteHeight * 2));
        Log.d("Height", Float.toString(note0Width));

        notePic.setBounds(can.getWidth() / 2 - (int) note0Width,  (int) y,
                can.getWidth() / 2 + (int) note0Width,  (int) (y + 2*noteHeight));
        notePic.draw(can);
    }

    public void drawNote_1_2(Note note)
    {
        Drawable notePic ;
        if(note.getKind() == 1)
        {
            if(note.getNoteNumber() > 10) {
                notePic = getResources().getDrawable(drawable.note_1_1);
                drawNote1Up(note, notePic);
            }
            else {
                notePic = getResources().getDrawable(drawable.note_1_2);
                drawNote1Down(note, notePic);
            }
        }
        else if(note.getKind() == 2)
        {
            if(note.getNoteNumber() > 10) {
                notePic = getResources().getDrawable(drawable.note_2_1);
                drawNote1Up(note, notePic);
            }
            else {
                notePic = getResources().getDrawable(drawable.note_2_2);
                drawNote1Down(note, notePic);
            }
        }

    }
    public void drawNote1Up(Note note, Drawable notePic)
    {

        float y = baseHeight + note.getNoteNumber() * noteHeight + 2*noteHeight;
        float note1Height = Note.NOTE_1_HEIGHT_ON_HEAD(noteHeight * 2 );
        float note1Width = Note.NOTE_1_WIDTH_ON_HEIGHT(note1Height/2);
        notePic.setBounds((int) (can.getWidth() / 2 -  note1Width),  (int) (y - note1Height),
                (int) (can.getWidth() / 2 +  note1Width),  (int) (y));
        notePic.draw(can);
    }
    public void drawNote1Down(Note note, Drawable notePic)
    {
        float y = baseHeight+ note.getNoteNumber() * noteHeight ;
        float note1Height = Note.NOTE_1_HEIGHT_ON_HEAD(noteHeight * 2 );
        float note1Width = Note.NOTE_1_WIDTH_ON_HEIGHT(note1Height/2);
        notePic.setBounds((int) (can.getWidth() / 2 -  note1Width),  (int) (y),
                (int) (can.getWidth() / 2 +  note1Width),  (int) (y + note1Height));
        notePic.draw(can);
    }
    public void drawNote_3to5(Note note)
    {
        Drawable notePic = null;
        switch (note.getKind())
        {
            case 3 :
                notePic = getResources().getDrawable(
                        note.getNoteNumber() > 10 ? drawable.note_3_1 : drawable.note_3_2);
                break;
            case 4 :
                notePic = getResources().getDrawable(
                        note.getNoteNumber() > 10 ? drawable.note_4_1 : drawable.note_4_2);
                break;
            case 5 :
                notePic = getResources().getDrawable(
                        note.getNoteNumber() > 10 ? drawable.note_5_1 : drawable.note_5_2);
                break;
        }

        if(note.getNoteNumber() > 10) {
            drawNote_3to5Up(note, notePic);
        }
        else {
            drawNote_3to5Down(note, notePic);
        }

    }
    public void drawNote_3to5Up(Note note, Drawable notePic)
    {
        float y = baseHeight + note.getNoteNumber() * noteHeight + 2*noteHeight;
        float note1Height = Note.NOTE_3_HEIGHT_ON_HEAD(noteHeight * 2 );
        float note1Width = Note.NOTE_3_WIDTH_ON_HEIGHT(note1Height/2);

        notePic.setBounds((int) (can.getWidth() / 2 -  note1Width),  (int) (y - note1Height),
                (int) (can.getWidth() / 2 +  note1Width),  (int) (y));
        notePic.draw(can);
    }
    public void drawNote_3to5Down(Note note, Drawable notePic)
    {
        float y = baseHeight + note.getNoteNumber() * noteHeight ;
        float note1Height = Note.NOTE_3_HEIGHT_ON_HEAD_DOWN(noteHeight * 2 );
        float note1Width = Note.NOTE_3_WIDTH_ON_HEIGHT_DOWN(note1Height/2);
        notePic.setBounds((int) (can.getWidth() / 2 -  note1Width),  (int) (y),
                (int) (can.getWidth() / 2 +  note1Width),  (int) (y + note1Height));
        notePic.draw(can);
    }
    public void drawNote_6to9(Note note)
    {
        Drawable notePic = null;
        switch (note.getKind())
        {
            case 6 :
                notePic = getResources().getDrawable( drawable.silent_0);
                break;
            case 7 :
                notePic = getResources().getDrawable( drawable.silent_1);
                break;
            case 8 :
                notePic = getResources().getDrawable( drawable.silent_2);
                break;
            case 9 :
                notePic = getResources().getDrawable( drawable.silent_3);
                break;
        }
        drawSilentNote(notePic);

    }
    public void drawSilentNote(Drawable notePic)
    {
        float height =   baseHeight + 7 * noteHeight / 2;
        float width = Note.SILENT_NOTE_WIDTH_ON_HEIGHT(height);

        notePic.setBounds((int) (can.getWidth() / 2 -  width),  (int) (can.getHeight() / 2 - height),
                (int) (can.getWidth() / 2 + width),  (int) (can.getHeight() / 2 + height));
        notePic.draw(can);
    }
    public void drawLines(Canvas canvas)
    {
        float height = noteHeight;
        float x = baseHeight +  height + height / 2;
        paint.setStrokeWidth(LINE_WIDTH);
        for(int i = 0 ; i < 11 ; i++)
        {
            if(i < 3 || i > 7)
                paint.setColor(getResources().getColor(color.secondary_line_color));
            else
                paint.setColor(getResources().getColor(color.main_line_color));

            x += height/2;
            canvas.drawLine(0, x, canvas.getWidth(), x , paint);
            x+= height/2;
            x += height;
        }




    }
    public void touch(float x, float y)
    {

        int noteNumber = (int) ((y - noteHeight/2) / noteHeight);

        if(noteNumber >= 23)
            noteNumber = 22;
        previewRect = new Rect();
        int  height = (int) (baseHeight + ( noteNumber * noteHeight) + (noteHeight / 2));
        previewRect.set(0, height - (int) noteHeight / 2,  can.getWidth(), height + (int) noteHeight * 3/2);
    }

}