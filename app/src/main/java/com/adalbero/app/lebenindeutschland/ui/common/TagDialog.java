package com.adalbero.app.lebenindeutschland.ui.common;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 20/05/2017.
 */

public class TagDialog extends DialogFragment {

    private ResultCallback mCallback;

    public Set<String> selected =  new TreeSet<>();
    private TextView newTag;
    private boolean allowNewTag;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_tag, null);
        initView(view);


        if (title == null) title = "Tags";

        builder.setView(view)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String text = newTag.getText().toString();
                        if (text != null && text.length() > 0) {
                            selected.add(text.trim());
                        }
                        mCallback.onResult(TagDialog.this, null);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TagDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void initView(View v) {
        Set<String> tags = AppController.getInstance().getQuestionDB().getAllTags();
        List<String> data = new ArrayList<>(tags);

        removeOldTags(data);

        TagItemAdapter adapter = new TagItemAdapter(v.getContext(), data, selected);
        ListView listView = v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        newTag = v.findViewById((R.id.new_tag));
        newTag.setVisibility(allowNewTag ? View.VISIBLE : View.GONE);
    }

    private void removeOldTags(List<String> data) {
        if (selected != null) {
            Iterator<String> iter = selected.iterator();
            while (iter.hasNext()) {
                String tag = iter.next();
                if (!data.contains(tag))
                    iter.remove();
            }
        }

    }

    public void setTags(Set<String> tags, ResultCallback callback, boolean allowNewTag) {
        mCallback = callback;
        selected = new TreeSet<>(tags);
        this.allowNewTag = allowNewTag;
    }

    public Set<String> getSelected() {
        return selected;
    }

}
