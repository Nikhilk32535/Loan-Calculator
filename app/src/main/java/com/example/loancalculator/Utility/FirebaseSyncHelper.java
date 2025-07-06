package com.example.loancalculator.Utility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.loancalculator.BaseLTV.LTVDB;
import com.example.loancalculator.BaseLTV.LTVSettings;
import com.example.loancalculator.BaseLTV.LTVSettingsDao;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDao;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.adapter.SchemeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseSyncHelper {

    public static void silentSyncSchemes(Context context) {
        LTVSettingsDao ltvSettingsDao = LTVDB.getInstance(context).ltvSettingsDao();
        SchemeDao schemeDao = SchemeDatabase.getInstance(context).schemeDao();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Step 1: Fetch Base LTV
        rootRef.child("BaseLTV").get().addOnSuccessListener(baseSnapshot -> {
            if (baseSnapshot.exists()) {
                Float baseLTV = baseSnapshot.getValue(Float.class);
                if (baseLTV != null) {
                    new Thread(() -> ltvSettingsDao.insertOrUpdate(new LTVSettings(baseLTV))).start();
                }
            }
        });

        // Step 2: Fetch All Schemes
        rootRef.child("Schemes").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                List<Scheme> firebaseSchemes = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Scheme scheme = child.getValue(Scheme.class);
                    if (scheme != null) {
                        firebaseSchemes.add(scheme);
                    }
                }

                // Save to local DB (Room)
                new Thread(() -> {
                    schemeDao.clearAll();
                    schemeDao.insertAll(firebaseSchemes);
                }).start();
            }
        });
    }

}
