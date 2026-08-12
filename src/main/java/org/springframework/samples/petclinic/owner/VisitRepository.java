/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for {@link Visit} domain objects.
 *
 * @author Anatoliy Milovsky
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	/**
	 * Count the number of {@link Visit}s handled by the
	 * {@link org.springframework.samples.petclinic.vet.Vet} with the given id, without
	 * loading the visits into memory.
	 * @param vetId the id of the vet
	 * @return the number of visits performed by the vet
	 */
	long countVisitsByVetId(Integer vetId);

	/**
	 * Count the number of {@link Visit}s handled by the
	 * {@link org.springframework.samples.petclinic.vet.Vet} with the given id on or after
	 * the given date, without loading the visits into memory.
	 * @param vetId the id of the vet
	 * @param date the earliest visit date (inclusive) to count from
	 * @return the number of visits performed by the vet since the given date
	 */
	long countByVetIdAndDateGreaterThanEqual(Integer vetId, LocalDate date);

}
