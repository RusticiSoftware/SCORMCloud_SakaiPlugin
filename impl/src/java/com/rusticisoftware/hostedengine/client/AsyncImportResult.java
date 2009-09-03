package com.rusticisoftware.hostedengine.client;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AsyncImportResult
{
    public enum ImportStatus { CREATED, RUNNING, FINISHED, ERROR };
    
    private ImportStatus status = ImportStatus.CREATED;
    private List<ImportResult> importResults;
    private String errorMessage;
    
    public ImportStatus getImportStatus()
    {
        return status;
    }
    public List<ImportResult> getImportResults()
    {
        return importResults;
    }
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    public AsyncImportResult(Document asyncImportResultXml)
    {
        String statusText = ((Element)(asyncImportResultXml
                                .getElementsByTagName("status")).item(0))
                                .getTextContent();
        
        if (statusText.equals("created")){
            this.status = ImportStatus.CREATED;
        }  else if (statusText.equals("running")) {
            this.status = ImportStatus.RUNNING;
        } else if (statusText.equals("finished")) {
            this.status = ImportStatus.FINISHED;
        } else if (statusText.equals("error")) {
            this.status = ImportStatus.ERROR;
        }

        if (this.status == ImportStatus.FINISHED) {
            this.importResults = ImportResult.ConvertToImportResults(asyncImportResultXml);
        }

        if (this.status == ImportStatus.ERROR) {
            this.errorMessage = ((Element)(asyncImportResultXml
                                    .getElementsByTagName("error")).item(0))
                                    .getTextContent();
        }
    }

    public Boolean IsComplete()
    {
        return ((this.status == ImportStatus.FINISHED) || (this.status == ImportStatus.ERROR));
    }

    public Boolean HasError()
    {
        return (this.status == ImportStatus.ERROR);
    }
}
