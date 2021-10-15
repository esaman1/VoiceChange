package com.vapp.admoblibrary.iap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.vapp.admoblibrary.utils.Utils;

import java.util.List;

public class PurchaseUtils {
    public static BillingProcessor bp;
    public static TransactionDetails purchaseTransactionDetails = null;

    private static volatile PurchaseUtils INSTANCE;


    public static synchronized PurchaseUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PurchaseUtils();
        }
        return INSTANCE;
    }

    public void initBilling(Context context, String play_console_license) {
            bp = new BillingProcessor(context, play_console_license, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
              //  Utils.getInstance().showMessenger(context,"onProductPurchased");
            }

            @Override
            public void onPurchaseHistoryRestored() {
               // Utils.getInstance().showMessenger(context,"onPurchaseHistoryRestored");

            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Utils.getInstance().showMessenger(context,"onBillingError");

            }

            @Override
            public void onBillingInitialized() {
//                getDetailSubscribe(context, "1111");
            }
        });
        bp.initialize();

        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    int a = 0;

                }

            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                int a = 0;

            }
        });
    }


    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    int a = 0;
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                int a = 0;

            } else {
                // Handle any other error codes.
                int a = 0;

            }
        }
    };

    private  boolean hasSubscription() {
        if (purchaseTransactionDetails != null) {
            return purchaseTransactionDetails.purchaseInfo != null;
        }
        return false;
    }


    public  boolean isSubscriptiond(String idSubscribe) {
        bp.loadOwnedPurchasesFromGoogle();
        purchaseTransactionDetails = bp.getSubscriptionTransactionDetails(idSubscribe);
        if (hasSubscription()) {
            return  true;
        } else {
            return false;
        }
    }

    public  boolean isPurchased(String idSubscribe) {
        bp.loadOwnedPurchasesFromGoogle();
        purchaseTransactionDetails = bp.getPurchaseTransactionDetails(idSubscribe);
        if (hasSubscription()) {
            return  true;
        } else {
            return false;
        }
    }

    public  void subscribeById(Activity context, String idSubscribe) {
        if (bp.isSubscriptionUpdateSupported()) {
            bp.subscribe(context, idSubscribe);
        } else {
            Log.d("MainActivity", "onBillingInitialized: Subscription updated is not supported");
        }
    }


    public  void purchaseById(Activity context, String idSubscribe) {
        if (bp.isSubscriptionUpdateSupported()) {
            bp.purchase(context, idSubscribe);
        } else {
            Log.d("MainActivity", "onBillingInitialized: Subscription updated is not supported");
        }
    }

    public SkuDetailsModel getDetailSubscribe(Context activity, String idSubscribe) {
        bp.loadOwnedPurchasesFromGoogle();
        SkuDetails subs = bp.getSubscriptionListingDetails(idSubscribe);
        SkuDetailsModel detailsModel = new SkuDetailsModel(subs.productId, subs.title, subs.description, subs.isSubscription, subs.currency, subs.priceValue, subs.subscriptionPeriod, subs.subscriptionFreeTrialPeriod, subs.haveTrialPeriod, subs.introductoryPriceValue, subs.introductoryPricePeriod, subs.haveIntroductoryPeriod, subs.introductoryPriceCycles);
        return detailsModel;
    }


    public SkuDetailsModel getDetailPurchase(Activity activity, String idSubscribe) {
        bp.loadOwnedPurchasesFromGoogle();
        SkuDetails subs = bp.getPurchaseListingDetails(idSubscribe);
        SkuDetailsModel detailsModel = new SkuDetailsModel(subs.productId, subs.title, subs.description, subs.isSubscription, subs.currency, subs.priceValue, subs.subscriptionPeriod, subs.subscriptionFreeTrialPeriod, subs.haveTrialPeriod, subs.introductoryPriceValue, subs.introductoryPricePeriod, subs.haveIntroductoryPeriod, subs.introductoryPriceCycles);
        return detailsModel;
    }


    public boolean restoreSubscription(String idSubscribeOrPurchases){
        bp.loadOwnedPurchasesFromGoogle();
        purchaseTransactionDetails = bp.getSubscriptionTransactionDetails(idSubscribeOrPurchases);
        if (hasSubscription()) {
            return  true;
        } else {
            return false;
        }
    }

    public boolean restorePurchase(String idSubscribeOrPurchases){
        bp.loadOwnedPurchasesFromGoogle();
        purchaseTransactionDetails = bp.getPurchaseTransactionDetails(idSubscribeOrPurchases);
        if (isPurchased(idSubscribeOrPurchases)) {
            return  true;
        } else {
            return false;
        }
    }
}
