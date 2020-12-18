package com.pinder.app.di;

import android.content.Context;
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
    public static Context application(@ApplicationContext Context context) {
        return context;
    }
}


