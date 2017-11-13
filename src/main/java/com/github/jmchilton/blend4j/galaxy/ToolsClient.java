package com.github.jmchilton.blend4j.galaxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.github.jmchilton.blend4j.galaxy.beans.ToolInputs;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.sun.jersey.api.client.ClientResponse;

public interface ToolsClient {
  ToolExecution create(History history, ToolInputs inputs);

  /**
   *
   * @deprecated Use {@link #uploadRequest(FileUploadRequest)} now.
   *
   */
  @Deprecated
  ClientResponse fileUploadRequest(String historyId,
                                   String fileType,
                                   String dbKey,
                                   File file);

  ClientResponse uploadRequest(FileUploadRequest request);

  ToolExecution upload(FileUploadRequest request);

  ClientResponse uploadRequest(FtpUploadRequest request);

  ToolExecution upload(FtpUploadRequest ftpRequest);

  /**
   * Show details about the specified tool.
   *
   * @param toolId the tool to look up.
   * @return details about the tool.
   */
  Tool showTool(final String toolId);

  /**
   * Get a list of all tools installed in Galaxy.
   *
   * @return the list of tools installed in Galaxy.
   */
  List<ToolSection> getTools();

  class UploadFile {
    private final File file;
    private final String name;

    public UploadFile(final File file) {
      this(file, file.getName());
    }

    public UploadFile(final File file, final String name) {
      this.file = file;
      this.name =name;
    }

    public File getFile() {
      return file;
    }

    public String getName() {
      return name;
    }

  }

  abstract class UploadRequest {
    private final String historyId;
    private String fileType = "auto";
    private String dbKey = "?";
    private String toolId = "upload1";
    // Specify datasetName instead of file name, useful for multiple file uploads.
    private String datasetName = null;
    private Map<String, String> extraParameters = new HashMap<String, String>();

    protected UploadRequest(String historyId) {
      this.historyId = historyId;
    }

    public Map<String, String> getExtraParameters() {
      return extraParameters;
    }

    public void setExtraParameters(final Map<String, String> extraParameters) {
      this.extraParameters = extraParameters;
    }

    public String getFileType() {
      return fileType;
    }

    public void setFileType(String fileType) {
      this.fileType = fileType;
    }

    public String getDbKey() {
      return dbKey;
    }

    public String getDatasetName() {
      return datasetName;
    }

    public void setDatasetName(String datasetName) {
      this.datasetName = datasetName;
    }

    public void setDbKey(String dbKey) {
      this.dbKey = dbKey;
    }

    public String getToolId() {
      return toolId;
    }

    public String getHistoryId() {
      return historyId;
    }

  }

  class FileUploadRequest extends UploadRequest {

    private final Iterable<UploadFile> files;

    public FileUploadRequest(final String historyId, final File file) {
      this(historyId, convertFiles(Arrays.asList(file)));
    }

    public FileUploadRequest(final String historyId, final UploadFile file) {
      this(historyId, Arrays.asList(file));
    }

    public FileUploadRequest(final String historyId, final Iterable<UploadFile> files) {
      super(historyId);
      this.files = files;
    }

    private static Iterable<UploadFile> convertFiles(final Iterable<File> files) {
      final List<UploadFile> uploadFiles = new ArrayList<UploadFile>();
      for(final File file : files) {
        uploadFiles.add(new UploadFile(file));
      }
      return uploadFiles;
    }

    public Iterable<UploadFile> getFiles() {
      return files;
    }

    public Iterable<File> getFileObjects() {
      final List<File> files = new ArrayList<File>();
      for(final UploadFile uploadFile : getFiles()) {
        files.add(uploadFile.getFile());
      }
      return files;
    }

    public String getDatasetName() {
      if(super.getDatasetName() != null || !files.iterator().hasNext()) {
        return super.getDatasetName();
      } else {
        return files.iterator().next().getName();
      }
    }
  }

  class FtpUploadRequest extends UploadRequest {
    private final String path;

    public FtpUploadRequest(final String historyId, String path) {
      super(historyId);
      this.path = path;
    }

    public String getPath() {
      return path;
    }

    public String getDatasetName() {
      if(super.getDatasetName() != null) {
        return super.getDatasetName();
      } else {
        return path.substring(path.lastIndexOf('/')+1, path.length());
      }
    }
  }

}