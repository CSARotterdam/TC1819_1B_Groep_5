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

public class qrReaderFragment extends Fragment {
    ///<< s c r e a m s  i n t o  t h e  v o i d >>

    private TextView textDing;
    private Button arse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_qr_reader);

        this.textDing = getView().findViewById(R.id.textDing);
        this.arse = getView().findViewById(R.id.arse);

        arse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentTransaction ts = getTargetFragment( new CameraFragment());//beginTransaction();
                //ts.replace(R.id.qrReaderContainer, new CameraFragment());
                ts.addToBackStack(null);
                ts.commit();
            }
        });
    }

    @Override
    public void onActivityResult(int req, int rslt, Intent it) { //verwerkt wat er uit de scanding is gekomen
        super.onActivityResult(req, rslt, it);

        if (rslt == RESULT_OK && req == 1) {
            if (it != null) {
                if (it.hasExtra("res")) {
                    textDing.setText(it.getExtras().toString());
                    Log.wtf(String.valueOf(this), "scanned.");
                    Toast.makeText(getContext(), "scanned", Toast.LENGTH_SHORT);
                } else {
                    Log.d(String.valueOf(this), "phail");
                    Toast.makeText(getContext(), "phail", Toast.LENGTH_SHORT);
                }
            } else {
                System.out.println("u done goofed.");
            }
        }
    }
}

