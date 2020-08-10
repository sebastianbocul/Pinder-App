package com.pinder.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.models.MatchesObject;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {
    MatchesRepository matchesRepository;
    LiveData<ArrayList<MatchesObject>> oryginalMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();
    public MutableLiveData<String> tagLD;

    public MatchesViewModel() {
        matchesRepository = new MatchesRepository();
        oryginalMatchesLiveData = getOryginalMatches();
        if (tagLD == null) {
            tagLD = new MutableLiveData<>();
            tagLD.setValue("all");
        }
    }

    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        oryginalMatchesLiveData = matchesRepository.getInstance().getOryginalMatches();
        return oryginalMatchesLiveData;
    }

    public LiveData<ArrayList<String>> getTags() {
        return matchesRepository.getInstance().getTags();
    }

    public ArrayList<MatchesObject> getSortedMatches() {
        ArrayList<MatchesObject> resultMatches = new ArrayList<>();
        ArrayList<MatchesObject> oryginalMatches= new ArrayList<>();
        if(oryginalMatchesLiveData.getValue()!=null){
            oryginalMatches = oryginalMatchesLiveData.getValue();
        }
        ArrayList mutualTags = new ArrayList();
        ArrayList<MatchesObject> bufforMatches = new ArrayList<MatchesObject>();
        if (tagLD.getValue().equals("all")) {
            return oryginalMatches;
        }
        for (MatchesObject mo : oryginalMatches) {
            mutualTags = mo.getMutualTags();
            if (mutualTags.contains(tagLD.getValue())) {
                bufforMatches.add(mo);
            }
        }
        if (bufforMatches.size() != 0) {
            resultMatches.clear();
            resultMatches = bufforMatches;
        }
        return resultMatches;
    }

    public ArrayList<MatchesObject> setTag(String tag) {
        tagLD.setValue(tag);
        return getSortedMatches();
    }

    public String getMyImageUrl() {
        return matchesRepository.getMyImageUrl();
    }

}