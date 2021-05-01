package pko.delorean.time.tracker.domain;

public enum WorkLogType {
    WORK_LOG(Constants.UNDIVIDABLE, Constants.EXPORTABLE),//👷
    BREAK(Constants.DIVIDABLE, Constants.EXPORTABLE),//🏖️
    WORK_ORGANIZATION(Constants.DIVIDABLE, Constants.EXPORTABLE),//🗄️
    PRIVATE_TIME(Constants.UNDIVIDABLE, Constants.UNEXPORTABLE);//🏡

    private boolean dividable;
    private boolean exportable;

    WorkLogType(boolean dividable, boolean exportable) {
        this.dividable = dividable;
        this.exportable = exportable;
    }

    boolean isDividable() {
        return dividable;
    }

    public boolean isUndividable(){
        return !isDividable();
    }

    boolean isExportable() {
        return exportable;
    }

    public boolean isUnexportable(){
        return !isExportable();
    }

    private static class Constants {
        public static final boolean DIVIDABLE = true;
        public static final boolean UNDIVIDABLE = false;
        public static final boolean EXPORTABLE = true;
        public static final boolean UNEXPORTABLE = false;
    }
}
