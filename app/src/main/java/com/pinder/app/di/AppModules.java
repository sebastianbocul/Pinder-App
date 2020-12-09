package com.pinder.app.di;

import android.app.Application;
import android.content.Context;

import com.pinder.app.cache.AuthCache;
import com.pinder.app.cache.MatchesCache;
import com.pinder.app.persistance.AuthFirebase;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.repository.AuthRepository;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.viewmodels.AuthViewModel;
import com.pinder.app.viewmodels.MatchesViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ApplicationComponent.class)
class AppModules {
    @Singleton
    @Provides
    public static MatchesCache matchesCache(@ApplicationContext Context context) {
        return new MatchesCache(context);
    }

    @Singleton
    @Provides
    public static MatchesRepository matchesRepository(MatchesCache matchesCache) {
        return new MatchesRepository(matchesCache);
    }
    @Singleton
    @Provides
    public static MatchesViewModel matchesViewModel(MatchesRepository matchesRepository) {
        return new MatchesViewModel(matchesRepository);
    }

    @Singleton
    @Provides
    public static MainFirebase mainFirebase(Application application) {
        return new MainFirebase(application);
    }

    @Singleton
    @Provides
    public static MainRepository mainRepository(MainFirebase mainFirebase) {
        return new MainRepository(mainFirebase);
    }


    //auth
    @Singleton
    @Provides
    public static AuthCache authCache(@ApplicationContext Context context) {
        return new AuthCache(context);
    }

    @Singleton
    @Provides
    public static AuthFirebase authFirebase(@ApplicationContext Context context) {
        return new AuthFirebase(context);
    }

    @Singleton
    @Provides
    public static AuthRepository authRepository(AuthFirebase authFirebase,AuthCache authCache) {
        return new AuthRepository(authFirebase,authCache);
    }
    @Singleton
    @Provides
    public static AuthViewModel authViewModel(AuthRepository authRepository) {
        return new AuthViewModel(authRepository);
    }

    @Singleton
    @Provides
    public static Context application(@ApplicationContext Context context) {
        return context;
    }
}
