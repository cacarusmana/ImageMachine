package com.caca.imagemachine.di;

import android.content.Context;

import androidx.room.Room;

import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.dao.MachineImageDao;
import com.caca.imagemachine.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * @author caca rusmana on 18/03/22
 */
@Module
@InstallIn(SingletonComponent.class)
public class RoomModule {

    private static final String DB_NAME = "image_machine.db";

    @Singleton
    @Provides
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, DB_NAME)
                .build();
    }

    @Singleton
    @Provides
    public static MachineDao provideMachineDao(AppDatabase appDatabase) {
        return appDatabase.machineDao();
    }

    @Singleton
    @Provides
    public static MachineImageDao provideMachineImageDao(AppDatabase appDatabase) {
        return appDatabase.machineImageDao();
    }

}
