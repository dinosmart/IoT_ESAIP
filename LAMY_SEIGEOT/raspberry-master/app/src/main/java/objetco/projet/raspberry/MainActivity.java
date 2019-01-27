package objetco.projet.raspberry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String BASE_URL = "https://api.mlab.com/api/1/databases/matpau/collections/mesure?apiKey=zRpsJsByf_zbFivaly8-C4pzMwAGP90q";
    RequestQueue queue;
    private TextView dernierTV;
    private JSONArray resultat;
    private DernierResultat dernier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dernierTV = findViewById(R.id.mesure);

        Connection();
    }

    protected void Connection(){
        queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsArrRequest = new JsonArrayRequest
                (Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dernier = new DernierResultat();
                        LineChart chart = (LineChart) findViewById(R.id.chart);

                        try {
                            resultat = new JSONArray(response.toString());
                            JSONObject JSONResultat = resultat.getJSONObject(resultat.length()-1);

                            dernier.setLum(getInt("mesure", JSONResultat));
                            dernier.setDate(getString("date", JSONResultat));
                            dernier.setHeure(getString("heure", JSONResultat));
                            dernierTV.setText(dernier.getDate()+" "+dernier.getHeure()+" "+dernier.getLum());

                            List<Entry> entries = new ArrayList<Entry>();
                            for (int i = 0; i<resultat.length();i++) {

                                entries.add(new Entry(i, getInt("mesure",resultat.getJSONObject(i))));
                                Log.i("ICIBA", Integer.toString(getInt("mesure",resultat.getJSONObject(i))));
                                LineDataSet dataSet = new LineDataSet(entries, "Label");
                                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                LineData lineData = new LineData(dataSet);
                                chart.setData(lineData);
                                chart.invalidate();
                            }

                        } catch (JSONException e) {
                            Log.i("ICILA",e.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { Log.e("error Volley", error.toString());

                    }
                });
        queue.add(jsArrRequest);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
}
