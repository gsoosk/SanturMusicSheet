package far.zad.gsoosk.musiccomposer.Notes;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import far.zad.gsoosk.musiccomposer.R;

public class NoteButton extends android.support.v7.widget.AppCompatImageView {

    private boolean selected = false;
    Paint paint = new Paint();
    private int index = 0;

    public NoteButton(Context context) {
        super(context);
    }

    public NoteButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        invalidate();
    }
    public boolean getSelected()
    {
        return selected;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex()
    {
        return index;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.btn_selected));
        if(selected)
            canvas.drawRect(0 , 0, canvas.getWidth()  , canvas.getHeight() , paint);

    }
}
