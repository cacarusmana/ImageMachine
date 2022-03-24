package com.caca.imagemachine.repository;

import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.entity.Machine;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

/**
 * @author caca rusmana on 23/03/22
 */
public class MachineRepository {

    private final MachineDao machineDao;

    @Inject
    public MachineRepository(MachineDao machineDao) {
        this.machineDao = machineDao;
    }


    public Single<List<Machine>> getAll(String sortBy) {
        if (sortBy.equals("name")) {
            return machineDao.getAllOrderByMachineName();
        } else {
            return machineDao.getAllOrderByMachineType();
        }
    }

    public Single<Machine> getByMachineQrCode(String machineQrCode) {
        return machineDao.findByMachineQrCode(machineQrCode);
    }

    public Machine saveMachine(Machine machine, boolean isEdit) {
        if (isEdit) {
            machineDao.update(machine);
        } else {
            var id = machineDao.insert(machine);
            machine.setMachineId(id);
        }

        return machine;
    }

    public void deleteMachine(Machine machine) {
        machineDao.delete(machine);
    }

    public Integer countByMachineQrCode(String machineQrCode) {
        return machineDao.countByMachineQrCode(machineQrCode);
    }

    public Integer countByMachineQrCodeAndMachineId(String machineQrCode, Long machineId) {
        return machineDao.countByMachineQrCodeAndMachineId(machineQrCode, machineId);
    }
}
