package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.ProfileCache;
import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.viewmodels.ProfileViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class ProfileModules {
    @ActivityRetainedScoped
    @Provides
    public static ProfileCache profileCache(@ApplicationContext Context context) {
        return new ProfileCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static ProfileFirebase profileFirebase() {
        return new ProfileFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static ProfileRepository profileRepository(ProfileFirebase profileFirebase, ProfileCache profileCache) {
        return new ProfileRepository(profileFirebase, profileCache);
    }

    @ActivityRetainedScoped
    @Provides
    public static ProfileViewModel profileViewModel(ProfileRepository profileRepository) {
        return new ProfileViewModel(profileRepository);
    }
}