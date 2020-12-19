package com.pinder.app.util;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class NetworkBoundResource<CacheObject, FirebaseObject> {
    private static final String TAG = "NetworkBound";
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    public NetworkBoundResource() {
        init();
    }

    private void init() {
        // update LiveData for loading status
        results.setValue((Resource<CacheObject>) Resource.loading(null));
        // observe LiveData source from local db
        final LiveData<CacheObject> dbSource = loadFromDb();
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                results.removeSource(dbSource);
                if (shouldFetch(cacheObject)) {
                    // get data from the network
                    fetchFromNetwork(dbSource);
                } else {
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }

    /**
     * 1) observe local db
     * 2) if <condition/> query the firebase
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource) {
        Log.d(TAG, "fetchFromNetwork: called.");
        // update LiveData for loading status
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject));
            }
        });
        final LiveData<Resource<FirebaseObject>> firebaseResponse = createFirebaseCall();
        results.addSource(firebaseResponse, new Observer<Resource<FirebaseObject>>() {
            @Override
            public void onChanged(@Nullable final Resource<FirebaseObject> firebaseObjectResponse) {
                results.removeSource(dbSource);
                results.removeSource(firebaseResponse);
                /*
                    3 cases:
                       1) FirebaseSuccessResponse
                       2) FirebaseErrorResponse
                       3) FirebaseEmptyResponse
                 */
                switch (firebaseObjectResponse.status) {
                    case SUCCESS:
                        Log.d(TAG, "onChanged: request success.");
                        Single.create(emitter -> {
                            saveFirebaseResult((FirebaseObject) processResponse((Resource) firebaseObjectResponse));
                            emitter.onSuccess("success");
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Object>() {
                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            }

                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Object o) {
                                Log.d(TAG, "success");
                                results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                    @Override
                                    public void onChanged(@Nullable CacheObject cacheObject) {
                                        Log.d(TAG, "success");
                                        setValue(Resource.success(cacheObject));
                                    }
                                });
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        });
                        break;
                    case EMPTY:
                        Log.d(TAG, "onChanged: empty request");
                        results.addSource(loadFromDb(), new Observer<CacheObject>() {
                            @Override
                            public void onChanged(@Nullable CacheObject cacheObject) {
                                setValue(Resource.success(cacheObject));
                            }
                        });
                        break;
                    case ERROR:
                        Log.d(TAG, "onChanged: request error.");
                        results.addSource(dbSource, new Observer<CacheObject>() {
                            @Override
                            public void onChanged(@Nullable CacheObject cacheObject) {
                                setValue(
                                        Resource.error(
                                                "Request error",
                                                cacheObject
                                        )
                                );
                            }
                        });
                        break;
                }
            }
        });
    }

    private CacheObject processResponse(Resource response) {
        return (CacheObject) response.data;
    }

    private void setValue(Resource<CacheObject> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    // Called to save the result of the firebase response into the database.
    @WorkerThread
    protected abstract void saveFirebaseResult(@NonNull FirebaseObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the firebase call.
    @NonNull
    @MainThread
    protected abstract LiveData<Resource<FirebaseObject>> createFirebaseCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData() {
        return results;
    }
}