package pt.isel.pdm.li51n.g4.tmdbisel.presentation.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG_LOG_DEBUG = "DEBUG";
    private static final int DEFAULT_INT = 9999;

    private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String RES_ATTRIB_DIALOG_MSG = "dialogMessage";
    private static final String RES_ATTRIB_TEXT = "text";
    private static final String RES_ATTRIB_MAX = "max";
    private static final String RES_ATTRIB_DEFAULT = "defaultValue";
    SeekBar seekBar;
    private String dialogMsg;
    private String units;
    private TextView valueTextView;
    private int maxValue, defaultValue, currentValue, transientValue;
    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference Constructor!");

        int resId; // For saving resource IDs

        // Get SeekBar dialog message (subtitle) string defined on correspondent resource attribute
        resId = attrs.getAttributeResourceValue(ANDROID_NAMESPACE, RES_ATTRIB_DIALOG_MSG, DEFAULT_INT);
        dialogMsg = getContext().getResources().getString(resId);

        // Get Seekbar units string defined on correspondent resource attribute (text)
        resId = attrs.getAttributeResourceValue(ANDROID_NAMESPACE, RES_ATTRIB_TEXT, DEFAULT_INT);
        units = getContext().getResources().getString(resId);

        // Get SeekBar maximum allowed value defined on correspondent resource attribute (max)
        maxValue = attrs.getAttributeIntValue(ANDROID_NAMESPACE, RES_ATTRIB_MAX, DEFAULT_INT);
        // Get Seekbar default value defined on correspondent resource attribute (max)
        defaultValue = attrs.getAttributeIntValue(ANDROID_NAMESPACE, RES_ATTRIB_DEFAULT, DEFAULT_INT);
    }

    @Override
    protected View onCreateDialogView() {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onCreateDialogView!");

        Context context = getContext();

        // Set main Layout which will contain two Views and the SeekBar
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Define two layout parameters: for describing text and the Seekbar
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // Add a new TextView with the description (subtitle) to Layout
        TextView msgTextView = new TextView(context);
        msgTextView.setPadding(75, 10, 0, 10);
        msgTextView.setText(dialogMsg);
        layout.addView(msgTextView, textParams);

        // Create and save new TextView with the current SeekBar value and units; add it to Layout
        valueTextView = new TextView(context);
        valueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        valueTextView.setTextSize(24);
        String concat = String.valueOf(currentValue).concat(" ") + units;
        valueTextView.setText(concat);
        layout.addView(valueTextView, textParams);

        // Create and save new SeekBar with current defined maximum value
        seekBar = new AppCompatSeekBar(getContext());
        seekBar.setOnSeekBarChangeListener(this); // Register SeekBar for change Listener
        seekBar.setMax(maxValue);
        layout.addView(seekBar, seekBarParams);   // Add it to Layout

        return layout;
    }

    //Binds views in the content View of the dialog to data.
    @Override
    protected void onBindDialogView(View view) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onBindDialogView: " + currentValue);

        super.onBindDialogView(view);

        seekBar.setProgress(transientValue); // Set last value verified even if not saved
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onSaveInstanceState!");

        final Parcelable superState = super.onSaveInstanceState();

        if (transientValue == currentValue) // If SeekBar not changed no need to persist transient value
            return superState;

        final SavedState savedState = new SavedState(superState);
        savedState.value = transientValue; // Save transient value to guarantee the change on SeekBar will be remembered

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onRestoreInstanceState!");

        // If no previous state exists
        if (state == null || !state.getClass().equals(SeekBarPreference.SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }

        // Get previous saved state
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        transientValue = savedState.value;
        seekBar.setProgress(transientValue); // Set it to the SeekBar
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onSetInitialValue!");

        super.onSetInitialValue(restorePersistedValue, defaultValue);

        if (restorePersistedValue) //If value was persisted earlier
            currentValue = getPersistedInt(this.defaultValue); // Get it as current value
        else { // If value was not persisted yet
            currentValue = (Integer) defaultValue; // Current value is the default resource value
            persistInt(currentValue); // And persist it for next time
        }
        transientValue = currentValue; // Initiate transient value to current value
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onGetDefaultValue!");

        return a.getInteger(index, defaultValue);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onDialogClose!");

        if (positiveResult) { // If defined value should be registered
            currentValue = seekBar.getProgress();
            persistInt(currentValue);
            Log.d(TAG_LOG_DEBUG, "Persisted " + currentValue);
        }

        transientValue = currentValue;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String concat = String.valueOf(progress).concat(" ") + units;
        valueTextView.setText(concat);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onStartTrackingTouch!");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG_LOG_DEBUG, "SeekBarPreference onStopTrackingTouch!");
        transientValue = seekBar.getProgress();
    }

    private static class SavedState extends BaseSavedState {
        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                Log.d(TAG_LOG_DEBUG, "CREATOR: CreateFromParcel!");
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                Log.d(TAG_LOG_DEBUG, "CREATOR: NewArray!");
                return new SavedState[size];
            }
        };
        private int value; // Seekbar value to maintain between reconfigurations

        public SavedState(Parcelable superState) {
            super(superState);
            Log.d(TAG_LOG_DEBUG, "SavedState constructor 1!");
        }

        public SavedState(Parcel source) {
            super(source);
            Log.d(TAG_LOG_DEBUG, "SavedState constructor 2!");
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Log.d(TAG_LOG_DEBUG, "SavedState WriteToParcel!");

            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }
    }
}
