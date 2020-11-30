package com.example.nalone.ui.recherche;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nalone.Cache;
import com.example.nalone.Group;
import com.example.nalone.R;
import com.example.nalone.User;
import com.example.nalone.ui.amis.display.GroupeFragment;
import com.example.nalone.ui.amis.display.PopUpGroupFragment;
import com.example.nalone.ui.amis.display.PopupProfilFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.USER_ID;
import static com.example.nalone.util.Constants.mStore;
import static com.example.nalone.util.Constants.mStoreBase;

public class RechercheFragment extends Fragment {

    private RecyclerView recyclerAmis;
    private RecyclerView recyclerGroupes;
    private CardView cardViewRechercheAmis;
    private CardView cardViewRechercheGroupes;
    private View rootView;
    private NavController navController;
    private List<String> friends;
    private List<String> mesGroupes;
    private FirestoreRecyclerAdapter adapter;
    private TextView textViewRechercheAmis;
    private TextView textViewRechercheGroupes;
    private ProgressBar loading;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recherche, container, false);

        recyclerAmis = rootView.findViewById(R.id.recyclerViewRechercheAmis);
        recyclerGroupes = rootView.findViewById(R.id.recyclerViewRechercheGroupes);
        cardViewRechercheAmis = rootView.findViewById(R.id.cardViewRechercheAmis);
        cardViewRechercheGroupes = rootView.findViewById(R.id.cardViewRechercheGroupes);
        textViewRechercheAmis = rootView.findViewById(R.id.textViewRechercheAmis);
        textViewRechercheGroupes = rootView.findViewById(R.id.textViewRechercheGroupes);
        loading = rootView.findViewById(R.id.search_loading);
        cardViewRechercheAmis.setVisibility(View.INVISIBLE);
        cardViewRechercheGroupes.setVisibility(View.INVISIBLE);


        textViewRechercheAmis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_recherche_to_navigation_recherche_amis);
            }
        });

        textViewRechercheGroupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_recherche_to_navigation_rcherche_groupe);
            }
        });

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        adapterGroups();

        friends = new ArrayList<>();

        mStoreBase.collection("users").document(USER.getUid()).collection("friends").whereEqualTo("status","add")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                friends.add(document.getId());
                            }
                            friends.add(USER.getUid());
                            //query
                            Query query = mStoreBase.collection("users").whereNotIn("uid", friends).limit(3);
                            FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

                            adapterUsers(options);


                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });



        return rootView;


    }

    private void adapterGroups() {
        DocumentReference ref = mStoreBase.document("users/"+USER_ID);
        Query query = mStoreBase.collection("groups").whereNotEqualTo("ownerDoc", ref).limit(3);
        FirestoreRecyclerOptions<Group> options = new FirestoreRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();

        adapter = new FirestoreRecyclerAdapter<Group, GroupViewHolder>(options) {
            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupe, parent, false);
                return new GroupViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final GroupViewHolder userViewHolder, int i, @NonNull final Group g) {
                final Group group = g;
                userViewHolder.nomGroup.setText(g.getName());

                userViewHolder.layoutGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUpGroup(group);
                    }
                });

                userViewHolder.imageGroup.post(new Runnable() {
                    @Override
                    public void run() {
                        if(g.getImage_url() != null) {
                            if(!Cache.fileExists(g.getUid())) {
                                StorageReference imgRef = mStore.getReference("groups/" + g.getUid());
                                if (imgRef != null) {
                                    imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri img = task.getResult();
                                                if (img != null) {
                                                    Cache.saveUriFile(g.getUid(), img);
                                                    g.setImage_url(Cache.getImageDate(g.getUid()));
                                                    mStoreBase.collection("groups").document(g.getUid()).set(g);
                                                    Glide.with(getContext()).load(img).fitCenter().centerCrop().into(userViewHolder.imageGroup);
                                                }
                                            }
                                        }
                                    });
                                }
                            }else{
                                Uri imgCache = Cache.getUriFromUid(g.getUid());
                                if(Cache.getImageDate(g.getUid()).equalsIgnoreCase(g.getImage_url())) {
                                    Glide.with(getContext()).load(imgCache).fitCenter().centerCrop().into(userViewHolder.imageGroup);
                                }else{
                                    StorageReference imgRef = mStore.getReference("groups/" + g.getUid());
                                    if (imgRef != null) {
                                        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri img = task.getResult();
                                                    if (img != null) {
                                                        Cache.saveUriFile(g.getUid(), img);
                                                        g.setImage_url(Cache.getImageDate(g.getUid()));
                                                        mStoreBase.collection("groups").document(g.getUid()).set(g);
                                                        Glide.with(getContext()).load(img).fitCenter().centerCrop().into(userViewHolder.imageGroup);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };
        recyclerGroupes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerGroupes.setAdapter(adapter);
        adapter.startListening();
    }

    private class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView nomGroup;
        private LinearLayout layoutGroup;
        private ImageView imageGroup;
        private ImageView button;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            nomGroup = itemView.findViewById(R.id.nomGroupe);
            layoutGroup = itemView.findViewById(R.id.layoutGroupe);
            imageGroup = itemView.findViewById(R.id.imageGroupe);

        }
    }


    public void showPopUpGroup(final Group g) {
        PopUpGroupFragment.GROUP_LOAD = g;
        navController.navigate(R.id.action_navigation_recherche_to_navigation_popup_group);
    }

    private void adapterUsers(FirestoreRecyclerOptions<User> options) {
        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.w("Add", "ViewHolder Recherche");
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person,parent,false);
                return new UserViewHolder(view);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int i, @NonNull final User u) {
                userViewHolder.villePers.setText(u.getCity());
                userViewHolder.nomInvit.setText(u.getFirst_name() + " "+ u.getLast_name());

                if(u.getCursus().equalsIgnoreCase("Informatique")){
                    userViewHolder.cardViewPhotoPerson.setCardBackgroundColor(Color.RED);
                }

                if(u.getCursus().equalsIgnoreCase("TC")){
                    userViewHolder.cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#00E9FD"));
                }

                if(u.getCursus().equalsIgnoreCase("MMI")){
                    userViewHolder.cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#FF1EED"));
                }

                if(u.getCursus().equalsIgnoreCase("GB")){
                    userViewHolder.cardViewPhotoPerson.setCardBackgroundColor(Color.parseColor("#41EC57"));
                }

                if(u.getCursus().equalsIgnoreCase("LP")){
                    userViewHolder.cardViewPhotoPerson.setCardBackgroundColor((Color.parseColor("#EC9538")));
                }

                userViewHolder.button.setImageResource(0);
                Log.w("Add", "BienHolder Recherche");
                if(u.getImage_url() != null) {
                    if(!Cache.fileExists(u.getUid())) {
                        StorageReference imgRef = mStore.getReference("users/" + u.getUid());
                        if (imgRef != null) {
                            imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri img = task.getResult();
                                        if (img != null) {
                                            Cache.saveUriFile(u.getUid(), img);
                                            u.setImage_url(Cache.getImageDate(u.getUid()));
                                            mStoreBase.collection("users").document(u.getUid()).set(u);
                                            Glide.with(getContext()).load(img).fitCenter().centerCrop().into(userViewHolder.imagePerson);
                                        }
                                    }
                                }
                            });
                        }
                    }else{
                        Uri imgCache = Cache.getUriFromUid(u.getUid());
                        Log.w("Cache", "Image Cache : " + Cache.getImageDate(u.getUid()));
                        Log.w("Cache", "Data Cache : " + u.getImage_url());
                        if(Cache.getImageDate(u.getUid()).equalsIgnoreCase(u.getImage_url())) {
                            Log.w("image", "get image from cache");
                            Glide.with(getContext()).load(imgCache).fitCenter().centerCrop().into(userViewHolder.imagePerson);
                        }else{
                            StorageReference imgRef = mStore.getReference("users/" + u.getUid());
                            if (imgRef != null) {
                                imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri img = task.getResult();
                                            if (img != null) {
                                                Cache.saveUriFile(u.getUid(), img);
                                                u.setImage_url(Cache.getImageDate(u.getUid()));
                                                mStoreBase.collection("users").document(u.getUid()).set(u);
                                                Glide.with(getContext()).load(img).fitCenter().centerCrop().into(userViewHolder.imagePerson);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

                userViewHolder.layoutProfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUpProfil(u);
                    }
                });
                loading.setVisibility(View.GONE);
                cardViewRechercheAmis.setVisibility(View.VISIBLE);
                cardViewRechercheGroupes.setVisibility(View.VISIBLE);
            }
        };
        Log.w("Add", "Set Adapter Recherche");
        recyclerAmis.setHasFixedSize(true);
        recyclerAmis.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerAmis.setAdapter(adapter);
        adapter.startListening();

    }
    private class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView nomInvit;
        private TextView villePers;
        private LinearLayout layoutProfil;
        private ImageView imagePerson;
        private ImageView button;
        private CardView cardViewPhotoPerson;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            nomInvit = itemView.findViewById(R.id.nomInvit);
            villePers = itemView.findViewById(R.id.villePers);
            layoutProfil = itemView.findViewById(R.id.layoutProfil);
            imagePerson = itemView.findViewById(R.id.imagePerson);
            button = itemView.findViewById(R.id.buttonImage);
            cardViewPhotoPerson = itemView.findViewById(R.id.cardViewPhotoPerson);

        }

    }

    public void showPopUpProfil(User u) {

        PopupProfilFragment.USER_LOAD = u;
        PopupProfilFragment.button = R.drawable.ic_baseline_add_circle_outline_24;

        PopupProfilFragment.type = "recherche";
        navController.navigate(R.id.action_navigation_recherche_to_navigation_popup_profil);
    }
}