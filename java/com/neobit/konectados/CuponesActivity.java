package com.neobit.konectados;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neobit.konectados.adapters.CuponesAdapter;
import com.neobit.konectados.helpers.JSONParser;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CuponesActivity extends Activity {

    private GetJSON jsonTask;
    private View mContentView, mProgressView;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycler);

        mContentView = findViewById(R.id.contentView);
        mProgressView = findViewById(R.id.progressView);
        if (jsonTask == null) {
            URL = getString(R.string.URL_cupones) + getString(R.string.device) + "/today";
            jsonTask = new GetJSON(URL);
            jsonTask.execute();
        }
    }

    public class GetJSON extends AsyncTask<Void, Void, JSONObject> {

        private final String URL;
        private Boolean success = false;

        GetJSON(String URL) {
            this.URL = URL;
            showProgress(true);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonOb = new JSONObject();
            try {
                Log.i("URL", URL);
                JSONParser jParser = new JSONParser();
                jsonOb = jParser.getJSONFromUrl(URL);
                if (jsonOb.getJSONArray("data").length() > 0) success = true;
                Log.i("DATA", jsonOb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonOb;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            jsonTask = null;
            showProgress(false);
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            if (success) {
                try {
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CuponesActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    CuponesAdapter mAdapter = new CuponesAdapter(CuponesActivity.this, response.getJSONArray("data"));
                    mRecyclerView.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                TextView textEmpty = (TextView) findViewById(R.id.textEmpty);
                textEmpty.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            jsonTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            mContentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}