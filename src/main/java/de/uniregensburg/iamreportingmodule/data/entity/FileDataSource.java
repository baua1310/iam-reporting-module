package de.uniregensburg.iamreportingmodule.data.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Entity file data source extends data source
 * Attributes: file (byte[]), fileType (FileType), fileName (String)
 *
 * @author Julian Bauer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FileDataSource extends DataSource {

    /**
     *
     */
    public FileDataSource() {
        setType(DataSourceType.FILE);
    }

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] file;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FileType fileType;

    @NotBlank
    private String fileName;

    /**
     * Returns file
     *
     * @return
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * Sets file
     *
     * @param file
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * Returns file type
     *
     * @return
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Sets file type
     *
     * @param fileType
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Returns file name
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
