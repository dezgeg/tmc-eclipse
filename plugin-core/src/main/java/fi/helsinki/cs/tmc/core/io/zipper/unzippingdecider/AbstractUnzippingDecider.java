package fi.helsinki.cs.tmc.core.io.zipper.unzippingdecider;

import java.io.File;
import java.util.List;

import fi.helsinki.cs.tmc.core.domain.Project;

/**
 * Abstract base class that provides common functionality for all unzipping
 * deciders.
 */
public abstract class AbstractUnzippingDecider implements UnzippingDecider {
    protected Project project;
    private List<String> doNotUnzip;

    public AbstractUnzippingDecider(Project project) {
        this.project = project;
        this.doNotUnzip = TmcProjectFile.forProject(new File(project.getRootPath())).getExtraStudentFiles();
    }

    /**
     * Prevents unzipping if file would overwrite file on the extra student
     * files list.
     */
    @Override
    public boolean shouldUnzip(String filePath) {
        for (String s : doNotUnzip) {
            if (s.charAt(s.length() - 1) == '/') {
                s = s.substring(0, s.length() - 1);
            }

            s = (project.getRootPath() + "/" + s);

            if (filePath.startsWith(s) && (filePath.equals(s) || filePath.charAt(s.length()) == '/')) {
                return false;
            }
        }
        return true;
    }
}
