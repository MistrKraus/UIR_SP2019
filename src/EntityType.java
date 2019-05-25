public enum EntityType {

    PERSONAL_NAME("po"),
    INSTITUTION("i"),
    GEOGRAPHICAL_NAME("g"),
    TIME_EXPRESSIONS("t"),
    ARTIFACT_NAMES("o"),
    AMBIGUIUS("a"),
    ERROR("e"),
    NOT_ENTITY("O");

    private final String shortcut;

    private EntityType(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }
}
