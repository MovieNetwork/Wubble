package com.proxima.Wubble.activities;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.proxima.Wubble.R;

/**
 * Created by Emre on 02/11/15.
 */
public class ToolbarActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    protected Toolbar activateToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.app_bar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return mToolbar;

    }
}
