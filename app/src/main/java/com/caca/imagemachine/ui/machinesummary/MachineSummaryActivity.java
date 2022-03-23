package com.caca.imagemachine.ui.machinesummary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.caca.imagemachine.R;
import com.caca.imagemachine.adapter.MachineAdapter;
import com.caca.imagemachine.databinding.ActivityMachineSummaryBinding;
import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.ui.addmachine.AddMachineActivity;
import com.caca.imagemachine.ui.base.BaseActivity;
import com.caca.imagemachine.util.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MachineSummaryActivity extends BaseActivity {

    private ActivityMachineSummaryBinding binding;
    private MachineAdapter machineAdapter;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private MachineSummaryViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MachineSummaryViewModel.class);

        binding = ActivityMachineSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponent();
        initListener();
        initObserver();
    }

    @Override
    protected void initComponent() {
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        machineAdapter = new MachineAdapter(new ArrayList<>(), machine -> goToAddMachineActivity(machine, true));
        binding.rvMachines.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMachines.setAdapter(machineAdapter);
    }

    @Override
    protected void initListener() {
        binding.fabDashboard.setOnClickListener(v -> onFabClicked());
        binding.fabAdd.setOnClickListener((v) -> goToAddMachineActivity(null, false));
        binding.fabScan.setOnClickListener(v -> {
            var scanOptions = new ScanOptions();
            scanOptions.setBeepEnabled(false);
            scanOptions.setPrompt(getString(R.string.label_scan_qr_code));
            barcodeLauncher.launch(scanOptions);
        });
    }

    @Override
    protected void initObserver() {
        mViewModel.errorState.observe(this, isError -> {
            if (isError) {
                Toast.makeText(this, getString(R.string.message_opps_error), Toast.LENGTH_LONG).show();
            }
        });

        mViewModel.machinesState.observe(this, machines -> {
            if (machines != null) {
                if (machines.isEmpty()) {
                    Utility.viewsVisible(binding.tvNoData);
                } else {
                    Utility.viewsGone(binding.tvNoData);
                    machineAdapter.notifyDataChanges(machines);
                }
            }
        });

        mViewModel.machineState.observe(this, machine -> {
            if (machine != null) {
                goToAddMachineActivity(machine, true);
                mViewModel.resetMachineState();
            }
        });
    }

    private void goToAddMachineActivity(Machine machine, boolean isEdit) {
        var intent = new Intent(this, AddMachineActivity.class);
        intent.putExtra("isEdit", isEdit);
        if (machine != null) {
            intent.putExtra("machine", machine);
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        var inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_menu) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.label_sort_machine))
                    .setSingleChoiceItems(getResources().getStringArray(R.array.sort_array), mViewModel.selectedSortedIndex, (dialogInterface, i) -> mViewModel.selectedSortedIndex = i)
                    .setPositiveButton(getString(R.string.btn_ok), (dialogInterface, i) -> {
                        mViewModel.getAllMachines();
                        dialogInterface.dismiss();
                    }).setNegativeButton(getString(R.string.btn_cancel), (dialogInterface, i) -> dialogInterface.dismiss()).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    mViewModel.getMachineByQrCode(result.getContents());
                }
            });

    private void onFabClicked() {
        if (!mViewModel.fabVisibility) {
            fabVisible();
        } else {
            fabGone();
        }
        mViewModel.updateFabVisibility();
    }

    private void fabVisible() {
        Utility.viewsVisible(binding.fabAdd, binding.fabScan);
        binding.fabAdd.startAnimation(fromBottom);
        binding.fabScan.startAnimation(fromBottom);
        binding.fabDashboard.startAnimation(rotateOpen);
    }

    private void fabGone() {
        Utility.viewsGone(binding.fabAdd, binding.fabScan);
        binding.fabAdd.startAnimation(toBottom);
        binding.fabScan.startAnimation(toBottom);
        binding.fabDashboard.startAnimation(rotateClose);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.getAllMachines();

        if (mViewModel.fabVisibility) {
            fabVisible();
        }
    }
}