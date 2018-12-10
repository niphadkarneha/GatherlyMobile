package com.example.sample.chatting.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sample.chatting.OpenImageActivity;
import com.example.sample.chatting.R;
import com.example.sample.chatting.data.SharedPreferenceHelper;
import com.example.sample.chatting.data.StaticConfig;
import com.example.sample.chatting.model.Configuration;
import com.example.sample.chatting.model.Consersation;
import com.example.sample.chatting.model.GlobalMessage;
import com.example.sample.chatting.model.Message;
import com.example.sample.chatting.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.format.DateFormat;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hassan Javaid on 11/22/2018.
 */

public class GlobalChatFragment extends Fragment {

    public Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int PICK_FILE_CODE = 72;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ListView recyclerChat;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private ImageButton attach;
    public String downloadUrl;
    private FirebaseListAdapter<GlobalMessage> adapter;
    String filename;
    View mView;


    public GlobalChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_globalchat,container,false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        recyclerChat = (ListView) layout.findViewById(R.id.recyclerChat);
        attach = (ImageButton) layout.findViewById(R.id.attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFile(v);
            }
        });
        btnSend = (ImageButton) layout.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWriteMessage = (EditText) layout.findViewById(R.id.editWriteMessage);
                GlobalMessage message = new GlobalMessage();
                message.setMessageText(editWriteMessage.getText().toString());
                message.setMessageUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                message.setMessageTime(new Date().getTime());
                FirebaseDatabase.getInstance().getReference("GlobalChat").push().setValue(message);
                editWriteMessage.setText("");
            }
        });
        displayMessage();
        return layout;
    }

    private void displayMessage() {

        adapter = new FirebaseListAdapter<GlobalMessage>(getActivity(),GlobalMessage.class,R.layout.customglobalchat,FirebaseDatabase.getInstance().getReference("GlobalChat")) {
            @Override
            protected void populateView(View v, final GlobalMessage model, int position) {

                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                ImageView group_image = (ImageView)v.findViewById(R.id.group_image);
                TextView message_file = (TextView)v.findViewById(R.id.message_file);
                // Set their text

                group_image.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(model.getImageUrl()).into(group_image);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                message_file.setVisibility(View.VISIBLE);
                message_file.setText(model.getFileUrl());

               group_image.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       String image_url = model.getImageUrl();
                       if(image_url.startsWith("https"))
                       {
                           Intent intent = (new Intent(getActivity(),OpenImageActivity.class));
                           intent.putExtra("url",image_url);
                           startActivity(intent);
                       }
                   }
               });

                message_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(model.getFileUrl()));
                        startActivity(intent);
                    }
                });
                // Format the date before showing it
                messageTime.setText(DateFormat.format("HH:mm",
                        model.getMessageTime()));
            }
        };
        recyclerChat.setAdapter(adapter);
    }

    private void SelectFile(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),v);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_file:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                            startActivity(intent);
                        }
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FILE_CODE);
                        return true;
                    case R.id.menu_image:
                        if (checkStoragePermission())
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, 432);
                            }
                        }
                        else
                            goToImageIntent();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                group_image.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //when the user choses the file
       else if (requestCode == PICK_FILE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {


                //uploading the file
                uploadFile(data.getData());
            } else {
                Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadFile(Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setCancelable(false);

        StorageReference sRef = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                        GlobalMessage globalMessage = new GlobalMessage();
                        globalMessage.setMessageUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        globalMessage.setMessageTime(new Date().getTime());
                        globalMessage.setFileUrl(taskSnapshot.getDownloadUrl().toString());
                        FirebaseDatabase.getInstance().getReference("GlobalChat").push().setValue(globalMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    public void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference ref = storageReference.child("images/"+  System.currentTimeMillis() + "." + GetFileExtension(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
//
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            GlobalMessage newMessage = new GlobalMessage();
                            newMessage.setMessageTime(new Date().getTime());
                            newMessage.setMessageUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            newMessage.setImageUrl(downloadUrl);
                            FirebaseDatabase.getInstance().getReference("GlobalChat").push().setValue(newMessage);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
//                            displayMessage("Upload Failed");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    private void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 432)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

}
