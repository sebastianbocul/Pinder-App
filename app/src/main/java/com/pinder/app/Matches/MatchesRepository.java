package com.pinder.app.Matches;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class MatchesRepository {
    public MatchesRepository instance = null;

    MatchesRepository getInstance(){

        if(instance==null){
            instance=new MatchesRepository();
        }

        return instance;
    }




    public LiveData<ArrayList<String>> getTags() {
        return MatchesFirebase.getInstance().getTags();
    }
}
