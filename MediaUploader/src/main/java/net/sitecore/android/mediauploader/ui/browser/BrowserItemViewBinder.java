package net.sitecore.android.mediauploader.ui.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import com.squareup.picasso.Picasso;

import net.sitecore.android.mediauploader.R;
import net.sitecore.android.sdk.api.DownloadMediaOptions;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.ui.ItemViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static net.sitecore.android.mediauploader.util.ScUtils.isFileTemplate;
import static net.sitecore.android.mediauploader.util.ScUtils.isImageTemplate;
import static net.sitecore.android.mediauploader.util.ScUtils.isVideoTemplate;

public class BrowserItemViewBinder implements ItemViewBinder {

    private final String mInstanceUrl;
    private final DownloadMediaOptions mMediaOptions;

    @Inject Picasso mImageLoader;

    public BrowserItemViewBinder(String instanceUrl, DownloadMediaOptions mediaOptions) {
        mInstanceUrl = instanceUrl;
        mMediaOptions = mediaOptions;
    }

    @Override
    public void bindView(Context context, View v, ScItem item) {
        ViewHolder holder = (ViewHolder) v.getTag();

        holder.name.setText(item.getDisplayName());
        if (isImageTemplate(item.getTemplate())) {
            holder.icon.setImageResource(R.drawable.ic_placeholder);

            String imageUrl = mInstanceUrl + item.getMediaDownloadUrl(mMediaOptions);
            mImageLoader.load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_action_cancel)
                    .into(holder.icon);
        } else if (isFileTemplate(item.getTemplate())) {
            holder.icon.setImageResource(R.drawable.ic_template_file);
        } else if (isVideoTemplate(item.getTemplate())) {
            holder.icon.setImageResource(R.drawable.ic_template_video);
        } else {
            holder.icon.setImageResource(R.drawable.ic_browser_folder);
        }
    }

    @Override
    public View newView(Context context, ViewGroup parent, LayoutInflater inflater, ScItem item) {
        final View v = LayoutInflater.from(context).inflate(R.layout.browser_item, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        v.setTag(holder);
        return v;
    }

    static class ViewHolder {
        @InjectView(R.id.item_icon) ImageView icon;
        @InjectView(R.id.item_name) TextView name;

        ViewHolder(View parent) {
            ButterKnife.inject(this, parent);
        }
    }
}
