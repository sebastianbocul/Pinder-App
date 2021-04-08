package com.pinder.app.di;

import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.viewmodels.SettingsViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class SettingsModules {
    @ActivityRetainedScoped
    @Provides
    public static SettingsFirebase settingsFirebase() {
        return new SettingsFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static SettingsRepository settingsRepository(SettingsFirebase settingsFirebase) {
        return new SettingsRepository(settingsFirebase);
    }

    @ActivityRetainedScoped
    @Provides
    public static SettingsViewModel settingsViewModel(SettingsRepository settingsRepository) {
        return new SettingsViewModel(settingsRepository);
    }
}