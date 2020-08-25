package com.iamdj.mymediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView mylistview;
    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mylistview = findViewById(R.id.mylistview);
        runtimePermission();


    }


    // Get read permission from user.
    private void runtimePermission(){

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displayInListView(); // display song list, when permission granted.
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Please grant permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest(); // if user denied permission.
                    }
                }).check();
    }


    // Read songs from device
    private ArrayList<File> findSong(File file) {

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles(); // create array object of File

        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    // After reading, display song in list view
    private void displayInListView() {

        final ArrayList<File> mysong = findSong(Environment.getExternalStorageDirectory()); // read songs by findSong method.

        items = new String[mysong.size()];  // declare size

        for(int i=0;i<mysong.size();i++){
            items[i] = mysong.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, items);
        mylistview.setAdapter(myAdapter); // plugin adapter with list view.



        // Perform action on item click
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String songName = mylistview.getItemAtPosition(position).toString();

                startActivity(new Intent(MainActivity.this, PlayerActivity.class)
                  .putExtra("songs", mysong).putExtra("songname",songName)
                  .putExtra("pos",position));
                // Pass selected (song) item to another activity and also position of song in list.
            }
        });

    }

}