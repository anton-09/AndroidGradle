package ru.home.serial;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MainTable implements BaseColumns {
    private MainTable() {
    }

    public static final String AUTHORITY = "ru.home.serial.provider";

    public static final String TABLE_SERIALS = "serials";
    public static final String TABLE_EPISODES = "episodes";

    public static final Uri CONTENT_URI_SERIALS = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SERIALS);
    public static final Uri CONTENT_ID_URI_SERIALS = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SERIALS + "/");
    public static final Uri CONTENT_URI_SERIAL_FILTER = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SERIALS + "/filter/");

    public static final Uri CONTENT_URI_EPISODES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_EPISODES);
    public static final Uri CONTENT_ID_URI_EPISODES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_EPISODES + "/");

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ru.home.serial";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ru.home.serial";

    public static final String DEFAULT_SORT_ORDER_SERIALS = "name COLLATE LOCALIZED ASC";
    public static final String DEFAULT_SORT_ORDER_EPISODES = "name COLLATE LOCALIZED ASC";

    public static final String SERIALS_NAME = "name";
    public static final String SERIALS_ALT_NAME = "alt_name";
    public static final String SERIALS_SEASON_COUNT = "season_count";
    public static final String SERIALS_WITH_EPISODE_NAMES = "episode_with_name";
    public static final String SERIALS_COLUMN_NUMBER = "column_number";
    public static final String SERIALS_IS_ACTIVE = "is_active";
    public static final String SERIALS_SEARCH_NAME = "search_name";
    public static final String SERIALS_RELEASE_STUDIO_NAME = "release_studio_name";
    public static final String SERIALS_TRANSLATE_STUDIO_NAME = "translate_studio_name";

    public static final String EPISODES_NUMBER = "number";
    public static final String EPISODES_SEASON = "season";
    public static final String EPISODES_NAME = "name";
    public static final String EPISODES_RELEASE_DATE = "release_date";
    public static final String EPISODES_DOWNLOAD_DATE = "download_date";
    public static final String EPISODES_WATCH_DATE = "watch_date";
    public static final String EPISODES_SERIAL_ID = "serial_id";
    public static final String EPISODES_ALT_NAME = "alt_name";
}
