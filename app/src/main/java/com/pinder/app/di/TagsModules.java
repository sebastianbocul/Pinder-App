package com.pinder.app.di;

import android.content.Context;

import com.pinder.app.cache.TagsCache;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.repository.TagsRepository;
import com.pinder.app.viewmodels.TagsViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class TagsModules {
    @ActivityRetainedScoped
    @Provides
    public static TagsCache tagsCache(@ApplicationContext Context context) {
        return new TagsCache(context);
    }

    @ActivityRetainedScoped
    @Provides
    public static TagsFirebase tagsFirebase() {
        return new TagsFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static TagsRepository tagsRepository(TagsFirebase tagsFirebase, TagsCache tagsCache) {
        return new TagsRepository(tagsFirebase, tagsCache);
    }

    @ActivityRetainedScoped
    @Provides
    public static TagsViewModel tagsViewModel(TagsRepository tagsRepository) {
        return new TagsViewModel(tagsRepository);
    }
}