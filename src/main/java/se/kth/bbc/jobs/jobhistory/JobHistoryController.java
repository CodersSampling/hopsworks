package se.kth.bbc.jobs.jobhistory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import se.kth.bbc.fileoperations.FileOperations;
import se.kth.bbc.lims.MessagesController;
import se.kth.bbc.lims.Utils;
import se.kth.bbc.study.StudyMB;

/**
 *
 * @author stig
 */
@ManagedBean(name = "jobHistoryController")
@RequestScoped
public class JobHistoryController implements Serializable {

  private static final Logger logger = Logger.getLogger(
          JobHistoryController.class.getName());

  @EJB
  private JobHistoryFacade history;

  @ManagedProperty(value = "#{studyManagedBean}")
  private StudyMB studies;

  @EJB
  private FileOperations fops;

  public void setStudies(StudyMB studies) {
    this.studies = studies;
  }

  public List<JobHistory> getHistoryForType(String type) {
    return history.findForStudyByType(studies.getStudyName(), type);
  }
  
  public StreamedContent downloadFile(String path) {
    String filename = Utils.getFileName(path);
    try {
      InputStream is = fops.getInputStream(path);
      StreamedContent sc = new DefaultStreamedContent(is, Utils.getMimeType(filename),
              filename);
      logger.log(Level.INFO, "File was downloaded from HDFS path: {0}",
              path);
      return sc;
    } catch (IOException ex) {
      logger.log(Level.SEVERE, "Failed to download file at path: " + path, ex);
      MessagesController.addErrorMessage("Download failed.");
    }
    return null;
  }
}
