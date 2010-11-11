package uk.co.bytemark.vm.enigma.inquisition.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParsingProblemRecorder {

    private final List<ProblemRecord> problemRecords = new ArrayList<ProblemRecord>();
    private final Stack<String>       contextStack   = new Stack<String>();

    public void pushContext(String context) {
        contextStack.push(context);
    }

    public void popContext(String context) {
        contextStack.pop();
    }

    public void recordWarning(String message) {
        recordProblem(message, Severity.WARNING);
    }

    public void recordError(String message) {
        recordProblem(message, Severity.ERROR);
    }
    
    public void recordFatal(String message) {
        recordProblem(message, Severity.FATAL);
    }

    public void recordProblem(String message, Severity severity) {
        ProblemRecord problemRecord = new ProblemRecord(severity, message, contextStack);
        problemRecords.add(problemRecord);
    }

    public boolean hasProblems() {
        return !problemRecords.isEmpty();
    }

    public static class ProblemRecord {
        private final Severity     severity;
        private final String       message;
        private final List<String> contextPath;

        public ProblemRecord(Severity severity, String message, List<String> contextPath) {
            this.severity = severity;
            this.message = message;
            this.contextPath = new ArrayList<String>(contextPath);
        }

        public Severity getSeverity() {
            return severity;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getContextPath() {
            return contextPath;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((contextPath == null) ? 0 : contextPath.hashCode());
            result = prime * result + ((message == null) ? 0 : message.hashCode());
            result = prime * result + ((severity == null) ? 0 : severity.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final ProblemRecord other = (ProblemRecord) obj;
            if (contextPath == null) {
                if (other.contextPath != null)
                    return false;
            } else if (!contextPath.equals(other.contextPath))
                return false;
            if (message == null) {
                if (other.message != null)
                    return false;
            } else if (!message.equals(other.message))
                return false;
            if (severity == null) {
                if (other.severity != null)
                    return false;
            } else if (!severity.equals(other.severity))
                return false;
            return true;
        }

    }

    private static enum Severity {
        WARNING, ERROR, FATAL;
    }

}
