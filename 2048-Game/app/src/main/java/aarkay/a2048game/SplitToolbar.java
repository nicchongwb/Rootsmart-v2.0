package aarkay.a2048game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by AarKay
 */
public class SplitToolbar extends Toolbar {

    public SplitToolbar(Context context) {
        super(context);
    }

    public SplitToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplitToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof ActionMenuView) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.addView(child, params);
    }
}