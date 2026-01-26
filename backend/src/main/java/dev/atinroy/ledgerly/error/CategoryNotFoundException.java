package dev.atinroy.ledgerly.error;

public class CategoryNotFoundException extends LedgerlyException {
    public CategoryNotFoundException(Long categoryId) {
        super("Could not find category with id: " + categoryId);
    }
}
