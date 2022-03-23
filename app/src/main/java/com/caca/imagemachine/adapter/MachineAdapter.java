package com.caca.imagemachine.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caca.imagemachine.databinding.ItemMachineBinding;
import com.caca.imagemachine.entity.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caca rusmana on 19/03/22
 */
public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MachineViewHolder> {

    private List<Machine> machines;
    private final ItemClickListener listener;

    public MachineAdapter(List<Machine> machines, ItemClickListener listener) {
        this.machines = machines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemMachineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MachineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
        holder.bindItem(machines.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return machines.size();
    }

    public void notifyDataChanges(List<Machine> machines) {
        this.machines = new ArrayList<>(machines);
        notifyDataSetChanged();
    }

    static class MachineViewHolder extends RecyclerView.ViewHolder {

        ItemMachineBinding binding;

        public MachineViewHolder(ItemMachineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(Machine machine, MachineAdapter.ItemClickListener listener) {
            binding.tvMachineName.setText(machine.getMachineName());
            binding.tvMachineType.setText(machine.getMachineType());

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(machine));
        }

    }

    public interface ItemClickListener {
        void onItemClick(Machine machine);
    }
}
