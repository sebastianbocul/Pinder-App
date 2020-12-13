package com.pinder.app.di;

import android.app.Application;
import android.content.Context;

import com.pinder.app.cache.AuthCache;
import com.pinder.app.cache.MatchesCache;
import com.pinder.app.cache.ProfileCache;
import com.pinder.app.persistance.AuthFirebase;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.repository.AuthRepository;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.viewmodels.AuthViewModel;
import com.pinder.app.viewmodels.MatchesViewModel;
import com.pinder.app.viewmodels.ProfileViewModel;

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

//    @Singleton
//    @Provides
//    public static MatchesFirebase matchesFirebase() {
//        return new MatchesFirebase();
//    }
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

    //auth
    @Singleton
    @Provides
    public static ProfileCache profileCache(@ApplicationContext Context context) {
        return new ProfileCache(context);
    }

    @Singleton
    @Provides
    public static ProfileFirebase profileFirebase() {
        return new ProfileFirebase();
    }

    @Singleton
    @Provides
    public static ProfileRepository profileRepository(ProfileFirebase profileFirebase, ProfileCache profileCache) {
        return new ProfileRepository(profileFirebase,profileCache);
    }
    @Singleton
    @Provides
    public static ProfileViewModel profileViewModel(ProfileRepository profileRepository) {
        return new ProfileViewModel(profileRepository);
    }

    @Singleton
    @Provides
    public static Context application(@ApplicationContext Context context) {
        return context;
    }
}
