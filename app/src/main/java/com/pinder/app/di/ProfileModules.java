package com.pinder.app.di;

import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.viewmodels.ProfileViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class ProfileModules {
    @ActivityRetainedScoped
    @Provides
    public static ProfileFirebase profileFirebase() {
        return new ProfileFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static ProfileRepository profileRepository(ProfileFirebase profileFirebase) {
        return new ProfileRepository(profileFirebase);
    }

    @ActivityRetainedScoped
    @Provides
    public static ProfileViewModel profileViewModel(ProfileRepository profileRepository) {
        return new ProfileViewModel(profileRepository);
    }
}