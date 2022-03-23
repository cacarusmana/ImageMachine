package com.caca.imagemachine.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.caca.imagemachine.entity.Machine;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

/**
 * @author caca rusmana on 18/03/22
 */
@Dao
public interface MachineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Machine machineImage);

    @Update
    void update(Machine machineImage);

    @Delete
    void delete(Machine machineImage);

    @Query("SELECT * FROM Machine ORDER BY machineName ASC")
    Single<List<Machine>> getAllOrderByMachineName();

    @Query("SELECT * FROM Machine ORDER BY machineType ASC")
    Single<List<Machine>> getAllOrderByMachineType();

    @Query("SELECT * FROM MACHINE WHERE machineQrCode = :machineQrCode")
    Single<Machine> findByMachineQrCode(String machineQrCode);
}
