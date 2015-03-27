package com.danish.uberproject.ui.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.danish.uberproject.R;
import com.danish.uberproject.networking.response.models.Cursor;
import com.danish.uberproject.networking.response.models.ResponseData;
import com.danish.uberproject.networking.response.models.SearchResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to populate Image GridView
 */
public class GridAdapter extends BaseAdapter {

    private List<SearchResult> imageUrls;
    private Context context;

    public GridAdapter (Context context) {
        this.context = context;
        imageUrls = new ArrayList<>();
    }

    private static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

    public void addImageUrls (List<SearchResult> results) {
        for (SearchResult result : results) {
            if (!imageUrls.contains(result)) {
                imageUrls.add(result);
            }
        }
        notifyDataSetChanged();
    }

    public void clearItems () {
        imageUrls.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Uses picasso to load image into the ImageView at the specified position in the grid view.
     *
     * @param position The position of the ImageView in the adapter's data set
     * @param viewHolder The ViewHolder class reference used to access views available in each item
     *                   for grid view.
     */
    private void loadImage (int position, final ViewHolder viewHolder) {

        SearchResult imageResult = (SearchResult) getItem(position);

        // Use Picasso to grab image from url and load into the specified ImageView
        Picasso.with(context).load(imageResult.getTbUrl()).
                into(viewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        viewHolder.imageView.setImageResource(R.mipmap.ic_content_unavailable);
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.grid_item);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder =  (ViewHolder) convertView.getTag();
        }
        loadImage(position, viewHolder);
        return convertView;
    }
}