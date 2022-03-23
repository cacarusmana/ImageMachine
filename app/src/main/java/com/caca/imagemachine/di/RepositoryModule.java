package com.caca.imagemachine.di;

import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.dao.MachineImageDao;
import com.caca.imagemachine.repository.MachineImageRepository;
import com.caca.imagemachine.repository.MachineRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * @author caca rusmana on 23/03/22
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Singleton
    @Provides
    MachineRepository provideMachineRepository(MachineDao machineDao) {
        return new MachineRepository(machineDao);
    }

    @Singleton
    @Provides
    MachineImageRepository provideMachineImageRepository(MachineImageDao machineImageDao) {
        return new MachineImageRepository(machineImageDao);
    }
}
