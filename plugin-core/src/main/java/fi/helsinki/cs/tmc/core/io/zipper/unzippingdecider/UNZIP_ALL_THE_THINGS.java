package fi.helsinki.cs.tmc.core.io.zipper.unzippingdecider;

/**
 * Unzips all the things
 * 
 */
public class UNZIP_ALL_THE_THINGS implements UnzippingDecider {
    @Override
    public boolean shouldUnzip(String filePath) {
        return true;
    }
}