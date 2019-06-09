package com.hr.techlabapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.QR.qrReader;
import com.hr.techlabapp.R;

import java.nio.ByteBuffer;

//ik haat mijzelf dus daarom maak ik een camera ding met een api dat nog niet eens in de beta stage is
//en waarvan de tutorial in een taal is dat ik 0% begrijp
//saus: https://codelabs.developers.google.com/codelabs/camerax-getting-started/
public class CameraFragment extends Fragment {
    //private int REQUEST_CODE_PERMISSIONS = 10; //idek volgens tutorial is dit een arbitraire nummer zou helpen als je app meerdere toestimmingen vraagt
    //private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"}; //array met permissions vermeld in manifest
    CameraInteractionListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        ((NavHostActivity)context).currentFragment = this;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CameraX.unbindAll(); //camera uitzetten zodra fragment afgesloten wordt
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        TextureView txView = getView().findViewById(R.id.view_finder);

        startCamera();
    }

    private void startCamera() {//heel veel dingen gebeuren hier
        final TextureView txView = getView().findViewById(R.id.view_finder);
        //eerst zeker zijn dat de camera niet gebruikt wordt.
        CameraX.unbindAll();

        /* doe preview weergeven */
        int aspRatioW = txView.getWidth(); //haalt breedte scherm op
        int aspRatioH = txView.getHeight(); //haalt hoogte scherm op
        Rational asp = null; //helpt bij zetten aspect ratio
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            asp = new Rational(aspRatioW, aspRatioH);
        }
        Size screen = null; //grootte scherm ofc
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            screen = new Size(aspRatioW, aspRatioH);
        }

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(asp).setTargetResolution(screen).build();
        Preview pview = new Preview(pConfig);

        pview.setOnPreviewOutputUpdateListener(
            new Preview.OnPreviewOutputUpdateListener() {
                //eigenlijk maakt dit al een nieuwe texturesurface aan
                // maar aangezien ik al eentje heb gemaakt aan het begin...
                @Override
                public void onUpdated(Preview.PreviewOutput output){
                    ViewGroup parent = (ViewGroup) txView.getParent();
                    parent.removeView(txView); //moeten wij hem eerst yeeten
                    parent.addView(txView, 0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        txView.setSurfaceTexture(output.getSurfaceTexture());  //dan weer toevoegen
                    }
                }
            });

        /* image capture */

        /*ImageCaptureConfig imgConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY).setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        ImageCapture imgCap = new ImageCapture(imgConfig);*/

        /* image analyser */

        ImageAnalysisConfig imgAConfig = new ImageAnalysisConfig.Builder().setImageQueueDepth(1).setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_NEXT_IMAGE).build();
        final ImageAnalysis imgAsys = new ImageAnalysis(imgAConfig);

        imgAsys.setAnalyzer(
            new ImageAnalysis.Analyzer(){
                @Override
                public void analyze(ImageProxy image, int rotationDegrees){
                    String result;
                    try {
                        ByteBuffer bf = image.getPlanes()[0].getBuffer(); //euh iets doen met de images
                        byte[] b = new byte[bf.capacity()]; //in array stoppen
                        bf.get(b);
                        Rect r = image.getCropRect(); //voor de dingetje dat gaat helpen met verwerken van de imgcapture
                        int w = image.getWidth(); //hxb voor dingetje
                        int h = image.getHeight();

                        PlanarYUVLuminanceSource sauce = new PlanarYUVLuminanceSource(b ,w, h, r.left, r.top, r.width(), r.height(),false);
                        BinaryBitmap bit = new BinaryBitmap(new HybridBinarizer(sauce));//dingetje

                        result = new qrReader().decoded(bit); //stopt dingetje in qrlezer
                        getQRRes(result);
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();//en als het goed gaat krijgen we te zien wat erin zit
                        Log.wtf("F: ", result);

                    } catch (NotFoundException e) {
                            e.printStackTrace();
                    } catch (FormatException e) {
                            e.printStackTrace();
                    }
                }
            });

        //bindt de shit hierboven aan de lifecycle:
        CameraX.bindToLifecycle(this, imgAsys, /*imgCap,*/ pview);
    }

    public interface CameraInteractionListener {
        void CameraInteractionListener(String rst);
    }

    public void getQRRes(String s){
        Intent it = new Intent(getActivity(), qrReaderFragment.class); //uuhhh omdat een void niet echt iets terugggefft
        it.putExtra("res", s); //dan maar via een intent terug stueren
        System.out.println(s);
        startActivityForResult(it,1);
    }

    public CameraFragment newInstance() {
        CameraFragment qrRes = new CameraFragment();
        return qrRes;
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CameraInteractionListener) {
            mListener = (CameraInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

}
