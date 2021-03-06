package com.ahsailabs.beritakita.pages.home.adapters;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahsailabs.beritakita.R;
import com.ahsailabs.beritakita.bases.BaseRecyclerViewAdapter;
import com.ahsailabs.beritakita.pages.home.models.News;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by ahmad s on 2019-09-27.
 */
public class NewsAdapter extends BaseRecyclerViewAdapter<News, NewsAdapter.NewsViewHolder>{

    public NewsAdapter(List<News> modelList) {
        super(modelList);
    }

    @Override
    protected int getLayout() {
        return R.layout.news_itemview;
    }

    @Override
    protected NewsViewHolder getViewHolder(View rootView) {
        return new NewsViewHolder(rootView);
    }

    @Override
    protected void doSettingViewWithModel(NewsViewHolder holder, News dataModel, int position) {
        holder.tvTitle.setText(dataModel.getTitle());
        holder.tvSummary.setText(dataModel.getSummary());
        if(!TextUtils.isEmpty(dataModel.getPhoto())){
            Picasso.get().load(dataModel.getPhoto()).into(holder.ivPhoto);
            holder.ivPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.ivPhoto.setVisibility(View.GONE);
        }
        holder.tvDate.setText(dataModel.getCreatedAt());
        holder.tvUser.setText(dataModel.getCreatedBy());
        setViewClickable(holder, holder.itemView);
        setViewLongClickable(holder, holder.itemView);
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvSummary;
        private ImageView ivPhoto;
        private TextView tvDate;
        private TextView tvUser;
        private NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUser = itemView.findViewById(R.id.tvUser);
        }
    }
}
