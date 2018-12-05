package com.example.sample.chatting.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sample.chatting.Helper.CustomAdapter;
import com.example.sample.chatting.MainActivity;
import com.example.sample.chatting.R;
import com.example.sample.chatting.data.SharedPreferenceHelper;
import com.example.sample.chatting.data.StaticConfig;
import com.example.sample.chatting.model.Configuration;
import com.example.sample.chatting.model.User;
import com.example.sample.chatting.util.ImageUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Hassan Javaid on 11/22/2018.
 */

public class NavigationDrawerFragment extends Fragment {

    TabLayout tableLayout;
    ImageView profile_image;
    TextView profile_name;
    TextView view_profile;
    ListView listView;
    CustomAdapter customAdapter;
    int images[] = {R.drawable.socialgroup,R.drawable.invite,R.drawable.help };
    String names[] = {"Groups"  , "Invite a friend" , "Help"};
    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private User myAccount;
    private Context context;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigationdrawer,container,false);
        tableLayout = (TabLayout) ((MainActivity)getActivity()).findViewById(R.id.tabs);
        profile_image = (ImageView)layout.findViewById(R.id.profile_image);
        profile_name = (TextView)layout.findViewById(R.id.profile_name);
        view_profile = (TextView)layout.findViewById(R.id.view_profile);
        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tableLayout.getTabAt(2).select();

            }
        });
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
        mAuth = FirebaseAuth.getInstance();
        context = layout.getContext();
        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(context);
        myAccount = prefHelper.getUserInfo();
        setImageAvatar(context, myAccount.avata);
        profile_name.setText(myAccount.name);

        listView = (ListView) layout.findViewById(R.id.profile_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) {
                    tableLayout.getTabAt(1).select();
                }
                if(position == 1)
                {
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Gatherly");
                        String strShareMessage = "\n Join the conversation today \n\n";
                        strShareMessage = strShareMessage + "http://qav2.cs.odu.edu/fordFanatics/index.php";
                        i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                        startActivity(Intent.createChooser(i, "Share via"));
                    } catch(Exception e) {
                        //e.toString();
                    }
                }
                if (position == 2)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://qav2.cs.odu.edu/fordFanatics/index.php")));
                }

            }
        });
        customAdapter = new CustomAdapter(getActivity(),names,images);
        listView.setAdapter(customAdapter);
        return layout;
    }


    private void setImageAvatar(Context context, String imgBase64){
        try {
            Resources res = getResources();
            //If you do not have an avatar, leave the default image
            Bitmap src;
            if (imgBase64.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }

            profile_image.setImageDrawable(ImageUtils.roundedImage(context, src));
        }catch (Exception e){
        }
    }
}
