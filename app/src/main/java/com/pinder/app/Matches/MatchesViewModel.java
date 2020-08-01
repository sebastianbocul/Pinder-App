package com.pinder.app.Matches;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MatchesViewModel extends ViewModel {
    MatchesRepository matchesRepository;
    LiveData<ArrayList<MatchesObject>> oryginalMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();
    MutableLiveData<ArrayList<MatchesObject>> resultMatchesLiveData = new MutableLiveData<>();
    MutableLiveData<String> tagLD;
    // public String tag = "AllButtonClicked";

    public MatchesViewModel() {
        matchesRepository = new MatchesRepository();
        oryginalMatchesLiveData = getOryginalMatches();
        if (tagLD == null) {
            tagLD = new MutableLiveData<>();
            tagLD.setValue("all");
        }
        //tagLD.postValue(tag);
    }

    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        oryginalMatchesLiveData = matchesRepository.getInstance().getOryginalMatches();
        return oryginalMatchesLiveData;
    }

    public LiveData<ArrayList<String>> getTags() {
        return matchesRepository.getInstance().getTags();
    }
//    public ArrayList<MatchesObject>> getSortedTags(ArrayList<MatchesObject> list) {
//        resultMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();
//        if(list==null) return resultMatchesLiveData;
//        ArrayList<MatchesObject> resultMatches = new ArrayList<>();
//        ArrayList<MatchesObject> oryginalMatches = list;
//        ArrayList mutualTags = new ArrayList();
//        ArrayList<MatchesObject> bufforMatches = new ArrayList<MatchesObject>();
//        Log.d("MatchesViewModelLog", "tag: " + tag);
//        Log.d("MatchesViewModelLog", "oryginalMatches: " + oryginalMatches.size());
//        if (tag.equals("AllButtonClicked")) {
//            resultMatchesLiveData.postValue(oryginalMatches);
//            return resultMatchesLiveData;
//        }
//        for (MatchesObject mo : oryginalMatches) {
//            Log.d("MatchesViewModelLog", "mo: " + mo.getUserId());
//            mutualTags = mo.getMutualTags();
//            if (mutualTags.contains(tag)) {
//                bufforMatches.add(mo);
//            }
//        }
//        if (bufforMatches.size() != 0) {
//            resultMatches.clear();
//            resultMatches = bufforMatches;
//            // resultMatches = sortCollection(resultMatches);
//            resultMatchesLiveData.postValue(resultMatches);
//        }
//        Log.d("MatchesViewModelLog", "resultMatches: " + resultMatches.size());
//        return resultMatchesLiveData;
//    }
//    private ArrayList<MatchesObject> sortCollection(ArrayList<MatchesObject> matchesList) {
//        Collections.sort(matchesList, new Comparator<MatchesObject>() {
//            @Override
//            public int compare(MatchesObject o1, MatchesObject o2) {
//                return o1.getSortId().compareTo(o2.getSortId());
//            }
//        });
//        Collections.reverse(matchesList);
//        return matchesList;
//    }
//    public LiveData<ArrayList<MatchesObject>> getSortedTags(String tag) {
//        this.tag.postValue(tag);
//        return getResultMatches();
//    }

    public ArrayList<MatchesObject> getSortedTags() {
        ArrayList<MatchesObject> resultMatches = new ArrayList<>();
        ArrayList<MatchesObject> oryginalMatches = oryginalMatchesLiveData.getValue();
        ArrayList mutualTags = new ArrayList();
        ArrayList<MatchesObject> bufforMatches = new ArrayList<MatchesObject>();
        Log.d("MatchesViewModelLog", "tag: " + tagLD.getValue());
        Log.d("MatchesViewModelLog", "oryginalMatches: " + oryginalMatches.size());
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
        Log.d("MatchesViewModelLog", "resultMatches: " + resultMatches.size());
        return resultMatches;
    }

    public ArrayList<MatchesObject> setTag(String tag) {
        tagLD.setValue(tag);
        return getSortedTags();
    }
}
