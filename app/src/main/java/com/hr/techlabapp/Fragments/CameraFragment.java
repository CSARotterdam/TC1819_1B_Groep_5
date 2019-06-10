package com.hr.techlabapp.Fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.hr.techlabapp.Activities.NavHostActivity;
import com.hr.techlabapp.CustomViews.ViewFinderResultPointCallback;
import com.hr.techlabapp.CustomViews.ViewFinderView;
import com.hr.techlabapp.QR.qrReader;
import com.hr.techlabapp.R;

import java.nio.ByteBuffer;
import java.util.HashMap;

//ik haat mijzelf dus daarom maak ik een camera ding met een api dat nog niet eens in de beta stage is
//en waarvan de tutorial in een taal is dat ik 0% begrijp
//saus: https://codelabs.developers.google.com/codelabs/camerax-getting-started/
public class CameraFragment extends Fragment {
    private TextureView txView;
    private ViewFinderView vfView;
    private Bundle bun = new Bundle();
    final int REQUEST_CODE = 1;
    private HashMap<DecodeHintType, Object> decodeDing = new HashMap<>();

    public CameraFragment() {
        // Required empty public constructor
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
        ((NavHostActivity)getActivity()).currentFragment = this;
        txView = getView().findViewById(R.id.camera_frag);
        vfView = getView().findViewById(R.id.viewfinder_view);

        startCamera();
    }

    private void startCamera() {//heel veel dingen gebeuren hier
        //eerst zeker zijn dat de camera niet gebruikt wordt.
        CameraX.unbindAll();

        /* doe preview weergeven */
        Rect outputF;//voor de outputframe dat mee meot mef vde viwefiender
        final int aspRatioW = txView.getWidth(); //haalt breedte scherm op
        final int aspRatioH = txView.getHeight(); //haalt hoogte scherm op
        Rational asp = new Rational (aspRatioW, aspRatioH); //helpt bij zetten aspect ratio
        Size screen = new Size(aspRatioW, aspRatioH); //grootte scherm ofc

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
                        txView.setSurfaceTexture(output.getSurfaceTexture());  //dan weer toevoegen
                    }
                });

        /* image capture */

        /*ImageCaptureConfig imgConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY).setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        ImageCapture imgCap = new ImageCapture(imgConfig);*/

        /* image analyser */

        ImageAnalysisConfig imgAConfig = new ImageAnalysisConfig.Builder().setImageQueueDepth(3).setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).build();
        final ImageAnalysis imgAsys = new ImageAnalysis(imgAConfig);

        imgAsys.setAnalyzer(
                new ImageAnalysis.Analyzer(){
                    @Override
                    public void analyze(ImageProxy image, int rotationDegrees){
                        String result;
                        try{
                            ByteBuffer bf = image.getPlanes()[0].getBuffer(); //euh iets doen met de images
                            byte[] b = new byte[bf.capacity()]; //in array stoppen
                            bf.get(b);
                            Rect r = image.getCropRect(); //voor de dingetje dat gaat helpen met verwerken van de imgcapture
                            int w = image.getWidth(); //hxb voor dingetje
                            int h = image.getHeight();

                            PlanarYUVLuminanceSource sauce = new PlanarYUVLuminanceSource(b ,w, h, r.left, r.top, r.width(), r.height(),false);
                            BinaryBitmap bit = new BinaryBitmap(new HybridBinarizer(sauce));//dingetje

                            decodeDing.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, new ViewFinderResultPointCallback(vfView)); //zoekt naar de qr vlakding

                            result = new qrReader().decoded(bit,decodeDing); //stopt dingetje in qrlezer
                            getQRRes(result);
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();//en als het goed gaat krijgen we te zien wat erin zit
                            Log.wtf("F", result);

                        } catch (NotFoundException e){
                            e.printStackTrace();
                        } catch (FormatException e){
                            e.printStackTrace();
                        } catch (ChecksumException e){
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
        if(s == null)
            return;
        String q = s;//uuhhh omdat een void niet echt iets terugggefft
        bun.putString("message", q);
        getFragmentManager().popBackStack();
        Navigation.findNavController(getView()).navigate(R.id.action_Camera_to_checkLendRequestFragment,bun);
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
