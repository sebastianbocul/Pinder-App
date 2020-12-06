package com.pinder.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.MatchesObject;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {
    MatchesRepository matchesRepository;
    LiveData<Resource<ArrayList<MatchesObject>>> oryginalMatchesLiveData = new MutableLiveData<>();
    public MutableLiveData<String> tagLD;

    public MatchesViewModel() {
        matchesRepository = new MatchesRepository();
        oryginalMatchesLiveData = getOryginalMatches();
        if (tagLD == null) {
            tagLD = new MutableLiveData<>();
            tagLD.setValue("all");
        }
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getOryginalMatches() {
        oryginalMatchesLiveData = matchesRepository.getInstance().getOryginalMatches();
        return oryginalMatchesLiveData;
    }

    public LiveData<ArrayList<String>> getTags() {
        return matchesRepository.getInstance().getTags();
    }

    public Resource<ArrayList<MatchesObject>> getSortedMatches() {
        Resource<ArrayList<MatchesObject>> resultMatches = null;
        Resource<ArrayList<MatchesObject>> oryginalMatches = null;
        if (oryginalMatchesLiveData.getValue() != null) {
            oryginalMatches = oryginalMatchesLiveData.getValue();
        }
        ArrayList mutualTags = new ArrayList();
        Resource<ArrayList<MatchesObject>> bufforMatches = null;
        if (tagLD.getValue().equals("all")) {
            return oryginalMatches;
        }
        for (MatchesObject mo : oryginalMatches.data) {
            mutualTags = mo.getMutualTags();
            if (mutualTags.contains(tagLD.getValue())) {
                bufforMatches.data.add(mo);
            }
        }
        if (bufforMatches.data.size() != 0) {
            resultMatches.data.clear();
            resultMatches = bufforMatches;
        }
        return resultMatches;
    }

    public Resource<ArrayList<MatchesObject>> setTag(String tag) {
        tagLD.setValue(tag);
        return getSortedMatches();
    }

    public String getMyImageUrl() {
        return matchesRepository.getMyImageUrl();
    }
}
