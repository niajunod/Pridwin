package pridwin.domain.model

enum class Role(
    val displayName: String,
    val isOutdoor: Boolean
) {

    POOL_SERVER(
        displayName = "Pool Server",
        isOutdoor = true
    ),

    BEACH_SERVER(
        displayName = "Beach Server",
        isOutdoor = true
    ),

    POOL_BARTENDER(
        displayName = "Pool Bartender",
        isOutdoor = true
    ),

    POOL_KITCHEN(
        displayName = "Pool Kitchen",
        isOutdoor = true
    ),

    MAIN_SERVER(
        displayName = "Main Server",
        isOutdoor = false
    ),

    MAIN_BARTENDER(
        displayName = "Main Bartender",
        isOutdoor = false
    ),

    MAIN_KITCHEN(
        displayName = "Main Kitchen",
        isOutdoor = false
    )
}