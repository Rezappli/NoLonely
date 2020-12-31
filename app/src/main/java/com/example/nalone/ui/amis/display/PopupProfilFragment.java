package com.example.nalone.ui.amis.display;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.nalone.Cache;
import com.example.nalone.Notification;
import com.example.nalone.R;
import com.example.nalone.TypeNotification;
import com.example.nalone.User;
import com.example.nalone.ModelData;
import com.example.nalone.fcm.MySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.nalone.HomeActivity.buttonBack;
import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.USER_REFERENCE;
import static com.example.nalone.util.Constants.mStore;
import static com.example.nalone.util.Constants.mStoreBase;
import static com.example.nalone.util.Constants.sendNotification;

public class PopupProfilFragment extends Fragment {


    public static User USER_LOAD;
    public static int button = 0;
    private NavController navController;
    public static String type;

    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_popup_profil, container, false);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        buttonBack.setVisibility(View.VISIBLE);
        if(type == "amis"){
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.action_navigation_popup_profil_to_navigation_amis2);
                }
            });
        }
        if(type == "recherche"){
            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.action_navigation_popup_profil_to_navigation_recherche_amis);
                }
            });
        }
        TextView nameProfil;
        TextView descriptionProfil;
        TextView nbCreateProfil;
        TextView nbParticipateProfil;
        TextView villeProfil;

        final ImageView imagePerson;
        final ImageView buttonAdd;
        CardView cardViewPhotoPerson;

        nameProfil = root.findViewById(R.id.profilName);
        descriptionProfil = root.findViewById(R.id.profilDescription);
        nbCreateProfil = root.findViewById(R.id.nbEventCreate);
        nbParticipateProfil = root.findViewById(R.id.nbEventParticipe);
        imagePerson = root.findViewById(R.id.imagePerson);
        buttonAdd = root.findViewById(R.id.buttonAdd);
        cardViewPhotoPerson = root.findViewById(R.id.cardViewPhotoPerson);
        villeProfil = root.findViewById(R.id.userConnectVille);

        nameProfil.setText(USER_LOAD.getFirst_name() + " " + USER_LOAD.getLast_name());
        villeProfil.setText(USER_LOAD.getCity());
        nbCreateProfil.setText(USER_LOAD.getNumber_events_create());
        nbParticipateProfil.setText(USER_LOAD.getNumber_events_attend());

        if(USER_LOAD.getCursus().equalsIgnoreCase("Informatique")){
            cardViewPhotoPerson.setCardBackgroundColor(Color.RED);
        }
        if(USER_LOAD.getCursus().equalsIgnoreCase("TC")){
            cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#00E9FD"));
        }
        if(USER_LOAD.getCursus().equalsIgnoreCase("MMI")){
            cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#FF1EED"));
        }
        if(USER_LOAD.getCursus().equalsIgnoreCase("GB")){
            cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#41EC57"));
        }
        if(USER_LOAD.getCursus().equalsIgnoreCase("LP")){
            cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#EC9538"));
        }

        if (USER_LOAD.getImage_url() != null) {
            if(!Cache.fileExists(USER_LOAD.getUid())) {
                StorageReference imgRef = mStore.getReference("users/" + USER_LOAD.getUid());
                if (imgRef != null) {
                    imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri img = task.getResult();
                                if (img != null) {
                                    Log.w("image", "save image from cache");
                                    Cache.saveUriFile(USER_LOAD.getUid(), img);
                                    Glide.with(getContext()).load(img).fitCenter().centerCrop().into(imagePerson);
                                }
                            }
                        }
                    });
                }
            }else{
                Log.w("image", "get image from cache");
                Glide.with(getContext()).load(Cache.getUriFromUid(USER_LOAD.getUid())).fitCenter().centerCrop().into(imagePerson);
            }
        }

        List<ImageView> imageCentreInteret = new ArrayList<>();

        ImageView img_centre1 = root.findViewById(R.id.imageViewCI1);
        ImageView img_centre2 = root.findViewById(R.id.imageViewCI2);
        ImageView img_centre3 = root.findViewById(R.id.imageViewCI3);
        ImageView img_centre4 = root.findViewById(R.id.imageViewCI4);
        ImageView img_centre5 = root.findViewById(R.id.imageViewCI5);

        imageCentreInteret.add(img_centre1);
        imageCentreInteret.add(img_centre2);
        imageCentreInteret.add(img_centre3);
        imageCentreInteret.add(img_centre4);
        imageCentreInteret.add(img_centre5);

        /*if(USER_LOAD.getCenters_interests() != null){
        for(int i = 0; i < USER_LOAD.getCenters_interests().size(); i++) {
            int imgResource = 0;
            if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("programmation")) {
                imgResource = R.drawable.ci_programmation;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("musique")) {
                imgResource = R.drawable.ci_musique;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("livre")) {
                imgResource = R.drawable.ci_livre;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("film")) {
                imgResource = R.drawable.ci_film;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("video")) {
                imgResource = R.drawable.ci_jeuxvideo;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("peinture")) {
                imgResource = R.drawable.ci_peinture;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("photo")) {
                imgResource = R.drawable.ci_photo;
            } else if (USER_LOAD.getCenters_interests().get(i).toString().equalsIgnoreCase("sport")) {
                imgResource = R.drawable.ci_sport;
            }

            imageCentreInteret.get(i).setImageResource(imgResource);
            imageCentreInteret.get(i).setVisibility(View.VISIBLE);
        }

        }*/


        nameProfil.setText(USER_LOAD.getLast_name() + " " + USER_LOAD.getFirst_name());
        descriptionProfil.setText(USER_LOAD.getDescription());
        nbCreateProfil.setText(USER_LOAD.getNumber_events_create());
        nbParticipateProfil.setText(USER_LOAD.getNumber_events_attend());
        buttonAdd.setImageResource(button);

        if (USER_LOAD.getDescription().matches("")) {
            descriptionProfil.setVisibility(View.GONE);
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Button", "Button : " + button);
                if (button == R.drawable.ic_round_mail_24) {
                    Toast.makeText(getContext(), "Vous avez reçu une demande d'amis de la part de cet utilisateur !", Toast.LENGTH_SHORT).show();
                } else if (button == R.drawable.ic_round_hourglass_top_24) {
                    Toast.makeText(getContext(), "Votre demande d'amis est en attente !", Toast.LENGTH_SHORT).show();
                } else {

                        Toast.makeText(getContext(), "Vous avez envoyé une demande d'amis !", Toast.LENGTH_SHORT).show();
                        addFriend();
                        buttonAdd.setImageResource(R.drawable.ic_round_hourglass_top_24);

                }
            }
        });



        return root;
    }

    public void addFriend() {
        ModelData data1 = new ModelData("received", mStoreBase.collection("users").document(USER.getUid()));
        ModelData data2 = new ModelData("send", mStoreBase.collection("users").document(USER_LOAD.getUid()));
        mStoreBase.collection("users").document(USER.getUid()).collection("friends").document(USER_LOAD.getUid()).set(data2);
        mStoreBase.collection("users").document(USER_LOAD.getUid()).collection("friends").document(USER.getUid()).set(data1);

        Notification.createDemandeAmi(USER_LOAD);

        TOPIC = "/topics/"+ USER_LOAD.getUid(); //topic must match with what the receiver subscribed to
        Log.w("TOPIC", "Topic : " + TOPIC);
        NOTIFICATION_TITLE = "Toc toc toc...";
        NOTIFICATION_MESSAGE = USER.getFirst_name() + " " + USER.getLast_name() + " vient de vous envoyer une demande d'amis !";

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notifcationBody.put("sender", USER.getUid());
            notifcationBody.put("type", "invitation");

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification, getContext());

    }

}