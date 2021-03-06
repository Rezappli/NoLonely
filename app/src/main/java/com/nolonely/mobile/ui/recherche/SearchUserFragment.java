package com.nolonely.mobile.ui.recherche;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.nolonely.mobile.R;
import com.nolonely.mobile.adapter.user.SearchUserAdapter;
import com.nolonely.mobile.bdd.json.JSONController;
import com.nolonely.mobile.bdd.json.JSONFragment;
import com.nolonely.mobile.bdd.json.JSONObjectCrypt;
import com.nolonely.mobile.items.ItemPerson;
import com.nolonely.mobile.listeners.JSONArrayListener;
import com.nolonely.mobile.objects.User;
import com.nolonely.mobile.ui.amis.display.InfoUserActivity;
import com.nolonely.mobile.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.nolonely.mobile.util.Constants.USER;

public class SearchUserFragment extends JSONFragment {

    private NavController navController;
    private RecyclerView mRecyclerView;
    private SearchUserAdapter mAdapter;

    private TextView resultat;

    private List<ItemPerson> items = new ArrayList<>();
    private View rootView;
    private ProgressBar loading;
    // private ImageView qr_code;
    private List<User> friends;

    private SwipeRefreshLayout swipeContainer;

    private LinearLayout linearSansRechercheAmis;
    private String searchQuery = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recherche_amis, container, false);
        linearSansRechercheAmis = rootView.findViewById(R.id.linearSansRechercheGroupe);
        swipeContainer = rootView.findViewById(R.id.AmisSwipeRefreshLayout);
        resultat = rootView.findViewById(R.id.resultatText);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        configureRecyclerViewAmis();

        launchJSONCall(true);
        createFragment();
        return rootView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friends = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createFragment() {
        loading = rootView.findViewById(R.id.search_loading);


        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);

        this.configureSwipeRefreshLayout();

      /*  qr_code = rootView.findViewById(R.id.qr_code_image);

        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CameraActivity.class));
            }
        });*/


        resultat.setVisibility(View.GONE);
        //navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);


/*        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar.onActionViewExpanded();
            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });*/


    }

    private void configureRecyclerViewAmis() {
        this.mAdapter = new SearchUserAdapter(this.friends);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAdapter.setOnItemClickListener(position -> showProfil(friends.get(position)));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(llm);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getUsers() {

        JSONObjectCrypt params = new JSONObjectCrypt();
        params.putCryptParameter("uid", USER.getUid());
        params.putCryptParameter("limit1", 0); //fix a limit to 10 users
        params.putCryptParameter("limit2", 20); //fix a limit to 10 users
        params.putCryptParameter("longitude", USER.getLongitude()); //fix a limit to 10 users
        params.putCryptParameter("latitude", USER.getLatitude()); //fix a limit to 10 users
        if (searchQuery != null) {
            params.putCryptParameter("search", searchQuery);
        }

        JSONController.getJsonArrayFromUrl(Constants.URL_USER_WHITHOUT_ME, getContext(), params, new JSONArrayListener() {
            @Override
            public void onJSONReceived(JSONArray jsonArray) {
                try {
                    friends.clear();

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            friends.add((User) JSONController.convertJSONToObject(jsonArray.getJSONObject(i), User.class));
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    if (friends.isEmpty()) {
                        linearSansRechercheAmis.setVisibility(View.VISIBLE);
                    } else {
                        linearSansRechercheAmis.setVisibility(View.GONE);
                    }


                    loading.setVisibility(View.GONE);

                    swipeContainer.setRefreshing(false);

                } catch (JSONException e) {
                    Log.w("Response", "Valeur" + jsonArray.toString());
                    Log.w("Response", "Erreur:" + e.getMessage());
                    Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onJSONReceivedError(VolleyError volleyError) {
                Log.w("Response", "Erreur:" + volleyError.toString());
                Toast.makeText(getContext(), getResources().getString(R.string.error_event), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() {
                launchJSONCall(true);
            }
        });
    }


    public void showProfil(User u) {
        Intent intent = new Intent(getContext(), InfoUserActivity.class);
        intent.putExtra("user", u);
        intent.putExtra("isFriend", false);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void doInHaveInternetConnection() {
        getUsers();
    }

    public void search(String query) {
        searchQuery = query;
        launchJSONCall(true);
    }


}