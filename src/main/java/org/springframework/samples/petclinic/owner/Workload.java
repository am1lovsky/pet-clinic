package org.springframework.samples.petclinic.owner;

public class Workload {

	private boolean overloaded;

	private long visitCount;

	public boolean isOverloaded() {
		return overloaded;
	}

	public void setOverloaded(boolean overloaded) {
		this.overloaded = overloaded;
	}

	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

}
