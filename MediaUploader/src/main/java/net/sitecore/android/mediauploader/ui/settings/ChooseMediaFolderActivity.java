package net.sitecore.android.mediauploader.ui.settings;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncQueryHandler;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import net.sitecore.android.mediauploader.R;
import net.sitecore.android.mediauploader.UploaderApp;
import net.sitecore.android.mediauploader.model.Instance;
import net.sitecore.android.mediauploader.provider.UploadMediaContract.Instances;
import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScRequestQueue;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.ui.ItemsBrowserFragment.ContentTreePositionListener;
import net.sitecore.android.sdk.ui.ItemsBrowserFragment.NetworkEventsListener;
import net.sitecore.android.sdk.ui.ItemsListBrowserFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChooseMediaFolderActivity extends Activity implements LoaderCallbacks<Cursor>, ErrorListener, Listener<ScApiSession> {
    public final static String INSTANCE_KEY = "instance";
    public static final int READ_NAMES_ACTION = 1;
    private static final String SELECTION = Instances.URL + "=? and " + Instances.LOGIN + "=? and " +
            Instances.PASSWORD + "=? and " + Instances.ROOT_FOLDER + "=?";

    private Instance mInstance;
    private Uri mInstanceUri;
    private ItemsListBrowserFragment mBrowserFragment;

    @InjectView(R.id.instance_root_folder) TextView mInstanceRootFolder;
    @Inject ScRequestQueue mScRequestQueue;

    private ContentTreePositionListener mContentTreePositionListener = new ContentTreePositionListener() {
        @Override public void onGoUp(ScItem item) {
            mInstanceRootFolder.setTag(item.getPath());
        }

        @Override public void onGoInside(ScItem item) {
            mInstanceRootFolder.setTag(item.getPath());
        }

        @Override public void onInitialized(ScItem item) {
            mInstanceRootFolder.setTag(item.getPath());
        }
    };

    private NetworkEventsListener mNetworkEventsListener = new NetworkEventsListener() {
        @Override public void onUpdateRequestStarted() {

        }

        @Override public void onUpdateSuccess(ItemsResponse itemsResponse) {

        }

        @Override public void onUpdateError(VolleyError volleyError) {
            onErrorResponse(volleyError);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_media_library);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mInstanceUri = getIntent().getData();
        mInstance = getIntent().getParcelableExtra(INSTANCE_KEY);
        if (mInstance == null) {
            throw new IllegalStateException("You should pass instance to start this activity");
        }

        UploaderApp.from(this).inject(this);
        ButterKnife.inject(this);

        mBrowserFragment = (ItemsListBrowserFragment) getFragmentManager().findFragmentById(R.id.browser_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_media_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                if (mBrowserFragment.getCurrentItem() != null) {
                    mInstance.setRootFolder(mBrowserFragment.getCurrentItem().getPath());
                }
                checkInstanceIfExists();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        super.onResume();
        ScApiSessionFactory.getSession(mScRequestQueue, mInstance.getUrl(), mInstance.getLogin(),
                mInstance.getPassword(), this, this);
    }

    private void checkInstanceIfExists() {
        getLoaderManager().restartLoader(READ_NAMES_ACTION, null, this);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(ScApiSession session) {
        mBrowserFragment.setRootFolder(mInstance.getRootFolder());
        mBrowserFragment.setNetworkEventsListener(mNetworkEventsListener);
        mBrowserFragment.setContentTreePositionListener(mContentTreePositionListener);
        mBrowserFragment.loadContent(session);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case READ_NAMES_ACTION:
                String[] selectionArgs = new String[]{mInstance.getUrl(), mInstance.getLogin(), mInstance.getPassword(),
                        mInstance.getRootFolder()};
                return new CursorLoader(this, Instances.CONTENT_URI, null, SELECTION, selectionArgs, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case READ_NAMES_ACTION: {
                if (data.moveToFirst()) {
                    Toast.makeText(this, R.string.text_instance_exists, Toast.LENGTH_LONG).show();
                } else {
                    saveInstanceToDB(mInstance);
                    NavUtils.navigateUpFromSameTask(this);
                }
                data.close();
                getLoaderManager().destroyLoader(READ_NAMES_ACTION);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void saveInstanceToDB(Instance instance) {
        if (mInstanceUri == null) {
            new AsyncQueryHandler(getContentResolver()) {
            }.startInsert(0, null, Instances.CONTENT_URI, instance.toContentValues());
        } else {
            new AsyncQueryHandler(getContentResolver()) {
            }.startUpdate(0, null, mInstanceUri, instance.toContentValues(), null, null);
        }
    }
}
