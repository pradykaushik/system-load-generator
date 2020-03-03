package loadGenerator.driver;

public final class UsageException extends RuntimeException {
    private String message;

    public UsageException(final String message) {
        this.message = message;
    }

    public UsageException() {
        this.message = "Usage Error!" + "\n" + CLIBuilder.getOptions();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
