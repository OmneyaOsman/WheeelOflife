package com.omni.wheeeloflife.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.omni.wheeeloflife.R;
import com.omni.wheeeloflife.model.WheelTask;

import butterknife.BindView;
import butterknife.ButterKnife;



public class TasksAdapter extends FirebaseRecyclerAdapter<WheelTask ,TasksAdapter.ViewHolder> {

    private Context context ;
    private String category ;


    public TasksAdapter(@NonNull FirebaseRecyclerOptions<WheelTask> options, Context context , String category) {
        super(options);
        this.context = context;
        this.category = category;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_list_item,parent,false);
        return new TasksAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final WheelTask model) {

        holder.taskTitle.setText(model.getTaskName());
        if (model.isCompleted()) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.taskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("taskDetails", true);
                intent.putExtra("task", model);
                intent.putExtra("category", category);
                if (intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }
        });


    }




    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_name_list_item)
        TextView taskTitle;

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this , v);
        }


    }

}
