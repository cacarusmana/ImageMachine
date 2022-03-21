package com.caca.imagemachine.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.dao.MachineImageDao;
import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.entity.MachineImage;

/**
 * @author caca rusmana on 18/03/22
 */
@Database(entities = {Machine.class, MachineImage.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MachineDao machineDao();

    public abstract MachineImageDao machineImageDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "image_machine.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
