package net.sitecore.android.mediauploader.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import net.sitecore.android.mediauploader.provider.UploadMediaContract.Instances;
import net.sitecore.android.mediauploader.provider.UploadMediaContract.Instances.Query;

public class UploadMediaProviderInstancesTest extends BaseUploadMediaProviderTest {

    private final String NAME_SAMPLE = "name";
    private final String URL_SAMPLE = "http://test.url";
    private final String LOGIN_SAMPLE = "test_login";
    private final String PASSWORD_SAMPLE = "test_password";
    private final String DEFAULT_FOLDER_SAMPLE = "/sitecore/media library";

    private final String UPDATED_NAME_SAMPLE = "updated_name";
    private final String UPDATED_URL_SAMPLE = "updated_url";
    private final String UPDATED_LOGIN_SAMPLE = "updated_login";
    private final String UPDATED_PASSWORD_SAMPLE = "updated_password";
    private final String UPDATED_DEFAULT_FOLDER_SAMPLE = "updated_folder";

    public ContentValues getSampleValue() {
        ContentValues value = new ContentValues();
        value.put(Instances.URL, URL_SAMPLE);
        value.put(Instances.LOGIN, LOGIN_SAMPLE);
        value.put(Instances.PASSWORD, PASSWORD_SAMPLE);
        value.put(Instances.ROOT_FOLDER, DEFAULT_FOLDER_SAMPLE);

        return value;
    }

    private void checkCursor(Cursor c, int count) {
        assertNotNull(c);
        assertEquals(count, c.getCount());
    }

    public void testInsert() {
        mResolver.insert(Instances.CONTENT_URI, getSampleValue());

        Cursor c = mResolver.query(Instances.CONTENT_URI, null, null, null, null);
        checkCursor(c, 1);
    }

    public void testInsertMultiple() {
        ContentValues[] values = new ContentValues[3];
        for (int i = 0; i < values.length; i++) {
            values[i] = getSampleValue();
        }

        mResolver.bulkInsert(Instances.CONTENT_URI, values);

        Cursor c = mResolver.query(Instances.CONTENT_URI, null, null, null, null);
        checkCursor(c, values.length);
    }

    public void testDelete() {
        mResolver.insert(Instances.CONTENT_URI, getSampleValue());

        assertEquals(1, mResolver.delete(Instances.CONTENT_URI, null, null));

        Cursor c = mResolver.query(Instances.CONTENT_URI, null, null, null, null);
        checkCursor(c, 0);
    }

    public void testQuery() {
        mResolver.insert(Instances.CONTENT_URI, getSampleValue());

        Cursor c = mResolver.query(Instances.CONTENT_URI, Query.PROJECTION, null, null, null);
        checkCursor(c, 1);
        if (!c.moveToFirst()) fail("Trying to read cursor but it is empty");

        assertEquals(URL_SAMPLE, c.getString(Query.URL));
        assertEquals(LOGIN_SAMPLE, c.getString(Query.LOGIN));
        assertEquals(PASSWORD_SAMPLE, c.getString(Query.PASSWORD));
        assertEquals(DEFAULT_FOLDER_SAMPLE, c.getString(Query.ROOT_FOLDER));
    }

    public void testUpdate() {
        mResolver.insert(Instances.CONTENT_URI, getSampleValue());

        Cursor c = mResolver.query(Instances.CONTENT_URI, null, null, null, null);
        checkCursor(c, 1);

        ContentValues newValue = new ContentValues();
        newValue.put(Instances.URL, UPDATED_URL_SAMPLE);
        newValue.put(Instances.LOGIN, UPDATED_LOGIN_SAMPLE);
        newValue.put(Instances.PASSWORD, UPDATED_PASSWORD_SAMPLE);
        newValue.put(Instances.ROOT_FOLDER, UPDATED_DEFAULT_FOLDER_SAMPLE);

        String selection = Instances.LOGIN + "='" + UPDATED_LOGIN_SAMPLE + "'";
        assertEquals(1, mResolver.update(Instances.CONTENT_URI, newValue, selection, null));

        c = mResolver.query(Instances.CONTENT_URI, Query.PROJECTION, null, null, null);
        checkCursor(c, 1);
        if (!c.moveToFirst()) fail("Trying to read cursor but it is empty");

        assertEquals(UPDATED_URL_SAMPLE, c.getString(Query.URL));
        assertEquals(UPDATED_LOGIN_SAMPLE, c.getString(Query.LOGIN));
        assertEquals(UPDATED_PASSWORD_SAMPLE, c.getString(Query.PASSWORD));
        assertEquals(UPDATED_DEFAULT_FOLDER_SAMPLE, c.getString(Query.ROOT_FOLDER));
    }

    public void testGetInstanceById() {
        Uri resultUri = mResolver.insert(Instances.CONTENT_URI, getSampleValue());
        assertNotNull(resultUri);

        Cursor cursor = mResolver.query(resultUri, Query.PROJECTION, null, null,
                null);
        checkCursor(cursor, 1);
    }

    public void testDeleteInstanceById() {
        Uri resultUri = mResolver.insert(Instances.CONTENT_URI, getSampleValue());
        assertNotNull(resultUri);

        int count = mResolver.delete(resultUri, null, null);
        assertEquals(1, count);
    }

    public void testUpdateInstanceById() {
        Uri resultUri = mResolver.insert(Instances.CONTENT_URI, getSampleValue());
        assertNotNull(resultUri);

        ContentValues values = new ContentValues();
        values.put(Instances.URL, UPDATED_URL_SAMPLE);
        values.put(Instances.LOGIN, UPDATED_LOGIN_SAMPLE);
        values.put(Instances.PASSWORD, UPDATED_PASSWORD_SAMPLE);
        values.put(Instances.ROOT_FOLDER, UPDATED_DEFAULT_FOLDER_SAMPLE);

        int count = mResolver.update(resultUri, values, null, null);
        assertEquals(1, count);

        Cursor cursor = mResolver.query(resultUri, Query.PROJECTION, null, null,
                null);
        checkCursor(cursor, 1);
        if (!cursor.moveToFirst()) fail("Trying to read cursor but it is empty");

        assertEquals(UPDATED_URL_SAMPLE, cursor.getString(Query.URL));
        assertEquals(UPDATED_LOGIN_SAMPLE, cursor.getString(Query.LOGIN));
        assertEquals(UPDATED_PASSWORD_SAMPLE, cursor.getString(Query.PASSWORD));
        assertEquals(UPDATED_DEFAULT_FOLDER_SAMPLE, cursor.getString(Query.ROOT_FOLDER));
    }

    public void testUnsupportedOperation() {
        try {
            mResolver.insert(Uri.withAppendedPath(Instances.CONTENT_URI, "/error"),
                    getSampleValue());
            fail("UnsupportedOperationException should be here");
        } catch (Exception exception) {
            assertEquals(true, exception instanceof UnsupportedOperationException);
        }
    }

}