package com.example.nalone.ui.evenements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.nalone.Adapter.SectionPageAdapter;
import com.example.nalone.R;
import com.example.nalone.ui.amis.display.AmisFragment;
import com.example.nalone.ui.amis.display.GroupeFragment;
import com.example.nalone.ui.amis.display.Fragment_3;
import com.example.nalone.ui.evenements.EvenementsViewModel;
import com.example.nalone.ui.evenements.display.EvenementsListFragment;
import com.example.nalone.ui.evenements.display.MapFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

public class EvenementsFragment extends Fragment {

    private com.example.nalone.ui.evenements.EvenementsViewModel EvenementsViewModel;

    View myFragment;

    ViewPager viewPager;
    TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        EvenementsViewModel =
                ViewModelProviders.of(this).get(EvenementsViewModel.class);
        myFragment = inflater.inflate(R.layout.fragment_evenements, container, false);


        viewPager = myFragment.findViewById(R.id.viewPagerEvenement);
        tabLayout = myFragment.findViewById(R.id.tabLayoutEvenement);

        return myFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceBundle){
        super.onActivityCreated(savedInstanceBundle);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager(), 0);

        adapter.addFragment(new EvenementsListFragment(), "Les évènements");
        adapter.addFragment(new MapFragment(), "La carte des évènements");
        //adapter.addFragment(new Fragment_3(), "Mes invitations");

        viewPager.setAdapter(adapter);
    }


}