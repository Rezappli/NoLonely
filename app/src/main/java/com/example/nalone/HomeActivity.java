package com.example.nalone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.nalone.ui.amis.MesAmisFragment;
import com.example.nalone.ui.evenements.EvenementsFragment;
import com.example.nalone.ui.message.MessagesFragment;
import com.example.nalone.ui.recherche.RechercheFragment;
import com.example.nalone.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity{

    public static String user_mail;
    public static String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Constants.NALONE_BUNDLE != null){
            Log.w("Fragment", "Lecture d'un save instance");
            String s = Constants.NALONE_BUNDLE.getString("MyArrayList");
            Log.w("Fragment", "Valeur : "+s);
        }

        ErrorClass.activity = this;
        ErrorClass.checkInternetConnection();
        setContentView(R.layout.activity_home);




        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_recherche, R.id.navigation_amis, R.id.navigation_evenements, R.id.navigation_messages)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        checkUserRegister();
    }


    private void checkUserRegister() {
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            user_mail = acct.getEmail();
        }

            DatabaseReference id_users = FirebaseDatabase.getInstance().getReference("id_users");
            id_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String id_users_text = snapshot.getValue(String.class);
                    int nb_users = Integer.parseInt(id_users_text);
                    for (int i = 0; i < nb_users; i++) {

                        DatabaseReference authentificationRef = FirebaseDatabase.getInstance().getReference("authentification/" + i);
                        final int finalI = i;
                        authentificationRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                String mail = snapshot.child("mail").getValue(String.class);
                                Log.w("Connexion", "Essaye avec la mail : "+mail);
                                Log.w("Connexion", "Essaye avec la mail : "+user_mail);
                                if (mail.equalsIgnoreCase(user_mail)) {
                                    user_id = finalI+"";
                                    Log.w("Connexion", "Mail check trouvé");
                                    Log.w("Connexion", "User connecté avec l'id:"+ user_id);
                                    user_mail = mail;
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("authentification/");
                                    final String finalUser_id = user_id;
                                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.hasChild(finalUser_id)) {
                                                Intent intent = new Intent(getBaseContext(), SignUpInformationActivity.class);
                                                startActivityForResult(intent, 0);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if(i == nb_users){
                            if(user_id == null) {
                                Log.w("Connexion", "User inconnu dans la base");
                                Intent intent = new Intent(getBaseContext(), SignUpInformationActivity.class);
                                startActivityForResult(intent, 0);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        user_mail = user_mail.replace(".", ",");
        Log.w("User","User mail:" + user_mail);
    }

    public void getGoogleInformations(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            
            String personEmail = acct.getEmail();
            acct.getPhotoUrl();
            String personId = acct.getId();
            System.out.println("**** INFORMATIIONS ****");
            System.out.println(personName);
            System.out.println(personGivenName);
            System.out.println(personFamilyName);
            System.out.println(personEmail);
            System.out.println(personId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.COUNTER = 0;
        ErrorClass.checkInternetConnection();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Constants.COUNTER ++;
        if(Constants.COUNTER == 1){
            CustomToast t = new CustomToast(getBaseContext(), "Appuyer de nouveau pour quitter", false, true);
            t.show();
        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            CustomToast t = new CustomToast(getBaseContext(), "Appuyer de nouveau pour quitter", false, true);
            t.show();    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}