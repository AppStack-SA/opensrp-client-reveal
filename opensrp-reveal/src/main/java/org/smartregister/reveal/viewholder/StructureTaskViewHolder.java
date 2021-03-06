package org.smartregister.reveal.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.reveal.R;
import org.smartregister.reveal.model.CardDetails;
import org.smartregister.reveal.model.StructureTaskDetails;
import org.smartregister.reveal.util.CardDetailsUtil;
import org.smartregister.reveal.util.Constants.BusinessStatus;
import org.smartregister.reveal.util.Constants.Intervention;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by samuelgithengi on 4/11/19.
 */
public class StructureTaskViewHolder extends RecyclerView.ViewHolder {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd", Locale.getDefault());
    private Context context;

    private TextView nameTextView;

    private TextView actionTextView;

    private ImageView viewEditImageView;

    private TextView lastEditedTextView;

    private TextView detailsTextView;

    private ImageView viewUndoImageView;

    public StructureTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        nameTextView = itemView.findViewById(R.id.task_name);
        actionTextView = itemView.findViewById(R.id.task_action);
        viewEditImageView = itemView.findViewById(R.id.view_edit);
        lastEditedTextView = itemView.findViewById(R.id.last_edited);
        detailsTextView = itemView.findViewById(R.id.task_details);
        viewUndoImageView = itemView.findViewById(R.id.view_undo);
    }

    public void setTaskName(String name) {
        nameTextView.setText(name);
        detailsTextView.setVisibility(View.GONE);
    }

    public void setTaskName(String taskName, String taskCode) {
        nameTextView.setText(taskName);
        detailsTextView.setText(taskCode);
        detailsTextView.setVisibility(View.VISIBLE);

    }

    public void setTaskAction(StructureTaskDetails taskDetails, View.OnClickListener onClickListener) {
        if (!BusinessStatus.NOT_VISITED.equals(taskDetails.getBusinessStatus())) {
            if (Intervention.CASE_CONFIRMATION.equals(taskDetails.getTaskCode())) {
                actionTextView.setText(context.getResources().getString(R.string.index_case_confirmed));
            } else if (StringUtils.isNotBlank(taskDetails.getPersonTested())
                    && Intervention.BLOOD_SCREENING.equals(taskDetails.getTaskCode())
                    && BusinessStatus.COMPLETE.equals(taskDetails.getBusinessStatus())) {
                String screening = context.getString(R.string.yes).equals(taskDetails.getPersonTested()) ?
                        context.getString(R.string.tested) : context.getString(R.string.not_tested);
                actionTextView.setText(screening);
            } else {
                actionTextView.setText(CardDetailsUtil.getTranslatedBusinessStatus(taskDetails.getBusinessStatus()));
            }
            actionTextView.setBackground(null);
            CardDetails cardDetails = new CardDetails(taskDetails.getBusinessStatus());
            CardDetailsUtil.formatCardDetails(cardDetails);
            actionTextView.setTextColor(context.getResources().getColor(cardDetails.getStatusColor()));
        } else {
            actionTextView.setText(taskDetails.getTaskAction());
            actionTextView.setBackground(context.getResources().getDrawable(R.drawable.structure_task_action_bg));
            actionTextView.setTextColor(context.getResources().getColor(R.color.task_not_done));
        }

        if (BusinessStatus.COMPLETE.equals(taskDetails.getBusinessStatus()) &&
                (Intervention.BEDNET_DISTRIBUTION.equals(taskDetails.getTaskCode()) || Intervention.BLOOD_SCREENING.equals(taskDetails.getTaskCode()))) {

            viewEditImageView.setVisibility(View.VISIBLE);
            setClickHandler(onClickListener, taskDetails, viewEditImageView);
            viewUndoImageView.setVisibility(View.VISIBLE);
            setClickHandler(onClickListener, taskDetails, viewUndoImageView);
            Date lastEdited = taskDetails.getLastEdited();
            if (lastEdited != null) {
                lastEditedTextView.setVisibility(View.VISIBLE);
                lastEditedTextView.setText(context.getString(R.string.last_edited, dateFormat.format(lastEdited)));
                actionTextView.setPadding(0, 0, 0, 0);
            } else {
                lastEditedTextView.setVisibility(View.GONE);
            }
        } else {
            viewEditImageView.setVisibility(View.GONE);
            lastEditedTextView.setVisibility(View.GONE);
            viewUndoImageView.setVisibility(View.GONE);
        }
        setClickHandler(onClickListener, taskDetails, actionTextView);

    }

    private void setClickHandler(View.OnClickListener onClickListener, StructureTaskDetails taskDetails, View view) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.task_details, taskDetails);
    }


}
