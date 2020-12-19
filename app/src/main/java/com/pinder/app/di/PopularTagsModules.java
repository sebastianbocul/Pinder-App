package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.PopularTagsCache;
import com.pinder.app.persistance.PopularTagsFirebase;
import com.pinder.app.repository.PopularTagsRepository;
import com.pinder.app.viewmodels.PopularTagsViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class PopularTagsModules {
    @ActivityRetainedScoped
    @Provides
    public static PopularTagsCache popularTagsCache(@ApplicationContext Context context) {
        return new PopularTagsCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static PopularTagsFirebase popularTagsFirebase() {
        return new PopularTagsFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static PopularTagsRepository popularTagsRepository(PopularTagsFirebase popularTagsFirebase, PopularTagsCache popularTagsCache) {
        return new PopularTagsRepository(popularTagsFirebase, popularTagsCache);
    }

    @ActivityRetainedScoped
    @Provides
    public static PopularTagsViewModel popularTagsViewModel(PopularTagsRepository popularTagsRepository) {
        return new PopularTagsViewModel(popularTagsRepository);
    }
}