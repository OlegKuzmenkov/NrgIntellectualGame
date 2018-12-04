package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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

    NewsListHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mNewsId = itemView.findViewById(R.id.news_id);
        mNewsTitle = itemView.findViewById(R.id.news_title);
        mNewsDescription = itemView.findViewById(R.id.news_description);
        mNewsSource = itemView.findViewById(R.id.news_source);
        mNewsImage = itemView.findViewById(R.id.news_image);
    }

    /**
     * Bind news
     */
    void bindNews(News news, int position) {
        mNews = news;

        mNewsId.setText(String.format("News # %d",(++position)));
        mNewsTitle.setText(mNews.getTitle());
        mNewsDescription.setText(mNews.getDescription());
        mNewsSource.setText(mNews.getSource());

        Bitmap newsImage = mNews.getImage();

        if (newsImage != null) {
            Bitmap scaledBitmap = scaleBitmap(mNewsImage, newsImage);
            mNewsImage.setImageBitmap(scaledBitmap);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), NewsActivity.class);
        intent.putExtra(INTENT_CONTENT, mNews.getUrl());
        view.getContext().startActivity(intent);
    }

    /**
     * Scale bitmap by size of the ImageView
     */
    private Bitmap scaleBitmap(ImageView imageView, Bitmap bitmap) {
        Bitmap scaledBitmap = null;

        if (imageView != null && bitmap != null) {
            ViewGroup.LayoutParams params =  imageView.getLayoutParams();
            int requiredWidth = params.width;
            int requiredHeight = params.height;

            scaledBitmap = Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, false);
        }

        return scaledBitmap;
    }
}
