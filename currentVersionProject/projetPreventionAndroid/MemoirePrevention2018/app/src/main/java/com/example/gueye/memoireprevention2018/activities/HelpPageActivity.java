package com.example.gueye.memoireprevention2018.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.HelpPageAdaptater;
import com.example.gueye.memoireprevention2018.fragments.PostDetailFragment;
import com.example.gueye.memoireprevention2018.fragments.SendAlerteFragment;
import com.example.gueye.memoireprevention2018.fragments.UrgencesAlertesFragment;
import com.example.gueye.memoireprevention2018.fragments.ViewPostFragment;
import com.example.gueye.memoireprevention2018.fragments.WelcomeFragment;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class HelpPageActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private ImageView ivNext;
    private ImageView ivBack;

    private Button btnFinish;
    private LinearLayout llFinish;

    private List<Fragment> fragments ;

    private PageIndicatorView pagerIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_help_page);

        mViewPager = (ViewPager) findViewById( R.id.vp_help_page);
       ivNext = (ImageView) findViewById( R.id.iv_next);
       ivBack = (ImageView) findViewById( R.id.iv_back);
       btnFinish = (Button) findViewById( R.id.btn_finish) ;
       llFinish = (LinearLayout) findViewById( R.id.ll_finish) ;

       pagerIndicator = findViewById( R.id.pageIndicatorView);


       fragments = new ArrayList<>();

       // HelpPageAdaptater helpPageAdaptater = new HelpPageAdaptater()

        fragments.add(new WelcomeFragment());
        fragments.add(new SendAlerteFragment());
        fragments.add(new ViewPostFragment());
        fragments.add(new PostDetailFragment());
        fragments.add(new UrgencesAlertesFragment());

        pagerIndicator.setCount(fragments.size());
        pagerIndicator.setSelected(0);

       mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

               int currentPositionFragment = mViewPager.getCurrentItem();

               if(currentPositionFragment ==0){

                   // do something here
                   pagerIndicator.setSelection(currentPositionFragment);

                   //do a transition fro btn finish

                   ivBack.setVisibility(View.GONE);
                   llFinish.setVisibility(View.VISIBLE);
               }

           }

           @Override
           public void onPageSelected(int position) {

               pagerIndicator.setSelection(position);

               if (position == 4){
                   btnFinish.setVisibility(View.VISIBLE);
                   ivNext.setVisibility(View.GONE);
               }

               if(position >0 && position <4){

                   if(ivBack.getVisibility()==View.GONE) ivBack.setVisibility(View.VISIBLE);
                   if(ivNext.getVisibility() ==View.GONE) ivNext.setVisibility(View.VISIBLE);

                   if (btnFinish.getVisibility()== View.VISIBLE) btnFinish.setVisibility(View.GONE);
                   if (llFinish.getVisibility()== View.VISIBLE) llFinish.setVisibility(View.GONE);
               }

               if (position == 0){

                   ivBack.setVisibility(View.GONE);
                   llFinish.setVisibility(View.VISIBLE);
               }

           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });



        FragmentPagerAdapter fragmentPagerAdapter = new HelpPageAdaptater(getSupportFragmentManager(), fragments);

        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setCurrentItem(0);


        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentPositionFragment = mViewPager.getCurrentItem();

                if(currentPositionFragment >0 && currentPositionFragment <4){

                    if(ivBack.getVisibility()==View.GONE) ivBack.setVisibility(View.VISIBLE);
                    if(ivNext.getVisibility() ==View.GONE) ivNext.setVisibility(View.VISIBLE);

                    mViewPager.setCurrentItem(currentPositionFragment+1);

                    pagerIndicator.setSelection(currentPositionFragment+1);

                    if(btnFinish.getVisibility()==View.VISIBLE) btnFinish.setVisibility(View.GONE);
                    if (llFinish.getVisibility() == View.VISIBLE) llFinish.setVisibility(View.GONE);
                }

                if(currentPositionFragment ==4){

                    // do something here
                    pagerIndicator.setSelection(currentPositionFragment);

                    //do a transition fro btn finish

                    ivNext.setVisibility(View.GONE);
                    if(ivBack.getVisibility()==View.GONE) ivBack.setVisibility(View.VISIBLE);

                    btnFinish.setVisibility(View.VISIBLE);

                    Toast.makeText(HelpPageActivity.this, "Vous avez atteint le nombre de page total ", Toast.LENGTH_SHORT).show();
                }

                if(currentPositionFragment ==0){

                    // do something here
                    pagerIndicator.setSelection(currentPositionFragment);

                    //do a transition fro btn finish

                    ivBack.setVisibility(View.GONE);
                    llFinish.setVisibility(View.VISIBLE);
                }



            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentPositionFragment = mViewPager.getCurrentItem();

                if(currentPositionFragment >0 &&  currentPositionFragment <4){

                    if(ivBack.getVisibility()==View.GONE) ivBack.setVisibility(View.VISIBLE);
                    if(ivNext.getVisibility() ==View.GONE) ivNext.setVisibility(View.VISIBLE);

                    mViewPager.setCurrentItem(currentPositionFragment-1);
                    if(btnFinish.getVisibility()==View.VISIBLE) btnFinish.setVisibility(View.GONE);
                    pagerIndicator.setSelection(currentPositionFragment-1);

                    if (llFinish.getVisibility() == View.VISIBLE) llFinish.setVisibility(View.GONE);
                }

                if(currentPositionFragment ==0){

                    // do something here
                    pagerIndicator.setSelection(currentPositionFragment);

                    //do a transition fro btn finish

                    ivBack.setVisibility(View.GONE);
                    llFinish.setVisibility(View.VISIBLE);
                }


            }
        });

        llFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
