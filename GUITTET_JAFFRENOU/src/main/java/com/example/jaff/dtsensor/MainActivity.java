package com.example.jaff.dtsensor;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DTSensor";
    FirebaseAuth mAuth;
    DatabaseReference myDbRef;
    GraphView graph;
    TextView txtDate;
    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Fullscreen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide(); //remove title bar
        overridePendingTransition(R.anim.fade_in_longtime, R.anim.fade_out_longtime); // Transition fade

        mAuth = FirebaseAuth.getInstance();
        graph = findViewById(R.id.graph);
        txtDate = findViewById(R.id.date);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                readDatabase();
            }
        }, 20000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        readDatabase();
    }

    /**
     * ---------------------DATABASE----------------------------------------------------------------
     */

    public void readDatabase(){
        myDbRef = FirebaseDatabase.getInstance().getReference("data");

        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //parcours des nodes id Maps
                int c = 0;
                int value;
                String datetime = " ";
                series = new LineGraphSeries<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    value = child.child("photocell").getValue(Integer.class);
                    Integer xi = c;
                    Integer yi = value;
                    series.appendData(new DataPoint(xi, yi),true,300);
                    datetime = child.child("date").getValue(String.class);
                    c++;
                }
                txtDate.setText("Last date value: "+ datetime);
                graph.removeAllSeries();
                graph.addSeries(series);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());
                Toast.makeText(MainActivity.this, "Problem for charging the list",Toast.LENGTH_SHORT).show();
            }
        };
        myDbRef.addValueEventListener(userListener);
    }

    /**
     * ---------------------QUIT-------------------------------------------------------------------
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
