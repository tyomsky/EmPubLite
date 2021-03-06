package com.tyomsky.empublite;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import com.tyomsky.empublite.event.NoteLoadedEvent;
import de.greenrobot.event.EventBus;

public class NoteFragment extends Fragment implements TextWatcher {

    private static final String KEY_POSITION = "position";
    private EditText editor;
    private ShareActionProvider sap;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND).setType("text/plain");

    static NoteFragment newInstance(int position) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.editor, container, false);
        editor = (EditText) result.findViewById(R.id.editor);
        editor.addTextChangedListener(this);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);
        sap = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        sap.setShareIntent(shareIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
        if (TextUtils.isEmpty(editor.getText())) {
            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
            db.loadNote(getPosition());
        }
    }

    @Override
    public void onPause() {
        DatabaseHelper.getInstance(getActivity())
                .updateNote(getPosition(),
                        editor.getText().toString());
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEventMainThread(NoteLoadedEvent event) {
        if (event.getPosition() == getPosition()) {
            editor.setText(event.getProse());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            editor.setText(null);
            getContract().closeNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getPosition() {
        return getArguments().getInt(KEY_POSITION, -1);
    }

    private Contract getContract() {
        return (Contract) getActivity();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, s.toString());
    }

    public interface Contract {
        void closeNotes();
    }

}
