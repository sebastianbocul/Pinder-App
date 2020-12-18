package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.AuthCache;
import com.pinder.app.persistance.AuthFirebase;
import com.pinder.app.repository.AuthRepository;
import com.pinder.app.viewmodels.AuthViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ApplicationComponent.class)
class AuthModules {

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
}


