package com.caca.imagemachine.ui.machinesummary;

import androidx.lifecycle.MutableLiveData;

import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.repository.MachineRepository;
import com.caca.imagemachine.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author caca rusmana on 21/03/22
 */
@HiltViewModel
public class MachineSummaryViewModel extends BaseViewModel {

    public MutableLiveData<List<Machine>> machinesState = new MutableLiveData<>();
    public MutableLiveData<Machine> machineState = new MutableLiveData<>();
    public int selectedSortedIndex = 0;
    public boolean fabVisibility = false;

    private final MachineRepository machineRepository;

    @Inject
    public MachineSummaryViewModel(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public void getAllMachines() {
        var single = machineRepository.getAll(selectedSortedIndex == 0 ? "name" : "type");
        disposable.add(single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMachinesState, this::setError));
    }

    public void getMachineByQrCode(String machineQrCode) {
        var single = machineRepository.getByMachineQrCode(machineQrCode);
        disposable.add(single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMachineState, this::setError));
    }

    private void setMachinesState(List<Machine> machines) {
        machinesState.postValue(machines);
    }

    private void setMachineState(Machine machine) {
        machineState.postValue(machine);
    }

    public void updateFabVisibility() {
        fabVisibility = !fabVisibility;
    }

    public void resetMachineState() {
        machineState.postValue(null);
    }
}
