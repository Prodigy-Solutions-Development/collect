package org.odk.collect.android.tasks;

import static org.odk.collect.settings.keys.ProjectKeys.KEY_IMAGE_SIZE;

import android.net.Uri;
import android.os.AsyncTask;

import org.odk.collect.android.activities.FormFillingActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.injection.DaggerUtils;
import org.odk.collect.android.utilities.ContentUriHelper;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.ImageCompressionController;
import org.odk.collect.android.widgets.BaseImageWidget;
import org.odk.collect.android.widgets.QuestionWidget;
import org.odk.collect.androidshared.ui.DialogFragmentUtils;
import org.odk.collect.settings.SettingsProvider;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

public class MediaLoadingTask extends AsyncTask<Uri, Void, File> {

    private final File instanceFile;
    @Inject
    SettingsProvider settingsProvider;

    @Inject
    ImageCompressionController imageCompressionController;

    private WeakReference<FormFillingActivity> formFillingActivity;

    public MediaLoadingTask(FormFillingActivity formFillingActivity, File instanceFile) {
        this.instanceFile = instanceFile;
        onAttach(formFillingActivity);
    }

    public void onAttach(FormFillingActivity formFillingActivity) {
        this.formFillingActivity = new WeakReference<>(formFillingActivity);
        DaggerUtils.getComponent(this.formFillingActivity.get()).inject(this);
    }

    @Override
    protected File doInBackground(Uri... uris) {
        if (instanceFile != null) {
            String extension = ContentUriHelper.getFileExtensionFromUri(uris[0]);
            QuestionWidget questionWidget = formFillingActivity.get().getWidgetWaitingForBinaryData();
            File newFile = null;
            List<String> attributes = List.of("", "", "");
            if (questionWidget instanceof BaseImageWidget) {
                String prefix="";
                try {
                    attributes = FileUtils.readExifData(uris[0], Collect.getInstance());
                    prefix = attributes.get(1) + "," + attributes.get(2) + " " + attributes.get(0) + "_";
                } catch (Exception e) {
                }
                newFile = FileUtils.createDestinationMediaFile(instanceFile.getParent(), extension, prefix);
            } else {
                newFile = FileUtils.createDestinationMediaFile(instanceFile.getParent(), extension);
            } FileUtils.saveAnswerFileFromUri(uris[0], newFile, Collect.getInstance());

            // apply image conversion if the widget is an image widget
            if (questionWidget instanceof BaseImageWidget) {
                try {
                    FileUtils.printLocationAndDateOnOriginalImage(newFile, attributes.get(0), attributes.get(1), attributes.get(2));
                } catch (Exception e) {
                }
                String imageSizeMode = settingsProvider.getUnprotectedSettings().getString(KEY_IMAGE_SIZE);
                imageCompressionController.execute(newFile.getPath(), questionWidget, formFillingActivity.get(), imageSizeMode);
            }
            return newFile;
        } return null;
    }

    @Override
    protected void onPostExecute(File result) {
        FormFillingActivity activity = this.formFillingActivity.get();
        DialogFragmentUtils.dismissDialog(FormFillingActivity.TAG_PROGRESS_DIALOG_MEDIA_LOADING, activity.getSupportFragmentManager());
        activity.setWidgetData(result);
    }
}
