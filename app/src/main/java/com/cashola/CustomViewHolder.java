package com.cashola;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashola.R;
//import com.google.android.gms.ads.NativeExpressAdView;
//import com.ramotion.foldingcell.FoldingCell;


/**
 * Created by Rahul on 2/4/2016.
 */
public class CustomViewHolder extends RecyclerView.ViewHolder {
    protected ImageView GameImage,Team1Image,Team2Image,InspiImage,GameImage1,AppImage,PlayerImage,AdsRentSell,NewsImage,PostPremium,EventImage,Share;
    protected TextView AppTitle,Appdescription,TaskName,TaskAmount,Bid,MatchDate,PlayerName,SR,AVG,PlanSelect,NewsTitle,AppInstallPay,OfferTitle,AppInstallText,AppInstallUrl,AppInstallPayout,AppInstallDescription,PostDecription;
    protected Button AppInstallBtn,PlayBidButton,NewsBtn;
    protected TextView UserName,Match,DateTime,Points,Prize;
    ImageView GiftCardImage,Offline,NewsShare,NewsWtsappShare,FacebookNewsShare;
    protected TextView OfferCashBack,GiftCardDesignText,OfferExpireDate,NewsDescription,NewsSource,NewsLink,StartDate,EndDate,EventTitle;
    protected RatingBar ratingBar;
   // AdView adView;
    CheckBox checkBox;
   // android.support.v7.widget.CardView CardView;
    LinearLayout adContainer;
    TextView TrasactionStatus,AmountPaid,Status,DateRech,UserMobileNumber;
    RelativeLayout clickcoupanRelative,GiftCardLayout;
   // NativeExpressAdView adView;
//    NativeExpressAdView adView ;
//    protected FoldingCell fc;
    public CustomViewHolder(View view) {
        super(view);

        ;

        //--------------------------------Player List--------------------------------


        this.checkBox=(CheckBox)view.findViewById(R.id.checkbox) ;

        this.TaskName=(TextView)view.findViewById(R.id.task_name);
        this.TaskAmount=(TextView)view.findViewById(R.id.task_amount);
//        this.NewsBtn=(Button)view.findViewById(R.id.NewsBtn);
//        this.NewsDescription=(TextView)view.findViewById(R.id.NewsDescripton);
       // this.AppImage=(ImageView)view.findViewById(R.id.AppIcon);
        this.AppInstallBtn=(Button)view.findViewById(R.id.Coinbtn);
//        this.NewsImage=(ImageView)view.findViewById(R.id.NewsImage);
//        this.Share=(ImageView)view.findViewById(R.id.share);
//        this.NewsWtsappShare=(ImageView)view.findViewById(R.id.whtsapp);
//        this.FacebookNewsShare=(ImageView)view.findViewById(R.id.facebook);
//        this.InspiImage=(ImageView)view.findViewById(R.id.inspiImage);

        //--------------------Wiiner Liste--------------------

       // this.DateTime=(TextView)view.findViewById(R.id.date);


        // this.GiftCardLayout=(RelativeLayout)view.findViewById(R.id.GiftCardLayout);
//
//        this.OfferTitle=(TextView)view.findViewById(R.id.OfferTitle);
//        this.OfferImage=(ImageView)view.findViewById(R.id.offerImage);
//      //  this.OfferExpireDate=(TextView) view.findViewById(R.id.OfferExpire);
//        this.OfferCashBack=(TextView) view.findViewById(R.id.OfferCashback);
//        /////////////////////////
//        this.TrasactionStatus=(TextView) view.findViewById(R.id.trasacctionType);
//        this.Status=(TextView) view.findViewById(R.id.status);
//        this.DateRech=(TextView) view.findViewById(R.id.datetrans);
//        this.AmountPaid=(TextView) view.findViewById(R.id.AmountPaidRech);
//        this.UserMobileNumber=(TextView) view.findViewById(R.id.rechmob);

//        adView = (NativeExpressAdView)view.findViewById(R.id.adView);
//        this.AdsCategory=(TextView)view.findViewById(R.id.AdsCategory);
//        this.AdsPrice=(TextView)view.findViewById(R.id.AdsPrice);
//        this.AdsImage=(ImageView)view.findViewById(R.id.AdsImage);

//        this.PlanDescription=(TextView)view.findViewById(R.id.planDecription);
//        this.PlanPrice=(TextView)view.findViewById(R.id.planPrice);
//        this.PlanSelect=(TextView)view.findViewById(R.id.planSelect);
//        this.PlanValidity=(TextView)view.findViewById(R.id.validity);
//        this.clickcoupanRelative=(RelativeLayout)view.findViewById(R.id.clickcoupan);
//        this.Offline=(ImageView)view.findViewById(R.id.offline);
//      this.fc = (FoldingCell)view.findViewById(R.id.folding_cell);
//       // this.PostRating=(TextView)view.findViewById(R.id.TotalRating);
//       // this.PostTotalRating=(TextView)view.findViewById(R.id.PostTitle);
//        //this.ratingBar=(RatingBar)view.findViewById(R.id.RatingBar);
        this.AppTitle=(TextView)view.findViewById(R.id.AppName);
        this.Appdescription=(TextView)view.findViewById(R.id.AppDescription);

        this.AppImage=(ImageView)view.findViewById(R.id.AppIcon);
        this.AppInstallBtn=(Button)view.findViewById(R.id.Coinbtn);

       //  adView = (NativeExpressAdView)view.findViewById(R.id.adView);
////        // this.AdsRentSell=(Ima)view.findViewById(R.id.rent_sell_tag);
//        this.PostDecription=(TextView)view.findViewById(R.id.postDescription);
//        this.StartDate=(TextView)view.findViewById(R.id.StartDate);
////       // this.BTN=(Button)view.findViewById(R.id.BTN);
//        this.EndDate=(TextView)view.findViewById(R.id.EndDate);

    }
}