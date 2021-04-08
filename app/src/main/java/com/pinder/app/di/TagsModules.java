package com.pinder.app.di;

import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.repository.TagsRepository;
import com.pinder.app.viewmodels.TagsViewModel;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
class TagsModules {
    @ActivityRetainedScoped
    @Provides
    public static TagsFirebase tagsFirebase() {
        return new TagsFirebase();
    }

    @ActivityRetainedScoped
    @Provides
    public static TagsRepository tagsRepository(TagsFirebase tagsFirebase) {
        return new TagsRepository(tagsFirebase);
    }

    @ActivityRetainedScoped
    @Provides
    public static TagsViewModel tagsViewModel(TagsRepository tagsRepository) {
        return new TagsViewModel(tagsRepository);
    }
}