/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.TextView;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.utilities.ViewIds;

@SuppressLint("ViewConstructor")
public class TriggerWidget extends QuestionWidget {

    public static final String OK_TEXT = "OK";

    private AppCompatCheckBox triggerButton;
    private TextView stringAnswer;

    public TriggerWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);

        setUpTriggerButton();
        setUpStringAnswer();

        addAnswerView(triggerButton);
    }

    @Override
    public void clearAnswer() {
        stringAnswer.setText(null);
        triggerButton.setChecked(false);
    }

    @Override
    public IAnswerData getAnswer() {
        String s = stringAnswer.getText().toString();
        return !s.isEmpty()
                ? new StringData(s)
                : null;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        triggerButton.setOnLongClickListener(l);
        stringAnswer.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        triggerButton.cancelLongPress();
        stringAnswer.cancelLongPress();
    }

    public CheckBox getTriggerButton() {
        return triggerButton;
    }

    private void setUpTriggerButton() {
        triggerButton = new AppCompatCheckBox(getContext());
        triggerButton.setId(ViewIds.generateViewId());
        triggerButton.setText(getContext().getString(R.string.trigger));
        triggerButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        triggerButton.setEnabled(!getFormEntryPrompt().isReadOnly());
        triggerButton.setOnClickListener(v -> stringAnswer.setText(triggerButton.isChecked() ? OK_TEXT : null));
        triggerButton.setChecked(OK_TEXT.equals(getFormEntryPrompt().getAnswerText()));
    }

    private void setUpStringAnswer() {
        stringAnswer = new TextView(getContext());
        stringAnswer.setId(ViewIds.generateViewId());
        stringAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getAnswerFontSize());
        stringAnswer.setGravity(Gravity.CENTER);
        stringAnswer.setText(getFormEntryPrompt().getAnswerText());
    }
}
