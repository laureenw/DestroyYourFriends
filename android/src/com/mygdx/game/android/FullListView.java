package com.mygdx.game.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FullListView extends ListView {

    public FullListView(Context context) {
        super(context);
    }

    public FullListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FullListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = 0;
        ListAdapter adapter = getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View childView = adapter.getView(i, null, this);
            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height += childView.getMeasuredHeight();
        }
        height += getDividerHeight() * (count-1);
        setMeasuredDimension(width, height);
    }
}
