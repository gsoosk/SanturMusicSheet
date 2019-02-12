package far.zad.gsoosk.musiccomposer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import far.zad.gsoosk.musiccomposer.Notes.Note;

public class LineView extends View {
    private Paint paint = new Paint();
    public static final int LINE_WIDTH = 5;
    private float noteHeight;
    private Bitmap bitmap ;
    private Canvas can;
    private Note note = null;
    private Rect previewRect = null;
    private boolean isNoteSelected = false;
    private int noteBase;

    public void setNoteBase(int noteBase) {
        this.noteBase = noteBase;
    }

    //Note Listener
    public interface LineViewListener
    {
        public void onNoteAdded(Note note);
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
        float height = canvas.getHeight() / 24;
        noteHeight = height;

        drawLines(canvas);


        canvas.drawBitmap(bitmap, 0, 0, paint);

        paint.setColor(getResources().getColor(R.color.note_prev));
        if(previewRect != null)
            canvas.drawRect(previewRect, paint);
        previewRect = null;

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
        if(!isNoteSelected) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch(x, y);
                    invalidate();
                    ok();
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
        return true;
    }
    public void ok()
    {
        invalidate();
    }
    public void touchUp(float x, float y)
    {
        int noteNumber = (int) ((y - noteHeight/2) / noteHeight);
        if(noteNumber >= 23)
            noteNumber = 22;
        addNewNote(noteNumber, x, y);
        previewRect = null;

    }
    public void addNewNote(int noteNumber, float x, float y)
    {
        note = new Note(noteNumber, x, y, noteBase);
        drawNote(note);
        isNoteSelected = true;
        if(listener != null)
            listener.onNoteAdded(note);

        Log.d("new Note : ", Integer.toString(noteNumber) + " " + Integer.toString(noteBase));
    }
    public void drawNote(Note note)
    {
        if(note.getKind() == 0)
            drawNote_0(note);
        else
        {
            Rect rect = new Rect();
            int  y = (int) ( note.getNoteNumber() *  noteHeight + noteHeight / 2);
            rect.set(0, y,  can.getWidth(), y + (int) noteHeight);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            can.drawRect(rect, paint);
        }
    }
    public void drawNote_0(Note note)
    {
        Drawable notePic = getResources().getDrawable(R.drawable.note_0);
        notePic.setBounds(0, 0, can.getWidth(), can.getHeight());
        notePic.draw(can);
    }


    public void drawLines(Canvas canvas)
    {
        float height = noteHeight;
        float x = height + height / 2;
        paint.setStrokeWidth(LINE_WIDTH);
        for(int i = 0 ; i < 11 ; i++)
        {
            if(i < 3 || i > 7)
                paint.setColor(getResources().getColor(R.color.secondary_line_color));
            else
                paint.setColor(getResources().getColor(R.color.main_line_color));

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
        int  height = (int) (( noteNumber * noteHeight) + (noteHeight / 2));
        previewRect.set(0, height - (int) noteHeight / 2,  can.getWidth(), height + (int) noteHeight * 3/2);
    }
}