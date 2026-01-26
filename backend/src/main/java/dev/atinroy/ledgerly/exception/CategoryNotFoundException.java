package dev.atinroy.ledgerly.exception;

public class CategoryNotFoundException extends LedgerlyException {
    public CategoryNotFoundException(Long categoryId) {
        super("Could not find category with id: " + categoryId);
    }
}
