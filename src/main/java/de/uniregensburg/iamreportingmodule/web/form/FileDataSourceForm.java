package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.data.entity.FileDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.FileType;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Form for editing file datasource
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class FileDataSourceForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FileDataSource dataSource;
    private final Binder<FileDataSource> binder = new BeanValidationBinder<>(FileDataSource.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button download = new Button("Download");
    private Anchor downloadAnchor;

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");
    private final TextField fileName = new TextField("File name");
    private final TextField fileType = new TextField("File type");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);

    /**
     *
     */
    public FileDataSourceForm() {
        init();
    }

    /**
     * Initializes form
     */
    public void init() {
        addClassName("datasource-form");

        // configure for mbinder
        binder.bind(name, "name");
        binder.bind(description, "description");
        binder.bind(fileName, "fileName");
        binder.forField(fileType)
                .withConverter(FileType::valueOf,String::valueOf)
                .bind("fileType");

        // configure form components
        configureUpload();
        configureDownload();

        // add upload field and hint to div
        Div uploadDiv = new Div();
        Paragraph hint = new Paragraph("Maximum file size: 100 MB");
        uploadDiv.add(hint, upload);

        // read only fields
        fileName.setEnabled(false);
        fileType.setEnabled(false);

        // add components to layout
        add(name, description, fileName, fileType, uploadDiv);
    }

    /**
     * Configures download button
     * Source: https://stackoverflow.com/a/60822730
     */
    private void configureDownload() {
        downloadAnchor = new Anchor(getStreamResource(), "");
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.add(download);

        download.setEnabled(false);
        downloadAnchor.setEnabled(false);

        binder.addValueChangeListener(event -> {
            if (event.getHasValue().equals(fileName)) {
                boolean fileAvailable = !fileName.getValue().isBlank();
                download.setEnabled(fileAvailable);
                downloadAnchor.setEnabled(fileAvailable);
            }
        });
    }

    /**
     * Returns file as stream resource
     * Source: https://stackoverflow.com/a/74046358
     *
     * @return
     */
    private StreamResource getStreamResource() {
        //
        return new StreamResource("", FileDataSourceForm.this::makeInputStreamOfContent) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>(super.getHeaders());
                String name1 = fileName.getValue();
                headers.put("Content-Disposition", "attachment; filename=\""+ name1 +"\"");
                return headers;
            }};
    }

    /**
     * Returns input stream of file
     *
     * @return
     */
    private InputStream makeInputStreamOfContent() {
        return new ByteArrayInputStream(dataSource.getFile());
    }

    /**
     * Configures upload field
     */
    private void configureUpload() {
        upload.setMaxFiles(1); // limit to one file
        upload.setMaxFileSize(100 * 1024 * 1024); // limit file size to 100 MB
        upload.setAcceptedFileTypes("text/csv"); // limit mime types

        upload.addSucceededListener(event -> {
            logger.info("File uploaded sucessfully");
            InputStream fileData = buffer.getInputStream();
            try {
                logger.info("Adding file content to bean");
                if (event.getMIMEType().equals("text/csv")) {
                    fileType.setValue(FileType.CSV.toString());
                } else {
                    logger.info("MIME type unknown: " + event.getMIMEType());
                    new ErrorNotification("File upload failed", "Unknown MIME type " + event.getMIMEType()).open();
                    return;
                }
                fileName.setValue(event.getFileName());
                dataSource.setFile(fileData.readAllBytes());
                new SuccessNotification("Success", "File uploaded successfully").open();
                logger.info("Added file content to bean");
            } catch (IOException e) {
                logger.info("Error while writing file content to bean: " + e.getMessage());
                new ErrorNotification("File upload failed", "Cannot save file content").open();
            }

        });

        upload.addFileRejectedListener(event -> {
            logger.info("File upload failed: " + event.getErrorMessage());
            new ErrorNotification("File upload failed", event.getErrorMessage()).open();
        });

        upload.addFailedListener(event -> {
            logger.info("File upload failed: " + event.getReason().getMessage());
            new ErrorNotification("File upload failed", event.getReason().getMessage()).open();
        });
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(delete, cancel, downloadAnchor, save);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, dataSource)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    }

    /**
     * Shows or hides delete button
     *
     * @param show
     */
    public void showDelete(boolean show) {
        delete.setVisible(show);
    }

    /**
     * Validates and saves file datasource
     */
    private void validateAndSave() {
        if (!uploadSuccessful()) {
            new ErrorNotification("No uploaded file found").open();
        } else if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else {
            try {
                binder.writeBean(dataSource);
                fireEvent(new SaveEvent(this, dataSource));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Returns if file was uploaded successfully
     *
     * @return
     */
    private boolean uploadSuccessful() {
        if (dataSource.getFile() == null) {
            logger.info("No file provided");
            return false;
        }
        if (dataSource.getFile().length == 0) {
            logger.info("No file data set");
            return false;
        }
        return true;
    }

    /**
     * Fills form based on datasource
     *
     * @param dataSource
     */
    public void setDataSource(FileDataSource dataSource) {
        this.dataSource = dataSource;
        binder.readBean(dataSource);
        boolean fileAvailable = !fileName.getValue().isBlank();
        download.setEnabled(fileAvailable);
        downloadAnchor.setEnabled(fileAvailable);
    }

    /**
     * Event definition
     */
    public static abstract class FileDataSourceFormEvent extends ComponentEvent<FileDataSourceForm> {
        private final FileDataSource dataSource;

        public FileDataSourceFormEvent(FileDataSourceForm source, FileDataSource dataSource) {
            super(source, false);
            this.dataSource = dataSource;
        }

        public FileDataSource getDataSource() {
            return dataSource;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends FileDataSourceForm.FileDataSourceFormEvent {
        SaveEvent(FileDataSourceForm source, FileDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends FileDataSourceForm.FileDataSourceFormEvent {
        DeleteEvent(FileDataSourceForm source, FileDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends FileDataSourceForm.FileDataSourceFormEvent {
        CloseEvent(FileDataSourceForm source) {
            super(source, null);
        }
    }

    /**
     * Event listener
     *
     * @param eventType
     * @param listener
     * @return
     * @param <T>
     */
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    /**
     * Adds button to navbar on attach event
     *
     * @param attachEvent
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Add menu bar
        MainLayout ml = MainLayout.getInstance();
        if (ml != null) {
            initNavbarButtons(ml.getNavbarButtons());
        }
    }

    /**
     * Removes buttons from navbar on detach event
     *
     * @param detachEvent
     */
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Remove menu bar
        MainLayout ml = MainLayout.getInstance();
        if (ml != null) {
            ml.getNavbarButtons().removeAll();
        }

        super.onDetach(detachEvent);
    }

}
