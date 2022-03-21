package com.caca.imagemachine.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.caca.imagemachine.entity.MachineImage;

import java.util.List;

import io.reactivex.Single;

/**
 * @author caca rusmana on 18/03/22
 */
@Dao
public interface MachineImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MachineImage> machineImages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MachineImage machineImage);

    @Update
    void update(MachineImage machineImage);

    @Delete
    void delete(List<MachineImage> machineImage);

    @Delete
    void delete(MachineImage machineImage);


    @Query("SELECT * FROM MachineImage")
    Single<List<MachineImage>> getAll();

    @Query("SELECT * FROM MachineImage WHERE machineId = :machineId")
    List<MachineImage> findByMachineId(long machineId);
}
