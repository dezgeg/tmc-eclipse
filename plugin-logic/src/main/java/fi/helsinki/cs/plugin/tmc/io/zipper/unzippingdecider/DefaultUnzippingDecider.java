package fi.helsinki.cs.plugin.tmc.io.zipper.unzippingdecider;

import fi.helsinki.cs.plugin.tmc.domain.Project;
import fi.helsinki.cs.plugin.tmc.io.FileIO;

public class DefaultUnzippingDecider extends AbstractUnzippingDecider {

    public DefaultUnzippingDecider(Project project) {
        super(project);
    }

    @Override
    public boolean shouldUnzip(String filePath) {
        String s = project.getRootPath() + "/src";
        if (filePath.startsWith(s) && (filePath.equals(s) || filePath.charAt(s.length()) == '/')) {
            return !(new FileIO(filePath).fileExists());
        }
        return super.shouldUnzip(filePath);
    }

}
