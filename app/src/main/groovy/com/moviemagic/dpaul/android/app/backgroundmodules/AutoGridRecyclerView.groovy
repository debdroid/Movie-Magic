/*
 * Copyright 2017 Debashis Paul

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import groovy.transform.CompileStatic

/**
 * This custom RecyclerView is used to create a auto fit grid layout.
 * This is built based on a blog post written by Chiu-Ki Chan. Many thanks to Chiu-Ki Chan.
 * (reference http://blog.sqisland.com/2014/12/recyclerview-autofit-grid.html)
 */

@CompileStatic
class AutoGridRecyclerView extends RecyclerView {
    private GridLayoutManager gridLayoutManager
    private int columnWidth = -1

    public AutoGridRecyclerView(final Context context) {
        super(context)
        init(context, null)
    }

    public AutoGridRecyclerView(final Context context, final AttributeSet attrs) {
        super(context, attrs)
        init(context, attrs)
    }

    public AutoGridRecyclerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle)
        init(context, attrs)
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (attrs != null) {
            final int[] attrsArray = [android.R.attr.columnWidth] as int[]
            final TypedArray array = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }
        //Even though the span count is set in onMeasure but the app will crash if it waits until then
        //to define a GridLayoutManager, so create one here as well, with a span count of 1.
        gridLayoutManager = new GridLayoutManager(getContext(), 1)
        setLayoutManager(gridLayoutManager)
    }

    @Override
    protected void onMeasure(final int widthSpec, final int heightSpec) {
        super.onMeasure(widthSpec, heightSpec)
        if (columnWidth > 0) {
            //This makes sure that it will return a span count of 1,
            //even if the column width is defined to be larger than the width of the RecyclerView
            final int spanCount = Math.max(1, getMeasuredWidth() / columnWidth)
            gridLayoutManager.setSpanCount(spanCount)
        }
    }
}