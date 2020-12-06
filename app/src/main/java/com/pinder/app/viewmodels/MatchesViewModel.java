package com.pinder.app.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.pinder.app.models.MatchesObject;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MatchesViewModel extends ViewModel {
    private MatchesRepository matchesRepository;
    LiveData<Resource<ArrayList<MatchesObject>>> oryginalMatchesLiveData = new MutableLiveData<>();
    MediatorLiveData<Resource<ArrayList<MatchesObject>>> sortedLiveData = new MediatorLiveData<>();
    public MutableLiveData<String> tagLD;

    @ViewModelInject
    public MatchesViewModel(MatchesRepository matchesRepository) {
        this.matchesRepository = matchesRepository;
        if (tagLD == null) {
            tagLD = new MutableLiveData<>();
            tagLD.setValue("all");
        }
    }

    public LiveData<Resource<ArrayList<MatchesObject>>> getOryginalMatches() {
        oryginalMatchesLiveData = matchesRepository.getMatches();
        sortedLiveData.addSource(oryginalMatchesLiveData, new Observer<Resource<ArrayList<MatchesObject>>>() {
            @Override
            public void onChanged(Resource<ArrayList<MatchesObject>> arrayListResource) {
                if(arrayListResource.data!=null){
                    arrayListResource.data = sortCollection(arrayListResource.data);
                }
                sortedLiveData.setValue(arrayListResource);
            }
        });
        return sortedLiveData;
    }

    public LiveData<Resource<ArrayList<String>>> getTags() {
        return matchesRepository.getTags();
    }

    public ArrayList<MatchesObject> getSortedMatches() {
        ArrayList<MatchesObject> resultMatches = new ArrayList<>();
        ArrayList<MatchesObject> oryginalMatches = new ArrayList<>();
        if (oryginalMatchesLiveData.getValue() != null) {
            if (oryginalMatchesLiveData.getValue().data != null) {
                oryginalMatches = oryginalMatchesLiveData.getValue().data;
            }
        }
        ArrayList mutualTags = new ArrayList();
        ArrayList<MatchesObject> bufforMatches = new ArrayList<>();
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

    private ArrayList<MatchesObject> sortCollection(ArrayList<MatchesObject> matchesList) {
        Collections.sort(matchesList, new Comparator<MatchesObject>() {
            @Override
            public int compare(MatchesObject o1, MatchesObject o2) {
                return o1.getSortId().compareTo(o2.getSortId());
            }
        });
        Collections.reverse(matchesList);
        return matchesList;
    }
}
