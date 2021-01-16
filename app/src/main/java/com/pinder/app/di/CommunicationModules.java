package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.persistance.AuthFirebase;
import com.pinder.app.repository.AuthRepository;
import com.pinder.app.viewmodels.AuthViewModel;
import com.pinder.app.viewmodels.CommunicationViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class CommunicationModules {

    @ActivityRetainedScoped
    @Provides
    public static CommunicationViewModel communicationViewModel() {
        return new CommunicationViewModel();
    }
}




