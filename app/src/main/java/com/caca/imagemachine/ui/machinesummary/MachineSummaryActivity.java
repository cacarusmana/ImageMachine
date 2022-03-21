package com.caca.imagemachine.ui.machinesummary;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.caca.imagemachine.R;
import com.caca.imagemachine.adapter.MachineAdapter;
import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.database.AppDatabase;
import com.caca.imagemachine.databinding.ActivityMainBinding;
import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.ui.addmachine.AddMachineActivity;
import com.caca.imagemachine.ui.base.BaseActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

//@AndroidEntryPoint
public class MachineSummaryActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private MachineAdapter machineAdapter;
    private List<Machine> machines = new ArrayList<>();
    private MachineDao machineDao;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isDashboardClicked = false;
    private int selectedSortedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        machineDao = AppDatabase.getInstance(this).machineDao();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        machineAdapter = new MachineAdapter(machines, machine -> goToAddMachineActivity(machine, true));
        binding.rvMachines.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMachines.setAdapter(machineAdapter);
    }

    @Override
    protected void initListener() {
        binding.fabDashboard.setOnClickListener(v -> onDashboardButtonClicked());
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
                    .setSingleChoiceItems(getResources().getStringArray(R.array.sort_array), selectedSortedIndex, (dialogInterface, i) -> selectedSortedIndex = i)
                    .setPositiveButton(getString(R.string.btn_ok), (dialogInterface, i) -> {
                        loadMachines();
                        dialogInterface.dismiss();
                    }).setNegativeButton(getString(R.string.btn_cancel), (dialogInterface, i) -> dialogInterface.dismiss()).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    onScanQrCode(result.getContents());
                }
            });


    private void onScanQrCode(String qrCode) {
        disposable.add(
                machineDao.findByMachineQrCode(qrCode)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((machine, throwable) -> {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                Toast.makeText(MachineSummaryActivity.this, getString(R.string.message_data_not_found), Toast.LENGTH_LONG).show();
                            } else {
                                goToAddMachineActivity(machine, true);
                            }
                        })
        );
    }

    private void onDashboardButtonClicked() {
        setVisibility();
        setAnimation();
        isDashboardClicked = !isDashboardClicked;
    }

    private void setVisibility() {
        if (!isDashboardClicked) {
            binding.fabAdd.setVisibility(View.VISIBLE);
            binding.fabScan.setVisibility(View.VISIBLE);
        } else {
            binding.fabAdd.setVisibility(View.GONE);
            binding.fabScan.setVisibility(View.GONE);
        }
    }

    private void setAnimation() {
        if (!isDashboardClicked) {
            binding.fabAdd.startAnimation(fromBottom);
            binding.fabScan.startAnimation(fromBottom);
            binding.fabDashboard.startAnimation(rotateOpen);
        } else {
            binding.fabAdd.startAnimation(toBottom);
            binding.fabScan.startAnimation(toBottom);
            binding.fabDashboard.startAnimation(rotateClose);
        }
    }

    private void loadMachines() {
        var single = selectedSortedIndex == 0 ? machineDao.getAllOrderByMachineName() : machineDao.getAllOrderByMachineType();
        disposable.add(single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((machines, throwable) -> {
                    this.machines = machines;
                    if (throwable != null) {
                        Toast.makeText(MachineSummaryActivity.this, "Oops error", Toast.LENGTH_SHORT).show();
                    } else {
                        machineAdapter.notifyDataChanges(machines);
                    }
                }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMachines();
    }


    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }
}