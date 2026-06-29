package dev.atinroy.ledgerly.domain.category.exception;

import dev.atinroy.ledgerly.shared.error.LedgerlyException;
public class CategoryNotFoundException extends LedgerlyException {
    public CategoryNotFoundException(Long categoryId) {
        super("Could not find category with id: " + categoryId);
    }
}
