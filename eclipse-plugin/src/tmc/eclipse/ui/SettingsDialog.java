package tmc.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import tmc.eclipse.activator.CoreInitializer;
import fi.helsinki.cs.tmc.core.Core;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.services.DomainUtil;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;

public class SettingsDialog extends Dialog {

    protected Object result;
    protected Shell shell;
    private Text userNameText;
    private Text passWordText;
    private Text serverAddress;
    private Text filePathText;
    private Settings settings;
    private Label lblErrorText;
    private Button btnSavePassword;
    private DirectoryDialog dirDialog;
    private Button btnCheckFor;
    private Button btnCheckThat;
    private Button btnSendSnapshots;
    private Button btnOk;
    private Combo localeList;
    private Combo combo;
    private Shell parent;

    public SettingsDialog(Shell parent, int style) {
        super(parent, style);
        this.parent = parent;
        this.settings = Core.getSettings();
        setText("TMC Settings");
    }

    public Object open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /* Generated by WindowBuilder-tool */
    private void createContents() {

        shell = new Shell(getParent(), getStyle());
        shell.setSize(548, 483);
        shell.setText(getText());
        dirDialog = new DirectoryDialog(shell);

        lblErrorText = new Label(shell, SWT.NONE);
        lblErrorText.setForeground(SWTResourceManager.getColor(SWT.COLOR_LINK_FOREGROUND));
        lblErrorText.setBounds(10, 10, 430, 17);

        Label lblUserName = new Label(shell, SWT.NONE);
        lblUserName.setBounds(10, 44, 87, 17);
        lblUserName.setText("Username");

        userNameText = new Text(shell, SWT.BORDER);
        userNameText.setBounds(154, 44, 259, 27);

        Label lblPassword = new Label(shell, SWT.NONE);
        lblPassword.setBounds(10, 83, 80, 17);
        lblPassword.setText("Password");

        passWordText = new Text(shell, SWT.BORDER | SWT.PASSWORD);
        passWordText.setBounds(154, 77, 259, 27);

        Label lblServerAddress = new Label(shell, SWT.NONE);

        lblServerAddress.setText("Server address");
        lblServerAddress.setBounds(10, 117, 133, 17);

        serverAddress = new Text(shell, SWT.BORDER);
        serverAddress.setBounds(154, 110, 386, 27);

        btnSavePassword = new Button(shell, SWT.CHECK);
        btnSavePassword.setBounds(419, 77, 141, 24);
        btnSavePassword.setText("Save Password");

        Label lblCurrentCourse = new Label(shell, SWT.NONE);
        lblCurrentCourse.setText("Current course");
        lblCurrentCourse.setBounds(10, 151, 143, 17);

        combo = new Combo(shell, SWT.READ_ONLY);
        combo.setBounds(154, 143, 259, 29);

        List<String> courseNames = new ArrayList<String>();
        for (Course c : Core.getCourseDAO().getCourses()) {
            courseNames.add(c.getName());
        }

        combo.setItems(DomainUtil.getCourseNames(Core.getCourseDAO().getCourses()));
        combo.select(indexOfCurrentCourse());

        Button btnRefreshCourses = new Button(shell, SWT.NONE);
        btnRefreshCourses.setBounds(419, 143, 126, 29);
        btnRefreshCourses.setText("Refresh");
        btnRefreshCourses.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                combo.setEnabled(false);
                settings.setUsername(userNameText.getText());
                settings.setPassword(passWordText.getText());
                settings.setServerBaseUrl(serverAddress.getText());
                settings.setCurrentCourseName(combo.getText());

                try {
                    Core.getUpdater().updateCourses();
                    lblErrorText.setText("");
                } catch (UserVisibleException uve) {
                    lblErrorText.setText(uve.getMessage());
                }

                combo.setItems(DomainUtil.getCourseNames(Core.getCourseDAO().getCourses()));
                combo.select(indexOfCurrentCourse());
                combo.setEnabled(true);
            }
        });

        Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setBounds(10, 188, 530, 2);

        Label lblFolder = new Label(shell, SWT.NONE);
        lblFolder.setText("Folder for projects");
        lblFolder.setBounds(10, 206, 143, 17);

        Button btnCancel = new Button(shell, SWT.NONE);
        btnCancel.setBounds(439, 410, 101, 29);
        btnCancel.setText("Cancel");
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });

        btnOk = new Button(shell, SWT.NONE);
        btnOk.setText("OK");

        btnOk.setBounds(329, 410, 101, 29);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                saveSettings();
                if (filePathText.getText().isEmpty()) {
                    lblErrorText.setText("No valid file path for exercises!");
                } else {
                    try {
                        Core.getUpdater().updateCourses();
                        lblErrorText.setText("");
                        settings.setCurrentCourseName(combo.getText());
                        CoreInitializer.getDefault().getRecurringTaskRunner().updateBackgroundExerciseUpdateChecks();
                        shell.close();
                        showExDownloaderDialog();
                    } catch (UserVisibleException uve) {
                        lblErrorText.setText(uve.getMessage());
                    }
                }
            }
        });

        filePathText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
        filePathText.setBounds(154, 206, 259, 27);

        Button btnBrowse = new Button(shell, SWT.NONE);
        btnBrowse.setText("Browse...");
        btnBrowse.setBounds(419, 204, 121, 29);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String filePath = dirDialog.open();
                if (filePath != null) {
                    settings.setExerciseFilePath(filePath);
                    setDirectory(filePath);
                }
            }
        });

        Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label_1.setBounds(10, 239, 530, 2);

        btnCheckFor = new Button(shell, SWT.CHECK);
        btnCheckFor.setText("Check for new or updated exercises regularly");
        btnCheckFor.setBounds(10, 263, 403, 24);
        btnCheckFor.setSelection(settings.isCheckingForUpdatesInTheBackground());

        btnCheckThat = new Button(shell, SWT.CHECK);
        btnCheckThat.setSelection(true);
        btnCheckThat.setText("Check that all active active exercises are open on startup");
        btnCheckThat.setBounds(10, 293, 430, 24);
        btnCheckThat.setSelection(settings.isCheckingForUnopenedAtStartup());

        btnSendSnapshots = new Button(shell, SWT.CHECK);
        btnSendSnapshots.setText("Send snapshots of your progress for study");
        btnSendSnapshots.setSelection(true);
        btnSendSnapshots.setBounds(10, 323, 430, 24);
        btnSendSnapshots.setSelection(settings.isSpywareEnabled());

        Label label_2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label_2.setBounds(10, 353, 530, 2);

        Label lblPreferredErrorMessage = new Label(shell, SWT.NONE);
        lblPreferredErrorMessage.setText("Preferred error message language");
        lblPreferredErrorMessage.setBounds(10, 376, 236, 17);

        localeList = new Combo(shell, SWT.READ_ONLY);
        localeList.setItems(settings.getAvailableLocales());
        localeList.setBounds(252, 375, 284, 29);
        localeList.select(settings.getErrorMsgLocaleNum());

        addFieldData();
    }

    private int indexOfCurrentCourse() {
        int index = 0;
        for (String name : DomainUtil.getCourseNames(Core.getCourseDAO().getCourses())) {
            if (settings.getCurrentCourseName().equals(
                    DomainUtil.getCourseNames(Core.getCourseDAO().getCourses())[index])) {
                return index;
            }
            index++;
        }
        return 0;
    }

    private void showExDownloaderDialog() {
        if (!Core.getCourseDAO().getCurrentCourse(Core.getSettings()).getDownloadableExercises().isEmpty()) {
            ExerciseSelectorDialog esd = new ExerciseSelectorDialog(parent, SWT.SHEET);
            esd.open();
        }
    }

    private void addFieldData() {
        lblErrorText.setText("");
        btnSavePassword.setSelection(settings.isSavePassword());
        userNameText.setText(settings.getUsername());
        passWordText.setText(settings.getPassword());
        serverAddress.setText(settings.getServerBaseUrl());
        filePathText.setText(settings.getExerciseFilePath());
    }

    private final void setDirectory(String filePath) {
        filePathText.setText(filePath);
    }

    private final void saveSettings() {
        settings.setUsername(userNameText.getText());
        settings.setPassword(passWordText.getText());
        settings.setServerBaseUrl(serverAddress.getText());
        settings.setExerciseFilePath(filePathText.getText());
        settings.setCheckingForUpdatesInTheBackground(btnCheckFor.getSelection());
        settings.setCheckingForUnopenedAtStartup(btnCheckThat.getSelection());
        settings.setIsSpywareEnabled(btnSendSnapshots.getSelection());
        settings.setErrorMsgLocale(localeList.getSelectionIndex());
        settings.setSavePassword(btnSavePassword.getSelection());

        settings.save();
    }
}
