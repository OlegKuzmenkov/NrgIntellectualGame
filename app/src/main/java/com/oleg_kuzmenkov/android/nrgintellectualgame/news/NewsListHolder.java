package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;

public class NewsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String INTENT_CONTENT = "url";

    private TextView mIdNewsTextView;
    private TextView mTitleNewsTextView;
    private TextView mDescriptionNewsrView;
    private TextView mSourceNewsView;
    private ImageView mImageNewsView;
    private News mNews;

    public NewsListHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mIdNewsTextView = itemView.findViewById(R.id.news_id);
        mTitleNewsTextView = itemView.findViewById(R.id.news_title);
        mDescriptionNewsrView = itemView.findViewById(R.id.news_description);
        mSourceNewsView = itemView.findViewById(R.id.news_source);
        mImageNewsView = itemView.findViewById(R.id.news_image);
    }

    /**
     * Bind news content
     */
    public void bindNews(News news, int position) {
        mNews = news;
        position++;
        mIdNewsTextView.setText("News # " + position);
        mTitleNewsTextView.setText(mNews.getTitle());
        mDescriptionNewsrView.setText(mNews.getDescription());
        mSourceNewsView.setText(mNews.getSourceName());
        if (mNews.getImage() == null) {
            // remain standart image
        } else {
            Bitmap scaledBitmap = scaleBitmap(mImageNewsView, mNews.getImage());
            mImageNewsView.setImageBitmap(scaledBitmap);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), NewsActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_CONTENT, mNews.getUrl());
        view.getContext().startActivity(intent);
    }

    /**
     * Scale bitmap by size of the ImageView
     */
    private Bitmap scaleBitmap(ImageView imageView, Bitmap bitmap) {
        Bitmap scaledBitmap = null;
        if (imageView != null && bitmap != null) {
            int wantedWidth = imageView.getLayoutParams().width;
            int wantedHeight = imageView.getLayoutParams().height;
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, wantedWidth, wantedHeight, false);
        }
        return scaledBitmap;
    }
}
