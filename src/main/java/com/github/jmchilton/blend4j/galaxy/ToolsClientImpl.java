package com.github.jmchilton.blend4j.galaxy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;

import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Tool;
import com.github.jmchilton.blend4j.galaxy.beans.ToolExecution;
import com.github.jmchilton.blend4j.galaxy.beans.ToolInputs;
import com.github.jmchilton.blend4j.galaxy.beans.ToolSection;
import com.sun.jersey.api.client.ClientResponse;

class ToolsClientImpl extends Client implements ToolsClient {
  ToolsClientImpl(GalaxyInstanceImpl galaxyInstance) {
    super(galaxyInstance, "tools");
  }

  public ToolExecution create(History history, ToolInputs inputs) {
    inputs.setHistoryId(history.getId());
    return super.create(inputs).getEntity(ToolExecution.class);
    // XXX Datasets not yet properly returned from Tool creation
    // return new ArrayList();
  }
  
  public ClientResponse fileUploadRequest(final String historyId,
                                          final String fileType,
                                          final String dbKey,
                                          final File file) {
    final FileUploadRequest request = new FileUploadRequest(historyId, file);
    request.setFileType(fileType);
    request.setDbKey(dbKey);
    return uploadRequest(request);
  }

  public ToolExecution upload(final FileUploadRequest request) {
    return uploadRequest(request).getEntity(ToolExecution.class);
  }

  public ClientResponse uploadRequest(final FileUploadRequest request) {
    Map<String, String> inputParameters = prepareInputParameters(request);
    final Map<String, Object> requestParameters = prepareRequestParameters(request, inputParameters);
    return multipartPost(getWebResource(), requestParameters, prepareUploads(request.getFileObjects()));
  }

  @Override
  public ToolExecution upload(FtpUploadRequest ftpRequest) {
    return uploadRequest(ftpRequest).getEntity(ToolExecution.class);
  }

  @Override
  public ClientResponse uploadRequest(FtpUploadRequest request) {
    Map<String, String> inputParameters = prepareInputParameters(request);
    inputParameters.put("files_0|url_paste", request.getPath());
    final Map<String, Object> requestParameters = prepareRequestParameters(request, inputParameters);
    return create(getWebResource(), requestParameters);
  }

  private Map<String, String> prepareInputParameters(UploadRequest request) {
    final Map<String, String> uploadParameters = new HashMap<String,String>();
    uploadParameters.put("files_0|NAME", request.getDatasetName());
    uploadParameters.put("dbkey", request.getDbKey());
    uploadParameters.put("file_type", request.getFileType());
    uploadParameters.putAll(request.getExtraParameters());
    return uploadParameters;
  }

  private Map<String, Object> prepareRequestParameters(UploadRequest request, Map<String, String> uploadParameters) {
    final Map<String, Object> requestParameters = new HashMap<String,Object>();
    requestParameters.put("tool_id", request.getToolId());
    requestParameters.put("history_id", request.getHistoryId());
    requestParameters.put("inputs", uploadParameters);
    requestParameters.put("type", "upload_dataset");
    return requestParameters;
  }

  /**
   * {@inheritDoc}
   */
  public Tool showTool(final String toolId) {
    return super.getWebResource(toolId).get(Tool.class);
  }

  
  public List<ToolSection> getTools() {
	return get(new TypeReference<List<ToolSection>>() {});
  }
}