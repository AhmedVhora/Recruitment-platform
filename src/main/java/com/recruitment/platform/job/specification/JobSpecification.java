
package com.recruitment.platform.job.specification;

import com.recruitment.platform.job.dto.JobSearchRequest;
import com.recruitment.platform.job.entity.Job;
import com.recruitment.platform.job.entity.enums.JobStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobSpecification {

    public static Specification<Job> filterJobs(JobSearchRequest request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Only published jobs
            predicates.add(
                    criteriaBuilder.equal(
                            root.get("status"), JobStatus.PUBLISHED
                    )
            );

            // 2. Only jobs with remaining vacancies
            predicates.add(
                    criteriaBuilder.greaterThan(
                            root.get("remainingVacancies"), 0
                    )
            );

            // 3. Application deadline must not have passed
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("applicationDeadline"), LocalDate.now()
                    )
            );

            // 4. Keyword search: title, description, required skills
            if (request.getKeyword() != null
                    && !request.getKeyword().isBlank()) {

                String keyword = "%"
                        + request.getKeyword().trim().toLowerCase(Locale.ROOT)
                        + "%";

                Join<Job, String> skillsJoin =
                        root.join("requiredSkills", JoinType.LEFT);

                Predicate titleMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), keyword
                );

                Predicate descriptionMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), keyword
                );

                Predicate skillsMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(skillsJoin), keyword
                );

                predicates.add(
                        criteriaBuilder.or(
                                titleMatch, descriptionMatch, skillsMatch
                        )
                );

                query.distinct(true);
            }

            // 5. Job category
            if (request.getJobCategory() != null
                    && !request.getJobCategory().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("jobCategory")),
                                "%" + request.getJobCategory().trim()
                                        .toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            // 6. Location
            if (request.getLocation() != null
                    && !request.getLocation().isBlank()) {

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("location")),
                                "%" + request.getLocation().trim()
                                        .toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            // 7. Employment type
            if (request.getEmploymentType() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("employmentType"),
                                request.getEmploymentType()
                        )
                );
            }

            // 8. Work mode
            if (request.getWorkMode() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("workMode"),
                                request.getWorkMode()
                        )
                );
            }

            // 9. Minimum experience requirement
            if (request.getMinExperience() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("minExperience"),
                                request.getMinExperience()
                        )
                );
            }

            // 10. Minimum salary filter
            if (request.getMinSalary() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("maxSalary"),
                                request.getMinSalary()
                        )
                );
            }

            // 11. Maximum salary filter
            if (request.getMaxSalary() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("minSalary"),
                                request.getMaxSalary()
                        )
                );
            }

            // 12. Company filter
            if (request.getCompanyId() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("company").get("id"),
                                request.getCompanyId()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
