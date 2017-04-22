package org.vt.edu.utils;

/**
 * This class is responsible for holding all the benchmarking result data.
 * 
 * @author shivam.maharshi
 */
public class Result {

  private long concurrency;
  private long runtime;
  private double throughput;
  private double avgLatency;
  private double minLatency;
  private double maxLatency;
  private long reads;
  private long readErrors;
  private long writes;
  private long writeErrors;
  private long deletes;
  private long deleteErrors;
  private long updates;
  private long updateErrors;
  private long totalOp;
  private long totalErrors;

  public long getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(long concurrency) {
    this.concurrency = concurrency;
  }

  public long getRuntime() {
    return runtime;
  }

  public void setRuntime(long runtime) {
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

  public long getReads() {
    return reads;
  }

  public void setReads(long reads) {
    this.reads = reads;
  }

  public long getReadErrors() {
    return readErrors;
  }

  public void setReadErrors(long readErrors) {
    this.readErrors = readErrors;
  }

  public long getWrites() {
    return writes;
  }

  public void setWrites(long writes) {
    this.writes = writes;
  }

  public long getWriteErrors() {
    return writeErrors;
  }

  public void setWriteErrors(long writeErrors) {
    this.writeErrors = writeErrors;
  }

  public long getDeletes() {
    return deletes;
  }

  public void setDeletes(long deletes) {
    this.deletes = deletes;
  }

  public long getDeleteErrors() {
    return deleteErrors;
  }

  public void setDeleteErrors(long deleteErrors) {
    this.deleteErrors = deleteErrors;
  }

  public long getUpdates() {
    return updates;
  }

  public void setUpdates(long updates) {
    this.updates = updates;
  }

  public long getUpdateErrors() {
    return updateErrors;
  }

  public void setUpdateErrors(long updateErrors) {
    this.updateErrors = updateErrors;
  }

  public long getTotalOp() {
    return totalOp;
  }

  public void setTotalOp(long totalOp) {
    this.totalOp = totalOp;
  }

  public long getTotalErrors() {
    return totalErrors;
  }

  public void setTotalErrors(long totalErrors) {
    this.totalErrors = totalErrors;
  }

}
