package com.caca.imagemachine.ui.addmachine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.caca.imagemachine.R;
import com.caca.imagemachine.adapter.MachineImageAdapter;
import com.caca.imagemachine.databinding.ActivityAddMachineBinding;
import com.caca.imagemachine.entity.MachineImage;
import com.caca.imagemachine.ui.base.BaseActivity;
import com.caca.imagemachine.util.FileUtil;
import com.caca.imagemachine.util.ItemOffsetDecoration;
import com.caca.imagemachine.util.Utility;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.IOException;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author caca rusmana on 18/03/22
 */
@AndroidEntryPoint
public class AddMachineActivity extends BaseActivity {

    private static final int PICK_IMAGE = 10;
    private static final int RUNTIME_PERMISSIONS = 11;

    private ActivityAddMachineBinding binding;
    private MachineImageAdapter machineImageAdapter;
    private AddMachineViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddMachineViewModel.class);
        mViewModel.fileDir = getFilesDir();
        mViewModel.isEdit = getIntent().getBooleanExtra("isEdit", false);
        mViewModel.machine = getIntent().getParcelableExtra("machine");

        binding = ActivityAddMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponent();
        initListener();
        initObserver();
    }

    @Override
    protected void initComponent() {
        machineImageAdapter = new MachineImageAdapter(mViewModel.machineImages, new MachineImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(MachineImage machineImage, int position) {
                if (mViewModel.isSelection()) {
                    mViewModel.onItemClick(machineImage);
                } else {
                    new StfalconImageViewer.Builder<>(AddMachineActivity.this, mViewModel.machineImages, (imageView, image) -> imageView.setImageBitmap(image.getBitmap()))
                            .withHiddenStatusBar(true)
                            .withStartPosition(position).show();
                }
            }

            @Override
            public void onLongItemClick(MachineImage machineImage, int position) {
                mViewModel.onLongItemClick(machineImage);
            }
        });

        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImages.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.recyclerview_item_offset));
        binding.rvImages.setAdapter(machineImageAdapter);

        if (mViewModel.isEdit) {
            initData();
        }
    }

    @Override
    protected void initListener() {
        binding.btnAdd.setOnClickListener((v) -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (hasPermissions()) {
                    pickImage();
                } else {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(this, permissions, RUNTIME_PERMISSIONS);
                }
            } else {
                pickImage();
            }
        });

        binding.btnSave.setOnClickListener((v) -> {
            if (validateInput()) {
                mViewModel.saveMachine(Utility.getEditTextValue(binding.tilMachineName), Utility.getEditTextValue(binding.tilMachineType),
                        Utility.getEditTextValue(binding.tilMachineQrCode), Utility.getEditTextValue(binding.tilMaintenanceDate));
            }
        });

        Objects.requireNonNull(binding.tilMaintenanceDate.getEditText()).setOnClickListener((v) -> {
            var builder = MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText(getString(R.string.label_select_date))
                    .build();

            builder.addOnPositiveButtonClickListener(selection -> binding.tilMaintenanceDate.getEditText().setText(builder.getHeaderText()));
            builder.show(getSupportFragmentManager(), "TAG");
        });

        binding.btnDeleteImage.setOnClickListener(v -> {
            mViewModel.deleteSelection();
        });
    }

    @Override
    protected void initObserver() {
        mViewModel.errorState.observe(this, isError -> {
            if (isError) {
                Utility.showSnackBar(binding.getRoot(), getString(R.string.message_opps_error));
            }
        });

        mViewModel.selectionModeState.observe(this, isSelection -> {
            if (isSelection == null || !isSelection) {
                Utility.viewsInvisible(binding.btnDeleteImage);
            } else {
                Utility.viewsVisible(binding.btnDeleteImage);
            }
        });

        mViewModel.machineImagesState.observe(this, machineImages -> {
            if (machineImages != null) {
                machineImageAdapter.setDataChanges(machineImages);
            }
        });

        mViewModel.cudMachineState.observe(this, result -> {
            if (result != null && result) {
                onBackPressed();
                mViewModel.resetCudMachineState();
            }
        });

        mViewModel.errorMessageState.observe(this, errorMessage -> {
            if (errorMessage != null) {
                Utility.showSnackBar(binding.getRoot(), errorMessage);
            }
        });
    }

    private void initData() {
        binding.btnSave.setText(getString(R.string.btn_update_machine));

        Utility.setTextInputEditTextValue(binding.tilMachineType, mViewModel.machine.getMachineType());
        Utility.setTextInputEditTextValue(binding.tilMachineName, mViewModel.machine.getMachineName());
        Utility.setTextInputEditTextValue(binding.tilMachineQrCode, mViewModel.machine.getMachineQrCode());
        Utility.setTextInputEditTextValue(binding.tilMaintenanceDate, mViewModel.machine.getLastMaintenanceDate());

        mViewModel.getMachineImages();
    }

    private boolean validateInput() {
        var counter = 0;
        Utility.clearErrors(binding.tilMachineName, binding.tilMachineType, binding.tilMachineQrCode);

        if (Utility.getEditTextValue(binding.tilMachineName).isEmpty()) {
            binding.tilMachineName.setError(getString(R.string.message_machine_name_required));
            counter++;
        }

        if (Utility.getEditTextValue(binding.tilMachineType).isEmpty()) {
            binding.tilMachineType.setError(getString(R.string.message_machine_type_required));
            counter++;
        }

        if (Utility.getEditTextValue(binding.tilMachineQrCode).isEmpty()) {
            binding.tilMachineQrCode.setError(getString(R.string.message_machine_qr_required));
            counter++;
        }

        return counter == 0;
    }

    private void pickImage() {
        var intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_pick_image)), PICK_IMAGE);
    }

    private void onDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.label_delete_machine))
                .setMessage(getString(R.string.message_delete_machine))
                .setPositiveButton(getString(R.string.btn_delete), (dialogInterface, i) -> mViewModel.deleteMachine()).setNegativeButton(getString(R.string.btn_cancel), (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                var mClipData = data.getClipData();
                int count = data.getClipData().getItemCount();

                if ((mViewModel.machineImages.size() + count) > 10) {
                    Utility.showSnackBar(binding.getRoot(), getString(R.string.message_max_image_files));
                    return;
                }

                for (int i = 0; i < count; i++) {
                    addMachineImages(mClipData.getItemAt(i).getUri());
                }
            } else {
                addMachineImages(data.getData());
            }
        }
    }


    private void addMachineImages(Uri imageUri) {
        try {
            mViewModel.addMachineImages(imageUri, FileUtil.getBitmapFromUri(this, imageUri), FileUtil.getFilenameFromUri(this, imageUri));
        } catch (IOException e) {
            Utility.showSnackBar(binding.getRoot(), getString(R.string.message_opps_error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mViewModel.isEdit) {
            var inflater = getMenuInflater();
            inflater.inflate(R.menu.add_machine_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_menu) {
            onDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.isSelection()) {
            mViewModel.resetSelection();
        } else {
            super.onBackPressed();
        }
    }

    public boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUNTIME_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }
}
