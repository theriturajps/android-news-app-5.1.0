package com.app.androidnewsapp.adapter;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.FAN;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.STARTAPP;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.app.androidnewsapp.R;
import com.app.androidnewsapp.config.AppConfig;
import com.app.androidnewsapp.database.prefs.AdsPref;
import com.app.androidnewsapp.database.prefs.SharedPref;
import com.app.androidnewsapp.models.Post;
import com.app.androidnewsapp.util.Constant;
import com.app.androidnewsapp.util.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.solodroid.ads.sdk.format.NativeAdViewHolder;

import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private final int VIEW_PROG = 0;
    private final int VIEW_HEAD = 1;
    private final int VIEW_ITEM = 2;
    private final int VIEW_AD = 3;
    List<Post> posts;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemOverflowClickListener mOnItemOverflowClickListener;
    boolean scrolling = false;
    boolean showHeader;
    SharedPref sharedPref;
    AdsPref adsPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Post obj, int position);
    }

    public interface OnItemOverflowClickListener {
        void onItemOverflowClick(View view, Post obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnItemOverflowClickListener(final OnItemOverflowClickListener mItemOverflowClickListener) {
        this.mOnItemOverflowClickListener = mItemOverflowClickListener;
    }

    public AdapterPost(Context context, RecyclerView view, List<Post> posts, boolean showHeader) {
        this.posts = posts;
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
        this.showHeader = showHeader;
        lastItemViewDetector(view);
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolling = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrolling = false;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView excerpt;
        public ImageView icDate;
        public TextView date;
        public ImageView icComment;
        public TextView comment;
        public ImageView image;
        public ImageView thumbnailVideo;
        public LinearLayout lytParent;
        public LinearLayout lytDate;
        public LinearLayout lytComment;
        public ImageView btnOverflow;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            excerpt = v.findViewById(R.id.excerpt);
            icDate = v.findViewById(R.id.ic_date);
            date = v.findViewById(R.id.date);
            icComment = v.findViewById(R.id.ic_comment);
            comment = v.findViewById(R.id.comment);
            image = v.findViewById(R.id.image);
            thumbnailVideo = v.findViewById(R.id.thumbnail_video);
            lytParent = v.findViewById(R.id.lyt_parent);
            lytDate = v.findViewById(R.id.lyt_date);
            lytComment = v.findViewById(R.id.lyt_comment);
            btnOverflow = v.findViewById(R.id.btn_overflow);
        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_HEAD) {
            View view;
            if (showHeader) {
                if (AppConfig.ENABLE_NEW_APP_DESIGN) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_post_large, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_large, parent, false);
                }
            } else {
                if (AppConfig.ENABLE_NEW_APP_DESIGN) {
                    if (AppConfig.BIG_NEWS_LIST_STYLE) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_post_large, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_post_default, parent, false);
                    }
                } else {
                    if (AppConfig.BIG_NEWS_LIST_STYLE) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_large, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_default, parent, false);
                    }
                }
            }
            vh = new OriginalViewHolder(view);
        } else if (viewType == VIEW_ITEM) {
            View view;
            if (AppConfig.ENABLE_NEW_APP_DESIGN) {
                if (AppConfig.BIG_NEWS_LIST_STYLE) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_post_large, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_post_default, parent, false);
                }
            } else {
                if (AppConfig.BIG_NEWS_LIST_STYLE) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_large, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_default, parent, false);
                }
            }
            vh = new OriginalViewHolder(view);
        } else if (viewType == VIEW_AD) {
            View view;
            if (adsPref.getNativeAdStylePostList().equals(Constant.NATIVE_AD_STYLE_SMALL)) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_radio, parent, false);
            } else if (adsPref.getNativeAdStylePostList().equals(Constant.NATIVE_AD_STYLE_MEDIUM)) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_news, parent, false);
            } else if (adsPref.getNativeAdStylePostList().equals(Constant.NATIVE_AD_STYLE_LARGE)) {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(com.solodroid.ads.sdk.R.layout.view_native_ad_medium, parent, false);
            }
            vh = new NativeAdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final Post p = posts.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.title.setText(Html.fromHtml(p.news_title));
            vItem.excerpt.setText(Html.fromHtml(p.news_description));

            if (AppConfig.BIG_NEWS_LIST_STYLE) {
                vItem.title.setMaxLines(4);
                vItem.excerpt.setVisibility(View.GONE);
            } else {
                if (AppConfig.SHOW_EXCERPT_IN_POST_LIST) {
                    vItem.title.setMaxLines(2);
                    vItem.excerpt.setVisibility(View.VISIBLE);
                } else {
                    vItem.title.setMaxLines(4);
                    vItem.excerpt.setVisibility(View.GONE);
                }
            }

            if (AppConfig.SHOW_DATE_DISPLAY_AS_TIME_AGO) {
                vItem.date.setText(Tools.getTimeAgo(p.news_date));
            } else {
                vItem.date.setText(Tools.getFormatedDateSimple(p.news_date));
            }

            if (AppConfig.SHOW_POST_DATE) {
                vItem.lytDate.setVisibility(View.VISIBLE);
            } else {
                vItem.lytDate.setVisibility(View.GONE);
            }

            if (sharedPref.getLoginFeature().equals("yes")) {
                vItem.lytComment.setVisibility(View.VISIBLE);
            } else {
                vItem.lytComment.setVisibility(View.GONE);
            }

            vItem.comment.setText("" + Tools.withSuffix(p.comments_count));

            if (p.content_type != null && p.content_type.equals("Post")) {
                vItem.thumbnailVideo.setVisibility(View.GONE);
            } else {
                vItem.thumbnailVideo.setVisibility(View.VISIBLE);
            }

            if (p.content_type != null && p.content_type.equals("youtube")) {
                Glide.with(context)
                        .load(Constant.YOUTUBE_IMG_FRONT + p.video_id + Constant.YOUTUBE_IMG_BACK)
                        .placeholder(R.drawable.ic_thumbnail)
                        .thumbnail(Tools.RequestBuilder(context))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(vItem.image);
            } else {
                Glide.with(context)
                        .load(sharedPref.getBaseUrl() + "/upload/" + p.news_image.replace(" ", "%20"))
                        .placeholder(R.drawable.ic_thumbnail)
                        .thumbnail(Tools.RequestBuilder(context))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(vItem.image);
            }

            if (sharedPref.getIsDarkTheme()) {
                vItem.btnOverflow.setColorFilter(ContextCompat.getColor(context, R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
                vItem.icDate.setColorFilter(ContextCompat.getColor(context, R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
                vItem.icComment.setColorFilter(ContextCompat.getColor(context, R.color.color_dark_icon), PorterDuff.Mode.SRC_IN);
                vItem.excerpt.setTextColor(ContextCompat.getColor(context, R.color.color_dark_text));
            } else {
                vItem.btnOverflow.setColorFilter(ContextCompat.getColor(context, R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
                vItem.icDate.setColorFilter(ContextCompat.getColor(context, R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
                vItem.icComment.setColorFilter(ContextCompat.getColor(context, R.color.color_light_icon), PorterDuff.Mode.SRC_IN);
                vItem.excerpt.setTextColor(ContextCompat.getColor(context, R.color.color_light_text));
            }

            if (AppConfig.ENABLE_NEW_APP_DESIGN) {
                if (sharedPref.getIsDarkTheme()) {
                    vItem.date.setTextColor(ContextCompat.getColor(context, R.color.color_dark_text));
                    vItem.comment.setTextColor(ContextCompat.getColor(context, R.color.color_dark_text));
                } else {
                    vItem.date.setTextColor(ContextCompat.getColor(context, R.color.color_light_text));
                    vItem.comment.setTextColor(ContextCompat.getColor(context, R.color.color_light_text));
                }
            }

            vItem.lytParent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, p, position);
                }
            });

            vItem.btnOverflow.setOnClickListener(view -> {
                if (mOnItemOverflowClickListener != null) {
                    mOnItemOverflowClickListener.onItemOverflowClick(view, p, position);
                }
            });

        } else if (holder instanceof NativeAdViewHolder) {

            final NativeAdViewHolder vItem = (NativeAdViewHolder) holder;

            if (adsPref.getIsNativeAdPostList()) {
                vItem.loadNativeAd(context,
                        adsPref.getAdStatus(),
                        1,
                        adsPref.getMainAds(),
                        adsPref.getBackupAds(),
                        adsPref.getAdMobNativeId(),
                        adsPref.getAdManagerNativeId(),
                        adsPref.getFanNativeId(),
                        adsPref.getAppLovinNativeAdManualUnitId(),
                        adsPref.getAppLovinBannerMrecZoneId(),
                        adsPref.getWortiseNativeId(),
                        sharedPref.getIsDarkTheme(),
                        false,
                        adsPref.getNativeAdStylePostList(),
                        R.color.color_light_native_ad_background,
                        R.color.color_dark_native_ad_background
                );

                if (!adsPref.getNativeAdStylePostList().equals(Constant.NATIVE_AD_STYLE_MEDIUM)) {
                    vItem.setNativeAdPadding(
                            context.getResources().getDimensionPixelOffset(R.dimen.padding_small),
                            context.getResources().getDimensionPixelOffset(R.dimen.no_padding),
                            context.getResources().getDimensionPixelOffset(R.dimen.padding_small),
                            context.getResources().getDimensionPixelOffset(R.dimen.no_padding)
                    );
                }

                vItem.setNativeAdMargin(
                        context.getResources().getDimensionPixelOffset(R.dimen.no_padding),
                        context.getResources().getDimensionPixelOffset(R.dimen.item_post_padding_small),
                        context.getResources().getDimensionPixelOffset(R.dimen.no_padding),
                        context.getResources().getDimensionPixelOffset(R.dimen.item_post_padding_small)
                );

                if (sharedPref.getIsDarkTheme()) {
                    vItem.setNativeAdBackgroundResource(R.drawable.bg_native_ad_dark);
                } else {
                    vItem.setNativeAdBackgroundResource(R.drawable.bg_native_ad_light);
                }

            }

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        if (post != null) {
            if (position == 0) {
                if (AppConfig.SHOW_HEADER_VIEW) {
                    if (AppConfig.BIG_NEWS_LIST_STYLE) {
                        return VIEW_ITEM;
                    } else {
                        return VIEW_HEAD;
                    }
                } else {
                    return VIEW_ITEM;
                }
            } else {
                if (post.news_title == null || post.news_title.equals("")) {
                    return VIEW_AD;
                }
                return VIEW_ITEM;
            }
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Post> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.posts.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void insertDataWithNativeAd(List<Post> items) {
        setLoaded();
        int positionStart = getItemCount();
        if (adsPref.getIsNativeAdPostList()) {
            if (items.size() >= adsPref.getNativeAdIndex()) {
                items.add(adsPref.getNativeAdIndex(), new Post());
            }
        }
        int itemCount = items.size();
        this.posts.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (posts.get(i) == null) {
                posts.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.posts.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetListData() {
        this.posts.clear();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null));
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (adsPref.getIsNativeAdPostList()) {
                            switch (adsPref.getMainAds()) {
                                case ADMOB:
                                case GOOGLE_AD_MANAGER:
                                case FAN:
                                case STARTAPP:
                                case APPLOVIN:
                                case APPLOVIN_MAX:
                                case WORTISE: {
                                    int current_page = getItemCount() / (AppConfig.LOAD_MORE + 1); //posts per page plus 1 Ad
                                    onLoadMoreListener.onLoadMore(current_page);
                                    break;
                                }
                                default: {
                                    int current_page = getItemCount() / (AppConfig.LOAD_MORE);
                                    onLoadMoreListener.onLoadMore(current_page);
                                    break;
                                }
                            }
                        } else {
                            int current_page = getItemCount() / (AppConfig.LOAD_MORE);
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    private int getLastVisibleItem(int[] into) {
        int lastIdx = into[0];
        for (int i : into) {
            if (lastIdx < i) lastIdx = i;
        }
        return lastIdx;
    }

}