package com.caca.imagemachine.database;

import androidx.room.Database;
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

}
