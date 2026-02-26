//Nia Junod & Alina Tarasevich
package pridwin.domain.model

data class UserProfile(
    val name: String = "",
    val role: Role,
    val commuteMode: CommuteMode = CommuteMode.WALK_FROM_PARKING,
    val notificationsEnabled: Boolean = true
)