package pridwin.domain.model

data class ShiftBrief(
    val phase: ShiftPhase,
    val title: String,
    val summary: String,
    val bulletPoints: List<String>,
    val riskTags: List<RiskTag>
)