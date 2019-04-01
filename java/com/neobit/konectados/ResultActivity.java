package com.neobit.konectados;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.Line;
import com.neobit.konectados.helpers.JSONParser;

import org.json.JSONObject;

import java.util.HashMap;

public class ResultActivity extends Activity {

    private CheckCodeTask checkTask;
    private View mContentView, mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);

        mContentView = findViewById(R.id.contentView);
        mProgressView = findViewById(R.id.progressView);
        if (checkTask == null) {
            checkTask = new CheckCodeTask(ResultActivity.this.getString(R.string.URL), getIntent().getExtras().getString("code"), ResultActivity.this.getString(R.string.device));
            checkTask.execute((Void) null);
        }
        Button btnRegresar = (Button) findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultActivity.this.finish();
            }
        });
    }

    public class CheckCodeTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mURL;
        private final String mCode;
        private final String mDevice;

        CheckCodeTask(String URL, String code, String device) {
            mURL = URL;
            mCode = code;
            mDevice = device;
            showProgress(true);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonOb = new JSONObject();
            try {
                String partes[] = mCode.split(",");
                if (partes.length == 3) {
                    HashMap<String, String> meMap = new HashMap<String, String>();
                    meMap.put("user_id", partes[1]);
                    meMap.put("coupon_id", partes[0]);
                    meMap.put("device_id", mDevice);
                    meMap.put("code2fa", partes[2]);

                    Log.i("URL", mURL);
                    JSONParser jParser = new JSONParser();
                    jsonOb = jParser.getJSONPOSTFromUrl(mURL, meMap);
                    Log.i("response", jsonOb.toString());
                } else {
                    jsonOb.put("msg", ResultActivity.this.getString(R.string.error_msg));
                    jsonOb.put("response_code", 400);
                }
            } catch(Exception e) {
                Log.e(ResultActivity.this.getString(R.string.app_name), ResultActivity.this.getString(R.string.error_tag), e);
            }
            return jsonOb;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            checkTask = null;
            showProgress(false);

            try {
                TextView textTitle = (TextView) findViewById(R.id.textTitle);
                TextView textMessage = (TextView) findViewById(R.id.textMessage);
                textMessage.setText(response.getString("msg"));
                if (response.getInt("response_code") == 200) {
                            textTitle.setText(ResultActivity.this.getString(R.string.title_ok));
                    TextView textProduct = (TextView) findViewById(R.id.textProduct);
                    textProduct.setText(response.getJSONObject("data").getJSONObject("product").getString("name"));
                    TextView textDescripcion = (TextView) findViewById(R.id.textDescripcion);
                    textDescripcion.setText(response.getJSONObject("data").getJSONObject("product").getString("description"));
                    TextView textPrecio = (TextView) findViewById(R.id.textPrecio);
                    textPrecio.setText("$" + response.getJSONObject("data").getJSONObject("product").getString("price"));
                    TextView textDescuento = (TextView) findViewById(R.id.textDescuento);
                    textDescuento.setText("Desc:" + response.getJSONObject("data").getString("discount") + "%");
                    /*SharedPreferences settings = getActivity().getSharedPreferences("MisPreferencias",getActivity().MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("jsonUsuario", response.getJSONObject("data").toString());
                    editor.commit();
                    getActivity().onBackPressed();*/
                    //}
                } else {
                    LinearLayout layProduct = (LinearLayout) findViewById(R.id.layProduct);
                    layProduct.setVisibility(View.GONE);
                    LinearLayout layProduct2 = (LinearLayout) findViewById(R.id.layProduct2);
                    layProduct2.setVisibility(View.GONE);
                    textTitle.setText(ResultActivity.this.getString(R.string.title_no));
                    ImageView imgSuperior = (ImageView) findViewById(R.id.imgSuperior);
                    imgSuperior.setImageResource(R.drawable.icono2);
                }
            } catch (Exception e) {
                Log.e(ResultActivity.this.getString(R.string.app_name), ResultActivity.this.getString(R.string.error_tag), e);
            }
        }

        @Override
        protected void onCancelled() {
            checkTask = null;
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
