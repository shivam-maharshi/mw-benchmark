package org.vt.edu.utils;

/**
 * This class is responsible for holding all the benchmarking resource data.
 * 
 * @author shivam.maharshi
 */
public class Resource {

  private double concurrency;
  private double cpu;
  private double ram;
  private double netread;
  private double netwrite;
  private double diskread;
  private double diskwrite;
  
  public double getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(double concurrency) {
    this.concurrency = concurrency;
  }

  public double getCpu() {
    return cpu;
  }

  public void setCpu(double cpu) {
    this.cpu = cpu;
  }

  public double getRam() {
    return ram;
  }

  public void setRam(double ram) {
    this.ram = ram;
  }

  public double getNetread() {
    return netread;
  }

  public void setNetread(double netread) {
    this.netread = netread;
  }

  public double getNetwrite() {
    return netwrite;
  }

  public void setNetwrite(double netwrite) {
    this.netwrite = netwrite;
  }

  public double getDiskread() {
    return diskread;
  }

  public void setDiskread(double diskread) {
    this.diskread = diskread;
  }

  public double getDiskwrite() {
    return diskwrite;
  }

  public void setDiskwrite(double diskwrite) {
    this.diskwrite = diskwrite;
  }

}
