package com.hr.techlabapp.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.hr.techlabapp.QR.qrGenerator;
import com.hr.techlabapp.R;


public class qrGenFragment extends Fragment {
    //waarom heeft dit ding een spellchecker. leaf me be.
    //also, waarom gebruikt dit ding letterlijk >2GB ram. android studio why
    //idc about those 4 warnings shhhh @ xml zooi
    //WAAROM IS MIJN XML ZOOI OPEENS COMPLETELY FUCKED

    private ImageView ivStats;
    private EditText dingetje;
    private Button butt;
    //private OnFragmentInteractionListener mListener;

    public qrGenFragment() {
        // Required empty public constructor
    }

    public static qrGenFragment newInstance() {
        qrGenFragment fragment = new qrGenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.ivStats = getView().findViewById(R.id.ivStats);
        this.dingetje = getView().findViewById(R.id.dingetje);
        this.butt = getView().findViewById(R.id.butt); //yeey it found the butt

        butt.setOnClickListener(new View.OnClickListener() { //kijken of er op het knopje is gedrukt
            @Override
            public void onClick(View v) {
                String geweldigeTekst = dingetje.getText().toString().trim(); //maakt van de ingevoerde text een string - spaties op het begin/einde.

                try {
                    Bitmap yeet = new qrGenerator().qrDing(geweldigeTekst); //string -> qr code
                    ivStats.setImageBitmap(yeet); // -> laat qr code zien als het goed gaat
                } catch (WriterException e) {
                    Toast.makeText(getContext(), "u done goofed", Toast.LENGTH_SHORT).show(); //?? moet nog uitzoeken hoe dit precies werkt lel.
                    e.printStackTrace(); //gooit een exception naar je.
                    Log.wtf(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_gen, container, false);
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
}
