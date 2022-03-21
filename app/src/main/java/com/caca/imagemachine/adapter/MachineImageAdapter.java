package com.caca.imagemachine.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caca.imagemachine.databinding.ItemMachineImageBinding;
import com.caca.imagemachine.entity.MachineImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caca rusmana on 18/03/22
 */
public class MachineImageAdapter extends RecyclerView.Adapter<MachineImageAdapter.MachineImageViewHolder> {

    private List<MachineImage> machines;
    private final ItemClickListener listener;

    public MachineImageAdapter(List<MachineImage> machines, ItemClickListener listener) {
        this.machines = machines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MachineImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemMachineImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MachineImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineImageViewHolder holder, int position) {
        holder.bindItem(machines.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return machines.size();
    }

    public void setDataChanges(List<MachineImage> machines) {
        this.machines = new ArrayList<>(machines);
        notifyDataSetChanged();
    }

    static class MachineImageViewHolder extends RecyclerView.ViewHolder {

        ItemMachineImageBinding binding;

        public MachineImageViewHolder(ItemMachineImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(MachineImage machineImage, int position, ItemClickListener listener) {
            binding.imgMachine.setImageBitmap(machineImage.getBitmap());
            binding.imgMachine.setOnClickListener((v) -> listener.onItemClick(machineImage, position));
            binding.imgMachine.setOnLongClickListener(view -> {
                listener.onLongItemClick(machineImage, position);
                return true;
            });

            if (machineImage.isChecked()) {
                binding.imgCheck.setVisibility(ViewGroup.VISIBLE);
            } else {
                binding.imgCheck.setVisibility(ViewGroup.GONE);
            }
        }

    }

    public interface ItemClickListener {
        void onItemClick(MachineImage machineImage, int position);

        void onLongItemClick(MachineImage machineImage, int position);
    }

}
