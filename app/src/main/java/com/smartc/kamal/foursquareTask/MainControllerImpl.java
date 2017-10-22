package com.smartc.kamal.foursquareTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainControllerImpl implements MainController {

  private ProgressDialog loadingDialog;

  public void getVenuesData(String area, final List venuesList, final RecyclerView.Adapter venuesRecyclerAdapter, Context mainContext) {

    final Context context = mainContext;

    loadingDialog = new ProgressDialog(context);
    loadingDialog.setMessage(context.getResources().getString(R.string.venue_search_loading));
    loadingDialog.setIndeterminate(false);
    loadingDialog.setCancelable(false);
    loadingDialog.show();

    RequestQueue requestQueue = Volley.newRequestQueue(context);
    String foursquareRequestUrl = null;

      foursquareRequestUrl = "https://api.foursquare.com/v2/venues/explore?client_id=YFRT0F0EKMLCUYYQO04YWQMUN15MXZH2KEXU5ZGN2D1QV0CF&client_secret=I2NQKHQPPL3DCCBTWU4PP4ZF2WJMGYZEOXIVXSHF0K330RJA&v=20170815%20&ll=" +area;


    JsonObjectRequest jsObjRequest = new JsonObjectRequest
        (Request.Method.GET, foursquareRequestUrl, null, new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {
            loadingDialog.dismiss();
            try {
              venuesList.clear();
              JSONArray items = response.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items");
              for (int i = 0; i < items.length(); i++) {
                JSONObject venuesJson = items.getJSONObject(i).getJSONObject("venue");
                venuesList.add(new VenueModel(
                    venuesJson.getString("name"),
                    venuesJson.getString("rating"),
                    venuesJson.getJSONArray("categories")
                ));
              }
            } catch (JSONException e) {
              venuesList.clear();
              Toast.makeText(context, context.getString(R.string.venue_search_invalid_error), Toast.LENGTH_SHORT).show();
            }
            venuesRecyclerAdapter.notifyDataSetChanged();
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError) {
              loadingDialog.dismiss();
              Toast.makeText(context, context.getString(R.string.no_inernet_connection), Toast.LENGTH_SHORT).show();
            } else {
              loadingDialog.dismiss();
              Toast.makeText(context, context.getString(R.string.venue_search_loading_error), Toast.LENGTH_SHORT).show();
            }
          }
        });
    jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
        5000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    requestQueue.add(jsObjRequest);
  }
}
