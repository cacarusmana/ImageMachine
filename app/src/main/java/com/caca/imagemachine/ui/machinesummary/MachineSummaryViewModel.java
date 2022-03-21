package com.caca.imagemachine.ui.machinesummary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.ui.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * @author caca rusmana on 21/03/22
 */
//@HiltViewModel
public class MachineSummaryViewModel extends BaseViewModel {

    private MutableLiveData<Boolean> selectionMode = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDashboardClicked = new MutableLiveData<>();
    private Machine machine;
    private Boolean isEdit;
    private boolean selectedSortedIndex;


    private MachineDao machineDao;

//    @Inject
//    public MachineSummaryViewModel(MachineDao machineDao) {
//        this.machineDao = machineDao;
//    }

    public void getAllMachines(){

    }

    public void getMachineByQrCode(String qrCode){

    }

    public LiveData<Boolean> isDashboardClicked() {
        return isDashboardClicked;
    }

    public void updateDashboardClicked(boolean isClicked){
        isDashboardClicked.postValue(isClicked);
    }
}
