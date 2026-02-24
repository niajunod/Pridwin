package com.example.pridwin.domain.usecase


import pridwin.domain.model.ContextState
import pridwin.domain.model.Recommendation
import pridwin.domain.model.WeatherInfo
import com.example.pridwin.domain.rules.ContextRulesEngine


class InferContextUseCase(
    private val rulesEngine: ContextRulesEngine = ContextRulesEngine()
) {
    operator fun invoke(weather: WeatherInfo): Pair<ContextState, List<Recommendation>> {
        return rulesEngine.evaluate(weather)
    }
}