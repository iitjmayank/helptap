package com.helptap.interview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.helptap.interview.part2.MyServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by maagarwa on 1/14/2016.
 */
public class Part2Fragment extends Fragment {

    private MyServer server;
    private final String TAG = "Part2";

    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.part2_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView)view.findViewById(R.id.text);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (isStoragePermissionGranted()) {
                server = new MyServer();
                server.setTempFileManagerFactory(new ExampleManagerFactory());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static class ExampleManagerFactory implements NanoHTTPD.TempFileManagerFactory {
        @Override
        public NanoHTTPD.TempFileManager create() {
            return new ExampleManager();
        }
    }

    private static class ExampleManager implements NanoHTTPD.TempFileManager {

        private final String tmpdir;
        private final List<NanoHTTPD.TempFile> tempFiles;

        private ExampleManager() {
            tmpdir = System.getProperty("java.io.tmpdir");
            //             tmpdir = System.getProperty("/sdcard");

            tempFiles = new ArrayList<NanoHTTPD.TempFile>();
        }

        @Override
        public NanoHTTPD.TempFile createTempFile(String value) throws Exception {
            NanoHTTPD.DefaultTempFile tempFile = null;
            try {
                tempFile = new NanoHTTPD.DefaultTempFile(new File(tmpdir));
            }catch (Exception e){
                return tempFile;
            }

            tempFiles.add(tempFile);
            System.out.println("Created tempFile: " + tempFile.getName());
            return tempFile;
        }


        @Override
        public void clear() {
            if (!tempFiles.isEmpty()) {
                System.out.println("Cleaning up:");
            }
            for (NanoHTTPD.TempFile file : tempFiles) {
                try {
                    System.out.println("   "+file.getName());
                    file.delete();
                } catch (Exception ignored) {}
            }
            tempFiles.clear();
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Exception", ex.toString());
        }

        return null;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(server != null) {
            server.stop();
        }
    }
}
