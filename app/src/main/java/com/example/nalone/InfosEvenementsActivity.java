package com.example.nalone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nalone.items.ItemImagePerson;
import com.example.nalone.ui.amis.display.AmisFragment;
import com.example.nalone.ui.evenements.CreateEventFragment;
import com.example.nalone.util.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.USER_ID;
import static com.example.nalone.util.Constants.dateFormat;
import static com.example.nalone.util.Constants.formatD;
import static com.example.nalone.util.Constants.mStore;
import static com.example.nalone.util.Constants.mStoreBase;
import static com.example.nalone.util.Constants.timeFormat;

public class InfosEvenementsActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTitle;
    private TextView mDate;
    private TextView mTimer;
    private TextView mOwner;
    private TextView mDescription;
    private FirestoreRecyclerAdapter adapter;
    private List<String> members = new ArrayList<>();

    public static Evenement EVENT_LOAD;

    public static String type;
    private View rootView;
    private NavController navController;
    private TextView textViewNbMembers;
    private TextView nbParticipants;
    private int participants;
    private Button buttonInscription;
    private Button buttonAnnuler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_infos_evenements, container, false);
        createFragment();
        return rootView;
    }

    private void createFragment() {
        participants = 0;
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        mTitle = rootView.findViewById(R.id.title);
        mDate = rootView.findViewById(R.id.date);
        mTimer = rootView.findViewById(R.id.time);
        mOwner = rootView.findViewById(R.id.owner);
        mDescription = rootView.findViewById(R.id.description);
        mRecyclerView = rootView.findViewById(R.id.recyclerViewMembresInscrits);
        textViewNbMembers = rootView.findViewById(R.id.textViewNbMembers);
        nbParticipants = rootView.findViewById(R.id.nbParticipants);
        buttonInscription = rootView.findViewById(R.id.buttonInscription);
        buttonInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incription();
            }
        });

        buttonAnnuler = rootView.findViewById(R.id.buttonAnnuler);
        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suppression();
            }
        });

        String date_text = formatD.format(EVENT_LOAD.getDate().toDate());
        String final_date_text = "";
        mTitle.setText(EVENT_LOAD.getName());
        mTimer.setText(timeFormat.format(EVENT_LOAD.getDate().toDate()));
        mOwner.setText(EVENT_LOAD.getOwner());
        mDescription.setText(EVENT_LOAD.getDescription());

        for (int i = 0; i < date_text.length() - 5; i++) {
            char character = date_text.charAt(i);
            if (i == 0) {
                character = Character.toUpperCase(character);
            }
            final_date_text += character;
        }

        mDate.setText(final_date_text);

        mStoreBase.collection("events").document(EVENT_LOAD.getUid())
                .collection("members").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult()){
                    members.add(doc.toObject(ModelData.class).getUser().getId());
                    participants ++;
                }
                nbParticipants.setText(participants+"");
                if(!members.isEmpty()){
                    Query query = mStoreBase.collection("users").whereIn("uid", members);
                    FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

                    adapter = new FirestoreRecyclerAdapter<User, InfosEvenementsActivity.UserViewHolder>(options) {
                        @NonNull
                        @Override
                        public InfosEvenementsActivity.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_user, parent, false);
                            return new InfosEvenementsActivity.UserViewHolder(view);
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        protected void onBindViewHolder(@NonNull final InfosEvenementsActivity.UserViewHolder userViewHolder, int i, @NonNull final User u) {
                            Constants.setUserImage(u, getContext(), userViewHolder.imagePerson);
                            if(u.getCursus().equalsIgnoreCase("Informatique")){
                                userViewHolder.cardViewUser.setCardBackgroundColor(Color.RED);
                            }

                            if(u.getCursus().equalsIgnoreCase("TC")){
                                userViewHolder.cardViewUser.setCardBackgroundColor(Color.parseColor("#00E9FD"));
                            }

                            if(u.getCursus().equalsIgnoreCase("MMI")){
                                userViewHolder.cardViewUser.setCardBackgroundColor(Color.parseColor("#FF1EED"));
                            }

                            if(u.getCursus().equalsIgnoreCase("GB")){
                                userViewHolder.cardViewUser.setCardBackgroundColor(Color.parseColor("#41EC57"));
                            }

                            if(u.getCursus().equalsIgnoreCase("LP")){
                                userViewHolder.cardViewUser.setCardBackgroundColor((Color.parseColor("#EC9538")));
                            }
                        }
                    };

                    mRecyclerView.setAdapter(adapter);
                    adapter.startListening();


                    mLayoutManager = new LinearLayoutManager(
                            getContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                }else{
                    mRecyclerView.setVisibility(View.GONE);
                }

            }
        });

        setData(type);
    }

    private void suppression() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Vous êtes sur le point de supprimer un évènement ! Cette action est irréversible ! \n Voulez-vous continuez ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mStoreBase.collection("events").document(EVENT_LOAD.getUid()).delete();
                        Toast.makeText(getContext(), "Vous avez supprimé(e) un évènement !", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }

    private void setData(String type) {
        if(type.equalsIgnoreCase("inscrit")){
            buttonInscription.setText("Se désinscrire");
            buttonInscription.setTextColor(Color.RED);
            buttonInscription.setBackground(getResources().getDrawable(R.drawable.custom_button_signup));
        }

        if(type.equalsIgnoreCase("creer")){
            buttonInscription.setText("Modifier");
            buttonAnnuler.setVisibility(View.VISIBLE);
            buttonInscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateEventFragment.EVENT_LOAD = EVENT_LOAD;
                    CreateEventFragment.edit = true;
                    navController.navigate(R.id.action_navigation_infos_events_to_navigation_create_event);
                }
            });
        }
    }

    private void incription() {
        ModelData owner = new ModelData("add", EVENT_LOAD.getOwnerDoc());
        ModelData m = new ModelData("add", mStoreBase.collection("users").document(USER_ID));
        mStoreBase.collection("events").document(EVENT_LOAD.getUid()).collection("members").document(USER.getUid()).set(m);
        mStoreBase.collection("users").document(USER_ID).collection("events").document(EVENT_LOAD.getUid()).set(owner);
        Toast.makeText(getContext(), "Vous êtes inscrit à l'évènement " + EVENT_LOAD.getName() + " !", Toast.LENGTH_SHORT).show();
        buttonInscription.setText("Se désinscrire");
        buttonInscription.setTextColor(Color.RED);
        buttonInscription.setBackground(getResources().getDrawable(R.drawable.custom_button_signup));
        createFragment();
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePerson;
        private CardView cardViewUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePerson = itemView.findViewById(R.id.imageUser);
            cardViewUser = itemView.findViewById(R.id.cardViewUser);


        }

    }
}