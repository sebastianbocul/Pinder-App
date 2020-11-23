package com.pinder.app.di;

import android.app.Application;
import android.content.Context;

import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.repository.MainRepository;

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
    public static MainFirebase mainFirebase(Application application) {
        return new MainFirebase(application);
    }

    @Singleton
    @Provides
    public static MainRepository mainRepository(MainFirebase mainFirebase) {
        return new MainRepository(mainFirebase);
    }
    @Singleton
    @Provides
    public static Context application(@ApplicationContext Context context){
        return context;
    }
}
