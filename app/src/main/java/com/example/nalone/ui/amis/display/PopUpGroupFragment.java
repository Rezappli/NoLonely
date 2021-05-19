package com.example.nalone.ui.amis.display;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.nalone.R;
import com.example.nalone.enumeration.Visibility;
import com.example.nalone.objects.Group;
import com.example.nalone.objects.ModelData;
import com.example.nalone.util.Constants;
import static com.example.nalone.HomeActivity.buttonBack;
import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.USER_ID;


public class PopUpGroupFragment extends Fragment {

    public static Group GROUP_LOAD;
    private NavController navController;

    public static String type;
    private TextView nameGroup, ownerGroup;
    private TextView descriptionGroup;
    private TextView nbCreateGroup;
    private TextView nbParticipateGroup;

    private ImageView imageGroup;
    private TextView visibilityGroup, textViewNbMembers;
    private Button buttonAdd;
    private CardView cardViewPhotoPerson;
    private Button buttonVoirMembres;
    private int nbMembers;
    private RelativeLayout relativeLayout;

    FrameLayout fenetrePrincipal;
    View root;
    private boolean buttonQuitter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pop_up_group, container, false);

        createFragment(root);

        return root;
    }

    private void createFragment(final View root) {
        nbMembers = 0;
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        textViewNbMembers = root.findViewById(R.id.groupNbMembers);
        buttonVoirMembres = root.findViewById(R.id.buttonVoirMembres);
        buttonBack.setVisibility(View.VISIBLE);
        fenetrePrincipal = root.findViewById(R.id.fenetrePrincipal);
        fenetrePrincipal.setVisibility(View.GONE);
    }

}
