package com.hr.techlabapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hr.techlabapp.R;

import static android.app.Activity.RESULT_OK;

//ik haat mijzelf dus daarom maak ik een camera ding met een api dat nog niet eens in de beta stage is
//en waarvan de tutorial in een taal is dat ik 0% begrijp
//saus: https://codelabs.developers.google.com/codelabs/camerax-getting-started/

public class qrReaderFragment extends AppCompatActivity {
    ///<< s c r e a m s  i n t o  t h e  v o i d >>

    private TextView textDing;
    private Button knopje;
    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qr_reader);

        this.textDing = findViewById(R.id.textDing);
        this.knopje = findViewById(R.id.arse);
        knopje.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentTransaction ts = getSupportFragmentManager().beginTransaction(); //switch naar de camera
                ts.replace(R.id.qrReaderContainer, new CameraFragment());
                ts.addToBackStack(null);
                ts.commit();
                //FragmentManager fm = getSupportFragmentManager();
                //FragmentTransaction ft = fm.beginTransaction();
                //ft.replace(R.id.qrReaderContainer, new CameraFragment());
                //ft.addToBackStack(null);
                //ft.commit();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //verwerkt wat er uit de scanding is gekomen
        super.onActivityResult(requestCode, resultCode, data);
        //Bundle bun = new Bundle();
        //bun.getBundle("message");

        //textDing.setText(bun.getString("message"));

        if (requestCode == 1) {
            if (requestCode == RESULT_OK) {
                if (data.hasExtra("res")) {
                    txt = data.getExtras().toString();
                    textDing.setText(txt);
                    Log.wtf(String.valueOf(this), "scanned.");
                } else {
                    Log.wtf(String.valueOf(this), "phail");
                }
            } else {
                System.out.println("u done goofed.");
            }
        }
    }

}
