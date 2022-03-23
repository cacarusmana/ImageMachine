package com.caca.imagemachine.ui.addmachine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.entity.MachineImage;
import com.caca.imagemachine.repository.MachineImageRepository;
import com.caca.imagemachine.repository.MachineRepository;
import com.caca.imagemachine.ui.base.BaseViewModel;
import com.caca.imagemachine.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author caca rusmana on 21/03/22
 */
@HiltViewModel
public class AddMachineViewModel extends BaseViewModel {

    private final MachineRepository machineRepository;
    private final MachineImageRepository machineImageRepository;

    public Machine machine;
    public List<MachineImage> machineImages = new ArrayList<>();
    public final List<MachineImage> deletedMachineImages = new ArrayList<>();
    public File fileDir;
    public boolean isEdit = false;

    public MutableLiveData<List<MachineImage>> machineImagesState = new MutableLiveData<>();
    public MutableLiveData<Boolean> cudMachineState = new MutableLiveData<>();
    public MutableLiveData<Boolean> errorState = new MutableLiveData<>();
    public MutableLiveData<Boolean> selectionModeState = new MutableLiveData<>();


    @Inject
    public AddMachineViewModel(MachineRepository machineRepository, MachineImageRepository machineImageRepository) {
        this.machineRepository = machineRepository;
        this.machineImageRepository = machineImageRepository;
    }

    public void getMachineImages() {
        Single<List<MachineImage>> single = Single.create(emitter -> {
            machineImages = machineImageRepository.getMachineImagesByMachineId(machine.getMachineId());
            for (MachineImage machineImage : machineImages) {
                var imageFile = new File(fileDir, machineImage.getFileName());
                var bmOptions = new BitmapFactory.Options();
                machineImage.setBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions));
                machineImage.setNew(false);
            }

            emitter.onSuccess(machineImages);
        });


        disposable.add(single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMachineImages, this::setError)
        );
    }

    public void saveMachine(String machineName, String machineType, String machineQrCode, String lastMaintenanceDate) {
        var single = Completable.create(emitter -> {
            if (machine == null) {
                machine = new Machine();
            }

            machine.setMachineName(machineName);
            machine.setMachineType(machineType);
            machine.setMachineQrCode(machineQrCode);
            machine.setLastMaintenanceDate(lastMaintenanceDate);

            machineRepository.saveMachine(machine, isEdit);

            for (MachineImage machineImage : machineImages) {
                if (machineImage.isNew()) {
                    machineImage.setMachineId(machine.getMachineId());
                    FileUtil.writeToFile(machineImage.getBitmap(), fileDir, machineImage.getFileName());
                    machineImageRepository.saveMachineImage(machineImage);
                }
            }

            for (MachineImage machineImage : deletedMachineImages) {
                FileUtil.deleteFile(fileDir, machineImage.getFileName());
                machineImageRepository.deleteMachineImage(machineImage);
            }

            emitter.onComplete();
        });
        disposable.add(single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::setCudMachineState, this::setError));
    }

    public void deleteMachine() {
        disposable.add(Completable.create(emitter -> {
            var machineImages = machineImagesState.getValue();
            assert machineImages != null;

            machineImageRepository.deleteMachineImages(machineImages);
            machineRepository.deleteMachine(machine);
            for (MachineImage machineImage : machineImages) {
                FileUtil.deleteFile(fileDir, machineImage.getFileName());
            }

            for (MachineImage machineImage : deletedMachineImages) {
                FileUtil.deleteFile(fileDir, machineImage.getFileName());
            }

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::setCudMachineState, this::setError));
    }

    public void addMachineImages(Uri imageUri, Bitmap imageBitmap, String fileName) {
        boolean isExists = false;
        for (MachineImage machineImage : machineImages) {
            if (machineImage.getFileName().equals(fileName)) {
                isExists = true;
                break;
            }
        }

        if (!isExists) {
            machineImages.add(new MachineImage(fileName, imageBitmap));
            machineImagesState.postValue(machineImages);
        }
    }

    public void setMachineImages(List<MachineImage> machineImages) {
        machineImagesState.postValue(machineImages);
    }

    public void setCudMachineState() {
        cudMachineState.postValue(true);
    }

    public void resetCudMachineState() {
        cudMachineState.postValue(false);
    }

    public void updateSelectionModeState(boolean selection) {
        selectionModeState.postValue(selection);
    }

    public boolean isSelection() {
        if (selectionModeState.getValue() != null) {
            return selectionModeState.getValue();
        }
        return false;
    }

    public void resetSelection() {
        for (MachineImage machineImage : machineImages) {
            machineImage.setChecked(false);
        }
        machineImagesState.postValue(machineImages);
        selectionModeState.postValue(false);
    }

    public void deleteSelection() {
        for (int i = 0; i < machineImages.size(); i++) {
            if (machineImages.get(i).isChecked()) {
                deletedMachineImages.add(machineImages.get(i));
                machineImages.remove(i);
                i--;
            }
        }
        machineImagesState.postValue(machineImages);
        selectionModeState.postValue(false);
    }

    public void onItemClick(MachineImage machineImage) {
        machineImage.setChecked(!machineImage.isChecked());

        boolean isEmpty = true;
        for (MachineImage image : machineImages) {
            if (image.isChecked()) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            updateSelectionModeState(false);
        }
        machineImagesState.postValue(machineImages);
    }

    public void onLongItemClick(MachineImage machineImage) {
        if (!machineImage.isChecked()) {
            updateSelectionModeState(true);
        }
        machineImage.setChecked(!machineImage.isChecked());
        machineImagesState.postValue(machineImages);
    }
}
