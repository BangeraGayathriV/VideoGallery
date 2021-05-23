package com.gayathri.videogallery

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private var context: Context
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val space = context.resources.getDimension(R.dimen.dimen_15dp).toInt()
        outRect.right = space
        outRect.top = space
        view.setPadding(space, space, space, space)
    }
}
