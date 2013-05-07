/**
 * 
 */
package org.emonocot.harvest.common;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * @author jk00kg
 * 
 * Utility class to inspect the execution for a provided parameter 
 */
public class ParameterPresenceDecider implements JobExecutionDecider {
    
    private Logger logger = LoggerFactory.getLogger(ParameterPresenceDecider.class);

    /**
     * 
     */
    private Object parameter;
    
    private Pattern pattern;

    /**
     * @return the parameter
     */
    public Object getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    /**
     * @param regex the pattern to set
     */
    public void setPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /* (non-Javadoc)
     * @see org.springframework.batch.core.job.flow.JobExecutionDecider#decide(org.springframework.batch.core.JobExecution, org.springframework.batch.core.StepExecution)
     */
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution,
            StepExecution stepExecution) {
        //TODO Possible extension point to delegate to a callback/add option to run additional checks here
        //Look for the parameter in the ExecutionContext
        FlowExecutionStatus returnStatus = null;
        Set<Object> toCheck = new HashSet<Object>(stepExecution.getJobParameters().getParameters().keySet());
        toCheck.addAll(stepExecution.getJobParameters().getParameters().values());
        for(Entry<String, Object> entry : stepExecution.getExecutionContext().entrySet()) {
            toCheck.add(entry.getKey());
            toCheck.add(entry.getValue());
        }
        if(parameter != null) {
            logger.debug("Looking for parameter " + parameter);
            searchForParameter(toCheck);
        }
        if(pattern != null && !"parameter".equals(returnStatus.toString())) {
            logger.debug("Trying to match against pattern " + pattern.pattern());
            for(Object value : toCheck) {
                if(pattern.matcher(value.toString()).matches()){
                    returnStatus = new FlowExecutionStatus("pattern");
                    break;
                }
            }
        }
        
        //Assume false
        return returnStatus;
    }
    protected FlowExecutionStatus searchForParameter(Set<Object> values) {
        if(values.contains(parameter)) {
            return new FlowExecutionStatus("parameter");
        }
        return new FlowExecutionStatus("not found");
    }

}
