package org.gooru.nucleus.search.indexers.app.constants;

public enum ColumnFamilyConstants {

    CONTENT_STATISTICS("content_statistics")
    ;
	String name;

    private ColumnFamilyConstants(String name) {
            this.name = name;
    }


    public String getColumnFamily(){
            return name;
    }

}
