package com.nolonely.mobile.ui.profil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nolonely.mobile.R;
import com.nolonely.mobile.util.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class AideActivity extends AppCompatActivity {

    private ImageView buttonBack;
    private LinearLayout linearCGU;
    private LinearLayout linearIssues;
    private LinearLayout linearAboutUs;
    private LinearLayout linearSupportUs;
    private AdView ad;


    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_aide);

        //PDFView pdfView = findViewById(R.id.pdfView);
        buttonBack = findViewById(R.id.buttonBack);
        linearCGU = findViewById(R.id.linearCGU);
        linearIssues = findViewById(R.id.linearIssues);
        linearAboutUs = findViewById(R.id.linearAboutUs);
        linearSupportUs = findViewById(R.id.linearSupportUs);
        ad = findViewById(R.id.adView);

        buttonBack.setVisibility(View.VISIBLE);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                ad.loadAd(new AdRequest.Builder().build());
            }
        });


        linearCGU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebBrowserOnUrl(Constants.BASE_URL + "/CGU");
            }
        });

        linearIssues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebBrowserOnUrl(Constants.BASE_URL + "/issues");
            }
        });

        linearAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebBrowserOnUrl(Constants.BASE_URL + "/contact");
            }
        });

        linearSupportUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebBrowserOnUrl(Constants.BASE_URL + "/support-us");
            }
        });

        /*pdfView.fromAsset("manuel.pdf")
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
                .load();*/
    }

    private void startWebBrowserOnUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}