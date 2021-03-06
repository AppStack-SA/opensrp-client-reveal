package org.smartregister.reveal;


import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.repository.Repository;
import org.smartregister.reveal.application.RevealApplication;
import org.smartregister.reveal.util.AppExecutors;

import java.util.concurrent.Executors;

import io.ona.kujaku.data.realm.RealmDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestRevealApplication extends RevealApplication {


    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context);

        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }

    @Override
    public AppExecutors getAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }

    @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        return repository;
    }

    @Override
    public RealmDatabase getRealmDatabase(android.content.Context context) {

        return mock(RealmDatabase.class);
    }
}
