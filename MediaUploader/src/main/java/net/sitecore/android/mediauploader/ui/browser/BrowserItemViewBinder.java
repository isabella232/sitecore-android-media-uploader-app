package net.sitecore.android.mediauploader.ui.browser;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import com.squareup.picasso.Picasso;

import net.sitecore.android.mediauploader.R;
import net.sitecore.android.mediauploader.util.ScUtils;
import net.sitecore.android.sdk.api.DownloadMediaOptions.Builder;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.ui.ItemViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BrowserItemViewBinder implements ItemViewBinder {
    public final Builder IMAGE_OPTIONS = new Builder()
            .maxWidth(200)
            .maxHeight(200);

    private final String mInstanceUrl;
    private final String mDatabase;

    @Inject Resources mResources;
    @Inject Picasso mImageLoader;

    public BrowserItemViewBinder(String instanceUrl, String database) {
        mInstanceUrl = instanceUrl;
        mDatabase = database;
    }

    @Override
    public void bindView(Context context, View v, ScItem item) {
        ViewHolder holder = (ViewHolder) v.getTag();

        holder.name.setText(item.getDisplayName());
        if (ScUtils.isImageTemplate(item.getTemplate())) {

            String imageUrl = mInstanceUrl + item.getMediaDownloadUrl(IMAGE_OPTIONS.database(mDatabase).build());
            mImageLoader.load(imageUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_action_cancel)
                    .into(holder.icon);
        } else {
            holder.icon.setImageDrawable(mResources.getDrawable(R.drawable.ic_browse));
        }
    }

    @Override
    public View newView(Context context, ViewGroup parent, LayoutInflater inflater, ScItem item) {
        View v = LayoutInflater.from(context).inflate(R.layout.browser_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        if (v != null) {
            v.setTag(holder);
        }
        return v;
    }

    class ViewHolder {
        @InjectView(R.id.item_icon) ImageView icon;
        @InjectView(R.id.item_name) TextView name;

        ViewHolder(View parent) {
            ButterKnife.inject(this, parent);
        }
    }
}
