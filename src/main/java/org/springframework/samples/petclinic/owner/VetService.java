package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Service;

/**
 * Holds the workload rules for {@link Vet}s that don't belong on the {@link Vet} entity
 * itself.
 *
 * @author Anatoliy Milovsky
 */
@Service
public class VetService {

	private static final int OVERLOADED_VISIT_THRESHOLD = 5;

	private static final int OVERLOADED_WINDOW_DAYS = 7;

	private final VisitRepository repository;

	public VetService(VisitRepository repository) {
		this.repository = repository;
	}

	/**
	 * A vet is overloaded if they have handled more than
	 * {@value #OVERLOADED_VISIT_THRESHOLD} visits in the last
	 * {@value #OVERLOADED_WINDOW_DAYS} days.
	 * @param vet the vet to check
	 * @return {@code true} if the vet's recent visit count exceeds the threshold
	 */
	public boolean isOverloaded(Vet vet) {
		return countRecentVisits(vet) >= OVERLOADED_VISIT_THRESHOLD;
	}

	/**
	 * Evaluates the workload rules for the given vet.
	 * @param vet the vet to evaluate
	 * @return a {@link Workload} describing the vet's recent visit count and whether they
	 * are overloaded
	 */
	public Workload getWorkload(Vet vet) {
		long recentVisits = countRecentVisits(vet);

		Workload workload = new Workload();
		workload.setVisitCount(recentVisits);
		workload.setOverloaded(isOverloaded(vet));
		return workload;
	}

	private long countRecentVisits(Vet vet) {
		LocalDate windowStart = LocalDate.now().minusDays(OVERLOADED_WINDOW_DAYS);
		return repository.countByVetIdAndDateGreaterThanEqual(vet.getId(), windowStart);
	}

}
