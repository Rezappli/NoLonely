package com.nolonely.mobile.ui.evenements.creation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nolonely.mobile.R;
import com.nolonely.mobile.bdd.json.JSONController;
import com.nolonely.mobile.bdd.json.JSONObjectCrypt;
import com.nolonely.mobile.enumeration.ImageType;
import com.nolonely.mobile.enumeration.TypeEvent;
import com.nolonely.mobile.enumeration.Visibility;
import com.nolonely.mobile.listeners.JSONObjectListener;
import com.nolonely.mobile.objects.Evenement;
import com.nolonely.mobile.signUpActivities.SpinnerAdapter;
import com.nolonely.mobile.util.Constants;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.nolonely.mobile.util.Constants.USER;

public class MainCreationEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static NavController navController;
    public static ImageView buttonBack, buttonNotif;
    public static Evenement currentEvent;
    private boolean photoValidate, dateValidate, nameValidate, membersValidate, addressValidate, costValidate;
    public static Uri image = null;

    private static Activity activity;

    private BottomSheetBehavior bottomSheetBehavior, bottomSheetBehaviorType;
    private View bottomSheetType, viewGrey, bottomSheet;
    private TextView textViewVisibility, textViewType;

    private CardView cardViewPublic, cardViewPrivate;

    private ImageView imageProgessCreationDate, imageProgessCreationMembers,
            imageProgessCreationName, imageProgessCreationPosition, imageProgessCreationPhoto, imageProgressCreationCost;
    private CardView cardViewProgressCreationDate, cardViewProgressCreationMembers,
            cardViewProgressCreationName, cardViewProgressCreationPosition, cardViewProgressCreationPhoto, cardViewProgressCreationCost;
    private TextView textViewTitleEvent;
    public static String ACTION_RECEIVE_FRAGMENT = "ACTION_RECEIVE_FRAGMENT";
    public static String ACTION_RECEIVE_NEXT_CLICK = "ACTION_RECEIVE_NEXT_CLICK";

    private boolean isTypeChoosed;

    private final BroadcastReceiver receiverFragmentValidate = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                CurrentFragment currentFragment = CurrentFragment.valueOf(intent.getStringExtra("fragment"));
                switch (currentFragment) {
                    case DATE:
                        imageProgessCreationDate.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_date_focused));
                        dateValidate = true;
                        break;
                    case COST:
                        imageProgressCreationCost.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.cost_event_focused));
                        costValidate = true;
                        break;
                    case NAME:
                        imageProgessCreationName.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_name_focused));
                        nameValidate = true;
                        break;
                    case PHOTO:
                        imageProgessCreationPhoto.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_photo_focused));
                        photoValidate = true;
                        break;
                    case ADRESS:
                        imageProgessCreationPosition.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_adress_focused));
                        addressValidate = true;
                        break;
                    case MEMBERS:
                        imageProgessCreationMembers.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_members_focused));
                        membersValidate = true;
                        break;

                }

                Fragment nextFrag = null;

                if (photoValidate && nameValidate && membersValidate && dateValidate && addressValidate && costValidate) {
                    createEvent();
                } else if (!photoValidate) {
                    nextFrag = new PhotoEventFragment();
                } else if (!nameValidate) {
                    nextFrag = new NameEventFragment();
                } else if (!dateValidate) {
                    nextFrag = new DateEventFragment();
                } else if (!addressValidate) {
                    nextFrag = new AddressEventFragment();
                } else if (!membersValidate) {
                    nextFrag = new MembersEventPrivateFragment();
                } else if (!costValidate) {
                    nextFrag = new CostEventFragment();
                }


                if (nextFrag != null) {
                    changeFragment(nextFrag);
                }
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isTypeChoosed) {
            changeType(TypeEvent.values()[position]);
        } else {
            isTypeChoosed = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public enum CurrentFragment {
        NAME, ADRESS,
        DATE, COST,
        PHOTO, MEMBERS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_creation_event);
        isTypeChoosed = false;
        textViewTitleEvent = findViewById(R.id.textViewTitleEvent);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentCreationEvent, new PhotoEventFragment(), "findThisFragment")
                .addToBackStack(null)
                .commit();
        initialiserImageView();
        initialiserCardView();
        Button buttonValidate = findViewById(R.id.buttonNextFragmentMembers);
        buttonValidate.setOnClickListener(v -> {
            LocalBroadcastManager localBctMgr = LocalBroadcastManager.getInstance(this);
            localBctMgr.sendBroadcast(new Intent(ACTION_RECEIVE_NEXT_CLICK));
        });
        bottomSheet = findViewById(R.id.sheetCreateEvent);
        viewGrey = findViewById(R.id.viewGrey2);
        viewGrey.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (bottomSheetBehaviorType.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehaviorType.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });


        activity = this;
        buttonBack = findViewById(R.id.buttonBack);


        initFirstFiltre();
        initCardViewVisibility();

        Spinner spin = (Spinner) findViewById(R.id.spinnerType);

        spin.setOnItemSelectedListener(this);

        SpinnerAdapter customAdapter = new SpinnerAdapter(getBaseContext(), TypeEvent.listOfImages(getBaseContext()), TypeEvent.listOfNames(getBaseContext()));
        spin.setAdapter(customAdapter);
        spin.setSelection(Arrays.asList(TypeEvent.values()).indexOf(currentEvent.getCategory()));

        buttonBack.setOnClickListener(v -> onBackPressed());

        bottomSheetType = findViewById(R.id.sheetCreateEventType);
        bottomSheetBehaviorType = BottomSheetBehavior.from(bottomSheetType);
        bottomSheetBehaviorType.setHideable(false);
        bottomSheetBehaviorType.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        viewGrey.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                        viewGrey.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        textViewVisibility.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            viewGrey.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVE_FRAGMENT);
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiverFragmentValidate, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiverFragmentValidate);
    }

    private void initCardViewVisibility() {

        cardViewPrivate = findViewById(R.id.cardViewPrivate);
        cardViewPublic = findViewById(R.id.cardViewPublic);
        cardViewPrivate.setOnClickListener(v -> {
            checkChangeVisibility(Visibility.PRIVATE);
        });

        cardViewPublic.setOnClickListener(v -> {
            checkChangeVisibility(Visibility.PUBLIC);
        });
    }

    private void changeVisibility(Visibility v) {
        currentEvent.setVisibility(v);
        initFirstFiltre();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        imageProgessCreationMembers.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.creation_event_members));
        membersValidate = false;
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentCreationEvent);
        if (fragment instanceof MembersEventPrivateFragment || fragment instanceof MembersEventPublicFragment) {
            if (v == Visibility.PRIVATE)
                changeFragment(new MembersEventPrivateFragment());
            else
                changeFragment(new MembersEventPublicFragment());
        }
    }

    private void checkChangeVisibility(Visibility v) {
        if (membersValidate) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            membersValidate = false;
                            changeVisibility(v);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.visibility_change_warning)).setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();
        } else {
            changeVisibility(v);
        }

    }

    private void changeType(TypeEvent te) {
        currentEvent.setCategory(te);
        initFirstFiltre();
        bottomSheetBehaviorType.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initFirstFiltre() {
        textViewVisibility = findViewById(R.id.textViewVisibility);
        textViewType = findViewById(R.id.textViewType);
        textViewType.setCompoundDrawablesWithIntrinsicBounds(TypeEvent.imageOfValue(currentEvent.getCategory()), 0, 0, 0);
        textViewType.setText(TypeEvent.nameOfValue(currentEvent.getCategory(), getBaseContext()));
        textViewType.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            viewGrey.setVisibility(View.VISIBLE);
            bottomSheetBehaviorType.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        textViewVisibility.setCompoundDrawablesWithIntrinsicBounds(getDrawableVisibility(currentEvent.getVisibility()), 0, 0, 0);
        textViewVisibility.setText(getTextVisibility(currentEvent.getVisibility()));


    }

    private int getDrawableVisibility(Visibility v) {
        switch (v) {
            case PUBLIC:
                return R.drawable.add_event_public;
            case PRIVATE:
                return R.drawable.add_event_prive;
            default:
                return 0;
        }
    }

    private String getTextVisibility(Visibility v) {
        switch (v) {
            case PUBLIC:
                return getResources().getString(R.string.location_public);
            case PRIVATE:
                return getResources().getString(R.string.location_private);
            default:
                return "Error";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createEvent() {
        JSONObjectCrypt params = new JSONObjectCrypt();
        params.putCryptParameter("uid", USER.getUid());
        params.putCryptParameter("uid_event", currentEvent.getUid());
        params.putCryptParameter("address", currentEvent.getAddress());
        params.putCryptParameter("city", currentEvent.getCity());
        params.putCryptParameter("description", currentEvent.getDescription());
        params.putCryptParameter("startDate", currentEvent.getStartDate());
        params.putCryptParameter("endDate", currentEvent.getEndDate());
        params.putCryptParameter("visibility", currentEvent.getVisibility());
        params.putCryptParameter("latitude", currentEvent.getLatitude());
        params.putCryptParameter("longitude", currentEvent.getLongitude());
        params.putCryptParameter("name", currentEvent.getName());
        params.putCryptParameter("nbMembers", currentEvent.getNbMembers() + "");
        params.putCryptParameter("category", currentEvent.getCategory());
        params.putCryptParameter("price", currentEvent.getPrice() + "");

        Log.w("Prix", "Prix : " + currentEvent.getPrice());
        Log.w("nbMembers", "nbMembers : " + currentEvent.getNbMembers());
        Log.w("Params", params.toString());

        JSONController.getJsonObjectFromUrl(Constants.URL_ADD_EVENT, getBaseContext(), params, new JSONObjectListener() {
            @Override
            public void onJSONReceived(JSONObject jsonObject) {
                if (jsonObject.length() == 3) {
                    Toast.makeText(getBaseContext(), getString(R.string.event_create), Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.event_error_create), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onJSONReceivedError(VolleyError volleyError) {
                Toast.makeText(getBaseContext(), getString(R.string.event_error_create), Toast.LENGTH_SHORT).show();
                Log.w("Response", "Erreur: " + volleyError.toString());
            }
        });

        try {
            String imageData = BitMapToString(MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), image));
            Constants.uploadImageOnServer(ImageType.EVENT, MainCreationEventActivity.currentEvent.getUid(), imageData, getBaseContext()); //upload image on web server
            MainCreationEventActivity.currentEvent.setImage_url(Constants.BASE_API_URL + "/" + ImageType.EVENT + "/" + MainCreationEventActivity.currentEvent.getUid());
        } catch (IOException e) {
            Log.w("Response", "Erreur: " + e.getMessage());
        }
    }

    private static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.event_not_save))
                .setPositiveButton(getResources().getString(R.string.button_yes), (dialog, id) -> {
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.button_no), (dialog, id) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentEvent = null;
    }

    private void setCurrentProgressColor(CardView cardView) {
        cardViewProgressCreationDate.setCardBackgroundColor(getResources().getColor(R.color.grey));
        cardViewProgressCreationName.setCardBackgroundColor(getResources().getColor(R.color.grey));
        cardViewProgressCreationMembers.setCardBackgroundColor(getResources().getColor(R.color.grey));
        cardViewProgressCreationPhoto.setCardBackgroundColor(getResources().getColor(R.color.grey));
        cardViewProgressCreationPosition.setCardBackgroundColor(getResources().getColor(R.color.grey));
        cardViewProgressCreationCost.setCardBackgroundColor(getResources().getColor(R.color.grey));

        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void initialiserImageView() {
        imageProgessCreationDate = findViewById(R.id.imageProgessCreationDate);
        imageProgessCreationMembers = findViewById(R.id.imageProgessCreationMembers);
        imageProgessCreationName = findViewById(R.id.imageProgessCreationName);
        imageProgessCreationPosition = findViewById(R.id.imageProgessCreationPosition);
        imageProgessCreationPhoto = findViewById(R.id.imageProgessCreationPhoto);
        imageProgressCreationCost = findViewById(R.id.imageProgessCreationCost);

        imageProgressCreationCost.setOnClickListener(v -> changeFragment(new CostEventFragment()));
        imageProgessCreationDate.setOnClickListener(v -> changeFragment(new DateEventFragment()));
        imageProgessCreationName.setOnClickListener(v -> changeFragment(new NameEventFragment()));
        imageProgessCreationPosition.setOnClickListener(v -> changeFragment(new AddressEventFragment()));
        imageProgessCreationMembers.setOnClickListener(v -> {
            if (currentEvent.getVisibility().equals(Visibility.PRIVATE))
                changeFragment(new MembersEventPrivateFragment());
            else
                changeFragment(new MembersEventPublicFragment());
        });
        imageProgessCreationPhoto.setOnClickListener(v -> changeFragment(new PhotoEventFragment()));
    }

    private void initialiserCardView() {
        cardViewProgressCreationDate = findViewById(R.id.cardViewProgressCreationDate);
        cardViewProgressCreationMembers = findViewById(R.id.cardViewProgressCreationMembers);
        cardViewProgressCreationName = findViewById(R.id.cardViewProgressCreationName);
        cardViewProgressCreationPhoto = findViewById(R.id.cardViewProgressCreationPhoto);
        cardViewProgressCreationPosition = findViewById(R.id.cardViewProgressCreationAdress);
        cardViewProgressCreationCost = findViewById(R.id.cardViewProgressCreationCost);

    }

    private void changeFragment(Fragment fragment) {
        if (fragment instanceof NameEventFragment) {
            setCurrentProgressColor(cardViewProgressCreationName);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_name));
        }
        if (fragment instanceof AddressEventFragment) {
            setCurrentProgressColor(cardViewProgressCreationPosition);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_adress));
        }
        if (fragment instanceof DateEventFragment) {
            setCurrentProgressColor(cardViewProgressCreationDate);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_date));
        }
        if (fragment instanceof CostEventFragment) {
            setCurrentProgressColor(cardViewProgressCreationCost);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_cost));
        }
        if (fragment instanceof MembersEventPrivateFragment || fragment instanceof MembersEventPublicFragment) {
            setCurrentProgressColor(cardViewProgressCreationMembers);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_members));
        }
        if (fragment instanceof PhotoEventFragment) {
            setCurrentProgressColor(cardViewProgressCreationPhoto);
            textViewTitleEvent.setText(getString(R.string.title_creation_event_photo));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentCreationEvent, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }


}