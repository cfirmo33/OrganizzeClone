package com.android.organize.configs;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ConfiguracaoFirebase {
    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebaseDatabase;

    //retorna a instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if ( firebaseDatabase == null ){
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDatabase;
    }
    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }


}
