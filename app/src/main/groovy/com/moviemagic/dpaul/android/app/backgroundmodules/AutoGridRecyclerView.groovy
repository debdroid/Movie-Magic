package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import groovy.transform.CompileStatic

/**
 * This custom RecyclerView is used to create a auto fit grid layout.
 * (reference http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html)
 */

@CompileStatic
class AutoGridRecyclerView extends RecyclerView {
    private GridLayoutManager gridLayoutManager
    private int columnWidth = -1

    public AutoGridRecyclerView(Context context) {
        super(context)
        init(context, null)
    }

    public AutoGridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs)
        init(context, attrs)
    }

    public AutoGridRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle)
        init(context, attrs)
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = [android.R.attr.columnWidth] as int[]
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }
        //Even though the span count is set in onMeasure but the app will crash if it waits until then
        //to define a GridLayoutManager, so create one here as well, with a span count of 1.
        gridLayoutManager = new GridLayoutManager(getContext(), 1)
        setLayoutManager(gridLayoutManager)
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec)
        if (columnWidth > 0) {
            //This makes sure that it will return a span count of 1,
            //even if the column width is defined to be larger than the width of the RecyclerView
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth)
            gridLayoutManager.setSpanCount(spanCount)
        }
    }
}