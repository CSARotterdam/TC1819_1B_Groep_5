package com.hr.techlabapp.Fragments;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.R;

import static android.app.Activity.RESULT_OK;

//ik haat mijzelf dus daarom maak ik een camera ding met een api dat nog niet eens in de beta stage is
//en waarvan de tutorial in een taal is dat ik 0% begrijp
//saus: https://codelabs.developers.google.com/codelabs/camerax-getting-started/

public class qrReaderFragment extends Fragment {
    ///<< s c r e a m s  i n t o  t h e  v o i d >>

    private TextView textDing;
    private Button knopje;
    String txt;

    public qrReaderFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((NavHostActivity)getActivity()).currentFragment = this;
        textDing = getView().findViewById(R.id.textDing);
        knopje = getView().findViewById(R.id.knopj);
        /*knopje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_qrReaderFragment_to_Camera);
            }
        });*/
    }
}