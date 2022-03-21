package com.caca.imagemachine.ui.addmachine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.caca.imagemachine.R;
import com.caca.imagemachine.adapter.MachineImageAdapter;
import com.caca.imagemachine.dao.MachineDao;
import com.caca.imagemachine.dao.MachineImageDao;
import com.caca.imagemachine.database.AppDatabase;
import com.caca.imagemachine.databinding.ActivityAddMachineBinding;
import com.caca.imagemachine.entity.Machine;
import com.caca.imagemachine.entity.MachineImage;
import com.caca.imagemachine.ui.base.BaseActivity;
import com.caca.imagemachine.util.FileUtil;
import com.caca.imagemachine.util.ItemOffsetDecoration;
import com.caca.imagemachine.util.Utility;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author caca rusmana on 18/03/22
 */
public class AddMachineActivity extends BaseActivity {

    private static final int PICK_IMAGE = 10;
    private static final int RUNTIME_PERMISSIONS = 11;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private ActivityAddMachineBinding binding;
    private List<MachineImage> machineImages = new ArrayList<>();
    private MachineImageAdapter machineImageAdapter;

    private MachineDao machineDao;
    private MachineImageDao machineImageDao;
    private Machine machine;
    private boolean isEdit = false;
    private final List<MachineImage> deletedMachineImages = new ArrayList<>();
    private boolean isSelectedMode = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var database = AppDatabase.getInstance(this);
        machineDao = database.machineDao();
        machineImageDao = database.machineImageDao();

