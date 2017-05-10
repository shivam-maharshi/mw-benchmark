package org.vt.edu.utils;

/**
 * This class is responsible for holding all the benchmarking result data.
 * 
 * @author shivam.maharshi
 */
public class Result {

  private double concurrency;
  private double runtime;
  private double throughput;
  private double avgLatency;
  private double minLatency;
  private double maxLatency;
  private double reads;
  private double readErrors;
  private double writes;
  private double writeErrors;
  private double deletes;
  private double deleteErrors;
  private double updates;
  private double updateErrors;
  private double totalOp;
  private double totalErrors;

  public double getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(double concurrency) {
    this.concurrency = concurrency;
  }

  public double getRuntime() {
    return runtime;
  }

  public void setRuntime(double runtime) {
    this.runtime = runtime;
  }

  public double getThroughput() {
    return throughput;
  }

  public void setThroughput(double throughput) {
    this.throughput = throughput;
  }

  public double getAvgLatency() {
    return avgLatency;
  }

  public void setAvgLatency(double avgLatency) {
    this.avgLatency = avgLatency;
  }

  public double getMinLatency() {
    return minLatency;
  }

  public void setMinLatency(double minLatency) {
    this.minLatency = minLatency;
  }

  public double getMaxLatency() {
    return maxLatency;
  }

  public void setMaxLatency(double maxLatency) {
    this.maxLatency = maxLatency;
  }

  public double getReads() {
    return reads;
  }

  public void setReads(double reads) {
    this.reads = reads;
  }

  public double getReadErrors() {
    return readErrors;
  }

  public void setReadErrors(double readErrors) {
    this.readErrors = readErrors;
  }

  public double getWrites() {
    return writes;
  }

  public void setWrites(double writes) {
    this.writes = writes;
  }

  public double getWriteErrors() {
    return writeErrors;
  }

  public void setWriteErrors(double writeErrors) {
    this.writeErrors = writeErrors;
  }

  public double getDeletes() {
    return deletes;
  }

  public void setDeletes(double deletes) {
    this.deletes = deletes;
  }

  public double getDeleteErrors() {
    return deleteErrors;
  }

  public void setDeleteErrors(double deleteErrors) {
    this.deleteErrors = deleteErrors;
  }

  public double getUpdates() {
    return updates;
  }

  public void setUpdates(double updates) {
    this.updates = updates;
  }

  public double getUpdateErrors() {
    return updateErrors;
  }

  public void setUpdateErrors(double updateErrors) {
    this.updateErrors = updateErrors;
  }

  public double getTotalOp() {
    return totalOp;
  }

  public void setTotalOp(double totalOp) {
    this.totalOp = totalOp;
  }

  public double getTotalErrors() {
    return totalErrors;
  }

  public void setTotalErrors(double totalErrors) {
    this.totalErrors = totalErrors;
  }

}
