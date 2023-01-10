package com.sharerideexpense.easycarpool.classes;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDb {
    FirebaseFirestore mDbRef;
    DocumentReference mDocRef;

    public UserDb() {
        mDbRef = FirebaseFirestore.getInstance();
    }

    public DocumentReference InsertUserInfo(String u_id) {
    return   mDbRef.collection("users").document(u_id);

    }

    public Task<DocumentSnapshot> CheckUser(String user) {
        return mDbRef.collection("users").document(user).get();
    }
}
