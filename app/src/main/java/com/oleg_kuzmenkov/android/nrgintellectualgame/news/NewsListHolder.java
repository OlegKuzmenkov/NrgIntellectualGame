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
    private static final String INTENT_CONTENT = "URL";

    private TextView mNewsId;
    private TextView mNewsTitle;
    private TextView mNewsDescription;
    private TextView mNewsSource;
    private ImageView mNewsImage;
    private News mNews;

    public NewsListHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mNewsId = itemView.findViewById(R.id.news_id);
        mNewsTitle = itemView.findViewById(R.id.news_title);
        mNewsDescription = itemView.findViewById(R.id.news_description);
        mNewsSource = itemView.findViewById(R.id.news_source);
        mNewsImage = itemView.findViewById(R.id.news_image);
    }

    /**
     * Bind news content
     */
    public void bindNews(News news, int position) {
        mNews = news;
        position++;
        mNewsId.setText("News # " + position);
        mNewsTitle.setText(mNews.getTitle());
        mNewsDescription.setText(mNews.getDescription());
        mNewsSource.setText(mNews.getSourceName());
        if (mNews.getImage() == null) {
            // remain standart image
        } else {
            Bitmap scaledBitmap = scaleBitmap(mNewsImage, mNews.getImage());
            mNewsImage.setImageBitmap(scaledBitmap);
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
