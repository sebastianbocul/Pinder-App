package com.pinder.app.di;

import com.pinder.app.viewmodels.CommunicationViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
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




