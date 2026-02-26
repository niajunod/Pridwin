//Nia Junod & Alina Tarasevich
package pridwin.domain.usecase


import pridwin.domain.model.ContextState
import pridwin.domain.model.Recommendation
import pridwin.domain.model.WeatherInfo
import pridwin.domain.rules.ContextRulesEngine


class InferContextUseCase(
    private val rulesEngine: ContextRulesEngine = ContextRulesEngine()
) {
    operator fun invoke(weather: WeatherInfo): Pair<ContextState, List<Recommendation>> {
        return rulesEngine.evaluate(weather)
    }
}