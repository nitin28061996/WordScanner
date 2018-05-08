package com.example.lalith.wordscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnProcess,btnUpload;
    private static final int PICK_IMAGE_REQUEST = 234;
    TextView txtResult;
    private Uri filePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.image_view);
        btnProcess=(Button)findViewById(R.id.button);
        btnUpload=(Button)findViewById(R.id.button1);
        txtResult=(TextView)findViewById(R.id.textview);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });







        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null)
                    Toast.makeText(getApplicationContext(), "Upload an image", Toast.LENGTH_LONG).show();
                else {
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!textRecognizer.isOperational()) {
                        Log.e("ERROR", "Detector dependencies not found");
                    } else {

                        if (textRecognizer.isOperational() && bitmap != null) {
                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
                            String blocks = "";
                            String lines = "";
                            String words = "";
                            for (int index = 0; index < textBlocks.size(); index++) {
                                //extract scanned text blocks here
                                TextBlock tBlock = textBlocks.valueAt(index);
                                blocks = blocks + tBlock.getValue() + "\n" + "\n";
                                for (Text line : tBlock.getComponents()) {
                                    //extract scanned text lines here
                                    lines = lines + line.getValue() + "\n";

                                    for (Text element : line.getComponents()) {
                                        //extract scanned text words here
                                        words = words + element.getValue() + ", ";
                                    }
                                }
                            }
                            txtResult.setText(words.toString());
                            txtResult.setMovementMethod(new ScrollingMovementMethod());
                        }
                    }
                }
            }
        });
        }


    private void showFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            filePath=data.getData();
            try {
                 bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
