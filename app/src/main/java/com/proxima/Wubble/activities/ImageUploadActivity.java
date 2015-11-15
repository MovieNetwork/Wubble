package com.proxima.Wubble.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.proxima.Wubble.R;
import com.proxima.Wubble.misc.MyFileContentProvider;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;


/**
 * Created by Epokhe on 23.12.2014.
 */
//EASY COPY PASTE
public class ImageUploadActivity extends ToolbarActivity {

    private Context context;
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int CAMERA_REQUEST = 1888;


    private ImageView imageView;

    private boolean isTransferCompleted = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        imageView = (ImageView) findViewById(R.id.chosenimage);
        activateToolbar();

        context = this;


        Button imageFromGalleryButton = (Button) findViewById(R.id.imagefromGallery);
        Button imageFromCameraButton = (Button) findViewById(R.id.imagefromCamera);
        Button uploadPictureButton = (Button) findViewById(R.id.uploadpicture);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        int defaultPicId = this.getResources().getIdentifier("ic_action_user", "drawable", this.getPackageName());

        boolean hasPicture = currentUser.getBoolean("hasPicture");

        // if user does not have profile pic, set default picture aka ic_action_user
        if (!hasPicture) {
            imageView.setImageResource(defaultPicId);
        } else {
            ParseFile currentProfileImage = (ParseFile) currentUser.get("profilepic");

            currentProfileImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap tempBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(tempBmp);
                    } else {
                        Toast.makeText(ImageUploadActivity.this, "Profile Picture can not be shown", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }


        // select an image from internal memory
        imageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isTransferCompleted) {
                    Crop.pickImage(ImageUploadActivity.this);
                }
            }
        });

        imageFromCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                if (isTransferCompleted) {
                    PackageManager pm = getPackageManager();

                    if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider.CONTENT_URI);
                        startActivityForResult(i, TAKE_PICTURE);
                    } else {
                        Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

//
        // upload this at parse database into pictures
        uploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isTransferCompleted) {

                    isTransferCompleted = false;
                    String currentUserName = currentUser.getUsername();

                    imageView.buildDrawingCache();
                    Bitmap picture = imageView.getDrawingCache();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    // get byte array here
                    byte[] byteArray = stream.toByteArray();

                    if (byteArray != null) {
                        final ParseFile photoFile = new ParseFile(currentUserName + ".jpg", byteArray);
                        currentUser.put("profilepic", photoFile);
                        currentUser.put("hasPicture", true);

                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
                        query.whereEqualTo("User", currentUserName);
                        query.orderByDescending("createdAt");

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> wubbles, ParseException e) {
                                if (e == null) {
                                    for (int k = 0; k < wubbles.size(); k++) {
                                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Wubbles");

                                        // Retrieve the object by id
                                        query2.getInBackground(wubbles.get(k).getObjectId(), new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject aWubble, ParseException e) {
                                                if (e == null) {
                                                    aWubble.put("wubblePhoto", photoFile);
                                                    aWubble.saveInBackground();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // handle problems here
                                    e.printStackTrace();
                                }
                            }
                        });


                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(ImageUploadActivity.this, "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                } else {
                                    Intent toMain = new Intent(ImageUploadActivity.this, FeedActivity.class);
                                    isTransferCompleted = true;
                                    startActivity(toMain);
                                    finish();
                                }
                            }
                        });
                    }
                }
            }

        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Crop.REQUEST_PICK: {
                if (resultCode == RESULT_OK) {
                    Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    new Crop(data.getData()).output(outputUri).asSquare().start(this);
                }
                break;
            }

            case Crop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    imageView.setImageURI(null);//necessary
                    imageView.setImageURI(Crop.getOutput(data));
                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case TAKE_PICTURE: {

                if (resultCode == RESULT_OK) {

                    File out = new File(getFilesDir(), "newImage.jpg");
                    if (!out.exists()) {
                        Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
                    }

                    Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    Uri outputFileUri = Uri.fromFile(out);
                    new Crop(outputFileUri).output(outputUri).asSquare().start(this);
                    break;

                }
            }
        }
    }

}



