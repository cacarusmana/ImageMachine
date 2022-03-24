package com.caca.imagemachine.repository;

import com.caca.imagemachine.dao.MachineImageDao;
import com.caca.imagemachine.entity.MachineImage;

import java.util.List;

import javax.inject.Inject;

/**
 * @author caca rusmana on 23/03/22
 */
public class MachineImageRepository {

    private final MachineImageDao machineImageDao;

    @Inject
    public MachineImageRepository(MachineImageDao machineImageDao) {
        this.machineImageDao = machineImageDao;
    }

    public List<MachineImage> getMachineImagesByMachineId(Long machineId) {
        return machineImageDao.findByMachineId(machineId);
    }

    public void saveMachineImage(MachineImage machineImage) {
        machineImageDao.insert(machineImage);
    }

    public void deleteMachineImage(MachineImage machineImage) {
        machineImageDao.delete(machineImage);
    }

    public void deleteMachineImages(List<MachineImage> machineImages) {
        machineImageDao.delete(machineImages);
    }

    public boolean isImageFileNeedToBeDeleted(Long machineImageId, String fileName) {
        return machineImageDao.countByMachineImageIdAndFileName(machineImageId, fileName) == 0;
    }

}
