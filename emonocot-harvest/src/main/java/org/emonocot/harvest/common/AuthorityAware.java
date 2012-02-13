package org.emonocot.harvest.common;

import org.emonocot.api.SourceService;
import org.emonocot.model.common.Annotation;
import org.emonocot.model.common.AnnotationCode;
import org.emonocot.model.common.AnnotationType;
import org.emonocot.model.common.Base;
import org.emonocot.model.common.RecordType;
import org.emonocot.model.source.Source;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ben
 *
 */
public class AuthorityAware implements StepExecutionListener {

    /**
     *
     */
    private StepExecution stepExecution;
    /**
        *
        */
    private SourceService sourceService;
    /**
       *
       */
    private Source source;
    /**
       *
       */
    private String sourceName;

    /**
       *
       * @param newSourceName Set the name of the Source
       */
    public final void setSourceName(final String newSourceName) {
         this.sourceName = newSourceName;
       }

    /**
        *
        * @return the Source
        */
    public final Source getSource() {
           if (source == null) {
               source = sourceService.load(sourceName);
           }
           return source;
       }

    /**
       *
       * @param newSourceService Set the source service
       */
    @Autowired
    public final void setSourceService(final SourceService newSourceService) {
          this.sourceService = newSourceService;
      }

    /**
     * @param newStepExecution Set the step exectuion
     */
    public void beforeStep(final StepExecution newStepExecution) {
        this.stepExecution = newStepExecution;
    }

    /**
     * @param newStepExecution Set the step execution
     * @return the exit status
     */
    public final ExitStatus afterStep(final StepExecution newStepExecution) {
        return null;
    }

    /**
     * @return the step execution
     */
    public final StepExecution getStepExecution() {
        return stepExecution;
    }

    /**
    *
    * @param object The type of object
    * @param recordType The record type
    * @param code the code of the annotation
    * @param annotationType the type of annotation
    * @return an annotation
    */
    protected final Annotation createAnnotation(final Base object,
            final RecordType recordType, final AnnotationCode code,
            final AnnotationType annotationType) {
       Annotation annotation = new Annotation();
       annotation.setAnnotatedObj(object);
       annotation.setType(annotationType);
       annotation.setJobId(getStepExecution().getJobExecutionId());
       annotation.setCode(code);
       annotation.setRecordType(recordType);
       annotation.setSource(getSource());
       return annotation;
   }
}