        binding = ActivityAddMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponent();
        initListener();
    }

    @Override
    protected void initComponent() {
        machineImageAdapter = new MachineImageAdapter(machineImages, new MachineImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(MachineImage machineImage, int position) {
                if (isSelectedMode) {
                    machineImage.setChecked(!machineImage.isChecked());
                    machineImageAdapter.notifyDataSetChanged();

                    boolean isEmpty = true;
                    for (MachineImage image : machineImages) {
                        if (image.isChecked()) {
                            isEmpty = false;
                            break;
                        }
                    }

                    if (isEmpty) {
                        isSelectedMode = false;
                        binding.btnDeleteImage.setVisibility(View.INVISIBLE);
                    }
                } else {
                    var imageLoader = new ImageLoader<MachineImage>() {
                        @Override
                        public void loadImage(ImageView imageView, MachineImage image) {
                            imageView.setImageBitmap(image.getBitmap());
                        }
                    };

                    new StfalconImageViewer.Builder<>(AddMachineActivity.this, machineImages, imageLoader)
                            .withHiddenStatusBar(true)
                            .withStartPosition(position).show();
                }

            }

            @Override
            public void onLongItemClick(MachineImage machineImage, int position) {
                if (!machineImage.isChecked()) {
                    isSelectedMode = true;
                    binding.btnDeleteImage.setVisibility(View.VISIBLE);
                }

                machineImage.setChecked(!machineImage.isChecked());
                machineImageAdapter.notifyDataSetChanged();
            }
        });

        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImages.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.recyclerview_item_offset));
        binding.rvImages.setAdapter(machineImageAdapter);

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            populateData();
        }
    }

    @Override
    protected void initObserver() {

    }

    private void populateData() {
        binding.btnSave.setText(getString(R.string.btn_update_machine));

        machine = getIntent().getParcelableExtra("machine");
        Objects.requireNonNull(binding.tilMachineType.getEditText()).setText(machine.getMachineType());
        if (binding.tilMachineName.getEditText() != null) {
            binding.tilMachineName.getEditText().setText(machine.getMachineName());
        }
        if (binding.tilMachineQrCode.getEditText() != null) {
            binding.tilMachineQrCode.getEditText().setText(machine.getMachineQrCode());
        }
        if (binding.tilMaintenanceDate.getEditText() != null) {
            binding.tilMaintenanceDate.getEditText().setText(machine.getLastMaintenanceDate());
        }


        getMachineData();
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
            saveMachine();
        });

        Objects.requireNonNull(binding.tilMaintenanceDate.getEditText()).setOnClickListener((v) -> {
            var builder = MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText(getString(R.string.label_select_date))
                    .build();

            builder.addOnPositiveButtonClickListener(selection -> binding.tilMaintenanceDate.getEditText().setText(builder.getHeaderText()));
            builder.show(getSupportFragmentManager(), "TT");
        });

        binding.btnDeleteImage.setOnClickListener(v -> {
            for (int i = 0; i < machineImages.size(); i++) {
                if (machineImages.get(i).isChecked()) {
                    deletedMachineImages.add(machineImages.get(i));
                    machineImages.remove(i);
                    i--;
                }
            }

            machineImageAdapter.setDataChanges(machineImages);
            binding.btnDeleteImage.setVisibility(View.INVISIBLE);
            isSelectedMode = false;
        });
    }

    private void getMachineData() {
        Single<List<MachineImage>> single = Single.create(emitter -> {
            machineImages = machineImageDao.findByMachineId(machine.getMachineId());
            for (MachineImage machineImage : machineImages) {
                var imageFile = new File(getFilesDir(), machineImage.getFileName());
                var bmOptions = new BitmapFactory.Options();
                machineImage.setBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions));
            }

            emitter.onSuccess(machineImages);
        });

        disposable.add(
                single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((machineImages, throwable) -> {
                            if (throwable != null) {
                                Toast.makeText(AddMachineActivity.this, "Error", Toast.LENGTH_LONG).show();
                            } else {
                                for (MachineImage machineImage : machineImages) {
                                    machineImage.setNew(false);
                                }
                                machineImageAdapter.setDataChanges(machineImages);
                            }
                        })
        );
    }

    private void saveMachine() {
        disposable.add(
                Completable.create(emitter -> {
                    if (machine == null) {
                        machine = new Machine();
                    }

                    machine.setMachineName(Utility.getEditTextValue(binding.tilMachineName));
                    machine.setMachineType(Utility.getEditTextValue(binding.tilMachineType));
                    machine.setMachineQrCode(Utility.getEditTextValue(binding.tilMachineQrCode));
                    machine.setLastMaintenanceDate(Utility.getEditTextValue(binding.tilMaintenanceDate));

                    if (isEdit) {
                        machineDao.update(machine);
                    } else {
                        var id = machineDao.insert(machine);
                        machine.setMachineId(id);
                    }

                    for (MachineImage machineImage : machineImages) {
                        if (machineImage.isNew()) {
                            machineImage.setMachineId(machine.getMachineId());
                            FileUtil.writeToFile(machineImage.getBitmap(), getFilesDir(), machineImage.getFileName());
                            machineImageDao.insert(machineImage);
                        }
                    }

                    for (MachineImage machineImage : deletedMachineImages) {
                        FileUtil.deleteFile(getFilesDir(), machineImage.getFileName());
                        machineImageDao.delete(machineImage);
                    }

                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> onBackPressed(),
                                throwable -> {
                                    throwable.printStackTrace();
                                    Toast.makeText(AddMachineActivity.this, getString(R.string.message_opps_error), Toast.LENGTH_LONG).show();
                                })
        );
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
                .setPositiveButton(getString(R.string.btn_delete), (dialogInterface, i) -> deleteMachine()).setNegativeButton(getString(R.string.btn_cancel), (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    private void deleteMachine() {
        disposable.add(Completable.create(emitter -> {
                    machineImageDao.delete(machineImages);
                    machineDao.delete(machine);
                    for (MachineImage machineImage : machineImages) {
                        FileUtil.deleteFile(getFilesDir(), machineImage.getFileName());
                    }

                    for (MachineImage machineImage : deletedMachineImages) {
                        FileUtil.deleteFile(getFilesDir(), machineImage.getFileName());
                    }

                    emitter.onComplete();
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onBackPressed,
                                throwable -> {
                                    throwable.printStackTrace();
                                    Toast.makeText(AddMachineActivity.this, getString(R.string.message_opps_error), Toast.LENGTH_LONG).show();
                                })
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                var mClipData = data.getClipData();
                int count = data.getClipData().getItemCount();

                if (count > 10) {
                    Toast.makeText(this, getString(R.string.message_max_image_files), Toast.LENGTH_SHORT).show();
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
        var fileName = FileUtil.getFilenameFromUri(this, imageUri);
        boolean isExists = false;
        for (MachineImage machineImage : machineImages) {
            if (machineImage.getFileName().equals(fileName)) {
                isExists = true;
                break;
            }
        }

        if (!isExists) {
            try {
                var bitmap = FileUtil.getBitmapFromUri(this, imageUri);
                machineImages.add(new MachineImage(fileName, bitmap));
                machineImageAdapter.setDataChanges(machineImages);
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
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
        if (isSelectedMode) {
            for (MachineImage machineImage : machineImages) {
                machineImage.setChecked(false);
            }
            machineImageAdapter.notifyDataSetChanged();
            isSelectedMode = false;
            binding.btnDeleteImage.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }
}
