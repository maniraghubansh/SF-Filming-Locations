package com.highsparrow.sffilminglocations.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by High Sparrow on 05-05-2016.
 */
public class SFFilmingLocationsContract {

    public static final String CONTENT_AUTHORITY = "com.highsparrow.sffilminglocations";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FILMING_LOCATIONS = "filming_locations";

    public static abstract class FilmingLocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILMING_LOCATIONS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_FILMING_LOCATIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FILMING_LOCATIONS;

        public static final String
                TABLE_NAME = "filming_locations",
                FIRST_ACTOR = "actor_1",
                SECOND_ACTOR = "actor_2",
                THIRD_ACTOR = "actor_3",
                LATITUDE = "latitude",
                LONGITUDE = "longitude",
                DIRECTOR = "director",
                DISTRIBUTOR = "distributor",
                FUN_FACTS = "fun_facts",
                LOCATIONS = "locations",
                PRODUCTION_COMPANY = "production_company",
                RELEASE_YEAR = "release_year",
                TITLE = "title",
                WRITER = "writer";
    }
}
