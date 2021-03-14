package com.example.nalone.ui.evenements.display;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.nalone.R;
import com.example.nalone.adapter.SearchEvenementAdapter;
import com.example.nalone.enumeration.FiltreDate;
import com.example.nalone.enumeration.FiltreEvent;
import com.example.nalone.enumeration.FiltreSort;
import com.example.nalone.enumeration.TypeEvent;
import com.example.nalone.enumeration.VisibilityMap;
import com.example.nalone.json.JSONController;
import com.example.nalone.json.JSONObjectCrypt;
import com.example.nalone.listeners.JSONArrayListener;
import com.example.nalone.objects.Evenement;
import com.example.nalone.ui.evenements.InfosEvenementsActivity;
import com.example.nalone.util.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.range;

public class SearchEventActivity extends AppCompatActivity {

    private TextView textViewType, textViewSort, textViewDate, textViewLocation, textViewPrice;
    private BottomSheetBehavior bottomSheetBehaviorDate, bottomSheetBehaviorType,bottomSheetBehaviorSort
            ,bottomSheetBehaviorPrice,bottomSheetBehaviorLocation,bottomSheetBehaviorParticipation;
    private View viewGrey, bottomSheetType,bottomSheetDate,bottomSheetSort,bottomSheetPrice,bottomSheetLocation, bottomSheetParticipation;
    public static TypeEvent currentType;
    private FiltreSort currentSort;
    private FiltreDate currentDate;
    private int currentLocation, currentPrice;
    private TextView textViewSortPertinence, textViewSortPriceASC,textViewSortPriceDESC,textViewSortLocation ,textViewSortDate;
    private TextView textViewDateToday,textViewDateTomorrow,textViewDateWeek,textViewDateMonth,textViewDateOther;
    private TextView textViewEventArt, textViewEventSport,textViewEventParty,textViewEventMusic,textViewEventMovie,textViewEventGame
            ,textViewEventCar,textViewEventGather,textViewEventConference,textViewEventShop,textViewEventShow,textViewEventScience;
    private SearchEvenementAdapter mAdapter;
    private RecyclerView mRecycler;
    private List<Evenement> evenementList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);
        currentSort = FiltreSort.PERTINENCE;
        currentDate = FiltreDate.NONE;
        currentLocation = Constants.range;

        viewGrey = findViewById(R.id.viewGrey);
        viewGrey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomExpandedToCollapsed();
            }
        });
        initTextView();
        initBottomSheet();
        selectedGenType();
        callJson();
    }

    private void initRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSearch);
        mAdapter = new SearchEvenementAdapter(evenementList);
        mAdapter.setOnItemClickListener(new SearchEvenementAdapter.OnItemClickListener() {
            @Override
            public void onDisplayClick(int position) {
                InfosEvenementsActivity.EVENT_LOAD = evenementList.get(position);
                //InfosEvenementsActivity.type = "nouveau";
            }

            @Override
            public void onParticipateClick(int position) {
                bottomSheetBehaviorParticipation.setState(BottomSheetBehavior.STATE_EXPANDED);
                viewGrey.setVisibility(View.VISIBLE);
            }
        });
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callJson(){
        JSONObjectCrypt params = new JSONObjectCrypt();

        params.addParameter("uid", USER.getUid());
        params.addParameter("category", currentType);
        params.addParameter("sort", currentSort);
        if(currentDate != FiltreDate.NONE)
            params.addParameter("date", currentDate);

        JSONController.getJsonArrayFromUrl(Constants.URL_EVENT_FILTRE, getBaseContext(), params, new JSONArrayListener() {
            @Override
            public void onJSONReceived(JSONArray jsonArray) {
                try {
                    evenementList = new ArrayList<>();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        evenementList.add((Evenement) JSONController.convertJSONToObject(jsonArray.getJSONObject(i), Evenement.class));
                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    Log.w("Response", "Erreur:"+e.getMessage());
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_event), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onJSONReceivedError(VolleyError volleyError) {
                Log.w("Response", "Erreur:"+volleyError.toString());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_event), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initTextView() {

        //TextView of event category
        initTextViewType();

        // TextView of sort filter
        initTextViewSort();

        // TextView of date filter
        initTextViewDate();
    }

    private void initTextViewDate() {
        textViewDateToday = findViewById(R.id.textViewDateToday);
        textViewDateToday.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualDate(FiltreDate.TODAY);
            }
        });
        textViewDateTomorrow = findViewById(R.id.textViewDateTomorrow);
        textViewDateTomorrow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualDate(FiltreDate.TOMORROW);
            }
        });
        textViewDateWeek = findViewById(R.id.textViewDateWeek);
        textViewDateWeek.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualDate(FiltreDate.WEEK);
            }
        });
        textViewDateMonth = findViewById(R.id.textViewDateMonth);
        textViewDateMonth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualDate(FiltreDate.MONTH);
            }
        });
        textViewDateOther = findViewById(R.id.textViewDateOther);
        textViewDateOther.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualDate(FiltreDate.OTHER);
            }
        });

    }

    private void initTextViewSort(){
        textViewType = findViewById(R.id.textViewType);
        changeTextViewType();
        textViewSort = findViewById(R.id.textViewSort);
        textViewSortDate = findViewById(R.id.textViewSortDate);
        textViewSortDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualSort(FiltreSort.DATE);
            }
        });
        textViewSortLocation = findViewById(R.id.textViewSortLocation);
        textViewSortLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualSort(FiltreSort.LOCATION);
            }
        });
        textViewSortPertinence = findViewById(R.id.textViewSortPertinence);
        textViewSortPertinence.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualSort(FiltreSort.PERTINENCE);
            }
        });
        textViewSortPriceASC = findViewById(R.id.textViewSortPriceASC);
        textViewSortPriceASC.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualSort(FiltreSort.PRICEASC);
            }
        });

        textViewSortPriceDESC = findViewById(R.id.textViewSortPriceDESC);
        textViewSortPriceDESC.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                changeActualSort(FiltreSort.PRICEDESC);
            }
        });

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewPrice = findViewById(R.id.textViewPrice);

        textViewDate = findViewById(R.id.textViewDate);
        textViewDateToday = findViewById(R.id.textViewDateToday);
        textViewDateTomorrow = findViewById(R.id.textViewDateTomorrow);
        textViewDateWeek = findViewById(R.id.textViewDateWeek);
        textViewDateMonth = findViewById(R.id.textViewDateMonth);
        textViewDateOther = findViewById(R.id.textViewDateOther);

        textViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCollapsedToExpanded(FiltreEvent.TYPE);
            }
        });

        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCollapsedToExpanded(FiltreEvent.LOCATION);
            }
        });

        textViewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCollapsedToExpanded(FiltreEvent.PRICE);
            }
        });

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCollapsedToExpanded(FiltreEvent.DATE);
            }
        });

        textViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomCollapsedToExpanded(FiltreEvent.SORT);
            }
        });
    }

    private void initTextViewType() {
        textViewEventArt = findViewById(R.id.textViewTypeArt);
        textViewEventSport = findViewById(R.id.textViewTypeSport);
        textViewEventParty = findViewById(R.id.textViewTypeParty);
        textViewEventMusic = findViewById(R.id.textViewTypeMusic);
        textViewEventMovie = findViewById(R.id.textViewTypeMovie);
        textViewEventGame = findViewById(R.id.textViewTypeGame);
        textViewEventCar = findViewById(R.id.textViewTypeCar);
        textViewEventGather = findViewById(R.id.textViewTypeGather);
        textViewEventConference = findViewById(R.id.textViewTypeConference);
        textViewEventShop = findViewById(R.id.textViewTypeShop);
        textViewEventShow = findViewById(R.id.textViewTypeShow);
        textViewEventScience = findViewById(R.id.textViewTypeScience);
        textViewEventArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.ART);
            }
        });
        textViewEventSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.SPORT);
            }
        });
        textViewEventParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.PARTY);
            }
        });
        textViewEventMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.MUSIC);
                            }
        });
        textViewEventMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.MOVIE);
                            }
        });
        textViewEventGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.GAME);
                             }
        });
        textViewEventCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.CAR);
                            }
        });
        textViewEventGather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.GATHER);
                           }
        });
        textViewEventConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.CONFERENCE);
                             }
        });
        textViewEventShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.SHOP);
                       }
        });
        textViewEventShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.SHOW);
                       }
        });
        textViewEventScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActualType(TypeEvent.SCIENCE);
                         }
        });
    }

    private void initBottomSheet() {
        bottomSheetType = findViewById(R.id.sheetType);
        bottomSheetBehaviorType= BottomSheetBehavior.from(bottomSheetType);

        bottomSheetDate = findViewById(R.id.sheetDate);
        bottomSheetBehaviorDate= BottomSheetBehavior.from(bottomSheetDate);

        bottomSheetSort = findViewById(R.id.sheetSort);
        bottomSheetBehaviorSort= BottomSheetBehavior.from(bottomSheetSort);

        bottomSheetLocation = findViewById(R.id.sheetLocation);
        bottomSheetBehaviorLocation= BottomSheetBehavior.from(bottomSheetLocation);

        bottomSheetPrice = findViewById(R.id.sheetPrice);
        bottomSheetBehaviorPrice= BottomSheetBehavior.from(bottomSheetPrice);

        bottomSheetParticipation = findViewById(R.id.sheetParticipate);
        bottomSheetBehaviorParticipation = BottomSheetBehavior.from(bottomSheetParticipation);
    }

    private void changeActualType(TypeEvent typeEvent){
        currentType = typeEvent;
        textViewEventArt.setTextColor(Color.DKGRAY);
        textViewEventSport.setTextColor(Color.DKGRAY);
        textViewEventParty.setTextColor(Color.DKGRAY);
        textViewEventMusic.setTextColor(Color.DKGRAY);
        textViewEventMovie.setTextColor(Color.DKGRAY);
        textViewEventGame.setTextColor(Color.DKGRAY);
        textViewEventCar.setTextColor(Color.DKGRAY);
        textViewEventGather.setTextColor(Color.DKGRAY);
        textViewEventConference.setTextColor(Color.DKGRAY);
        textViewEventShop.setTextColor(Color.DKGRAY);
        textViewEventShow .setTextColor(Color.DKGRAY);
        textViewEventScience.setTextColor(Color.DKGRAY);

        textViewEventArt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_art_grey, 0, 0, 0);
        textViewEventSport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_sport_grey, 0, 0, 0);
        textViewEventParty.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_party_grey,0, 0, 0);
        textViewEventMusic.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_music_grey,0, 0, 0);
        textViewEventMovie.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_movie_grey, 0, 0, 0);
        textViewEventGame.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_game_grey,0, 0, 0);
        textViewEventCar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_car_grey,0, 0, 0);
        textViewEventGather.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_gather_grey, 0, 0, 0);
        textViewEventConference.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_conference_grey, 0, 0, 0);
        textViewEventShop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_shop_grey, 0, 0, 0);
        textViewEventShow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_show_grey, 0, 0, 0);
        textViewEventScience.setCompoundDrawablesWithIntrinsicBounds(R.drawable.event_science_grey, 0, 0, 0);

        switch (typeEvent){
            case ART:selectedType(textViewEventArt);
            break;
            case CAR:selectedType(textViewEventCar);
                break;
            case GAME:selectedType(textViewEventGame);
                break;
            case SHOP:selectedType(textViewEventShop);
                break;
            case SHOW:selectedType(textViewEventShow);
                break;
            case MOVIE:selectedType(textViewEventMovie);
                break;
            case MUSIC:selectedType(textViewEventMusic);
                break;
            case PARTY:selectedType(textViewEventParty);
                break;
            case SPORT:selectedType(textViewEventSport);
                break;
            case SCIENCE:selectedType(textViewEventScience);
                break;
            case CONFERENCE:selectedType(textViewEventConference);
                break;
            case GATHER:selectedType(textViewEventGather);
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeActualDate(FiltreDate filtreDate){
        currentDate = filtreDate;
        textViewDateToday.setTextColor(Color.DKGRAY);
        textViewDateTomorrow.setTextColor(Color.DKGRAY);
        textViewDateWeek.setTextColor(Color.DKGRAY);
        textViewDateMonth.setTextColor(Color.DKGRAY);
        textViewDateOther.setTextColor(Color.DKGRAY);

        textViewDateToday.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24, 0, 0, 0);
        textViewDateTomorrow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24, 0, 0, 0);
        textViewDateWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24, 0, 0, 0);
        textViewDateMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24, 0, 0, 0);
        textViewDateOther.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24, 0, 0, 0);


        switch (filtreDate){
            case WEEK:selectedDate(textViewDateWeek,getResources().getString(R.string.filter_date_week));
                break;
            case MONTH:selectedDate(textViewDateMonth,getResources().getString(R.string.filter_date_month));
                break;
            case OTHER:selectedDate(textViewDateOther,getResources().getString(R.string.filter_date_choose));
                break;
            case TODAY:selectedDate(textViewDateToday,getResources().getString(R.string.filter_date_today));
                break;
            case TOMORROW:selectedDate(textViewDateTomorrow,getResources().getString(R.string.filter_date_tomorrow));
                break;
        }
        callJson();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void changeActualSort(FiltreSort filtreSort){
        currentSort = filtreSort;
        textViewSortPertinence.setTextColor(Color.DKGRAY);
        textViewSortPriceDESC.setTextColor(Color.DKGRAY);
        textViewSortPriceASC.setTextColor(Color.DKGRAY);
        textViewSortDate.setTextColor(Color.DKGRAY);
        textViewSortLocation.setTextColor(Color.DKGRAY);
        textViewSortPertinence.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filtre_sort, 0, 0, 0);
        textViewSortPriceDESC.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filtre_sort, 0, 0, 0);
        textViewSortPriceASC.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filtre_sort, 0, 0, 0);
        textViewSortDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filtre_sort, 0, 0, 0);
        textViewSortLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filtre_sort, 0, 0, 0);

        switch (filtreSort){
            case PERTINENCE:
                selectedSort(textViewSortPertinence,getResources().getString(R.string.filter_sort_pertinence));
                break;
            case PRICEDESC:selectedSort(textViewSortPriceDESC,getResources().getString(R.string.filter_sort_price_desc));
                break;
            case PRICEASC:selectedSort(textViewSortPriceASC,getResources().getString(R.string.filter_sort_price_asc));
                break;
            case DATE:selectedSort(textViewSortDate,getResources().getString(R.string.filter_sort_date));
                break;
            case LOCATION:selectedSort(textViewSortLocation,getResources().getString(R.string.filter_sort_location));
            break;
        }
        callJson();
    }

    private void selectedDate(TextView textView, String text){
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24_primary, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textViewDate.setText(text);
        textViewDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_event_24_primary, 0, 0, 0);
    }

    private void selectedType(TextView textView){
        textView.setCompoundDrawablesWithIntrinsicBounds(getDrawableType(currentType), 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        selectedGenType();
    }

    private void selectedGenType(){
        textViewType.setCompoundDrawablesWithIntrinsicBounds(getDrawableType(currentType), 0, 0, 0);
        textViewType.setText(getTextType(currentType));
    }

    private void selectedSort(TextView textView, String text){
        textViewSort.setText(text);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sort, 0, 0, 0);
    }

    private void bottomCollapsedToExpanded(FiltreEvent filtreEvent) {
        bottomExpandedToCollapsed();
        viewGrey.setVisibility(View.VISIBLE);
        switch (filtreEvent){
            case DATE:if(bottomSheetBehaviorDate.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case SORT:if(bottomSheetBehaviorSort.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehaviorSort.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case TYPE:if(bottomSheetBehaviorType.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehaviorType.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case PRICE:if(bottomSheetBehaviorPrice.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehaviorPrice.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case LOCATION:if(bottomSheetBehaviorLocation.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehaviorLocation.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
        }
    }

    private void bottomExpandedToCollapsed() {

        if(bottomSheetBehaviorPrice.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehaviorPrice.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if(bottomSheetBehaviorDate.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if(bottomSheetBehaviorSort.getState() == BottomSheetBehavior.STATE_EXPANDED ){
            bottomSheetBehaviorSort.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if(bottomSheetBehaviorLocation.getState() == BottomSheetBehavior.STATE_EXPANDED ){
            bottomSheetBehaviorLocation.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if(bottomSheetBehaviorType.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehaviorType.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if(bottomSheetBehaviorParticipation.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehaviorParticipation.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        viewGrey.setVisibility(View.GONE);
    }

    private void changeTextViewType(){
        textViewType.setCompoundDrawablesWithIntrinsicBounds(getDrawableType(currentType), 0, 0, 0);
        textViewType.setText(getTextType(currentType));
    }

    private int getDrawableType(TypeEvent te) {
        switch (te){
            case ART:return R.drawable.event_art;
            case CAR:return R.drawable.event_car;
            case GAME:return R.drawable.event_game;
            case SHOP:return R.drawable.event_shop;
            case SHOW:return R.drawable.event_show;
            case MOVIE:return R.drawable.event_movie;
            case MUSIC:return R.drawable.event_music;
            case PARTY:return R.drawable.event_party;
            case SPORT:return R.drawable.event_sport;
            case GATHER:return R.drawable.event_gather;
            case SCIENCE:return R.drawable.event_science;
            case CONFERENCE:return R.drawable.event_conference;
            default:return 0;
        }
    }

    private String getTextType(TypeEvent te) {
        switch (te){
            case ART:return getResources().getString(R.string.event_art);
            case CAR:return getResources().getString(R.string.event_car);
            case GAME:return getResources().getString(R.string.event_game);
            case SHOP:return getResources().getString(R.string.event_shop);
            case SHOW:return getResources().getString(R.string.event_show);
            case MOVIE:return getResources().getString(R.string.event_movie);
            case MUSIC:return getResources().getString(R.string.event_music);
            case PARTY:return getResources().getString(R.string.event_party);
            case SPORT:return getResources().getString(R.string.event_sport);
            case GATHER:return getResources().getString(R.string.event_gather);
            case SCIENCE:return getResources().getString(R.string.event_science);
            case CONFERENCE:return getResources().getString(R.string.event_conference);
            default:return "Error";
        }
    }
}