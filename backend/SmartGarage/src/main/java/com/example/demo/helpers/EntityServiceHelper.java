package com.example.demo.helpers;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.filter.EntitySpecificationProvider;
import com.example.demo.filter.Filter;
import com.example.demo.filter.FilterHelper;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Getter
public final class EntityServiceHelper<T, ID,
        R extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>> {

    private final R repository;
    private final FilterHelper filterHelper;
    private static final Map<Class<?>, String> ENTITY_NAME_CACHE = new ConcurrentHashMap<>();
    // Make this method final so it can't be overridden
    @Getter
    private final String entityName;
    private final EntitySpecificationProvider<T> specificationProvider;

    public EntityServiceHelper(R repository, FilterHelper filterHelper, Class<T> entityClass, EntitySpecificationProvider<T> specificationProvider) {
        this.repository = repository;
        this.filterHelper = filterHelper;
        this.entityName = determineEntityName(entityClass);
        this.specificationProvider = specificationProvider;
    }

    private String determineEntityName(Class<T> entityClass) {
        // Check cache first
        if (ENTITY_NAME_CACHE.containsKey(entityClass)) {
            return ENTITY_NAME_CACHE.get(entityClass);
        }

        String name = formatEntityName(entityClass.getSimpleName());

        // Cache for future use
        ENTITY_NAME_CACHE.put(entityClass, name);

        return name;
    }

    private String formatEntityName(String className) {
        // Remove common suffixes for better readability
        String name = className
                .replaceAll("(?i)(Entity|Model|DTO|VO|BO)$", "")
                .replaceAll("([a-z])([A-Z])", "$1 $2"); // Add space between words

        return capitalizeWords(name);
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    // Optional: Helper method for error messages
    public String getEntityNotFoundMessage(ID id) {
        return String.format("%s with id %s not found", getEntityName(), id);
    }

    // ========== PUBLIC INTERFACE METHODS ==========

    public List<T> getAll(MultiValueMap<String, String> allParams) {
        Specification<T> spec = buildSpecification(allParams);
        return repository.findAll(spec);
    }

    private Specification<T> buildSpecification(MultiValueMap<String, String> allParams) {
        Specification<T> spec = Specification.where(null);
        if (allParams == null) {
            return spec;
        }

        // 1. Filters
        if (allParams.containsKey("filters")) {
            List<Filter> filters = filterHelper.convertToFilters(allParams);
            if (!filters.isEmpty()) {
                spec = spec.and(specificationProvider.createFilterSpecification(filters));
            }
        }

        // 2. Search
        if (allParams.containsKey("search")) {
            String search = allParams.getFirst("search");
            if (search != null && !search.isBlank()) {
                spec = spec.and(specificationProvider.createSearchSpecification(search));
            }
        }
        return spec;
    }

    // ========== PUBLIC UPDATE HELPER ==========

    /**
     * Public helper for field updates using FieldUpdateHelper pattern
     * Reusable by child class update methods
     */
    public T update(
            ID id,
            T existing,
            List<FieldUpdateHelper<T, ?>> duplicateCheckFields,  // Wildcard here
            List<FieldUpdateHelper<T, ?>> nonDuplicateCheckFields,  // Wildcard here
            Function<T, ID> idExtractor) {

        List<T> allEntities = repository.findAll();

        // Process fields with duplicate checking
        for (FieldUpdateHelper<T, ?> fieldHelper : duplicateCheckFields) {
            processWithDuplicateCheck(existing, allEntities, fieldHelper, id, idExtractor);
        }

        // Process fields without duplicate checking
        for (FieldUpdateHelper<T, ?> fieldHelper : nonDuplicateCheckFields) {
            processFieldIfChanged(existing, fieldHelper);
        }

        return repository.save(existing);
    }

    // Helper methods to handle wildcards
    @SuppressWarnings("unchecked")
    private <V> void processWithDuplicateCheck(T existing,
                                               List<T> allEntities,
                                               FieldUpdateHelper<T, ?> fieldHelper,
                                               ID id,
                                               Function<T, ID> idExtractor) {

        FieldUpdateHelper<T, V> typedHelper = (FieldUpdateHelper<T, V>) fieldHelper;

        FieldHelper.updateWithDuplicateCheck(
                existing,
                allEntities,
                typedHelper.getter(),
                typedHelper.setter(),
                typedHelper.newValue(),
                id,
                idExtractor,
                typedHelper.fieldName()
        );
    }

    @SuppressWarnings("unchecked")
    private <V> void processFieldIfChanged(T existing, FieldUpdateHelper<T, ?> fieldHelper) {

        FieldUpdateHelper<T, V> typedHelper = (FieldUpdateHelper<T, V>) fieldHelper;

        FieldHelper.updateFieldIfChanged(
                existing,
                typedHelper.getter(),
                typedHelper.setter(),
                typedHelper.newValue()
        );
    }

    // ========== PUBLIC DELETE METHODS ==========

    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("%s with id %s not found", getEntityName(), id));
        }
        repository.deleteById(id);
    }

    public void deleteAllById(List<ID> ids) {
        repository.deleteAllById(ids);
    }
}