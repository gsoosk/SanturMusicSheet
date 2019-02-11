package far.zad.gsoosk.musiccomposer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

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
        float height = canvas.getHeight() / 23;
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
        int noteNumber = (int) (y / noteHeight);
        if(noteNumber >= 23)
            noteNumber = 22;
        addNewNote(noteNumber, x, y);
        previewRect = null;

    }
    public void addNewNote(int noteNumber, float x, float y)
    {
        note = new Note(noteNumber, x, y);
        drawNote(note);
        isNoteSelected = true;
        if(listener != null)
            listener.onNoteAdded(note);

        Log.d("new Note : ", Integer.toString(noteNumber));
    }
    public void drawNote(Note note)
    {
        Rect rect = new Rect();
        int  y = ( note.getNoteNumber()) * ((int) noteHeight);
        rect.set(0, y,  can.getWidth(), y + (int) noteHeight);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        can.drawRect(rect, paint);

    }
    public void drawLines(Canvas canvas)
    {
        float height = noteHeight;
        float x = height;
        paint.setStrokeWidth(LINE_WIDTH);
        for(int i = 0 ; i < 12 ; i++)
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
        int noteNumber = (int) (y / noteHeight);
        if(noteNumber >= 23)
            noteNumber = 22;
        previewRect = new Rect();
        int  height = ( noteNumber ) * ((int) noteHeight);
        previewRect.set(0, height - (int) noteHeight / 2,  can.getWidth(), height + (int) noteHeight * 3/2);
    }
}