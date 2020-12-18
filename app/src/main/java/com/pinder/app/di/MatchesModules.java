package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.MatchesCache;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.viewmodels.MatchesViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class MatchesModules {

    @ActivityRetainedScoped
    @Provides
    public static MatchesViewModel matchesViewModel(MatchesRepository matchesRepository) {
        return new MatchesViewModel(matchesRepository);
    }

    @ActivityRetainedScoped
    @Provides
    public static MatchesCache matchesCache(@ApplicationContext Context context) {
        return new MatchesCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static MatchesFirebase matchesFirebase() {
        return new MatchesFirebase();
    }
    @ActivityRetainedScoped
    @Provides
    public static MatchesRepository matchesRepository(MatchesFirebase matchesFirebase,MatchesCache matchesCache) {
        return new MatchesRepository(matchesFirebase,matchesCache);
    }


}