package com.example.pridwin.domain.usecase


import com.example.pridwin.domain.model.ContextState
import com.example.pridwin.domain.model.Recommendation
import com.example.pridwin.domain.model.WeatherInfo
import com.example.pridwin.domain.rules.ContextRulesEngine


class InferContextUseCase(
    private val rulesEngine: ContextRulesEngine = ContextRulesEngine()
) {
    operator fun invoke(weather: WeatherInfo): Pair<ContextState, List<Recommendation>> {
        return rulesEngine.evaluate(weather)
    }
}